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
 * Test class {@link org.qedeq.kernel.se.dto.module.HypothesisVo}.
 *
 * @author  Michael Meyling
 */
public class HypothesisVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = HypothesisVo.class;

    private HypothesisVo v0;

    private HypothesisVo v1;

    private HypothesisVo v2;

    protected void setUp() throws Exception {
        super.setUp();
        v0 = new HypothesisVo(null, null);
        v1 = new HypothesisVo("first", new FormulaVo(new DefaultAtom("dummy")));
        v2 = new HypothesisVo(new FormulaVo(new DefaultAtom("dummy")));
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetLabel() {
        assertNull(v0.getLabel());
        assertEquals("first", v1.getLabel());
        assertNull(v2.getLabel());
    }

    public void testGetFormula() {
        assertNull(v0.getFormula());
        assertTrue(EqualsUtility.equals(new FormulaVo(new DefaultAtom("dummy")), v1.getFormula()));
        assertTrue(EqualsUtility.equals(new FormulaVo(new DefaultAtom("dummy")), v2.getFormula()));
    }

}
