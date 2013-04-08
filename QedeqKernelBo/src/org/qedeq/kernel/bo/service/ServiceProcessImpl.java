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

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.common.PluginCall;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.QedeqBoSet;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.KernelQedeqBoSet;

/**
 * Process info for a kernel service.
 *
 * @author  Michael Meyling
 */
public class ServiceProcessImpl implements InternalServiceProcess {

    /** This class. */
    private static final Class CLASS = ServiceProcessImpl.class;

    /** Counter for each service process. */
    private static long globalCounter;

    /** The plugin call the process currently works for. */
    private PluginCall call;

    /** The thread the service is done within. */
    private final Thread thread;

    /** Start time for process. */
    private long start;

    /** End time for process. */
    private long stop;

    /** State for process. 0 = running, 1 = success, -1 failure. */
    private int state;

    /** Action name. */
    private final String actionName;

    /** Percentage of currently running plugin execution. */
    private double executionPercentage = 0;

    /** Percentage of currently running plugin execution. */
    private String executionActionDescription = "not yet started";

    /** Is this process blocked? */
    private boolean blocked;

    /** We block these QEDEQ modules for other processes. */
    private final KernelQedeqBoSet blockedModules;

    /** Process id. */
    private final long id;

    /**
     * A new service process within the current thread.
     *
     * @param   actionName  Main process purpose.
     */
    public ServiceProcessImpl(final String actionName) {
        this.id = inc();
        this.thread = Thread.currentThread();
        this.call = null;
        this.blockedModules = new KernelQedeqBoSet();
        this.actionName = actionName;
        start();
    }

    private synchronized long inc() {
        return globalCounter++;
    }

    public synchronized void setPluginCall(final PluginCall call) {
        this.call = call;
    }

    public synchronized PluginCall getPluginCall() {
        return call;
    }

    public synchronized Thread getThread() {
        return thread;
    }

    public synchronized QedeqBo getQedeq() {
        if (call != null) {
            return call.getQedeq();
        }
        return null;
    }

    public synchronized String getQedeqName() {
        if (call != null) {
            return call.getQedeq().getName();
        }
        return "";
    }

    public synchronized String getQedeqUrl() {
        if (call != null) {
            return call.getQedeq().getUrl();
        }
        return "";
    }

    public synchronized long getStart() {
        return start;
    }

    public synchronized long getStop() {
        return stop;
    }

    private synchronized void start() {
        start = System.currentTimeMillis();
        executionActionDescription = "started";
    }

    private synchronized void stop() {
        stop = System.currentTimeMillis();
    }

    public synchronized void setSuccessState() {
        if (isRunning()) {
            state = 1;
            stop();
            executionActionDescription = "finished";
            executionPercentage = 100;
        }
    }

    public synchronized void setFailureState() {
        if (isRunning()) {
            state = -1;
            stop();
        }
    }

    public synchronized boolean isRunning() {
        if (state == 0) {
            if (!thread.isAlive()) {
                Trace.fatal(CLASS, this, "isRunning()", "Thread has unexpectly died",
                    new RuntimeException());
                setFailureState();
                return false;
            }
            return true;
        }
        return false;
    }

    public synchronized boolean isBlocked() {
        if (isRunning()) {
            return blocked;
        }
        return false;
    }

    public synchronized void setBlocked(final boolean blocked) {
        this.blocked = blocked;
    }

    public synchronized boolean wasSuccess() {
        return state == 1;
    }

    public synchronized boolean wasFailure() {
        return state == -1;
    }


    public synchronized void interrupt() {
        thread.interrupt();
    }

    public synchronized double getExecutionPercentage() {
        if (isRunning() || isBlocked()) {
            if (call != null) {
                executionPercentage = call.getExecutionPercentage();
            }
        }
        return executionPercentage;
    }

    public synchronized String getActionName() {
        return actionName;
    }

    public synchronized String getExecutionActionDescription() {
        if (isRunning() || isBlocked()) {
            if (call != null) {
                executionActionDescription = call.getExecutionActionDescription();
            }
        }
        return executionActionDescription;
    }

    public synchronized QedeqBoSet getBlockedModules() {
        return new KernelQedeqBoSet(blockedModules);
    }

    public synchronized void addBlockedModules(final KernelQedeqBoSet set) {
        blockedModules.add(set);
    }

    public synchronized void addBlockedModule(final KernelQedeqBo element) {
        blockedModules.add(element);
    }

    public synchronized void removeBlockedModules(final KernelQedeqBoSet set) {
        blockedModules.remove(set);
    }

    public synchronized void removeBlockedModule(final KernelQedeqBo element) {
        blockedModules.remove(element);
    }

    public long getId() {
        return id;
    }

    public int hashCode() {
        return (int) id;
    }

    public boolean equals(final Object obj) {
        return 0 == compareTo(obj);
    }

    public int compareTo(final Object o) {
        if (!(o instanceof ServiceProcess)) {
            return -1;
        }
        final ServiceProcess s = (ServiceProcess) o;
        return (getId() < s.getId() ? -1 : (getId() == s.getId() ? 0 : 1));
    }


}
