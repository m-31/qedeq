/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.control;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.ModuleServiceResult;
import org.qedeq.kernel.bo.common.ServiceJob;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleServicePlugin;
import org.qedeq.kernel.bo.module.ModuleServicePluginExecutor;
import org.qedeq.kernel.se.common.ModuleService;
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
    public synchronized ServiceJob[] getServiceProcesses() {
        return (ServiceJob[]) processes.toArray(new ServiceJob[] {});
    }

    /**
     * Get all running service processes. But remember a running process might currently
     * be blocked.
     *
     * @return  All service running processes.
     */
    public synchronized ServiceJob[] getRunningServiceProcesses() {
        final ArrayList result = new ArrayList(processes);
        for (int i = 0; i < result.size(); ) {
            if (!((ServiceJob) result.get(i)).isRunning()) {
                result.remove(i);
            } else {
                i++;
            }
        }
        return (ServiceJob[]) result.toArray(new ServiceJob[] {});
    }

    /**
     * Create service process. Might block further execution, because an exclusive access to given module is given.
     *
     * @param   service             The service that runs in current thread.
     * @param   qedeq               QEDEQ module for service.
     * @param   configParameters    Config parameters for the service.
     * @param   parameters          Parameter for this service call.
     * @param   process             We run in this process.
     * @return  Created service call. Never <code>null</code> (if no {@link InterruptException} occurred).
     * @throws  InterruptException  User canceled call.
     */
    public ServiceCallImpl createServiceCall(final Service service,
            final KernelQedeqBo qedeq, final Parameters configParameters, final Parameters parameters,
            final InternalServiceJob process) throws InterruptException {
        if (!process.isRunning()) { // should not occur
            throw new RuntimeException("Service process is not running any more.");
        }
        if (!process.getThread().isAlive()) {
            throw new RuntimeException("thread is already dead");
        }
        final ServiceCallImpl call = new ServiceCallImpl(service, qedeq, configParameters, parameters, process,
            process.getModuleServiceCall());
        synchronized (this) {
            calls.add(call);
        }
        process.setInternalServiceCall(call);
        lockRequiredModule(call, qedeq, service);
        return call;
    }

    /**
     * End service call by unlocking previously locked module.
     *
     * @param   call    End this call, which should be finished, interrupted or halted before.
     */
    public void endServiceCall(final InternalModuleServiceCall call) {
        // TODO 20130521 m31: do it without cast
        if (call != null && ((ServiceCallImpl) call).getNewlyBlockedModule()) {
            unlockRequiredModule(call, call.getKernelQedeq());
        }
    }

    /**
     * Remove all service processes. All processes are also terminated via interruption.
     */
    public synchronized void terminateAndRemoveAllServiceProcesses() {
        terminateAllServiceProcesses();
        processes.clear();
        finished.clear();
        calls.clear();
    }

    /**
     * Terminate all service processes.
     */
    public synchronized void terminateAllServiceProcesses() {
        for (int i = 0; i < processes.size(); i++) {
            final ServiceJob proc = (ServiceJob) processes.get(i);
            proc.interrupt();
        }
    }

    public synchronized InternalServiceJobImpl createServiceProcess(final String action) {
        final InternalServiceJobImpl process = new InternalServiceJobImpl(arbiter, action);
        processes.add(process);
        return process;
    }

    ModuleServiceResult executeService(final ModuleService service, final ModuleServiceExecutor executor,
            final KernelQedeqBo qedeq, final InternalServiceJob process) throws InterruptException {
        final String method = "executePlugin(String, KernelQedeqBo, Object)";
        if (process == null) {
            throw new NullPointerException("ServiceProcess must not be null");
        }
        final Parameters configParameters = KernelContext.getInstance().getConfig().getServiceEntries(service);
        ServiceCallImpl call = null;
        try {
            call = createServiceCall(service, qedeq, configParameters, Parameters.EMPTY, process);
            executor.executeService(call);
            return call.getServiceResult();
        } catch (final RuntimeException e) {
            final String msg = service.getServiceAction() + " failed with a runtime exception.";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
            if (call != null) {
                call.finish(msg + " " + e.getMessage());
            }
            process.setFailureState();
            return call != null ? call.getServiceResult() : null;
        } catch (final InterruptException e) {
            final String msg = service.getServiceAction() + " was canceled by user.";
            QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
            if (call != null) {
                call.interrupt();
            }
            process.setInterruptedState();
            throw e;
        } finally {
            endServiceCall(call);
        }
    }

    /**
     * Execute a plugin on an QEDEQ module.
     *
     * @param   id          Plugin to use.
     * @param   qedeq       QEDEQ module to work on.
     * @param   data        Process parameters.
     * @param   proc        Process. Might be <code>null</code>. Otherwise should be a process that is still running.
     * @return  Plugin specific result object. Might be <code>null</code>.
     * @throws  InterruptException  User interrupt occurred.
     * @throws  RuntimeException    Plugin unknown or process is not running any more.
     */
    Object executePlugin(final String id, final KernelQedeqBo qedeq, final Object data,
            final InternalServiceJob proc) throws InterruptException {
        final String method = "executePlugin(String, KernelQedeqBo, Object)";
        final ModuleServicePlugin plugin = pluginManager.getPlugin(id);
        if (plugin == null) {
            final String message = "Kernel does not know about plugin: ";
            final RuntimeException e = new RuntimeException(message + id);
            Trace.fatal(CLASS, this, method, message + id,
                e);
            throw e;
        }
        final Parameters configParameters = KernelContext.getInstance().getConfig().getServiceEntries(plugin);
        InternalServiceJob process = null;
        if (proc != null) {
            if (!proc.isRunning()) {
                // TODO 20140124 m31: but if it was interrupted we want to throw a InterrruptException
                final String message = "Process " + proc.getId() + " was already finished: "
                    + proc.getExecutionActionDescription();
                final RuntimeException e = new RuntimeException(message + id);
                Trace.fatal(CLASS, this, method, message + id,
                    e);
                throw e;
            }
            process = proc;
        } else {
            process = createServiceProcess(plugin.getServiceAction());
        }
        ServiceCallImpl call = null;
        try {
            call = createServiceCall(plugin, qedeq, configParameters, Parameters.EMPTY,
                process);
            final ModuleServicePluginExecutor exe = plugin.createExecutor(qedeq, configParameters);
            call.setServiceCompleteness(exe);
            final Object result = exe.executePlugin(call, data);
            if (exe.getInterrupted()) {
                call.interrupt();
                throw new InterruptException(qedeq.getModuleAddress().createModuleContext());
            } else {
                call.finish();
                process.setInternalServiceCall((InternalModuleServiceCall) call.getParentServiceCall());
            }
            return result;
        } catch (final RuntimeException e) {
            final String msg = plugin.getServiceAction() + " failed with a runtime exception.";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
            if (call != null) {
                call.finish(msg + ": " + e.getMessage());
            }
            return null;
        } catch (final InterruptException e) {
            final String msg = plugin.getServiceAction() + " was canceled by user.";
            QedeqLog.getInstance().logFailureReply(msg, qedeq.getUrl(), e.getMessage());
            if (call != null) {
                call.interrupt();
            }
            throw e;
        } finally {
            endServiceCall(call);
            // if we created the process we also close it
            if (proc == null) {
                if (process.isRunning()) {
                    process.setSuccessState();
                }
            }
        }
    }

    public boolean lockRequiredModule(final ServiceCallImpl call, final KernelQedeqBo qedeq, final Service service)
            throws InterruptException {
        call.pause();
        call.getInternalServiceProcess().setBlocked(true);
        try {
            final boolean result = arbiter.lockRequiredModule(call.getInternalServiceProcess(), qedeq, service);
            call.setNewlyBlockedModule(result);
            return result;
        } finally {
            call.getInternalServiceProcess().setBlocked(false);
            call.resume();
        }
    }

    public boolean unlockRequiredModule(final InternalModuleServiceCall call, final KernelQedeqBo qedeq) {
        return arbiter.unlockRequiredModule(call.getInternalServiceProcess(), qedeq);
    }

}
