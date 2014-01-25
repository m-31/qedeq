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

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.ModuleServiceCall;
import org.qedeq.kernel.bo.common.ModuleServiceResult;
import org.qedeq.kernel.se.common.ServiceCompleteness;


/**
 * Information for a service call. Occurs during execution of a
 * {@link org.qedeq.kernel.se.common.ServiceJob}.
 *
 * @author  Michael Meyling
 */
public interface InternalModuleServiceCall extends ModuleServiceCall {

    /**
     * Get global config parameters for service call.
     *
     * @return  Service parameter.
     */
    public Parameters getConfigParameters();

    /**
     * Get call specific parameters for service call.
     *
     * @return  Service parameter.
     */
    public Parameters getParameters();

    /**
     * Set percentage of currently running plugin execution.
     *
     * @param   percentage  Number between 0 and 100.
     */
    public void setExecutionPercentage(double percentage);

    /**
     * Set someone who answers completeness questions.
     *
     * @param   completeness    An answer for completeness questions. Might be <code>null</code>.
     */
    public void setServiceCompleteness(ServiceCompleteness completeness);

    /**
     * Set description of currently taken action.
     *
     * @param   action  We are doing this currently.
     */
    public void setAction(String action);

    /**
     * Return service process the call was initiated.
     *
     * @return  Service process for this call.
     */
    public InternalServiceJob getInternalServiceProcess();

    /**
     * Signal an execution pause.
     */
    public void pause();

    /**
     * Signal execution resume.
     */
    public void resume();

    /**
     * Set generic success result for call and stop.
     * Can only be done if call is still running.
     */
    public void finish();

    /**
     * Set generic failure result for call and stop.
     * Can only be done if call is still running.
     *
     * @param   errorMessage    Reason for finishing with error.
     */
    public void finish(final String errorMessage);

    /**
     * Set result state for call and stop.
     * Can only be done if call is still running.
     *
     * @param   result  Service result.
     */
    public void finish(ModuleServiceResult result);

    /**
     * Set result state for call and stop.
     * Can only be done if call is still running.
     *
     * @param   result  Must include reason for halting.
     */
    public void halt(ModuleServiceResult result);

    /**
     * Set generic failure result for call and stop.
     * Can only be done if call is still running.
     *
     * @param   errorMessage    Reason for halting.
     */
    public void halt(final String errorMessage);

    /**
     * Set failure state for call and stop.
     * Can only be done if call is still running.
     */
    public void interrupt();

}
