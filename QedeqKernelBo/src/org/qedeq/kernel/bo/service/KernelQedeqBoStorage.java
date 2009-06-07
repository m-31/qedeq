/* $Id: KernelQedeqBoStorage.java,v 1.1 2008/07/26 07:58:28 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.ModuleAddress;

/**
 * Holds all known QEDEQ modules.
 */
class KernelQedeqBoStorage {

    /** This class. */
    private static final Class CLASS = KernelQedeqBoStorage.class;

    /** QEDEQ Modules; key: ModuleAddress, value: KernelQedeqBo. */
    private final Map bos = new HashMap();


    /**
     * Get {@link QedeqBo} for an module address. If it is unknown it will be created.
     *
     * @param   services    Internal kernel services.
     * @param   address     Module address.
     * @return  QedeqBo for module.
     */
    synchronized DefaultKernelQedeqBo getKernelQedeqBo(final InternalKernelServices services,
            final ModuleAddress address) {
        if (bos.containsKey(address)) {
            return (DefaultKernelQedeqBo) bos.get(address);
        } else {
            final DefaultKernelQedeqBo prop = new DefaultKernelQedeqBo(services, address);
            bos.put(address, prop);
            return prop;
        }
    }

    /**
     * Remove all modules from memory.
     */
    synchronized void removeAllModules() {
        final String method = "removeAllModules()";
        Trace.begin(CLASS, this, method);
        try {
            for (final Iterator iterator
                    = bos.entrySet().iterator();
                    iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iterator.next();
                final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) entry.getValue();
                prop.delete();
            }
            bos.clear();
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Validate module dependencies and throw Error if they are not correct.
     */
    synchronized void validateDependencies() {
        final String method = "validateDependencies";
        boolean error = false;
        Trace.begin(CLASS, this, method);
        for (final Iterator iterator = bos.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) entry.getValue();
            Trace.param(CLASS, this, method, "prop", prop);
            if (!prop.hasLoadedRequiredModules()) {
                continue;
            }

            // prop must be in dependent list for all required modules
            final KernelModuleReferenceList refs = prop.getKernelRequiredModules();
            for (int i = 0; i < refs.size(); i++) {
                final KernelQedeqBo ref = refs.getKernelQedeqBo(i);
                final KernelModuleReferenceList dependents = (KernelModuleReferenceList)
                     IoUtility.getFieldContent(ref, "dependent");   // TODO mime 20080325: Q & D
                if (!dependents.contains(prop)) {
                    Trace.fatal(CLASS, this, method, ref.getUrl() + " missing dependent module: "
                        + prop.getUrl(), null);
                    error = true;
                }
            }

            // for all dependent modules, prop must be in required list
            final KernelModuleReferenceList dependents = prop.getDependentModules();
            for (int i = 0; i < dependents.size(); i++) {
                final KernelQedeqBo dependent = dependents.getKernelQedeqBo(i);
                final KernelModuleReferenceList refs2 = (KernelModuleReferenceList)
                    IoUtility.getFieldContent(dependent, "required");
                if (!refs2.contains(prop)) {                        // TODO mime 20080325: Q & D
                    Trace.fatal(CLASS, this, method, dependent.getUrl()
                        + " missing required module: " + prop.getUrl(), null);
                    error = true;
                }
            }
        }
        Trace.end(CLASS, this, method);

        // if the dependencies are not ok we throw an error!
        if (error) {
            Error e = new Error("QEDEQ dependencies and status are flawed! This is a major error!");
            Trace.fatal(CLASS, this, method, "Shutdown because of major validation error", e);
            throw e;
        }
    }

    /**
     * Remove a QEDEQ module from memory.
     *
     * @param   prop    Defines the module.
     */
    synchronized void removeModule(final KernelQedeqBo prop) {
        final String method = "removeModule(KernelQedeqBo)";
        Trace.begin(CLASS, this, method);
        try {
            Trace.trace(CLASS, this, method, "removing " +  prop.getUrl());
            bos.remove(prop.getModuleAddress());
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Get list of all successfully loaded modules.
     *
     * @return  list of all successfully loaded modules.
     */
    synchronized ModuleAddress[] getAllLoadedModules() {
        final String method = "getAllModules()";
        Trace.begin(CLASS, this, method);
        try {
            final List list = new ArrayList();
            for (final Iterator iterator = bos.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iterator.next();
                final QedeqBo prop = (QedeqBo) entry.getValue();
                if (prop.getLoadingState().getCode() >= LoadingState.STATE_LOADED.getCode()) {
                    list.add(prop.getModuleAddress());
                }
            }
            return (ModuleAddress[]) list.toArray(new ModuleAddress[] {});
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Get number of QEDEQ modules in STATE_LOADED.
     *
     * @return  Number of loaded modules.
     */
    synchronized int getNumberOfLoadedModules() {
        return getAllLoadedModules().length;
    }

}
