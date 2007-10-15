/* $Id: FormulaCheckerNegationTest.java,v 1.5 2007/04/12 23:56:37 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.bo.module.ModuleContext;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 * Testing formulas made of negation.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class FormulaCheckerNegationTest extends AbstractFormulaChecker {

    private ModuleContext context;
    
    protected void setUp() throws Exception {
        context = new ModuleContext("memory", "getElement()");
    }

    protected void tearDown() throws Exception {
        context = null;
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     -A
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegationPositive01() throws Exception {
        final Element ele = TestParser.createElement(
            "<NOT><PREDVAR id=\"A\"/></NOT>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30710
     * Data:     -
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegationNegative01() throws Exception {
        final Element ele = TestParser.createElement("<NOT />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30710, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30710
     * Data:     -(A, B)
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegationNegative02() throws Exception {
        final Element ele = TestParser.createElement(
            "<NOT><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></NOT>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30710, e.getErrorCode());
        }
    }
    
}
