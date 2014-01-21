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

package org.qedeq.kernel.se.visitor;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.Vector;

import org.qedeq.base.io.TextOutput;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.se.base.list.Atom;
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
import org.qedeq.kernel.se.common.ModuleDataException;

/**
 * Test visitor concept for {@link org.qedeq.kernel.visitor.QedeqVisitor}.
 * This class doesn't test much existing code directly, but checks that the
 * {@link QedeqNotNullTraverser} works correctly for
 * the list part.
 *
 * @author  Michael Meyling
 */
public class QedeqVisitorTester implements QedeqVisitor {

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    private Vector counter = new Vector();

    private TextOutput text;

//    private final TextOutput text = new TextOutput("local", new PrintStream(System.out));

    private final Stack commaStack = new Stack();

    private final Stack getterStack = new Stack();

    private final Stack objectStack = new Stack();

    private QedeqTraverser traverser;

    public QedeqVisitorTester(final QedeqTraverser traverser) {
        this.traverser = traverser;
        init();
    }

    public void init() {
        out.reset();
        if (text != null) {
            text.close();
        }
        commaStack.clear();
        objectStack.clear();
        counter.clear();
        getterStack.clear();
        out = new ByteArrayOutputStream();
        text = new TextOutput("local", out, "UTF-8");
    }

    public String getOutput() {
        try {
            return out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void visitEnter(Atom atom) {
        enter(atom);
    }

    public void visitLeave(Atom atom) {
        leave(atom);
    }

    public void visitEnter(ElementList list) {
        enter(list);
    }

    public void visitLeave(ElementList list) {
        leave(list);
    }

    public void visitEnter(Author author) {
        enter(author);
    }

    public void visitEnter(AuthorList authorList) {
        enter(authorList);
    }

    public void visitEnter(Axiom axiom) {
        enter(axiom);
    }

    public void visitEnter(Chapter chapter) {
        enter(chapter);
    }

    public void visitEnter(ChapterList chapterList) {
        enter(chapterList);
    }

    public void visitEnter(Formula formula) {
        enter(formula);
    }

    public void visitEnter(InitialFunctionDefinition functionDefinition) {
        enter(functionDefinition);
    }

    public void visitEnter(FunctionDefinition functionDefinition) {
        enter(functionDefinition);
    }

    public void visitEnter(Header header) {
        enter(header);
    }

    public void visitEnter(Import imp) {
        enter(imp);
    }

    public void visitEnter(ImportList importList) {
        enter(importList);
    }

    public void visitEnter(Latex latex) {
        enter(latex);
    }

    public void visitEnter(LatexList latexList) {
        enter(latexList);
    }

    public void visitEnter(LinkList linkList) {
        enter(linkList);
    }

    public void visitEnter(LiteratureItem literatureItem) {
        enter(literatureItem);
    }

    public void visitEnter(LiteratureItemList literatureItemList) {
        enter(literatureItemList);
    }

    public void visitEnter(Location location) {
        enter(location);
    }

    public void visitEnter(LocationList locationList) {
        enter(locationList);
    }

    public void visitEnter(Node node) {
        enter(node);
    }

    public void visitEnter(InitialPredicateDefinition predicateDefinition) {
        enter(predicateDefinition);
    }

    public void visitEnter(PredicateDefinition predicateDefinition) {
        enter(predicateDefinition);
    }

    public void visitEnter(FormalProof proof) {
        enter(proof);
    }

    public void visitEnter(FormalProofList proofList) {
        enter(proofList);
    }

    public void visitEnter(FormalProofLine proofLine) {
        enter(proofLine);
    }

    public void visitEnter(Reason reason) {
        enter(reason);
    }

    public void visitEnter(ModusPonens reason) {
        enter(reason);
    }

    public void visitEnter(Add reason) {
        enter(reason);
    }

    public void visitEnter(Rename reason) {
        enter(reason);
    }

    public void visitEnter(SubstFree reason) {
        enter(reason);
    }

    public void visitEnter(SubstFunc reason) {
        enter(reason);
    }

    public void visitEnter(SubstPred reason) {
        enter(reason);
    }

    public void visitEnter(Existential reason) {
        enter(reason);
    }

    public void visitEnter(Universal reason) {
        enter(reason);
    }

    public void visitEnter(ConditionalProof reason) {
        enter(reason);
    }

    public void visitEnter(Hypothesis hypothesis) {
        enter(hypothesis);
    }

    public void visitEnter(Conclusion conclusion) {
        enter(conclusion);
    }

    public void visitEnter(FormalProofLineList proofLineList) {
        enter(proofLineList);
    }

    public void visitEnter(Proof proof) {
        enter(proof);
    }

    public void visitEnter(ProofList proofList) {
        enter(proofList);
    }

    public void visitEnter(Proposition proposition) {
        enter(proposition);
    }

    public void visitEnter(Qedeq qedeq) {
        enter(qedeq);
    }

    public void visitEnter(Rule rule) {
        enter(rule);
    }

    public void visitEnter(ChangedRuleList list) {
        enter(list);
    }

    public void visitEnter(ChangedRule rule) {
        enter(rule);
    }

    public void visitEnter(Section section) {
        enter(section);
    }

    public void visitEnter(SectionList sectionList) {
        enter(sectionList);
    }

    public void visitEnter(Specification specification) {
        enter(specification);
    }

    public void visitEnter(Subsection subsection) {
        enter(subsection);
    }

    public void visitEnter(SubsectionList subsectionList) {
        enter(subsectionList);
    }

    public void visitEnter(SubsectionType subsectionType) {
        enter(subsectionType);
    }

    public void visitEnter(Term term) {
        enter(term);
    }

    public void visitEnter(UsedByList usedByList) {
        enter(usedByList);
    }

    public void visitLeave(Author author) throws ModuleDataException {
        leave(author);
    }

    public void visitLeave(AuthorList authorList) {
        leave(authorList);
    }

    public void visitLeave(Axiom axiom) {
        leave(axiom);
    }

    public void visitLeave(Chapter chapter) {
        leave(chapter);
    }

    public void visitLeave(ChapterList chapterList) {
        leave(chapterList);
    }

    public void visitLeave(Formula formula) {
        leave(formula);
    }

    public void visitLeave(InitialFunctionDefinition functionDefinition) {
        leave(functionDefinition);
    }

    public void visitLeave(FunctionDefinition functionDefinition) {
        leave(functionDefinition);
    }

    public void visitLeave(Header header) {
        leave(header);
    }

    public void visitLeave(Import imp) {
        leave(imp);
    }

    public void visitLeave(ImportList importList) {
        leave(importList);
    }

    public void visitLeave(Latex latex) {
        leave(latex);
    }

    public void visitLeave(LatexList latexList) {
        leave(latexList);
    }

    public void visitLeave(LinkList linkList) {
        leave(linkList);
    }

    public void visitLeave(LiteratureItem literatureItem) {
        leave(literatureItem);
    }

    public void visitLeave(LiteratureItemList literatureItemList) {
        leave(literatureItemList);
    }

    public void visitLeave(Location location) {
        leave(location);
    }

    public void visitLeave(LocationList locationList) {
        leave(locationList);
    }

    public void visitLeave(Node node) {
        leave(node);
    }

    public void visitLeave(InitialPredicateDefinition predicateDefinition) {
        leave(predicateDefinition);
    }

    public void visitLeave(PredicateDefinition predicateDefinition) {
        leave(predicateDefinition);
    }

    public void visitLeave(FormalProofList proofList) {
        leave(proofList);
    }

    public void visitLeave(FormalProof proof) {
        leave(proof);
    }

    public void visitLeave(FormalProofLine proofLine) {
        leave(proofLine);
    }

    public void visitLeave(Reason reason) {
        leave(reason);
    }

    public void visitLeave(FormalProofLineList proofLineList) {
        leave(proofLineList);
    }

    public void visitLeave(ModusPonens reason) {
        leave(reason);
    }

    public void visitLeave(Add reason) {
        leave(reason);
    }

    public void visitLeave(Rename reason) {
        leave(reason);
    }

    public void visitLeave(SubstFree reason) {
        leave(reason);
    }

    public void visitLeave(SubstFunc reason) {
        leave(reason);
    }

    public void visitLeave(SubstPred reason) {
        leave(reason);
    }

    public void visitLeave(Existential reason) {
        leave(reason);
    }

    public void visitLeave(Universal reason) {
        leave(reason);
    }

    public void visitLeave(ConditionalProof reason) {
        leave(reason);
    }

    public void visitLeave(Hypothesis hypothesis) {
        leave(hypothesis);
    }

    public void visitLeave(Conclusion conclusion) {
        leave(conclusion);
    }

    public void visitLeave(Proof proof) {
        leave(proof);
    }

    public void visitLeave(ProofList proofList) {
        leave(proofList);
    }

    public void visitLeave(Proposition proposition) {
        leave(proposition);
    }

    public void visitLeave(Qedeq qedeq) {
        leave(qedeq);
    }

    public void visitLeave(Rule rule) {
        leave(rule);
    }

    public void visitLeave(ChangedRuleList list) {
        leave(list);
    }

    public void visitLeave(ChangedRule rule) {
        leave(rule);
    }

    public void visitLeave(Section section) {
        leave(section);
    }

    public void visitLeave(SectionList sectionList) {
        leave(sectionList);
    }

    public void visitLeave(Specification specification) {
        leave(specification);
    }

    public void visitLeave(Subsection subsection) {
        leave(subsection);
    }

    public void visitLeave(SubsectionList subsectionList) {
        leave(subsectionList);
    }

    public void visitLeave(SubsectionType subsectionType) {
        leave(subsectionType);
    }

    public void visitLeave(Term term) {
        leave(term);
    }

    public void visitLeave(UsedByList usedByList) {
        leave(usedByList);
    }

    private int getCounter(final int level) {
        int value = 0;
        if (level < counter.size()) {
            Object obj = counter.get(level);
            if (obj != null) {
                value = ((Integer) obj).intValue();
            }
        }
        return value;
    }

    private void setCounter(final int level, final int value) {
        while (level >= counter.size()) {
            counter.add(new Integer(0));
        }
        counter.set(level, new Integer(value));
    }

    private void increaseCounter(final int level) {
        while (level >= counter.size()) {
            counter.add(new Integer(0));
        }
        counter.set(level, new Integer(1 + getCounter(level)));
    }

    private void deleteCounter(final int level) {
        counter.setSize(level);
    }

    public String getContext() {
        String result = "";
        for (int i = 1; i < getterStack.size(); i++) {
            if (i > 1) {
                result += ".";
            }
            result += getterStack.get(i);
        }
        return result;
    }

    protected void enter(final Object obj) {
        if (obj == null) {
            return;
        }
        String name = StringUtility.getClassName(obj.getClass());
//        System.out.println(name);
        if (name.endsWith("Vo")) {
            name = name.substring(0, name.length() - 2);
        } else if (name.equals("DefaultAtom")) {
            name = "Atom";
        } else if (name.equals("DefaultElementList")) {
            name = "ElementList";
        }
        String lastName = "";
        if (getLevel() > 0) {
            lastName = objectStack.lastElement().toString();
        }
        String getter = "get" + name + "()";
        if (name.equals("Atom")) {
            getter = "getElement().getAtom()";
        } else if (name.equals("ElementList")) {
            getter = "getElement().getList()";
        }
//        System.out.println("lastName=" + lastName);
//        System.out.println("getter=" + getter);
        if (!getter.startsWith("getElement()") && name.endsWith("List")) {
            setCounter(getLevel() + 1, 0);
            if ("LatexList".equals(name)) {
                // we have to guess the correct context :-(
//                System.out.println("name=" + name);
                getter = StringUtility.getLastDotString(traverser.getCurrentContext().getLocationWithinModule());
//                System.out.println("getter=" + getter);
            }
        
        } else if (lastName.endsWith("List")) {
//          if (lastName.equals(name + "List")) {
//            if (!(name + "List").equals(lastName)) {
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>> " + name + "  " + obj.getClass().getName());
//            }
            getter = "get(" + getCounter(getLevel() + 1) + ")";
            if (name.equals("Subsection")) {
                getter += ".getSubsection()";
            } if (name.equals("Node")) {
                getter += ".getNode()";
            }
            increaseCounter(getLevel() + 1);
        } else if (lastName.equals("Author")) {
            if (name.equals("Latex")) {
                getter = "getName()";
            }
        } else if (lastName.equals("FormalProofLine") && obj instanceof Reason) {
            getter = "getReason()";
        } else if (getter.startsWith("getElement()")) {
//            System.out.println("getElement() found");
            if (lastName.equals("InitialPredicateDefinition")) {
//                System.out.println("getPredCon() found");
                getter = "getPredCon()." + StringUtility.getLastDotString(getter);
            } else if (lastName.equals("InitialFunctionDefinition")) {
                getter = "getFunCon()." + StringUtility.getLastDotString(getter);
            } else if (lastName.equals("SubstFree")) {
                getter = "getSubjectVariable()." + StringUtility.getLastDotString(getter);
            } else if (lastName.equals("SubstPred")) {
                getter = "getPredicateVariable()." + StringUtility.getLastDotString(getter);
            } else if (lastName.equals("Rename")) {
                // we have a 50 to 50 chance so we get it from the correct(?) context
//                getter = "getPredicateVariable()." + StringUtility.getLastDotString(getter);
                getter = StringUtility.getLastTwoDotStrings(traverser.getCurrentContext()
                    .getLocationWithinModule());
            } else if (lastName.equals("Existential")) {
                getter = "getSubjectVariable()." + StringUtility.getLastDotString(getter);
            } else if (lastName.equals("Universal")) {
                getter = "getSubjectVariable()." + StringUtility.getLastDotString(getter);
            } else if (lastName.equals("SubstFunc")) {
                getter = "getFunctionVariable()." + StringUtility.getLastDotString(getter);
            }
        } else if (lastName.equals("SubstFree") && name.equals("Term")) {
            getter = "getSubstituteTerm()";
        } else if (lastName.equals("SubstFunc") && name.equals("Term")) {
            getter = "getSubstituteTerm()";
        } else if (lastName.equals("SubstPred") && name.equals("Formula")) {
            getter = "getSubstituteFormula()";
// TODO 20130131 m31: this shows a broken design!!!
        } else if (name.equals("Proposition")) {
            getter = "getNodeType()." + getter;
        } else if (name.equals("Rule")) {
            getter = "getNodeType()." + getter;
        } else if (name.endsWith("Definition")) {
            getter = "getNodeType()." + getter;
        } else if (name.endsWith("Axiom")) {
            getter = "getNodeType()." + getter;
        }
//        System.out.println(name);
        objectStack.push(name);
        getterStack.push(getter);
        text.println("<" + name + ">");
        text.pushLevel();
        checkEnter();
    }

    protected void leave(final Object obj) {
        if (obj == null) {
            return;
        }
        checkLeave();
        String name = StringUtility.getClassName(obj.getClass());
        if (name.endsWith("Vo")) {
            name = name.substring(0, name.length() - 2);
        }
        if (name.endsWith("List")) {
            deleteCounter(getLevel());
        } else {
            if (getterStack.size() > 0 && getterStack.lastElement().toString().startsWith("get(")) {
                increaseCounter(getLevel());
            }
        }
        objectStack.pop();
        getterStack.pop();
        text.popLevel();
        text.println("</" + StringUtility.getClassName(obj.getClass()) + ">");
    }

    public int getLevel() {
        return objectStack.size();
    }

    protected void checkEnter() {
//        System.out.println(">" + getContext());
//        for (int i = 0; i < objectStack.size(); i++) {
//            System.out.print(objectStack.get(i) + " ");
//        }
//        System.out.println();
    }

    protected void checkLeave() {
//        System.out.println("<" + getContext());
    }
}
