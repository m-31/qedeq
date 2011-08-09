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
import org.qedeq.kernel.se.dto.list.DefaultAtom;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.SubstFuncVo}.
 *
 * @author  Michael Meyling
 */
public class SubstFuncVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = SubstFuncVo.class;

    private SubstFuncVo subst;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getSubstFunc");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        subst = new SubstFuncVo("first", new DefaultAtom("first"), new DefaultAtom("second"));
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetName() {
        assertEquals("SubstFun", subst.getName());
    }

    public void testGetReferences() {
        assertTrue(EqualsUtility.equals(new String[] {"first"}, subst.getReferences()));
    }

}
