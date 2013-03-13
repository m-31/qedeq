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
package org.qedeq.kernel.bo.service.latex;

import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.state.WellFormedState;

/**
 * For test of generating LaTeX output.
 *
 * @author Michael Meyling
 */
public class Qedeq2LatexTest extends QedeqBoTestCase {

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
        final QedeqBo bo = getServices().getQedeqBo(address);
        getServices().executePlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin", address,
            null, null);
        assertFalse(bo.getLoadingState().isFailure());
        assertFalse(WellFormedState.STATE_UNCHECKED.equals(bo.getWellFormedState()));
        assertTrue(bo.hasErrors());
        assertNotNull(bo.getErrors());
        assertEquals(1, bo.getErrors().size());
        assertFalse(bo.hasWarnings());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
    }

    /**
     * Check that there are no LaTeX warnings in Q2L001.
     * After checking the module with errors.
     *
     * @throws  Exception
     */
    public void testQ2L001_v2() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("latex/Q2L001.xml"));
        getServices().checkWellFormedness(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.getWellFormedState().isFailure());
        assertFalse(bo.hasWarnings());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(1, bo.getErrors().size());
        assertEquals(11231, bo.getErrors().get(0).getErrorCode());
        getServices().executePlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin", address,
            null, null);
        assertTrue(bo.getWellFormedState().isFailure());
        assertFalse(bo.hasWarnings());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(1, bo.getErrors().size());
        assertEquals(11231, bo.getErrors().get(0).getErrorCode());
        
    }

    /**
     * Check that there is one LaTeX warning in Q2L001.
     *
     * @throws  Exception
     */
    public void testQ2L002() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("latex/Q2L002.xml"));
        getServices().checkWellFormedness(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertFalse(bo.getWellFormedState().isFailure());
        assertFalse(bo.hasWarnings());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertFalse(bo.hasErrors());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        getServices().executePlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin", address,
            null, null);
        assertFalse(bo.getWellFormedState().isFailure());
        assertFalse(bo.hasErrors());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
//        System.out.println(bo.getWarnings());
        assertEquals(1, bo.getWarnings().size());
        assertEquals(80007, bo.getWarnings().get(0).getErrorCode());
        
    }

    /**
     * Check that there is one LaTeX warning in Q2L003.
     *
     * @throws  Exception
     */
    public void testQ2L003() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("latex/Q2L003.xml"));
        getServices().checkWellFormedness(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertFalse(bo.getWellFormedState().isFailure());
        assertFalse(bo.hasWarnings());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertFalse(bo.hasErrors());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        getServices().executePlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin", address,
            null, null);
        assertFalse(bo.getWellFormedState().isFailure());
        assertFalse(bo.hasErrors());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
//        System.out.println("\n" + bo.getWarnings());
        assertEquals(3, bo.getWarnings().size());
        assertEquals(80008, bo.getWarnings().get(0).getErrorCode());
        assertEquals(610007, bo.getWarnings().get(1).getErrorCode());
        assertEquals(610007, bo.getWarnings().get(2).getErrorCode());
        
    }


}
