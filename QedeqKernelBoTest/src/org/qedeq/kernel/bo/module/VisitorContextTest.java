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

package org.qedeq.kernel.bo.module;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.test.DynamicGetter;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.service.QedeqBoFactoryTest;
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
import org.qedeq.kernel.se.base.module.VariableList;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.se.visitor.QedeqVisitor;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;


/**
 * Test context of {@link QedeqNotNullTraverser}.
 *
 * @author Michael Meyling
 */
public class VisitorContextTest extends QedeqTestCase implements QedeqVisitor {

    /** This class. */
    private static final Class CLASS = VisitorContextTest.class;

    /** Traverse QEDEQ module with this traverser. */
    private QedeqNotNullTraverser traverser;

    private Qedeq qedeq;

    private File moduleFile;

    public void testContext() throws Exception {
        if (slow()) {
            moduleFile = QedeqBoFactoryTest.getQedeqFile("math/qedeq_set_theory_v1.xml");
            final ModuleAddress globalContext = new DefaultModuleAddress(moduleFile);
            qedeq = QedeqBoFactoryTest.loadQedeq(moduleFile);
            traverser = new QedeqNotNullTraverser(globalContext, this);
            traverser.accept(qedeq);
        }
    }

    public void visitEnter(final Atom atom) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final ElementList list) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Author author) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final AuthorList authorList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Chapter chapter) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final ChapterList chapterList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final ConditionalProof cp) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Formula formula) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final FunctionDefinition functionDefinition) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Header header) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Hypothesis hypothesis) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Import imp) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final ImportList importList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Latex latex) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final LatexList latexList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final LinkList linkList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final LiteratureItem literatureItem) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final LiteratureItemList literatureItemList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Location location) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final LocationList locationList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Node authorList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final PredicateDefinition predicateDefinition)
            throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final FormalProofList formalProofList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final FormalProof proof) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final FormalProofLine formalProofLine) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final FormalProofLineList formalProofLineList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Reason reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final ModusPonens reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Add reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Rename reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final SubstFree reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final SubstFunc reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final SubstPred reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Existential reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Universal reason) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Conclusion conclusion) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Proof proof) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final ProofList proofList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Proposition proposition) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Qedeq qedeq) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Section section) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final SectionList sectionList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Specification specification) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Subsection subsection) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final SubsectionList subsectionList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final SubsectionType subsectionType) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Term term) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final UsedByList usedByList) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final VariableList variableList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Author author) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final AuthorList authorList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Axiom axiom) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Chapter chapter) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final ChapterList chapterList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Formula formula) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final FunctionDefinition functionDefinition) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Header header) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Import imp) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final ImportList importList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Latex latex) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final LatexList latexList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final LinkList linkList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final LiteratureItem literatureItem) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final LiteratureItemList literatureItemList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Location location) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final LocationList locationList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Node authorList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final PredicateDefinition predicateDefinition)
            throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final FormalProofList formalProofList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final FormalProofLine formalProof) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final FormalProof proof) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Reason reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final FormalProofLineList formalProofLineList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final ModusPonens reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Add reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Rename reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final SubstFree reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final SubstFunc reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final SubstPred reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Existential reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Universal reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final ConditionalProof reason) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Hypothesis hypothesis) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Conclusion conclusion) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Proof proof) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final ProofList proofList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Proposition proposition) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Qedeq qedeq) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Rule rule) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Section section) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final SectionList sectionList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Specification specification) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Subsection subsection) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final SubsectionList subsectionList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final SubsectionType subsectionType) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Term term) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final UsedByList usedByList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final VariableList variableList) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final Atom atom) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(final ElementList list) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(InitialFunctionDefinition functionDefinition)
            throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(InitialPredicateDefinition predicateDefinition)
            throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(InitialFunctionDefinition functionDefinition)
            throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(InitialPredicateDefinition predicateDefinition)
            throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(ChangedRuleList list) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(ChangedRule rule) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(ChangedRuleList list) throws ModuleDataException {
        checkContext();
    }

    public void visitLeave(ChangedRule rule) throws ModuleDataException {
        checkContext();
    }

    private void checkContext() throws ModuleDataException {
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        try {
            Trace.param(CLASS, "checkContext()",
            "context > ", context);
            if (context.length() > 0) {
                DynamicGetter.get(qedeq, context);
            }
        } catch (RuntimeException e) {
            System.err.println(context);
            throw e;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        SimpleXPath xpath = Context2SimpleXPath.getXPath(traverser.getCurrentContext(), qedeq);
        Trace.param(CLASS, "checkContext()",
            "xpath   < ", xpath);
        final SourceArea find = XPathLocationParser.findSourceArea(moduleFile,
            xpath);
        if (find.getStartPosition() == null) {
            System.out.println(traverser.getCurrentContext());
            throw new RuntimeException("start not found: " + find + "\ncontext: "
                + context);
        }
        if (find.getEndPosition() == null) {
            System.out.println(traverser.getCurrentContext());
            throw new RuntimeException("end not found: " + find + "\ncontext: "
                + context);
        }
    }

}
