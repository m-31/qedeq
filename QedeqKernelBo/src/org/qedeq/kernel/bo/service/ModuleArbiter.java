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

package org.qedeq.kernel.bo.service;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;

/**
 * Get locks for modules.
 *
 * @author  Michael Meyling
 */
public class ModuleArbiter {

    final Map blocked;
    
    public ModuleArbiter() {
        blocked = new HashMap();
    }

    public void lockRequiredModules(final ServiceProcess process, final KernelQedeqBo qedeq) {
        while (!lock(process, qedeq)) {
            IoUtility.sleep(10000);
            if (Thread.interrupted()) {
                process.setFailureState();
                break;
            }
        }
    }

    private synchronized boolean lock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        System.out.println(StringUtility.format(process.getId(), 3) + " is trying to lock   " + qedeq.getName());
        final ServiceProcess origin = (ServiceProcess) blocked.get(qedeq);
        if (origin != null) {
            System.out.println(StringUtility.format(process.getId(), 3) + " failed to lock      " + qedeq.getName());
            return false;
        }
        System.out.println(StringUtility.format(process.getId(), 3) + " locked successfuly  " + qedeq.getName());
        blocked.put(qedeq, process);
        return true;
    }

    public void unlockRequiredModules(final ServiceProcess process, final KernelQedeqBo qedeq) {
        unlock(process, qedeq);
    }

    private synchronized void unlock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        System.out.println(StringUtility.format(process.getId(), 3) + " is trying to unlock " + qedeq.getName());
        final ServiceProcess origin = (ServiceProcess) blocked.get(qedeq);
        if (origin != null) {
            if (origin.equals(process)) {
                System.out.println(StringUtility.format(process.getId(), 3) + " unlocked            " + qedeq.getName());
                blocked.remove(qedeq);
            } else {
                System.out.println(StringUtility.format(process.getId(), 3) + " illegal unlock try  " + qedeq.getName());
                throw new IllegalArgumentException("locked by service process " + origin.getId());
            }
        } else {
            System.out.println(StringUtility.format(process.getId(), 3) + " unlock unneccassary " + qedeq.getName());
        }
    }

    private synchronized ServiceProcess getProcess(final KernelQedeqBo qedeq) {
        return (ServiceProcess) blocked.get(qedeq);
    }
    
    private synchronized boolean isBlocked(final KernelQedeqBo qedeq) {
        return blocked.containsKey(qedeq);
    }

    private synchronized void addBlocked(final KernelQedeqBo qedeq, final ServiceProcess process) {
        blocked.put(qedeq, process);
    }

    private synchronized void removeBlocked(final KernelQedeqBo qedeq) {
        blocked.remove(qedeq);
    }

}
