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

package org.qedeq.kernel.se.dto.module;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.FormalProofLineVo}.
 *
 * @author  Michael Meyling
 */
public class FormalProofLineVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = FormalProofLineVo.class;

    protected Class getTestedClass() {
        return clazz;
    }

    public void testConstructos() {
        FormalProofLineVo vo = null;
        vo = new FormalProofLineVo(null, null);
        assertNull(vo.getLabel());
        assertNull(vo.getFormula());
        assertNull(vo.getReason());
        vo.hashCode();
        assertEquals("    null null", vo.toString());
        vo = new FormalProofLineVo(null, null, null);
        assertNull(vo.getLabel());
        assertNull(vo.getFormula());
        assertNull(vo.getReason());
        vo.hashCode();
        assertEquals("    null null", vo.toString());
    }
    
}
