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


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.FunctionDefinitionVo}.
 *
 * @author    Michael Meyling
 */
public class FunctionDefinitionVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = FunctionDefinitionVo.class;

    private FunctionDefinitionVo vo;

    protected void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getAxiom");
        removeMethodToCheck("getInitialPredicateDefinition");
        removeMethodToCheck("getPredicateDefinition");
        removeMethodToCheck("getInitialFunctionDefinition");
        removeMethodToCheck("getFunctionDefinition");
        removeMethodToCheck("getProposition");
        removeMethodToCheck("getRule");
        vo = new FunctionDefinitionVo();
    }

    public void testOtherGetters() {
        assertNull(vo.getAxiom());
        assertNull(vo.getInitialPredicateDefinition());
        assertNull(vo.getPredicateDefinition());
        assertEquals(vo, vo.getFunctionDefinition());
        assertNull(vo.getInitialFunctionDefinition());
        assertNull(vo.getProposition());
        assertNull(vo.getRule());
    }
    
    protected Class getTestedClass() {
        return clazz;
    }

}
