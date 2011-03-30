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

package org.qedeq.kernel.bo.logic.model;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.test.DynamicGetter;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.xml.parser.BasicParser;

/**
 * For testing {@link org.qedeq.kernel.bo.logic.model.CalculateTruth}.
 *
 * @author  Michael Meyling
 */
public class DynamicDirectInterpreterTest extends QedeqBoTestCase {

    private DynamicDirectInterpreter interpreter;
    private KernelQedeqBo prop;

    /**
     * Constructor.
     *
     * @param   model    Dynamic to use.
     */
    public DynamicDirectInterpreterTest() {
        super();
    }

    public void setUp() throws Exception {
        super.setUp();
        final ModuleAddress address = getServices().getModuleAddress(
                IoUtility.toUrl(new File(getDocDir(), "math/qedeq_set_theory_v1.xml")));
        prop = (KernelQedeqBo) getServices().loadModule(
            address);
        getServices().checkModule(prop.getModuleAddress());
    }

    public void tearDown() throws Exception {
        interpreter = null;
        super.tearDown();
    }

    /**
     * Test if given formula is a tautology. This is done by checking a default model and
     * iterating through variable values.
     *
     * @param   formula         Formula.
     * @return  Is this formula a tautology according to our tests.
     * @throws  HeuristicException  Evaluation failed.
     */
    public boolean isTautology(final Element formula)
            throws HeuristicException {
        interpreter = new DynamicDirectInterpreter(prop, new FourDynamicModel()) {
            
            /** Start element for calculation. */
            private Object startElement = formula;

            /** Module context. Here were are currently. */
            private ModuleContext startContext = new ModuleContext(new DefaultModuleAddress());

            protected void setModuleContext(KernelQedeqBo qedeq) {
                super.setModuleContext(qedeq);
                startElement = qedeq.getQedeq();
//                System.out.println("Startelement set to " + startElement.getClass());
            }

            protected void setLocationWithinModule(final String localContext) {
                super.setLocationWithinModule(localContext);
                if (localContext.equals(startContext.getLocationWithinModule())) {
                    return;
                }
                if (!localContext.startsWith("getChapterList")) {
                    startElement = formula;
                }
                String position
                    = getModuleContext().getLocationWithinModule().substring(startContext.getLocationWithinModule().length());
                if (position.startsWith(".")) {
                    position = position.substring(1);
                }
                try {
//                    System.out.println(position);
//                    System.out.println(DynamicGetter.get(startElement, position));
                    DynamicGetter.get(startElement, position);
//                    System.out.println(Latex2Utf8Parser.transform(null,
//                            prop.getElement2Latex().getLatex((Element) DynamicGetter.get(startElement, position)), 0));
                } catch (IllegalAccessException e) {
                    System.out.println(position);
                    e.printStackTrace(System.out);
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    System.out.println(position);
//                    System.out.println(startElement);
                    e.printStackTrace(System.out);
                    throw new RuntimeException(e);
                } catch (RuntimeException e) {
                    System.out.println(position);
//                    System.out.println(startElement);
                    e.printStackTrace(System.out);
                    throw e;
                }
            }

        };
        final ModuleContext context = new ModuleContext(new DefaultModuleAddress());
        boolean result = true;
        do {
            result &= interpreter.calculateValue(context, formula);
//            System.out.println(interpreter.toString());
        } while (result && interpreter.next());
        if (!result) {
//            System.out.println(interpreter);
        }
//        System.out.println("interpretation finished - and result is = " + result);
        interpreter = null;
        return result;
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     --A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology01() throws Exception {
        final Element ele = BasicParser.createElement(
            "<NOT><NOT><PREDVAR id=\"A\"/></NOT></NOT>");
        // System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A v -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology02() throws Exception {
        final Element ele = BasicParser.createElement(
            "<OR><PREDVAR id=\"A\"/><NOT><PREDVAR id=\"A\"/></NOT></OR>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A n -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology03() throws Exception {
        final Element ele = BasicParser.createElement(
            "<AND><PREDVAR id=\"A\"/><NOT><PREDVAR id=\"A\"/></NOT></AND>");
        // System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A => -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology04() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><NOT><PREDVAR id=\"A\"/></NOT></IMPL>");
        // System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     -A => A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology05() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><NOT><PREDVAR id=\"A\"/></NOT><PREDVAR id=\"A\"/></IMPL>");
        // System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A => A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology06() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><PREDVAR id=\"A\"/></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology07() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><NOT><NOT><PREDVAR id=\"A\"/></NOT></NOT></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A <-> A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology08() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"/><NOT><NOT><PREDVAR id=\"A\"/></NOT></NOT></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> B v A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology09() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><OR><PREDVAR id=\"B\"/><PREDVAR id=\"A\"/></OR></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> -B v A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology10() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><OR><NOT><PREDVAR id=\"B\"/></NOT><PREDVAR id=\"A\"/></OR></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A -> -B n A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology11() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><AND><NOT><PREDVAR id=\"B\"/></NOT><PREDVAR id=\"A\"/></AND></IMPL>");
        // System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> A v -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology12() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><OR><PREDVAR id=\"A\"/><NOT><PREDVAR id=\"A\"/></NOT></OR></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A -> A v B v C v D v E v F v G v H v I v J
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology13() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/><OR><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/><PREDVAR id=\"C\"/>"
            + "<PREDVAR id=\"D\"/><PREDVAR id=\"E\"/><PREDVAR id=\"F\"/><PREDVAR id=\"G\"/>"
            + "<PREDVAR id=\"H\"/><PREDVAR id=\"I\"/><PREDVAR id=\"J\"/></OR></IMPL>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A v B <-> B v A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology14() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><OR><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></OR>"
            + "<OR><PREDVAR id=\"B\"/><PREDVAR id=\"A\"/></OR></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A n B <-> B n A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology15() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><AND><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></AND>"
            + "<AND><PREDVAR id=\"B\"/><PREDVAR id=\"A\"/></AND></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A n B <-> B v A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology16() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><AND><PREDVAR id=\"A\"/><PREDVAR id=\"B\"/></AND>"
            + "<OR><PREDVAR id=\"B\"/><PREDVAR id=\"A\"/></OR></EQUI>");
        // System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     -A n B <-> -(-B v A)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology17() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><AND><NOT><PREDVAR id=\"A\"/></NOT><PREDVAR id=\"B\"/></AND>"
            + "<NOT><OR><NOT><PREDVAR id=\"B\"/></NOT><PREDVAR id=\"A\"/></OR></NOT></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A(x,y) <-> A(x,y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology18() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR></EQUI>");
        // System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A(x,y) <-> A(y,x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology19() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"y\"/><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A(x,y) <-> A(x,x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology20() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A(y) <-> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology21() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A(x, y) <-> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology22() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A <-> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology23() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"/>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A <-> B
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology24() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI><PREDVAR id=\"A\"/>"
            + "<PREDVAR id=\"B\"/></EQUI>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A -> B
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology25() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/>"
            + "<PREDVAR id=\"B\"/></IMPL>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     A -> A(x)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology26() throws Exception {
        final Element ele = BasicParser.createElement(
            "<IMPL><PREDVAR id=\"A\"/>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></IMPL>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \forall x (A(x) -> A(x))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology27() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FORALL><VAR id=\"x\"/><IMPL><PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR></IMPL></FORALL>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     \forall x \forall y (A(x) -> A(y))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology28() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FORALL><VAR id=\"x\"/><FORALL><VAR id=\"y\"/><IMPL><PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"y\"/></PREDVAR></IMPL></FORALL></FORALL>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \forall x \forall y (A(x,y) -> A(x,y))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology29() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FORALL><VAR id=\"x\"/><FORALL><VAR id=\"y\"/><IMPL><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR></IMPL></FORALL></FORALL>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \forall x \forall A(x,y): A(x,y))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology30() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FORALL><VAR id=\"x\"/><FORALL><VAR id=\"y\"/><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR></FORALL></FORALL>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     \forall x \forall A(x,y): A(y,x))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology31() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FORALL><VAR id=\"x\"/><FORALL><VAR id=\"y\"/><PREDVAR id=\"A\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"y\"/><VAR id=\"x\"/></PREDVAR></FORALL></FORALL>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \forall x \exists y (A(x) -> A(y))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology32() throws Exception {
        final Element ele = BasicParser.createElement(
            "<FORALL><VAR id=\"x\"/><EXISTS><VAR id=\"y\"/><IMPL><PREDVAR id=\"A\"><VAR id=\"x\"/></PREDVAR>"
            + "<PREDVAR id=\"A\"><VAR id=\"y\"/></PREDVAR></IMPL></EXISTS></FORALL>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \exists! y (x = y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology33() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EXISTSU><VAR id=\"y\"/><PREDCON id=\"l.equal\"><VAR id=\"x\"/><VAR id=\"y\"/></PREDCON>"
            + "</EXISTSU>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     \exists! y (y = y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology34() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EXISTSU><VAR id=\"y\"/><PREDCON id=\"l.equal\"><VAR id=\"y\"/><VAR id=\"y\"/></PREDCON>"
            + "</EXISTSU>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \exists y (y = y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology35() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EXISTS><VAR id=\"y\"/><PREDCON id=\"equal\"><VAR id=\"y\"/><VAR id=\"y\"/></PREDCON>"
            + "</EXISTS>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     f(y) = f(y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology36() throws Exception {
        final Element ele = BasicParser.createElement(
            "<PREDCON id=\"equal\"><FUNVAR id=\"f\"><VAR id=\"y\"/></FUNVAR>"
            + "<FUNVAR id=\"f\"><VAR id=\"y\"/></FUNVAR></PREDCON>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     f(x) = f(y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology37() throws Exception {
        final Element ele = BasicParser.createElement(
            "<PREDCON id=\"equal\"><FUNVAR id=\"f\"><VAR id=\"x\"/></FUNVAR>"
            + "<FUNVAR id=\"f\"><VAR id=\"y\"/></FUNVAR></PREDCON>");
//        System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     \exists y f(x) = f(y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology38() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EXISTS><VAR id=\"y\"/><PREDCON id=\"equal\"><FUNVAR id=\"f\"><VAR id=\"x\"/></FUNVAR>"
            + "<FUNVAR id=\"f\"><VAR id=\"y\"/></FUNVAR></PREDCON></EXISTS>");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     see qedeq_logic_v1.xml theorem:propositionalCalculus from 2010-09-26
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology39() throws Exception {
        final Element ele = BasicParser.createElement(
            "\n            <AND>\n" +
            "\n" +
            "\n                <PREDCON ref=\"TRUE\" />\n" +
            "\n" +
            "\n                <NOT>\n" +
            "\n                  <PREDCON ref=\"FALSE\" />\n" +
            "\n                </NOT>" +
            "\n" +
            "\n                <IMPL>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                </IMPL>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <OR>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </OR>" +
            "\n                  <OR>" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </OR>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </AND>" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </AND>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <IMPL>" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </AND>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                </IMPL>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </EQUI>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <OR>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <OR>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </OR>" +
            "\n                  </OR>" +
            "\n                  <OR>" +
            "\n                    <OR>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </OR>" +
            "\n                    <PREDVAR id=\"C\" />" +
            "\n                  </OR>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </AND>" +
            "\n                  </AND>" +
            "\n                  <AND>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </AND>" +
            "\n                    <PREDVAR id=\"C\" />" +
            "\n                  </AND>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                  <OR>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </OR>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </AND>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                  <NOT>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                    </NOT>" +
            "\n                  </NOT>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </IMPL>" +
            "\n                  <IMPL>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </NOT>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                    </NOT>" +
            "\n                  </IMPL>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                    </NOT>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </NOT>" +
            "\n                  </EQUI>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <IMPL>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </IMPL>" +
            "\n                  </IMPL>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                    <IMPL>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </IMPL>" +
            "\n                  </IMPL>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <NOT>" +
            "\n                    <OR>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </OR>" +
            "\n                  </NOT>" +
            "\n                  <AND>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                    </NOT>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </NOT>" +
            "\n                  </AND>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <NOT>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </AND>" +
            "\n                  </NOT>" +
            "\n                  <OR>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                    </NOT>" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </NOT>" +
            "\n                  </OR>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <OR>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </AND>" +
            "\n                  </OR>" +
            "\n                  <AND>" +
            "\n                    <OR>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </OR>" +
            "\n                    <OR>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </OR>" +
            "\n                  </AND>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <OR>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </OR>" +
            "\n                  </AND>" +
            "\n                  <OR>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </AND>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </AND>" +
            "\n                  </OR>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDCON ref=\"TRUE\" />" +
            "\n                  </AND>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDCON ref=\"FALSE\" />" +
            "\n                  </AND>" +
            "\n                  <PREDCON ref=\"FALSE\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <OR>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDCON ref=\"TRUE\" />" +
            "\n                  </OR>" +
            "\n                  <PREDCON ref=\"TRUE\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <OR>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDCON ref=\"FALSE\" />" +
            "\n                  </OR>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <OR>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                    </NOT>" +
            "\n                  </OR>" +
            "\n                  <PREDCON ref=\"TRUE\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <AND>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <NOT>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                    </NOT>" +
            "\n                  </AND>" +
            "\n                  <PREDCON ref=\"FALSE\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <IMPL>" +
            "\n                    <PREDCON ref=\"TRUE\" />" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </IMPL>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <IMPL>" +
            "\n                    <PREDCON ref=\"FALSE\" />" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </IMPL>" +
            "\n                  <PREDCON ref=\"TRUE\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDCON ref=\"FALSE\" />" +
            "\n                  </IMPL>" +
            "\n                  <NOT>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </NOT>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDCON ref=\"TRUE\" />" +
            "\n                  </IMPL>" +
            "\n                  <PREDCON ref=\"TRUE\" />" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDCON ref=\"TRUE\" />" +
            "\n                  </EQUI>" +
            "\n                  <PREDVAR id=\"A\" />" +
            "\n                </EQUI>" +
            "\n                " +
            "\n                <IMPL>" +
            "\n                  <AND>" +
            "\n                    <IMPL>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </IMPL>" +
            "\n                    <IMPL>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </IMPL>" +
            "\n                  </AND>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"C\" />" +
            "\n                  </IMPL>" +
            "\n                </IMPL>" +
            "\n" +
            "\n                <IMPL>" +
            "\n                  <AND>" +
            "\n                    <EQUI>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </EQUI>" +
            "\n                    <EQUI>" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </EQUI>" +
            "\n                  </AND>" +
            "\n                  <EQUI>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"C\" />" +
            "\n                  </EQUI>" +
            "\n                </IMPL>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </AND>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </AND>" +
            "\n                  </EQUI>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <EQUI>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </EQUI>" +
            "\n                  </IMPL>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </AND>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <NOT>" +
            "\n                        <PREDVAR id=\"B\" />" +
            "\n                      </NOT>" +
            "\n                    </AND>" +
            "\n                  </EQUI>" +
            "\n                  <NOT>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                  </NOT>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                    </AND>" +
            "\n                  </EQUI>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </IMPL>" +
            "\n                </EQUI>" +
            "\n" +
            "\n                <IMPL>" +
            "\n                  <IMPL>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </IMPL>" +
            "\n                  <IMPL>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </AND>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </AND>" +
            "\n                  </IMPL>" +
            "\n                </IMPL>" +
            "\n" +
            "\n                <IMPL>" +
            "\n                  <EQUI>" +
            "\n                    <PREDVAR id=\"A\" />" +
            "\n                    <PREDVAR id=\"B\" />" +
            "\n                  </EQUI>" +
            "\n                  <EQUI>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"A\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </AND>" +
            "\n                    <AND>" +
            "\n                      <PREDVAR id=\"B\" />" +
            "\n                      <PREDVAR id=\"C\" />" +
            "\n                    </AND>" +
            "\n                  </EQUI>" +
            "\n                </IMPL>" +
            "\n" +
            "\n              </AND>" +
            "\n");
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     f(y) = f(y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology40() throws Exception {
        final Element ele = BasicParser.createElement(
            "                <AND>"
            + "\n                "
            + "\n                <IMPL>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <IMPL>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </IMPL>"
            + "\n                  </FORALL>"
            + "\n                  <IMPL>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                  </IMPL>"
            + "\n                </IMPL>"
            + "\n"
            + "\n                <IMPL>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <IMPL>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </IMPL>"
            + "\n                  </FORALL>"
            + "\n                  <IMPL>"
            + "\n                    <EXISTS>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EXISTS>"
            + "\n                    <EXISTS>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EXISTS>"
            + "\n                  </IMPL>"
            + "\n                </IMPL>"
            + "\n"
            + "\n                <IMPL>"
            + "\n                  <EXISTS>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <AND>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </AND>"
            + "\n                  </EXISTS>"
            + "\n                  <AND>"
            + "\n                    <EXISTS>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EXISTS>"
            + "\n                    <EXISTS>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EXISTS>"
            + "\n                  </AND>"
            + "\n                </IMPL>"
            + "\n"
            + "\n                <IMPL>"
            + "\n                  <OR>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                  </OR>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <OR>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </OR>"
            + "\n                  </FORALL>"
            + "\n                </IMPL>"
            + "\n"
            + "\n                <EQUI>"
            + "\n                  <EXISTS>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <OR>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </OR>"
            + "\n                  </EXISTS>"
            + "\n                  <OR>"
            + "\n                    <EXISTS>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EXISTS>"
            + "\n                    <EXISTS>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EXISTS>"
            + "\n                  </OR>"
            + "\n                </EQUI>"
            + "\n"
            + "\n                <EQUI>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <AND>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </AND>"
            + "\n                  </FORALL>"
            + "\n                  <AND>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                  </AND>"
            + "\n                </EQUI>"
            + "\n"
            + "\n                <EQUI>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"y\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                        <VAR id=\"y\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                  </FORALL>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"y\" />"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                        <VAR id=\"y\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                  </FORALL>"
            + "\n                </EQUI>"
            + "\n"
            + "\n                <EQUI>"
            + "\n                  <EXISTS>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <EXISTS>"
            + "\n                      <VAR id=\"y\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                        <VAR id=\"y\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EXISTS>"
            + "\n                  </EXISTS>"
            + "\n                  <EXISTS>"
            + "\n                    <VAR id=\"y\" />"
            + "\n                    <EXISTS>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                        <VAR id=\"y\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EXISTS>"
            + "\n                  </EXISTS>"
            + "\n                </EQUI>"
            + "\n"
            + "\n                <IMPL>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <IMPL>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"A\" />"
            + "\n                    </IMPL>"
            + "\n                  </FORALL>"
            + "\n                  <IMPL>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                    <PREDVAR id=\"A\" />"
            + "\n                  </IMPL>"
            + "\n                </IMPL>"
            + "\n"
            + "\n                <EQUI>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <IMPL>"
            + "\n                      <PREDVAR id=\"A\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </IMPL>"
            + "\n                  </FORALL>"
            + "\n                  <IMPL>"
            + "\n                    <PREDVAR id=\"A\" />"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                  </IMPL>"
            + "\n                </EQUI>"
            + "\n"
            + "\n                <EQUI>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <AND>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"A\" />"
            + "\n                    </AND>"
            + "\n                  </FORALL>"
            + "\n                  <AND>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                    <PREDVAR id=\"A\" />"
            + "\n                  </AND>"
            + "\n                </EQUI>"
            + "\n"
            + "\n                <EQUI>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <OR>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"A\" />"
            + "\n                    </OR>"
            + "\n                  </FORALL>"
            + "\n                  <OR>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                    <PREDVAR id=\"A\" />"
            + "\n                  </OR>"
            + "\n                </EQUI>"
            + "\n"
            + "\n                <IMPL>"
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
            + "\n                </IMPL>"
            + "\n"
            + "\n                <IMPL>"
            + "\n                  <FORALL>"
            + "\n                    <VAR id=\"x\" />"
            + "\n                    <EQUI>"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </EQUI>"
            + "\n                  </FORALL>"
            + "\n                  <EQUI>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\phi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                    <FORALL>"
            + "\n                      <VAR id=\"x\" />"
            + "\n                      <PREDVAR id=\"\\psi\">"
            + "\n                        <VAR id=\"x\" />"
            + "\n                      </PREDVAR>"
            + "\n                    </FORALL>"
            + "\n                  </EQUI>"
            + "\n                </IMPL>"
            + "\n"
            + "\n              </AND>"
            + "\n"
            );
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     negative
     * Data:     \forall x (\phi(x) <-> A) <-> (\forall x (\phi(x)) <-> A)
     *           \forall x ( 2 | x) <-> F) <-> (\forall x (2 | x)) <-> F)
     *           f                                  t
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
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \forall x (\phi(x) -> A) -> (\forall x (\phi(x)) -> A)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology42() throws Exception {
        final Element ele = BasicParser.createElement(
              "\n                <IMPL>"
              + "\n                  <FORALL>"
              + "\n                    <VAR id=\"x\" />"
              + "\n                    <IMPL>"
              + "\n                      <PREDVAR id=\"\\phi\">"
              + "\n                        <VAR id=\"x\" />"
              + "\n                      </PREDVAR>"
              + "\n                      <PREDVAR id=\"A\" />"
              + "\n                    </IMPL>"
              + "\n                  </FORALL>"
              + "\n                  <IMPL>"
              + "\n                    <FORALL>"
              + "\n                      <VAR id=\"x\" />"
              + "\n                      <PREDVAR id=\"\\phi\">"
              + "\n                        <VAR id=\"x\" />"
              + "\n                      </PREDVAR>"
              + "\n                    </FORALL>"
              + "\n                    <PREDVAR id=\"A\" />"
              + "\n                  </IMPL>"
              + "\n                </IMPL>"

        );
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \forall z (z \in x <-> z \in y) -> x = y)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology43() throws Exception {
        final Element ele = BasicParser.createElement(
            "                <IMPL>\n"
            + "                <FORALL>\n"
            + "                  <VAR id=\"z\"/>\n"
            + "                  <EQUI>\n"
            + "                    <PREDCON ref=\"in\">\n"
            + "                      <VAR id=\"z\"/>\n"
            + "                      <VAR id=\"x\"/>\n"
            + "                    </PREDCON>\n"
            + "                    <PREDCON ref=\"in\">\n"
            + "                      <VAR id=\"z\"/>\n"
            + "                      <VAR id=\"y\"/>\n"
            + "                    </PREDCON>\n"
            + "                  </EQUI>\n"
            + "                </FORALL>\n"
            + "                <PREDCON ref=\"l.equal\">\n"
            + "                  <VAR id=\"x\"/>\n"
            + "                  <VAR id=\"y\"/>\n"
            + "                </PREDCON>\n"
            + "              </IMPL>\n"
        );
//        System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
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
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     C(x) = (z | z \nin x}
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology45() throws Exception {
        final Element ele = BasicParser.createElement(
            "<PREDCON id=\"equal\">\n"
            + "  <FUNCON id=\"complement\">\n"
            + "    <VAR id=\"x\" />\n"
            + "  </FUNCON>\n"
            + "  <CLASS>\n"
            + "   <VAR id=\"z\" />\n"
            + "    <PREDCON ref=\"notIn\">\n"
            + "      <VAR id=\"z\" />\n"
            + "      <VAR id=\"x\" />\n"
            + "    </PREDCON>\n"
            + "  </CLASS>\n"
            + "</PREDCON>\n"
        );
//      System.out.println(ele.toString());
      assertTrue(isTautology(ele));
  }

    /**
     * Function: isTautology(Element)
     * Type:     positive
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
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
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
      assertTrue(isTautology(ele));
  }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     A v -A
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology50() throws Exception {
        final Element ele = BasicParser.createElement(
        "<OR>\n"
        + "  <PREDVAR id=\"A\"/>\n"
        + "  <NOT>\n"
        + "    <PREDVAR id=\"A\"/>\n"
        + "  </NOT>\n"
        +"</OR>\n");
        //System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     TRUE <-> (A v -A)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology51() throws Exception {
        final Element ele = BasicParser.createElement(
            "<EQUI>\n"
            + "  <PREDCON id=\"l.TRUE\"/>\n"
            + "  <OR>\n"
            + "    <PREDVAR id=\"A\"/>\n"
            + "    <NOT>\n"
            + "      <PREDVAR id=\"A\"/>\n"
            + "    </NOT>\n"
            + "  </OR>\n"
            + "</EQUI>\n");
        //System.out.println(ele.toString());
        assertTrue(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     \forall x (\phi(x) -> A) -> (\forall x (\phi(x)) -> A)
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology52() throws Exception {
        final Element formula = BasicParser.createElement(
            "<EQUI>\n"
            + "  <PREDCON id=\"TRUE\"/>\n"
            + "  <OR>\n"
            + "    <PREDVAR id=\"A\"/>\n"
            + "    <NOT>\n"
            + "      <PREDVAR id=\"A\"/>\n"
            + "    </NOT>\n"
            + "  </OR>\n"
            + "</EQUI>\n");
        assertTrue(isTautology(formula));
    }


    /**
     * Function: isTautology(Element)
     * Type:     positive
     * Data:     see theorem:powerProposition in qedeq_set_theory_v1.xml
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology53() throws Exception {
        final Element formula = BasicParser.createElement(
                "              <AND>"
                + "                <EQUI>"
                + "                  <PREDCON ref=\"in\">"
                + "                    <VAR id=\"z\" />"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"x\" />"
                + "                    </FUNCON>"
                + "                  </PREDCON>"
                + "                  <AND>"
                + "                    <PREDCON ref=\"isSet\">"
                + "                      <VAR id=\"z\" />"
                + "                    </PREDCON>"
                + "                    <PREDCON ref=\"subclass\">"
                + "                      <VAR id=\"z\" />"
                + "                      <VAR id=\"x\" />"
                + "                    </PREDCON>"
                + "                  </AND>"
                + "                </EQUI>"
                + "                <PREDCON ref=\"l.equal\">"
                + "                  <FUNCON ref=\"power\">"
                + "                    <FUNCON ref=\"universalClass\" />"
                + "                  </FUNCON>"
                + "                  <FUNCON ref=\"universalClass\" />"
                + "                </PREDCON>"
                + "                <PREDCON ref=\"l.equal\">"
                + "                  <FUNCON ref=\"power\">"
                + "                    <FUNCON ref=\"emptySet\" />"
                + "                  </FUNCON>"
                + "                  <FUNCON ref=\"classList\">"
                + "                    <FUNCON ref=\"emptySet\" />"
                + "                  </FUNCON>"
                + "                </PREDCON>"
                + "                <EQUI>"
                + "                  <PREDCON ref=\"isSet\">"
                + "                    <VAR id=\"x\" />"
                + "                  </PREDCON>"
                + "                  <PREDCON ref=\"in\">"
                + "                    <VAR id=\"x\" />"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"x\" />"
                + "                    </FUNCON>"
                + "                  </PREDCON>"
                + "                </EQUI>"
                + "                <IMPL>"
                + "                  <PREDCON ref=\"subclass\">"
                + "                    <VAR id=\"x\" />"
                + "                    <VAR id=\"y\" />"
                + "                  </PREDCON>"
                + "                  <PREDCON ref=\"subclass\">"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"x\" />"
                + "                    </FUNCON>"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"y\" />"
                + "                    </FUNCON>"
                + "                  </PREDCON>"
                + "                </IMPL>"
                + "                <IMPL>"
                + "                  <AND>"
                + "                    <PREDCON ref=\"isSet\">"
                + "                      <VAR id=\"x\" />"
                + "                    </PREDCON>"
                + "                    <PREDCON ref=\"subclass\">"
                + "                      <FUNCON ref=\"power\">"
                + "                        <VAR id=\"x\" />"
                + "                      </FUNCON>"
                + "                      <FUNCON ref=\"power\">"
                + "                        <VAR id=\"y\" />"
                + "                      </FUNCON>"
                + "                    </PREDCON>"
                + "                  </AND>"
                + "                  <PREDCON ref=\"subclass\">"
                + "                    <VAR id=\"x\" />"
                + "                    <VAR id=\"y\" />"
                + "                  </PREDCON>"
                + "                </IMPL>"
                + "                <PREDCON ref=\"l.equal\">"
                + "                  <FUNCON ref=\"power\">"
                + "                    <FUNCON ref=\"intersection\">"
                + "                      <VAR id=\"x\" />"
                + "                      <VAR id=\"y\" />"
                + "                    </FUNCON>"
                + "                  </FUNCON>"
                + "                  <FUNCON ref=\"intersection\">"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"x\" />"
                + "                    </FUNCON>"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"y\" />"
                + "                    </FUNCON>"
                + "                  </FUNCON>"
                + "                </PREDCON>"
                + "                <PREDCON ref=\"subclass\">"
                + "                  <FUNCON ref=\"union\">"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"x\" />"
                + "                    </FUNCON>"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"y\" />"
                + "                    </FUNCON>"
                + "                  </FUNCON>"
                + "                  <FUNCON ref=\"power\">"
                + "                    <FUNCON ref=\"union\">"
                + "                      <VAR id=\"x\" />"
                + "                      <VAR id=\"y\" />"
                + "                    </FUNCON>"
                + "                  </FUNCON>"
                + "                </PREDCON>"
                + "                <PREDCON ref=\"subclass\">"
                + "                  <VAR id=\"x\" />"
                + "                  <FUNCON ref=\"power\">"
                + "                    <FUNCON ref=\"setSum\">"
                + "                      <VAR id=\"x\" />"
                + "                    </FUNCON>"
                + "                  </FUNCON>"
                + "                </PREDCON>"
                + "                <PREDCON ref=\"subclass\">"
                + "                  <FUNCON ref=\"setSum\">"
                + "                    <FUNCON ref=\"power\">"
                + "                      <VAR id=\"x\" />"
                + "                    </FUNCON>"
                + "                  </FUNCON>"
                + "                  <VAR id=\"x\" />"
                + "                </PREDCON>"
                + "              </AND>"
                );
        assertTrue(isTautology(formula));
    }


}
