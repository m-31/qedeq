/* $Id: QedeqTraverser.java,v 1.1 2008/01/26 12:39:10 m31 Exp $
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

package org.qedeq.kernel.bo.visitor;

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
import org.qedeq.kernel.bo.module.ModuleDataException;

/**
 * Traverse a QEDEQ module and visit all elements.
 * All contained elements are called recursively.
 * See {@link org.qedeq.kernel.bo.visitor.QedeqVisitor}.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public interface QedeqTraverser {

    /**
     * Start with the top structure of a QEDEQ module.
     *
     * @param   qedeq          Traverse this element. Must not be <code>null</code>.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Qedeq qedeq) throws ModuleDataException;

    /**
     * Traverse header.
     *
     * @param   header         Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Header header) throws ModuleDataException;

    /**
     * Traverse used by list.
     *
     * @param   usedByList     Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final UsedByList usedByList) throws ModuleDataException;

    /**
     * Traverse import list.
     *
     * @param   importList     Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final ImportList importList) throws ModuleDataException;

    /**
     * Traverse import.
     *
     * @param   imp            Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Import imp) throws ModuleDataException;

    /**
     * Traverse specification.
     *
     * @param   specification  Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Specification specification) throws ModuleDataException;

    /**
     * Traverse location list.
     *
     * @param   locationList   Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final LocationList locationList) throws ModuleDataException;

    /**
     * Traverse location.
     *
     * @param   location       Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Location location) throws ModuleDataException;

    /**
     * Traverse author list.
     *
     * @param   authorList      Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final AuthorList authorList) throws ModuleDataException;

    /**
     * Traverse author.
     *
     * @param   author          Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Author author) throws ModuleDataException;

    /**
     * Traverse chapter list.
     *
     * @param   chapterList     Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final ChapterList chapterList) throws ModuleDataException;

    /**
     * Traverse chapter.
     *
     * @param   chapter         Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Chapter chapter) throws ModuleDataException;

    /**
     * Traverse literature item list.
     *
     * @param   literatureItemList  Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final LiteratureItemList literatureItemList) throws ModuleDataException;

    /**
     * Traverse literature item.
     *
     * @param   literatureItem  Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final LiteratureItem literatureItem) throws ModuleDataException;

    /**
     * Traverse section list.
     *
     * @param   sectionList     Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final SectionList sectionList) throws ModuleDataException;

    /**
     * Traverse section.
     *
     * @param   section         Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Section section) throws ModuleDataException;

    /**
     * Traverse subsection list.
     *
     * @param   subsectionList  Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final SubsectionList subsectionList) throws ModuleDataException;

    /**
     * Traverse subsection list.
     *
     * @param   subsection      Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Subsection subsection) throws ModuleDataException;

    /**
     * Traverse node.
     *
     * @param   node            Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Node node) throws ModuleDataException;

    /**
     * Traverse axiom.
     *
     * @param   axiom           Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Axiom axiom) throws ModuleDataException;

    /**
     * Traverse predicate definition.
     *
     * @param   definition      Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final PredicateDefinition definition) throws ModuleDataException;

    /**
     * Traverse function definition.
     *
     * @param   definition      Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final FunctionDefinition definition) throws ModuleDataException;

    /**
     * Traverse proposition.
     *
     * @param   proposition     Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Proposition proposition) throws ModuleDataException;

    /**
     * Traverse rule.
     *
     * @param   rule            Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Rule rule) throws ModuleDataException;

    /**
     * Traverse link list.
     *
     * @param   linkList        Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final LinkList linkList) throws ModuleDataException;

    /**
     * Traverse variable list.
     *
     * @param   variableList    Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final VariableList variableList) throws ModuleDataException;

    /**
     * Traverse proof list.
     *
     * @param   proofList       Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final ProofList proofList) throws ModuleDataException;

    /**
     * Traverse proof.
     *
     * @param   proof           Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Proof proof) throws ModuleDataException;

    /**
     * Traverse formula.
     *
     * @param   formula         Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Formula formula) throws ModuleDataException;

    /**
     * Traverse term.
     *
     * @param   term            Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Term term) throws ModuleDataException;

    /**
     * Traverse latex list.
     *
     * @param   latexList       Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final LatexList latexList) throws ModuleDataException;

    /**
     * Traverse latex.
     *
     * @param   latex           Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Latex latex) throws ModuleDataException;

    /**
     * Traverse element.
     *
     * @param   element         Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Element element) throws ModuleDataException;

    /**
     * Traverse atom.
     *
     * @param   atom            Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final Atom atom) throws ModuleDataException;

    /**
     * Traverse element list.
     *
     * @param   list            Traverse this element.
     * @throws  ModuleDataException     Severe error during occurred.
     */
    public void accept(final ElementList list) throws ModuleDataException;

}
