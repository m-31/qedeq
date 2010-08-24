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
package org.qedeq.kernel.bo.service.latex;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.common.DefaultModuleAddress;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleAddress;

/**
 * For test of generating LaTeX output.
 *
 * @author Michael Meyling
 */
public class Qedeq2LatexTest extends QedeqTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        KernelFacade.startup();
    }

    protected void tearDown() throws Exception {
        KernelFacade.shutdown();
        super.tearDown();
    }

    public Qedeq2LatexTest() {
        super();
    }

    public Qedeq2LatexTest(final String name) {
        super(name);
    }

    /**
     * Check that there are no LaTeX warnings in Q2L001.
     *
     * @throws  Exception
     */
    public void testQ2L001_v1() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("latex/Q2L001.xml"));
        final QedeqBo bo = KernelContext.getInstance().getQedeqBo(address);
        KernelContext.getInstance().executePlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin", address, null);
        assertFalse(bo.getLoadingState().isFailure());
        assertEquals(LogicalState.STATE_UNCHECKED, bo.getLogicalState());
        assertNull(bo.getErrors());
        assertNull(bo.getWarnings());
    }

    /**
     * Check that there are no LaTeX warnings in Q2L001.
     * After checking the module with errors.
     *
     * @throws  Exception
     */
    public void testQ2L001_v2() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("latex/Q2L001.xml"));
        KernelContext.getInstance().checkModule(address);
        final QedeqBo bo = KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.getLogicalState().isFailure());
        assertNull(bo.getWarnings());
        assertEquals(1, bo.getErrors().size());
        assertEquals(11231, bo.getErrors().get(0).getErrorCode());
        KernelContext.getInstance().executePlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin", address, null);
        assertTrue(bo.getLogicalState().isFailure());
        assertNull(bo.getWarnings());
        assertEquals(1, bo.getErrors().size());
        assertEquals(11231, bo.getErrors().get(0).getErrorCode());
        
    }

    /**
     * Check that there are no LaTeX Errors in Q2L001.
     *
     * @throws  Exception
     */
    public void testQ2L002() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("latex/Q2L002.xml"));
        KernelContext.getInstance().checkModule(address);
        final QedeqBo bo = KernelContext.getInstance().getQedeqBo(address);
        assertFalse(bo.getLogicalState().isFailure());
        assertNull(bo.getWarnings());
        assertNull(bo.getErrors());
        KernelContext.getInstance().executePlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin", address, null);
        assertFalse(bo.getLogicalState().isFailure());
        assertNull(bo.getErrors());
        assertEquals(1, bo.getWarnings().size());
        assertEquals(80010, bo.getWarnings().get(0).getErrorCode());
        
    }


}
