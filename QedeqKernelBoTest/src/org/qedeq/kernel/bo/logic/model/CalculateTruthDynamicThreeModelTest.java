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

package org.qedeq.kernel.bo.logic.model;

import java.io.File;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.bo.test.TestParser;
import org.qedeq.kernel.common.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.dto.module.VariableListVo;


/**
 * Testing the truth calculation with default model.
 *
 * @author  Michael Meyling
 */
public class CalculateTruthDynamicThreeModelTest extends CalculateTruthTestCase {

    private DynamicInterpreter interpreter;

    /**
     * Constructor.
     */
    public CalculateTruthDynamicThreeModelTest() {
        super(new ThreeDynamicModel());
    }

    public void setUp() throws Exception {
        super.setUp();
        final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
                IoUtility.toUrl(new File(getDocDir(), "math/qedeq_set_theory_v1.xml")));
        final KernelQedeqBo prop = (KernelQedeqBo) KernelFacade.getKernelContext().loadModule(
            address);
        interpreter = new DynamicInterpreter((DynamicModel) getModel(), prop);
    }

    /**
     * Function: isTautology(Element)
     * Type:     false (in this model)
     * Data:     \exists x \all y (y \in x <-> (isSet(y) n \phi(y)))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology44b() throws Exception {
        final Element def = TestParser.createElement(
             "   <EXISTS>\n"
             + "   <VAR id=\"y\"/>\n"
             + "   <PREDCON ref=\"in\">\n"
             + "     <VAR id=\"x\"/>\n"
             + "     <VAR id=\"y\"/>\n"
             + "   </PREDCON>\n"
             + " </EXISTS>\n");
        VariableListVo variables = new VariableListVo();
        final Element x = TestParser.createElement(
                "<VAR id=\"x\"/>\n");
        final Element y = TestParser.createElement(
            "<VAR id=\"x\"/>\n");
        variables.add(x);
        variables.add(y);
        interpreter.addPredicateConstant(new PredicateConstant("isSet", 1), variables, def.getList());
        final Element ele = TestParser.createElement(
                "    <EXISTS>\n"
                + "    <VAR id=\"x\"/>\n"
                + "    <FORALL>\n"
                + "      <VAR id=\"y\"/>\n"
                + "      <EQUI>\n"
                + "        <PREDCON ref=\"in\">\n"
                + "          <VAR id=\"y\"/>\n"
                + "          <VAR id=\"x\"/>\n"
                + "        </PREDCON>\n"
                + "        <AND>\n"
                + "          <PREDCON ref=\"isSet\">\n"
                + "            <VAR id=\"y\"/>\n"
                + "          </PREDCON>\n"
                + "          <PREDVAR id=\"\\phi\">\n"
                + "            <VAR id=\"y\"/>\n"
                + "          </PREDVAR>\n"
                + "        </AND>\n"
                + "      </EQUI>\n"
                + "    </FORALL>\n"
                + "  </EXISTS>\n"
        );
//      System.out.println(ele.toString());
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     false (in this model)
     * Data:     (y \in {x | \phi(x)} <-> (isSet(y) n \phi(y))
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology44() throws Exception {
        final Element def = TestParser.createElement(
             "   <EXISTS>\n"
             + "   <VAR id=\"y\"/>\n"
             + "   <PREDCON ref=\"in\">\n"
             + "     <VAR id=\"x\"/>\n"
             + "     <VAR id=\"y\"/>\n"
             + "   </PREDCON>\n"
             + " </EXISTS>\n");
        VariableListVo variables = new VariableListVo();
        final Element x = TestParser.createElement(
                "<VAR id=\"x\"/>\n");
        final Element y = TestParser.createElement(
            "<VAR id=\"x\"/>\n");
        variables.add(x);
        variables.add(y);
        interpreter.addPredicateConstant(new PredicateConstant("isSet", 1), variables, def.getList());
        final Element ele = TestParser.createElement(
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
        Trace.setTraceOn(true);
        assertFalse(isTautology(ele));
    }

    /**
     * Function: isTautology(Element)
     * Type:     not tested
     * Data:     see below
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology45() throws Exception {
        // ignore
    }

    /**
     * Function: isTautology(Element)
     * Type:     not tested
     * Data:     x <= y <-> x n C(y) = 0
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology46() throws Exception {
        // ignore
    }

    /**
     * Function: isTautology(Element)
     * Type:     not tested
     * Data:     see below
     *
     * @throws  Exception   Test failed.
     */
    public void testTautology47() throws Exception {
        // ignore
    }

    protected boolean isTautology(final Element formula) throws HeuristicException {
        boolean result = true;
        ModuleContext context = new ModuleContext(new DefaultModuleAddress());
        try {
            do {
//                System.out.println(interpreter.toString());
//                if (interpreter.toString().indexOf("subject variables {y={{}}}") >= 0
//                if (interpreter.toString().indexOf("predicate variables {\\phi(*)=={{}}}") >= 0) {
//                    Trace.setTraceOn(true);
//                    System.out.println("Know!");
//                }
                result &= interpreter.calculateValue(new ModuleContext(context), formula);
//                if (interpreter.toString().indexOf("predicate variables {\\phi(*)=={{}}}") >= 0) {
//                    Trace.setTraceOn(false);
//                }
            } while (result && interpreter.next());
//            if (!result) {
//                System.out.println(interpreter);
//            }
//            System.out.println("interpretation finished - and result is = " + result);
        } finally {
            interpreter.clearVariables();
        }
        return result;
    }

}
