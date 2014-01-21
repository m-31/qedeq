/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic.model;

import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.xml.parser.BasicParser;


/**
 * Testing the truth calculation with zero model.
 *
 * @author  Michael Meyling
 */
public class CalculateTruthDynamicUnaryModelTest extends CalculateTruthTestCase {

    /**
     * Constructor.
     */
    public CalculateTruthDynamicUnaryModelTest() {
        super(new UnaryDynamicModel());
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive (only in this model!!!)
     * Data:     A(x,y) <-> A(y,x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology19() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"y\"/><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Type:     positive (only in this model!!!)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology20() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive (only in this model!!!)
     * Data:     A(y) <-> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology21() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }
    
    /**
     * Function: isTautology(Element)
     * Type:     positive (only in this model!!!)
     * Data:     \forall x \forall y (A(x) -> A(y))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology28() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FORALL><VAR id=\"x\"/><FORALL><VAR id=\"y\"/><IMPL><PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"y\"/></PREDVAR></IMPL></FORALL></FORALL>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive (only in this model!!!)
     * Data:     \forall x \forall A(x,y): A(y,x))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology31() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FORALL><VAR id=\"x\"/><FORALL><VAR id=\"y\"/><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"y\"/><VAR id=\"x\"/></PREDVAR></FORALL></FORALL>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive (only in this model!!!)
     * Data:     \exists! y (y = y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology34() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EXISTSU><VAR id=\"y\"/><PREDCON id=\"equal\"><VAR id=\"y\"/><VAR id=\"y\"/></PREDCON>"
            + "</EXISTSU>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive (only in this model!!!)
     * Data:     f(x) = f(y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology37() throws Exception {
        final Element ele = BasicParser.createElement(
            "<PREDCON id=\"equal\"><FUNVAR id=\"f\"><VAR id=\"x\"/></FUNVAR>"
            + "<FUNVAR id=\"f\"><VAR id=\"y\"/></FUNVAR></PREDCON>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }
    
    /**
     * Function: isTautology(Element)
     * Type:     positive (only in this model!!!)
     * Data:     \forall x (\phi(x) <-> A) <-> (\forall x (\phi(x)) <-> A)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology41() throws Exception {
        final Element ele = BasicParser.createElement(
            "\n                <EQUI>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <EQUI>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"A\" />"
            + "\n                    </EQUI>"
            + "\n                  </FORALL>"
            + "\n                  <EQUI>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                    <PREDVAR id=\"A\" />"
            + "\n                  </EQUI>"
            + "\n                </EQUI>"
            );
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    public void testTautology44()  {
        try {
            super.testTautology44();
            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getMessage().indexOf("unknown term operator: isSet") >= 0);
        }
    }

    public void testTautology45()  {
        try {
            super.testTautology44();
            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getMessage().indexOf("unknown term operator: isSet") >= 0);
        }
    }

    public void testTautology46()  {
        try {
            super.testTautology44();
            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getMessage().indexOf("unknown term operator: isSet") >= 0);
        }
    }

    public void testTautology47()  {
        try {
            super.testTautology44();
            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getMessage().indexOf("unknown term operator: isSet") >= 0);
        }
    }

}
