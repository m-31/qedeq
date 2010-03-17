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

import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * Holds all known QEDEQ modules.
 */
public class PluginManager {

    /** This class. */
    private static final Class CLASS = PluginManager.class;

    /** Stores all plugins. */
    private final Map plugins = new HashMap();


    /**
     * Get all registered plugins.
     *
     * @return  Registered plugins.
     */
    synchronized PluginBo[] getPlugins() {
        return (PluginBo[]) plugins.keySet().toArray(new PluginBo[] {});
    }

    /**
     * Add a plugin.
     *
     * @param   Plugin to add. A plugin with same name can not be added twice.
     */
    synchronized void addPlugin(final PluginBo plugin) {
        if (plugins.get(plugin.getPluginName()) != null) {
            final PluginBo oldPlugin = (PluginBo) plugins.get(plugin.getPluginName());
            throw new IllegalArgumentException("plugin with that name already added: "
                + oldPlugin.getPluginName() + ": " + plugin.getPluginDescription());
        }
        plugins.put(plugin.getPluginName(), plugin);
    }

    /**
     * Execute a plugin on an QEDEQ module.
     *
     * @param   name    Plugin to use.
     * @param   qedeq   QEDEQ module to work on.
     * @throws  SourceFileExceptionList     Problems during execution.
     */
    synchronized void executePlugin(final String name, final KernelQedeqBo qedeq) throws SourceFileExceptionList {
        final PluginBo plugin = (PluginBo) plugins.get(name);
        plugin.executePlugin(qedeq);
    }

}
