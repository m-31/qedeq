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
import org.qedeq.kernel.se.dto.list.DefaultAtom;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.RenameVo}.
 *
 * @author  Michael Meyling
 */
public class RenameVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = RenameVo.class;

    private RenameVo rename0;

    private RenameVo rename1;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getRename");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        rename0 = new RenameVo();
        rename1 = new RenameVo("first", new DefaultAtom("first"), new DefaultAtom("second"), 3);
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetRename() {
        assertEquals(rename0, rename0.getRename());
        assertEquals(rename1, rename1.getRename());
    }

    public void testGetName() {
        assertEquals("Rename", rename0.getName());
        assertEquals("Rename", rename1.getName());
    }

    public void testGetReferences() {
        assertTrue(EqualsUtility.equals(new String[] {}, rename0.getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {"first"}, rename1.getReferences()));
    }

}
