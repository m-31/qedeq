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

    /**
     * Lock QEDEQ module.
     * 
     * @param   process This process aquires the lock.
     * @param   qedeq   Lock this module.
     * @return  The process locked this module already, we didn't do anything.
     */
    public boolean lockRequiredModule(final ServiceProcess process, final KernelQedeqBo qedeq) {
        if (isAlreadyLocked(process, qedeq)) {
            return false;
        }
        while (!lock(process, qedeq)) {
            IoUtility.sleep(10000);
            if (Thread.interrupted()) {
                process.setFailureState();
                break;
            }
        }
        return true;
    }

    private synchronized boolean lock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        System.out.println(getName(process) + " is trying to lock   " + qedeq.getName());
        final ServiceProcess origin = (ServiceProcess) blocked.get(qedeq);
        if (origin != null) {
            System.out.println(getName(process) + " failed to lock      " + qedeq.getName());
            return false;
        }
        System.out.println(getName(process) + " locked successfuly  " + qedeq.getName());
        blocked.put(qedeq, process);
        return true;
    }

    private String getName(final ServiceProcess process) {
        return StringUtility.format(process.getId(), 3)
            + (process.getPluginCall() != null ? StringUtility.getLastDotString(
            process.getPluginCall().getPlugin().getPluginId()) : "");
    }

    public void unlockRequiredModule(final ServiceProcess process, final KernelQedeqBo qedeq) {
        unlock(process, qedeq);
    }

    private synchronized void unlock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        System.out.println(getName(process) + " is trying to unlock " + qedeq.getName());
        final ServiceProcess origin = (ServiceProcess) blocked.get(qedeq);
        if (origin != null) {
            if (origin.equals(process)) {
                System.out.println(getName(process) + " unlocked            " + qedeq.getName());
                blocked.remove(qedeq);
            } else {
                System.out.println(getName(process) + " illegal unlock try  " + qedeq.getName());
                throw new IllegalArgumentException("locked by service process " + origin.getId());
            }
        } else {
            System.out.println(getName(process) + " unlock unneccassary " + qedeq.getName());
        }
    }

    private synchronized ServiceProcess getProcess(final KernelQedeqBo qedeq) {
        return (ServiceProcess) blocked.get(qedeq);
    }
    
    private synchronized boolean isLocked(final KernelQedeqBo qedeq) {
        return blocked.containsKey(qedeq);
    }

    private synchronized boolean isAlreadyLocked(final ServiceProcess process,
            final KernelQedeqBo qedeq) {
        return process.equals(blocked.get(qedeq));
    }

    private synchronized void addLock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        blocked.put(qedeq, process);
    }

    private synchronized void removeLock(final KernelQedeqBo qedeq) {
        blocked.remove(qedeq);
    }

}
