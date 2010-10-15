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
import org.qedeq.kernel.base.module.SubsectionType;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.base.module.UsedByList;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.common.ModuleDataException;



/**
 * Basic visitor that makes nothing.
 *
 * @author Michael Meyling
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

    public void visitEnter(final PredicateDefinition predicateDefinition)
            throws ModuleDataException {
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

    public void visitEnter(final VariableList variableList) throws ModuleDataException {
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

    public void visitLeave(final PredicateDefinition predicateDefinition)
            throws ModuleDataException {
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

    public void visitLeave(final VariableList variableList) throws ModuleDataException {
    }

    public void visitLeave(final Atom atom) throws ModuleDataException {
    }

    public void visitLeave(final ElementList list) throws ModuleDataException {
    }
}
