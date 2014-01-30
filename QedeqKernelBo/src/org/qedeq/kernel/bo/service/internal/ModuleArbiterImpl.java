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

package org.qedeq.kernel.bo.service.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.QedeqBoSet;
import org.qedeq.kernel.bo.common.ServiceJob;
import org.qedeq.kernel.bo.job.InternalModuleServiceCallImpl;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.ModuleArbiter;
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
public class ModuleArbiterImpl implements ModuleArbiter {

    /** This class. */
    private static final Class CLASS = ModuleArbiterImpl.class;

    /** Map blocked QEDEQ modules to service processes. */
    private final Map blocked;

    /**
     * Constructor.
     */
    public ModuleArbiterImpl() {
        blocked = new HashMap();
    }

    public boolean lockRequiredModule(final InternalModuleServiceCall call) throws  InterruptException {
        call.pause();
        call.getInternalServiceProcess().setBlocked(true);
        try {
            final boolean result = lockRequiredModule(call.getInternalServiceProcess(), call.getQedeq(),
                call.getService());
            ((InternalModuleServiceCallImpl) call).setNewlyBlockedModule(result);
            return result;
        } finally {
            call.getInternalServiceProcess().setBlocked(false);
            call.resume();
        }
    }

    private boolean lockRequiredModule(final InternalServiceJob process,
            final QedeqBo qedeq, final Service service) throws  InterruptException {
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
                        monitor.wait(1000);
                    } catch (InterruptedException e) {
                        process.setInterruptedState();
                        throw new InterruptException(qedeq.getModuleAddress().createModuleContext());
                    }
                }
                if (Thread.interrupted()) {
                    process.setInterruptedState();
                        throw new InterruptException(qedeq.getModuleAddress().createModuleContext());
                }
            }
        } finally {
            process.setBlocked(false);
        }
        return true;
    }

    public boolean unlockRequiredModule(final InternalModuleServiceCall call) {
        // TODO 20130521 m31: do it without cast
        if (call != null && ((InternalModuleServiceCallImpl) call).getNewlyBlockedModule()) {
            return unlockRequiredModule(call.getInternalServiceProcess(), call.getQedeq());
        }
        return false;
    }

    public boolean unlockRequiredModule(final ServiceJob process, final QedeqBo qedeq) {
        return unlock(process, qedeq);

    }

    private synchronized boolean lock(final ServiceJob process, final QedeqBo qedeq, final Service service) {
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

    private synchronized boolean unlock(final ServiceJob process, final QedeqBo qedeq) {
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

    private synchronized ServiceJob getProcess(final QedeqBo qedeq) {
        return (ServiceJob) blocked.get(qedeq);
    }

    private synchronized boolean isAlreadyLocked(final ServiceJob process,
            final QedeqBo qedeq) {
        return process.equals(blocked.get(qedeq));
    }

    private synchronized void addLock(final ServiceJob process, final QedeqBo qedeq) {
        if (Trace.isTraceOn()) {
            Trace.info(CLASS, this, "addLock", getName(process) + " locked successfuly  " + qedeq.getName());
        }
        blocked.put(qedeq, process);
    }

    private synchronized void removeLock(final ServiceJob process, final QedeqBo qedeq) {
        if (Trace.isTraceOn()) {
            Trace.info(CLASS, this, "removeLock", getName(process) + " unlocked            " + qedeq.getName());
        }
        blocked.remove(qedeq);
    }

    public synchronized QedeqBoSet getBlockedModules(final ServiceJob process) {
        QedeqBoSet result = new QedeqBoSet();
        final Iterator i = blocked.entrySet().iterator();
        while (i.hasNext()) {
            final Map.Entry entry = (Map.Entry) i.next();
            QedeqBo qedeq = (QedeqBo) entry.getKey();
            if (process.equals(entry.getValue())) {
                result.add(qedeq);
            }
        }
        return result;
    }

}
