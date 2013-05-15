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
import org.qedeq.kernel.bo.common.PluginCall;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.bo.service.common.InternalServiceCall;
import org.qedeq.kernel.bo.service.common.ServiceCallImpl;
import org.qedeq.kernel.se.common.Plugin;

/**
 * Single call for a service.
 *
 * @author  Michael Meyling
 */
public class PluginCallImpl extends ServiceCallImpl implements PluginCall {

    /** Execution object. Might be <code>null</code>. */
    private PluginExecutor executor;


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
        super(service, qedeq, parameters, Parameters.EMPTY, (InternalServiceProcess) process, (InternalServiceCall) parent);  
    }

    public Plugin getPlugin() {
        return (Plugin) getService();
    }

    /**
     * Get current executor for this call.
     *
     * @return  Current executor, might be <code>null</code>.
     */
    public synchronized PluginExecutor getExecutor() {
        return executor;
    }

    /**
     * Set current executor for this call.
     *
     * @param   executor    Executor, might be <code>null</code>.
     */
    public synchronized void setExecutor(final PluginExecutor executor) {
        this.executor = executor;
    }

    public long getStart() {
        return getBeginTime();
    }

    public long getStop() {
        return getEndTime();
    }

    /**
     * Set success state for call and stop.
     */
    public synchronized void setSuccessState() {
        finish();
    }

    /**
     * Set failure state for call and stop.
     */
    public synchronized void setFailureState() {
        finish("Failure");
    }

    public synchronized boolean isFinished() {
        return !isRunning();
    }

    public synchronized boolean wasInterrupted() {
        if (isRunning()) {
            return false;
        }
        return getServiceResult().wasInterrupted();
    }

    public synchronized boolean hasNormallyFinished() {
        if (isRunning()) {
            return false;
        }
        return !wasInterrupted();
    }

    public synchronized double getExecutionPercentage() {
        if (isRunning()) {
            if (executor != null) {
                setExecutionPercentage(executor.getExecutionPercentage());
            }
        }
        return super.getExecutionPercentage();
    }

    public synchronized String getExecutionActionDescription() {
        if (isRunning()) {
            if (executor != null) {
                return executor.getActionDescription();
            }
        }
        return getAction();
    }

    public synchronized PluginCall getParentPluginCall() {
        return (PluginCall) getParentServiceCall();
    }

    public synchronized Object getExecutionResult() {
        return getServiceResult().getExecutionResult();
    }

    public synchronized int getReturnCode() {
        return 0;
    }

    public String getParameterString() {
        return "";
    }


}
