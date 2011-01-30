/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import java.util.Iterator;
import java.util.Map;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.PluginExecutor;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.ServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
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
    private final Map parameters;

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

    /**
     * A new service process.
     *
     * @param   service     This service is executed.
     * @param   qedeq       This QEDEQ module we work on.
     * @param   thread      The process the service is executed within.
     * @param   parameters  Interesting process parameters (e.g. QEDEQ module).
     */
    public ServiceProcessImpl(final Plugin service, final Thread thread, final KernelQedeqBo qedeq,
            final Map parameters) {
        this.service = service;
        this.thread = thread;
        this.qedeq = qedeq;
        this.parameters = parameters;
        if (!thread.isAlive()) {
            throw new RuntimeException("thread is already dead");
        }
        start();
    }

    /**
     * A new service process within the current thread.
     *
     * @param   service     This service is executed.
     * @param   qedeq       Module we work on.
     * @param   parameters  Interesting process parameters (e.g. QEDEQ module).
     */
    public ServiceProcessImpl(final Plugin service, final KernelQedeqBo qedeq, final Map parameters) {
        this(service, Thread.currentThread(), qedeq, parameters);
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getService()
     */
    public synchronized Plugin getService() {
        return service;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getThread()
     */
    public synchronized Thread getThread() {
        return thread;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getQedeq()
     */
    public synchronized QedeqBo getQedeq() {
        return qedeq;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getParameters()
     */
    public synchronized Map getParameters() {
        return parameters;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getExecutor()
     */
    public synchronized PluginExecutor getExecutor() {
        return executor;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#setExecutor(org.qedeq.kernel.bo.module.PluginExecutor)
     */
    public synchronized void setExecutor(final PluginExecutor executor) {
        this.executor = executor;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getParameterString()
     */
    public synchronized String getParameterString() {
        final StringBuffer buffer = new StringBuffer(30);
        final int len = service.getPluginId().length() + 1;
        if (parameters != null) {
            Iterator e = parameters.entrySet().iterator();
            boolean notFirst = false;
            while (e.hasNext()) {
                final Map.Entry entry = (Map.Entry) e.next();
                String key = String.valueOf(entry.getKey());
                if (key.startsWith(service.getPluginId() + "$")) {
                    if (notFirst) {
                        buffer.append(", ");
                    } else {
                        notFirst = true;
                    }
                    key = key.substring(len);
                    buffer.append(key);
                    buffer.append("=");
                    buffer.append(String.valueOf(entry.getValue()));
                }
            }
        }
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getStart()
     */
    public synchronized long getStart() {
        return start;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getStop()
     */
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

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#setSuccessState()
     */
    public synchronized void setSuccessState() {
        if (isRunning()) {
            state = 1;
            stop();
            executionActionDescription = "finished";
            executionPercentage = 100;
        }
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#setFailureState()
     */
    public synchronized void setFailureState() {
        if (isRunning()) {
            state = -1;
            stop();
        }
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#isRunning()
     */
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

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#isBlocked()
     */
    public synchronized boolean isBlocked() {
        if (isRunning()) {
            return blocked;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#setBlocked(boolean)
     */
    public synchronized void setBlocked(final boolean blocked) {
        this.blocked = blocked;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#wasSuccess()
     */
    public synchronized boolean wasSuccess() {
        return state == 1;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#wasFailure()
     */
    public synchronized boolean wasFailure() {
        return state == -1;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#interrupt()
     */
    public synchronized void interrupt() {
        thread.interrupt();
        setFailureState();
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getExecutionPercentage()
     */
    public synchronized double getExecutionPercentage() {
        if (isRunning() || isBlocked()) {
            if (executor != null) {
                executionPercentage = executor.getExecutionPercentage();
            }
        }
        return executionPercentage;
    }

    /* (non-Javadoc)
     * @see org.qedeq.kernel.bo.ServiceProcess#getExecutionActionDescription()
     */
    public synchronized String getExecutionActionDescription() {
        if (isRunning() || isBlocked()) {
            if (executor != null) {
                executionActionDescription = executor.getExecutionActionDescription();
            }
        }
        return executionActionDescription;
    }

}
