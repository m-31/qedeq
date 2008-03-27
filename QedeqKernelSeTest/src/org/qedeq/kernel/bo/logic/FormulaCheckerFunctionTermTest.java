/* $Id: FormulaCheckerFunctionTermTest.java,v 1.8 2008/03/27 05:12:43 m31 Exp $
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
 * Testing terms made of function variables and function constants.
 *
 * @version $Revision: 1.8 $
 * @author  Michael Meyling
 */
public class FormulaCheckerFunctionTermTest extends AbstractFormulaChecker {

    private ModuleContext context;
    
    protected void setUp() throws Exception {
        context = new ModuleContext(new DefaultModuleAddress("http://memory.org/sample.xml"), "getElement()");
    }

    protected void tearDown() throws Exception {
        context = null;
    }

    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     f(x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermPositive01() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNVAR id=\"f\">" +
            "  <VAR id=\"x\" />" +
            "</FUNVAR>");
        System.out.println(ele.toString());
        FormulaChecker.checkTerm(ele, context);
        FormulaChecker.checkTerm(ele, context, getChecker());
        FormulaChecker.checkTerm(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     V
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermPositive02() throws Exception {
        final Element ele = TestParser.createElement("<FUNCON ref=\"empty\"/>");
        System.out.println(ele.toString());
        FormulaChecker.checkTerm(ele, context);
        FormulaChecker.checkTerm(ele, context, getChecker());
        FormulaChecker.checkTerm(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30740
     * Data:     no function variable name and no argument
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative01() throws Exception {
        final Element ele = TestParser.createElement("<FUNVAR />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30740, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30740
     * Data:     f() (no function argument)
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative01b() throws Exception {
        final Element ele = TestParser.createElement("<FUNVAR id=\"f\"/>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30740, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30720
     * Data:     no function constant name
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative02() throws Exception {
        final Element ele = TestParser.createElement("<FUNCON />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30720, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     function name missing (but list instead of name)
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative09() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNVAR>" +
            "  <VAR id=\"x\" /> <VAR id=\"x\" />" +
            "</FUNVAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     function name missing (but list instead of name)
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative10() throws Exception {
        final Element ele = TestParser.createElement("<FUNCON > <VAR id=\"x\" /> </FUNCON>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     function name missing
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative13() throws Exception {
        final Element ele = TestParser.createElement("<FUNVAR> <A/> <A/></FUNVAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     function name missing
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative14() throws Exception {
        final Element ele = TestParser.createElement("<FUNCON> <A/> </FUNCON>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30730, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30770
     * Data:     f(x, {x| phi})
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative15() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNVAR id=\"f\">"
                + "<VAR id=\"x\"/>"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
            + "</FUNVAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30770
     * Data:     x union {x| phi}
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative16() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNCON ref=\"union\">"
                + "<VAR id=\"x\"/>"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
            + "</FUNCON>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30780
     * Data:     f({x| phi}, x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative19() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNVAR id=\"f\">"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
                + "<VAR id=\"x\"/>"
            + "</FUNVAR>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30780
     * Data:     {x| phi} union x
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative20() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNCON id=\"union\">"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
                + "<VAR id=\"x\"/>"
            + "</FUNCON>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30690
     * Data:     F(x)  (unknown function constant)
     * 
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative22() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNCON id=\"F\" />");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30690, e.getErrorCode());
        }
    }
    
}
