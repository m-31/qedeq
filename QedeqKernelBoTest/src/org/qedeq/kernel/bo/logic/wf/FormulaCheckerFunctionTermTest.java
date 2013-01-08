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

package org.qedeq.kernel.bo.logic.wf;

import org.qedeq.kernel.bo.logic.common.FormulaChecker;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.xml.parser.BasicParser;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 * Testing terms made of function variables and function constants.
 *
 * @author  Michael Meyling
 */
public class FormulaCheckerFunctionTermTest extends AbstractFormulaChecker {

    private ModuleContext context;

    private FormulaChecker checker;

    protected void setUp() throws Exception {
        context = new ModuleContext(new DefaultModuleAddress("http://memory.org/sample.xml"), "getElement()");
        checker = new FormulaCheckerImpl();
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
        final Element ele = BasicParser.createElement(
            "<FUNVAR id=\"f\">" +
            "  <VAR id=\"x\" />" +
            "</FUNVAR>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkTerm(ele, context).hasErrors());
        assertFalse(checker.checkTerm(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkTerm(ele, context, getCheckerWithoutClass()).hasErrors());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     V
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermPositive02() throws Exception {
        final Element ele = BasicParser.createElement("<FUNCON ref=\"empty\"/>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkTerm(ele, context).hasErrors());
        assertFalse(checker.checkTerm(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkTerm(ele, context, getCheckerWithoutClass()).hasErrors());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30740
     * Data:     no function variable name and no argument
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative01() throws Exception {
        final Element ele = BasicParser.createElement("<FUNVAR />");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30740
     * Data:     f() (no function argument)
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative01b() throws Exception {
        final Element ele = BasicParser.createElement("<FUNVAR id=\"f\"/>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30720
     * Data:     no function constant name
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative02() throws Exception {
        final Element ele = BasicParser.createElement("<FUNCON />");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30720, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     function name missing (but list instead of name)
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative09() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FUNVAR>" +
            "  <VAR id=\"x\" /> <VAR id=\"x\" />" +
            "</FUNVAR>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30730, list.get(0).getErrorCode());
    }


    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     function name missing (but list instead of name)
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative10() throws Exception {
        final Element ele = BasicParser.createElement("<FUNCON > <VAR id=\"x\" /> </FUNCON>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30730, list.get(0).getErrorCode());
    }


    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     function name missing
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative13() throws Exception {
        final Element ele = BasicParser.createElement("<FUNVAR> <A/> <A/></FUNVAR>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30730, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30730
     * Data:     function name missing
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative14() throws Exception {
        final Element ele = BasicParser.createElement("<FUNCON> <A/> </FUNCON>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30730, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30770
     * Data:     f(x, {x| phi})
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative15() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FUNVAR id=\"f\">"
                + "<VAR id=\"x\"/>"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
            + "</FUNVAR>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30770, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30770
     * Data:     x union {x| phi}
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative16() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FUNCON ref=\"union\">"
                + "<VAR id=\"x\"/>"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
            + "</FUNCON>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30770, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30780
     * Data:     f({x| phi}, x)
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative19() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FUNVAR id=\"f\">"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
                + "<VAR id=\"x\"/>"
            + "</FUNVAR>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30780
     * Data:     {x| phi} union x
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative20() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FUNCON id=\"union\">"
                + "<CLASS> <VAR id=\"x\"/> <PREDVAR id=\"\\phi\" /> </CLASS>"
                + "<VAR id=\"x\"/>"
            + "</FUNCON>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30690
     * Data:     F(x)  (unknown function constant)
     *
     * @throws  Exception   Test failed.
     */
    public void testFunctionTermNegative22() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FUNCON id=\"F\" />");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30690, list.get(0).getErrorCode());
    }

}
