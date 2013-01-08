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
 * Testing formulas made of logical connectives.
 *
 * @author  Michael Meyling
 */
public class FormulaCheckerLogicalConnectivesTest extends AbstractFormulaChecker {

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
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A & B
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectivePositive01() throws Exception {
        final Element ele = BasicParser.createElement(
            "<AND><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></AND>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A v B
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectivePositive02() throws Exception {
        final Element ele = BasicParser.createElement(
            "<OR><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></OR>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A -> B
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectivePositive03() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></IMPL>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A <-> B
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectivePositive04() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></EQUI>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A & B & C
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectivePositive05() throws Exception {
        final Element ele = BasicParser.createElement(
            "<AND><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/><PREDVAR id=\"C\"/></AND>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A v B v C
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectivePositive06() throws Exception {
        final Element ele = BasicParser.createElement(
            "<OR><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/><PREDVAR id=\"C\"/></OR>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A <-> B <--> C
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectivePositive07() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/><PREDVAR id=\"C\"/></EQUI>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     &
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative01() throws Exception {
        final Element ele = BasicParser.createElement("<AND />");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     v
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative02() throws Exception {
        final Element ele = BasicParser.createElement("<OR />");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     ->
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative03() throws Exception {
        final Element ele = BasicParser.createElement("<IMPL />");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     <->
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative04() throws Exception {
        final Element ele = BasicParser.createElement("<EQUI />");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     A &
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative05() throws Exception {
        final Element ele = BasicParser.createElement("<AND><PREDVAR id=\"A\"/></AND>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     A v
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative06() throws Exception {
        final Element ele = BasicParser.createElement("<OR><PREDVAR id=\"A\"/></OR>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     A ->
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative07() throws Exception {
        final Element ele = BasicParser.createElement("<IMPL><PREDVAR id=\"A\"/></IMPL>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     A <->
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative08() throws Exception {
        final Element ele = BasicParser.createElement("<EQUI><PREDVAR id=\"A\"/></EQUI>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30760
     * Data:     A -> B --> C
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative09() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/><PREDVAR id=\"C\"/></IMPL>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30760, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30770, free and bound variables mixed
     * Data:     (x = x) & (y = {x | phi})
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative10() throws Exception {
        final Element ele = BasicParser.createElement(
            "<AND>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                  "<VAR id=\"y\" />" +
                  "<CLASS>" +
                      "<VAR id=\"x\" />" +
                      "<PREDVAR id=\"phi\" />" +
                  "</CLASS>" +
               "</PREDVAR>" +
            "</AND>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30770, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780, free and bound variables mixed
     * Data:     (y = {x | phi}) & (x = x)
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative11() throws Exception {
        final Element ele = BasicParser.createElement(
            "<AND>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"y\" />" +
                    "<CLASS>" +
                        "<VAR id=\"x\" />" +
                        "<PREDVAR id=\"phi\" />" +
                    "</CLASS>" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
            "</AND>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30770, free and bound variables mixed
     * Data:     (x = x) v (y = {x | phi})
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative12() throws Exception {
        final Element ele = BasicParser.createElement(
            "<OR>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                  "<VAR id=\"y\" />" +
                  "<CLASS>" +
                      "<VAR id=\"x\" />" +
                      "<PREDVAR id=\"phi\" />" +
                  "</CLASS>" +
               "</PREDVAR>" +
            "</OR>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30770, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780, free and bound variables mixed
     * Data:     (y = {x | phi}) v (x = x)
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative13() throws Exception {
        final Element ele = BasicParser.createElement(
            "<OR>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"y\" />" +
                    "<CLASS>" +
                        "<VAR id=\"x\" />" +
                        "<PREDVAR id=\"phi\" />" +
                    "</CLASS>" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
            "</OR>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30770, free and bound variables mixed
     * Data:     (x = x) -> (y = {x | phi})
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative14() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                  "<VAR id=\"y\" />" +
                  "<CLASS>" +
                      "<VAR id=\"x\" />" +
                      "<PREDVAR id=\"phi\" />" +
                  "</CLASS>" +
               "</PREDVAR>" +
            "</IMPL>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30770, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780, free and bound variables mixed
     * Data:     (y = {x | phi}) -> (x = x)
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative15() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"y\" />" +
                    "<CLASS>" +
                        "<VAR id=\"x\" />" +
                        "<PREDVAR id=\"phi\" />" +
                    "</CLASS>" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
            "</IMPL>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30770, free and bound variables mixed
     * Data:     (x = x) <-> (y = {x | phi})
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative16() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                  "<VAR id=\"y\" />" +
                  "<CLASS>" +
                      "<VAR id=\"x\" />" +
                      "<PREDVAR id=\"phi\" />" +
                  "</CLASS>" +
               "</PREDVAR>" +
            "</EQUI>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30770, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780, free and bound variables mixed
     * Data:     (y = {x | phi}) <-> (x = x)
     *
     * @throws  Exception   Test failed.
     */
    public void testLogicalConnectiveNegative17() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"y\" />" +
                    "<CLASS>" +
                        "<VAR id=\"x\" />" +
                        "<PREDVAR id=\"phi\" />" +
                    "</CLASS>" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
            "</EQUI>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }

}
