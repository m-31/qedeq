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

package org.qedeq.kernel.bo.common;

import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.KernelQedeqBoSet;


/**
 * Process info for a kernel service.
 *
 * @author  Michael Meyling
 */
public interface ServiceProcess extends Comparable {

    /**
     * Get currently running plugin call.
     *
     * @return  Plugin call.
     */
    public PluginCall getPluginCall();

    /**
     * Plugin execution.
     *
     * @param   call    Execute this plugin call.
     */
    public void setPluginCall(final PluginCall call);

    /**
     * Get thread the service runs within.
     *
     * @return  Service thread.
     */
    public Thread getThread();

    /**
     * Get currently processed QedeqBo.
     *
     * @return  QEDEQ module. Might be <code>null</code>.
     */
    public QedeqBo getQedeq();

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
     * Mark that thread execution has normally ended.
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
     * Interrupt running thread. Usually because of user canceling. This should initiate a
     * {@link org.qedeq.kernel.se.visitor.InterruptException} when {@link Thread.interrupted()}
     * is <code>true</code>.
     */
    public void interrupt();

    /**
     * Get action name. This is what the process mainly does.
     *
     * @return  Action name.
     */
    public String getActionName();

    /**
     * Get percentage of currently running execution.
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

    /**
     * Get {@link QedeqModule}s blocked by this process.
     *
     * @return  Blocked QEDEQ modules.
     */
    public QedeqBoSet getBlockedModules();

    public void addBlockedModules(KernelQedeqBoSet set);

    public void addBlockedModule(KernelQedeqBo element);

    public void removeBlockedModules(KernelQedeqBoSet set);

    public void removeBlockedModule(KernelQedeqBo element);

    /**
     * Get process id.
     *
     * @return  Process identifying number.
     */
    public long getId();

}
