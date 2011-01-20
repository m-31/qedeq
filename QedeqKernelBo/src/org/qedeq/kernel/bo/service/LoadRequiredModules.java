/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.se.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.se.common.DependencyState;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;


/**
 * Load all required QEDEQ modules.
 *
 * @author  Michael Meyling
 */
public final class LoadRequiredModules {

    /** This class. */
    private static final Class CLASS = LoadRequiredModules.class;

    /** All QedeqBos currently in state "loading required modules". */
    private final Map loadingRequiredInProgress = new HashMap();

    /**
     * Don't use this constructor.
     */
    private LoadRequiredModules() {
        // nothing to do
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @param   plugin      We work for this plugin.
     * @param   prop        QEDEQ module BO. This module must be loaded.
     * @return  Loading successful.
     * @throws  IllegalArgumentException    BO is not loaded
     */
    public static boolean loadRequired(final Plugin plugin, final DefaultKernelQedeqBo prop) {
        // did we check this already?
        if (prop.getDependencyState().areAllRequiredLoaded()) {
            return true; // everything is OK
        }
        return (new LoadRequiredModules()).loadAllRequired(plugin, prop);
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @param   plugin      We work for this plugin.
     * @param   prop        QEDEQ module BO. This module must be loaded.
     * @return  Loading successful.
     * @throws  IllegalArgumentException    BO is not loaded
     */
    private boolean loadAllRequired(final Plugin plugin, final DefaultKernelQedeqBo prop) {
        final String method = "loadRequired(DefaultQedeqBo)";
        Trace.param(CLASS, this, method, "prop.getModuleAddress", prop.getModuleAddress());
        synchronized (prop) {
            if (prop.getDependencyState().areAllRequiredLoaded()) {
                return true; // everything is OK
            }
            if (!prop.isLoaded()) {
                throw new IllegalArgumentException("Programming error BO must be loaded!");
            }
            if (loadingRequiredInProgress.containsKey(prop)) { // already checked?
                throw new IllegalStateException("Programming error: must not be marked!");
            }
            prop.setDependencyProgressState(DependencyState.STATE_LOADING_REQUIRED_MODULES);
            loadingRequiredInProgress.put(prop, prop);

        }
        SourceFileExceptionList sfl = null;
        final LoadDirectlyRequiredModules loader = new LoadDirectlyRequiredModules(plugin, prop);
        KernelModuleReferenceList required = null;
        try {
            required = loader.load();
            sfl = loader.getErrorList();
        } catch (SourceFileExceptionList e) {
            sfl = e;
        }
        if (sfl == null || sfl.size() == 0) {
            for (int i = 0; i < required.size(); i++) {
                Trace.trace(CLASS, this, method, "loading required modules of " + prop.getUrl());
                DefaultKernelQedeqBo current = null;
                current = (DefaultKernelQedeqBo) required.getKernelQedeqBo(i);
                if (loadingRequiredInProgress.containsKey(current)) {
                    ModuleDataException me = new LoadRequiredModuleException(
                        ServiceErrors.RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_CODE,
                        ServiceErrors.RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_TEXT + "\""
                        + required.getLabel(i) + "\"",
                        required.getModuleContext(i));
                    final SourceFileException sf = prop.createSourceFileException(plugin, me);
                    if (sfl == null) {
                        sfl = new DefaultSourceFileExceptionList(sf);
                    } else {
                        sfl.add(sf);
                    }
                    continue;
                }
                if (!loadAllRequired(plugin, current)) {
                    // LATER 20110119 m31: we take only the first error, is that ok?
                    ModuleDataException me = new LoadRequiredModuleException(
                        ServiceErrors.IMPORT_OF_MODULE_FAILED_CODE,
                        ServiceErrors.IMPORT_OF_MODULE_FAILED_TEXT + "\"" + required.getLabel(i)
                            + "\", " + current.getErrors().get(0).getMessage(),
                    required.getModuleContext(i));
                    final SourceFileException sf = prop.createSourceFileException(plugin, me);
                    if (sfl == null) {
                        sfl = new DefaultSourceFileExceptionList(sf);
                    } else {
                        sfl.add(sf);
                    }
                    continue;
                }
            }
        }

        synchronized (prop) {
            loadingRequiredInProgress.remove(prop);
            if (prop.getDependencyState().areAllRequiredLoaded()) {
                return true; // everything is OK, someone elses thread might have corrected errors!
            }
            if (sfl == null || sfl.size() == 0) {
                prop.setLoadedRequiredModules(required);
                prop.getElement2Latex().setModuleReferences(required);
                return true;
            } else {
                prop.setDependencyFailureState(
                    DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
                return false;
            }
        }
    }

}
