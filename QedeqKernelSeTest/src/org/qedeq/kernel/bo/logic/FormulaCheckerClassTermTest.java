/* $Id: FormulaCheckerClassTermTest.java,v 1.8 2008/03/27 05:12:43 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.bo.control.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleContext;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 * Testing formulas made of class terms.
 *
 * @version $Revision: 1.8 $
 * @author  Michael Meyling
 */
public class FormulaCheckerClassTermTest extends AbstractFormulaChecker {

    private ModuleContext context;
    
    protected void setUp() throws Exception {
        context = new ModuleContext(new DefaultModuleAddress("http://memory.org/sample.xml"),
            "getElement()");
    }

    protected void tearDown() throws Exception {
        context = null;
    }

    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     {x | A}
     * 
     * @throws  Exception   Test failed.
     */
    public void testClassTermPositive01() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS><VAR id=\"x\" /><PREDVAR id=\"A\" /></CLASS>");
        System.out.println(ele.toString());
        FormulaChecker.checkTerm(ele, context);
        FormulaChecker.checkTerm(ele, context, getChecker());
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     {x | phi(y, x)}
     * 
     * @throws  Exception   Test failed.
     */
    public void testClassTermPositive02() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"y\" />" +
            "    <VAR id=\"x\" />" +
            "  </PREDVAR>" +
            "</CLASS>");
        System.out.println(ele.toString());
        FormulaChecker.checkTerm(ele, context);
        FormulaChecker.checkTerm(ele, context, getChecker());
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     {x | forall y phi(y, x)}
     * 
     * @throws  Exception   Test failed.
     */
    public void testClassTermPositive03() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS>" +
            "  <VAR id=\"x\" />" +
            "  <FORALL>" +
            "    <VAR id=\"y\" />" +
            "    <PREDVAR id=\"phi\">" +
            "      <VAR id=\"y\" />" +
            "      <VAR id=\"x\" />" +
            "    </PREDVAR>" +
            "  </FORALL>" +
            "</CLASS>");
        System.out.println(ele.toString());
        FormulaChecker.checkTerm(ele, context);
        FormulaChecker.checkTerm(ele, context, getChecker());
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30760, exactly two arguments expected
     * Data:     {x | phi(y, x) | phi(y, x)}
     * 
     * @throws  Exception   Test failed.
     */
    public void testClassTermNegative01() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"y\" />" +
            "    <VAR id=\"x\" />" +
            "  </PREDVAR>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"y\" />" +
            "    <VAR id=\"x\" />" +
            "  </PREDVAR>" +
            "</CLASS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30760, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30760, exactly two arguments expected
     * Data:     {x | }
     * 
     * @throws  Exception   Test failed.
     */
    public void testClassTermNegative02() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS>" +
            "  <VAR id=\"x\" />" +
            "</CLASS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30760, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30540, first argument must be a subject variable
     * Data:     {x | phi(y, x) | phi(y, x)}
     * 
     * @throws  Exception   Test failed.
     */
    public void testClassTermNegative03() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"y\" />" +
            "    <VAR id=\"x\" />" +
            "  </PREDVAR>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"y\" />" +
            "    <VAR id=\"x\" />" +
            "  </PREDVAR>" +
            "</CLASS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30540, e.getErrorCode());
        }
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30550, subject variable already bound
     * Data:     {x | forall x phi(y, x)}
     * 
     * @throws  Exception   Test failed.
     */
    public void testClassTermNegative04() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS>" +
            "  <VAR id=\"x\" />" +
            "  <FORALL>" +
            "    <VAR id=\"x\" />" +
            "    <PREDVAR id=\"phi\">" +
            "      <VAR id=\"y\" />" +
            "      <VAR id=\"x\" />" +
            "    </PREDVAR>" +
            "  </FORALL>" +
            "</CLASS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30680, undefined class operator
     * Data:     {x | forall y phi(y, x)}
     * 
     * @throws  Exception   Test failed.
     */
    public void testClassTermNegative05() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS>" +
            "  <VAR id=\"x\" />" +
            "  <FORALL>" +
            "    <VAR id=\"y\" />" +
            "    <PREDVAR id=\"phi\">" +
            "      <VAR id=\"y\" />" +
            "      <VAR id=\"x\" />" +
            "    </PREDVAR>" +
            "  </FORALL>" +
            "</CLASS>");
        System.out.println(ele.toString());
        FormulaChecker.checkTerm(ele, context, getChecker());   // here class operator is defined
        try {
            FormulaChecker.checkTerm(ele, context, getCheckerWithoutClass());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30680, e.getErrorCode());
        }
    }
    
    
}
