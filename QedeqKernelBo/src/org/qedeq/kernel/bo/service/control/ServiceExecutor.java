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

package org.qedeq.kernel.bo.service.control;

import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.se.visitor.InterruptException;


/**
 * Represents a service execution.
 *
 * @author  Michael Meyling
 */
public interface ServiceExecutor {

    /**
     * Execute service.
     *
     * @param   call    Parameters for service call and current execution status.
     * @throws  InterruptException  User canceled call.
     */
    public void executeService(InternalModuleServiceCall call) throws InterruptException;

}
