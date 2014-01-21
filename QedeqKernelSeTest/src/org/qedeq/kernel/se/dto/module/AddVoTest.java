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
 * Test class {@link org.qedeq.kernel.se.dto.module.AddVo}.
 *
 * @author  Michael Meyling
 */
public class AddVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = AddVo.class;

    private AddVo add1;
    private AddVo add2;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getAdd");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        add1 = new AddVo("first");
        add2 = new AddVo();
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetName() {
        assertEquals("Add", add1.getName());
        assertEquals("Add", add2.getName());
    }

    public void testGetModusPonens() {
        assertEquals(add1, add1.getAdd());
        assertEquals(add2, add2.getAdd());
    }

    public void testGetReferences() {
        assertTrue(EqualsUtility.equals(new String[] {"first"}, add1.getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {}, add2.getReferences()));
    }

}
