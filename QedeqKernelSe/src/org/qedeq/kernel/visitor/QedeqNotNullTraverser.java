/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.visitor;

import org.qedeq.kernel.base.list.Atom;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.base.module.Author;
import org.qedeq.kernel.base.module.AuthorList;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Chapter;
import org.qedeq.kernel.base.module.ChapterList;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.Header;
import org.qedeq.kernel.base.module.Import;
import org.qedeq.kernel.base.module.ImportList;
import org.qedeq.kernel.base.module.Latex;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.LinkList;
import org.qedeq.kernel.base.module.LiteratureItem;
import org.qedeq.kernel.base.module.LiteratureItemList;
import org.qedeq.kernel.base.module.Location;
import org.qedeq.kernel.base.module.LocationList;
import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proof;
import org.qedeq.kernel.base.module.ProofList;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Section;
import org.qedeq.kernel.base.module.SectionList;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.base.module.Subsection;
import org.qedeq.kernel.base.module.SubsectionList;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.base.module.UsedByList;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;


/**
 * Traverse QEDEQ module. Calls visitors of {@link org.qedeq.kernel.visitor.QedeqVisitor}
 * for non <code>null</code> arguments.
 *
 * @author  Michael Meyling
 */
public class QedeqNotNullTraverser implements QedeqTraverser {

    /** Current context during creation. */
    private ModuleContext currentContext;

    /**
     * These methods are called if a node is visited. To start the whole process just call
     * {@link #accept(Qedeq)}.
     */
    private QedeqVisitor visitor;

    /** Is sub node traverse currently blocked? */
    private boolean blocked;

    /**
     * Constructor.
     *
     * @param   globalContext   Module location information.
     * @param   visitor         These methods are called if a node is visited.
     */
    public QedeqNotNullTraverser(final ModuleAddress globalContext, final QedeqVisitor visitor) {
        currentContext = globalContext.createModuleContext();
        this.visitor = visitor;
    }

    public void accept(final Qedeq qedeq) throws ModuleDataException {
        getCurrentContext().setLocationWithinModule("");
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        blocked = false;
        if (qedeq == null) {
            throw new NullPointerException("null QEDEQ module");
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(qedeq);
        if (qedeq.getHeader() != null) {
            getCurrentContext().setLocationWithinModule(context + "getHeader()");
            accept(qedeq.getHeader());
        }
        if (qedeq.getChapterList() != null) {
            getCurrentContext().setLocationWithinModule(context + "getChapterList()");
            accept(qedeq.getChapterList());
        }
        if (qedeq.getLiteratureItemList() != null) {
            getCurrentContext().setLocationWithinModule(context + "getLiteratureItemList()");
            accept(qedeq.getLiteratureItemList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(qedeq);
        setLocationWithinModule(context);
    }

    public void accept(final Header header) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || header == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(header);
        if (header.getSpecification() != null) {
            setLocationWithinModule(context + ".getSpecification()");
            accept(header.getSpecification());
        }
        if (header.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            accept(header.getTitle());
        }
        if (header.getSummary() != null) {
            setLocationWithinModule(context + ".getSummary()");
            accept(header.getSummary());
        }
        if (header.getAuthorList() != null) {
            setLocationWithinModule(context + ".getAuthorList()");
            accept(header.getAuthorList());
        }
        if (header.getImportList() != null) {
            setLocationWithinModule(context + ".getImportList()");
            accept(header.getImportList());
        }
        if (header.getUsedByList() != null) {
            setLocationWithinModule(context + ".getUsedByList()");
           accept(header.getUsedByList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(header);
        setLocationWithinModule(context);
    }

    public void accept(final UsedByList usedByList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || usedByList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(usedByList);
        for (int i = 0; i < usedByList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(usedByList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(usedByList);
        setLocationWithinModule(context);
    }

    public void accept(final ImportList importList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || importList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(importList);
        for (int i = 0; i < importList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(importList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(importList);
        setLocationWithinModule(context);
    }

    public void accept(final Import imp) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || imp == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(imp);
        if (imp.getSpecification() != null) {
            setLocationWithinModule(context + ".getSpecification()");
            accept(imp.getSpecification());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(imp);
        setLocationWithinModule(context);
    }

    public void accept(final Specification specification) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || specification == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(specification);
        if (specification.getLocationList() != null) {
            setLocationWithinModule(context + ".getLocationList()");
            accept(specification.getLocationList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(specification);
        setLocationWithinModule(context);
    }

    public void accept(final LocationList locationList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || locationList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(locationList);
        for (int i = 0; i < locationList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(locationList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(locationList);
        setLocationWithinModule(context);
    }

    public void accept(final Location location) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || location == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(location);
        setLocationWithinModule(context);
        visitor.visitLeave(location);
        setLocationWithinModule(context);
    }

    public void accept(final AuthorList authorList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || authorList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(authorList);
        for (int i = 0; i < authorList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(authorList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(authorList);
        setLocationWithinModule(context);
    }

    public void accept(final Author author) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || author == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(author);
        if (author.getName() != null) {
            setLocationWithinModule(context + ".getName()");
            accept(author.getName());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(author);
        setLocationWithinModule(context);
    }

    public void accept(final ChapterList chapterList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || chapterList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(chapterList);
        for (int i = 0; i < chapterList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(chapterList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(chapterList);
        setLocationWithinModule(context);
    }

    public void accept(final Chapter chapter) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || chapter == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(chapter);
        if (chapter.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            accept(chapter.getTitle());
        }
        if (chapter.getIntroduction() != null) {
            setLocationWithinModule(context + ".getIntroduction()");
            accept(chapter.getIntroduction());
        }
        if (chapter.getSectionList() != null) {
            setLocationWithinModule(context + ".getSectionList()");
            accept(chapter.getSectionList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(chapter);
        setLocationWithinModule(context);
    }

    public void accept(final LiteratureItemList literatureItemList)
            throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || literatureItemList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(literatureItemList);
        for (int i = 0; i < literatureItemList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(literatureItemList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(literatureItemList);
        setLocationWithinModule(context);
    }

    public void accept(final LiteratureItem item) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || item == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(item);
        if (item.getItem() != null) {
            setLocationWithinModule(context + ".getItem()");
            accept(item.getItem());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(item);
        setLocationWithinModule(context);
    }

    public void accept(final SectionList sectionList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || sectionList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(sectionList);
        for (int i = 0; i < sectionList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(sectionList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(sectionList);
        setLocationWithinModule(context);
    }

    public void accept(final Section section) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || section == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(section);
        if (section.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            accept(section.getTitle());
        }
        if (section.getIntroduction() != null) {
            setLocationWithinModule(context + ".getIntroduction()");
            accept(section.getIntroduction());
        }
        if (section.getSubsectionList() != null) {
            setLocationWithinModule(context + ".getSubsectionList()");
            accept(section.getSubsectionList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(section);
        setLocationWithinModule(context);
    }

    public void accept(final SubsectionList subsectionList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || subsectionList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(subsectionList);
        for (int i = 0; i < subsectionList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            // TODO mime 20050608: here the Subsection context is type dependently specified
            if (subsectionList.get(i) instanceof Subsection) {
                accept((Subsection) subsectionList.get(i));
            } else if (subsectionList.get(i) instanceof Node) {
                accept((Node) subsectionList.get(i));
            } else if (subsectionList.get(i) == null) {
                // ignore
            } else {
                throw new IllegalArgumentException("unexpected subsection type: "
                    + subsectionList.get(i).getClass());
            }
        }
        setLocationWithinModule(context);
        visitor.visitLeave(subsectionList);
        setLocationWithinModule(context);
    }

    public void accept(final Subsection subsection) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || subsection == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(subsection);
        if (subsection.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            accept(subsection.getTitle());
        }
        if (subsection.getLatex() != null) {
            setLocationWithinModule(context + ".getLatex()");
            accept(subsection.getLatex());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(subsection);
        setLocationWithinModule(context);
    }

    public void accept(final Node node) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || node == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(node);
        if (node.getName() != null) {
            setLocationWithinModule(context + ".getName()");
            accept(node.getName());
        }
        if (node.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            accept(node.getTitle());
        }
        if (node.getPrecedingText() != null) {
            setLocationWithinModule(context + ".getPrecedingText()");
            accept(node.getPrecedingText());
        }
        if (node.getNodeType() != null) {
            setLocationWithinModule(context + ".getNodeType()");
            if (node.getNodeType() instanceof Axiom) {
                setLocationWithinModule(context + ".getNodeType().getAxiom()");
                accept((Axiom) node.getNodeType());
            } else if (node.getNodeType() instanceof PredicateDefinition) {
                setLocationWithinModule(context + ".getNodeType().getPredicateDefinition()");
                accept((PredicateDefinition) node.getNodeType());
            } else if (node.getNodeType() instanceof FunctionDefinition) {
                setLocationWithinModule(context + ".getNodeType().getFunctionDefinition()");
                accept((FunctionDefinition) node.getNodeType());
            } else if (node.getNodeType() instanceof Proposition) {
                setLocationWithinModule(context + ".getNodeType().getProposition()");
                accept((Proposition) node.getNodeType());
            } else if (node.getNodeType() instanceof Rule) {
                setLocationWithinModule(context + ".getNodeType().getRule()");
                accept((Rule) node.getNodeType());
            } else {
                throw new IllegalArgumentException("unexpected node type: "
                    + node.getNodeType().getClass());
            }
        }
        if (node.getSucceedingText() != null) {
            setLocationWithinModule(context + ".getSucceedingText()");
            accept(node.getSucceedingText());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(node);
        setLocationWithinModule(context);
    }

    public void accept(final Axiom axiom) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || axiom == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(axiom);
        if (axiom.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula()");
            accept(axiom.getFormula());
        }
        if (axiom.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            accept(axiom.getDescription());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(axiom);
        setLocationWithinModule(context);
    }

    public void accept(final PredicateDefinition definition)
            throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(definition);
        if (definition.getVariableList() != null) {
            setLocationWithinModule(context + ".getVariableList()");
            accept(definition.getVariableList());
        }
        if (definition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula()");
            accept(definition.getFormula());
        }
        if (definition.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            accept(definition.getDescription());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(definition);
        setLocationWithinModule(context);
    }

    public void accept(final FunctionDefinition definition) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(definition);
        if (definition.getVariableList() != null) {
            setLocationWithinModule(context + ".getVariableList()");
            accept(definition.getVariableList());
        }
        if (definition.getTerm() != null) {
            setLocationWithinModule(context + ".getTerm()");
            accept(definition.getTerm());
        }
        if (definition.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            accept(definition.getDescription());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(definition);
        setLocationWithinModule(context);
    }

    public void accept(final Proposition proposition) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || proposition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(proposition);
        if (proposition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula()");
            accept(proposition.getFormula());
        }
        if (proposition.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            accept(proposition.getDescription());
        }
        if (proposition.getProofList() != null) {
            setLocationWithinModule(context + ".getProofList()");
            accept(proposition.getProofList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(proposition);
        setLocationWithinModule(context);
    }

    public void accept(final Rule rule) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || rule == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(rule);
        if (rule.getLinkList() != null) {
            setLocationWithinModule(context + ".getLinkList()");
            accept(rule.getLinkList());
        }
        if (rule.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            accept(rule.getDescription());
        }
        if (rule.getProofList() != null) {
            setLocationWithinModule(context + ".getProofList()");
            accept(rule.getProofList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(rule);
        setLocationWithinModule(context);
    }

    public void accept(final LinkList linkList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || linkList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(linkList);
        setLocationWithinModule(context);
        visitor.visitLeave(linkList);
        setLocationWithinModule(context);
    }

    public void accept(final VariableList variableList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || variableList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(variableList);
        for (int i = 0; i < variableList.size(); i++) {
            final String piece = context + ".get(" + i + ")";
            setLocationWithinModule(piece);
            if (variableList.get(i) != null) {
                if (variableList.get(i).isList()) {
                    setLocationWithinModule(piece + ".getList()");
                    accept(variableList.get(i).getList());
                } else if (variableList.get(i).isAtom()) {
                    setLocationWithinModule(piece + ".getAtom()");
                    accept(variableList.get(i).getAtom());
                } else {
                    throw new IllegalArgumentException("unexpected element type: "
                        + variableList.get(i).toString());
                }
            }
        }
        setLocationWithinModule(context);
        visitor.visitLeave(variableList);
        setLocationWithinModule(context);
    }

    public void accept(final ProofList proofList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || proofList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(proofList);
        for (int i = 0; i < proofList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(proofList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(proofList);
        setLocationWithinModule(context);
    }

    public void accept(final Proof proof) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || proof == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(proof);
        if (proof.getNonFormalProof() != null) {
            setLocationWithinModule(context + ".getNonFormalProof()");
            accept(proof.getNonFormalProof());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(proof);
        setLocationWithinModule(context);
    }

    public void accept(final Formula formula) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || formula == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(formula);
        if (formula.getElement() != null) {
            setLocationWithinModule(context + ".getElement()");
            accept(formula.getElement());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(formula);
        setLocationWithinModule(context);
    }

    public void accept(final Term term) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || term == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(term);
        if (term.getElement() != null) {
            setLocationWithinModule(context + ".getElement()");
            accept(term.getElement());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(term);
        setLocationWithinModule(context);
    }

    public void accept(final Element element) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || element == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        if (element.isList()) {
            setLocationWithinModule(context + ".getList()");
            accept(element.getList());
        } else if (element.isAtom()) {
            setLocationWithinModule(context + ".getAtom()");
            accept(element.getAtom());
        } else {
            throw new IllegalArgumentException("unexpected element type: "
                + element.toString());
        }
        setLocationWithinModule(context);
    }

    public void accept(final Atom atom) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || atom == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(atom);
        setLocationWithinModule(context);
        visitor.visitLeave(atom);
        setLocationWithinModule(context);
    }

    public void accept(final ElementList list) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || list == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(list);
        for (int i = 0; i < list.size(); i++) {
            setLocationWithinModule(context + ".getElement(" + i + ")");
            accept(list.getElement(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(list);
        setLocationWithinModule(context);
    }

    public void accept(final LatexList latexList) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || latexList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(latexList);
        for (int i = 0; i < latexList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(latexList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(latexList);
        setLocationWithinModule(context);
    }

    public void accept(final Latex latex) throws ModuleDataException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptException(getCurrentContext());
        }
        if (blocked || latex == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(latex);
        setLocationWithinModule(context);
        visitor.visitLeave(latex);
        setLocationWithinModule(context);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    /**
     * Get current context within original. Remember to use the copy constructor
     * when trying to remember this context!
     *
     * @return  Current context.
     */
    public final ModuleContext getCurrentContext() {
        return currentContext;
    }

    /**
     * Is further traversing blocked?
     *
     * @return  Is further traversing blocked?
     */
    public final boolean getBlocked() {
        return blocked;
    }

    /**
     * Set if further transversing is blocked.
     *
     * @param   blocked Further transversion?
     */
    public final void setBlocked(final boolean blocked) {
        this.blocked = blocked;
    }

}

