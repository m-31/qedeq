/* $Id: FormulaCheckerGlobalTest.java,v 1.8 2008/01/26 12:39:50 m31 Exp $
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

import java.util.List;

import org.qedeq.kernel.base.list.Atom;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.bo.load.DefaultModuleAddress;
import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.dto.list.DefaultAtom;
import org.qedeq.kernel.dto.list.DefaultElementList;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 *
 * @version $Revision: 1.8 $
 * @author  Michael Meyling
 */
public class FormulaCheckerGlobalTest extends AbstractFormulaChecker {

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
     * Data:     y = { x | phi(x)} <-> (all z: z in y <-> z in {x | phi(x)})
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaPositive() throws Exception {
        final Element ele = new DefaultElementList("EQUI", new Element[] {
            new DefaultElementList("PREDCON", new Element[] {
                new DefaultAtom("equal"), 
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("y"), 
                }),
                new DefaultElementList("CLASS", new Element[] {
                    new DefaultElementList("VAR", new Element[] {
                        new DefaultAtom("x"), 
                    }),
                    new DefaultElementList("PREDVAR", new Element[] {
                        new DefaultAtom("\\phi"), 
                        new DefaultElementList("VAR", new Element[] {
                            new DefaultAtom("x"), 
                        })
                    })
                })
            }),
            new DefaultElementList("FORALL", new Element[] {
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("z"), 
                }),
                new DefaultElementList("EQUI", new Element[] {
                    new DefaultElementList("PREDCON", new Element[] {
                        new DefaultAtom("in"), 
                        new DefaultElementList("VAR", new Element[] {
                            new DefaultAtom("z"), 
                        }),
                        new DefaultElementList("VAR", new Element[] {
                            new DefaultAtom("y"), 
                        })
                    }),
                    new DefaultElementList("PREDCON", new Element[] {
                        new DefaultAtom("in"), 
                        new DefaultElementList("VAR", new Element[] {
                            new DefaultAtom("z"), 
                        }),
                        new DefaultElementList("CLASS", new Element[] {
                            new DefaultElementList("VAR", new Element[] {
                                new DefaultAtom("x"), 
                            }),
                            new DefaultElementList("PREDVAR", new Element[] {
                                new DefaultAtom("\\phi"), 
                                new DefaultElementList("VAR", new Element[] {
                                    new DefaultAtom("x"), 
                                })
                            })
                        })
                    })
                })
            })
        });
        System.out.println(ele.toString());
        FormulaChecker.checkFormula(ele, context);
        FormulaChecker.checkFormula(ele, context, getChecker());
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30400
     * Data:     element null
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative01() throws Exception {
        try {
            FormulaChecker.checkFormula(null, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30400, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30400
     * Data:     element null
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalTermNegative01() throws Exception {
        try {
            FormulaChecker.checkFormula(null, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30400, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30410
     * Data:     element getAtom() null
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative03() throws Exception {
        try {
            FormulaChecker.checkFormula(new DefaultElementList("PREDVAR", new Element[]{new Atom(){

                public String getString() {
                    return null;
                }

                public boolean isAtom() {
                    return true;
                }

                public Atom getAtom() {
                    return null;
                }

                public boolean isList() {
                    return false;
                }

                public ElementList getList() {
                    return null;
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }}}), 
                context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30410, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30410
     * Data:     element getAtom() null
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative04() throws Exception {
        try {
            FormulaChecker.checkTerm(new DefaultElementList("FUNCON", new Element[]{new Atom(){

                public String getString() {
                    return null;
                }

                public boolean isAtom() {
                    return true;
                }

                public Atom getAtom() {
                    return null;
                }

                public boolean isList() {
                    return false;
                }

                public ElementList getList() {
                    return null;
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }}}), 
                context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30410, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30420
     * Data:     element getList() null
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative05() throws Exception {
        try {
            FormulaChecker.checkFormula(new Element() {

                public boolean isAtom() {
                    return false;
                }

                public Atom getAtom() {
                    return null;
                }

                public boolean isList() {
                    return true;
                }

                public ElementList getList() {
                    return null;
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }},
                context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30420, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30420
     * Data:     element getList() null
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative06() throws Exception {
        try {
            FormulaChecker.checkTerm(new Element() {

                public boolean isAtom() {
                    return false;
                }

                public Atom getAtom() {
                    return null;
                }

                public boolean isList() {
                    return true;
                }

                public ElementList getList() {
                    return null;
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }},
                context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30420, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30430
     * Data:     PREDVAR(null) (atom content is null)
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative07() throws Exception {
        try {
            FormulaChecker.checkFormula(new DefaultElementList("PREDVAR", new Element[]{new Atom(){

                public String getString() {
                    return null;
                }

                public boolean isAtom() {
                    return true;
                }

                public Atom getAtom() {
                    return this;
                }

                public boolean isList() {
                    return false;
                }

                public ElementList getList() {
                    return null;
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }}}), 
                context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30430, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30430
     * Data:     FUNCON(null) (atom content is null)
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative08() throws Exception {
        try {
            FormulaChecker.checkTerm(new DefaultElementList("FUNCON", new Element[]{new Atom(){

                public String getString() {
                    return null;
                }

                public boolean isAtom() {
                    return true;
                }

                public Atom getAtom() {
                    return this;
                }

                public boolean isList() {
                    return false;
                }

                public ElementList getList() {
                    return null;
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }}}), 
                context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30430, e.getErrorCode());
        }
    }
    
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30440
     * Data:     PREDVAR("")  (atom contents has length 0)
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative09() throws Exception {
        try {
            FormulaChecker.checkFormula(new DefaultElementList("PREDVAR", new Element[]{new Atom(){

                public String getString() {
                    return "";
                }

                public boolean isAtom() {
                    return true;
                }

                public Atom getAtom() {
                    return this;
                }

                public boolean isList() {
                    return false;
                }

                public ElementList getList() {
                    return null;
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }}}), 
                context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30440, e.getErrorCode());
        }
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30440
     * Data:     PREDCON("")  (atom contents has length 0)
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative11() throws Exception {
        final Element ele = new DefaultElementList("PREDCON", new Element[] {
            new DefaultAtom("")});
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30440, e.getErrorCode());
        }
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30440
     * Data:     FUNCON("")  (atom contents has length 0)
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative12() throws Exception {
        final Element ele = new DefaultElementList("FUNCON", new Element[] {
            new DefaultAtom("")});
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30440, e.getErrorCode());
        }
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30450
     * Data:     operator is null
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative13() throws Exception {
        final Element ele = new ElementList() {

            public int size() {
                return 0;
            }

            public String getOperator() {
                return null;
            }

            public Element getElement(int i) {
                return null;
            }

            public List getElements() {
                return null;
            }

            public void add(Element element) {
                
            }

            public void insert(int position, Element element) {
            }

            public void replace(int position, Element element) {
            }

            public void remove(int i) {
            }

            public boolean isAtom() {
                return false;
            }

            public Atom getAtom() {
                return null;
            }

            public boolean isList() {
                return true;
            }

            public ElementList getList() {
                return this;
            }

            public Element copy() {
                return null;
            }

            public Element replace(Element search, Element replacement) {
                return null;
            }

        }; 
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30450, e.getErrorCode());
        }
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30450
     * Data:     operator is null
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative14() throws Exception {
        final Element ele = new ElementList() {

            public int size() {
                return 0;
            }

            public String getOperator() {
                return null;
            }

            public Element getElement(int i) {
                return null;
            }

            public List getElements() {
                return null;
            }

            public void add(Element element) {
                
            }

            public void insert(int position, Element element) {
            }

            public void replace(int position, Element element) {
            }

            public void remove(int i) {
            }

            public boolean isAtom() {
                return false;
            }

            public Atom getAtom() {
                return null;
            }

            public boolean isList() {
                return true;
            }

            public ElementList getList() {
                return this;
            }

            public Element copy() {
                return null;
            }

            public Element replace(Element search, Element replacement) {
                return null;
            }
        }; 
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30450, e.getErrorCode());
        }
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30460
     * Data:     operator has length 0
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative15() throws Exception {
        try {
            final Element ele = new Element() {

                public boolean isAtom() {
                    return false;
                }

                public Atom getAtom() {
                    return null;
                }

                public boolean isList() {
                    return true;
                }

                public ElementList getList() {
                    return new DefaultElementList("", new Element[] {});
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }};
            FormulaChecker.checkFormula(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30460, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30460
     * Data:     operator has length 0
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative16() throws Exception {
        try {
            final Element ele = new Element() {

                public boolean isAtom() {
                    return false;
                }

                public Atom getAtom() {
                    return null;
                }

                public boolean isList() {
                    return true;
                }

                public ElementList getList() {
                    return new DefaultElementList("", new Element[] {});
                }

                public Element copy() {
                    return null;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }};
            FormulaChecker.checkTerm(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30460, e.getErrorCode());
        }
    }
        
    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30460
     * Data:     operator has length 0
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative21() throws Exception {
        final Element ele = new ElementList() {

            public int size() {
                return 0;
            }

            public String getOperator() {
                return "";
            }

            public Element getElement(int i) {
                return null;
            }

            public List getElements() {
                return null;
            }

            public void add(Element element) {
                
            }

            public void insert(int position, Element element) {
            }

            public void replace(int position, Element element) {
            }

            public void remove(int i) {
            }

            public boolean isAtom() {
                return false;
            }

            public Atom getAtom() {
                return null;
            }

            public boolean isList() {
                return true;
            }

            public ElementList getList() {
                return this;
            }

            public Element copy() {
                return null;
            }

            public Element replace(Element search, Element replacement) {
                return null;
            }
        }; 
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30460, e.getErrorCode());
        }
    }

    /**
     * Function: checkFormula(Element)
     * Type:     negative, code 30470
     * Data:     
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative23() throws Exception {
        final Element ele = new DefaultAtom("Atom");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkFormula(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30470, e.getErrorCode());
        }
    }

    /**
     * Function: checkTerm(Element)
     * Type:     negative, code 30470
     * Data:     
     * 
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative24() throws Exception {
        final Element ele = new DefaultAtom("Atom");
        System.out.println(ele.toString());
        try {
            FormulaChecker.checkTerm(ele, context);
            fail("Exception expected");
        } catch (LogicalCheckException e) {
            assertEquals(30470, e.getErrorCode());
        }
    }

}
