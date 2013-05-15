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

package org.qedeq.kernel.bo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.InternalPluginBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;

/**
 * Manage all known plugins.
 */
public class PluginManager {

    /** This class. */
    private static final Class CLASS = PluginManager.class;

    /** Maps plugin ids to plugins. */
    private final Map id2plugin = new HashMap();

    /** Stores all plugins. */
    private final List plugins = new ArrayList();

    /** Basic kernel properties.  */
    private final InternalKernelServices kernel;

    /**
     * Constructor.
     *
     * @param   kernel          Kernel access.
     */
    public PluginManager(final InternalKernelServices kernel) {
        this.kernel = kernel;
    }

    /**
     * Get plugin with given id.
     *
     * @param   id  Plugin id.
     * @return  Registered plugin. Might be <code>null</code>..
     */
    public synchronized PluginBo getPlugin(final String id) {
        return (PluginBo) id2plugin.get(id);
    }

    /**
     * Get all registered (non internal) plugins.
     *
     * @return  Registered plugins. Internal plugins are not included.
     */
    synchronized PluginBo[] getNonInternalPlugins() {
        final List result = new ArrayList(plugins.size());
        for (int i = 0; i < plugins.size(); i++) {
            if (!(plugins.get(i) instanceof InternalPluginBo)) {
                result.add(plugins.get(i));
            }
        }
        return (PluginBo[]) result.toArray(new PluginBo[] {});
    }

    /**
     * Add a plugin..
     *
     * @param   pluginClass Class that extends {@link PluginBo} to add.
     *                      A plugin with same name can not be added twice.
     *                      Must not be <code>null</code>.
     * @throws  RuntimeException    Plugin addition failed.
     */
    synchronized void addPlugin(final String pluginClass) {
        final String method = "addPlugin";
        PluginBo plugin = null;
        try {
            Class cl = Class.forName(pluginClass);
            plugin = (PluginBo) cl.newInstance();
        } catch (ClassNotFoundException e) {
            Trace.fatal(CLASS, this, method, "Plugin class not in class path: " + pluginClass, e);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            Trace.fatal(CLASS, this, method, "Plugin class could not be istanciated: "
                + pluginClass, e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            Trace.fatal(CLASS, this, method,
                "Programming error, access for instantiation failed for plugin: " + pluginClass,
                e);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, method,
                "Programming error, instantiation failed for plugin: " + pluginClass, e);
            throw new RuntimeException(e);
        }
        addPlugin(plugin);
        // set default plugin values for not yet set parameters
        final Parameters parameters = kernel.getConfig().getServiceEntries(plugin);
        plugin.setDefaultValuesForEmptyPluginParameters(parameters);
        kernel.getConfig().setServiceKeyValues(plugin, parameters);
    }

    /**
     * Add a plugin.
     *
     * @param   plugin  Plugin to add. A plugin with same name can not be added twice.
     *                  Must not be <code>null</code>.
     * @throws  RuntimeException    Plugin addition failed.
     */
    synchronized void addPlugin(final PluginBo plugin) {
        if (id2plugin.get(plugin.getServiceId()) != null) {
            final PluginBo oldPlugin = (PluginBo) id2plugin.get(plugin.getServiceId());
            final RuntimeException e = new IllegalArgumentException("plugin with that name already added: "
                    + oldPlugin.getServiceId() + ": " + plugin.getServiceDescription());
            Trace.fatal(CLASS, this, "addPlugin", "Programing error", e);
            throw e;
        }
        id2plugin.put(plugin.getServiceId(), plugin);
        plugins.add(plugin);
    }

    /**
     * Clear all plugin errors and warnings on an QEDEQ module.
     *
     * @param   qedeq   Clear reults for this module.
     */
    public void clearAllPluginResults(final KernelQedeqBo qedeq) {
        qedeq.clearAllPluginErrorsAndWarnings();
    }

}
