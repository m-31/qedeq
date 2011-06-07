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

package org.qedeq.kernel.bo.common;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.se.common.Plugin;


/**
 * Process info for a service.
 *
 * @author  Michael Meyling
 */
public interface ServiceProcess {

    /**
     * Get service.
     *
     * @return  service
     */
    public Plugin getService();

    /**
     * Get thread the service runs within.
     *
     * @return  Service thread.
     */
    public Thread getThread();

    /**
     * Get service.
     *
     * @return  service
     */
    public QedeqBo getQedeq();

    /**
     * Get service parameter.
     *
     * @return  Service parameter.
     */
    public Parameters getParameters();

    /**
     * Get associated executor.
     *
     * @return  Associated executor. Might be <code>null</code>.
     */
    public PluginExecutor getExecutor();

    /**
     * Set associated executor.
     *
     * @param   executor    Associated executor.
     */
    public void setExecutor(final PluginExecutor executor);

    /**
     * Get service parameter. Filters only for parameters that are explicitly for this plugin.
     *
     * @return  Service parameter.
     */
    public String getParameterString();

    /**
     * Get timestamp for service start.
     *
     * @return  Service start timestamp.
     */
    public long getStart();

    /**
     * Get timestamp for service stop.
     *
     * @return  Service stop timestamp.
     */
    public long getStop();

    /**
     * Mark that thread execution was has normally ended.
     */
    public void setSuccessState();

    /**
     * Mark that thread execution was canceled.
     */
    public void setFailureState();

    /**
     * Is the process still running?
     *
     * @return  The process is still running. (But it might be blocked.)
     */
    public boolean isRunning();

    /**
     * Is the process running, but is blocked?
     *
     * @return  Process is running and blocked.
     */
    public boolean isBlocked();

    /**
     * Set blocked state.
     *
     * @param   blocked Blocked state.
     */
    public void setBlocked(final boolean blocked);

    /**
     * Has the process normally ended?
     *
     * @return  Has the process normally ended?
     */
    public boolean wasSuccess();

    /**
     * Has the process been canceled?
     *
     * @return  The process has been canceled.
     */
    public boolean wasFailure();

    /**
     * Interrupt running thread. Usually because of user canceling.
     */
    public void interrupt();

    /**
     * Get percentage of currently running plugin execution.
     *
     * @return  Number between 0 and 100.
     */
    public double getExecutionPercentage();

    /**
     * Get description of currently taken action.
     *
     * @return  We are doing this currently.
     */
    public String getExecutionActionDescription();

}
