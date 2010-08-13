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

package org.qedeq.kernel.xml.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.Enumerator;
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
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.xml.tracker.SimpleXPath;


/**
 * Map content string to SimpleXPath string. This class makes it possible to transfer an location
 * of an {@link org.qedeq.kernel.base.module.Qedeq} object into an XPath like position description
 * for an XML file representation of that object.
 *
 * <p>
 * See {@link #getXPath(ModuleContext, Qedeq)} for further details.
 *
 * <p>
 * TODO mime 20070217: It seems to work this way but: this class assumes that we can find
 * QEDEQ/CHAPTER[2]/SECTION[4]/SUBSECTIONS/SUBSECTION[2]
 * even if we have some ../NODE s inbetween.
 * (Example: NODE, NODE, SUBSECTION, NODE, SUBSECTION, NODE..)
 *
 * Is this still a correct XPath? (Old solution was usage of "*")
 * Seems ok for official XPath specification, but does it work for our SimpleXPathFinder?
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class Context2SimpleXPath extends AbstractModuleVisitor {

    /** This class. */
    private static final Class CLASS = Context2SimpleXPath.class;

    /** Traverse QEDEQ module with this traverser. */
    private QedeqNotNullTraverser traverser;

    /** QEDEQ object to work on. */
    private Qedeq qedeq;

    /** Search for this context. */
    private final ModuleContext find;

    /** We are currently at this position. */
    private SimpleXPath current;

    /** Element stack. */
    private final List elements;

    /** Current stack level. */
    private int level;

    /** Is the current context already matching the beginning of the search context? */
    private boolean matching;

    /** Last matching begin of search context. See {@link #matching}. */
    private String matchingBegin;

    /** Corresponding XPath for the {@link #matchingBegin}. */
    private SimpleXPath matchingPath;

    /**
     * Constructor.
     *
     * @param   find    Find this location.
     * @param   qedeq   Within this QEDEQ object.
     */
    private Context2SimpleXPath(final ModuleContext find, final Qedeq qedeq) {
        this.qedeq = qedeq;
        traverser = new QedeqNotNullTraverser(find.getModuleLocation(), this);
        this.find = find;
        elements = new ArrayList(20);
    }

    /**
     * This method finds a {@link ModuleContext} something like<br>
     * <code>
     * getChapterList().get(4).getSectionList().get(0).getSubsectionList().get(4).getLatex().get(0)
     * </code><br>
     * within a {@link Qedeq} module and returns a kind of XPath location for an associated
     * XML document:<br>
     * <code>QEDEQ/CHAPTER[5]/SECTION/SUBSECTIONS/SUBSECTION[2]/TEXT/LATEX</code>
     *
     * <p>
     * At this example one can already see that <code>getSubsectionList().get(4)</code> is
     * transformed into <code>SUBSECTIONS/SUBSECTION[2]</code>. This is due to the fact that
     * <code>SUBSECTION</code> contains a sequence of <code>SUBSECTION</code> or <code>NODE</code>
     * elements. The transformation depends not only from the context but also from
     * the concrete QEDEQ module.
     *
     * <p>
     * Especially the transformation of formula location information in their XML counterpart
     * demands parsing the whole formula.
     *
     * @param   find    Find this location.
     * @param   qedeq   Within this QEDEQ object.
     * @return  XPath for this location in the XML document.
     * @throws  ModuleDataException
     */
    public static SimpleXPath getXPath(final ModuleContext find, final Qedeq qedeq)
            throws ModuleDataException {
        final Context2SimpleXPath converter = new Context2SimpleXPath(find, qedeq);
        return converter.find();
    }

    private final SimpleXPath find() throws ModuleDataException {
        final String method = "find()";
        Trace.paramInfo(CLASS, this, method, "find", find);
        elements.clear();
        level = 0;
        current = new SimpleXPath();
        try {
            traverser.accept(qedeq);
        } catch (LocationFoundException e) {
            Trace.paramInfo(CLASS, this, method, "location found", current);
            return current;
        }
        Trace.param(CLASS, this, method, "level", level);  // level should be equal to zero now
        Trace.info(CLASS, this, method, "location was not found");
        throw new LocationNotFoundException(find);
    }

    public final void visitEnter(final Qedeq qedeq) throws ModuleDataException {
        enter("QEDEQ");
        final String method = "visitEnter(Qedeq)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final Qedeq qedeq) {
        leave();
    }

    public final void visitEnter(final Header header) throws ModuleDataException {
        enter("HEADER");
        final String method = "visitEnter(Header)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getEmail()");
        current.setAttribute("email");
        checkIfFound();
    }

    public final void visitLeave(final Header header) {
        leave();
    }

    public final void visitEnter(final Specification specification) throws ModuleDataException {
        enter("SPECIFICATION");
        final String method = "visitEnter(Specification)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getName()");
        current.setAttribute("name");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getRuleVersion()");
        current.setAttribute("ruleVersion");
        checkIfFound();
    }

    public final void visitLeave(final Specification specification) {
        leave();
    }

    public final void visitEnter(final LatexList latexList) throws ModuleDataException {
        final String method = "visitEnter(LatexList)";
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        final String name;
        if (context.endsWith(".getTitle()")) {
            name = "TITLE";
        } else if (context.endsWith(".getSummary()")) {
            name = "ABSTRACT";
        } else if (context.endsWith(".getIntroduction()")) {
            name = "INTRODUCTION";
        } else if (context.endsWith(".getName()")) {
            name = "NAME";
        } else if (context.endsWith(".getPrecedingText()")) {
            name = "PRECEDING";
        } else if (context.endsWith(".getSucceedingText()")) {
            name = "SUCCEEDING";
        } else if (context.endsWith(".getLatex()")) {
            name = "TEXT";
        } else if (context.endsWith(".getDescription()")) {
            name = "DESCRIPTION";
        } else if (context.endsWith(".getNonFormalProof()")) {  // no extra XSD element
            name = null;
        } else if (context.endsWith(".getItem()")) {            // no extra XSD element
            name = null;
        } else {    // programming error
            throw new IllegalArgumentException("unknown LatexList " + context);
        }
        Trace.param(CLASS, this, method, "name", name);
        if (name != null) {
            enter(name);
        }
        Trace.param(CLASS, this, method, "current", current);

        checkMatching(method);
    }

    public final void visitLeave(final LatexList latexList) {
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        if (!context.endsWith(".getNonFormalProof()")       // no extra XSD element
                && !context.endsWith(".getItem()")) {
            leave();
        }
    }

    public final void visitEnter(final Latex latex) throws ModuleDataException {
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        if (context.indexOf(".getAuthorList().get(") >= 0) {    // TODO mime 20070216: why is the
            enter("NAME");                                      // XSD so cruel???
        }
        enter("LATEX");
        final String method = "visitEnter(Latex)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getLanguage()");
        current.setAttribute("language");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getLatex()");
        current.setAttribute(null); // element character data of LATEX is LaTeX content
        checkIfFound();
    }

    public final void visitLeave(final Latex latex) {
        // because NAME of AUTHOR/NAME/LATEX has no equivalent in interfaces:
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        if (context.indexOf(".getAuthorList().get(") >= 0) {
            leave();
        }
        leave();
    }

    public final void visitEnter(final LocationList locationList) throws ModuleDataException {
        enter("LOCATIONS");
        final String method = "visitEnter(LocationList)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);

    }

    public final void visitLeave(final LocationList locationList) {
        leave();
    }

    public final void visitEnter(final Location location) throws ModuleDataException {
        enter("LOCATION");
        final String method = "visitEnter(Location)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getLocation()");
        current.setAttribute("value");
        checkIfFound();
    }

    public final void visitLeave(final Location location) {
        leave();
    }

    public final void visitEnter(final AuthorList authorList) throws ModuleDataException {
        enter("AUTHORS");
        final String method = "visitEnter(AuthorList)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final AuthorList authorList) {
        leave();
    }

    public final void visitEnter(final Author author) throws ModuleDataException {
        enter("AUTHOR");
        final String method = "visitEnter(Author)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getEmail()");
        current.setAttribute("email");
        checkIfFound();
    }

    public final void visitLeave(final Author author) {
        leave();
    }

    public final void visitEnter(final ImportList importList) throws ModuleDataException {
        enter("IMPORTS");
        final String method = "visitEnter(ImportList)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final ImportList importList) {
        leave();
    }

    public final void visitEnter(final Import imp) throws ModuleDataException {
        enter("IMPORT");
        final String method = "visitEnter(Import)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getLabel()");
        current.setAttribute("label");
        checkIfFound();
    }

    public final void visitLeave(final Import imp) {
        leave();
    }

    public final void visitEnter(final UsedByList usedByList) throws ModuleDataException {
        enter("USEDBY");
        final String method = "visitEnter(UsedByList)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final UsedByList usedByList) {
        leave();
    }

    public final void visitEnter(final ChapterList chapterList) throws ModuleDataException {
        final String method = "visitEnter(ChapterList)";
        // because no equivalent level of "getChapterList()" exists in the XSD we simply
        // point to the current location that must be "QEDEQ"
        checkMatching(method);
    }

    public final void visitLeave(final ChapterList chapterList) {
        traverser.setBlocked(false);  // free sub node search
    }

    public final void visitEnter(final Chapter chapter) throws ModuleDataException {
        enter("CHAPTER");
        final String method = "visitEnter(Chapter)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getNoNumber()");
        current.setAttribute("noNumber");
        checkIfFound();
    }

    public final void visitLeave(final Chapter chapter) {
        leave();
    }

    public final void visitEnter(final SectionList sectionList) throws ModuleDataException {
        final String method = "visitEnter(SectionList)";
        // because no equivalent level of "getSectionList()" exists in the XSD we simply
        // point to the current location that must be "QEDEQ/CHAPTER[x]"
        checkMatching(method);
    }

    public final void visitLeave(final SectionList sectionList) {
        traverser.setBlocked(false);  // free sub node search
    }

    public final void visitEnter(final Section section) throws ModuleDataException {
        enter("SECTION");
        final String method = "visitEnter(Section)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getNoNumber()");
        current.setAttribute("noNumber");
        checkIfFound();
    }

    public final void visitLeave(final Section section) {
        leave();
    }

    public final void visitEnter(final SubsectionList subsectionList) throws ModuleDataException {
        enter("SUBSECTIONS");
        final String method = "visitEnter(SubsectionList)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final SubsectionList subsectionList) {
        leave();
    }

    public final void visitEnter(final Subsection subsection) throws ModuleDataException {
        enter("SUBSECTION");
        final String method = "visitEnter(Subsection)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getId()");
        current.setAttribute("id");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getLevel()");
        current.setAttribute("level");
        checkIfFound();
    }

    public final void visitLeave(final Subsection subsection) {
        leave();
    }

    public final void visitEnter(final Node node) throws ModuleDataException {
        enter("NODE");
        final String method = "visitEnter(Node)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getId()");
        current.setAttribute("id");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getLevel()");
        current.setAttribute("level");
        checkIfFound();

        // we dont't differentiate the different node types here and point to the parent element
        traverser.setLocationWithinModule(context + ".getNodeType()");
        current.setAttribute(null);
        checkIfFound();

    }

    public final void visitLeave(final Node node) {
        leave();
    }

    public final void visitEnter(final Axiom axiom) throws ModuleDataException {
        enter("AXIOM");
        final String method = "visitEnter(Axiom)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final Axiom axiom) {
        leave();
    }

    public final void visitEnter(final Proposition proposition) throws ModuleDataException {
        enter("THEOREM");
        final String method = "visitEnter(Proposition)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final Proposition proposition) {
        leave();
    }

    public final void visitEnter(final ProofList proofList) throws ModuleDataException {
        final String method = "visitEnter(ProofList)";
        // because no equivalent level of "getProofList()" exists in the XSD we simply
        // point to the current location that must be within the element "THEOREM"
        checkMatching(method);
    }

    public final void visitEnter(final Proof proof) throws ModuleDataException {
        enter("PROOF");
        final String method = "visitEnter(Proof)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getKind()");
        current.setAttribute("kind");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getLevel()");
        current.setAttribute("level");
        checkIfFound();
    }

    public final void visitLeave(final Proof proof) {
        leave();
    }

    public final void visitEnter(final PredicateDefinition definition) throws ModuleDataException {
        enter("DEFINITION_PREDICATE");
        final String method = "visitEnter(PredicateDefinition)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getArgumentNumber()");
        current.setAttribute("arguments");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getName()");
        current.setAttribute("name");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getLatexPattern()");
        enter("LATEXPATTERN");
        if (find.getLocationWithinModule().equals(traverser.getCurrentContext()
                .getLocationWithinModule())) {
            if (definition.getLatexPattern() == null) { // NOT FOUND
                leave();
            }
            throw new LocationFoundException(traverser.getCurrentContext());
        }
        leave();
    }

    public final void visitLeave(final PredicateDefinition definition) {
        leave();
    }

    public final void visitEnter(final FunctionDefinition definition) throws ModuleDataException {
        enter("DEFINITION_FUNCTION");
        final String method = "visitEnter(FunctionDefinition)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getArgumentNumber()");
        current.setAttribute("arguments");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getName()");
        current.setAttribute("name");
        checkIfFound();

        traverser.setLocationWithinModule(context + ".getLatexPattern()");
        enter("LATEXPATTERN");
        if (find.getLocationWithinModule().equals(traverser.getCurrentContext()
                .getLocationWithinModule())) {
            if (definition.getLatexPattern() == null) { // NOT FOUND
                leave();
            }
            throw new LocationFoundException(traverser.getCurrentContext());
        }
        leave();
    }

    public final void visitLeave(final FunctionDefinition definition) {
        leave();
    }

    public final void visitEnter(final Rule rule) throws ModuleDataException {
        enter("RULE");
        final String method = "visitEnter(Rule)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getName()");
        current.setAttribute("name");
        checkIfFound();
    }

    public final void visitLeave(final Rule rule) {
        leave();
    }

    public final void visitEnter(final LinkList linkList) throws ModuleDataException {
        final String method = "visitEnter(LinkList)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        for (int i = 0; i < linkList.size(); i++) {
            enter("LINK");
            if (linkList.get(i) != null) {
                traverser.setLocationWithinModule(context + ".get(" + i + ")");
                current.setAttribute("id");
                checkIfFound();
            }
            leave();
        };
    }

    public final void visitLeave(final LinkList linkList) {
    }

    public final void visitEnter(final Formula formula) throws ModuleDataException {
        enter("FORMULA");
        final String method = "visitEnter(Formula)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final Formula formula) {
        leave();
    }

    public final void visitEnter(final Term term) throws ModuleDataException {
        enter("TERM");
        final String method = "visitEnter(Term)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final Term term) {
        leave();
    }

    public final void visitEnter(final VariableList variableList) throws ModuleDataException {
        enter("VARLIST");
        final String method = "visitEnter(VariableList)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final VariableList variableList) {
        leave();
    }

    public final void visitEnter(final ElementList list) throws ModuleDataException {
        final String operator = list.getOperator();
        enter(operator);
        final String method = "visitEnter(ElementList)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();

        // to find something like getElement(0).getList().getElement(0)
        if (context.startsWith(find.getLocationWithinModule())) {
            throw new LocationFoundException(find);
        }

        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getOperator()");
        checkIfFound();
        traverser.setLocationWithinModule(context);
        final boolean firstIsAtom = list.size() > 0 && list.getElement(0).isAtom();
        if (firstIsAtom) {
            traverser.setLocationWithinModule(context + ".getElement(0).getAtom()");
            if ("VAR".equals(operator) || "PREDVAR".equals(operator)
                    || "FUNVAR".equals(operator)) {
                current.setAttribute("id");
                checkIfFound();
            } else if ("PREDCON".equals(operator) || "FUNCON".equals(operator)) {
                current.setAttribute("ref");
                checkIfFound();
            } else {    // should not occur, but just in case
                current.setAttribute(null);
                Trace.info(CLASS, this, method, "unknown operator " + operator);
                throw new LocationFoundException(traverser.getCurrentContext());
            }
        }
    }

/* TODO remove method
    public final void visitEnter(final Atom atom) throws ModuleDataException {
        final String method = "visitEnter(Atom)";
        Trace.param(this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        // mime 20070217: should never occur
        checkMatching(method);
    }
*/
    public final void visitLeave(final ElementList list) {
        leave();
    }

    public final void visitEnter(final LiteratureItemList list) throws ModuleDataException {
        enter("BIBLIOGRAPHY");
        final String method = "visitEnter(LiteratureItemList)";
        Trace.param(CLASS, this, method, "current", current);
        checkMatching(method);
    }

    public final void visitLeave(final LiteratureItemList list) {
        leave();
    }

    public final void visitEnter(final LiteratureItem item) throws ModuleDataException {
        enter("ITEM");
        final String method = "visitEnter(LiteratureItem)";
        Trace.param(CLASS, this, method, "current", current);
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        checkMatching(method);

        traverser.setLocationWithinModule(context + ".getLabel()");
        current.setAttribute("label");
        checkIfFound();
    }

    public final void visitLeave(final LiteratureItem item) {
        leave();
    }

    /**
     * Check if searched for context is already reached.
     *
     * @throws  LocationFoundException  We have found it.
     */
    private final void checkIfFound() throws LocationFoundException {
        if (find.getLocationWithinModule().equals(traverser.getCurrentContext()
                .getLocationWithinModule())) {
            throw new LocationFoundException(traverser.getCurrentContext());
        }
    }

    /**
     * Checks if the current context matches the beginning of the context we want to find.
     * This method must be called at the beginning of the <code>visitEnter</code> method when
     * {@link #current} is correctly set. If the context of a previously visited node was already
     * matching and the current node doesn't start with the old matching context any longer we
     * throw a {@link LocationNotFoundException}.
     *
     * @param   method  Method we were called from.
     * @throws  LocationNotFoundException
     * @throws  LocationFoundException
     */
    private final void checkMatching(final String method)
            throws LocationNotFoundException, LocationFoundException {
        final String context = traverser.getCurrentContext().getLocationWithinModule();
        if (find.getLocationWithinModule().startsWith(context)) {
            Trace.info(CLASS, this, method, "beginning matches");
            Trace.paramInfo(CLASS, this, method, "context", context);
            matching = true;
            matchingBegin = context;                    // remember matching context
            matchingPath = new SimpleXPath(current);    // remember last matching XPath
        } else {
            if (matching) {
                // for example we are looking for "getHeader().getImportList().getImport(2)"
                // getHeader()                    matches, we remember "getHeader()"
                // getHeader().getSpecification() doesn't match, but still starts with "getHeader()"
                // getHeader().getImportList()    matches, we remember "getHeader.getImportList()"
                // getHeader().getImportList().get(0)  doesn't match but still starts with
                //                                     "getHeader.getImportList()"
                if (!context.startsWith(matchingBegin)) {
                    // Matching lost, that means we will never found the location
                    // so what can we do? We just return the last matching location and hope
                    // it is close enough to the searched one. But at least we do some
                    // logging here:
                    Trace.info(CLASS, this, method, "matching lost");
                    Trace.paramInfo(CLASS, this, method, "last match     ", matchingBegin);
                    Trace.paramInfo(CLASS, this, method, "current context", context);
                    Trace.paramInfo(CLASS, this, method,
                        "find context   ", find.getLocationWithinModule());

                    // throw new LocationNotFoundException(find);  // when we really want to fail

                    Trace.traceStack(CLASS, this, method);
                    Trace.info(CLASS, this, method, "changing XPath to last matching one");
                    // now we change the current XPath to the last matching one because the
                    // contents of "current" is used as the resulting XPath later on when
                    // catching the exception in {@link #find()}
                    current = matchingPath;
                    throw new LocationFoundException(new ModuleContext(find.getModuleLocation(),
                        matchingBegin));
                }
            }
            traverser.setBlocked(true);   // block further search in sub nodes
        }
        checkIfFound();
    }

    /**
     * Enter new XML element.
     *
     * @param   element     Element name.
     */
    private final void enter(final String element) {
        level++;
        current.addElement(element, addOccurence(element));
    }

    /**
     * Leave last XML element.
     */
    private final void leave() {
        level--;
        current.deleteLastElement();
        traverser.setBlocked(false);  //  enable further search in sub notes
    }

    /**
     * Add element occurrence. For example we have <code>getHeader().getImportList().get(2)</code>
     * and we want to get <code>QEDEQ/HEADER/IMPORTS/IMPORT[3]</code>.
     * So we call <code>enter("QEDEQ")</code>, <code>enter("HEADER")</code>,
     * <code>enter("IMPORTS")</code> and last but not least
     * three times the sequence <code>enter("IMPORT")</code>, <code>leave("IMPORT")</code>,
     *
     * @param   name    Element that occurred.
     * @return  Number of occurrences including this one.
     */
    private final int addOccurence(final String name) {
        while (level < elements.size()) {
            elements.remove(elements.size() - 1);
        }
        while (level > elements.size()) {
            elements.add(new HashMap());
        }
        final Map levelMap = (Map) elements.get(level - 1);
        final Enumerator counter;
        if (levelMap.containsKey(name)) {
            counter = (Enumerator) levelMap.get(name);
            counter.increaseNumber();
        } else {
            counter = new Enumerator(1);
            levelMap.put(name, counter);
        }
        return counter.getNumber();
    }

}
