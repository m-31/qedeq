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
import java.util.Map;

import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.Plugin;

/**
 * Manage all known processes.
 */
public class ServiceProcessManager {

    /** This class. */
    private static final Class CLASS = ServiceProcessManager.class;

    /** Stores all processes. */
    private final List processes = new ArrayList();


    /**
     * Get all service processes.
     *
     * @return  All service processes.
     */
    public synchronized ServiceProcess[] getServiceProcesses() {
        return (ServiceProcess[]) processes.toArray(new ServiceProcess[] {});
    }

    /**
     * Create service process.
     *
     * @param   service     The service that runs in current thread.
     * @param   qedeq       QEDEQ module for service.
     * @param   parameters  Parameter for the service.
     * @return  Created process.
     */
    public synchronized ServiceProcess createProcess(final Plugin service,
            final KernelQedeqBo qedeq, final Map parameters) {
        final ServiceProcess process = new ServiceProcess(service, qedeq, parameters);
        processes.add(process);
        return process;
    }

    /**
     * Remove all service processes. All processes are also terminated via interruption.
     */
    public synchronized void terminateAndRemoveAllServiceProcesses() {
        for (int i = 0; i < processes.size(); i++) {
            final ServiceProcess proc = (ServiceProcess) processes.get(i);
            proc.interrupt();
        }
        processes.clear();
    }


}
