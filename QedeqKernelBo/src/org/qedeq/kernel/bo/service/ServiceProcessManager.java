/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.common.ServiceProcess;

/**
 * Manage all known processes.
 */
public class ServiceProcessManager {

    /** This class. */
    private static final Class CLASS = ServiceProcessManager.class;

    /** Stores all processes. */
    private final List processes = new ArrayList();


    /**
     * Get all running service processes.
     *
     * @return  All running service processes.
     */
    public synchronized ServiceProcess[] getServiceProcesses() {
        return (ServiceProcess[]) processes.toArray(new ServiceProcess[] {});
    }

    /**
     * Create service process.
     *
     * @param   service     The service that runs in current thread.
     * @param   parameter   Parameter for the service.
     * @return  Created process.
     */
    public synchronized ServiceProcess createProcess(final String service, final String parameter) {
        final ServiceProcess process = new ServiceProcess(service, parameter);
        processes.add(process);
        return process;
    }

}
