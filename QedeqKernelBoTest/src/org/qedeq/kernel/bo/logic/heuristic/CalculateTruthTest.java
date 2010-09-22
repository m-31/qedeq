/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic.heuristic;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.bo.logic.model.CalculateTruth;
import org.qedeq.kernel.bo.test.TestParser;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 * Testing formulas made of negation.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class CalculateTruthTest extends QedeqTestCase {

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     --A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology01() throws Exception {
        final Element ele = TestParser.createElement(
            "<NOT><NOT><PREDVAR id=\"A\"/></NOT></NOT>");
        // System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A v -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology02() throws Exception {
        final Element ele = TestParser.createElement(
            "<OR><PREDVAR id=\"A\"/><NOT><PREDVAR id=\"A\"/></NOT></OR>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A n -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology03() throws Exception {
        final Element ele = TestParser.createElement(
            "<AND><PREDVAR id=\"A\"/><NOT><PREDVAR id=\"A\"/></NOT></AND>");
        // System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A => -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology04() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><NOT><PREDVAR id=\"A\"/></NOT></IMPL>");
        // System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     -A => A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology05() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><NOT><PREDVAR id=\"A\"/></NOT><PREDVAR id=\"A\"/></IMPL>");
        // System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A => A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology06() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><PREDVAR id=\"A\"/></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology07() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><NOT><NOT><PREDVAR id=\"A\"/></NOT></NOT></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A <-> A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology08() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><PREDVAR id=\"A\"/><NOT><NOT><PREDVAR id=\"A\"/></NOT></NOT></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> B v A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology09() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><OR><PREDVAR id=\"B\"/><PREDVAR id=\"A\"/></OR></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> -B v A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology10() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><OR><NOT><PREDVAR id=\"B\"/></NOT><PREDVAR id=\"A\"/></OR></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A -> -B n A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology11() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><AND><NOT><PREDVAR id=\"B\"/></NOT><PREDVAR id=\"A\"/></AND></IMPL>");
        // System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> A v -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology12() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><OR><PREDVAR id=\"A\"/><NOT><PREDVAR id=\"A\"/></NOT></OR></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> A v B v C v D v E v F v G v H v I v J
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology13() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><OR><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/><PREDVAR id=\"C\"/>"
            + "<PREDVAR id=\"D\"/><PREDVAR id=\"E\"/><PREDVAR id=\"F\"/><PREDVAR id=\"G\"/>"
            + "<PREDVAR id=\"H\"/><PREDVAR id=\"I\"/><PREDVAR id=\"J\"/></OR></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A v B <-> B v A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology14() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><OR><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></OR>"
            + "<OR><PREDVAR id=\"B\"/><PREDVAR id=\"A\"/></OR></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A n B <-> B n A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology15() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><AND><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></AND>"
            + "<AND><PREDVAR id=\"B\"/><PREDVAR id=\"A\"/></AND></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A n B <-> B v A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology16() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><AND><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></AND>"
            + "<OR><PREDVAR id=\"B\"/><PREDVAR id=\"A\"/></OR></EQUI>");
        // System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     -A n B <-> -(-B v A)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology17() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><AND><NOT><PREDVAR id=\"A\"/></NOT><PREDVAR id=\"B\"/></AND>"
            + "<NOT><OR><NOT><PREDVAR id=\"B\"/></NOT><PREDVAR id=\"A\"/></OR></NOT></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A(x,y) <-> A(x,y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology18() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A(x,y) <-> A(y,x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology19() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"y\"/><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A(x,y) <-> A(x,x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology20() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A(y) <-> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology21() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A(x, y) <-> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology22() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A <-> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology23() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><PREDVAR id=\"A\"/>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A <-> B
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology24() throws Exception {
        final Element ele = TestParser.createElement(
            "<EQUI><PREDVAR id=\"A\"/>"
            + "<PREDVAR id=\"B\"/></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A -> B
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology25() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/>"
            + "<PREDVAR id=\"B\"/></IMPL>");
//        System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A -> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology26() throws Exception {
        final Element ele = TestParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></IMPL>");
//        System.out.println(ele.toString());
        assertFalse(CalculateTruth.isTautology(ele));
    }

}
