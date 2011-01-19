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
package org.qedeq.kernel.bo.service;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.common.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * For testing of loading required QEDEQ modules.
 *
 * @author  Michael Meyling
 */
public class LoadRequiredModulesTest extends QedeqTestCase {

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
        final ModuleAddress address = new DefaultModuleAddress(getFile("loadRequired/LRM011.xml"));
        if (!KernelContext.getInstance().loadRequiredModules(address)) {
            fail("loading should be successful");
        }
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
        final ModuleAddress address = new DefaultModuleAddress(
            getFile("loadRequired/LRM021.xml"));
        if (KernelContext.getInstance().loadRequiredModules(address)) {
            fail("021 -> 021 cycle");
        } else {
            SourceFileExceptionList e = KernelContext.getInstance().getQedeqBo(address).getErrors();
            assertEquals(1, e.size());
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(90722, e.get(0).getErrorCode());
            assertTrue(e.get(0).getDescription().endsWith(
                "Recursive import of modules is forbidden, label: \"LRM021\""));
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
        final ModuleAddress address = new DefaultModuleAddress(
            getFile("loadRequired/LRM031.xml"));
        KernelContext.getInstance().loadRequiredModules(address);
        if (KernelContext.getInstance().loadRequiredModules(address)) {
            fail("031 -> 032 -> 031 cycle");
        } else {
            SourceFileExceptionList e = KernelContext.getInstance().getQedeqBo(address).getErrors();
            assertEquals(1, e.size());
            // e.printStackTrace();
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(0).getErrorCode());
            assertTrue(e.get(0).getDescription().endsWith(
                "Import of module failed, label: \"LRM032\", Recursive import of modules is"
                + " forbidden, label: \"LRM031\""));
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
        final ModuleAddress address = new DefaultModuleAddress(
            getFile("loadRequired/LRM041.xml"));
        KernelContext.getInstance().loadRequiredModules(address);
        if (KernelContext.getInstance().loadRequiredModules(address)) {
            fail("041 -> 042 -> 043 -> 044 -> 042 cycle\n"
               + "041 -> 043 -> 044 -> 042 -> 043 cycle\n"
               + "041 -> 044 -> 042 -> 043 -> 044 cycle");
        } else {
            SourceFileExceptionList e = KernelContext.getInstance().getQedeqBo(address).getErrors();
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(0).getErrorCode());
            assertTrue(e.get(0).getDescription().endsWith(
                "Import of module failed, label: \"LRM042\", Import of module failed, label: "
                + "\"LRM043\", Import of module failed, label: \"LRM044\", Recursive import of "
                + "modules is forbidden, label: \"LRM042\""));
            assertEquals(3, e.size());
            assertEquals(38, e.get(1).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(1).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(1).getErrorCode());
            assertTrue(e.get(1).getDescription().endsWith(
                "Import of module failed, label: \"LRM043\", Import of module failed, label: "
                + "\"LRM044\", Import of module failed, label: \"LRM042\", Recursive import of "
                + "modules is forbidden, label: \"LRM043\""));
            assertEquals(45, e.get(2).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(2).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(2).getErrorCode());
            assertTrue(e.get(2).getDescription().endsWith(
                "Import of module failed, label: \"LRM044\", Import of module failed, label: "
                + "\"LRM042\", Import of module failed, label: \"LRM043\", Recursive import of "
                + "modules is forbidden, label: \"LRM044\""));
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
        final ModuleAddress address = new DefaultModuleAddress(
            getFile("loadRequired/LRM051.xml"));
        if (!KernelContext.getInstance().loadRequiredModules(address)) {
            fail("loading should be successful");
        }
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
        final ModuleAddress address = new DefaultModuleAddress(getFile("loadRequired/LRM061.xml"));
        if (!KernelContext.getInstance().loadRequiredModules(address)) {
            fail("loading should be successful");
        }
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
        final ModuleAddress address = new DefaultModuleAddress(
            getFile("loadRequired/LRM071.xml"));
        if (KernelContext.getInstance().loadRequiredModules(address)) {
            fail("see test method description");
        } else {
            SourceFileExceptionList e = KernelContext.getInstance().getQedeqBo(address).getErrors();
            // e.printStackTrace(System.out);
            System.out.println(e);
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(0).getErrorCode());
            assertTrue(e.get(0).getDescription().endsWith(
                "Import of module failed, label: \"LRM072\", Import of module failed, label: "
                + "\"LRM073\", Import of module failed, label: \"LRM076\", Recursive import of "
                + "modules is forbidden, label: \"LRM071\""));
            assertEquals(4, e.size());
            assertEquals(38, e.get(1).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(1).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(1).getErrorCode());
            assertTrue(e.get(1).getDescription().endsWith(
                "Import of module failed, label: \"LRM073\", Import of module failed, label: "
                + "\"LRM076\", Recursive import of "
                + "modules is forbidden, label: \"LRM071\""));
            assertEquals(45, e.get(2).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(2).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(2).getErrorCode());
            assertTrue(e.get(2).getDescription().endsWith(
                "Import of module failed, label: \"LRM074\", Import of module failed, label: "
                + "\"LRM076\", Recursive import of "
                + "modules is forbidden, label: \"LRM071\""));
            assertEquals(52, e.get(3).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(3).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(3).getErrorCode());
            assertTrue(e.get(3).getDescription().endsWith(
                "Import of module failed, label: \"LRM075\", Import of module failed, label: "
                + "\"LRM073\", Import of module failed, label: \"LRM076\", Recursive import of "
                + "modules is forbidden, label: \"LRM071\""));
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
        final ModuleAddress address = new DefaultModuleAddress(getFile("loadRequired/LRM081.xml"));
        if (!KernelContext.getInstance().loadRequiredModules(address)) {
            fail("loading should be successful");
        }
        assertEquals(0, KernelContext.getInstance().getQedeqBo(address).getErrors().size());
        assertTrue(!KernelContext.getInstance().getQedeqBo(address).hasBasicFailures());
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
        final ModuleAddress address = new DefaultModuleAddress(
            getFile("loadRequired/LRM091.xml"));
        if (KernelContext.getInstance().loadRequiredModules(address)) {
            fail("091 -> 092 -> 093 -> 094 -> 095 -> 096 -> 097 -> 098 -> 099 -> 091 cycle\n");
        } else {
            SourceFileExceptionList e = KernelContext.getInstance().getQedeqBo(address).getErrors();
            assertEquals(31, e.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(90723, e.get(0).getErrorCode());
            assertTrue(e.get(0).getDescription().endsWith(
                "Import of module failed, label: \"LRM092\", Import of module failed, label: "
                + "\"LRM093\", Import of module failed, label: \"LRM094\", Import of module "
                + "failed, label: \"LRM095\", Import of module failed, label: \"LRM096\", Import "
                + "of module failed, label: \"LRM097\", Import of module failed, label: \"LRM098\", "
                + "Import of module failed, label: \"LRM099\", Recursive import of modules is "
                + "forbidden, label: \"LRM091\""));
            assertEquals(1, e.size());
        }
    }

    /**
     * Load following dependencies:
     * <pre>
     * 101 -> 102
     * 101 -> notThere
     * 101 -> 103
     * 101 -> alsoNotThere
     * 101 -> 104
     * </pre>
     *
     * @throws Exception
     */
    public void testLoadRequiredModules_10() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(
                getFile("loadRequired/LRM101.xml"));
        if (KernelContext.getInstance().loadRequiredModules(address)) {
            fail("two imports don't exist: \"notThere\" and \"alsoNotThere\"\n");
        } else {
            SourceFileExceptionList e = KernelContext.getInstance().getQedeqBo(address).getErrors();
            assertEquals(38, e.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.get(0).getSourceArea().getStartPosition().getColumn());
            assertEquals(90710, e.get(0).getErrorCode());
            assertTrue(0 <= e.get(0).getDescription().indexOf(
                "import of module with label \"notThere\" failed: Loading module from local file "
                + "failed.; file not readable: "));
            assertEquals(2, e.size());
            assertEquals(90710, e.get(1).getErrorCode());
        }
    }

}
