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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.Enumerator;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.xml.parser.BasicParser;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.common.FormulaUtility}.
 *
 * @author  Michael Meyling
 */
public class FormulaUtilityReplaceSubjectVariableQuantifierTest extends QedeqTestCase {

    /**
     * Function: replaceSubjectVariableQuantifier().
     *
     * @throws  Exception   Test failed.
     */
    public void testClassTermPositive01() throws Exception {
        final Element ele1 = BasicParser.createElement(
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

        final Element ele2 = BasicParser.createElement(
              "<AND>" +
                "<PREDVAR id=\"equal\">" +
                  "<VAR id=\"x\" />" +
                  "<VAR id=\"x\" />" +
                "</PREDVAR>" +
                "<PREDVAR id=\"equal\">" +
                  "<VAR id=\"y\" />" +
                  "<CLASS>" +
                    "<VAR id=\"z\" />" +
                    "<PREDVAR id=\"phi\" />" +
                  "</CLASS>" +
                "</PREDVAR>" +
              "</AND>");
        // System.out.println(ele.toString());
        assertTrue(ele2.equals(FormulaUtility.replaceSubjectVariableQuantifier(
            createVar("x"), createVar("z"), ele1, 1, new Enumerator())));
    }

    /**
     * Function: replaceSubjectVariableQuantifier().
     *
     * @throws  Exception   Test failed.
     */
    public void testClassTermPositive02() throws Exception {
        final Element ele1 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"y\" />" +
                "<FORALL>" +
                  "<VAR id=\"z\" />" +
                  "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"y\" />" +
                    "<CLASS>" +
                      "<VAR id=\"x\" />" +
                      "<PREDVAR id=\"phi\">" +
                        "<VAR id=\"x\" />" +
                      "</PREDVAR>" +
                    "</CLASS>" +
                  "</PREDVAR>" +
                "</FORALL>" +
              "</FORALL>" +
            "</AND>");
        // System.out.println(ele.toString());

        final Element ele2 = BasicParser.createElement(
              "<AND>" +
                "<FORALL>" +
                  "<VAR id=\"x\" />" +
                  "<PREDVAR id=\"equal\">" +
                      "<VAR id=\"x\" />" +
                      "<VAR id=\"x\" />" +
                  "</PREDVAR>" +
                "</FORALL>" +
                "<FORALL>" +
                  "<VAR id=\"y\" />" +
                  "<FORALL>" +
                    "<VAR id=\"z\" />" +
                    "<PREDVAR id=\"equal\">" +
                      "<VAR id=\"y\" />" +
                      "<CLASS>" +
                        "<VAR id=\"uv\" />" +
                        "<PREDVAR id=\"phi\">" +
                          "<VAR id=\"uv\" />" +
                        "</PREDVAR>" +
                      "</CLASS>" +
                    "</PREDVAR>" +
                  "</FORALL>" +
                "</FORALL>" +
              "</AND>");
        // System.out.println(ele.toString());
        assertTrue(ele2.equals(FormulaUtility.replaceSubjectVariableQuantifier(
            createVar("x"), createVar("uv"), ele1, 2, new Enumerator())));
    }

    /**
     * Function: replaceSubjectVariableQuantifier().
     *
     * @throws  Exception   Test failed.
     */
    public void testClassTermPositive03() throws Exception {
        final Element ele1 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
            "</AND>");
        // System.out.println(ele.toString());

        final Element ele2 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"tip\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"tip\" />" +
                    "<VAR id=\"tip\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
            "</AND>");
        // System.out.println(ele.toString());
        assertTrue(ele2.equals(FormulaUtility.replaceSubjectVariableQuantifier(
            createVar("x"), createVar("tip"), ele1, 1, new Enumerator())));
    }

    public void testClassTermPositive04() throws Exception {
        final Element ele1 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
            "</AND>");
        // System.out.println(ele.toString());

        final Element ele2 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"tip\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"tip\" />" +
                    "<VAR id=\"tip\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
            "</AND>");
        // System.out.println(ele.toString());
        assertTrue(ele2.equals(FormulaUtility.replaceSubjectVariableQuantifier(
            createVar("x"), createVar("tip"), ele1, 4, new Enumerator())));
    }

    public void testClassTermPositive05() throws Exception {
        final Element ele1 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
            "</AND>");
        // System.out.println(ele.toString());

        final Element ele2 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDVAR id=\"equal\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                "</PREDVAR>" +
              "</FORALL>" +
            "</AND>");
        System.out.println(ele2.toString());
        assertTrue(ele2.equals(FormulaUtility.replaceSubjectVariableQuantifier(
            createVar("x"), createVar("tip"), ele1, 5, new Enumerator())));
    }

    private Element createVar(final String name) throws Exception {
        return BasicParser.createElement("<VAR id=\"" + name + "\" />");
    }
}
