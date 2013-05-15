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
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.bo.service.common.InternalServiceCall;
import org.qedeq.kernel.bo.service.common.ServiceCallImpl;
import org.qedeq.kernel.bo.service.common.ServiceExecutor;
import org.qedeq.kernel.bo.service.common.ServiceResult;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * Manage all known processes.
 */
public class ServiceProcessManager {

    /** This class. */
    private static final Class CLASS = ServiceProcessManager.class;

    /** Stores all running processes. */
    private final List processes = new ArrayList();

    /** Stores some finished processes. FIXME 20130408 m31: use! */
    private final List finished = new ArrayList();

    /** Stores all calls. */
    private final List calls = new ArrayList();

    /** Manage all known plugins. */
    private final PluginManager pluginManager;

    /** Manage synchronized module access. */
    private final ModuleArbiter arbiter;

    /**
     * Constructor.
     *
     * @param   pluginManager   Collects process information.
     * @param   arbiter         For module access synchronization.
     */
    public ServiceProcessManager(final PluginManager pluginManager, final ModuleArbiter arbiter) {
        this.pluginManager = pluginManager;
        this.arbiter = arbiter;
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
     * @param   process     We run in this process.
     * @param   parent      Parent process that creates a new one.
     * @return  Created process.
     */
    public synchronized ServiceCallImpl createServiceCall(final Service service,
            final KernelQedeqBo qedeq, final Parameters configParameters, final Parameters parameters,
            final InternalServiceProcess process, final InternalServiceCall parent) {
        final ServiceCallImpl call = new ServiceCallImpl(service, qedeq, configParameters, parameters, process, parent);
        calls.add(call);
        return call;
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

    public synchronized InternalServiceProcess createServiceProcess(final String action) {
        final InternalServiceProcess process = new ServiceProcessImpl(arbiter, action);
        synchronized (this) {
            processes.add(process);
        }
        return process;
    }

    ServiceResult executeService(final Service service, final ServiceExecutor executor, final KernelQedeqBo qedeq,
            final InternalServiceProcess process) {
        final String method = "executePlugin(String, KernelQedeqBo, Object)";
        if (process == null) {
            throw new NullPointerException("ServiceProcess must not be null");
        }
        final Parameters configParameters = KernelContext.getInstance().getConfig().getServiceEntries(service);
        final ServiceCallImpl call = createServiceCall(service, qedeq, configParameters, Parameters.EMPTY, process,
            (InternalServiceCall) process.getServiceCall());
        if (!process.isRunning()) {
            call.halt("Service process is not running any more.");
            return call.getServiceResult();
        }
        process.setServiceCall(call);
        process.setBlocked(true);
        call.pause();
        boolean newBlockedModule = false;
        try {
            newBlockedModule = arbiter.lockRequiredModule(process, qedeq);
        } catch (InterruptException e) {
            final String msg = service.getServiceAction() + " was interrupted.";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
            call.interrupt();
            process.setFailureState();
            return call.getServiceResult();
        }
        process.setBlocked(false);
        call.resume();
        try {
            executor.executeService(call);
        } catch (final RuntimeException e) {
            final String msg = service.getServiceAction() + " failed with a runtime exception.";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
            call.finish(msg + " " + e.getMessage());
            process.setFailureState();
            return call.getServiceResult();
        } finally {
            if (newBlockedModule) {
                arbiter.unlockRequiredModule(process, qedeq);
            }
        }
        return call.getServiceResult();
    }

    /**
     * Execute a plugin on an QEDEQ module.
     *
     * @param   id          Plugin to use.
     * @param   qedeq       QEDEQ module to work on.
     * @param   data        Process parameters.
     * @param   proc        Process. Might be <code>null</code>.
     * @return  Plugin specific result object. Might be <code>null</code>.
     * @throws  RuntimeException    Plugin unknown.
     */
    Object executePlugin(final String id, final KernelQedeqBo qedeq, final Object data,
            final InternalServiceProcess proc) {
        final String method = "executePlugin(String, KernelQedeqBo, Object)";
        final PluginBo plugin = pluginManager.getPlugin(id);
        if (plugin == null) {
            final String message = "Kernel does not know about plugin: ";
            final RuntimeException e = new RuntimeException(message + id);
            Trace.fatal(CLASS, this, method, message + id,
                e);
            throw e;
        }
        final Parameters configParameters = KernelContext.getInstance().getConfig().getServiceEntries(plugin);
        InternalServiceProcess process = null;
        if (proc != null) {
            if (!proc.isRunning()) {
                return null;
            }
            process = proc;
        } else {
            process = new ServiceProcessImpl(arbiter, plugin.getServiceAction());
            synchronized (this) {
                processes.add(process);
            }
        }
        final ServiceCallImpl call = createServiceCall(plugin, qedeq, configParameters, Parameters.EMPTY,
            process, (InternalServiceCall) process.getServiceCall());
        process.setServiceCall(call);
        process.setBlocked(true);
        boolean newBlockedModule = false;
        try {
            newBlockedModule = arbiter.lockRequiredModule(process, qedeq);
        } catch (InterruptException e) {
            final String msg = plugin.getServiceAction() + " was interrupted.";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
            call.interrupt();
            process.setFailureState();
            return null;
        }
        process.setBlocked(false);
        try {
            final PluginExecutor exe = plugin.createExecutor(qedeq, configParameters);
            final Object result = exe.executePlugin(process, data);
            if (exe.getInterrupted()) {
                call.interrupt();
                process.setFailureState();
            } else {
                call.finish();
            }
            return result;
        } catch (final RuntimeException e) {
            final String msg = plugin.getServiceAction() + " failed with a runtime exception.";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
            call.finish(msg + ": " + e.getMessage());
            process.setFailureState();
            return null;
        } finally {
            if (newBlockedModule) {
                arbiter.unlockRequiredModule(process, qedeq);
            }
            // if we created the process we also close it
            if (proc == null) {
                if (process.isRunning()) {
                    process.setSuccessState();
                }
            }
        }
    }


}
