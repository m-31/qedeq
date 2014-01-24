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

package org.qedeq.kernel.bo.common;



/**
 * Execution result of a {@link ModuleServiceCall}.
 *
 * @author  Michael Meyling
 */
public interface ServiceResult {

    /**
     * Was the call interrupted by user interaction?
     *
     * @return  Call was interrupted by a user.
     */
    public boolean wasInterrupted();

    /**
     * Did the call finish without errors and warnings? (And was not interrupted by a user.)
     *
     * @return  Call finished successfully.
     */
    public boolean isOk();

    /**
     * Did the service call finish with warnings?
     *
     * @return Occurred warnings?
     */
    public boolean hasWarnings();

    /**
     * Did the service call finish with errors?
     *
     * @return Occurred errors?
     */
    public boolean hasErrors();

    /**
     * Error (and perhaps warning) messages for service call. Never <code>null</code>.
     * Should be of zero length if call {@link #isOk()}.
     *
     * @return  Error messages.
     */
    public String getErrorMessage();

    /**
     * Result of service execution.
     *
     * @return  Result. Might be <code>null</code>.
     */
    public Object getExecutionResult();

}
