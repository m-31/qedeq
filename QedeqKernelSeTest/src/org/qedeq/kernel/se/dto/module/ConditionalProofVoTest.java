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

    private ConditionalProofVo vo;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getFormula");
        removeMethodToCheck("getLabel");
        removeMethodToCheck("getConditionalProof");
        removeMethodToCheck("getReason");
        removeMethodToCheck("getName");
        removeMethodToCheck("getReferences");
        vo = new ConditionalProofVo();
        vo.setConclusion(new ConclusionVo("first", new FormulaVo(new DefaultAtom("dummy"))));
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetLabel() {
        assertEquals("first", vo.getLabel());
    }

    public void testUnuasalGetter() {
        assertTrue(EqualsUtility.equals(new FormulaVo(new DefaultAtom("dummy")), vo.getFormula()));
        assertTrue(EqualsUtility.equals("first", vo.getLabel()));
        assertTrue(EqualsUtility.equals(vo, vo.getConditionalProof()));
        assertTrue(EqualsUtility.equals(vo, vo.getReason()));
        assertTrue(EqualsUtility.equals("CP", vo.getName()));
        assertTrue(EqualsUtility.equals(new String[] {}, vo.getReferences()));
    }

}
