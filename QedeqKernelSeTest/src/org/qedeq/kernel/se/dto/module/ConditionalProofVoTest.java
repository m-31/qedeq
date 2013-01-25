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

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.dto.list.DefaultAtom;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.ConditionalProofVo}.
 *
 * @author  Michael Meyling
 */
public class ConditionalProofVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = ConditionalProofVo.class;

    private ConditionalProofVo vo1;
    private ConditionalProofVo vo2;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getFormula");
        removeMethodToCheck("getLabel");
        removeMethodToCheck("getConditionalProof");
        removeMethodToCheck("getReason");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        vo1 = new ConditionalProofVo();
        vo1.setConclusion(new ConclusionVo("first", new FormulaVo(new DefaultAtom("dummy"))));
        vo2 = new ConditionalProofVo(null, null, null);
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetLabel() {
        assertEquals("first", vo1.getLabel());
        assertEquals(null, vo2.getLabel());
    }

    public void testGetFormula() {
        assertEquals(new FormulaVo(new DefaultAtom("dummy")), vo1.getFormula());
        assertEquals(null, vo2.getFormula());
    }

    public void testUnuasalGetter() {
        assertTrue(EqualsUtility.equals(new FormulaVo(new DefaultAtom("dummy")), vo1.getFormula()));
        assertTrue(EqualsUtility.equals("first", vo1.getLabel()));
        assertTrue(EqualsUtility.equals(vo1, vo1.getConditionalProof()));
        assertTrue(EqualsUtility.equals(vo1, vo1.getReason()));
        assertTrue(EqualsUtility.equals("CP", vo1.getName()));
        assertTrue(EqualsUtility.equals(new String[] {}, vo1.getReferences()));
    }

}
