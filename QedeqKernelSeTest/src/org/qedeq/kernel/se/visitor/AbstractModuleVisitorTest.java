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

import org.qedeq.base.test.QedeqTestCase;
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

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class AbstractModuleVisitorTest extends QedeqTestCase {

    private AbstractModuleVisitor listener;

    protected void setUp() throws Exception {
        super.setUp();
        listener = new AbstractModuleVisitor() {};
    }

    public void testEnter() throws Exception {
        listener.visitEnter((Add) null);
        listener.visitEnter((Atom) null);
        listener.visitEnter((Author) null);
        listener.visitEnter((AuthorList) null);
        listener.visitEnter((Axiom) null);
        listener.visitEnter((ChangedRule) null);
        listener.visitEnter((ChangedRuleList) null);
        listener.visitEnter((Chapter) null);
        listener.visitEnter((ChapterList) null);
        listener.visitEnter((Conclusion) null);
        listener.visitEnter((ConditionalProof) null);
        listener.visitEnter((ElementList) null);
        listener.visitEnter((Existential) null);
        listener.visitEnter((FormalProof) null);
        listener.visitEnter((FormalProofLine) null);
        listener.visitEnter((FormalProofLineList) null);
        listener.visitEnter((FormalProofList) null);
        listener.visitEnter((Formula) null);
        listener.visitEnter((FunctionDefinition) null);
        listener.visitEnter((Header) null);
        listener.visitEnter((Hypothesis) null);
        listener.visitEnter((Import) null);
        listener.visitEnter((ImportList) null);
        listener.visitEnter((InitialFunctionDefinition) null);
        listener.visitEnter((InitialPredicateDefinition) null);
        listener.visitEnter((Latex) null);
        listener.visitEnter((LatexList) null);
        listener.visitEnter((LinkList) null);
        listener.visitEnter((LiteratureItem) null);
        listener.visitEnter((LiteratureItemList) null);
        listener.visitEnter((Location) null);
        listener.visitEnter((LocationList) null);
        listener.visitEnter((ModusPonens) null);
        listener.visitEnter((Node) null);
        listener.visitEnter((PredicateDefinition) null);
        listener.visitEnter((Proof) null);
        listener.visitEnter((ProofList) null);
        listener.visitEnter((Proposition) null);
        listener.visitEnter((Qedeq) null);
        listener.visitEnter((Reason) null);
        listener.visitEnter((Rename) null);
        listener.visitEnter((Rule) null);
        listener.visitEnter((Section) null);
        listener.visitEnter((SectionList) null);
        listener.visitEnter((Specification) null);
        listener.visitEnter((Subsection) null);
        listener.visitEnter((SubsectionList) null);
        listener.visitEnter((SubsectionType) null);
        listener.visitEnter((SubstFree) null);
        listener.visitEnter((SubstFunc) null);
        listener.visitEnter((SubstPred) null);
        listener.visitEnter((Term) null);
        listener.visitEnter((Universal) null);
        listener.visitEnter((UsedByList) null);
    }

    public void testLeave() throws Exception {
        listener.visitLeave((Add) null);
        listener.visitLeave((Atom) null);
        listener.visitLeave((Author) null);
        listener.visitLeave((AuthorList) null);
        listener.visitLeave((Axiom) null);
        listener.visitLeave((ChangedRule) null);
        listener.visitLeave((ChangedRuleList) null);
        listener.visitLeave((Chapter) null);
        listener.visitLeave((ChapterList) null);
        listener.visitLeave((Conclusion) null);
        listener.visitLeave((ConditionalProof) null);
        listener.visitLeave((ElementList) null);
        listener.visitLeave((Existential) null);
        listener.visitLeave((FormalProof) null);
        listener.visitLeave((FormalProofLine) null);
        listener.visitLeave((FormalProofLineList) null);
        listener.visitLeave((FormalProofList) null);
        listener.visitLeave((Formula) null);
        listener.visitLeave((FunctionDefinition) null);
        listener.visitLeave((Header) null);
        listener.visitLeave((Hypothesis) null);
        listener.visitLeave((Import) null);
        listener.visitLeave((ImportList) null);
        listener.visitLeave((InitialFunctionDefinition) null);
        listener.visitLeave((InitialPredicateDefinition) null);
        listener.visitLeave((Latex) null);
        listener.visitLeave((LatexList) null);
        listener.visitLeave((LinkList) null);
        listener.visitLeave((LiteratureItem) null);
        listener.visitLeave((LiteratureItemList) null);
        listener.visitLeave((Location) null);
        listener.visitLeave((LocationList) null);
        listener.visitLeave((ModusPonens) null);
        listener.visitLeave((Node) null);
        listener.visitLeave((PredicateDefinition) null);
        listener.visitLeave((Proof) null);
        listener.visitLeave((ProofList) null);
        listener.visitLeave((Proposition) null);
        listener.visitLeave((Qedeq) null);
        listener.visitLeave((Reason) null);
        listener.visitLeave((Rename) null);
        listener.visitLeave((Rule) null);
        listener.visitLeave((Section) null);
        listener.visitLeave((SectionList) null);
        listener.visitLeave((Specification) null);
        listener.visitLeave((Subsection) null);
        listener.visitLeave((SubsectionList) null);
        listener.visitLeave((SubsectionType) null);
        listener.visitLeave((SubstFree) null);
        listener.visitLeave((SubstFunc) null);
        listener.visitLeave((SubstPred) null);
        listener.visitLeave((Term) null);
        listener.visitLeave((Universal) null);
        listener.visitLeave((UsedByList) null);
    }

}
