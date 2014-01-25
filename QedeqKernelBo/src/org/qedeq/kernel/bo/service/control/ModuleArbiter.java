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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.QedeqBoSet;
import org.qedeq.kernel.bo.common.ServiceJob;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.KernelQedeqBoSet;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * Get locks for modules.
 * TODO 20130508 m31: Currently we make no difference between read and write locks. We also lock
 * a module during the whole plugin processing for that module. This could be limited to status
 * changes only.
 *
 * @author  Michael Meyling
 */
public class ModuleArbiter {

    /** This class. */
    private static final Class CLASS = ModuleArbiter.class;

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
     * @param   service For this service.
     * @return  The process locked this module newly. Before this call the module was not locked.
     * @throws  InterruptException  Lock acquirement interrupted.
     */
    public boolean lockRequiredModule(final InternalServiceJob process,
            final KernelQedeqBo qedeq, final Service service) throws  InterruptException {
        if (isAlreadyLocked(process, qedeq)) {
            return false;
        }
        process.setBlocked(true);
        // we try to get a lock, if not we wait
        try {
            while (!lock(process, qedeq, service)) {
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
        } finally {
            process.setBlocked(false);
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
    public boolean unlockRequiredModule(final ServiceJob process, final KernelQedeqBo qedeq) {
        return unlock(process, qedeq);

    }

    private synchronized boolean lock(final ServiceJob process, final KernelQedeqBo qedeq, final Service service) {
        final String method = "lock";
        if (Trace.isTraceOn()) {
            Trace.info(CLASS, this, method, getName(process) + " is trying to lock " + qedeq.getName());
        }
        final ServiceJob origin = getProcess(qedeq);
        if (origin != null) {
            if (Trace.isTraceOn()) {
                Trace.info(CLASS, this, method, getName(process) + " failed to lock " + qedeq.getName()
                    + "\tbecause it is locked by " + getName(origin));
            }
            return false;
        }
        addLock(process, qedeq);
        ((DefaultKernelQedeqBo) qedeq).setCurrentlyRunningService(service);
        return true;
    }

    private synchronized boolean unlock(final ServiceJob process, final KernelQedeqBo qedeq) {
        final String method = "unlock";
        if (Trace.isTraceOn()) {
            Trace.info(CLASS, this, method, getName(process) + " is trying to unlock " + qedeq.getName());
        }
        ((DefaultKernelQedeqBo) qedeq).setCurrentlyRunningService(null);
        final ServiceJob origin = getProcess(qedeq);
        if (origin != null) {
            if (origin.equals(process)) {
                removeLock(process, qedeq);
                return true;
            } else {
                RuntimeException e = new IllegalArgumentException(getName(process) + " illegal unlock try for "
                    + qedeq.getName() + " which is currently locked by service process " + getName(origin));
                Trace.fatal(CLASS, this, method, "Programming error. Unallowed unlock try.", e);
                // TODO 20130324 m31: later on we might handle this differently but for now:
                throw e;
            }
        } else {
            if (Trace.isTraceOn()) {
                Trace.info(CLASS, this, method, getName(process) + " unlock unnecessary " + qedeq.getName());
            }
            return false;
        }
    }

    private String getName(final ServiceJob process) {
        return StringUtility.format(process.getId(), 3) + " "
            + (process.getModuleServiceCall() != null ? StringUtility.getLastDotString(
            process.getModuleServiceCall().getService().getServiceId()) : "");
    }

    private synchronized ServiceJob getProcess(final KernelQedeqBo qedeq) {
        return (ServiceJob) blocked.get(qedeq);
    }

    private synchronized boolean isAlreadyLocked(final ServiceJob process,
            final KernelQedeqBo qedeq) {
        return process.equals(blocked.get(qedeq));
    }

    private synchronized void addLock(final ServiceJob process, final KernelQedeqBo qedeq) {
        if (Trace.isTraceOn()) {
            Trace.info(CLASS, this, "addLock", getName(process) + " locked successfuly  " + qedeq.getName());
        }
        blocked.put(qedeq, process);
    }

    private synchronized void removeLock(final ServiceJob process, final KernelQedeqBo qedeq) {
        if (Trace.isTraceOn()) {
            Trace.info(CLASS, this, "removeLock", getName(process) + " unlocked            " + qedeq.getName());
        }
        blocked.remove(qedeq);
    }

    public synchronized QedeqBoSet getBlockedModules(final ServiceJob process) {
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
