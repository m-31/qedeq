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

import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.ServiceProcess;


/**
 * Information for a service call. Occurs during execution of a {@link ServiceProcess}.
 *
 * @author  Michael Meyling
 */
public interface ServiceCall extends Comparable {

    /**
     * Get QEDEQ module we work on.
     *
     * @return  QEDEQ module.
     */
    public QedeqBo getQedeq();

    /**
     * Get service we work for.
     *
     * @return  service
     */
    public Service getService();

    /**
     * Get global config parameter for service call as string.
     *
     * @return  Service parameter.
     */
    public String getConfigParametersString();

    /**
     * Get call specific parameters for service call as string.
     *
     * @return  Service parameters.
     */
    public String getParametersString();

    /**
     * Get timestamp for service start.
     *
     * @return  Service start timestamp.
     */
    public long getBeginTime();

    /**
     * Get timestamp for service stop.
     *
     * @return  Service stop timestamp.
     */
    public long getEndTime();

    /**
     * Return parent service call if any.
     *
     * @return  Parent service call. Might be <code>null</code>.
     */
    public ServiceCall getParentServiceCall();

    /**
     * Return service process the call was initiated by.
     *
     * @return  Service process for this call.
     */
    public ServiceProcess getServiceProcess();

    /**
     * Is this service still running?
     *
     * @return  Still running?
     */
    public boolean isRunning();

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
    public String getAction();

    /**
     * Result of service execution.
     *
     * @return  Result. Might be <code>null</code>.
     */
    public ServiceResult getServiceResult();

    /**
     * Get call id.
     *
     * @return  Service call identifying number.
     */
    public long getId();

}
