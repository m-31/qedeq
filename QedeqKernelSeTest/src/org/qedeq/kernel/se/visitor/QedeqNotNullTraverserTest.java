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

package org.qedeq.kernel.se.visitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Stack;

import org.qedeq.base.io.TextOutput;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.AuthorList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.ChangedRule;
import org.qedeq.kernel.se.base.module.ChangedRuleList;
import org.qedeq.kernel.se.base.module.Chapter;
import org.qedeq.kernel.se.base.module.ChapterList;
import org.qedeq.kernel.se.base.module.Conclusion;
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FormalProofList;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Header;
import org.qedeq.kernel.se.base.module.Hypothesis;
import org.qedeq.kernel.se.base.module.Import;
import org.qedeq.kernel.se.base.module.ImportList;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.Latex;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.LinkList;
import org.qedeq.kernel.se.base.module.LiteratureItem;
import org.qedeq.kernel.se.base.module.LiteratureItemList;
import org.qedeq.kernel.se.base.module.Location;
import org.qedeq.kernel.se.base.module.LocationList;
import org.qedeq.kernel.se.base.module.ModusPonens;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proof;
import org.qedeq.kernel.se.base.module.ProofList;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.base.module.Rename;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.base.module.Section;
import org.qedeq.kernel.se.base.module.SectionList;
import org.qedeq.kernel.se.base.module.Specification;
import org.qedeq.kernel.se.base.module.Subsection;
import org.qedeq.kernel.se.base.module.SubsectionList;
import org.qedeq.kernel.se.base.module.SubsectionType;
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Term;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.base.module.UsedByList;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.test.QedeqVoCreator;

/**
 * Test visitor concept for {@link org.qedeq.kernel.visitor.QedeqVisitor}.
 * This class doesn't test much existing code directly, but checks that the
 * {@link QedeqNotNullTraverser} works correctly for
 * the list part.
 *
 * @author  Michael Meyling
 */
public class QedeqNotNullTraverserTest extends QedeqTestCase {

    /** This class. */
    private static final Class CLASS = QedeqNotNullTraverserTest.class;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final TextOutput text = new TextOutput("local", out, "UTF-8");

//    private final TextOutput text = new TextOutput("local", new PrintStream(System.out));

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

    private final QedeqVisitor visitor2 = new QedeqVisitor() {

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

        public void visitEnter(Author author) {
            text.println("<author email=\"" + author.getEmail() + "\">");
            text.pushLevel();
        }

        public void visitEnter(AuthorList authorList) {
            text.println("<authorlist>");
            text.pushLevel();
        }

        public void visitEnter(Axiom axiom) {
            text.println("<axiom>");
            text.pushLevel();
        }

        public void visitEnter(Chapter chapter) {
            text.println("<chapter>");
            text.pushLevel();
        }

        public void visitEnter(ChapterList chapterList) {
            text.println("<chapterlist>");
            text.pushLevel();
        }

        public void visitEnter(Formula formula) {
            text.println("<formula>");
            text.pushLevel();
        }

        public void visitEnter(InitialFunctionDefinition functionDefinition) {
            text.println("<initialfunctiondefinition>");
            text.pushLevel();
        }

        public void visitEnter(FunctionDefinition functionDefinition) {
            text.println("<functiondefinition>");
            text.pushLevel();
        }

        public void visitEnter(Header header) {
            text.println("<header>");
            text.pushLevel();
        }

        public void visitEnter(Import imp) {
            text.println("<import>");
            text.pushLevel();
        }

        public void visitEnter(ImportList importList) {
            text.println("<importlist>");
            text.pushLevel();
        }

        public void visitEnter(Latex latex) {
            text.println("<latex>");
            text.pushLevel();
        }

        public void visitEnter(LatexList latexList) {
            text.println("<latexlist>");
            text.pushLevel();
        }

        public void visitEnter(LinkList linkList) {
            text.println("<linklist>");
            text.pushLevel();
        }

        public void visitEnter(LiteratureItem literatureItem) {
            text.println("<literatureitem>");
            text.pushLevel();
        }

        public void visitEnter(LiteratureItemList literatureItemList) {
            text.println("<literatureitemlist>");
            text.pushLevel();
        }

        public void visitEnter(Location location) {
            text.println("<location>");
            text.pushLevel();
        }

        public void visitEnter(LocationList locationList) {
            text.println("<locationlist>");
            text.pushLevel();
        }

        public void visitEnter(Node node) {
            text.println("<node>");
            text.pushLevel();
        }

        public void visitEnter(InitialPredicateDefinition predicateDefinition) {
            text.println("<initialpredicatedefinition>");
            text.pushLevel();
        }

        public void visitEnter(PredicateDefinition predicateDefinition) {
            text.println("<predicatedefinition>");
            text.pushLevel();
        }

        public void visitEnter(FormalProof proof) {
            text.println("<formalproof>");
            text.pushLevel();
        }

        public void visitEnter(FormalProofList proofList) {
            text.println("<formalprooflist>");
            text.pushLevel();
        }

        public void visitEnter(FormalProofLine proofLine) {
            text.println("<formalproofline>");
            text.pushLevel();
        }

        public void visitEnter(Reason reason) {
            text.println("<reason>");
            text.pushLevel();
        }

        public void visitEnter(ModusPonens reason) {
            text.println("<modusponens>");
            text.pushLevel();
        }

        public void visitEnter(Add reason) {
            text.println("<add>");
            text.pushLevel();
        }

        public void visitEnter(Rename reason) {
            text.println("<rename>");
            text.pushLevel();
        }

        public void visitEnter(SubstFree reason) {
            text.println("<substfree>");
            text.pushLevel();
        }

        public void visitEnter(SubstFunc reason) {
            text.println("<substfunc>");
            text.pushLevel();
        }

        public void visitEnter(SubstPred reason) {
            text.println("<substpred>");
            text.pushLevel();
        }

        public void visitEnter(Existential reason) {
            text.println("<existential>");
            text.pushLevel();
        }

        public void visitEnter(Universal reason) {
            text.println("<universal>");
            text.pushLevel();
        }

        public void visitEnter(ConditionalProof reason) {
            text.println("<conditionalproof>");
            text.pushLevel();
        }

        public void visitEnter(Hypothesis hypothesis) {
            text.println("<hypothesis>");
            text.pushLevel();
        }

        public void visitEnter(Conclusion conclusion) {
            text.println("<conclusion>");
            text.pushLevel();
        }

        public void visitEnter(FormalProofLineList proofLineList) {
            text.println("<formalprooflinelist>");
            text.pushLevel();
        }

        public void visitEnter(Proof proof) {
            text.println("<proof>");
            text.pushLevel();
        }

        public void visitEnter(ProofList proofList) {
            text.println("<prooflist>");
            text.pushLevel();
        }

        public void visitEnter(Proposition proposition) {
            text.println("<proposition>");
            text.pushLevel();
        }

        public void visitEnter(Qedeq qedeq) {
            text.println("<qedeq>");
            text.pushLevel();
        }

        public void visitEnter(Rule rule) {
            text.println("<rule>");
            text.pushLevel();
        }

        public void visitEnter(ChangedRuleList list) {
            text.println("<changedrulelist>");
            text.pushLevel();
        }

        public void visitEnter(ChangedRule rule) {
            text.println("<changedrule>");
            text.pushLevel();
        }

        public void visitEnter(Section section) {
            text.println("<section>");
            text.pushLevel();
        }

        public void visitEnter(SectionList sectionList) {
            text.println("<sectionlist>");
            text.pushLevel();
        }

        public void visitEnter(Specification specification) {
            text.println("<specification>");
            text.pushLevel();
        }

        public void visitEnter(Subsection subsection) {
            text.println("<subsection>");
            text.pushLevel();
        }

        public void visitEnter(SubsectionList subsectionList) {
            text.println("<subsectionlist>");
            text.pushLevel();
        }

        public void visitEnter(SubsectionType subsectionType) {
            text.println("<subsectiontype>");
            text.pushLevel();
        }

        public void visitEnter(Term term) {
            text.println("<term>");
            text.pushLevel();
        }

        public void visitEnter(UsedByList usedByList) {
            text.println("<usedbylist>");
            text.pushLevel();
        }

        public void visitLeave(Author author) throws ModuleDataException {
            text.popLevel();
            text.println("</author>");
        }

        public void visitLeave(AuthorList authorList) {
            text.popLevel();
            text.println("</authorlist>");
        }

        public void visitLeave(Axiom axiom) {
            text.popLevel();
            text.println("</axiom>");
        }

        public void visitLeave(Chapter chapter) {
            text.popLevel();
            text.println("</chapter>");
        }

        public void visitLeave(ChapterList chapterList) {
            text.popLevel();
            text.println("</chapterlist>");
        }

        public void visitLeave(Formula formula) {
            text.popLevel();
            text.println("</formula>");
        }

        public void visitLeave(InitialFunctionDefinition functionDefinition) {
            text.popLevel();
            text.println("</initialfunctiondefinition>");
        }

        public void visitLeave(FunctionDefinition functionDefinition) {
            text.popLevel();
            text.println("</functiondefinition>");
        }

        public void visitLeave(Header header) {
            text.popLevel();
            text.println("</header>");
        }

        public void visitLeave(Import imp) {
            text.popLevel();
            text.println("</import>");
        }

        public void visitLeave(ImportList importList) {
            text.popLevel();
            text.println("</importlist>");
        }

        public void visitLeave(Latex latex) {
            text.popLevel();
            text.println("</latex>");
        }

        public void visitLeave(LatexList latexList) {
            text.popLevel();
            text.println("</latexlist>");
        }

        public void visitLeave(LinkList linkList) {
            text.popLevel();
            text.println("</linklist>");
        }

        public void visitLeave(LiteratureItem literatureItem) {
            text.popLevel();
            text.println("</literatureitem>");
        }

        public void visitLeave(LiteratureItemList literatureItemList) {
            text.popLevel();
            text.println("</literatureitemlist>");
        }

        public void visitLeave(Location location) {
            text.popLevel();
            text.println("</location>");
        }

        public void visitLeave(LocationList locationList) {
            text.popLevel();
            text.println("</locationlist>");
        }

        public void visitLeave(Node node) {
            text.popLevel();
            text.println("</node>");
        }

        public void visitLeave(InitialPredicateDefinition predicateDefinition) {
            text.popLevel();
            text.println("</initialpredicatedefinition>");
        }

        public void visitLeave(PredicateDefinition predicateDefinition) {
            text.popLevel();
            text.println("</predicatedefinition>");
        }

        public void visitLeave(FormalProofList proofList) {
            text.popLevel();
            text.println("</formalproofList>");
        }

        public void visitLeave(FormalProof proof) {
            text.popLevel();
            text.println("</proof>");
        }

        public void visitLeave(FormalProofLine proofLine) {
            text.popLevel();
            text.println("</formalproofline>");
        }

        public void visitLeave(Reason reason) {
            text.popLevel();
            text.println("</reason>");
        }

        public void visitLeave(FormalProofLineList proofLineList) {
            text.popLevel();
            text.println("</formalprooflinelist>");
        }

        public void visitLeave(ModusPonens reason) {
            text.popLevel();
            text.println("</modulsponens>");
        }

        public void visitLeave(Add reason) {
            text.popLevel();
            text.println("</add>");
        }

        public void visitLeave(Rename reason) {
            text.popLevel();
            text.println("</rename>");
        }

        public void visitLeave(SubstFree reason) {
            text.popLevel();
            text.println("</substfree>");
        }

        public void visitLeave(SubstFunc reason) {
            text.popLevel();
            text.println("</substfunc>");
        }

        public void visitLeave(SubstPred reason) {
            text.popLevel();
            text.println("</substpred>");
        }

        public void visitLeave(Existential reason) {
            text.popLevel();
            text.println("</existential>");
        }

        public void visitLeave(Universal reason) {
            text.popLevel();
            text.println("</universal>");
        }

        public void visitLeave(ConditionalProof reason) {
            text.popLevel();
            text.println("</conditionalproof>");
        }

        public void visitLeave(Hypothesis hypothesis) {
            text.popLevel();
            text.println("</hypothesis>");
        }

        public void visitLeave(Conclusion conclusion) {
            text.popLevel();
            text.println("</conclusion>");
        }

        public void visitLeave(Proof proof) {
            text.popLevel();
            text.println("</proof>");
        }

        public void visitLeave(ProofList proofList) {
            text.popLevel();
            text.println("</prooflist>");
        }

        public void visitLeave(Proposition proposition) {
            text.popLevel();
            text.println("</proposition>");
        }

        public void visitLeave(Qedeq qedeq) {
            text.popLevel();
            text.println("</qedeq>");
        }

        public void visitLeave(Rule rule) {
            text.popLevel();
            text.println("</rule>");
        }

        public void visitLeave(ChangedRuleList list) {
            text.popLevel();
            text.println("</changedrulelist>");
        }

        public void visitLeave(ChangedRule rule) {
            text.popLevel();
            text.println("</changedrule>");
        }

        public void visitLeave(Section section) {
            text.popLevel();
            text.println("</section>");
        }

        public void visitLeave(SectionList sectionList) {
            text.popLevel();
            text.println("</sectionlist>");
        }

        public void visitLeave(Specification specification) {
            text.popLevel();
            text.println("</specification>");
        }

        public void visitLeave(Subsection subsection) {
            text.popLevel();
            text.println("</subsection>");
        }

        public void visitLeave(SubsectionList subsectionList) {
            text.popLevel();
            text.println("</subsectionlist>");
        }

        public void visitLeave(SubsectionType subsectionType) {
            text.popLevel();
            text.println("</subsectiontype>");
        }

        public void visitLeave(Term term) {
            text.popLevel();
            text.println("</term>");
        }

        public void visitLeave(UsedByList usedByList) {
            text.popLevel();
            text.println("</usedbylist>");
        }

    };

    public void testQedeq() throws Exception {
//        QedeqNotNullTraverser trans2 = new QedeqNotNullTraverser(address,
//            visitor2);
        final QedeqNotNullTraverser trans2 = new QedeqNotNullTraverser(address);
;
        final QedeqVisitorTester testVisitor = new QedeqVisitorTester() {
            public void checkEnter() {
//                super.checkEnter();
//                System.out.println(")" + trans2.getCurrentContext().getLocationWithinModule());
            }
            public void checkLeave() {
//                super.checkLeave();
//                System.out.println("(" + trans2.getCurrentContext().getLocationWithinModule());
            }
        };
        trans2.setVisitor(testVisitor);
        final List list = new QedeqVoCreator().create();
        for (int i = 0; i < list.size(); i++) {
//        System.out.print(qedeq);
//        System.out.println();
//            System.out.println(out.toString("UTF-8"));
            trans2.accept((Qedeq) list.get(i));
            assertEquals(0, testVisitor.getLevel());
        }
    }
    

    public void testQedeq2() throws Exception {
        QedeqNotNullTraverser trans2 = new QedeqNotNullTraverser(address,
            visitor2);
        try {
            trans2.accept((Qedeq) null);
            fail("Execption expected");
        } catch (Exception e) {
            // ok
        }
    }

    public void testAcceptors1() throws Exception {
        QedeqNotNullTraverser trans2 = new QedeqNotNullTraverser(address,
            visitor2);
        final Method[] methods = trans2.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (!"accept".equals(method.getName())) {
                continue;
            }
            assertEquals(1, method.getParameterTypes().length);
            if (Qedeq.class == method.getParameterTypes()[0]) {
                continue;
            }
            method.invoke(trans2, new Object[] {null });
        }
    }

    public void testAcceptors2() throws Throwable {
        QedeqNotNullTraverser trans2 = new QedeqNotNullTraverser(address,
            visitor2);
        final Method[] methods = trans2.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (!"accept".equals(method.getName())) {
                continue;
            }
            assertEquals(1, method.getParameterTypes().length);
            if (Qedeq.class == method.getParameterTypes()[0]) {
                continue;
            }
            Thread.currentThread().interrupt();
            try {
                method.invoke(trans2, new Object[] {null });
                fail("Exception expected");
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof InterruptException) {
                    // ok
                } else {
                    throw e.getCause();
                }
            }
            assertFalse(Thread.interrupted());
        }
    }
}
