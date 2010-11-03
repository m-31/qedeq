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

import java.util.Iterator;
import java.util.Map;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.Plugin;

/**
 * Process info for a service.
 */
public class ServiceProcess {

    /** This class. */
    private static final Class CLASS = ServiceProcess.class;

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

    /** QEDEQ module visitor that is used. */
    private ControlVisitor vistor;

    /**
     * A new service process.
     *
     * @param   service     This service is executed.
     * @param   qedeq       This QEDEQ module we work on.
     * @param   thread      The process the service is executed within.
     * @param   parameters  Interesting process parameters (e.g. QEDEQ module).
     */
    public ServiceProcess(final Plugin service, final Thread thread, final KernelQedeqBo qedeq,
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
    public ServiceProcess(final Plugin service, final KernelQedeqBo qedeq, final Map parameters) {
        this(service, Thread.currentThread(), qedeq, parameters);
    }

    /**
     * Get service.
     *
     * @return  service
     */
    public synchronized Plugin getService() {
        return service;
    }

    /**
     * Get thread the service runs within.
     *
     * @return  Service thread.
     */
    public synchronized Thread getThread() {
        return thread;
    }

    /**
     * Get service.
     *
     * @return  service
     */
    public synchronized KernelQedeqBo getQedeq() {
        return qedeq;
    }

    /**
     * Get service parameter.
     *
     * @return  Service parameter.
     */
    public synchronized Map getParameters() {
        return parameters;
    }

    /**
     * Get service parameter. Filters only for parameters that are explicitly for this plugin.
     *
     * @return  Service parameter.
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

    /**
     * Get timestamp for service start.
     *
     * @return  Service start timestamp.
     */
    public synchronized long getStart() {
        return start;
    }

    /**
     * Get timestamp for service stop.
     *
     * @return  Service stop timestamp.
     */
    public synchronized long getStop() {
        return stop;
    }

    private void start() {
        start = System.currentTimeMillis();
    }

    private void stop() {
        stop = System.currentTimeMillis();
    }

    public synchronized void setSuccessState() {
        if (isRunning()) {
            state = 1;
            stop();
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
                state = -1;
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

    public synchronized void interrupt() {
        thread.interrupt();
        setFailureState();
    }

}
