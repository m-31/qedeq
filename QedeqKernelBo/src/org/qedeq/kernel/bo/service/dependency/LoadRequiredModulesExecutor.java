/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.state.DependencyState;


/**
 * Load all required QEDEQ modules.
 *
 * @author  Michael Meyling
 */
public final class LoadRequiredModulesExecutor extends ControlVisitor implements PluginExecutor {

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
    LoadRequiredModulesExecutor(final Plugin plugin, final KernelQedeqBo prop,
            final Parameters parameters) {
        super(plugin, prop);
    }

    public Object executePlugin(final InternalServiceProcess process, final Object data) {
        percentage = 0;
        final String method = "executePlugin";
        if (getQedeqBo().hasLoadedRequiredModules()) {
            return Boolean.TRUE; // everything is OK
        }
        QedeqLog.getInstance().logRequest(
            "Loading required modules", getQedeqBo().getUrl());
        // all QedeqBos currently in state "loading required modules"
        Map loadingRequiredInProgress = (Map) data;
        if (loadingRequiredInProgress == null) {
            loadingRequiredInProgress = new HashMap();
        }
        Boolean all = (Boolean) getServices().executePlugin(process,
               LoadAllRequiredModulesPlugin.class.getName(), getQedeqBo(), null);
        if (!all.booleanValue()) {
            final String msg = "Loading required modules failed";
            QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(),
                "Not all required modules could not even be loaded.");
            getQedeqBo().setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, getQedeqBo().getErrors());
            return Boolean.FALSE;
        }
        if (loadingRequiredInProgress.containsKey(getQedeqBo())) { // already checked?
            throw new IllegalStateException("Programming error: must not be marked!");
        }
        getQedeqBo().setDependencyProgressState(getPlugin(), DependencyState.STATE_LOADING_REQUIRED_MODULES);

        loadingRequiredInProgress.put(getQedeqBo(), getQedeqBo());

        final KernelModuleReferenceList required = (KernelModuleReferenceList) getQedeqBo().getRequiredModules();

        final SourceFileExceptionList sfl = new SourceFileExceptionList();
        Trace.trace(CLASS, this, method, "loading required modules of " + getQedeqBo().getUrl());
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
            getQedeqBo().getKernelServices().executePlugin(process,
                LoadRequiredModulesPlugin.class.getName(), current, loadingRequiredInProgress);
            if (!current.hasLoadedRequiredModules()) {
                // LATER 20110119 m31: we take only the first error, is that ok?
                String text = DependencyErrors.IMPORT_OF_MODULE_FAILED_TEXT + "\""
                    + required.getLabel(i) + "\"";
                if (current.getErrors().size() > 0) {
                    // FIXME 20130324 m31: what if this changed directly after .size() call?
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

        loadingRequiredInProgress.remove(getQedeqBo());

        if (getQedeqBo().getDependencyState().areAllRequiredLoaded()) {
            return Boolean.TRUE; // everything is OK, someone elses thread might have corrected errors!
        }
        getQedeqBo().getLabels().setModuleReferences(required); // FIXME why here?
        if (!getQedeqBo().hasBasicFailures() && sfl.size() == 0) {
            getQedeqBo().setLoadedRequiredModules();
            QedeqLog.getInstance().logSuccessfulReply(
                "Loading required modules successful", getQedeqBo().getUrl());
            return Boolean.TRUE;
        }
        if (sfl.size() != 0) {
            getQedeqBo().setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
        } else {
            getQedeqBo().setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, getQedeqBo().getErrors());
        }
        final String msg = "Loading required modules failed";
        QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(),
             StringUtility.replace(getQedeqBo().getErrors().getMessage(), "\n", "\n\t"));
        return  Boolean.FALSE;
    }

    public double getExecutionPercentage() {
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
        return getQedeqBo().createSourceFileException(getPlugin(), me);
    }

}
