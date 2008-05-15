/* $Id: KernelFacade.java,v 1.2 2008/05/15 21:27:30 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.test;

import java.io.File;
import java.io.IOException;

import org.qedeq.kernel.bo.control.DefaultInternalKernelServices;
import org.qedeq.kernel.bo.control.InternalKernelServices;
import org.qedeq.kernel.bo.control.ModuleLoader;
import org.qedeq.kernel.config.QedeqConfig;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.LogListener;
import org.qedeq.kernel.log.LogListenerImpl;
import org.qedeq.kernel.log.ModuleEventListener;
import org.qedeq.kernel.log.ModuleEventListenerLog;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.xml.loader.XmlModuleLoader;


/**
 * This class provides static access methods for basic informations.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class KernelFacade {
    
    private static KernelContext context;
    private static LogListener log;
    private static ModuleEventListener mod;

    public static void startup() {
        log = new LogListenerImpl();
        // QedeqLog.getInstance().addLog(log);
        try {
            final QedeqConfig config = new QedeqConfig(
                new File(new File("../../../qedeq_gen/test"), "config/org.qedeq.properties"),
                "This file is part of the project *Hilbert II* - http://www.qedeq.org",
                new File("../../../qedeq_gen/test"));
            config.setAutoReloadLastSessionChecked(false);
            final ModuleLoader loader = new XmlModuleLoader();
            final InternalKernelServices services = new DefaultInternalKernelServices(
                KernelContext.getInstance(), loader);
            KernelContext.getInstance().init(services , config);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        mod = new ModuleEventListenerLog();
        ModuleEventLog.getInstance().addLog(mod);
        KernelContext.getInstance().startup();
        context = KernelContext.getInstance();
    }
    
    public static void shutdown() {
        KernelContext.getInstance().shutdown();
        QedeqLog.getInstance().removeLog(log);
        ModuleEventLog.getInstance().removeLog(mod);
        context = null;
        log = null;
        mod = null;
    }
    
    
    public static KernelContext getKernelContext() {
        return context;
    }

}
