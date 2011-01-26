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

import java.util.Stack;

import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.AuthorList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.Chapter;
import org.qedeq.kernel.se.base.module.ChapterList;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FormalProofList;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Header;
import org.qedeq.kernel.se.base.module.Import;
import org.qedeq.kernel.se.base.module.ImportList;
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
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Term;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.base.module.UsedByList;
import org.qedeq.kernel.se.base.module.VariableList;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.se.dto.module.TermVo;


/**
 * Traverse QEDEQ module. Calls visitors of {@link org.qedeq.kernel.se.visitor.QedeqVisitor}
 * for non <code>null</code> arguments.
 *
 * @author  Michael Meyling
 */
public class QedeqNotNullTraverser implements QedeqTraverser {

    /** Current context during creation. */
    private final ModuleContext currentContext;

    /** Readable traverse location info. */
    private final Stack location = new Stack();

    /** Herein are various counters for the current node. */
    private QedeqNumbers data;

    /** Converts chapter and other titles into text. */
    private final LatexList2Text transform = new LatexList2Text();

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
        setLocation("started");
        if (qedeq == null) {
            throw new NullPointerException("null QEDEQ module");
        }
        data = new QedeqNumbers(
            (qedeq.getHeader() != null && qedeq.getHeader().getImportList() != null
                ? qedeq.getHeader().getImportList().size() : 0),
            (qedeq.getChapterList() != null ? qedeq.getChapterList().size() : 0)
        );
        getCurrentContext().setLocationWithinModule("");
        checkForInterrupt();
        blocked = false;
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
        setLocation("finished");
        data.setFinished(true);
    }

    /**
     * Check if current thread is interrupted.
     *
     * @throws  InterruptException  We were interrupted.
     */
    private void checkForInterrupt() throws InterruptException {
        if (Thread.interrupted()) {
            throw new InterruptException(getCurrentContext());
        }
    }

    public void accept(final Header header) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || header == null) {
            return;
        }
        setLocation("analyzing header");
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
        checkForInterrupt();
        if (blocked || usedByList == null) {
            return;
        }
        location.push("working on used by list");
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(usedByList);
        for (int i = 0; i < usedByList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(usedByList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(usedByList);
        setLocationWithinModule(context);
        location.pop();
    }

    public void accept(final ImportList importList) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || importList == null) {
            return;
        }
        location.push("working on import list");
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(importList);
        for (int i = 0; i < importList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(importList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(importList);
        setLocationWithinModule(context);
        location.pop();
    }

    public void accept(final Import imp) throws ModuleDataException {
        data.increaseImportNumber();
        checkForInterrupt();
        if (blocked || imp == null) {
            return;
        }
        location.push("import " + data.getImportNumber() + ": " + imp.getLabel());
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(imp);
        if (imp.getSpecification() != null) {
            setLocationWithinModule(context + ".getSpecification()");
            accept(imp.getSpecification());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(imp);
        setLocationWithinModule(context);
        location.pop();
    }

    public void accept(final Specification specification) throws ModuleDataException {
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
        if (blocked || chapter == null) {
            return;
        }
        data.increaseChapterNumber(
                (chapter.getSectionList() != null ? chapter.getSectionList().size() : 0),
                chapter.getNoNumber() == null || !chapter.getNoNumber().booleanValue()
            );
        if (data.isChapterNumbering()) {
            setLocation("Chapter " + data.getChapterNumber() + " "
                + transform.transform(chapter.getTitle()));
        } else {
            setLocation(transform.transform(chapter.getTitle()));
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
        checkForInterrupt();
        if (blocked || literatureItemList == null) {
            return;
        }
        setLocation("working on literature list");
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
        if (blocked || section == null) {
            return;
        }
        data.increaseSectionNumber(
                (section.getSubsectionList() != null ? section.getSubsectionList().size() : 0),
                section.getNoNumber() == null || !section.getNoNumber().booleanValue()
            );
        String title = "";
        if (data.isChapterNumbering()) {
            title += data.getChapterNumber();
        }
        if (data.isSectionNumbering()) {
            title += (title.length() > 0 ? "." : "") + data.getSectionNumber();
        }
        if (section.getTitle() != null) {
            title += " " + transform.transform(section.getTitle());
        }
        location.push(title);
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
        location.pop();
    }

    public void accept(final SubsectionList subsectionList) throws ModuleDataException {
        checkForInterrupt();
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
        checkForInterrupt();
        if (blocked || subsection == null) {
            return;
        }
        data.increaseSubsectionNumber();
        String title = "";
        if (data.isChapterNumbering()) {
            title += data.getChapterNumber();
        }
        if (data.isSectionNumbering()) {
            title += (title.length() > 0 ? "." : "") + data.getSectionNumber();
        }
        title += (title.length() > 0 ? "." : "") + data.getSubsectionNumber();
        if (subsection.getTitle() != null) {
            title += " " + transform.transform(subsection.getTitle());
        }
        title = title + " [" + subsection.getId() + "]";
        location.push(title);
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
        location.pop();
    }

    public void accept(final Node node) throws ModuleDataException {
        checkForInterrupt();
        data.increaseNodeNumber();
        if (blocked || node == null) {
            return;
        }
        String title = "";
        if (node.getTitle() != null) {
            title = transform.transform(node.getTitle());
        }
        title = title + " [" + node.getId() + "]";
        location.push(title);
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
        location.pop();
    }

    public void accept(final Axiom axiom) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || axiom == null) {
            return;
        }
        data.increaseAxiomNumber();
        location.set(location.size() - 1, "Axiom " + data.getAxiomNumber() + " "
            + (String) location.lastElement());
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
        checkForInterrupt();
        if (blocked || definition == null) {
            return;
        }
        data.increasePredicateDefinitionNumber();
        location.set(location.size() - 1, "Definition " + (data.getPredicateDefinitionNumber()
             + data.getFunctionDefinitionNumber()) + " "
             + (String) location.lastElement());
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
        checkForInterrupt();
        if (blocked || definition == null) {
            return;
        }
        data.increaseFunctionDefinitionNumber();
        location.set(location.size() - 1, "Definition " + (data.getPredicateDefinitionNumber()
                + data.getFunctionDefinitionNumber()) + " "
                + (String) location.lastElement());
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
        checkForInterrupt();
        if (blocked || proposition == null) {
            return;
        }
        data.increasePropositionNumber();
        location.set(location.size() - 1, "Proposition " + data.getPropositionNumber() + " "
                + (String) location.lastElement());
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
        if (proposition.getFormalProofList() != null) {
            setLocationWithinModule(context + ".getFormalProofList()");
            accept(proposition.getFormalProofList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(proposition);
        setLocationWithinModule(context);
    }

    public void accept(final Rule rule) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || rule == null) {
            return;
        }
        data.increaseRuleNumber();
        location.set(location.size() - 1, "Rule " + data.getRuleNumber() + " "
                + (String) location.lastElement());
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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

    public void accept(final FormalProofList proofList) throws ModuleDataException {
        checkForInterrupt();
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

    public void accept(final FormalProof proof) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || proof == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(proof);
        if (proof.getPrecedingText() != null) {
            setLocationWithinModule(context + ".getPrecedingText()");
            accept(proof.getFormalProofLineList());
        }
        if (proof.getFormalProofLineList() != null) {
            setLocationWithinModule(context + ".getFormalProofLineList()");
            accept(proof.getFormalProofLineList());
        }
        if (proof.getSucceedingText() != null) {
            setLocationWithinModule(context + ".getSucceedingText()");
            accept(proof.getFormalProofLineList());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(proof);
        setLocationWithinModule(context);
    }

    public void accept(final FormalProofLineList proofLineList) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || proofLineList == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(proofLineList);
        for (int i = 0; i < proofLineList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            accept(proofLineList.get(i));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(proofLineList);
        setLocationWithinModule(context);
    }

    public void accept(final FormalProofLine proofLine) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || proofLine == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(proofLine);
        if (proofLine.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula()");
            accept(proofLine.getFormula());
        }
        if (proofLine.getReason() != null) {
            final Reason reason = proofLine.getReason();
            // TODO 20110124: here the context is type dependently specified
            if (reason instanceof ModusPonens) {
                setLocationWithinModule(context + ".getModusPonens()");
                accept(proofLine.getModusPonens());
            } else if (reason instanceof Add) {
                setLocationWithinModule(context + ".getAdd()");
                accept(proofLine.getAdd());
            } else if (reason instanceof Rename) {
                setLocationWithinModule(context + ".getRename()");
                accept(proofLine.getRename());
            } else if (reason instanceof SubstFree) {
                setLocationWithinModule(context + ".getSubstFree()");
                accept(proofLine.getSubstFree());
            } else if (reason instanceof SubstFunc) {
                setLocationWithinModule(context + ".getSubstFunc()");
                accept(proofLine.getSubstFunc());
            } else if (reason instanceof SubstPred) {
                setLocationWithinModule(context + ".getSubstPred()");
                accept(proofLine.getSubstPred());
            } else if (reason instanceof Existential) {
                setLocationWithinModule(context + ".getExistential()");
                accept(proofLine.getExistential());
            } else if (reason instanceof Universal) {
                setLocationWithinModule(context + ".getUniversal()");
                accept(proofLine.getUniversal());
            } else {
                throw new IllegalArgumentException("unexpected reason type: "
                    + reason.getClass());
            }
        }
        setLocationWithinModule(context);
        visitor.visitLeave(proofLine);
        setLocationWithinModule(context);
    }

    public void accept(final ModusPonens reason) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || reason == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(reason);
        setLocationWithinModule(context);
        visitor.visitLeave(reason);
        setLocationWithinModule(context);
    }

    public void accept(final Add reason) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || reason == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(reason);
        setLocationWithinModule(context);
        visitor.visitLeave(reason);
        setLocationWithinModule(context);
    }

    public void accept(final Rename reason) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || reason == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(reason);
        if (reason.getOriginalSubjectVariable() != null) {
            setLocationWithinModule(context + ".getOriginalSubjectVariable()");
            accept(reason.getOriginalSubjectVariable());
        }
        if (reason.getReplacementSubjectVariable() != null) {
            setLocationWithinModule(context + ".getReplacementSubjectVariable()");
            accept(reason.getReplacementSubjectVariable());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(reason);
        setLocationWithinModule(context);
    }

    public void accept(final SubstFree reason) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || reason == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(reason);
        if (reason.getSubjectVariable() != null) {
            setLocationWithinModule(context + ".getSubjectVariable()");
            accept(reason.getSubjectVariable());
        }
        if (reason.getSubstituteTerm() != null) {
            setLocationWithinModule(context + ".getSubstituteTerm()");
            accept(new TermVo(reason.getSubstituteTerm()));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(reason);
        setLocationWithinModule(context);
    }

    public void accept(final SubstFunc reason) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || reason == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(reason);
        if (reason.getFunctionVariable() != null) {
            setLocationWithinModule(context + ".getFunctionVariable()");
            accept(reason.getFunctionVariable());
        }
        if (reason.getSubstituteTerm() != null) {
            setLocationWithinModule(context + ".getSubstituteTerm()");
            accept(new TermVo(reason.getSubstituteTerm()));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(reason);
        setLocationWithinModule(context);
    }

    public void accept(final SubstPred reason) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || reason == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(reason);
        if (reason.getPredicateVariable() != null) {
            setLocationWithinModule(context + ".getPredicateVariable()");
            accept(reason.getPredicateVariable());
        }
        if (reason.getSubstituteFormula() != null) {
            setLocationWithinModule(context + ".getSubstituteFormula()");
            accept(new FormulaVo(reason.getSubstituteFormula()));
        }
        setLocationWithinModule(context);
        visitor.visitLeave(reason);
        setLocationWithinModule(context);
    }

    public void accept(final Existential reason) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || reason == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(reason);
        if (reason.getSubjectVariable() != null) {
            setLocationWithinModule(context + ".getSubjectVariable()");
            accept(reason.getSubjectVariable());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(reason);
        setLocationWithinModule(context);
    }

    public void accept(final Universal reason) throws ModuleDataException {
        checkForInterrupt();
        if (blocked || reason == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        visitor.visitEnter(reason);
        if (reason.getSubjectVariable() != null) {
            setLocationWithinModule(context + ".getSubjectVariable()");
            accept(reason.getSubjectVariable());
        }
        setLocationWithinModule(context);
        visitor.visitLeave(reason);
        setLocationWithinModule(context);
    }

    public void accept(final Formula formula) throws ModuleDataException {
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
        checkForInterrupt();
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
     * Set if further traverse is blocked.
     *
     * @param   blocked Further transversion?
     */
    public final void setBlocked(final boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * Get calculated visit percentage.
     *
     * @return  Value between 0 and 100.
     */
    public double getVisitPercentage() {
        if (data == null) {
            return 0;
        }
        return data.getVisitPercentage();
    }

    /**
     * Set absolute location description.
     *
     * @param   text    Description.
     */
    private void setLocation(final String text) {
        location.setSize(0);
        location.push(text);
    }
    /**
     * Get calculated visit percentage.
     *
     * @return  Value between 0 and 100.
     */
    public String getVisitAction() {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < location.size(); i++) {
            if (i > 0) {
                buffer.append(" / ");
            }
            buffer.append(location.get(i));
        }
        return buffer.toString();
    }

    /**
     * Get copy of current counters.
     *
     * @return  Values of various counters.
     */
    public QedeqNumbers getCurrentNumbers() {
        return new QedeqNumbers(data);
    }

}

