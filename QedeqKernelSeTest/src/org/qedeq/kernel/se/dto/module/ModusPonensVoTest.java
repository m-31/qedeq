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


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.ModusPonensVo}.
 *
 * @author  Michael Meyling
 */
public class ModusPonensVoTest extends AbstractVoModuleTest {

    /** This class is tested. */
    private Class clazz = ModusPonensVo.class;

    private ModusPonensVo mp;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getModusPonens");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        mp = new ModusPonensVo("first", "second");
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetName() {
        assertEquals("MP", mp.getName());
    }

    public void testGetReferences() {
        assertTrue(EqualsUtility.equals(new String[] {"first", "second"}, mp.getReferences()));
    }

}
