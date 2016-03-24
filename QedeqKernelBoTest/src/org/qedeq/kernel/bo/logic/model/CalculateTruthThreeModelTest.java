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
 * Testing the truth calculation with default model.
 *
 * @author  Michael Meyling
 */
public class CalculateTruthThreeModelTest extends CalculateTruthTestCase {

    /**
     * Constructor.
     */
    public CalculateTruthThreeModelTest() {
        super(new ThreeModel());
    }

    /**
     * Function: isTautology(Element)
     * Type:     false (in this model)
     * Data:     (y \in {x | \phi(x)} <-> (isSet(y) n \phi(y))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology44() throws Exception {
        final Element ele = BasicParser.createElement(
            "    <EQUI>\n"
            + "    <PREDCON ref=\"in\">\n"
            + "      <VAR id=\"y\"/>\n"
            + "      <CLASS>\n"
            + "        <VAR id=\"x\"/>\n"
            + "        <PREDVAR id=\"\\phi\">\n"
            + "          <VAR id=\"x\"/>\n"
            + "        </PREDVAR>\n"
            + "      </CLASS>\n"
            + "    </PREDCON>\n"
            + "    <AND>\n"
            + "      <PREDCON ref=\"isSet\">\n"
            + "        <VAR id=\"y\"/>\n"
            + "      </PREDCON>\n"
            + "      <PREDVAR id=\"\\phi\">\n"
            + "        <VAR id=\"y\"/>\n"
            + "      </PREDVAR>\n"
            + "    </AND>\n"
            + "  </EQUI>\n"
        );
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     false (in this model)
     * Data:     x <= y <-> x n C(y) = 0
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology46() throws Exception {
        final Element ele = BasicParser.createElement(
            "  <EQUI>\n"
            + "    <PREDCON ref=\"subclass\">\n"
            + "      <VAR id=\"x\" />\n"
            + "      <VAR id=\"y\" />\n"
            + "    </PREDCON>\n"
            + "    <PREDCON ref=\"l.equal\">\n"
            + "      <FUNCON ref=\"intersection\">\n"
            + "        <VAR id=\"x\" />\n"
            + "        <FUNCON ref=\"complement\">\n"
            + "          <VAR id=\"y\" />\n"
            + "        </FUNCON>\n"
            + "      </FUNCON>\n"
            + "      <FUNCON ref=\"emptySet\" />\n"
            + "    </PREDCON>\n"
            + "  </EQUI>\n"
            );
//      System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative (in this model)
     * Data:     see below
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology47() throws Exception {
        final Element ele = BasicParser.createElement(
            "<AND>\n"
            + "  <EQUI>\n"
            + "    <PREDCON ref=\"subclass\">\n"
            + "      <VAR id=\"x\" />\n"
            + "      <VAR id=\"y\" />\n"
            + "    </PREDCON>\n"
            + "    <PREDCON ref=\"l.equal\">\n"
            + "      <FUNCON ref=\"union\">\n"
            + "        <FUNCON ref=\"complement\">\n"
            + "          <VAR id=\"x\" />\n"
            + "        </FUNCON>\n"
            + "        <VAR id=\"y\" />\n"
            + "      </FUNCON>\n"
            + "      <FUNCON ref=\"universalClass\" />\n"
            + "    </PREDCON>\n"
            + "  </EQUI>\n"
            + "      \n"
            + "  <EQUI>\n"
            + "    <PREDCON ref=\"subclass\">\n"
            + "      <VAR id=\"x\" />\n"
            + "      <FUNCON ref=\"complement\">\n"
            + "        <VAR id=\"y\" />\n"
            + "      </FUNCON>\n"
            + "    </PREDCON>\n"
            + "    <PREDCON ref=\"l.equal\">\n"
            + "      <FUNCON ref=\"intersection\">\n"
            + "        <VAR id=\"x\" />\n"
            + "        <VAR id=\"y\" />\n"
            + "      </FUNCON>\n"
            + "      <FUNCON ref=\"emptySet\" />\n"
            + "    </PREDCON>\n"
            + "  </EQUI>\n"
            + "      \n"
            + "  <EQUI>\n"
            + "    <PREDCON ref=\"subclass\">\n"
            + "      <FUNCON ref=\"intersection\">\n"
            + "        <VAR id=\"x\" />\n"
            + "        <VAR id=\"y\" />\n"
            + "      </FUNCON>\n"
            + "      <VAR id=\"z\" />\n"
            + "    </PREDCON>\n"
            + "    <PREDCON ref=\"subclass\">\n"
            + "      <VAR id=\"x\" />\n"
            + "      <FUNCON ref=\"union\">\n"
            + "        <FUNCON ref=\"complement\">\n"
            + "          <VAR id=\"y\" />\n"
            + "        </FUNCON>\n"
            + "        <VAR id=\"z\" />\n"
            + "      </FUNCON>\n"
            + "    </PREDCON>\n"
            + "  </EQUI>              \n"
            + "      \n"
            + "</AND>\n"
        );
//      System.out.println(ele.toString());
      assertFalse(isTautology(ele));
  }



}
