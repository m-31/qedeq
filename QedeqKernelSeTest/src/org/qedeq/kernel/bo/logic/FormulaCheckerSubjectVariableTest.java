/* $Id: FormulaCheckerSubjectVariableTest.java,v 1.5 2007/04/12 23:56:37 m31 Exp $
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
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class FormulaCheckerSubjectVariableTest extends AbstractFormulaChecker {

    private ModuleContext context;
    
    protected void setUp() throws Exception {
        context = new ModuleContext("memory", "getElement()");
    }

    protected void tearDown() throws Exception {
        context = null;
    }

    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     x
     * 
     * @throws  Exception   Test failed.
     */
    public void testSubjectVariablePositive() throws Exception {
        final Element ele = TestParser.createElement("<VAR id=\"x\"/>");
        System.out.println(ele.toString());
        FormulaChecker.checkTerm(ele, context);
        FormulaChecker.checkTerm(ele, context, getChecker());
        FormulaChecker.checkTerm(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30710
     * Data:     no variable name
     * 
     * @throws  Exception   Test failed.
     */
    public void testSubjectVariableNegative1() throws Exception {
        final Element ele = TestParser.createElement("<VAR />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30710, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30710
     * Data:     x 12
     * 
     * @throws  Exception   Test failed.
     */
    public void testSubjectVariableNegative2() throws Exception {
        final Element ele = TestParser.createElement("<VAR id=\"x\" ref=\"12\" />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30710, e.getErrorCode());
        }
    }
    
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     ?
     * 
     * @throws  Exception   Test failed.
     */
    public void testSubjectVariableNegative3() throws Exception {
        final Element ele = TestParser.createElement("<VAR> <A/> </VAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    
}
