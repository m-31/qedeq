/* $Id: LoadRequiredModulesTest.java,v 1.2 2008/01/26 12:39:50 m31 Exp $
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
package org.qedeq.kernel.bo.control;

import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.test.KernelFacade;
import org.qedeq.kernel.test.QedeqTestCase;

/**
 * For testing of loading required QEDEQ modules.
 *
 * @version $Revision: 1.2 $
 * @author Michael Meyling
 */
public class LoadRequiredModulesTest extends QedeqTestCase{

    protected void setUp() throws Exception {
        super.setUp();
        KernelFacade.startup();
    }

    protected void tearDown() throws Exception {
        KernelFacade.shutdown();
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
        final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM011.xml");
        KernelContext.getInstance().loadRequiredModules(address);
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
            final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM021.xml");
            KernelContext.getInstance().loadRequiredModules(address);
            fail("021 -> 021 cycle");
        } catch (SourceFileExceptionList e) {
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
            final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM031.xml");
            KernelContext.getInstance().loadRequiredModules(address);
            fail("031 -> 032 -> 031 cycle");
        } catch (SourceFileExceptionList e) {
            assertEquals(1, e.size());
            e.printStackTrace();
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
            final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM041.xml");
            KernelContext.getInstance().loadRequiredModules(address);
            fail("041 -> 042 -> 043 -> 044 -> 042 cycle\n"
               + "041 -> 043 -> 044 -> 042 -> 043 cycle\n"
               + "041 -> 044 -> 042 -> 043 -> 044 cycle");
        } catch (SourceFileExceptionList e) {
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(3, e.size());
            assertEquals(38, e.get(1).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(1).getSourceArea().getStartPosition().getColumn());
            assertEquals(45, e.get(2).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(2).getSourceArea().getStartPosition().getColumn());
            // TODO mime 20071101: check if exception code and description is ok
        }
    }
    
    /**
     * Load following dependencies:
     * <pre>
     * 051 -> 052 -> 053 -> 056
     * 051 -> 052 -> 054 -> 056
     * 051 -> 052 -> 055 -> 053 -> 056
     * 051 -> 052 -> 055 -> 057
     * 051 -> 053 -> 056
     * 051 -> 054 -> 056   
     * 051 -> 055 -> 057 
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_05() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM051.xml");
        KernelContext.getInstance().loadRequiredModules(address);
    }

    /**
     * Load following dependencies:
     * <pre>
     * 061 -> 062 -> 063 -> 066 -> 067
     * 061 -> 062 -> 064 -> 066 -> 067
     * 061 -> 062 -> 065 -> 063 -> 066 -> 067
     * 061 -> 062 -> 065 -> 067
     * 061 -> 063 -> 066 -> 067
     * 061 -> 064 -> 066 -> 067  
     * 061 -> 065 -> 067 
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_06() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM061.xml");
        KernelContext.getInstance().loadRequiredModules(address);
    }

    /**
     * Load following dependencies:
     * <pre>
     * 071 -> 072 -> 073 -> 076 -> 077
     * 071 -> 072 -> 073 -> 076 -> 071             c
     * 071 -> 072 -> 073 -> 075 -> 073             c
     * 071 -> 072 -> 073 -> 075 -> 077
     * 071 -> 072 -> 074 -> 076 -> 077
     * 071 -> 072 -> 074 -> 076 -> 071             c
     * 071 -> 072 -> 075 -> 073 -> 076 -> 077
     * 071 -> 072 -> 075 -> 073 -> 075             c
     * 071 -> 072 -> 075 -> 077
     * 071 -> 073 -> 076 -> 077
     * 071 -> 073 -> 076 -> 071                    c
     * 071 -> 073 -> 075 -> 073                    c
     * 071 -> 074 -> 076 -> 077  
     * 071 -> 074 -> 076 -> 071                    c
     * 071 -> 075 -> 073 -> 076 -> 077
     * 071 -> 075 -> 073 -> 076 -> 071             c
     * 071 -> 075 -> 073 -> 075 - >073             c
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_07() throws Exception {
        try {
            final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM071.xml");
            KernelContext.getInstance().loadRequiredModules(address);
            fail("see test method description");
        } catch (SourceFileExceptionList e) {
            e.printStackTrace(System.out);
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(4, e.size());
            assertEquals(38, e.get(1).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(1).getSourceArea().getStartPosition().getColumn());
            assertEquals(45, e.get(2).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(2).getSourceArea().getStartPosition().getColumn());
            assertEquals(52, e.get(3).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(3).getSourceArea().getStartPosition().getColumn());
            // TODO mime 20071105: check if exception code and description is ok
        }
    }

    /**
     * Load following dependencies:
     * <pre>
     * 081 -> 082 -> 083 -> 084 -> 085 -> 086 -> 087 -> 089
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_08() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM081.xml");
        KernelContext.getInstance().loadRequiredModules(address);
    }

    /**
     * Load following dependencies:
     * <pre>
     * 091 -> 092 -> 093 -> 094 -> 095 -> 096 -> 097 -> 099 -> 091
     * </pre>
     * 
     * @throws Exception
     */
    public void testLoadRequiredModules_09() throws Exception {
        try {
            final ModuleAddress address = new DefaultModuleAddress("data/loadRequired/LRM091.xml");
            KernelContext.getInstance().loadRequiredModules(address);
            fail("091 -> 092 -> 093 -> 094 -> 095 -> 096 -> 097 -> 098 -> 099 -> 091 cycle\n");
        } catch (SourceFileExceptionList e) {
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(7, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(1, e.size());
            // TODO mime 20071105: check if exception code and description is ok
        }
    }

}
