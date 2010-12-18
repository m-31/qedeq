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

package org.qedeq.kernel.bo.test;

import java.io.File;
import java.io.IOException;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.DefaultInternalKernelServices;
import org.qedeq.kernel.config.QedeqConfig;
import org.qedeq.kernel.xml.dao.XmlQedeqFileDao;


/**
 * This class provides static access methods for basic informations.
 *
 * @author  Michael Meyling
 */
public final class KernelFacade {

    private static KernelContext context;
//    private static LogListener log;
//    private static ModuleEventListener mod;

    public static void startup() {
//        log = new LogListenerImpl();
//        QedeqLog.getInstance().addLog(log);
        try {
            final File dir = (new QedeqTestCase() {}).getOutdir();
            final File cf = new File(dir, "config/org.qedeq.properties");
            cf.getParentFile().mkdirs();
            cf.createNewFile();
            final QedeqConfig config = new QedeqConfig(
                cf,
                "This file is part of the project *Hilbert II* - http://www.qedeq.org",
                dir);
            config.setAutoReloadLastSessionChecked(false);
            final QedeqFileDao loader = new XmlQedeqFileDao();
            final DefaultInternalKernelServices services = new DefaultInternalKernelServices(
                KernelContext.getInstance(), loader);
            services.addPlugin("org.qedeq.kernel.bo.service.heuristic.HeuristicCheckerPlugin");

            KernelContext.getInstance().init(services , config);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        mod = new ModuleEventListenerLog();
//        ModuleEventLog.getInstance().addLog(mod);
        KernelContext.getInstance().startupServices();
        context = KernelContext.getInstance();
    }

    public static void shutdown() {
        KernelContext.getInstance().shutdown();
//        QedeqLog.getInstance().removeLog(log);
//        ModuleEventLog.getInstance().removeLog(mod);
        context = null;
//        log = null;
//        mod = null;
    }

    public static KernelContext getKernelContext() {
        return context;
    }

}
