/* $Id: LoadRequiredModules.java,v 1.2 2008/01/26 12:39:09 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.control;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;


/**
 * Load all required QEDEQ modules.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public class LoadRequiredModules {

    /** This class. */
    private static final Class CLASS = LoadRequiredModules.class;

    /** All QedeqBos currently in state "loading required modules". */
    private final Map loadingRequiredInProgress = new HashMap();

    /** Kernel services. */
    private final DefaultInternalKernelServices services;

    /**
     * Constructor.
     *
     * @param   services    Kernel services.
     */
    LoadRequiredModules(final DefaultInternalKernelServices services) {
        this.services = services;
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @param   prop        Module properties.
     * @param   services    Kernel services.
     * @throws  SourceFileExceptionList Failure(s).
     */
    public static void loadRequired(final KernelQedeqBo prop,
            final DefaultInternalKernelServices services) throws SourceFileExceptionList {
        // did we check this already?
        if (prop.getDependencyState().areAllRequiredLoaded()) {
            return; // everything is OK
        }
        (new LoadRequiredModules(services)).loadRequired(prop);
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @param   prop        QEDEQ module BO. This module must be loaded.
     * @throws  SourceFileExceptionList Failure(s).
     * @throws  IllegalArgumentException    BO is not loaded
     */
    private void loadRequired(final KernelQedeqBo prop)
            throws SourceFileExceptionList {
        final String method = "loadRequired(DefaultQedeqBo, DefaultKernelServices, Map)";
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
        DefaultSourceFileExceptionList sfl = null;
        final LoadDirectlyRequiredModules loader = new LoadDirectlyRequiredModules(prop, services);
        KernelModuleReferenceList required = null;
        try {
            required = loader.load();
            sfl = loader.getSourceFileExceptionList();
        } catch (DefaultSourceFileExceptionList e) {
            sfl = e;
        }
        loader.getSourceFileExceptionList();
        if (sfl == null) {
            for (int i = 0; i < required.size(); i++) {
                Trace.trace(CLASS, this, method, "loading required modules of " + prop.getUrl());
                KernelQedeqBo current = null;
                current = required.getKernelQedeqBo(i);
                if (loadingRequiredInProgress.containsKey(current)) {
                    ModuleDataException me = new LoadRequiredModuleException(12,
                        "recursive import of modules is forbidden, label \""
                        + required.getLabel(i) + "\"",
                        required.getModuleContext(i));
                    final SourceFileException sf = prop.createSourceFileException(me);
                    if (sfl == null) {
                        sfl = new DefaultSourceFileExceptionList(sf);
                    } else {
                        sfl.add(sf);
                    }
                    continue;
                }
                try {
                    loadRequired(current);
                } catch (SourceFileExceptionList e) {
                    ModuleDataException me = new LoadRequiredModuleException(13,
                        "import of module \"" + required.getLabel(i) + "\" failed: "
                        + e.get(0).getMessage(),
                    required.getModuleContext(i));
                    final SourceFileException sf = prop.createSourceFileException(me);
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
            } else {
                prop.setDependencyFailureState(
                    DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
                throw sfl;
            }
        }
    }

}
