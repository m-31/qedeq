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
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
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

    public Object executePlugin(final ServiceProcess process, final Object data) {
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
        Boolean all = (Boolean) getServices().executePlugin(LoadAllRequiredModulesPlugin.class.getName(),
               getQedeqBo().getModuleAddress(), null, process);
        if (!all.booleanValue()) {
            final String msg = "Loading required modules failed";
            QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(),
                "Module could not even be loaded.");
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
            getQedeqBo().getKernelServices().executePlugin(LoadRequiredModulesPlugin.class.getName(),
                current.getModuleAddress(), loadingRequiredInProgress, process);
            if (!current.hasLoadedRequiredModules()) {
                // LATER 20110119 m31: we take only the first error, is that ok?
                ModuleDataException me = new LoadRequiredModuleException(
                    DependencyErrors.IMPORT_OF_MODULE_FAILED_CODE,
                    DependencyErrors.IMPORT_OF_MODULE_FAILED_TEXT + "\"" + required.getLabel(i)
                        + "\", " + current.getErrors().get(0).getMessage(),
                        required.getModuleContext(i));
                sfl.add(createError(me));
                continue;
            }
        }

        loadingRequiredInProgress.remove(getQedeqBo());

        if (getQedeqBo().getDependencyState().areAllRequiredLoaded()) {
            return Boolean.TRUE; // everything is OK, someone elses thread might have corrected errors!
        }
        getQedeqBo().getLabels().setModuleReferences(required); // FIXME why here?
        if (!getQedeqBo().hasBasicFailures()) {
            if (sfl.size() == 0) {
                getQedeqBo().setLoadedRequiredModules();
                QedeqLog.getInstance().logSuccessfulReply(
                    "Loading required modules successful", getQedeqBo().getUrl());
                return Boolean.TRUE;
            }
            getQedeqBo().setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
        }
        final String msg = "Loading required modules failed";
        QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(),
             StringUtility.replace(getQedeqBo().getErrors().getMessage(), "\n", "\n\t"));
        return  Boolean.FALSE;
    }

    // FIXME
    public double getExecutionPercentage() {
        return 1;
    }

    public boolean getInterrupted() {
        return false;
    }

    public String getLocationDescription() {
        return "running";
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
