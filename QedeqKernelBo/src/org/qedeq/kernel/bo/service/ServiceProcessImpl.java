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

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.QedeqBoSet;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.KernelQedeqBoSet;
import org.qedeq.kernel.se.common.Plugin;

/**
 * Process info for a service.
 *
 * @author  Michael Meyling
 */
public class ServiceProcessImpl implements ServiceProcess {

    /** This class. */
    private static final Class CLASS = ServiceProcessImpl.class;

    /** The service the thread works for. */
    private final Plugin service;

    /** The thread the service is done within. */
    private final Thread thread;

    /** Some important parameters for the service. For example QEDEQ module address. */
    private final Parameters parameters;

    /** Start time for process. */
    private long start;

    /** End time for process. */
    private long stop;

    /** State for process. 0 = running, 1 = success, -1 failure. */
    private int state;

    /** Is this process blocked? */
    private boolean blocked;

    /** QEDEQ module the process is working on. */
    private KernelQedeqBo qedeq;

    /** Percentage of currently running plugin execution. */
    private double executionPercentage = 0;

    /** Percentage of currently running plugin execution. */
    private String executionActionDescription = "not yet started";

    /** Created execution object. Might be <code>null</code>. */
    private PluginExecutor executor;

    /** Parent process. Might be <code>null</code>. */
    private final ServiceProcess parent;

    /** We block these QEDEQ modules for other processes. */
    private final KernelQedeqBoSet blockedModules;

    /**
     * A new service process within the current thread.
     *
     * @param   service     This service is executed.
     * @param   thread      The process the service is executed within.
     * @param   qedeq       Module we work on.
     * @param   parameters  Interesting process parameters (e.g. QEDEQ module).
     * @param   parent      Parent service process.
     */
    public ServiceProcessImpl(final Plugin service, final Thread thread,
            final KernelQedeqBo qedeq, final Parameters parameters, final ServiceProcess parent) {
        this.service = service;
        this.thread = thread;
        this.qedeq = qedeq;
        this.parameters = parameters;
        this.parent = parent;
        if (!thread.isAlive()) {
            throw new RuntimeException("thread is already dead");
        }
        this.blockedModules = (KernelQedeqBoSet) (parent != null ? parent.getBlockedModules()
             : new KernelQedeqBoSet(qedeq));
        start();
    }

    /**
     * A new service process.
     *
     * @param   service     This service is executed.
     * @param   thread      The process the service is executed within.
     * @param   qedeq       This QEDEQ module we work on.
     * @param   parameters  Interesting process parameters (e.g. QEDEQ module).
     */
    public ServiceProcessImpl(final Plugin service, final Thread thread, final KernelQedeqBo qedeq,
            final Parameters parameters) {
        this(service, thread, qedeq, parameters, null);
    }

    /**
     * A new service process within the current thread.
     *
     * @param   service     This service is executed.
     * @param   qedeq       Module we work on.
     * @param   parameters  Interesting process parameters (e.g. QEDEQ module).
     */
    public ServiceProcessImpl(final Plugin service, final KernelQedeqBo qedeq,
            final Parameters parameters) {
        this(service, Thread.currentThread(), qedeq, parameters, null);
    }

    /**
     * A new service process within the current thread.
     *
     * @param   service     This service is executed.
     * @param   qedeq       Module we work on.
     * @param   parameters  Interesting process parameters (e.g. QEDEQ module).
     */
    public ServiceProcessImpl(final Plugin service, final KernelQedeqBo qedeq,
            final Parameters parameters, final ServiceProcess parent) {
        this(service, Thread.currentThread(), qedeq, parameters, parent);
    }

    public synchronized Plugin getService() {
        return service;
    }

    public synchronized Thread getThread() {
        return thread;
    }

    public synchronized QedeqBo getQedeq() {
        return qedeq;
    }

    public synchronized Parameters getParameters() {
        return parameters;
    }

    public synchronized PluginExecutor getExecutor() {
        return executor;
    }

    public synchronized void setExecutor(final PluginExecutor executor) {
        this.executor = executor;
    }

    public synchronized String getParameterString() {
        return parameters.getParameterString();
    }

    public synchronized long getStart() {
        return start;
    }

    public synchronized long getStop() {
        return stop;
    }

    private synchronized void start() {
        start = System.currentTimeMillis();
        executionActionDescription = "started";
    }

    private synchronized void stop() {
        stop = System.currentTimeMillis();
    }

    public synchronized void setSuccessState() {
        if (isRunning()) {
            state = 1;
            stop();
            executionActionDescription = "finished";
            executionPercentage = 100;
        }
    }

    public synchronized void setFailureState() {
        if (isRunning()) {
            state = -1;
            stop();
        }
    }

    public synchronized boolean isRunning() {
        if (state == 0) {
            if (!thread.isAlive()) {
                Trace.fatal(CLASS, this, "isRunning()", "Thread has unexpectly died",
                    new RuntimeException());
                setFailureState();
                return false;
            }
            return true;
        }
        return false;
    }

    public synchronized boolean isBlocked() {
        if (isRunning()) {
            return blocked;
        }
        return false;
    }

    public synchronized void setBlocked(final boolean blocked) {
        this.blocked = blocked;
    }

    public synchronized boolean wasSuccess() {
        return state == 1;
    }

    public synchronized boolean wasFailure() {
        return state == -1;
    }

    public synchronized ServiceProcess getParentServiceProcess() {
        return parent;
    }

    public synchronized void interrupt() {
        thread.interrupt();
    }

    public synchronized double getExecutionPercentage() {
        if (isRunning() || isBlocked()) {
            if (executor != null) {
                executionPercentage = executor.getExecutionPercentage();
            }
        }
        return executionPercentage;
    }

    public synchronized String getExecutionActionDescription() {
        if (isRunning() || isBlocked()) {
            if (executor != null) {
                executionActionDescription = executor.getLocationDescription();
            }
        }
        return executionActionDescription;
    }

    public synchronized QedeqBoSet getBlockedModules() {
        return new KernelQedeqBoSet(blockedModules);
    }

    public synchronized void addBlockedModules(final KernelQedeqBoSet set) {
        blockedModules.add(set);
    }

    public synchronized void addBlockedModule(final KernelQedeqBo element) {
        blockedModules.add(element);
    }

    public synchronized void removeBlockedModules(final KernelQedeqBoSet set) {
        blockedModules.remove(set);
    }

    public synchronized void removeBlockedModule(final KernelQedeqBo element) {
        blockedModules.remove(element);
    }

}
