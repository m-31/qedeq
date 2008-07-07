/* $Id: FormulaCheckerTest.java,v 1.9 2008/05/15 21:27:30 m31 Exp $
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



import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.bo.logic.FormulaChecker;
import org.qedeq.kernel.bo.service.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleContext;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 *
 * @version $Revision: 1.9 $
 * @author  Michael Meyling
 */
public class FormulaCheckerTest extends AbstractFormulaChecker {

    /** This class. */
    private static final Class CLASS = FormulaCheckerTest.class;

    private ModuleContext context;
    
    protected void setUp() throws Exception {
        context = new ModuleContext(new DefaultModuleAddress("http://memory.org/sample.xml"), 
            "getElement()");
    }

    protected void tearDown() throws Exception {
        context = null;
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     A & A.
     * 
     * @throws  Exception   Test failed.
     */
    public void testPositive1() throws Exception {
        final Element ele = TestParser.createElement("<AND><PREDVAR id=\"A\"/><PREDVAR id=\"A\"/></AND>");
        Trace.param(CLASS, this, "testPositive1", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getChecker()).hasErrors());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     (all z (isSet(z) -> (z in x <-> z in y))) -> x = y.
     * 
     * @throws  Exception   Test failed.
     */
    public void testPositive2() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <IMPL>"
            + "      <PREDCON ref=\"isSet\">"
            + "        <VAR id=\"z\"/>"
            + "      </PREDCON>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </IMPL>"
            + "  </FORALL>"
            + "  <PREDCON ref=\"equal\">"
            + "    <VAR id=\"x\"/>"
            + "    <VAR id=\"y\"/>"
            + "  </PREDCON>"
            + "</IMPL>");
        Trace.param(CLASS, this, "testPositive2", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     (all isSet(z) (z in x <-> z in y)) -> x = y.
     * 
     * @throws  Exception   Test failed.
     */
    public void testPositive3() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <PREDCON ref=\"isSet\">"
            + "      <VAR id=\"z\"/>"
            + "    </PREDCON>"
            + "    <EQUI>"
            + "      <PREDCON ref=\"in\">"
            + "        <VAR id=\"z\"/>"
            + "        <VAR id=\"x\"/>"
            + "      </PREDCON>"
            + "      <PREDCON ref=\"in\">"
            + "        <VAR id=\"z\"/>"
            + "        <VAR id=\"y\"/>"
            + "      </PREDCON>"
            + "    </EQUI>"
            + "  </FORALL>"
            + "  <PREDCON ref=\"equal\">"
            + "    <VAR id=\"x\"/>"
            + "    <VAR id=\"y\"/>"
            + "  </PREDCON>"
            + "</IMPL>");
        Trace.param(CLASS, this, "testPositive3", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     (all z (all t isSet(t) (z in x <-> z in y))) -> x = y.
     * 
     * @throws  Exception   Test failed.
     */
    public void testPositive4() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"t\"/>"
            + "      <PREDCON ref=\"isSet\">"
            + "        <VAR id=\"t\"/>"
            + "      </PREDCON>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "  <PREDCON ref=\"equal\">"
            + "    <VAR id=\"x\"/>"
            + "    <VAR id=\"y\"/>"
            + "  </PREDCON>"
            + "</IMPL>");
        Trace.param(CLASS, this, "testPositive4", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     (all z (all t (z in x <-> z in y))) -> (all z (all t (z in x <-> z in y)))
     * 
     * @throws  Exception   Test failed.
     */
    public void testPositive5() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"t\"/>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"t\"/>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "</IMPL>");
        Trace.param(CLASS, this, "testPositive5", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     (all z (all t (z in P(x) <-> z in y))) -> (all z (all t (z in x <-> z in y)))
     * 
     * @throws  Exception   Test failed.
     */
    public void testPositive6() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"t\"/>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <FUNCON ref=\"power\">"
            + "            <VAR id=\"x\"/>"
            + "          </FUNCON>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"t\"/>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "</IMPL>");
        Trace.param(CLASS, this, "testPositive6", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getChecker()).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass())
            .hasErrors());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     y = [x | phi(x)} <-> all z (z in y <-> z in {x | phi(x)})
     *  
     * @throws  Exception   Test failed.
     */
    public void testPositive7() throws Exception {
        final Element ele = TestParser.createElement(
            "            <EQUI>"
            + "              <PREDCON ref=\"equal\">"
            + "                <VAR id=\"y\"/>"
            + "                <CLASS>"
            + "                  <VAR id=\"x\"/>"
            + "                  <PREDVAR id=\"\\phi\">"
            + "                    <VAR id=\"x\"/>"
            + "                  </PREDVAR>"
            + "                </CLASS>"
            + "              </PREDCON>"
            + "              <FORALL>"
            + "                <VAR id=\"z\"/>"
            + "                <EQUI>"
            + "                  <PREDCON ref=\"in\">"
            + "                    <VAR id=\"z\"/>"
            + "                    <VAR id=\"y\"/>"
            + "                  </PREDCON>"
            + "                  <PREDCON ref=\"in\">"
            + "                    <VAR id=\"z\"/>"
            + "                    <CLASS>"
            + "                      <VAR id=\"x\"/>"
            + "                      <PREDVAR id=\"\\phi\">"
            + "                        <VAR id=\"x\"/>"
            + "                      </PREDVAR>"
            + "                    </CLASS>"
            + "                  </PREDCON>"
            + "                </EQUI>"
            + "              </FORALL>"
            + "            </EQUI>"
        );
        Trace.param(CLASS, this, "testPositive7", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getChecker()).hasErrors());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     all y (all z (phi(z) <-> z in y) -> y = {z | phi(z)})
     * 
     * @throws  Exception   Test failed.
     */
    public void testPositive8() throws Exception {
        final Element ele = TestParser.createElement(
              "<FORALL>"
            + "  <VAR id=\"y\"/>"
            + "  <IMPL>"
            + "    <FORALL>"
            + "      <VAR id=\"z\"/>"
            + "      <EQUI>"
            + "        <PREDVAR ref=\"phi\">"
            + "          <VAR id=\"z\"/>"
            + "        </PREDVAR>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "    <PREDCON ref=\"equal\">"
            + "      <VAR id=\"y\"/>"
            + "      <CLASS>"
            + "        <VAR id=\"z\"/>"
            + "        <PREDVAR ref=\"phi\">"
            + "          <VAR id=\"z\"/>"
            + "        </PREDVAR>"
            + "      </CLASS>"
            + "    </PREDCON>"
            + "  </IMPL>"
            + "</FORALL>");
        Trace.param(CLASS, this, "testPositive8", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
        assertFalse(FormulaChecker.checkFormula(ele, context, getChecker()).hasErrors());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550
     * Data:     (all z (all z isSet(z) (z in x <-> z in y))) -> x = y.
     * Reason:   z is quantifier variable for two times 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative1() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"z\"/>"
            + "      <PREDCON ref=\"isSet\">"
            + "        <VAR id=\"z\"/>"
            + "      </PREDCON>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "  <PREDCON ref=\"equal\">"
            + "    <VAR id=\"x\"/>"
            + "    <VAR id=\"y\"/>"
            + "  </PREDCON>"
            + "</IMPL>");
        Trace.param(CLASS, this, "testNegative1", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30550, list.get(0).getErrorCode());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     (all z (all t isSet(z) (z in x <-> z in y))) -> x = y.
     *           Restriction formula for t contains no t but that is ok.
     * 
     * @throws  Exception   Test failed.
     */
    public void testPositive9() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"t\"/>"
            + "      <PREDCON ref=\"isSet\">"
            + "        <VAR id=\"z\"/>"
            + "      </PREDCON>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "  <PREDCON ref=\"equal\">"
            + "    <VAR id=\"x\"/>"
            + "    <VAR id=\"y\"/>"
            + "  </PREDCON>"
            + "</IMPL>");
        Trace.param(CLASS, this, "testPositive9", "ele", ele);
        assertFalse(FormulaChecker.checkFormula(ele, context).hasErrors());
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780
     * Data:     (all z (all x (z in x <-> z in y))) -> (all z (all t (z in x <-> z in y)))
     * Reason:   x occurs non free and free 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative3() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"x\"/>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "  <FORALL>"
            + "    <VAR id=\"z\"/>"
            + "    <FORALL>"
            + "      <VAR id=\"t\"/>"
            + "      <EQUI>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"x\"/>"
            + "        </PREDCON>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "  </FORALL>"
            + "</IMPL>");
        Trace.param(CLASS, this, "testNegative3", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }
     
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780
     * Data:     (all x (all y (all z (z in x <-> z in y)) -> (z = y)))
     * Reason:   z occurs non free and free 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative4() throws Exception {
        final Element ele = TestParser.createElement(
              "<FORALL>"
            + "  <VAR id=\"x\"/>"
            + "  <FORALL>"
            + "    <VAR id=\"y\"/>"
            + "    <IMPL>"
            + "      <FORALL>"
            + "        <VAR id=\"z\"/>"
            + "        <EQUI>"
            + "          <PREDCON ref=\"in\">"
            + "            <VAR id=\"z\"/>"
            + "            <VAR id=\"x\"/>"
            + "          </PREDCON>"
            + "          <PREDCON ref=\"in\">"
            + "            <VAR id=\"z\"/>"
            + "            <VAR id=\"y\"/>"
            + "          </PREDCON>"
            + "        </EQUI>"
            + "      </FORALL>"
            + "      <PREDCON ref=\"equal\">"
            + "        <VAR id=\"z\"/>"
            + "        <VAR id=\"y\"/>"
            + "      </PREDCON>"
            + "    </IMPL>"
            + "  </FORALL>"
            + "</FORALL>"
        );
        Trace.param(CLASS, this, "testNegative4", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30780
     * Data:     (all x (all y (all z (z in x <-> z in y))) -> (x = y))
     * Reason:   y occurs non free and free 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative5() throws Exception {
        final Element ele = TestParser.createElement(
              "<FORALL>"
            + "  <VAR id=\"x\"/>"
            + "  <IMPL>"
            + "    <FORALL>"
            + "    <VAR id=\"y\"/>"
            + "      <FORALL>"
            + "        <VAR id=\"z\"/>"
            + "        <EQUI>"
            + "          <PREDCON ref=\"in\">"
            + "            <VAR id=\"z\"/>"
            + "            <VAR id=\"x\"/>"
            + "          </PREDCON>"
            + "          <PREDCON ref=\"in\">"
            + "            <VAR id=\"z\"/>"
            + "            <VAR id=\"y\"/>"
            + "          </PREDCON>"
            + "        </EQUI>"
            + "      </FORALL>"
            + "    </FORALL>"
            + "    <PREDCON ref=\"equal\">"
            + "      <VAR id=\"x\"/>"
            + "      <VAR id=\"y\"/>"
            + "    </PREDCON>"
            + "  </IMPL>"
            + "</FORALL>"
        );
        Trace.param(CLASS, this, "testNegative5", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30780, list.get(0).getErrorCode());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30770
     * Data:     (all x ((x = y) -> all y (all z (z in x <-> z in y))))
     * Reason:   y occurs free and non free 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative6() throws Exception {
        final Element ele = TestParser.createElement(
              "<FORALL>"
            + "  <VAR id=\"x\"/>"
            + "  <IMPL>"
            + "    <PREDCON ref=\"equal\">"
            + "      <VAR id=\"x\"/>"
            + "      <VAR id=\"y\"/>"
            + "    </PREDCON>"
            + "    <FORALL>"
            + "      <VAR id=\"y\"/>"
            + "      <FORALL>"
            + "        <VAR id=\"z\"/>"
            + "        <EQUI>"
            + "          <PREDCON ref=\"in\">"
            + "            <VAR id=\"z\"/>"
            + "            <VAR id=\"x\"/>"
            + "          </PREDCON>"
            + "          <PREDCON ref=\"in\">"
            + "            <VAR id=\"z\"/>"
            + "            <VAR id=\"y\"/>"
            + "          </PREDCON>"
            + "        </EQUI>"
            + "      </FORALL>"
            + "    </FORALL>"
            + "  </IMPL>"
            + "</FORALL>"
        );
        Trace.param(CLASS, this, "testNegative6", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30770, list.get(0).getErrorCode());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30740
     * Data:     (x = y) -> 
     * Reason:   second operand missing 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative7() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <PREDCON ref=\"equal\">"
            + "    <VAR id=\"x\"/>"
            + "    <VAR id=\"y\"/>"
            + "  </PREDCON>"
            + "</IMPL>"
        );
        Trace.param(CLASS, this, "testNegative7", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30740, list.get(0).getErrorCode());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30530
     * Data:     x -> y
     * Reason:   x and y are no formulas 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative8() throws Exception {
        final Element ele = TestParser.createElement(
              "<IMPL>"
            + "  <VAR id=\"x\"/>"
            + "  <VAR id=\"y\"/>"
            + "</IMPL>"
        );
        Trace.param(CLASS, this, "testNegative8", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        // System.out.println(list);
        assertEquals(2, list.size());
        assertEquals(30530, list.get(0).getErrorCode());
        assertEquals(30530, list.get(1).getErrorCode());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30530
     * Data:     UIMPL(x, y)
     * Reason:   UIMPL is an unknown logical operator 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative9() throws Exception {
        final Element ele = TestParser.createElement(
              "<UIMPL>"
            + "  <VAR id=\"x\"/>"
            + "  <VAR id=\"y\"/>"
            + "</UIMPL>"
        );
        Trace.param(CLASS, this, "testNegative9", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30530, list.get(0).getErrorCode());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30620
     * Data:     sirup(x, unknown(y))
     * Reason:   "unknown" is an unknown term operator 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative10() throws Exception {
        final Element ele = TestParser.createElement(
              "<PREDVAR id=\"sirup\">"
            + "  <VAR id=\"x\"/>"
            + "  <unknown id=\"y\"/>"
            + "</PREDVAR>"
        );
        Trace.param(CLASS, this, "testNegative10", "ele", ele);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30620, list.get(0).getErrorCode());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30590
     * Data:     x union unknown(y)
     * Reason:   "and" is an unknown predicate constant 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative11() throws Exception {
        final Element ele = TestParser.createElement(
              "<PREDCON ref=\"and\">"
            + "  <VAR id=\"x\"/>"
            + "  <FUNCON ref=\"power\">"
            + "    <VAR id=\"y\"/>"
            + "  </FUNCON>"
            + "</PREDCON>"
        );
        Trace.param(CLASS, this, "testNegative11", "ele", ele);
        FormulaChecker.checkFormula(ele, context);
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30590, list.get(0).getErrorCode());
    }
            
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30690
     * Data:     x union unknown(y)
     * Reason:   "unknown" is an unknown term operator 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative12() throws Exception {
        final Element ele = TestParser.createElement(
              "<FUNCON ref=\"union\">"
            + "  <VAR id=\"x\"/>"
            + "  <FUNCON ref=\"unknown\">"
            + "    <VAR id=\"y\"/>"
            + "  </FUNCON>"
            + "</FUNCON>"
        );
        Trace.param(CLASS, this, "testNegative12", "ele", ele);
        FormulaChecker.checkTerm(ele, context);
        LogicalCheckExceptionList list =
            FormulaChecker.checkTerm(ele, context, getChecker());
        assertEquals(1, list.size());
        assertEquals(30690, list.get(0).getErrorCode());
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30680
     * Data:     all y (all z (phi(z) <-> z in y) -> y = {z | phi(z)})
     * Reason:   "{ .. }" is an unknown term operator 
     * 
     * @throws  Exception   Test failed.
     */
    public void testNegative13() throws Exception {
        final Element ele = TestParser.createElement(
              "<FORALL>"
            + "  <VAR id=\"y\"/>"
            + "  <IMPL>"
            + "    <FORALL>"
            + "      <VAR id=\"z\"/>"
            + "      <EQUI>"
            + "        <PREDVAR ref=\"phi\">"
            + "          <VAR id=\"z\"/>"
            + "        </PREDVAR>"
            + "        <PREDCON ref=\"in\">"
            + "          <VAR id=\"z\"/>"
            + "          <VAR id=\"y\"/>"
            + "        </PREDCON>"
            + "      </EQUI>"
            + "    </FORALL>"
            + "    <PREDCON ref=\"equal\">"
            + "      <VAR id=\"y\"/>"
            + "      <CLASS>"
            + "        <VAR id=\"z\"/>"
            + "        <PREDVAR ref=\"phi\">"
            + "          <VAR id=\"z\"/>"
            + "        </PREDVAR>"
            + "      </CLASS>"
            + "    </PREDCON>"
            + "  </IMPL>"
            + "</FORALL>"
        );
        Trace.param(CLASS, this, "testNegative13", "ele", ele);
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        LogicalCheckExceptionList list =
            FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
        assertEquals(1, list.size());
        assertEquals(30680, list.get(0).getErrorCode());
    }
    
}
