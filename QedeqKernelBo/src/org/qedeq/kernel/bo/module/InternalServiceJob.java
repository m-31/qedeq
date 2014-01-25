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

import org.qedeq.kernel.bo.common.ServiceJob;


/**
 * Process info for a kernel service.
 *
 * @author  Michael Meyling
 */
public interface InternalServiceJob extends ServiceJob {

    /**
     * Mark that thread execution has normally ended.
     */
    public void setSuccessState();

    /**
     * Set blocked state.
     *
     * @param   blocked Blocked state.
     */
    public void setBlocked(boolean blocked);

    /**
     * Mark that thread execution was canceled.
     */
    public void setFailureState();

    /**
     * Service call.
     *
     * @param   call    Execute this service call.
     */
    public void setInternalServiceCall(final InternalModuleServiceCall call);

    /**
     * Get currently running service call.
     *
     * @return  Service call.
     */
    public InternalModuleServiceCall getInternalServiceCall();

}
