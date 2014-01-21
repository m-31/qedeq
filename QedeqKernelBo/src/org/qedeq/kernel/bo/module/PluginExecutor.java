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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.se.common.ServiceCompleteness;
import org.qedeq.kernel.se.visitor.InterruptException;



/**
 * Represents a plugin execution.
 *
 * @author  Michael Meyling
 */
public interface PluginExecutor extends ServiceCompleteness {

    /**
     * Execute plugin.
     *
     * @param   call        Service call.
     * @param   data        Process execution data.
     * @return  Plugin specific resulting object. Might be <code>null</code>.
     * @throws  InterruptException    User canceled execution.
     */
    public Object executePlugin(final InternalServiceCall call, final Object data) throws InterruptException;

    /**
     * Get percentage of currently running plugin execution.
     *
     * @return  Number between 0 and 100.
     */
    public double getVisitPercentage();

    /**
     * Get description of currently taken action.
     *
     * @return  We are doing this currently.
     */
    public String getLocationDescription();

    /**
     * Was the execution interrupted by the user?
     *
     * @return  The execution was interrupted.
     */
    public boolean getInterrupted();

}
