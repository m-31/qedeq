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
 * Test class {@link org.qedeq.kernel.se.dto.module.ExistentialVo}.
 *
 * @author  Michael Meyling
 */
public class ExistentialVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = ExistentialVo.class;

    private ExistentialVo existential0;
    private ExistentialVo existential1;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getExistential");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        existential0 = new ExistentialVo();
        existential1 = new ExistentialVo("first", new DefaultAtom("first"));
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetExistential() {
        assertEquals(existential0, existential0.getExistential());
        assertEquals(existential1, existential1.getExistential());
    }

    public void testGetName() {
        assertEquals("Existential", existential0.getName());
        assertEquals("Existential", existential1.getName());
    }

    public void testGetReferences() {
        assertTrue(EqualsUtility.equals(new String[] {}, existential0.getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {"first"}, existential1.getReferences()));
    }

}
