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

package org.qedeq.kernel.bo.logic.wf;

import java.util.List;

import org.qedeq.kernel.bo.logic.common.FormulaChecker;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 *
 * @author  Michael Meyling
 */
public class FormulaCheckerGlobalTest extends AbstractFormulaChecker {

    /** Module context for module access. */
    private ModuleContext context;

    private FormulaChecker checker;

    protected void setUp() throws Exception {
        context = new ModuleContext(new DefaultModuleAddress("http://memory.org/sample.xml"), "getElement()");
        checker = new FormulaCheckerImpl();
    }

    protected void tearDown() throws Exception {
        context = null;
    }

    /**
     * Function: checkFormula(Element).
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
        // System.out.println(ele.toString());
        assertFalse(checker.checkFormula(ele, context).hasErrors());
        assertFalse(checker.checkFormula(ele, context, getChecker()).hasErrors());
    }

    /**
     * Function: checkFormula(Element).
     * Type:     negative, code 30400
     * Data:     element null
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative01() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkFormula(null, context);
        assertEquals(1, list.size());
        assertEquals(30400, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element).
     * Type:     negative, code 30400
     * Data:     element null
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalTermNegative01() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkFormula(null, context);
        assertEquals(1, list.size());
        assertEquals(30400, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
     * Type:     negative, code 30410
     * Data:     element getAtom() null
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative03() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkFormula(new DefaultElementList("PREDVAR", new Element[]{new Atom() {

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

                public Element replace(final Element search, final Element replacement) {
                    return null;
                }
            }
        }), context);
        assertEquals(1, list.size());
        assertEquals(30410, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element).
     * Type:     negative, code 30410
     * Data:     element getAtom() null
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative04() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkTerm(new DefaultElementList("FUNCON", new Element[] {new Atom() {

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

                public Element replace(final Element search, final Element replacement) {
                    return null;
                }
            }
        }), context);
        assertEquals(1, list.size());
        assertEquals(30410, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
     * Type:     negative, code 30420
     * Data:     element getList() null
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative05() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkFormula(new Element() {

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

                public Element replace(final Element search, final Element replacement) {
                    return null;
                }
            }, context);
        assertEquals(1, list.size());
        assertEquals(30420, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element).
     * Type:     negative, code 30420
     * Data:     element getList() null
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative06() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkTerm(new Element() {

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

                public Element replace(final Element search, final Element replacement) {
                    return null;
                }
            }, context);
        assertEquals(1, list.size());
        assertEquals(30420, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
     * Type:     negative, code 30430
     * Data:     PREDVAR(null) (atom content is null)
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative07() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkFormula(new DefaultElementList("PREDVAR", new Element[]{new Atom() {

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

                public Element replace(final Element search, final Element replacement) {
                    return null;
                }
            }
        }), context);
        assertEquals(1, list.size());
        assertEquals(30430, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element).
     * Type:     negative, code 30430
     * Data:     FUNCON(null) (atom content is null)
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative08() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkTerm(new DefaultElementList("FUNCON", new Element[]{new Atom() {

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

                public Element replace(final Element search, final Element replacement) {
                    return null;
                }
            }
        }), context);
        assertEquals(1, list.size());
        assertEquals(30430, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
     * Type:     negative, code 30440
     * Data:     PREDVAR("")  (atom contents has length 0)
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative09() throws Exception {
        LogicalCheckExceptionList list =
            checker.checkFormula(new DefaultElementList("PREDVAR", new Element[]{new Atom() {

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

                public Element replace(final Element search, final Element replacement) {
                    return null;
                }
            }
        }), context);
        assertEquals(1, list.size());
        assertEquals(30440, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
     * Type:     negative, code 30440
     * Data:     PREDCON("")  (atom contents has length 0)
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative11() throws Exception {
        final Element ele = new DefaultElementList("PREDCON", new Element[] {
            new DefaultAtom("")});
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30440, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element).
     * Type:     negative, code 30440
     * Data:     FUNCON("")  (atom contents has length 0)
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative12() throws Exception {
        final Element ele = new DefaultElementList("FUNCON", new Element[] {
            new DefaultAtom("")});
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context);
        assertEquals(1, list.size());
        assertEquals(30440, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
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

            public Element getElement(final int i) {
                return null;
            }

            public List getElements() {
                return null;
            }

            public void add(final Element element) {

            }

            public void insert(final int position, final Element element) {
            }

            public void replace(final int position, final Element element) {
            }

            public void remove(final int i) {
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

            public Element replace(final Element search, final Element replacement) {
                return null;
            }

        };
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30450, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element).
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

            public Element getElement(final int i) {
                return null;
            }

            public List getElements() {
                return null;
            }

            public void add(final Element element) {

            }

            public void insert(final int position, final Element element) {
            }

            public void replace(final int position, final Element element) {
            }

            public void remove(final int i) {
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

            public Element replace(final Element search, final Element replacement) {
                return null;
            }
        };
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context);
        assertEquals(1, list.size());
        assertEquals(30450, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
     * Type:     negative, code 30460
     * Data:     operator has length 0
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative15() throws Exception {
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

            public Element replace(final Element search, final Element replacement) {
                return null;
            }
        };
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30460, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element).
     * Type:     negative, code 30460
     * Data:     operator has length 0
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative16() throws Exception {
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

            public Element replace(final Element search, final Element replacement) {
                return null;
            }
        };
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context);
        assertEquals(1, list.size());
        assertEquals(30460, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
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

            public Element getElement(final int i) {
                return null;
            }

            public List getElements() {
                return null;
            }

            public void add(final Element element) {

            }

            public void insert(final int position, final Element element) {
            }

            public void replace(final int position, final Element element) {
            }

            public void remove(final int i) {
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

            public Element replace(final Element search, final Element replacement) {
                return null;
            }
        };
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30460, list.get(0).getErrorCode());
    }

    /**
     * Function: checkFormula(Element).
     * Type:     negative, code 30470
     * Data:
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative23() throws Exception {
        final Element ele = new DefaultAtom("Atom");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkFormula(ele, context);
        assertEquals(1, list.size());
        assertEquals(30470, list.get(0).getErrorCode());
    }

    /**
     * Function: checkTerm(Element).
     * Type:     negative, code 30470
     * Data:
     *
     * @throws  Exception   Test failed.
     */
    public void testGlobalFormulaNegative24() throws Exception {
        final Element ele = new DefaultAtom("Atom");
        // System.out.println(ele.toString());
        LogicalCheckExceptionList list =
            checker.checkTerm(ele, context);
        assertEquals(1, list.size());
        assertEquals(30470, list.get(0).getErrorCode());
    }

}
