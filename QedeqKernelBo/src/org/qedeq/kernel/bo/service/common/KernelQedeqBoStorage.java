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

package org.qedeq.kernel.bo.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.control.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.service.control.ServiceProcessManager;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.state.LoadingState;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * Holds all known QEDEQ modules.
 */
class KernelQedeqBoStorage {

    /** This class. */
    private static final Class CLASS = KernelQedeqBoStorage.class;

    /** QEDEQ Modules; key: ModuleAddress, value: KernelQedeqBo. */
    private final Map bos = new HashMap();


    /**
     * Get {@link QedeqBo} for an module address. If it is unknown it will be created.
     *
     * @param   services    Internal kernel services.
     * @param   address     Module address.
     * @return  QedeqBo for module.
     */
    synchronized DefaultKernelQedeqBo getKernelQedeqBo(final InternalKernelServices services,
            final ModuleAddress address) {
        if (bos.containsKey(address)) {
            return (DefaultKernelQedeqBo) bos.get(address);
        }
        final DefaultKernelQedeqBo prop = new DefaultKernelQedeqBo(services, address);
        bos.put(address, prop);
        return prop;
    }

    /**
     * FIXME 20140123 m31: rework: don't give so many parameters into this method!!!
     *
     * Remove all modules from memory.
     *
     * @throws  InterruptException  User stopped process.
     */
    void lockAndRemoveAllModules(final Service service,
            final ServiceProcessManager processManager, final InternalServiceJob proc) throws InterruptException {
        final String method = "removeAllModules()";
        Trace.begin(CLASS, this, method);
        final List calls = new ArrayList();
        boolean ok = false;
        try {
            // lock all modules
            for (final Iterator iterator
                    = bos.entrySet().iterator();
                    iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iterator.next();
                final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) entry.getValue();
                final InternalModuleServiceCall call = processManager.createServiceCall(service, prop, Parameters.EMPTY,
                        Parameters.EMPTY, proc);
                calls.add(call);
            }

            // delete all modules
            for (final Iterator iterator
                    = bos.entrySet().iterator();
                    iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iterator.next();
                final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) entry.getValue();
                synchronized (prop) {
                    prop.delete();
                }
            }
            bos.clear();
            ok = true;

        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
        } finally {
            final Iterator iterator = calls.iterator();
            while (iterator.hasNext()) {
                if (ok) {
                    ((InternalModuleServiceCall) (iterator.next())).finish();
                } else {
                    ((InternalModuleServiceCall) (iterator.next())).finish("couldn't lock all modules");
                }
            }
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Remove all modules from memory.
     */
    synchronized void removeAllModules() {
        final String method = "removeAllModules()";
        Trace.begin(CLASS, this, method);
        try {
            for (final Iterator iterator
                    = bos.entrySet().iterator();
                    iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iterator.next();
                final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) entry.getValue();
                prop.delete();
            }
            bos.clear();
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Validate module dependencies and throw Error if they are not correct.
     */
    synchronized void validateDependencies() {
        final String method = "validateDependencies";
        String url = StringUtils.EMPTY;
        String text = StringUtils.EMPTY;
        boolean error = false;
        Trace.begin(CLASS, this, method);
// for debugging: print dependency tree:
//        for (final Iterator iterator = bos.entrySet().iterator(); iterator.hasNext(); ) {
//            Map.Entry entry = (Map.Entry) iterator.next();
//            final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) entry.getValue();
//            prop.getStateManager().printDependencyTree();
//        }
        for (final Iterator iterator = bos.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) entry.getValue();
            Trace.param(CLASS, this, method, "prop", prop);
            if (!prop.hasLoadedRequiredModules()) {
                continue;
            }

            // prop must be in dependent list for all required modules
            final KernelModuleReferenceList refs = prop.getKernelRequiredModules();
            for (int i = 0; i < refs.size(); i++) {
                final DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) refs.getKernelQedeqBo(i);
                final KernelModuleReferenceList dependents = ref.getDependentModules();
                if (!dependents.contains(prop)) {
                    Trace.fatal(CLASS, this, method, ref.getUrl() + " missing dependent module: "
                        + prop.getUrl(), null);
                    if (!error) {
                        url = ref.getUrl();
                        text = "missing dependent module " + prop.getUrl();
                    }
                    error = true;
                }
            }

            // for all dependent modules, prop must be in required list
            final KernelModuleReferenceList dependents = prop.getDependentModules();
            for (int i = 0; i < dependents.size(); i++) {
                final DefaultKernelQedeqBo dependent
                    = (DefaultKernelQedeqBo) dependents.getKernelQedeqBo(i);
                final KernelModuleReferenceList refs2 = dependent.getKernelRequiredModules();
                if (!refs2.contains(prop)) {
                    Trace.fatal(CLASS, this, method, dependent.getUrl()
                        + " missing required module: " + prop.getUrl(), null);
                    if (!error) {
                        url = prop.getUrl();
                        text = "missing required module " + prop.getUrl();
                    }
                    error = true;
                }
            }
        }
        Trace.end(CLASS, this, method);

        // if the dependencies are not ok we throw an error!
        if (error) {
            Error e = new Error("QEDEQ dependencies and status are flawed! "
                + "This is a major error! We do a kernel shutdown!");
            Trace.fatal(CLASS, this, method, "Shutdown because of major validation error", e);
            QedeqLog.getInstance().logFailureReply(e.getMessage(), url, text);
            KernelContext.getInstance().shutdown();
            throw e;
        }
    }

    /**
     * Remove a QEDEQ module from memory.
     *
     * @param   prop    Defines the module.
     */
    synchronized void removeModule(final KernelQedeqBo prop) {
        final String method = "removeModule(KernelQedeqBo)";
        Trace.begin(CLASS, this, method);
        try {
            Trace.trace(CLASS, this, method, "removing " +  prop.getUrl());
            bos.remove(prop.getModuleAddress());
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected runtime exception", e);
            throw e;
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Get list of all successfully loaded modules.
     *
     * @return  list of all successfully loaded modules.
     */
    synchronized ModuleAddress[] getAllLoadedModules() {
        final String method = "getAllModules()";
        Trace.begin(CLASS, this, method);
        try {
            final List list = new ArrayList();
            for (final Iterator iterator = bos.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iterator.next();
                final QedeqBo prop = (QedeqBo) entry.getValue();
                if (prop.getLoadingState().getCode() >= LoadingState.STATE_LOADED.getCode()) {
                    list.add(prop.getModuleAddress());
                }
            }
            return (ModuleAddress[]) list.toArray(new ModuleAddress[list.size()]);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

}
