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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.service.internal.Element2LatexImpl;
import org.qedeq.kernel.bo.service.internal.Element2Utf8Impl;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.xml.parser.BasicParser;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.common.FormulaUtility}.
 *
 * @author  Michael Meyling
 */
public class FormulaUtilityReplaceOperatorVariableTest extends QedeqBoTestCase {

    
    private Element predVar1;

    private Element funVar1;


    protected void setUp() throws Exception {

        predVar1 = BasicParser.createElement(
            "<PREDVAR id=\"equal\">" +
                "<VAR id=\"x\" />" +
                "<VAR id=\"y\" />" +
            "</PREDVAR>");
        funVar1 = BasicParser.createElement(
            "<FUNVAR id=\"u\">" +
                "<VAR id=\"u\" />" +
                "<VAR id=\"v\" />" +
            "</FUNVAR>");
        super.setUp();
    }

    /**
     * Function: replaceOperatorVariable.
     *
     * @throws  Exception   Test failed.
     */
    public void test_Positive01() throws Exception {
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
                      "<PREDVAR id=\"\\phi\">" +
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
                "<PREDCON id=\"\\leq\">" +
                  "<VAR id=\"x\" />" +
                  "<VAR id=\"y\" />" +
                "</PREDCON>" +
                "<PREDCON id=\"\\leq\">" +
                  "<VAR id=\"y\" />" +
                  "<VAR id=\"x\" />" +
                "</PREDCON>" +
              "</AND>");
        // System.out.println(ele.toString());
        
        final Element ele3 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<AND>" +
                  "<PREDCON id=\"\\leq\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                  "</PREDCON>" +
                  "<PREDCON id=\"\\leq\">" +
                    "<VAR id=\"x\" />" +
                    "<VAR id=\"x\" />" +
                  "</PREDCON>" +
                "</AND>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"y\" />" +
                "<FORALL>" +
                  "<VAR id=\"z\" />" +
                  "<AND>" +
                    "<PREDCON id=\"\\leq\">" +
                      "<VAR id=\"y\" />" +
                      "<CLASS>" +
                        "<VAR id=\"x\" />" +
                        "<PREDVAR id=\"\\phi\">" +
                          "<VAR id=\"x\" />" +
                        "</PREDVAR>" +
                      "</CLASS>" +
                    "</PREDCON>" +
                    "<PREDCON id=\"\\leq\">" +
                      "<CLASS>" +
                        "<VAR id=\"x\" />" +
                        "<PREDVAR id=\"\\phi\">" +
                          "<VAR id=\"x\" />" +
                        "</PREDVAR>" +
                      "</CLASS>" +
                      "<VAR id=\"y\" />" +
                    "</PREDCON>" +
                  "</AND>" +
                "</FORALL>" +
              "</FORALL>" +
            "</AND>");
//        println(ele3);
//        println(FormulaUtility.replaceOperatorVariable(ele1,
//            predVar1, ele2));
        assertTrue(ele3.equals(FormulaUtility.replaceOperatorVariable(ele1,
            predVar1, ele2)));
    }

    /**
     * Function: replaceOperatorVariable.
     *
     * @throws  Exception   Test failed.
     */
    public void test_Positive02() throws Exception {
        final Element ele1 = BasicParser.createElement(
            "<AND>" +
              "<FORALL>" +
                "<VAR id=\"x\" />" +
                "<PREDCON id=\"equal\">" +
                    "<FUNVAR id=\"u\"> " +
                      "<VAR id=\"x\" />" +
                      "<VAR id=\"x\" />" +
                    "</FUNVAR>" +
                    "<VAR id=\"x\" />" +
                "</PREDCON>" +
              "</FORALL>" +
              "<FORALL>" +
                "<VAR id=\"y\" />" +
                "<FORALL>" +
                  "<VAR id=\"z\" />" +
                  "<PREDCON id=\"equal\">" +
                    "<VAR id=\"z\" />" +
                    "<FUNVAR id=\"u\">" +
                      "<CLASS>" +
                        "<VAR id=\"x\" />" +
                        "<PREDVAR id=\"\\phi\">" +
                          "<VAR id=\"x\" />" +
                        "</PREDVAR>" +
                      "</CLASS>" +
                      "<VAR id=\"y\" />" +
                    "</FUNVAR>" +
                  "</PREDCON>" +
                "</FORALL>" +
              "</FORALL>" +
            "</AND>");
        // System.out.println(ele1.toString());

        final Element ele2 = BasicParser.createElement(
              "<FUNCON id=\"union\">" +
                  "<VAR id=\"v\" />" +
                  "<VAR id=\"u\" />" +
                  "<VAR id=\"v\" />" +
               "</FUNCON>");
        // System.out.println(ele2.toString());
        
        final Element ele3 = BasicParser.createElement(
              "<AND>\r\n" + 
              "  <FORALL>\r\n" + 
              "    <VAR id=\"x\"/>\r\n" + 
              "    <PREDCON ref=\"equal\">\r\n" + 
              "      <FUNCON ref=\"union\">\r\n" + 
              "        <VAR id=\"x\"/>\r\n" + 
              "        <VAR id=\"x\"/>\r\n" + 
              "        <VAR id=\"x\"/>\r\n" + 
              "      </FUNCON>\r\n" + 
              "      <VAR id=\"x\"/>\r\n" + 
              "    </PREDCON>\r\n" + 
              "  </FORALL>\r\n" + 
              "  <FORALL>\r\n" + 
              "    <VAR id=\"y\"/>\r\n" + 
              "    <FORALL>\r\n" + 
              "      <VAR id=\"z\"/>\r\n" + 
              "      <PREDCON ref=\"equal\">\r\n" + 
              "        <VAR id=\"z\"/>\r\n" + 
              "        <FUNCON ref=\"union\">\r\n" + 
              "          <VAR id=\"y\"/>\r\n" + 
              "          <CLASS>\r\n" + 
              "            <VAR id=\"x\"/>\r\n" + 
              "            <PREDVAR id=\"\\phi\">\r\n" + 
              "              <VAR id=\"x\"/>\r\n" + 
              "            </PREDVAR>\r\n" + 
              "          </CLASS>\r\n" + 
              "          <VAR id=\"y\"/>\r\n" + 
              "        </FUNCON>\r\n" + 
              "      </PREDCON>\r\n" + 
              "    </FORALL>\r\n" + 
              "  </FORALL>\r\n" + 
              "</AND>\r\n" + 
              "");
                
//        println(ele1);
//        println(funVar1);
//        println(ele2);
//        println(FormulaUtility.replaceOperatorVariable(ele1, funVar1, ele2));
//        printlnXml(FormulaUtility.replaceOperatorVariable(ele1, funVar1, ele2));
        assertTrue(ele3.equals(FormulaUtility.replaceOperatorVariable(ele1, funVar1, ele2)));
    }

    public void println(final Element element) {
        ModuleLabels labels = new ModuleLabels();
        Element2LatexImpl converter = new Element2LatexImpl(labels);
        Element2Utf8Impl textConverter = new Element2Utf8Impl(converter);
        System.out.println(textConverter.getUtf8(element));
    }

}
