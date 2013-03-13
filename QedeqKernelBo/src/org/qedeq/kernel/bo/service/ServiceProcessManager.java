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
import java.util.List;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.PluginBo;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.common.Plugin;

/**
 * Manage all known processes.
 */
public class ServiceProcessManager {

    /** This class. */
    private static final Class CLASS = ServiceProcessManager.class;

    /** Stores all processes. */
    private final List processes = new ArrayList();

    /** Manage all known plugins. */
    private final PluginManager pluginManager;

    /**
     * Constructor.
     *
     * @param   pluginManager   Collects process information.
     */
    public ServiceProcessManager(final PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }


    /**
     * Get all service processes.
     *
     * @return  All service processes.
     */
    public synchronized ServiceProcess[] getServiceProcesses() {
        return (ServiceProcess[]) processes.toArray(new ServiceProcess[] {});
    }

    /**
     * Get all running service processes. But remember a running process might currently
     * be blocked.
     *
     * @return  All service running processes.
     */
    public synchronized ServiceProcess[] getRunningServiceProcesses() {
        final ArrayList result = new ArrayList(processes);
        for (int i = 0; i < result.size(); ) {
            if (!((ServiceProcess) result.get(i)).isRunning()) {
                result.remove(i);
            } else {
                i++;
            }
        }
        return (ServiceProcess[]) result.toArray(new ServiceProcess[] {});
    }

    /**
     * Create service process.
     *
     * @param   service     The service that runs in current thread.
     * @param   qedeq       QEDEQ module for service.
     * @param   parameters  Parameter for the service.
     * @return  Created process.
     */
    public synchronized ServiceProcessImpl createProcess(final Plugin service,
            final KernelQedeqBo qedeq, final Parameters parameters, final ServiceProcess parent) {
        final ServiceProcessImpl process = new ServiceProcessImpl(service, qedeq, parameters, parent);
        processes.add(process);
        return process;
    }

    /**
     * Remove all service processes. All processes are also terminated via interruption.
     */
    public synchronized void terminateAndRemoveAllServiceProcesses() {
        terminateAllServiceProcesses();
        processes.clear();
    }

    /**
     * Terminate all service processes.
     */
    public synchronized void terminateAllServiceProcesses() {
        for (int i = 0; i < processes.size(); i++) {
            final ServiceProcess proc = (ServiceProcess) processes.get(i);
            proc.interrupt();
        }
    }


    /**
     * Execute a plugin on an QEDEQ module.
     *
     * @param   id          Plugin to use.
     * @param   qedeq       QEDEQ module to work on.
     * @param   data        Process parameters.
     * @param   parent      Parent service process. Might be <code>null</code>.
     * @return  Plugin specific resulting object. Might be <code>null</code>.
     * @throws  RuntimeException    Plugin unknown.
     */
    Object executePlugin(final String id, final KernelQedeqBo qedeq, final Object data,
            final ServiceProcess parent) {
        final String method = "executePlugin(String, KernelQedeqBo, Object, ServiceProcess)";
        final PluginBo plugin = pluginManager.getPlugin(id);
        if (plugin == null) {
            final String message = "Kernel does not know about plugin: ";
            final RuntimeException e = new RuntimeException(message + id);
            Trace.fatal(CLASS, this, method, message + id,
                e);
            throw e;
        }
        final Parameters parameters = KernelContext.getInstance().getConfig().getPluginEntries(plugin);
        final ServiceProcessImpl process = createProcess(plugin,
            qedeq, parameters, parent);
        process.setBlocked(true);
        synchronized (qedeq) {
            process.setBlocked(false);
            try {
                process.addBlockedModule(qedeq);
                final PluginExecutor exe = plugin.createExecutor(qedeq, parameters);
                process.setExecutor(exe);
                qedeq.setCurrentlyRunningPlugin(plugin);
                final Object result = exe.executePlugin(process, data);
                if (exe.getInterrupted()) {
                    process.setFailureState();
                } else {
                    process.setSuccessState();
                }
                return result;
            } catch (final RuntimeException e) {
                final String msg = plugin.getPluginActionName() + " failed with a runtime exception.";
                Trace.fatal(CLASS, this, method, msg, e);
                QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
                return null;
            } finally {
                process.removeBlockedModule(qedeq);
                if (process.isRunning()) {
                    process.setFailureState();
                }
                // remove old executor
                process.setExecutor(null);
                qedeq.setCurrentlyRunningPlugin(null);
            }
        }
    }


}
