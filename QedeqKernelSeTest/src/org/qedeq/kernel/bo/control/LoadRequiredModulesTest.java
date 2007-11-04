/* $Id: LoadRequiredModulesTest.java,v 1.3 2007/05/10 00:38:08 m31 Exp $
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
package org.qedeq.kernel.bo.control;

import java.io.File;
import java.net.URL;

import org.qedeq.kernel.bo.load.DefaultModuleFactory;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.qedeq.kernel.config.QedeqConfig;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.LogListenerImpl;
import org.qedeq.kernel.log.ModuleEventListenerLog;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.utility.IoUtility;

/**
 * For testing of loading required QEDEQ modules.
 *
 * @version $Revision: 1.19 $
 * @author Michael Meyling
 */
public class LoadRequiredModulesTest extends QedeqTestCase{

    /** Kernel log. */
    private LogListenerImpl log;
    
    /** Module event log. */
    private ModuleEventListenerLog eventLog;

    protected void setUp() throws Exception {
        super.setUp();
        // TODO mime 20071102: check if there are already listeners
        // registered
        log = new LogListenerImpl();
        QedeqLog.getInstance().addLog(log);

        final QedeqConfig config = new QedeqConfig(
            new File(new File("../../../qedeq_gen/test"), "config/org.qedeq.properties"),
            "This file is part of the project *Hilbert II* - http://www.qedeq.org",
            new File("../../../qedeq_gen/test"));
        KernelContext.getInstance().init(new DefaultModuleFactory(KernelContext.getInstance()), 
            config);
        eventLog = new ModuleEventListenerLog();
        ModuleEventLog.getInstance().addLog(eventLog);
        KernelContext.getInstance().startup();
    }

    protected void tearDown() throws Exception {
        KernelContext.getInstance().shutdown();
        ModuleEventLog.getInstance().removeLog(eventLog);
        QedeqLog.getInstance().removeLog(log);
        super.tearDown();
    }

    public LoadRequiredModulesTest() {
        super();
    }
    
    public LoadRequiredModulesTest(final String name) {
        super(name);
    }
    
    /**
     * Load following dependencies:
     * <pre>
     * 011 -> 012
     * 011 -> 013 -> 012
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_01() throws Exception {
        final URL url = IoUtility.toUrl(new File("data/loadRequired/LRM011.xml"));
        KernelContext.getInstance().loadRequiredModules(url.toString());
    }

    /**
     * Load following dependencies:
     * <pre>
     * 021 -> 021
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_02() throws Exception {
        try {
            final URL url = IoUtility.toUrl(new File("data/loadRequired/LRM021.xml"));
            KernelContext.getInstance().loadRequiredModules(url.toString());
            fail("021 -> 021 cycle");
        } catch (XmlFileExceptionList e) {
            assertEquals(1, e.size());
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(0).getSourceArea().getStartPosition().getColumn());
            // TODO mime 20071101: check if exception code and description is ok
        }
    }
    
    /**
     * Load following dependencies:
     * <pre>
     * 031 -> 032 -> 031
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_03() throws Exception {
        try {
            final URL url = IoUtility.toUrl(new File("data/loadRequired/LRM031.xml"));
            KernelContext.getInstance().loadRequiredModules(url.toString());
            fail("031 -> 032 -> 031 cycle");
        } catch (XmlFileExceptionList e) {
            assertEquals(1, e.size());
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(0).getSourceArea().getStartPosition().getColumn());
            // TODO mime 20071101: check if exception code and description is ok
        }
    }
    
    /**
     * Load following dependencies:
     * <pre>
     * 041 -> 042 -> 043
     * 041 -> 043 -> 044
     * 041 -> 044 -> 042
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_04() throws Exception {
        try {
            final URL url = IoUtility.toUrl(new File("data/loadRequired/LRM041.xml"));
            KernelContext.getInstance().loadRequiredModules(url.toString());
            fail("041 -> 042 -> 043 -> 044 -> 042 cycle\n"
               + "041 -> 043 -> 044 -> 042 -> 043 cycle\n"
               + "041 -> 044 -> 042 -> 043 -> 044 cycle");
        } catch (XmlFileExceptionList e) {
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(3, e.size());
            assertEquals(38, e.get(1).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(1).getSourceArea().getStartPosition().getColumn());
            assertEquals(45, e.get(1).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(1).getSourceArea().getStartPosition().getColumn());
            // TODO mime 20071101: check if exception code and description is ok
        }
    }
    
    /**
     * Load following dependencies:
     * <pre>
     * 051 -> 052 -> 053 -> 056
     * 051 -> 052 -> 053 -> 055
     * 051 -> 052 -> 054 -> 056
     * 051 -> 052 -> 055 -> 053 -> 056
     * 051 -> 052 -> 055 -> 057
     * 051 -> 053 -> 056
     * 051 -> 053 -> 055 -> 057
     * 051 -> 054 -> 056   
     * 051 -> 055 -> 057 
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_05() throws Exception {
        final URL url = IoUtility.toUrl(new File("data/loadRequired/LRM011.xml"));
        KernelContext.getInstance().loadRequiredModules(url.toString());
    }

}
