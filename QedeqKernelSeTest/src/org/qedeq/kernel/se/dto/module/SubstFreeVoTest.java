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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.dto.list.DefaultAtom;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.SubstFreeVo}.
 *
 * @author  Michael Meyling
 */
public class SubstFreeVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = SubstFreeVo.class;

    private SubstFreeVo free0;

    private SubstFreeVo free1;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getSubstFree");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        free0 = new SubstFreeVo();
        free1 = new SubstFreeVo("first", new DefaultAtom("first"), new DefaultAtom("second"));
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetSubstFree() {
        assertEquals(free0, free0.getSubstFree());
        assertEquals(free1, free1.getSubstFree());
        assertFalse(free0.equals(free1));
        assertFalse(free0.getSubstFree().equals(free1.getSubstFree()));
    }

    public void testGetName() {
        assertEquals("SubstFree", free0.getName());
        assertEquals("SubstFree", free1.getName());
    }

    public void testGetReferences() {
        assertTrue(EqualsUtility.equals(new String[] {}, free0.getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {"first"}, free1.getReferences()));
    }

}
