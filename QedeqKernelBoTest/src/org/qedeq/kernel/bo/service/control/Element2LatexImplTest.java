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
package org.qedeq.kernel.bo.service.control;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.service.control.Element2LatexImpl;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;

/**
 * Testing {@link Element2LatexImplTest}.
 *
 * @author  Michael Meyling
 */
public class Element2LatexImplTest extends QedeqTestCase {

    private Element2LatexImpl transform;

    protected void setUp() throws Exception {
        super.setUp();
        transform = new Element2LatexImpl(new ModuleLabels());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testVar() {
        final Element ele = new DefaultElementList("VAR", new Element[] {
            new DefaultAtom("x")});
//        System.out.println(transform.getLatex(ele));
        assertEquals("x", transform.getLatex(ele));
        final Element ele2 = new DefaultElementList("VAR", new Element[] {
            new DefaultAtom("1")});
//        System.out.println(transform.getLatex(ele2));
        assertEquals("x", transform.getLatex(ele2));
        final Element ele3 = new DefaultElementList("FUNVAR", new Element[] {
            new DefaultAtom("psi"),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("x"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("1"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("y"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("2"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("z"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("3"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("u"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("4"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("v"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("5"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("w"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("6"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("7"),
            }),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("8"),
            })
        });
        assertEquals("psi(x, x, y, y, z, z, u, u, v, v, w, w, x_1, x_2)", transform.getLatex(ele3));
        final Element ele4 = new DefaultElementList("VAR", new Element[] {
                new DefaultAtom(" 1")});
//            System.out.println(transform.getLatex(ele4));
            assertEquals(" 1", transform.getLatex(ele4));
    }

    public void testFuncon() {
        final Element ele = new DefaultElementList("FUNCON", new Element[] {
            new DefaultAtom("RussellClass")});
//        System.out.println(transform.getLatex(ele));
        assertEquals("\\mathfrak{Ru}", transform.getLatex(ele));
        final Element ele2 = new DefaultElementList("FUNCON", new Element[] {
            new DefaultAtom("phi")});
//        System.out.println(transform.getLatex(ele2));
        assertEquals("phi_0()", transform.getLatex(ele2));
    }

    public void testFunvar() {
        final Element ele = new DefaultElementList("FUNVAR", new Element[] {
            new DefaultAtom("phi")});
//        System.out.println(transform.getLatex(ele));
        assertEquals("phi", transform.getLatex(ele));
        final Element ele2 = new DefaultElementList("FUNVAR", new Element[] {
            new DefaultAtom("phi"),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("x"),
            })
        });
//        System.out.println(transform.getLatex(ele2));
        assertEquals("phi(x)", transform.getLatex(ele2));
    }

    public void testPredcon() {
        final Element ele = new DefaultElementList("PREDCON", new Element[] {
            new DefaultAtom("isSet"),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("x"),
            })
        });
//        System.out.println(transform.getLatex(ele));
        assertEquals("\\mathfrak{M}(x)", transform.getLatex(ele));
        final Element ele2 = new DefaultElementList("PREDCON", new Element[] {
            new DefaultAtom("isSet")
        });
//        System.out.println(transform.getLatex(ele));
        assertEquals("isSet_0()", transform.getLatex(ele2));
        final Element ele3 = new DefaultElementList("PREDCON", new Element[] {
            new DefaultAtom("isMyOwnPredcon")
        });
//        System.out.println(transform.getLatex(ele));
        assertEquals("isMyOwnPredcon_0()", transform.getLatex(ele3));
    }

    public void testPredvar() {
        final Element ele = new DefaultElementList("PREDVAR", new Element[] {
            new DefaultAtom("alpha")});
//        System.out.println(transform.getLatex(ele));
        assertEquals("alpha", transform.getLatex(ele));
        final Element ele2 = new DefaultElementList("PREDVAR", new Element[] {
            new DefaultAtom("alpha"),
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("x"),
            })
        });
//        System.out.println(transform.getLatex(ele2));
        assertEquals("alpha(x)", transform.getLatex(ele2));
    }

    public void testVarious1() {
        Element ele = new DefaultElementList("EQUI", new Element[] {
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
//        System.out.println(transform.getLatex(ele));
        assertEquals("y \\ = \\ \\{ x \\ | \\ \\phi(x) \\} \\ \\leftrightarrow\\ "
            + "\\forall z\\ (z \\in y\\ \\leftrightarrow\\ z \\in \\{ x \\ | \\ \\phi(x) \\} )",
            transform.getLatex(ele));
        assertEquals("(y \\ = \\ \\{ x \\ | \\ \\phi(x) \\} \\ \\leftrightarrow\\ "
                + "\\forall z\\ (z \\in y\\ \\leftrightarrow\\ z \\in \\{ x \\ | \\ \\phi(x) \\} ))",
                transform.getLatex(ele, false));
    }

    public void testBinaryLogical() {
        Element ele = new DefaultElementList("AND", new Element[] {
            new DefaultElementList("PREDVAR", new Element[] {
                new DefaultAtom("\\alpha"),
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("x"),
                })
            }),
            new DefaultElementList("PREDVAR", new Element[] {
                new DefaultAtom("\\beta"),
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("y")
                })
            }),
            new DefaultElementList("IMPL", new Element[] {
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("\\alpha"),
                    new DefaultElementList("VAR", new Element[] {
                        new DefaultAtom("x")
                    })
                }),
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("\\beta"),
                    new DefaultElementList("VAR", new Element[] {
                        new DefaultAtom("y")
                    })
                })
            })
        });
//        System.out.println(transform.getLatex(ele));
        assertEquals("\\alpha(x)\\ \\land\\ \\beta(y)\\ \\land\\ (\\alpha(x)\\ \\rightarrow\\ \\beta(y))",
            transform.getLatex(ele));
        assertEquals("(\\alpha(x)\\ \\land\\ \\beta(y)\\ \\land\\ (\\alpha(x)\\ \\rightarrow\\ \\beta(y)))",
                transform.getLatex(ele, false));
    }
 
    public void testVarious2() {
        Element ele = new DefaultElementList("myOperator", new Element[] {
            new DefaultAtom("Hello"),
            new DefaultAtom("Again"),
            new DefaultElementList("again", new Element[] {
                new DefaultAtom("one"),
                new DefaultAtom("two"),
                new DefaultAtom("three")
            })
        });
        assertEquals("myOperator(Hello, Again, again(one, two, three))", transform.getLatex(ele));
        assertEquals("myOperator(Hello, Again, again(one, two, three))", transform.getLatex(ele, false));
    }

    public void testClass() {
        Element ele = new DefaultElementList("CLASS", new Element[] {
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("x"),
            }),
            new DefaultElementList("PREDVAR", new Element[] {
                new DefaultAtom("\\beta"),
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("y")
                })
            })
        });
//        System.out.println(transform.getLatex(ele));
        assertEquals("\\{ x \\ | \\ \\beta(y) \\} ",
            transform.getLatex(ele));
        assertEquals("\\{ x \\ | \\ \\beta(y) \\} ",
                transform.getLatex(ele, false));
    }
 
    public void testClasslist() {
        Element ele = new DefaultElementList("CLASSLIST", new Element[] {
            new DefaultElementList("VAR", new Element[] {
                new DefaultAtom("x"),
            }),
            new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("y"),
                }),
            new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("z"),
                })
        });
        System.out.println(transform.getLatex(ele));
        assertEquals("\\{ x, \\ y, \\ z \\} ",
            transform.getLatex(ele));
        assertEquals("\\{ x, \\ y, \\ z \\} ",
                transform.getLatex(ele, false));
    }
}

