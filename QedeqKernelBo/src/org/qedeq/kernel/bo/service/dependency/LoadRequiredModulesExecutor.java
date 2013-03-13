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
public final class LoadRequiredModulesExecutor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = LoadRequiredModulesExecutor.class;

    private final Plugin plugin;

    private final KernelQedeqBo prop;

    /**
     * Constructor.
     *
     * @param   plugin      Plugin we work for.
     * @param   prop        Internal QedeqBo.
     * @param   parameters  Currently ignored.
     */
    LoadRequiredModulesExecutor(final Plugin plugin, final KernelQedeqBo prop,
            final Parameters parameters) {
        this.plugin = plugin;
        this.prop = prop;
    }

    public Object executePlugin(final ServiceProcess process, final Object data) {
        final String method = "executePlugin";
        if (prop.getDependencyState().areAllRequiredLoaded()) {
            return Boolean.TRUE; // everything is OK
        }
        // all QedeqBos currently in state "loading required modules"
        Map loadingRequiredInProgress = (Map) data;
        if (loadingRequiredInProgress == null) {
            loadingRequiredInProgress = new HashMap();
        }
        QedeqLog.getInstance().logRequest(
            "Loading required modules", prop.getUrl());
        prop.getKernelServices().loadModule(prop.getModuleAddress());
        if (!prop.isLoaded()) {
            final String msg = "Loading required modules failed";
            QedeqLog.getInstance().logFailureReply(msg, prop.getUrl(),
                "Module could not even be loaded.");
            return Boolean.FALSE;
        }
        if (loadingRequiredInProgress.containsKey(prop)) { // already checked?
            throw new IllegalStateException("Programming error: must not be marked!");
        }
        prop.setDependencyProgressState(plugin, DependencyState.STATE_LOADING_REQUIRED_MODULES);

        loadingRequiredInProgress.put(prop, prop);

        final KernelModuleReferenceList required = (KernelModuleReferenceList) prop
            .getKernelServices().executePlugin(LoadDirectlyRequiredModulesPlugin.class.getName(),
            prop.getModuleAddress(), null, process);

        final SourceFileExceptionList sfl = new SourceFileExceptionList();
        if (!prop.hasBasicFailures()) {
            for (int i = 0; i < required.size(); i++) {
                Trace.trace(CLASS, this, method, "loading required modules of " + prop.getUrl());
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
                prop.getKernelServices().executePlugin(LoadRequiredModulesPlugin.class.getName(),
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
        }

        loadingRequiredInProgress.remove(prop);

        if (prop.getDependencyState().areAllRequiredLoaded()) {
            return Boolean.TRUE; // everything is OK, someone elses thread might have corrected errors!
        }
        prop.getLabels().setModuleReferences(required);
        if (!prop.hasBasicFailures()) {
            if (sfl.size() == 0) {
                prop.setLoadedRequiredModules(required);
                QedeqLog.getInstance().logSuccessfulReply(
                    "Loading required modules successful", prop.getUrl());
                return Boolean.TRUE;
            }
            prop.setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
        }
        final String msg = "Loading required modules failed";
        QedeqLog.getInstance().logFailureReply(msg, prop.getUrl(),
             StringUtility.replace(prop.getErrors().getMessage(), "\n", "\n\t"));
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
        return prop.createSourceFileException(plugin, me);
    }

}
