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
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.KernelServices;
import org.qedeq.kernel.bo.common.PluginBo;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalPluginBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;

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

    /** Collects process infos. */
    private final ServiceProcessManager processManager;

    /** Basic kernel properties.  */
    private final KernelServices kernel;

    /**
     * Constructor.
     *
     * @param   kernel          Kernel access.
     * @param   processManager  Collects process information.
     */
    public PluginManager(final KernelServices kernel, final ServiceProcessManager processManager) {
        this.kernel = kernel;
        this.processManager = processManager;
    }

    /**
     * Get all registered (non internal) plugins.
     *
     * @return  Registered plugins. Internal plugins are not included.
     */
    synchronized PluginBo[] getPlugins() {
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
        final Parameters parameters = kernel.getConfig().getPluginEntries(plugin);
        plugin.setDefaultValuesForEmptyPluginParameters(parameters);
        kernel.getConfig().setPluginKeyValues(plugin, parameters);
    }

    /**
     * Add a plugin.
     *
     * @param   plugin  Plugin to add. A plugin with same name can not be added twice.
     *                  Must not be <code>null</code>.
     * @throws  RuntimeException    Plugin addition failed.
     */
    synchronized void addPlugin(final PluginBo plugin) {
        if (id2plugin.get(plugin.getPluginId()) != null) {
            final PluginBo oldPlugin = (PluginBo) id2plugin.get(plugin.getPluginId());
            final RuntimeException e = new IllegalArgumentException("plugin with that name already added: "
                    + oldPlugin.getPluginId() + ": " + plugin.getPluginDescription());
            Trace.fatal(CLASS, this, "addPlugin", "Programing error", e);
            throw e;
        }
        id2plugin.put(plugin.getPluginId(), plugin);
        plugins.add(plugin);
    }

    /**
     * Execute a plugin on an QEDEQ module.
     *
     * @param   id          Plugin to use.
     * @param   qedeq       QEDEQ module to work on.
     * @return  Plugin specific resulting object. Might be <code>null</code>.
     * @throws  RuntimeException    Plugin unknown.
     */
    Object executePlugin(final String id, final KernelQedeqBo qedeq) {
        final String method = "executePlugin(String, KernelQedeqBo)";
        final PluginBo plugin = (PluginBo) id2plugin.get(id);
        if (plugin == null) {
            final String message = "Kernel does not know about plugin: ";
            final RuntimeException e = new RuntimeException(message + id);
            Trace.fatal(CLASS, this, method, message + id,
                e);
            throw e;
        }
        final Parameters parameters = KernelContext.getInstance().getConfig().getPluginEntries(plugin);
        final ServiceProcess process = processManager.createProcess(plugin,
            qedeq, parameters);
        process.setBlocked(true);
        synchronized (qedeq) {
            process.setBlocked(false);
            try {
                final PluginExecutor exe = plugin.createExecutor(qedeq, parameters);
                process.setExecutor(exe);
                final Object result = exe.executePlugin();
                process.setSuccessState();
                return result;
            } catch (final RuntimeException e) {
                final String msg = "Execution of plugin failed with a runtime exception.";
                Trace.fatal(CLASS, this, method, msg, e);
                QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
                return null;
            } finally {
                if (process.isRunning()) {
                    process.setFailureState();
                }
                // remove old executor
                process.setExecutor(null);
            }
        }
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
