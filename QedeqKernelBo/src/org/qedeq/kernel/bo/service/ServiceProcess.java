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

import java.util.Date;

import org.qedeq.base.trace.Trace;

/**
 * Process info for a service.
 */
public class ServiceProcess {

    /** This class. */
    private static final Class CLASS = ServiceProcess.class;

    /** The service the thread works for. */
    private final String service;

    /** The thread the service is done within. */
    private final Thread thread;

    /** Some important parameters for the service. For example QEDEQ module address. */
    private final String parameter;

    /** Start time for process. */
    private Date start;

    /** End time for process. */
    private Date stop;

    /** State for process. 0 = running, 1 = success, -1 failure. */
    private int state;

    /**
     * A new service process.
     *
     * @param   service     This service is executed.
     * @param   thread      The process the service is executed within.
     * @param   parameter   Interesting process parameters (e.g. QEDEQ module).
     */
    public ServiceProcess(final String service, final Thread thread, final String parameter) {
        this.service = service;
        this.thread = thread;
        this.parameter = parameter;
        if (!thread.isAlive()) {
            throw new RuntimeException("thread is already dead");
        }
        start();
    }

    /**
     * A new service process within the current thread.
     *
     * @param   service     This service is executed.
     * @param   parameter   Interesting process parameters (e.g. QEDEQ module).
     */
    public ServiceProcess(final String service, final String parameter) {
        this(service, Thread.currentThread(), parameter);
    }

    private void start() {
        start = new Date();
    }

    private void stop() {
        stop = new Date();
    }

    public synchronized void setSuccessState() {
        state = 1;
        stop();
    }

    public synchronized void setFailureState() {
        state = -1;
        stop();
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

    public synchronized boolean wasSuccess() {
        return state == 1;
    }

    public synchronized boolean wasFailure() {
        return state == -1;
    }

    public synchronized void interrupt() {
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setFailureState();
    }

}
