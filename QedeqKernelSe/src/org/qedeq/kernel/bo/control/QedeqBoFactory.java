/* $Id: Modules.java,v 1.5 2008/01/26 12:39:08 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleProperties;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.trace.Trace;

/**
 * Encapsulates all modules.
 */
class QedeqBoFactory {

    /** This class. */
    private static final Class CLASS = QedeqBoFactory.class;

    /** properties of modules; key: ModuleAddress, value: DefaultModuleProperties.*/
    private final Map moduleProperties = new HashMap();


    /**
     * Get module properties for an module address. If it is unknown it will be created.
     *
     * @param   address     Module address.
     * @return  Module properties for module.
     */
    final DefaultModuleProperties getModuleProperties(final ModuleAddress address) {
        synchronized (moduleProperties) {
            if (moduleProperties.containsKey(address)) {
                return (DefaultModuleProperties) moduleProperties.get(address);
            } else {
                final DefaultModuleProperties prop = new DefaultModuleProperties(address);
                moduleProperties.put(address, prop);
                return prop;
            }
        }
    }


    /**
     * Remove all modules from memory.
     */
    final void removeAllModules() {
        final String method = "removeAllModules";
        Trace.begin(CLASS, this, method);
        try {
            synchronized (moduleProperties) {
                for (final Iterator iterator
                        = moduleProperties.entrySet().iterator();
                        iterator.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    final ModuleProperties prop = (ModuleProperties) entry.getValue();
                    Trace.trace(CLASS, this, method, "remove " +  prop);
                    ModuleEventLog.getInstance().removeModule(prop);
                }
                moduleProperties.clear();
            }
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }


    /**
     * Remove a QEDEQ module from memory.
     *
     * @param   prop    Defines the module.
     */
    final void removeModule(final ModuleProperties prop) {
        final String method = "removeModule";
        Trace.begin(CLASS, this, method);
        try {
            synchronized (moduleProperties) {
                Trace.trace(CLASS, this, method, "remove module "
                    +  prop.getUrl());
                if (!prop.isLoaded()) {
                    Trace.trace(CLASS, this, method, "removing " +  prop.getUrl());
                    ModuleEventLog.getInstance().removeModule(prop);
                    moduleProperties.remove(prop.getModuleAddress());
                } else {
                    Trace.trace(CLASS, this, method, "module number=" + moduleProperties.size());
                    Trace.trace(CLASS, this, method, "removing module itself: " +  prop.getUrl());
                    ModuleEventLog.getInstance().removeModule(prop);
                    moduleProperties.remove(prop.getModuleAddress());
                    Trace.trace(CLASS, this, method, "module number=" + moduleProperties.size());
                }
            }
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
    final ModuleAddress[] getAllLoadedModules() {
        final String method = "getAllModules";
        Trace.begin(CLASS, this, method);
        try {
            final List list = new ArrayList();
            synchronized (moduleProperties) {
                for (final Iterator iterator
                        = moduleProperties.entrySet().iterator();
                        iterator.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    final ModuleProperties prop = (ModuleProperties) entry.getValue();
                    if (prop.getLoadingState().getCode() >= LoadingState.STATE_LOADED.getCode()) {
                        list.add(prop.getModuleAddress());
                    }
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
    final int getNumberOfLoadedModules() {
        return getAllLoadedModules().length;
    }

}
