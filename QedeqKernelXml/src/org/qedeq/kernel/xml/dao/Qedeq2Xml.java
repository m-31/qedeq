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

package org.qedeq.kernel.xml.dao;

import java.io.IOException;

import org.qedeq.base.io.TextOutput;
import org.qedeq.base.utility.StringUtility;
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
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * This class prints a QEDEQ module in XML format in an output stream.
 *
 * TODO mime 20080309: escape XML attributes like &gt;, &amp; and other.
 * See {@link org.qedeq.base.utility.StringUtility#decodeXmlMarkup(StringBuffer)}.
 *
 * @author  Michael Meyling
 */
public final class Qedeq2Xml extends ControlVisitor implements Plugin {

    /** Output goes here. */
    private TextOutput printer;

    /**
     * Constructor.
     *
     * @param   plugin  This plugin we work for.
     * @param   bo      QEDEQ BO.
     * @param   printer Print herein.
     */
    private Qedeq2Xml(final Plugin plugin, final KernelQedeqBo bo, final TextOutput printer) {
        super(plugin, bo);
        this.printer = printer;
    }

    /**
     * Prints a XML representation of given QEDEQ module into a given output stream.
     *
     * @param   plugin              Plugin we work for.
     * @param   bo                  BO QEDEQ module object.
     * @param   printer             Print herein.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException         Writing failed.
     */
    public static void print(final Plugin plugin, final KernelQedeqBo bo, final TextOutput printer) throws
            SourceFileExceptionList, IOException {
        final Qedeq2Xml converter = new Qedeq2Xml(plugin, bo, printer);
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
        printer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        printer.println("<QEDEQ");
        printer.println("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        printer.println("    xsi:noNamespaceSchemaLocation=\"http://www.qedeq.org/"
            + KernelContext.getInstance().getKernelVersionDirectory() + "/xml/qedeq.xsd\">");
        printer.pushLevel();
    }

    public final void visitLeave(final Qedeq qedeq) {
        printer.popLevel();
        printer.println("</QEDEQ>");
    }

    public void visitEnter(final Header header) {
        printer.print("<HEADER");
        if (header.getEmail() != null) {
            printer.print(" email=\"" + header.getEmail() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Header header) {
        printer.popLevel();
        printer.println("</HEADER>");
    }

    public void visitEnter(final Specification specification) {
        printer.print("<SPECIFICATION");
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
        printer.println("</SPECIFICATION>");
    }

    public void visitEnter(final LatexList latexList) {
        final String last = getCurrentContext().getLocationWithinModule();
        if (last.endsWith(".getTitle()")) {
            printer.println("<TITLE>");
        } else if (last.endsWith(".getSummary()")) {
            printer.println("<ABSTRACT>");
        } else if (last.endsWith(".getIntroduction()")) {
            printer.println("<INTRODUCTION>");
        } else if (last.endsWith(".getName()")) {
            printer.println("<NAME>");
        } else if (last.endsWith(".getPrecedingText()")) {
            printer.println("<PRECEDING>");
        } else if (last.endsWith(".getSucceedingText()")) {
            printer.println("<SUCCEEDING>");
        } else if (last.endsWith(".getLatex()")) {
            printer.println("<TEXT>");
        } else if (last.endsWith(".getDescription()")) {
            printer.println("<DESCRIPTION>");
        }
        printer.pushLevel();
    }

    public void visitLeave(final LatexList latexList) {
        printer.popLevel();
        final String last = getCurrentContext().getLocationWithinModule();
        if (last.endsWith(".getTitle()")) {
            printer.println("</TITLE>");
        } else if (last.endsWith(".getSummary()")) {
            printer.println("</ABSTRACT>");
        } else if (last.endsWith(".getIntroduction()")) {
            printer.println("</INTRODUCTION>");
        } else if (last.endsWith(".getName()")) {
            printer.println("</NAME>");
        } else if (last.endsWith(".getPrecedingText()")) {
            printer.println("</PRECEDING>");
        } else if (last.endsWith(".getSucceedingText()")) {
            printer.println("</SUCCEEDING>");
        } else if (last.endsWith(".getLatex()")) {
            printer.println("</TEXT>");
        } else if (last.endsWith(".getDescription()")) {
            printer.println("</DESCRIPTION>");
        }
    }

    public void visitEnter(final Latex latex) {
        printer.print("<LATEX");
        if (latex.getLanguage() != null) {
            printer.print(" language=\"" + latex.getLanguage() + "\"");
        }
        printer.println(">");
        if (latex.getLatex() != null) {
            printer.pushLevel();
            printer.println("<![CDATA[");
            printer.print("  ");
            final String tabs = printer.getLevel();
            printer.clearLevel();
            printer.println(StringUtility.useSystemLineSeparator(latex.getLatex()).trim());
            printer.pushLevel(tabs);
        }
    }

    public void visitLeave(final Latex latex) {
        if (latex.getLatex() != null) {
            printer.println("]]>");
            printer.popLevel();
        }
        printer.println("</LATEX>");
    }

    public void visitEnter(final LocationList locationList) {
        printer.println("<LOCATIONS>");
        printer.pushLevel();
    }

    public void visitLeave(final LocationList locationList) {
        printer.popLevel();
        printer.println("</LOCATIONS>");
    }

    public void visitEnter(final Location location) {
        printer.print("<LOCATION");
        if (location.getLocation() != null) {
            printer.print(" value=\"" + location.getLocation() + "\"");
        }
        printer.println(" />");
    }

    public void visitEnter(final AuthorList authorList) {
        printer.println("<AUTHORS>");
        printer.pushLevel();
    }

    public void visitLeave(final AuthorList authorList) {
        printer.popLevel();
        printer.println("</AUTHORS>");
    }

    public void visitEnter(final Author author) {
        printer.print("<AUTHOR");
        if (author.getEmail() != null) {
            printer.print(" email=\"" + author.getEmail() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (author.getName() != null) {
            printer.println("<NAME>");
        }
        printer.pushLevel();
    }

    public void visitLeave(final Author author) {
        printer.popLevel();
        if (author.getName() != null) {
            printer.println("</NAME>");
        }
        printer.popLevel();
        printer.println("</AUTHOR>");
    }

    public void visitEnter(final ImportList importList) {
        printer.println("<IMPORTS>");
        printer.pushLevel();
    }

    public void visitLeave(final ImportList importList) {
        printer.popLevel();
        printer.println("</IMPORTS>");
    }

    public void visitEnter(final Import imp) {
        printer.print("<IMPORT");
        if (imp.getLabel() != null) {
            printer.print(" label=\"" + imp.getLabel() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Import imp) {
        printer.popLevel();
        printer.println("</IMPORT>");
    }

    public void visitEnter(final UsedByList usedByList) {
        printer.println("<USEDBY>");
        printer.pushLevel();
    }

    public void visitLeave(final UsedByList usedByList) {
        printer.popLevel();
        printer.println("</USEDBY>");
    }

    public void visitEnter(final Chapter chapter) {
        printer.print("<CHAPTER");
        if (chapter.getNoNumber() != null) {
            printer.print(" noNumber=\"" + chapter.getNoNumber().booleanValue() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Chapter chapter) {
        printer.popLevel();
        printer.println("</CHAPTER>");
    }

    public void visitEnter(final Section section) {
        printer.print("<SECTION");
        if (section.getNoNumber() != null) {
            printer.print(" noNumber=\"" + section.getNoNumber().booleanValue() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Section section) {
        printer.popLevel();
        printer.println("</SECTION>");
    }

    public void visitEnter(final SubsectionList subsectionList) {
        printer.println("<SUBSECTIONS>");
        printer.pushLevel();
    }

    public void visitLeave(final SubsectionList subsectionList) {
        printer.popLevel();
        printer.println("</SUBSECTIONS>");
    }

    public void visitEnter(final Subsection subsection) {
        printer.print("<SUBSECTION");
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
        printer.println("</SUBSECTION>");
    }

    public void visitEnter(final Node node) {
        printer.print("<NODE");
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
        printer.println("</NODE>");
    }

    public void visitEnter(final Axiom axiom) {
        printer.println("<AXIOM>");
        printer.pushLevel();
    }

    public void visitLeave(final Axiom axiom) {
        printer.popLevel();
        printer.println("</AXIOM>");
    }

    public void visitEnter(final Proposition proposition) {
        printer.println("<THEOREM>");
        printer.pushLevel();
    }

    public void visitLeave(final Proposition proposition) {
        printer.popLevel();
        printer.println("</THEOREM>");
    }

    public void visitEnter(final Proof proof) {
        printer.print("<PROOF");
        if (proof.getKind() != null) {
            printer.print(" kind=\"" + proof.getKind() + "\"");
        }
        if (proof.getLevel() != null) {
            printer.print(" level=\"" + proof.getLevel() + "\"");
        }
        printer.println(">");
    }

    public void visitLeave(final Proof proof) {
        printer.println("</PROOF>");
    }

    public void visitEnter(final PredicateDefinition definition) {
        printer.print("<DEFINITION_PREDICATE");
        if (definition.getArgumentNumber() != null) {
            printer.print(" arguments=\"" + definition.getArgumentNumber() + "\"");
        }
        if (definition.getName() != null) {
            printer.print(" name=\"" + definition.getName() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (definition.getLatexPattern() != null) {
            printer.println("<LATEXPATTERN>" + definition.getLatexPattern()
                + "</LATEXPATTERN>");
        }
    }

    public void visitLeave(final PredicateDefinition definition) {
        printer.popLevel();
        printer.println("</DEFINITION_PREDICATE>");
    }

    public void visitEnter(final FunctionDefinition definition) {
        printer.print("<DEFINITION_FUNCTION");
        if (definition.getArgumentNumber() != null) {
            printer.print(" arguments=\"" + definition.getArgumentNumber() + "\"");
        }
        if (definition.getName() != null) {
            printer.print(" name=\"" + definition.getName() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (definition.getLatexPattern() != null) {
            printer.println("<LATEXPATTERN>" + definition.getLatexPattern()
                + "</LATEXPATTERN>");
        }
    }

    public void visitLeave(final FunctionDefinition definition) {
        printer.popLevel();
        printer.println("</DEFINITION_FUNCTION>");
    }

    public void visitEnter(final Rule rule) {
        printer.print("<RULE");
        if (rule.getName() != null) {
            printer.print(" name=\"" + rule.getName() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Rule rule) {
        printer.popLevel();
        printer.println("</RULE>");
    }

    public void visitEnter(final LinkList linkList) {
        for (int i = 0; i < linkList.size(); i++) {
            printer.print("<LINK");
            if (linkList.get(i) != null) {
                printer.print(" id=\"" + linkList.get(i) + "\"");
            }
            printer.println("/>");
        };
    }

    public void visitEnter(final Formula formula) {
        printer.println("<FORMULA>");
        printer.pushLevel();
    }

    public void visitLeave(final Formula formula) {
        printer.popLevel();
        printer.println("</FORMULA>");
    }

    public void visitEnter(final Term term) {
        printer.println("<TERM>");
        printer.pushLevel();
    }

    public void visitLeave(final Term term) {
        printer.popLevel();
        printer.println("</TERM>");
    }

    public void visitEnter(final VariableList variableList) {
        printer.println("<VARLIST>");
        printer.pushLevel();
    }

    public void visitLeave(final VariableList variableList) {
        printer.popLevel();
        printer.println("</VARLIST>");
    }

    // TODO mime 20070217: what do we do if an atom is not first element of a list?
    // we wouldn't get it here!!! But can we think of an output syntax anyway????
    public void visitEnter(final ElementList list) {
        final String operator = list.getOperator();
        printer.print("<" + operator);
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
        printer.println("</" + list.getOperator() + ">");
    }

    public void visitEnter(final LiteratureItemList list) {
        printer.println("<BIBLIOGRAPHY>");
        printer.pushLevel();
    }

    public void visitLeave(final LiteratureItemList list) {
        printer.popLevel();
        printer.println("</BIBLIOGRAPHY>");
    }

    public void visitEnter(final LiteratureItem item) {
        printer.print("<ITEM");
        if (item.getLabel() != null) {
            printer.print(" label=\"" + item.getLabel() + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final LiteratureItem item) {
        printer.popLevel();
        printer.println("</ITEM>");
    }

    public String getPluginId() {
        return this.getClass().getName();
    }

    public String getPluginName() {
        return "generate XML";
    }

    public String getPluginDescription() {
        return "Transformes QEDEQ module into XML data";
    }

}
