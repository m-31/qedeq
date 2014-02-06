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

import java.util.Stack;

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

        if (!loadAllRequiredModules(call, getKernelQedeqBo(), true)) {
            final String msg = "Loading required modules failed";
            QedeqLog.getInstance().logFailureReply(msg, getKernelQedeqBo().getUrl(),
                "Not all required modules could not even be loaded.");
            return Boolean.FALSE;
        }
        percentage = 100;
        Trace.trace(CLASS, this, method, "loading required modules of " + getKernelQedeqBo().getUrl());
        getKernelQedeqBo().setDependencyProgressState(DependencyState.STATE_LOADING_REQUIRED_MODULES);


        final SourceFileExceptionList sfl = new SourceFileExceptionList();
        if (circlesInRequiredModules(call, getKernelQedeqBo(), sfl)) {
            final String msg = "Loading required modules failed";
            QedeqLog.getInstance().logFailureReply(msg, getKernelQedeqBo().getUrl(),
                "There were circular dependencies.");
            return Boolean.FALSE;
        }

        if (getKernelQedeqBo().getDependencyState().areAllRequiredLoaded()) {
            return Boolean.TRUE; // everything is OK, someone else's thread might have corrected errors!
        }

        getKernelQedeqBo().getLabels().setModuleReferences(getKernelQedeqBo().getRequiredModules()); // FIXME why here?
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

    private boolean circlesInRequiredModules(final InternalModuleServiceCall call, final KernelQedeqBo bo,
            final SourceFileExceptionList sfl) {
        if (bo.hasLoadedRequiredModules()) {
            return false;
        }
        Stack loadingRequiredInProgress = new Stack();
        Stack labels = new Stack();
//        System.out.println("->checking " + bo.getName());
        loadingRequiredInProgress.push(bo);
        final KernelModuleReferenceList required = bo.getKernelRequiredModules();
        final StringBuffer error = new StringBuffer();
        for (int i = 0; i < required.size(); i++) {
            final KernelQedeqBo current = required.getKernelQedeqBo(i);
//            System.out.println("-->testing " + current.getName());
            labels.push(required.getLabel(i));
            if (loadingRequiredInProgress.contains(current)) {
//                for (int j = 0; j < loadingRequiredInProgress.size(); j++) {
//                    System.out.print("-> " + labels.get(j).toString());
//                }
                ModuleDataException me = new LoadRequiredModuleException(
                    DependencyErrors.RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_CODE,
                    DependencyErrors.RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_TEXT + "\""
                    + required.getLabel(i) + "\"",
                    required.getModuleContext(i));
                sfl.add(createError(me));
//                me.printStackTrace(System.out);
                labels.pop();
                continue;
            }

//            System.out.println("->removing " + bo.getName());
//            loadingRequiredInProgress.remove(bo);
            error.setLength(0);
            if (!noCirclesInRequiredModules(call, required.getKernelQedeqBo(i), loadingRequiredInProgress, labels,
                    error)) {
                // LATER 20110119 m31: we take only the first error, is that ok?
                String text = DependencyErrors.RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_TEXT + error.toString();
                ModuleDataException me = new LoadRequiredModuleException(
                    DependencyErrors.RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_CODE,
                    text, required.getModuleContext(i));
                sfl.add(createError(me));
            }
            labels.pop();
        }
//        System.out.println("->removing " + bo.getName());
        loadingRequiredInProgress.pop();
        if (sfl.size() > 0) {
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
            return true;
        }
        return false;
    }

    private boolean noCirclesInRequiredModules(final InternalModuleServiceCall call, final KernelQedeqBo bo,
            final Stack loadingRequiredInProgress, final Stack labels, final StringBuffer error) {
        if (!bo.hasLoadedImports()) {
            return false;
        }
//        System.out.println("->checking " + bo.getName());
        loadingRequiredInProgress.push(bo);
        final KernelModuleReferenceList required = bo.getKernelRequiredModules();
        boolean result = true;
        for (int i = 0; i < required.size(); i++) {
            final KernelQedeqBo current = required.getKernelQedeqBo(i);
//            System.out.println("-->testing " + current.getName() + " (" + required.getLabel(i) + ")");
            labels.push(required.getLabel(i));
            if (loadingRequiredInProgress.contains(current)) {
                for (int j = 0; j < loadingRequiredInProgress.size(); j++) {
                    if (j > 0) {
                        error.append(" -> ");
                    }
                    error.append("\"" + labels.get(j).toString() + "\"");
                }
                result = false;
//                System.out.println("## " + error);
                labels.pop();
                break;
            }

            if (!noCirclesInRequiredModules(call, required.getKernelQedeqBo(i), loadingRequiredInProgress, labels,
                    error)) {
                result = false;
//                System.out.println("## " + error);
                labels.pop();
                break;
            }
            labels.pop();
        }
//        System.out.println("->removing " + bo.getName());
        loadingRequiredInProgress.pop();
        return result;
    }

    private boolean loadAllRequiredModules(final InternalModuleServiceCall call, final KernelQedeqBo bo,
            final boolean first) throws InterruptException {
        if (bo.hasLoadedImports()) {
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
                if (!loadAllRequiredModules(call, (KernelQedeqBo) imports.getQedeqBo(i), false)) {
                    result = false;
                    if (first) {
                        // LATER 20110119 m31: we take only the first error, is that ok?
                        String text = DependencyErrors.IMPORT_OF_MODULE_FAILED_TEXT + "\""
                            + imports.getLabel(i) + "\"";
                        if (imports.getQedeqBo(i).getErrors().size() > 0) {
                            text += ", " + imports.getQedeqBo(i).getErrors().get(0).getMessage();
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
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
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
