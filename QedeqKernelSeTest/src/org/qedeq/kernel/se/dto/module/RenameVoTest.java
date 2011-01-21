/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.se.dto.list.DefaultAtom;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.RenameVo}.
 *
 * @author  Michael Meyling
 */
public class RenameVoTest extends AbstractVoModuleTest {

    /** This class is tested. */
    private Class clazz = AddVo.class;

    private RenameVo rename;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        rename = new RenameVo("first", new DefaultAtom("first"), new DefaultAtom("second"), 3);
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetName() {
        assertEquals("Rename", rename.getName());
    }

    public void testGetReferences() {
        System.out.println(StringUtility.toString(rename.getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {"first"}, rename.getReferences()));
    }

}
