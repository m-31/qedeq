/* $Id: KernelFacade.java,v 1.2 2007/12/21 23:35:18 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.rel.test.text;

import java.io.File;
import java.io.IOException;

import org.qedeq.kernel.bo.load.DefaultModuleFactory;
import org.qedeq.kernel.config.QedeqConfig;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.LogListener;
import org.qedeq.kernel.log.LogListenerImpl;
import org.qedeq.kernel.log.ModuleEventListener;
import org.qedeq.kernel.log.ModuleEventListenerLog;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.log.QedeqLog;


/**
 * FIXME mime 20071102: this class should <b>not</b> be used in test classes: it
 * registers listeners and never shuts down the kernel. Refactoring!!! 
 * 
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
        QedeqLog.getInstance().addLog(log);
        try {
            final QedeqConfig config = new QedeqConfig(
                new File(new File("../../../qedeq_gen/test"), "config/org.qedeq.properties"),
                "This file is part of the project *Hilbert II* - http://www.qedeq.org",
                new File("../../../qedeq_gen/test"));
            KernelContext.getInstance().init(new DefaultModuleFactory(KernelContext.getInstance()), 
                config);
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
