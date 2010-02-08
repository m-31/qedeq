/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.test.DynamicGetter;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.trace.Trace;
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
import org.qedeq.kernel.bo.control.QedeqBoFactoryTest;
import org.qedeq.kernel.bo.service.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.visitor.QedeqVisitor;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;
import org.xml.sax.SAXException;



/**
 * Basic visitor that makes nothing.
 *
 * @version $Revision: 1.1 $
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
        moduleFile = QedeqBoFactoryTest.getQedeqFile("math/qedeq_set_theory_v1.xml");
        final ModuleAddress globalContext = new DefaultModuleAddress(moduleFile);
        qedeq = QedeqBoFactoryTest.loadQedeq(moduleFile);
        traverser = new QedeqNotNullTraverser(globalContext, this);
        traverser.accept(qedeq);
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

    public void visitEnter(final Formula formula) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final FunctionDefinition functionDefinition) throws ModuleDataException {
        checkContext();
    }

    public void visitEnter(final Header header) throws ModuleDataException {
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
        try {
            final SimpleXPath find = XPathLocationParser.getXPathLocation(moduleFile,
                xpath.toString(), IoUtility.toUrlString(moduleFile));
            if (find.getStartLocation() == null) {
                System.out.println(traverser.getCurrentContext());
                throw new RuntimeException("start not found: " + find + "\ncontext: "
                    + context);
            }
            if (find.getEndLocation() == null) {
                System.out.println(traverser.getCurrentContext());
                throw new RuntimeException("end not found: " + find + "\ncontext: "
                    + context);
            }

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
