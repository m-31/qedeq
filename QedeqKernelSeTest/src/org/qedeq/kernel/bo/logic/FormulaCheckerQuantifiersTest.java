/* $Id: FormulaCheckerQuantifiersTest.java,v 1.6 2007/12/21 23:35:17 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.bo.load.DefaultModuleAddress;
import org.qedeq.kernel.bo.module.ModuleContext;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 * Testing formulas made of quantifiers.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public class FormulaCheckerQuantifiersTest extends AbstractFormulaChecker {

    private ModuleContext context;
    
    protected void setUp() throws Exception {
        context = new ModuleContext(new DefaultModuleAddress("http://memory.org/sample.xml"), "getElement()");
    }

    protected void tearDown() throws Exception {
        context = null;
    }

    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     all x A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersPositive01() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL><VAR id=\"x\" /><PREDVAR id=\"A\"/></FORALL>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     exists x A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersPositive02() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS><VAR id=\"x\" /><PREDVAR id=\"A\"/></EXISTS>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     exists! x A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersPositive03() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU><VAR id=\"x\" /><PREDVAR id=\"A\"/></EXISTSU>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     all x phi(x): A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersPositive04() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <PREDVAR id=\"A\"/>" +
            "</FORALL>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     exists x phi(x) A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersPositive05() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <PREDVAR id=\"A\"/>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     positive
     * Data:     exists! x phi(x): A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersPositive06() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <PREDVAR id=\"A\"/>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
        FormulaChecker.checkFormula(ele, context, getCheckerWithoutClass());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30540, missing subject variable
     * Data:     all phi(x) A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative01() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <PREDVAR id=\"A\"/>" +
            "</FORALL>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30540, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30540, missing subject variable
     * Data:     exists phi(x) A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative02() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <PREDVAR id=\"A\"/>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30540, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30540, missing subject variable
     * Data:     exists! phi(x): A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative03() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <PREDVAR id=\"A\"/>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30540, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject already bound
     * Data:     all x all x phi(x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative04() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <VAR id=\"x\" />" +
            "  <FORALL>" +
            "    <VAR id=\"x\" />" +
            "    <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  </FORALL>" +
            "</FORALL>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject variable already bound
     * Data:     all x phi(x) all x phi(x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative05() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <FORALL>" +
            "    <VAR id=\"x\" />" +
            "    <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "   </FORALL>" +
            "</FORALL>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject variable already bound
     * Data:     all x phi({x | phi(x)})
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative06() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <CLASS>" +
            "      <VAR id=\"x\" />" +
            "      <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "     </CLASS>" +
            "   </PREDVAR>" +
            "</FORALL>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject already bound
     * Data:     exists x all x phi(x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative07() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <VAR id=\"x\" />" +
            "  <FORALL>" +
            "    <VAR id=\"x\" />" +
            "    <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  </FORALL>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject variable already bound
     * Data:     exists x phi(x) exists x phi(x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative08() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <EXISTS>" +
            "    <VAR id=\"x\" />" +
            "    <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  </EXISTS>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject variable already bound
     * Data:     exists x phi({x | phi(x)})
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative09() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <CLASS>" +
            "      <VAR id=\"x\" />" +
            "      <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "     </CLASS>" +
            "   </PREDVAR>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject already bound
     * Data:     exists! x all x phi(x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative10() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <VAR id=\"x\" />" +
            "  <FORALL>" +
            "    <VAR id=\"x\" />" +
            "    <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  </FORALL>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject variable already bound
     * Data:     exists! x phi(x) exists! x phi(x)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative11() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  <EXISTSU>" +
            "    <VAR id=\"x\" />" +
            "    <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "  </EXISTSU>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30550, subject variable already bound
     * Data:     exists! x phi({x | phi(x)})
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative12() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <CLASS>" +
            "      <VAR id=\"x\" />" +
            "      <PREDVAR id=\"phi\"><VAR id=\"x\" /></PREDVAR>" +
            "     </CLASS>" +
            "   </PREDVAR>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30550, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30570, mixed free bound
     * Data:     all x phi(x, y): phi({y | A)})
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative16() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "  <PREDVAR id=\"phi\">" +
            "    <CLASS>" +
            "      <VAR id=\"y\" />" +
            "      <PREDVAR id=\"A\"/>" +
            "    </CLASS>" +
            "  </PREDVAR>" +
            "</FORALL>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30570, mixed free bound
     * Data:     exists x phi(x, y): phi({y | A)})
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative17() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "  <PREDVAR id=\"phi\">" +
            "    <CLASS>" +
            "      <VAR id=\"y\" />" +
            "      <PREDVAR id=\"A\"/>" +
            "    </CLASS>" +
            "  </PREDVAR>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30570, mixed free bound
     * Data:     exists! x phi(x, y): phi({y | A)})
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative18() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "  <PREDVAR id=\"phi\">" +
            "    <CLASS>" +
            "      <VAR id=\"y\" />" +
            "      <PREDVAR id=\"A\"/>" +
            "    </CLASS>" +
            "  </PREDVAR>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30570, mixed free bound
     * Data:     all x phi(x, y): all y  A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative19() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "  <FORALL>" +
            "    <VAR id=\"y\" />" +
            "    <PREDVAR id=\"A\"/>" +
            "  </FORALL>" +
            "</FORALL>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30570, mixed free bound
     * Data:     exists x phi(x, y): all y A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative20() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "  <FORALL>" +
            "    <VAR id=\"y\" />" +
            "    <PREDVAR id=\"A\"/>" +
            "  </FORALL>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30570, mixed free bound
     * Data:     exists! x phi(x, y): exists! y phi(y) A
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative21() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "  <EXISTSU>" +
            "    <VAR id=\"y\" />" +
            "    <PREDVAR id=\"phi\">" +
            "      <VAR id=\"y\" />" +
            "    </PREDVAR>" +
            "    <PREDVAR id=\"A\"/>" +
            "  </EXISTSU>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30770, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30580, mixed free bound
     * Data:     all x phi(x, {y | A)}): phi(x, y)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative22() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <CLASS>" +
            "      <VAR id=\"y\" />" +
            "      <PREDVAR id=\"A\"/>" +
            "    </CLASS>" +
            "  </PREDVAR>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "</FORALL>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30580, mixed free bound
     * Data:     exists x phi(x, {y | A)}) phi(x, y)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative23() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <CLASS>" +
            "      <VAR id=\"y\" />" +
            "      <PREDVAR id=\"A\"/>" +
            "    </CLASS>" +
            "  </PREDVAR>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30580, mixed free bound
     * Data:     exists! x phi({y | A)}, x): phi(x, y)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative24() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <VAR id=\"x\" />" +
            "  <PREDVAR id=\"phi\">" +
            "    <CLASS>" +
            "      <VAR id=\"y\" />" +
            "      <PREDVAR id=\"A\"/>" +
            "    </CLASS>" +
            "    <VAR id=\"x\" />" +
            "  </PREDVAR>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30580, mixed free bound
     * Data:     all x (all y psi(x)): phi(x, y)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative25() throws Exception {
        final Element ele = TestParser.createElement(
            "<FORALL>" +
            "  <VAR id=\"x\" />" +
            "  <FORALL>" +
            "    <VAR id=\"y\" />" +
            "    <PREDVAR id=\"psi\">" +
            "      <VAR id=\"x\" />" +
            "    </PREDVAR>" +
            "  </FORALL>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "</FORALL>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30580, mixed free bound
     * Data:     exists x (all y phi(x, y)): phi(x, y)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative26() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTS>" +
            "  <VAR id=\"x\" />" +
            "  <FORALL>" +
            "    <VAR id=\"y\" />" +
            "    <PREDVAR id=\"phi\">" +
            "      <VAR id=\"x\" />" +
            "      <VAR id=\"y\" />" +
            "    </PREDVAR>" +
            "  </FORALL>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "</EXISTS>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30580, mixed free bound
     * Data:     exists! x (exists! y phi(y, x) A): phi(x, y)
     * 
     * @throws  Exception   Test failed.
     */
    public void testQuantifiersNegative27() throws Exception {
        final Element ele = TestParser.createElement(
            "<EXISTSU>" +
            "  <VAR id=\"x\" />" +
            "  <EXISTSU>" +
            "    <VAR id=\"y\" />" +
            "    <PREDVAR id=\"phi\">" +
            "      <VAR id=\"y\" />" +
            "      <VAR id=\"x\" />" +
            "    </PREDVAR>" +
            "    <PREDVAR id=\"A\"/>" +
            "  </EXISTSU>" +
            "  <PREDVAR id=\"phi\">" +
            "    <VAR id=\"x\" />" +
            "    <VAR id=\"y\" />" +
            "  </PREDVAR>" +
            "</EXISTSU>");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context, getChecker());
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30780, e.getErrorCode());
        }
    }
    
}
