/* $Id: ListTraverserQedeqNotNullTraverserTest.java,v 1.4 2007/12/21 23:35:17 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;

import org.qedeq.kernel.base.list.Atom;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.bo.load.DefaultModuleAddress;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.bo.visitor.QedeqVisitor;
import org.qedeq.kernel.dto.list.DefaultAtom;
import org.qedeq.kernel.dto.list.DefaultElementList;
import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.TextOutput;

/**
 * Test visitor concept for {@link org.qedeq.kernel.bo.visitor.ListVisitor}. 
 * This class doesn't test much existing code directly, but checks that the
 * {@link org.qedeq.kernel.bo.visitor.QedeqNotNullTraverser} works correctly for
 * the list part. 
 * 
 * @version $Revision: 1.4 $
 * @author Michael Meyling
 */
public class QedeqNotNullTraverserTest extends QedeqTestCase {
   
    /** This class. */
    private static final Class CLASS = QedeqNotNullTraverserTest.class;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    private final TextOutput text = new TextOutput("local", out);
    
    private final Stack stack = new Stack();
    
    private static ModuleAddress address;
    
    static {
        try {
            address = new DefaultModuleAddress("http://memory.org/sample.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private final QedeqVisitor visitor = new AbstractModuleVisitor() {
        
        public void visitEnter(Atom atom) {
            handleComma();
            text.print("\"");
            text.print(atom.getString());
        }

        public void visitLeave(Atom atom) {
            text.print("\"");
        }

        public void visitEnter(ElementList list) {
            handleComma();
            text.print(list.getOperator() + "(");
            stack.push(Boolean.FALSE);
        }

        public void visitLeave(ElementList list) {
            text.print(")");
            stack.pop();
        }

        private void handleComma() {
            if (!stack.isEmpty() && ((Boolean) stack.peek()).booleanValue()) {
                text.print(", ");
            } else {
                if (!stack.isEmpty()) {
                    stack.pop();
                    stack.push(Boolean.TRUE);
                }
            }
        }

    };
    
    private final QedeqNotNullTraverser trans = new QedeqNotNullTraverser(address, 
        visitor);
    
    
    /**
     * Constructor.
     */
    public QedeqNotNullTraverserTest() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param   name    Test case name.
     */
    public QedeqNotNullTraverserTest(String name) {
        super(name);
    }

    /**
     * Test visitor concept.
     */
    public void testVisit() throws Exception {
        Element el = new DefaultElementList("myOperator", new Element[] {
            new DefaultAtom("Hello"), 
            new DefaultAtom("Again"),
            new DefaultElementList("again", new Element[] {
                new DefaultAtom("one"), 
                new DefaultAtom("two"),
                new DefaultAtom("three")
            })
        });

        trans.accept(el);
        Trace.trace(CLASS, this, "testVisit", out.toString("ISO-8859-1"));
        assertEquals("myOperator(\"Hello\", \"Again\", again(\"one\", \"two\", \"three\"))",
            out.toString("ISO-8859-1"));
    }
    
    /**
     * Test generation.
     */
    public void testGeneration() throws Exception {
        Element el = new DefaultElementList("EQUI", new Element[] {
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
        trans.accept(el);
        Trace.trace(CLASS, this, "testGeneration", out.toString("ISO-8859-1"));
        assertEquals("EQUI(PREDCON(\"equal\", VAR(\"y\"), CLASS(VAR(\"x\"), PREDVAR(\"\\phi\","
            + " VAR(\"x\")))), FORALL(VAR(\"z\"), EQUI(PREDCON(\"in\", VAR(\"z\"), VAR(\"y\")),"
            + " PREDCON(\"in\", VAR(\"z\"), CLASS(VAR(\"x\"), PREDVAR(\"\\phi\", VAR(\"x\")))))))",
            out.toString("ISO-8859-1"));
        Trace.trace(CLASS, this, "testGeneration", el.toString());
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        out.reset();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

/*    
    public void accept(final Element element) throws ModuleDataException {
        if (element.isList()) {
            accept(element.getList());
        } else if (element.isAtom()) {
            accept(element.getAtom());
        } else {
            throw new IllegalArgumentException("unexpected element type: "
                + element.toString());
        }
    }

    public void accept(final Atom atom) throws ModuleDataException {
        visitEnter(atom);
        visitLeave(atom);
    }

    public void accept(final ElementList list) throws ModuleDataException {
        visitEnter(list);
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                text.print(", ");
            }
            accept(list.getElement(i));
        }
        visitLeave(list);
    }
*/
    
}
