/* $Id: FormulaCheckerPredicateFormulaTest.java,v 1.5 2007/04/12 23:56:37 m31 Exp $
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

import java.net.URL;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.bo.module.ModuleContext;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 * Testing formulas made of predicate variables and predicate constants.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class FormulaCheckerPredicateFormulaTest extends AbstractFormulaChecker {

    private ModuleContext context;
    
    protected void setUp() throws Exception {
        context = new ModuleContext(new URL("http://memory.org"), "getElement()");
    }

    protected void tearDown() throws Exception {
        context = null;
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaPositive01() throws Exception {
        final Element ele = TestParser.createElement("<PREDVAR id=\"A\"/>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     V
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaPositive02() throws Exception {
        final Element ele = TestParser.createElement("<PREDCON ref=\"true\"/>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30720
     * Data:     no function variable name
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative01() throws Exception {
        final Element ele = TestParser.createElement("<PREDVAR />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30720, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30720
     * Data:     no function constant name
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative02() throws Exception {
        final Element ele = TestParser.createElement("<PREDCON />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30720, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30730
     * Data:     function name missing (but list instead of name)
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative09() throws Exception {
        final Element ele = TestParser.createElement("<PREDVAR > <VAR id=\"x\" /> </PREDVAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30730
     * Data:     function name missing (but list instead of name)
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative10() throws Exception {
        final Element ele = TestParser.createElement("<PREDCON > <VAR id=\"x\" /> </PREDCON>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30730
     * Data:     function name missing
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative13() throws Exception {
        final Element ele = TestParser.createElement("<PREDVAR> <A/> </PREDVAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30730
     * Data:     function name missing
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative14() throws Exception {
        final Element ele = TestParser.createElement("<PREDCON> <A/> </PREDCON>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30770
     * Data:     f(x, {x| phi})
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative15() throws Exception {
        final Element ele = TestParser.createElement(
            "<PREDVAR id=\"f\">"
                + "<VAR id=\"x\"/>"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
            + "</PREDVAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30770
     * Data:     x in {x| phi}
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative16() throws Exception {
        final Element ele = TestParser.createElement(
            "<PREDCON ref=\"in\">"
                + "<VAR id=\"x\"/>"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
            + "</PREDCON>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780
     * Data:     f({x| phi}, x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative19() throws Exception {
        final Element ele = TestParser.createElement(
            "<PREDVAR id=\"f\">"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
                + "<VAR id=\"x\"/>"
            + "</PREDVAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780
     * Data:     {x| phi} in x
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative20() throws Exception {
        final Element ele = TestParser.createElement(
            "<PREDCON id=\"in\">"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
                + "<VAR id=\"x\"/>"
            + "</PREDCON>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30590
     * Data:     F(x)  (unknown predicate constant)
     * 
     * @throws  Exception   Test failed.
     */
    public void testPredicateFormulaNegative22() throws Exception {
        final Element ele = TestParser.createElement(
            "<PREDCON id=\"F\" />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30590, e.getErrorCode());
        }
    }
    
}
