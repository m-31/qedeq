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

package org.qedeq.kernel.bo.service.common;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;


/**
 * Information for a service call. Occurs during execution of a {@link ServiceProcess}.
 *
 * @author  Michael Meyling
 */
public interface InternalServiceCall extends ServiceCall {

    /**
     * Get QEDEQ module we work on.
     *
     * @return  QEDEQ module.
     */
    public KernelQedeqBo getKernelQedeq();

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
     * Return parent service call if any.
     *
     * @return  Parent service call. Might be <code>null</code>.
     */
    public InternalServiceCall getParentInternalServiceCall();

    /**
     * Return service process the call was initiated.
     *
     * @return  Service process for this call.
     */
    public InternalServiceProcess getInternalServiceProcess();

    /**
     * Signal an execution pause.
     */
    public void pause();

    /**
     * Signal execution resume.
     */
    public void resume();

}
