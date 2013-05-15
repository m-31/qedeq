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

import org.qedeq.kernel.bo.service.common.ServiceCall;
import org.qedeq.kernel.se.common.Plugin;


/**
 * Information for a service call. Occurs during execution of a {@link ServiceProcess}.
 *
 * @author  Michael Meyling
 */
public interface PluginCall extends ServiceCall {

    /**
     * Get plugin we work for.
     *
     * @return  service
     */
    public Plugin getPlugin();

    /**
     * Return parent service call if any.
     *
     * @return  Parent service call. Might be <code>null</code>.
     */
    public PluginCall getParentPluginCall();

    /**
     * Is the call finished?
     *
     * @return  Call finished?
     */
    public boolean isFinished();

    /**
     * Was the call normally finished?
     *
     * @return  Was the call normally finished?
     */
    public boolean hasNormallyFinished();

    /**
     * Was the call interrupted?
     *
     * @return  Call finished?
     */
    public boolean wasInterrupted();

    /**
     * Get description of currently taken action.
     *
     * @return  We are doing this currently.
     */
    public String getExecutionActionDescription();

    /**
     * Return code of plugin execution.
     *
     * @return  Return code.
     */
    public int getReturnCode();

    /**
     * Result of plugin execution.
     *
     * @return  Result. Might be <code>null</code>.
     */
    public Object getExecutionResult();

    /**
     * Get call id.
     *
     * @return  Service call identifying number.
     */
    public long getId();

}
