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
import java.util.Iterator;
import java.util.Map;

import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.QedeqBoSet;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.KernelQedeqBoSet;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * Get locks for modules.
 * FIXME 20130508 m31: Currently we make no difference between read and write locks. We also lock
 * a module during the whole plugin processing for that module. This could be limited to status
 * changes only.
 *
 * @author  Michael Meyling
 */
public class ModuleArbiter {

    /** Map blocked QEDEQ modules to service processes. */
    private final Map blocked;

    /**
     * Constructor.
     */
    public ModuleArbiter() {
        blocked = new HashMap();
    }

    /**
     * Lock QEDEQ module for exclusive read and write access.
     *
     * @param   process This process acquires the lock.
     * @param   qedeq   Lock this module.
     * @return  The process locked this module newly. Before this call the module was not locked.
     * @throws  InterruptException  Lock acquirement interrupted.
     */
    public boolean lockRequiredModule(final InternalServiceProcess process,
            final KernelQedeqBo qedeq) throws  InterruptException {
        if (isAlreadyLocked(process, qedeq)) {
            return false;
        }
        // we try to get a lock, if not we wait
        while (!lock(process, qedeq)) {
            final Object monitor = new Object();
            synchronized (monitor) {
                try {
                    monitor.wait(10000);
                } catch (InterruptedException e) {
                    process.setFailureState();
                    throw new InterruptException(qedeq.getModuleAddress().createModuleContext());
                }
            }
            if (Thread.interrupted()) {
                process.setFailureState();
                    throw new InterruptException(qedeq.getModuleAddress().createModuleContext());
            }
        }
        return true;
    }

    /**
     * Unlock module again.
     *
     * @param   process     This process must have acquired the lock.
     * @param   qedeq       This module was locked before.
     * @return  Was this module even locked?
     */
    public boolean unlockRequiredModule(final ServiceProcess process, final KernelQedeqBo qedeq) {
        return unlock(process, qedeq);
    }

    private synchronized boolean unlock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        // FIXME 20130508 m31: remove System.out
        System.out.println(getName(process) + " is trying to unlock " + qedeq.getName());
        final ServiceProcess origin = getProcess(qedeq);
        if (origin != null) {
            if (origin.equals(process)) {
                removeLock(process, qedeq);
                return true;
            } else {
                System.out.println(getName(process) + " illegal unlock try  " + qedeq.getName());
                // FIXME 20130324 m31: later on we might handle this differently but for now:
                throw new IllegalArgumentException("locked by service process " + origin.getId());
            }
        } else {
            System.out.println(getName(process) + " unlock unneccassary " + qedeq.getName());
            return false;
        }
    }

    private synchronized boolean lock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        // FIXME 20130508 m31: remove System.out
        System.out.println(getName(process) + " is trying to lock   " + qedeq.getName());
        final ServiceProcess origin = getProcess(qedeq);
        if (origin != null) {
            System.out.println(getName(process) + " failed to lock      " + qedeq.getName());
            System.out.println("\tbecause it is locked by " + getName(origin));
            return false;
        }
        addLock(process, qedeq);
        return true;
    }

    private String getName(final ServiceProcess process) {
        return StringUtility.format(process.getId(), 3) + " "
            + (process.getServiceCall() != null ? StringUtility.getLastDotString(
            process.getServiceCall().getService().getServiceId()) : "");
    }

    private synchronized ServiceProcess getProcess(final KernelQedeqBo qedeq) {
        return (ServiceProcess) blocked.get(qedeq);
    }

    private synchronized boolean isAlreadyLocked(final ServiceProcess process,
            final KernelQedeqBo qedeq) {
        return process.equals(blocked.get(qedeq));
    }

    private synchronized void addLock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        System.out.println(getName(process) + " locked successfuly  " + qedeq.getName());
        blocked.put(qedeq, process);
    }

    private synchronized void removeLock(final ServiceProcess process, final KernelQedeqBo qedeq) {
        System.out.println(getName(process) + " unlocked            " + qedeq.getName());
        blocked.remove(qedeq);
    }

    public synchronized QedeqBoSet getBlockedModules(final ServiceProcess process) {
        KernelQedeqBoSet result = new KernelQedeqBoSet();
        final Iterator i = blocked.entrySet().iterator();
        while (i.hasNext()) {
            final Map.Entry entry = (Map.Entry) i.next();
            KernelQedeqBo qedeq = (KernelQedeqBo) entry.getKey();
            if (process.equals(entry.getValue())) {
                result.add(qedeq);
            }
        }
        return result;
    }

}
