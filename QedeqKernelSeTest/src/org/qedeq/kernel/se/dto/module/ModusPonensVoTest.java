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

import org.qedeq.base.utility.EqualsUtility;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.ModusPonensVo}.
 *
 * @author  Michael Meyling
 */
public class ModusPonensVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = ModusPonensVo.class;

    private ModusPonensVo mp1;
    private ModusPonensVo mp2;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getModusPonens");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        mp1 = new ModusPonensVo("first", "second");
        mp2 = new ModusPonensVo();
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetModusPonens() {
        assertEquals(mp1, mp1.getModusPonens());
        assertEquals(mp2, mp2.getModusPonens());
    }

    public void testGetName() {
        assertEquals("MP", mp1.getName());
        assertEquals("MP", mp2.getName());
    }

    public void testGetReference1() {
        assertEquals("first", mp1.getReference1());
        assertNull(mp2.getReference1());
    }

    public void testGetReference2() {
        assertEquals("second", mp1.getReference2());
        assertNull(mp2.getReference2());
    }

    public void testGetReferences() {
        assertTrue(EqualsUtility.equals(new String[] {"first", "second"}, mp1.getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {}, mp2.getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {"first"},
            new ModusPonensVo("first", null).getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {"second"},
            new ModusPonensVo(null, "second").getReferences()));
    }

}
