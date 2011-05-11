/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.AuthorList;
import org.qedeq.kernel.se.base.module.Axiom;
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
import org.qedeq.kernel.se.base.module.ReasonType;
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
import org.qedeq.kernel.se.common.ModuleDataException;

/**
 * Here are all elements to visit assembled that can be visited within a  QEDEQ module.
 *
 * @author Michael Meyling
 */
public interface QedeqVisitor extends ListVisitor {

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   author              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Author author) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   authorList          Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(AuthorList authorList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   axiom               Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Axiom axiom) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   chapter             Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Chapter chapter) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   chapterList         Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(ChapterList chapterList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   formula       Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Formula formula) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   functionDefinition  Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(InitialFunctionDefinition functionDefinition) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   functionDefinition  Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(FunctionDefinition functionDefinition) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   header              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Header header) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   imp                 Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Import imp) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   importList          Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(ImportList importList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   latex               Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Latex latex) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   latexList           Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(LatexList latexList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   linkList            Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(LinkList linkList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   literatureItem      Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(LiteratureItem literatureItem) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   literatureItemList  Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(LiteratureItemList literatureItemList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   location            Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Location location) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   locationList        Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(LocationList locationList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   node                Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Node node) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   predicateDefinition Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(InitialPredicateDefinition predicateDefinition) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   predicateDefinition Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(PredicateDefinition predicateDefinition) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   proof               Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(FormalProof proof) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   proofList       Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(FormalProofList proofList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   proofLine           Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(FormalProofLine proofLine) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reasonType          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(ReasonType reasonType) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(ModusPonens reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Add reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Rename reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(SubstFree reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(SubstFunc reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(SubstPred reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Existential reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Universal reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   reason              Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(ConditionalProof reason) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   hypothesis          Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Hypothesis hypothesis) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   conclusion          Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Conclusion conclusion) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   proofLineList       Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(FormalProofLineList proofLineList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   proof               Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Proof proof) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   proofList           Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(ProofList proofList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   proposition         Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Proposition proposition) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   qedeq               Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Qedeq qedeq) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   rule                Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Rule rule) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   section             Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Section section) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   sectionList         Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(SectionList sectionList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   specification       Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Specification specification) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   subsection          Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Subsection subsection) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   subsectionList      Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(SubsectionList subsectionList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   subsectionType      Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(SubsectionType subsectionType) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   term      Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(Term term) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   usedByList          Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(UsedByList usedByList) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param   variableList        Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(VariableList variableList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   author              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Author author) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   authorList          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(AuthorList authorList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   axiom               End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Axiom axiom) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   chapter             End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Chapter chapter) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   chapterList         End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(ChapterList chapterList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   formula       End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Formula formula) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   functionDefinition  End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(InitialFunctionDefinition functionDefinition) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   functionDefinition  End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(FunctionDefinition functionDefinition) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   header              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Header header) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   imp                 End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Import imp) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   importList          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(ImportList importList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   latex               End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Latex latex) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   latexList           End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(LatexList latexList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   linkList            End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(LinkList linkList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   literatureItem      End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(LiteratureItem literatureItem) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   literatureItemList  End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(LiteratureItemList literatureItemList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   location            End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Location location) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   locationList        End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(LocationList locationList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   authorList          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Node authorList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   predicateDefinition End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(InitialPredicateDefinition predicateDefinition) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   predicateDefinition End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(PredicateDefinition predicateDefinition) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   proofList       End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(FormalProofList proofList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   proof               End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(FormalProof proof) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   proofLine           End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(FormalProofLine proofLine) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reasonType          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(ReasonType reasonType) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   proofLineList       End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(FormalProofLineList proofLineList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(ModusPonens reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Add reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Rename reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(SubstFree reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(SubstFunc reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(SubstPred reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Existential reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Universal reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   reason              End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(ConditionalProof reason) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   hypothesis          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Hypothesis hypothesis) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   conclusion          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Conclusion conclusion) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   proof               End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Proof proof) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   proofList           End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(ProofList proofList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   proposition         End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Proposition proposition) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   qedeq               End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Qedeq qedeq) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   rule                End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Rule rule) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   section             End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Section section) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   sectionList         End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(SectionList sectionList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   specification       End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Specification specification) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   subsection          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Subsection subsection) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   subsectionList      End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(SubsectionList subsectionList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   subsectionType      End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(SubsectionType subsectionType) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   term                End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(Term term) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   usedByList          End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(UsedByList usedByList) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param   variableList        End visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitLeave(VariableList variableList) throws ModuleDataException;

}
