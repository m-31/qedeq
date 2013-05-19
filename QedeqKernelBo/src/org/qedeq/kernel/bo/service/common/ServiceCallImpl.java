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

package org.qedeq.kernel.bo.service.common;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * Single call for a service.
 *
 * @author  Michael Meyling
 */
public class ServiceCallImpl implements InternalServiceCall {

    /** Counter for each service call. */
    private static volatile long globalCounter;

    /** The service the thread works for. */
    private final Service service;

    /** QEDEQ module the process is working on. */
    private final KernelQedeqBo qedeq;

    /** Current global config parameters for the service. */
    private final Parameters config;

    /** Call specific parameters for this service call. */
    private final Parameters parameters;

    /** Begin time for service call. */
    private long begin;

    /** End time for service call. */
    private long end;

    /** Last time we started. */
    private long start;

    /** Call duration time without being blocked. */
    private long duration;

    /** Is this process paused? */
    private boolean paused;

    /** Is this process running (even if its paused)? */
    private boolean running;

    /** Percentage of currently running service execution. */
    private double executionPercentage;

    /** Currently taken action. */
    private String action = "not yet started";

    /** Service process. */
    private final InternalServiceProcess process;

    /** Parent service call. Might be <code>null</code>. */
    private final ServiceCall parent;

    /** Call id. */
    private final long id;

    /** Result of service call. */
    private ServiceResult result;

    /** Was the module newly blocked by this call. Otherwise a previous service call might have locked the module
     * for the process already. */
    private boolean newBlockedModule;

    /**
     * A new service process within the current thread.
     *
     * @param   service     This service is executed.
     * @param   qedeq       Module we work on.
     * @param   config      Current global config parameters for this call.
     * @param   parameters  Call specific parameters..
     * @param   process     Service process we run within.
     * @param   parent      Parent service call if any.
     */
    public ServiceCallImpl(final Service service, final KernelQedeqBo qedeq,
            final Parameters config, final Parameters parameters, final InternalServiceProcess process,
            final ServiceCall parent) {
        this.id = inc();
        this.qedeq = qedeq;
        this.service = service;
        this.config = config;
        this.parameters = parameters;
        this.process = process;
        this.parent = parent;
        running = false;
    }

    public void start() throws InterruptException {
        if (!running && result == null) {
            begin();
            if (!process.isRunning()) {
                throw new RuntimeException("Service process is not running any more.");
            }
            if (!process.getThread().isAlive()) {
                throw new RuntimeException("thread is already dead");
            }
            process.setServiceCall(this);
            pause();
            newBlockedModule = process.lockRequiredModule(qedeq);
            resume();
        }
    }

    private synchronized long inc() {
        return globalCounter++;
    }

    public Service getService() {
        return service;
    }

    public QedeqBo getQedeq() {
        return qedeq;
    }

    public synchronized Parameters getConfigParameters() {
        return config;
    }

    public String getConfigParametersString() {
        return config.getParameterString();
    }

    public synchronized Parameters getParameters() {
        return parameters;
    }

    public String getParametersString() {
        return parameters.getParameterString();
    }

    public long getBeginTime() {
        return begin;
    }

    public synchronized long getEndTime() {
        return end;
    }

    public synchronized long getDuration() {
        return duration;
    }

    private synchronized void begin() {
        begin = System.currentTimeMillis();
        start = begin;
        action = "started";
        running = true;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized void pause() {
        duration += System.currentTimeMillis() - start;
        paused = true;
        process.setBlocked(true);
    }

    public synchronized void resume() {
        paused = false;
        start = System.currentTimeMillis();
        process.setBlocked(false);
    }

    private synchronized void end() {
        if (newBlockedModule) {
            process.unlockRequiredModule(qedeq);
        }
        end = System.currentTimeMillis();
        duration += end - start;
        paused = false;
        running = false;
    }

    public synchronized void finish() {
        finish(ServiceResultImpl.SUCCESSFUL);
    }

    public synchronized void finish(final String errorMessage) {
        finish(new ServiceResultImpl(errorMessage));
    }

    public synchronized void finish(final ServiceResult result) {
        if (running) {
            action = "finished";
            executionPercentage = 100;
            this.result = result;
            end();
        }
    }

    public synchronized void halt(final ServiceResult result) {
        if (running) {
            this.result = result;
            end();
        }
    }

    public synchronized void halt(final String errorMessage) {
        halt(new ServiceResultImpl(errorMessage));
    }

    public synchronized void interrupt() {
        if (running) {
            this.result = ServiceResultImpl.INTERRUPTED;
            process.setFailureState();
            end();
        }
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized ServiceProcess getServiceProcess() {
        return process;
    }

    public synchronized double getExecutionPercentage() {
        return executionPercentage;
    }

    public synchronized void setExecutionPercentage(final double percentage) {
        this.executionPercentage = percentage;
    }

    public synchronized String getAction() {
        return action;
    }

    public synchronized void setAction(final String action) {
        this.action = action;
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
        if (!(o instanceof ServiceCall)) {
            return -1;
        }
        final ServiceCall s = (ServiceCall) o;
        return (getId() < s.getId() ? -1 : (getId() == s.getId() ? 0 : 1));
    }

    public ServiceCall getParentServiceCall() {
        return parent;
    }

    public synchronized ServiceResult getServiceResult() {
        return result;
    }

    public KernelQedeqBo getKernelQedeq() {
        return qedeq;
    }

    public InternalServiceProcess getInternalServiceProcess() {
        return process;
    }

}
