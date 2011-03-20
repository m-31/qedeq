/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
 * Testing formulas made of negation.
 *
 * @author  Michael Meyling
 */
public class FormulaCheckerNegationTest extends AbstractFormulaChecker {

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
     * Data:     -A
     *
     * @throws  Exception   Test failed.
     */
    public void testNegationPositive01() throws Exception {
        final Element ele = BasicParser.createElement(
            "<NOT><PREDVAR id=\"A\"/></NOT>");
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30710
     * Data:     -
     *
     * @throws  Exception   Test failed.
     */
    public void testNegationNegative01() throws Exception {
        final Element ele = BasicParser.createElement("<NOT />");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30710, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30710
     * Data:     -(A, B)
     *
     * @throws  Exception   Test failed.
     */
    public void testNegationNegative02() throws Exception {
        final Element ele = BasicParser.createElement(
            "<NOT><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></NOT>");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30710, list.get(0).getErrorCode());
    }

}
