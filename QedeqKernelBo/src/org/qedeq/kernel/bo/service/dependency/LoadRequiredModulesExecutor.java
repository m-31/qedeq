/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
 *
 * "Hilbert II" is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package org.qedeq.kernel.bo.service.dependency;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.basis.ControlVisitor;
import org.qedeq.kernel.bo.service.basis.ModuleServicePluginExecutor;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.visitor.InterruptException;


/**
 * Load all required QEDEQ modules.
 *
 * @author  Michael Meyling
 */
public final class LoadRequiredModulesExecutor extends ControlVisitor implements ModuleServicePluginExecutor {

    /** This class. */
    private static final Class CLASS = LoadRequiredModulesExecutor.class;

    /** Percentage between 0 and 100. */
    private double percentage;

    /**
     * Constructor.
     *
     * @param   plugin      Plugin we work for.
     * @param   prop        Internal QedeqBo.
     * @param   parameters  Currently ignored.
     */
    LoadRequiredModulesExecutor(final ModuleService plugin, final KernelQedeqBo prop,
            final Parameters parameters) {
        super(plugin, prop);
    }

    public Object executePlugin(final InternalModuleServiceCall call, final Object data) throws InterruptException {
        percentage = 0;
        final String method = "executePlugin";
        if (getKernelQedeqBo().hasLoadedRequiredModules()) {
            percentage = 100;
            return Boolean.TRUE; // everything is OK
        }
        QedeqLog.getInstance().logRequest(
            "Loading required modules", getKernelQedeqBo().getUrl());
        // all QedeqBos currently in state "loading required modules"
        Map loadingRequiredInProgress = (Map) data;
        if (loadingRequiredInProgress == null) {
            loadingRequiredInProgress = new HashMap();
        }
// LATER 20140205 m31: remove if plugin locking is no problem any more (load required modules)
//        // we unlock the currently locked module to get rid of death locks
//        getKernelQedeqBo().getKernelServices().unlockModule(call);
        if (!loadAllRequiredModules(call, getKernelQedeqBo(), true)) {
            final String msg = "Loading required modules failed";
            QedeqLog.getInstance().logFailureReply(msg, getKernelQedeqBo().getUrl(),
                "Not all required modules could not even be loaded.");
            return Boolean.FALSE;
        }
// LATER 20140205 m31: remove if plugin locking is no problem any more (load required modules)
//        try {
//            getKernelQedeqBo().getKernelServices().lockModule(call);
//        } catch (InterruptException e) {    // TODO 20130521 m31: ok?
//            call.interrupt();
//            return Boolean.FALSE;
//        }
        if (loadingRequiredInProgress.containsKey(getKernelQedeqBo())) { // already checked?
            throw new IllegalStateException("Programming error: must not be marked!");
        }
        getKernelQedeqBo().setDependencyProgressState(DependencyState.STATE_LOADING_REQUIRED_MODULES);

        loadingRequiredInProgress.put(getKernelQedeqBo(), getKernelQedeqBo());

        final KernelModuleReferenceList required = (KernelModuleReferenceList) getKernelQedeqBo().getRequiredModules();

// LATER 20140205 m31: remove if plugin locking is no problem any more (load required modules)
//        getKernelQedeqBo().getKernelServices().unlockModule(call);

        final SourceFileExceptionList sfl = new SourceFileExceptionList();
        Trace.trace(CLASS, this, method, "loading required modules of " + getKernelQedeqBo().getUrl());
        for (int i = 0; i < required.size(); i++) {
            percentage = 100 * (double) i / required.size();
            final KernelQedeqBo current = required.getKernelQedeqBo(i);
            if (loadingRequiredInProgress.containsKey(current)) {
                ModuleDataException me = new LoadRequiredModuleException(
                    DependencyErrors.RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_CODE,
                    DependencyErrors.RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_TEXT + "\""
                    + required.getLabel(i) + "\"",
                    required.getModuleContext(i));
                sfl.add(createError(me));
                continue;
            }
            getKernelQedeqBo().getKernelServices().executePlugin(call.getInternalServiceProcess(),
                LoadRequiredModulesPlugin.class.getName(), current, loadingRequiredInProgress);
            if (!current.hasLoadedRequiredModules()) {
                // LATER 20110119 m31: we take only the first error, is that ok?
                String text = DependencyErrors.IMPORT_OF_MODULE_FAILED_TEXT + "\""
                    + required.getLabel(i) + "\"";
                if (current.getErrors().size() > 0) {
                    // TODO 20130324 m31: what if this changed directly after .size() call?
                    //                    check if locking the module is active
                    text += ", " + current.getErrors().get(0).getMessage();
                }
                ModuleDataException me = new LoadRequiredModuleException(
                    DependencyErrors.IMPORT_OF_MODULE_FAILED_CODE,
                    text, required.getModuleContext(i));
                sfl.add(createError(me));
                continue;
            }
        }
        percentage = 100;

        loadingRequiredInProgress.remove(getKernelQedeqBo());

        if (getKernelQedeqBo().getDependencyState().areAllRequiredLoaded()) {
            return Boolean.TRUE; // everything is OK, someone else's thread might have corrected errors!
        }

// LATER 20140205 m31: remove if plugin locking is no problem any more (load required modules)
//        try {
//            getKernelQedeqBo().getKernelServices().lockModule(call);
//        } catch (InterruptException e) {    // TODO 20130521 m31: OK?
//            call.interrupt();
//            return Boolean.FALSE;
//        }

        getKernelQedeqBo().getLabels().setModuleReferences(required); // FIXME why here?
        if (!getKernelQedeqBo().hasBasicFailures() && sfl.size() == 0) {
            getKernelQedeqBo().setLoadedRequiredModules();
            QedeqLog.getInstance().logSuccessfulReply(
                "Loading required modules successful", getKernelQedeqBo().getUrl());
            return Boolean.TRUE;
        }
        if (sfl.size() != 0) {
            getKernelQedeqBo().setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
        } else {
            getKernelQedeqBo().setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, getKernelQedeqBo().getErrors());
        }
        final String msg = "Loading required modules failed";
        QedeqLog.getInstance().logFailureReply(msg, getKernelQedeqBo().getUrl(),
             StringUtility.replace(getKernelQedeqBo().getErrors().getMessage(), "\n", "\n\t"));
        return  Boolean.FALSE;
    }

    private boolean loadAllRequiredModules(final InternalModuleServiceCall call, final KernelQedeqBo bo,
                final boolean first) throws InterruptException {
        if (bo.hasLoadedRequiredModules()) {
            return true;
        }
        getServices().executePlugin(call.getInternalServiceProcess(),
            LoadDirectlyRequiredModulesPlugin.class.getName(), bo, null);
        if (!bo.hasLoadedImports()) {
            return false;
        }
        final ModuleReferenceList imports = bo.getRequiredModules();
        final SourceFileExceptionList sfl = new SourceFileExceptionList();
        boolean result = true;
        for (int i = 0; i < imports.size(); i++) {
            if (!imports.getQedeqBo(i).hasLoadedImports()) {
                if (!loadAllRequiredModules(call, (KernelQedeqBo) imports.getQedeqBo(i), first)) {
                    result = false;
                    if (first) {
                        // LATER 20110119 m31: we take only the first error, is that ok?
                        String text = DependencyErrors.IMPORT_OF_MODULE_FAILED_TEXT + "\""
                            + imports.getLabel(i) + "\"";
                        if (bo.getErrors().size() > 0) {
                            // TODO 20130324 m31: what if this changed directly after .size() call?
                            //                    check if locking the module is active
                            text += ", " + bo.getErrors().get(0).getMessage();
                        }
                        ModuleDataException me = new LoadRequiredModuleException(
                            DependencyErrors.IMPORT_OF_MODULE_FAILED_CODE,
                            text, imports.getModuleContext(i));
                        sfl.add(createError(me));
                    }
                }
            }
        }
        if (sfl.size() > 0) {
// LATER 20140205 m31: remove if plugin locking is no problem any more (load required modules)
//            try {
//                getKernelQedeqBo().getKernelServices().lockModule(call);
//            } catch (InterruptException e) {    // TODO 20130521 m31: ok?
//                call.interrupt();
//                return false;
//            }
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);

// LATER 20140205 m31: remove if plugin locking is no problem any more (load required modules)
//            getKernelQedeqBo().getKernelServices().unlockModule(call);
        }
        return result;
    }

    public double getVisitPercentage() {
        return percentage;
    }

    public boolean getInterrupted() {
        return false;
    }

    public String getLocationDescription() {
        return super.getLocationDescription();
    }


    /**
     * Prepare exception for error collection.
     *
     * @param   me  Basis exception.
     * @return  Error.
     */
    private SourceFileException createError(final ModuleDataException me) {
        return getKernelQedeqBo().createSourceFileException(getService(), me);
    }

}
