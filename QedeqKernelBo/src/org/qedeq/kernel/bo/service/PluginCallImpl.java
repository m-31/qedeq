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
import org.qedeq.kernel.bo.common.PluginCall;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.KernelQedeqBoSet;
import org.qedeq.kernel.se.common.Plugin;

/**
 * Single call for a service.
 *
 * @author  Michael Meyling
 */
public class PluginCallImpl implements PluginCall {

    /** This class. */
    private static final Class CLASS = PluginCallImpl.class;

    /** Counter for each service call. */
    private static long globalCounter;

    /** The service the thread works for. */
    private final Plugin plugin;

    /** QEDEQ module the process is working on. */
    private KernelQedeqBo qedeq;

    /** Some important parameters for the service. For example QEDEQ module address. */
    private final Parameters parameters;

    /** Start time for process. */
    private long start;

    /** End time for process. */
    private long stop;

    /** State for process. 0 = running, 1 = success, -1 finished by interrupt. */
    private int state;

    /** Percentage of currently running plugin execution. */
    private double executionPercentage = 0;

    /** Percentage of currently running plugin execution. */
    private String executionActionDescription = "not yet started";

    /** Created execution object. Might be <code>null</code>. */
    private PluginExecutor executor;

    /** Service process. */
    private final ServiceProcess process;

    /** Parent plugin call. Might be <code>null</code>. */
    private final PluginCall parent;

    /** Call id. */
    private final long id;

    /** Return code. */
    private int ret;

    /** Resulting object. */
    private Object result;

    /**
     * A new service process within the current thread.
     *
     * @param   service     This service is executed.
     * @param   qedeq       Module we work on.
     * @param   parameters  Interesting process parameters (e.g. QEDEQ module).
     * @param   process     Service process we run within.
     * @param   parent      Parent plugin call if any. 
     */
    public PluginCallImpl(final Plugin service,
            final KernelQedeqBo qedeq, final Parameters parameters, final ServiceProcess process,
            final PluginCall parent) {
        this.id = globalCounter++;
        this.qedeq = qedeq;
        this.plugin = service;
        this.parameters = parameters;
        this.process = process;
        if (!process.getThread().isAlive()) {
            throw new RuntimeException("thread is already dead");
        }
        this.parent = parent;
        start();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public QedeqBo getQedeq() {
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

    public String getParameterString() {
        return parameters.getParameterString();
    }

    public long getStart() {
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
        return state == 0;
    }

    public synchronized boolean isFinished() {
        return state != 0;
    }

    public synchronized boolean wasInterrupted() {
        return state == -1;
    }

    public synchronized boolean wasSuccess() {
        return state == 1;
    }

    public synchronized boolean wasFailure() {
        return state == -1;
    }

    public synchronized ServiceProcess getServiceProcess() {
        return process;
    }

    public synchronized double getExecutionPercentage() {
        if (isRunning()) {
            if (executor != null) {
                executionPercentage = executor.getExecutionPercentage();
            }
        }
        return executionPercentage;
    }

    public synchronized String getExecutionActionDescription() {
        if (isRunning()) {
            if (executor != null) {
                executionActionDescription = executor.getLocationDescription();
            }
        }
        return executionActionDescription;
    }

    public long getId() {
        return id;
    }

    public int hashCode() {
        return (int) id;
    }

    public boolean equals(final Object obj) {
        return 0 == compareTo(obj);
    }

    public int compareTo(final Object o) {
        if (!(o instanceof PluginCall)) {
            return -1;
        }
        final PluginCall s = (PluginCall) o;
        return (getId() < s.getId() ? -1 : (getId() == s.getId() ? 0 : 1));
    }

    public synchronized PluginCall getParentPluginCall() {
        return parent;
    }

    public synchronized Object getExecutionResult() {
        return result;
    }

    public synchronized int getReturnCode() {
        return ret;
    }

}
