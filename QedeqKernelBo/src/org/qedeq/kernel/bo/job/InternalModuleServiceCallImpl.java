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

package org.qedeq.kernel.bo.job;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.ModuleServiceCall;
import org.qedeq.kernel.bo.common.ModuleServiceResult;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.ServiceJob;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.ServiceCompleteness;

/**
 * Single call for a service.
 *
 * @author  Michael Meyling
 */
public class InternalModuleServiceCallImpl implements InternalModuleServiceCall {

    /** Counter for each service call. */
    private static volatile long globalCounter;

    /** The service the thread works for. */
    private final Service service;

    /** QEDEQ module the process is working on. */
    private final QedeqBo qedeq;

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
    private final InternalServiceJob process;

    /** Parent service call. Might be <code>null</code>. */
    private final ModuleServiceCall parent;

    /** Call id. */
    private final long id;

    /** Result of service call. */
    private ModuleServiceResult result;

    /** Was the module newly blocked by this call. Otherwise a previous service call might have locked the module
     * for the process already. */
    private boolean newlyBlockedModule;

    /** Answers completeness questions if not <code>null</code>. */
    private ServiceCompleteness completeness;

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
    public InternalModuleServiceCallImpl(final Service service, final QedeqBo qedeq,
            final Parameters config, final Parameters parameters, final InternalServiceJob process,
            final ModuleServiceCall parent) {
        this.id = inc();
        this.qedeq = qedeq;
        this.service = service;
        this.config = config;
        this.parameters = parameters;
        this.process = process;
        this.parent = parent;
        running = false;
        begin();
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
    }

    public synchronized void resume() {
        paused = false;
        start = System.currentTimeMillis();
    }

    private synchronized void end() {
        end = System.currentTimeMillis();
        duration += end - start;
        paused = false;
        running = false;
    }

    public void setNewlyBlockedModule(final boolean newlyBlockedModule) {
        this.newlyBlockedModule = newlyBlockedModule;
    }

    public boolean getNewlyBlockedModule() {
        return this.newlyBlockedModule;
    }

    public synchronized void finishOk() {
        finish(ServiceResultImpl.SUCCESSFUL);
    }

    public synchronized void finishError(final String errorMessage) {
        finish(new ServiceResultImpl(errorMessage));
    }

    public synchronized void finish(final ModuleServiceResult result) {
        if (running) {
            action = "finished";
            executionPercentage = 100;
            completeness = null;
            this.result = result;
            end();
        }
    }

    public synchronized void halt(final ModuleServiceResult result) {
        if (running) {
            this.result = result;
            if (completeness != null) {
                executionPercentage = completeness.getVisitPercentage();
            } else {
                completeness = null;
            }
            end();
        }
    }

    public synchronized void halt(final String errorMessage) {
        halt(new ServiceResultImpl(errorMessage));
    }

    public synchronized void interrupt() {
        if (running) {
            this.result = ServiceResultImpl.INTERRUPTED;
            if (completeness != null) {
                executionPercentage = completeness.getVisitPercentage();
            } else {
                completeness = null;
            }
            process.setInterruptedState();
            end();
        }
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized ServiceJob getServiceProcess() {
        return process;
    }

    public synchronized double getExecutionPercentage() {
        if (completeness != null) {
            executionPercentage = completeness.getVisitPercentage();
        }
        return executionPercentage;
    }

    public synchronized void setExecutionPercentage(final double percentage) {
        this.executionPercentage = percentage;
    }

    public synchronized String getAction() {
        return action;
    }

    public synchronized String getLocation() {
        if (completeness != null) {
            return completeness.getLocationDescription();
        }
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
        if (!(o instanceof ModuleServiceCall)) {
            return -1;
        }
        final ModuleServiceCall s = (ModuleServiceCall) o;
        return (getId() < s.getId() ? -1 : (getId() == s.getId() ? 0 : 1));
    }

    public ModuleServiceCall getParentServiceCall() {
        return parent;
    }

    public synchronized ModuleServiceResult getServiceResult() {
        return result;
    }

    public InternalServiceJob getInternalServiceProcess() {
        return process;
    }

    public synchronized void setServiceCompleteness(final ServiceCompleteness completeness) {
        this.completeness = completeness;
    }

}
