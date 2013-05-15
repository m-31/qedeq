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

package org.qedeq.kernel.se.common;


/**
 * Represents a service and its properties.
 *
 * @author  Michael Meyling
 */
public interface Service {

    /**
     * Get service id.
     *
     * @return  Service id
     */
    public String getServiceId();

    /**
     * Get plugin action name. This is what the plugin does.
     *
     * @return  Plugin name
     */
    public String getServiceAction();

    /**
     * Get service description.
     *
     * @return  Description of service.
     */
    public String getServiceDescription();

}
