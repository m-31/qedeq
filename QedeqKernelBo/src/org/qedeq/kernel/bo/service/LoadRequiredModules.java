/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;


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
     * @throws  SourceFileExceptionList Failure(s).
     * @throws  IllegalArgumentException    BO is not loaded
     */
    public static void loadRequired(final Plugin plugin, final DefaultKernelQedeqBo prop)
            throws SourceFileExceptionList {
        // did we check this already?
        if (prop.getDependencyState().areAllRequiredLoaded()) {
            return; // everything is OK
        }
        (new LoadRequiredModules()).loadAllRequired(plugin, prop);
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @param   plugin      We work for this plugin.
     * @param   prop        QEDEQ module BO. This module must be loaded.
     * @throws  SourceFileExceptionList Failure(s).
     * @throws  IllegalArgumentException    BO is not loaded
     */
    private void loadAllRequired(final Plugin plugin, final DefaultKernelQedeqBo prop)
            throws SourceFileExceptionList {
        final String method = "loadRequired(DefaultQedeqBo)";
        Trace.param(CLASS, this, method, "prop.getModuleAddress", prop.getModuleAddress());
        synchronized (prop) {
            if (prop.getDependencyState().areAllRequiredLoaded()) {
                return; // everything is OK
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
        } catch (DefaultSourceFileExceptionList e) {
            sfl = e;
        }
        if (sfl == null) {
            for (int i = 0; i < required.size(); i++) {
                Trace.trace(CLASS, this, method, "loading required modules of " + prop.getUrl());
                DefaultKernelQedeqBo current = null;
                current = (DefaultKernelQedeqBo) required.getKernelQedeqBo(i);
                if (loadingRequiredInProgress.containsKey(current)) {
                    ModuleDataException me = new LoadRequiredModuleException(12,
                        "recursive import of modules is forbidden, label \""
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
                try {
                    loadAllRequired(plugin, current);
                } catch (SourceFileExceptionList e) {
                    ModuleDataException me = new LoadRequiredModuleException(13,
                        "import of module \"" + required.getLabel(i) + "\" failed: "
                        + e.get(0).getMessage(),
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
                return; // everything is OK, someone elses thread might have corrected errors!
            }
            if (sfl == null) {
                prop.setLoadedRequiredModules(required);
                prop.getElement2Latex().setModuleReferences(required);
            } else {
                prop.setDependencyFailureState(
                    DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
                throw sfl;
            }
        }
    }

}
