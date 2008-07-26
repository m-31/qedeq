/* $Id: FormulaCheckerTermTest.java,v 1.9 2008/05/15 21:27:29 m31 Exp $
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

package org.qedeq.kernel.bo.logic.wf;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.bo.logic.FormulaChecker;
import org.qedeq.kernel.bo.service.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleContext;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 * Testing terms.
 *
 * @version $Revision: 1.9 $
 * @author  Michael Meyling
 */
public class FormulaCheckerTermTest extends AbstractFormulaChecker {

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
     * Data:     {x | x in a & x in b}
     *
     * @throws  Exception   Test failed.
     */
    public void testTermPositive01() throws Exception {
        final Element ele = TestParser.createElement(
            "<CLASS>" +
            "  <VAR id=\"x\" />" +
            "  <AND>" +
            "    <PREDCON id=\"in\">" +
            "      <VAR id=\"x\" />" +
            "      <VAR id=\"a\" />" +
            "    </PREDCON>" +
            "    <PREDCON id=\"in\">" +
            "      <VAR id=\"x\" />" +
            "      <VAR id=\"b\" />" +
            "    </PREDCON>" +
            "  </AND>" +
            "</CLASS>");
        // System.out.println(ele.toString());
        assertFalse(FormulaChecker.checkTerm(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkTerm(ele, context, getChecker()).hasErrors());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     f(x, y, x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTermPositive02() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNVAR id=\"f\">" +
            "  <VAR id=\"x\" />" +
            "  <VAR id=\"y\" />" +
            "  <VAR id=\"x\" />" +
            "</FUNVAR>");
        // System.out.println(ele.toString());
        assertFalse(FormulaChecker.checkTerm(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkTerm(ele, context, getChecker()).hasErrors());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     Power(x union y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTermPositive03() throws Exception {
        final Element ele = TestParser.createElement(
            "<FUNCON id=\"power\">" +
            "  <FUNCON id=\"union\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </FUNCON>" +
            "</FUNCON>");
        // System.out.println(ele.toString());
        assertFalse(FormulaChecker.checkTerm(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkTerm(ele, context, getChecker()).hasErrors());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     positive
     * Data:     x
     *
     * @throws  Exception   Test failed.
     */
    public void testTermPositive04() throws Exception {
        final Element ele = TestParser.createElement(
            "<VAR id=\"x\" />");
        // System.out.println(ele.toString());
        assertFalse(FormulaChecker.checkTerm(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkTerm(ele, context, getChecker()).hasErrors());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     A & B
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative01() throws Exception {
        final Element ele = TestParser.createElement(
          "<AND><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></AND>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     UNKNOWN(A & B)
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative02() throws Exception {
        final Element ele = TestParser.createElement(
          "<UNKNOWN><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></UNKNOWN>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     A v B
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative03() throws Exception {
        final Element ele = TestParser.createElement(
        "<OR><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></OR>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     A -> B
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative04() throws Exception {
        final Element ele = TestParser.createElement(
        "<IMPL><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></IMPL>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     A <-> B
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative05() throws Exception {
        final Element ele = TestParser.createElement(
        "<EQUI><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></EQUI>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative06() throws Exception {
        final Element ele = TestParser.createElement(
          "<NOT><PREDVAR id=\"A\"/></NOT>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     A
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative07() throws Exception {
        final Element ele = TestParser.createElement(
          "<PREDVAR id=\"A\"/>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     all x A
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative08() throws Exception {
        final Element ele = TestParser.createElement(
          "<FORALL><VAR id=\"x\" /><PREDVAR id=\"A\"/></FORALL>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     exists x A
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative09() throws Exception {
        final Element ele = TestParser.createElement(
          "<EXISTS><VAR id=\"x\" /><PREDVAR id=\"A\"/></EXISTS>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30620, unknown term operator
     * Data:     exists! x A
     *
     * @throws  Exception   Test failed.
     */
    public void testTermNegative10() throws Exception {
        final Element ele = TestParser.createElement(
          "<EXISTSU><VAR id=\"x\" /><PREDVAR id=\"A\"/></EXISTSU>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }

}
