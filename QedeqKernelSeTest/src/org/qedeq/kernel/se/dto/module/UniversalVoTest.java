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
 * Test class {@link org.qedeq.kernel.se.dto.module.UniversalVo}.
 *
 * @author  Michael Meyling
 */
public class UniversalVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = UniversalVo.class;

    private UniversalVo universal0;
    private UniversalVo universal1;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getUniversal");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        universal0 = new UniversalVo(null, null);
        universal1 = new UniversalVo("first", new DefaultAtom("first"));
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetUniversal() {
        assertEquals(universal0, universal0.getUniversal());
        assertEquals(universal1, universal1.getUniversal());
    }

    public void testGetName() {
        assertEquals("Universal", universal0.getName());
        assertEquals("Universal", universal1.getName());
    }

    public void testGetReferences() {
        assertTrue(EqualsUtility.equals(new String[] {}, universal0.getReferences()));
        assertTrue(EqualsUtility.equals(new String[] {"first"}, universal1.getReferences()));
    }

}
