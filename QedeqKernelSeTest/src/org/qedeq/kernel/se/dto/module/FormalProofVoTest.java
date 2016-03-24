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
 * Test class {@link org.qedeq.kernel.se.dto.module.FormalProofVo}.
 *
 * @author  Michael Meyling
 */
public class FormalProofVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = FormalProofVo.class;


    protected Class getTestedClass() {
        return clazz;
    }

    public void testConstructor() {
        FormalProofVo vo = null;
        vo = new FormalProofVo(null);
        assertNull(vo.getFormalProofLineList());
        FormalProofLineListVo lines = new FormalProofLineListVo();
        lines.add(new FormalProofLineVo());
        vo = new FormalProofVo(lines);
        assertEquals(lines, vo.getFormalProofLineList());
    }

}
