/* $Id: Qedeq2Xml.java,v 1.6 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.latex;

import java.io.IOException;

import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.base.module.Author;
import org.qedeq.kernel.base.module.AuthorList;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Chapter;
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
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Section;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.base.module.Subsection;
import org.qedeq.kernel.base.module.SubsectionList;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.base.module.UsedByList;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.bo.control.ControlVisitor;
import org.qedeq.kernel.bo.control.DefaultQedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.utility.TextOutput;


/**
 * This class prints a QEDEQ module in XML format in an output stream.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public final class Qedeq2Xml extends ControlVisitor {

    /** Output goes here. */
    private TextOutput printer;

    /**
     * Constructor.
     *
     * @param   bo                  QEDEQ BO.
     * @param   printer             Print herein.
     */
    private Qedeq2Xml(final DefaultQedeqBo bo, final TextOutput printer) {
        super(bo);
        this.printer = printer;
    }

    /**
     * Prints a XML representation of given QEDEQ module into a given output stream.
     *
     * @param   bo                  BO QEDEQ module object.
     * @param   printer             Print herein.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException
     */
    public static void print(final DefaultQedeqBo bo, final TextOutput printer) throws
            SourceFileExceptionList, IOException {
        final Qedeq2Xml converter = new Qedeq2Xml(bo, printer);
        try {
            converter.traverse();
        } finally {
            printer.flush();
        }
        if (printer.checkError()) {
            throw printer.getError();
        }
    }

    public final void visitEnter(final Qedeq qedeq) {
        printer.levelPrintln("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        printer.levelPrintln("<QEDEQ");
        printer.levelPrintln("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        printer.levelPrintln("    xsi:noNamespaceSchemaLocation=\"http://www.qedeq.org/"
            + KernelContext.getInstance().getKernelVersionDirectory() + "/xml/qedeq.xsd\">");
        printer.pushLevel();
    }

    public final void visitLeave(final Qedeq qedeq) {
        printer.popLevel();
        printer.levelPrintln("</QEDEQ>");
    }

    public void visitEnter(final Header header) {
        printer.levelPrint("<HEADER");
        if (header.getEmail() != null) {
            printer.print(" email=\"" + header.getEmail() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Header header) {
        printer.popLevel();
        printer.levelPrintln("</HEADER>");
    }

    public void visitEnter(final Specification specification) {
        printer.levelPrint("<SPECIFICATION");
        if (specification.getName() != null) {
            printer.print(" name=\"" + specification.getName() + "\"");
        }
        if (specification.getName() != null) {
            printer.print(" ruleVersion=\"" + specification.getRuleVersion() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Specification specification) {
        printer.popLevel();
        printer.levelPrintln("</SPECIFICATION>");
    }

    public void visitEnter(final LatexList latexList) {
        final String last = getCurrentContext().getLocationWithinModule();
        if (last.endsWith(".getTitle()")) {
            printer.levelPrintln("<TITLE>");
        } else if (last.endsWith(".getSummary()")) {
            printer.levelPrintln("<ABSTRACT>");
        } else if (last.endsWith(".getIntroduction()")) {
            printer.levelPrintln("<INTRODUCTION>");
        } else if (last.endsWith(".getName()")) {
            printer.levelPrintln("<NAME>");
        } else if (last.endsWith(".getPrecedingText()")) {
            printer.levelPrintln("<PRECEDING>");
        } else if (last.endsWith(".getSucceedingText()")) {
            printer.levelPrintln("<SUCCEEDING>");
        } else if (last.endsWith(".getLatex()")) {
            printer.levelPrintln("<TEXT>");
        } else if (last.endsWith(".getDescription()")) {
            printer.levelPrintln("<DESCRIPTION>");
        }
        printer.pushLevel();
    }

    public void visitLeave(final LatexList latexList) {
        printer.popLevel();
        final String last = getCurrentContext().getLocationWithinModule();
        if (last.endsWith(".getTitle()")) {
            printer.levelPrintln("</TITLE>");
        } else if (last.endsWith(".getSummary()")) {
            printer.levelPrintln("</ABSTRACT>");
        } else if (last.endsWith(".getIntroduction()")) {
            printer.levelPrintln("</INTRODUCTION>");
        } else if (last.endsWith(".getName()")) {
            printer.levelPrintln("</NAME>");
        } else if (last.endsWith(".getPrecedingText()")) {
            printer.levelPrintln("</PRECEDING>");
        } else if (last.endsWith(".getSucceedingText()")) {
            printer.levelPrintln("</SUCCEEDING>");
        } else if (last.endsWith(".getLatex()")) {
            printer.levelPrintln("</TEXT>");
        } else if (last.endsWith(".getDescription()")) {
            printer.levelPrintln("</DESCRIPTION>");
        }
    }

    public void visitEnter(final Latex latex) {
        printer.levelPrint("<LATEX");
        if (latex.getLanguage() != null) {
            printer.print(" language=\"" + latex.getLanguage() + "\"");
        }
        printer.println(">");
        if (latex.getLatex() != null) {
            printer.pushLevel();
            printer.levelPrintln("<![CDATA[");
            printer.pushLevel();
            printer.levelPrintln(latex.getLatex());
        }
    }

    public void visitLeave(final Latex latex) {
        if (latex.getLatex() != null) {
            printer.popLevel();
            printer.levelPrintln("]]>");
            printer.popLevel();
        }
        printer.levelPrintln("</LATEX>");
    }

    public void visitEnter(final LocationList locationList) {
        printer.levelPrintln("<LOCATIONS>");
        printer.pushLevel();
    }

    public void visitLeave(final LocationList locationList) {
        printer.popLevel();
        printer.levelPrintln("</LOCATIONS>");
    }

    public void visitEnter(final Location location) {
        printer.levelPrint("<LOCATION");
        if (location.getLocation() != null) {
            printer.print(" value=\"" + location.getLocation() + "\"");
        }
        printer.println(" />");
    }

    public void visitEnter(final AuthorList authorList) {
        printer.levelPrintln("<AUTHORS>");
        printer.pushLevel();
    }

    public void visitLeave(final AuthorList authorList) {
        printer.popLevel();
        printer.levelPrintln("</AUTHORS>");
    }

    public void visitEnter(final Author author) {
        printer.levelPrint("<AUTHOR");
        if (author.getEmail() != null) {
            printer.print(" email=\"" + author.getEmail() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (author.getName() != null) {
            printer.levelPrintln("<NAME>");
        }
        printer.pushLevel();
    }

    public void visitLeave(final Author author) {
        printer.popLevel();
        if (author.getName() != null) {
            printer.levelPrintln("</NAME>");
        }
        printer.popLevel();
        printer.levelPrintln("</AUTHOR>");
    }

    public void visitEnter(final ImportList importList) {
        printer.levelPrintln("<IMPORTS>");
        printer.pushLevel();
    }

    public void visitLeave(final ImportList importList) {
        printer.popLevel();
        printer.levelPrintln("</IMPORTS>");
    }

    public void visitEnter(final Import imp) {
        printer.levelPrint("<IMPORT");
        if (imp.getLabel() != null) {
            printer.print(" label=\"" + imp.getLabel() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Import imp) {
        printer.popLevel();
        printer.levelPrintln("</IMPORT>");
    }

    public void visitEnter(final UsedByList usedByList) {
        printer.levelPrintln("<USEDBY>");
        printer.pushLevel();
    }

    public void visitLeave(final UsedByList usedByList) {
        printer.popLevel();
        printer.levelPrintln("</USEDBY>");
    }

    public void visitEnter(final Chapter chapter) {
        printer.levelPrint("<CHAPTER");
        if (chapter.getNoNumber() != null) {
            printer.print(" noNumber=\"" + chapter.getNoNumber().booleanValue() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Chapter chapter) {
        printer.popLevel();
        printer.levelPrintln("</CHAPTER>");
    }

    public void visitEnter(final Section section) {
        printer.levelPrint("<SECTION");
        if (section.getNoNumber() != null) {
            printer.print(" noNumber=\"" + section.getNoNumber().booleanValue() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Section section) {
        printer.popLevel();
        printer.levelPrintln("</SECTION>");
    }

    public void visitEnter(final SubsectionList subsectionList) {
        printer.levelPrintln("<SUBSECTIONS>");
        printer.pushLevel();
    }

    public void visitLeave(final SubsectionList subsectionList) {
        printer.popLevel();
        printer.levelPrintln("</SUBSECTIONS>");
    }

    public void visitEnter(final Subsection subsection) {
        printer.levelPrint("<SUBSECTION");
        if (subsection.getId() != null) {
            printer.print(" id=\"" + subsection.getId() + "\"");
        }
        if (subsection.getLevel() != null) {
            printer.print(" level=\"" + subsection.getLevel() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Subsection subsection) {
        printer.popLevel();
        printer.levelPrintln("</SUBSECTION>");
    }

    public void visitEnter(final Node node) {
        printer.levelPrint("<NODE");
        if (node.getId() != null) {
            printer.print(" id=\"" + node.getId() + "\"");
        }
        if (node.getLevel() != null) {
            printer.print(" level=\"" + node.getLevel() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Node node) {
        printer.popLevel();
        printer.levelPrintln("</NODE>");
    }

    public void visitEnter(final Axiom axiom) {
        printer.levelPrintln("<AXIOM>");
        printer.pushLevel();
    }

    public void visitLeave(final Axiom axiom) {
        printer.popLevel();
        printer.levelPrintln("</AXIOM>");
    }

    public void visitEnter(final Proposition proposition) {
        printer.levelPrintln("<THEOREM>");
        printer.pushLevel();
    }

    public void visitLeave(final Proposition proposition) {
        printer.popLevel();
        printer.levelPrintln("</THEOREM>");
    }

    public void visitEnter(final Proof proof) {
        printer.levelPrint("<PROOF");
        if (proof.getKind() != null) {
            printer.print(" kind=\"" + proof.getKind() + "\"");
        }
        if (proof.getLevel() != null) {
            printer.print(" level=\"" + proof.getLevel() + "\"");
        }
        printer.println(">");
    }

    public void visitLeave(final Proof proof) {
        printer.levelPrintln("</PROOF>");
    }

    public void visitEnter(final PredicateDefinition definition) {
        printer.levelPrint("<DEFINITION_PREDICATE");
        if (definition.getArgumentNumber() != null) {
            printer.print(" arguments=\"" + definition.getArgumentNumber() + "\"");
        }
        if (definition.getName() != null) {
            printer.print(" name=\"" + definition.getName() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (definition.getLatexPattern() != null) {
            printer.levelPrintln("<LATEXPATTERN>" + definition.getLatexPattern()
                + "</LATEXPATTERN>");
        }
    }

    public void visitLeave(final PredicateDefinition definition) {
        printer.popLevel();
        printer.levelPrintln("</DEFINITION_PREDICATE>");
    }

    public void visitEnter(final FunctionDefinition definition) {
        printer.levelPrint("<DEFINITION_FUNCTION");
        if (definition.getArgumentNumber() != null) {
            printer.print(" arguments=\"" + definition.getArgumentNumber() + "\"");
        }
        if (definition.getName() != null) {
            printer.print(" name=\"" + definition.getName() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (definition.getLatexPattern() != null) {
            printer.levelPrintln("<LATEXPATTERN>" + definition.getLatexPattern()
                + "</LATEXPATTERN>");
        }
    }

    public void visitLeave(final FunctionDefinition definition) {
        printer.popLevel();
        printer.levelPrintln("</DEFINITION_FUNCTION>");
    }

    public void visitEnter(final Rule rule) {
        printer.levelPrint("<RULE");
        if (rule.getName() != null) {
            printer.print(" name=\"" + rule.getName() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Rule rule) {
        printer.popLevel();
        printer.levelPrintln("</RULE>");
    }

    public void visitEnter(final LinkList linkList) {
        for (int i = 0; i < linkList.size(); i++) {
            printer.levelPrint("<LINK");
            if (linkList.get(i) != null) {
                printer.print(" id=\"" + linkList.get(i) + "\"");
            }
            printer.println("/>");
        };
    }

    public void visitEnter(final Formula formula) {
        printer.levelPrintln("<FORMULA>");
        printer.pushLevel();
    }

    public void visitLeave(final Formula formula) {
        printer.popLevel();
        printer.levelPrintln("</FORMULA>");
    }

    public void visitEnter(final Term term) {
        printer.levelPrintln("<TERM>");
        printer.pushLevel();
    }

    public void visitLeave(final Term term) {
        printer.popLevel();
        printer.levelPrintln("</TERM>");
    }

    public void visitEnter(final VariableList variableList) {
        printer.levelPrintln("<VARLIST>");
        printer.pushLevel();
    }

    public void visitLeave(final VariableList variableList) {
        printer.popLevel();
        printer.levelPrintln("</VARLIST>");
    }

    // TODO mime 20070217: what do we do if an atom is not first element of a list?
    // we wouldn't get it here!!! But can we think of an output syntax anyway????
    public void visitEnter(final ElementList list) {
        final String operator = list.getOperator();
        printer.levelPrint("<" + operator);
        final boolean firstIsAtom = list.size() > 0 && list.getElement(0).isAtom();
        if (firstIsAtom) {
            final String atom = list.getElement(0).getAtom().getString();
            if (atom != null) {
                if ("VAR".equals(operator) || "PREDVAR".equals(operator)
                        || "FUNVAR".equals(operator)) {
                    printer.print(" id=\"" + atom + "\"");
                } else if ("PREDCON".equals(operator) || "FUNCON".equals(operator)) {
                    printer.print(" ref=\"" + atom + "\"");
                } else {
                    printer.print(" unknown=\"" + atom + "\"");
                }
            }
        }
        if (list.size() == 0 || list.size() == 1 && list.getElement(0).isAtom()) {
            printer.print("/");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final ElementList list) {
        printer.popLevel();
        if (list.size() == 0 || list.size() == 1 && list.getElement(0).isAtom()) {
            return;
        }
        printer.levelPrintln("</" + list.getOperator() + ">");
    }

    public void visitEnter(final LiteratureItemList list) {
        printer.levelPrintln("<BIBLIOGRAPHY>");
        printer.pushLevel();
    }

    public void visitLeave(final LiteratureItemList list) {
        printer.popLevel();
        printer.levelPrintln("</BIBLIOGRAPHY>");
    }

    public void visitEnter(final LiteratureItem item) {
        printer.levelPrint("<ITEM");
        if (item.getLabel() != null) {
            printer.print(" label=\"" + item.getLabel() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final LiteratureItem item) {
        printer.popLevel();
        printer.levelPrintln("</ITEM>");
    }

}
