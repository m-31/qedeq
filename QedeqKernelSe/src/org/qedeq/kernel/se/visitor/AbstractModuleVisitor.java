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
 * Basic visitor that makes nothing.
 *
 * @author  Michael Meyling
 */
public abstract class AbstractModuleVisitor implements QedeqVisitor {

    public void visitEnter(final Atom atom) throws ModuleDataException {
    }

    public void visitEnter(final ElementList list) throws ModuleDataException {
    }

    public void visitEnter(final Author author) throws ModuleDataException {
    }

    public void visitEnter(final AuthorList authorList) throws ModuleDataException {
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
    }

    public void visitEnter(final Chapter chapter) throws ModuleDataException {
    }

    public void visitEnter(final ChapterList chapterList) throws ModuleDataException {
    }

    public void visitEnter(final Formula formula) throws ModuleDataException {
    }

    public void visitEnter(final InitialFunctionDefinition functionDefinition)
            throws ModuleDataException {
    }

    public void visitEnter(final FunctionDefinition functionDefinition) throws ModuleDataException {
    }

    public void visitEnter(final Header header) throws ModuleDataException {
    }

    public void visitEnter(final Import imp) throws ModuleDataException {
    }

    public void visitEnter(final ImportList importList) throws ModuleDataException {
    }

    public void visitEnter(final Latex latex) throws ModuleDataException {
    }

    public void visitEnter(final LatexList latexList) throws ModuleDataException {
    }

    public void visitEnter(final LinkList linkList) throws ModuleDataException {
    }

    public void visitEnter(final LiteratureItem literatureItem) throws ModuleDataException {
    }

    public void visitEnter(final LiteratureItemList literatureItemList) throws ModuleDataException {
    }

    public void visitEnter(final Location location) throws ModuleDataException {
    }

    public void visitEnter(final LocationList locationList) throws ModuleDataException {
    }

    public void visitEnter(final Node node) throws ModuleDataException {
    }

    public void visitEnter(final InitialPredicateDefinition predicateDefinition)
            throws ModuleDataException {
    }

    public void visitEnter(final PredicateDefinition predicateDefinition)
            throws ModuleDataException {
    }

    public void visitEnter(final FormalProofList proofList) throws ModuleDataException {
    }

    public void visitEnter(final FormalProof proof) throws ModuleDataException {
    }

    public void visitEnter(final FormalProofLine proofLine) throws ModuleDataException {
    }

    public void visitEnter(final Reason reason) throws ModuleDataException {
    }

    public void visitEnter(final ModusPonens reason) throws ModuleDataException {
    }

    public void visitEnter(final Add reason) throws ModuleDataException {
    }

    public void visitEnter(final Rename reason) throws ModuleDataException {
    }

    public void visitEnter(final SubstFree reason) throws ModuleDataException {
    }

    public void visitEnter(final SubstFunc reason) throws ModuleDataException {
    }

    public void visitEnter(final SubstPred reason) throws ModuleDataException {
    }

    public void visitEnter(final Existential reason) throws ModuleDataException {
    }

    public void visitEnter(final Universal reason) throws ModuleDataException {
    }

    public void visitEnter(final ConditionalProof reason) throws ModuleDataException {
    }

    public void visitEnter(final Hypothesis hypothesis) throws ModuleDataException {
    }

    public void visitEnter(final Conclusion conclusion) throws ModuleDataException {
    }

    public void visitEnter(final FormalProofLineList proofLineList) throws ModuleDataException {
    }

    public void visitEnter(final Proof proof) throws ModuleDataException {
    }

    public void visitEnter(final ProofList proofList) throws ModuleDataException {
    }

    public void visitEnter(final Proposition proposition) throws ModuleDataException {
    }

    public void visitEnter(final Qedeq qedeq) throws ModuleDataException {
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
    }

    public void visitEnter(final ChangedRuleList rule) throws ModuleDataException {
    }

    public void visitEnter(final ChangedRule rule) throws ModuleDataException {
    }

    public void visitEnter(final Section section) throws ModuleDataException {
    }

    public void visitEnter(final SectionList sectionList) throws ModuleDataException {
    }

    public void visitEnter(final Specification specification) throws ModuleDataException {
    }

    public void visitEnter(final Subsection subsection) throws ModuleDataException {
    }

    public void visitEnter(final SubsectionList subsectionList) throws ModuleDataException {
    }

    public void visitEnter(final SubsectionType subsectionType) throws ModuleDataException {
    }

    public void visitEnter(final Term term) throws ModuleDataException {
    }

    public void visitEnter(final UsedByList usedByList) throws ModuleDataException {
    }

    public void visitLeave(final Author author) throws ModuleDataException {
    }

    public void visitLeave(final AuthorList authorList) throws ModuleDataException {
    }

    public void visitLeave(final Axiom axiom) throws ModuleDataException {
    }

    public void visitLeave(final Chapter chapter) throws ModuleDataException {
    }

    public void visitLeave(final ChapterList chapterList) throws ModuleDataException {
    }

    public void visitLeave(final Formula formula) throws ModuleDataException {
    }

    public void visitLeave(final InitialFunctionDefinition functionDefinition) throws ModuleDataException {
    }

    public void visitLeave(final FunctionDefinition functionDefinition) throws ModuleDataException {
    }

    public void visitLeave(final Header header) throws ModuleDataException {
    }

    public void visitLeave(final Import imp) throws ModuleDataException {
    }

    public void visitLeave(final ImportList importList) throws ModuleDataException {
    }

    public void visitLeave(final Latex latex) throws ModuleDataException {
    }

    public void visitLeave(final LatexList latexList) throws ModuleDataException {
    }

    public void visitLeave(final LinkList linkList) throws ModuleDataException {
    }

    public void visitLeave(final LiteratureItem literatureItem) throws ModuleDataException {
    }

    public void visitLeave(final LiteratureItemList literatureItemList) throws ModuleDataException {
    }

    public void visitLeave(final Location location) throws ModuleDataException {
    }

    public void visitLeave(final LocationList locationList) throws ModuleDataException {
    }

    public void visitLeave(final Node authorList) throws ModuleDataException {
    }

    public void visitLeave(final InitialPredicateDefinition predicateDefinition)
        throws ModuleDataException {
    }

    public void visitLeave(final PredicateDefinition predicateDefinition)
            throws ModuleDataException {
    }

    public void visitLeave(final FormalProofList proofList) throws ModuleDataException {
    }

    public void visitLeave(final FormalProof proof) throws ModuleDataException {
    }

    public void visitLeave(final FormalProofLine proofLine) throws ModuleDataException {
    }

    public void visitLeave(final Reason reason) throws ModuleDataException {
    }

    public void visitLeave(final ModusPonens reason) throws ModuleDataException {
    }

    public void visitLeave(final Add reason) throws ModuleDataException {
    }

    public void visitLeave(final Rename reason) throws ModuleDataException {
    }

    public void visitLeave(final SubstFree reason) throws ModuleDataException {
    }

    public void visitLeave(final SubstFunc reason) throws ModuleDataException {
    }

    public void visitLeave(final SubstPred reason) throws ModuleDataException {
    }

    public void visitLeave(final Existential reason) throws ModuleDataException {
    }

    public void visitLeave(final Universal reason) throws ModuleDataException {
    }

    public void visitLeave(final ConditionalProof reason) throws ModuleDataException {
    }

    public void visitLeave(final Hypothesis hypothesis) throws ModuleDataException {
    }

    public void visitLeave(final Conclusion conclusion) throws ModuleDataException {
    }

    public void visitLeave(final FormalProofLineList proofLineList) throws ModuleDataException {
    }

    public void visitLeave(final Proof proof) throws ModuleDataException {
    }

    public void visitLeave(final ProofList proofList) throws ModuleDataException {
    }

    public void visitLeave(final Proposition proposition) throws ModuleDataException {
    }

    public void visitLeave(final Qedeq qedeq) throws ModuleDataException {
    }

    public void visitLeave(final Rule rule) throws ModuleDataException {
    }

    public void visitLeave(final ChangedRuleList rule) throws ModuleDataException {
    }

    public void visitLeave(final ChangedRule rule) throws ModuleDataException {
    }

    public void visitLeave(final Section section) throws ModuleDataException {
    }

    public void visitLeave(final SectionList sectionList) throws ModuleDataException {
    }

    public void visitLeave(final Specification specification) throws ModuleDataException {
    }

    public void visitLeave(final Subsection subsection) throws ModuleDataException {
    }

    public void visitLeave(final SubsectionList subsectionList) throws ModuleDataException {
    }

    public void visitLeave(final SubsectionType subsectionType) throws ModuleDataException {
    }

    public void visitLeave(final Term term) throws ModuleDataException {
    }

    public void visitLeave(final UsedByList usedByList) throws ModuleDataException {
    }

    public void visitLeave(final Atom atom) throws ModuleDataException {
    }

    public void visitLeave(final ElementList list) throws ModuleDataException {
    }
}
