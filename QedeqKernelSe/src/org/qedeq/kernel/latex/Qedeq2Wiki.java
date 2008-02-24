/* $Id: Qedeq2Wiki.java,v 1.9 2008/01/26 12:39:09 m31 Exp $
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Chapter;
import org.qedeq.kernel.base.module.ChapterList;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.Latex;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.LiteratureItem;
import org.qedeq.kernel.base.module.LiteratureItemList;
import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.base.module.NodeType;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Section;
import org.qedeq.kernel.base.module.SectionList;
import org.qedeq.kernel.base.module.Subsection;
import org.qedeq.kernel.base.module.SubsectionList;
import org.qedeq.kernel.base.module.SubsectionType;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.StringUtility;


/**
 * Transfer a QEDEQ module into text files in MediaWiki format.
 * <p>
 * LATER mime 20060831: This is just a quick hacked generator. This class just
 * generates some text files.
 * <p>
 * <b>It is not finished!</b>
 * <p>
 * It should be compared with Qedeq2Latex and then refactored to reuse common functions.
 *
 * @version $Revision: 1.9 $
 * @author  Michael Meyling
 */
public final class Qedeq2Wiki {

    /** This class. */
    private static final Class CLASS = Qedeq2Wiki.class;

    /** Alphabet for tagging. */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /** QEDEQ module properties object to work on. */
    private final QedeqBo prop;

    /** Output goes here. */
    private PrintStream printer;

    /** Filter text to get and produce text in this language. */
    private String language;

    /** Transformer to get LaTeX out of {@link Element}s. */
    private final Element2Latex elementConverter;

    /** Destination directory. */
    private File outputDirectory;

    /**
     * Constructor.
     *
     * @param   prop            QEDEQ module properties object.
     */
    public Qedeq2Wiki(final QedeqBo prop) {
        this.prop = prop;
        this.elementConverter = new Element2Latex((prop.hasLoadedRequiredModules()
            ? prop.getRequiredModules() : null));
    }

    /**
     * Prints a LaTeX file into a given output stream.
     *
     * @param   language        Filter text to get and produce text in this language only.
     * @param   level           Filter for this detail level. TODO mime 20050205: not supported yet.
     * @param   outputDirectory Write files to this directory.
     * @throws  IOException
     */
    public final synchronized void printWiki(final String language, final String level,
            final File outputDirectory) throws IOException {
        if (language == null) {
            this.language = "en";
        } else {
            this.language = language;
        }
        this.outputDirectory = outputDirectory;
        printQedeqChapters();
        printQedeqBibliography();
        if (printer.checkError()) {
            throw new IOException("TODO mime: better use another OutputStream");
        }
    }

    /**
     * Print all chapters.
     *
     * @throws IOException  Writing failed.
     */
    private void printQedeqChapters() throws IOException {
        final ChapterList chapters = prop.getQedeq().getChapterList();
        for (int i = 0; i < chapters.size(); i++) {
            final String label = prop.getQedeq().getHeader().getSpecification()
                .getName() + "_ch_" + i;
            final OutputStream outputStream = new FileOutputStream(new File(outputDirectory,
                label + "_" + language + ".wiki"));
            this.printer = new PrintStream(outputStream);
            final Chapter chapter = chapters.get(i);
            printer.print("\\chapter");
            if (chapter.getNoNumber() != null && chapter.getNoNumber().booleanValue()) {
                printer.print("*");
            }
            printer.println("== " + getLatexListEntry(chapter.getTitle()) + " ==");
            printer.println();
            printer.println("<div id=\"" + label + "\"></div>");
//            if (chapter.getNoNumber() != null && chapter.getNoNumber().booleanValue()) {
            printer.println();
            if (chapter.getIntroduction() != null) {
                printer.println(getLatexListEntry(chapter.getIntroduction()));
                printer.println();
            }
            final SectionList sections = chapter.getSectionList();
            if (sections != null) {
                printSections(i, sections);
                printer.println();
            }
            printer.println("%% end of chapter " + getLatexListEntry(chapter.getTitle()));
            printer.println();
        }
    }

    /**
     * Print bibliography (if any).
     */
    private void printQedeqBibliography() {
        final LiteratureItemList items = prop.getQedeq().getLiteratureItemList();
        if (items == null) {
            return;
        }
        printer.println("\\begin{thebibliography}{99}");
        for (int i = 0; i < items.size(); i++) {
            final LiteratureItem item = items.get(i);
            printer.print("\\bibitem{" + item.getLabel() + "} ");
            printer.println(getLatexListEntry(item.getItem()));
            printer.println();
        }
        printer.println("\\end{thebibliography}");
        printer.println("\\addcontentsline{toc}{chapter}{Literaturverzeichnis}");
    }

    /**
     * Print all given sections.
     *
     * @param   chapter     Chapter number.
     * @param   sections    List of sections.
     */
    private void printSections(final int chapter, final SectionList sections) {
        if (sections == null) {
            return;
        }
        for (int i = 0; i < sections.size(); i++) {
            final Section section = sections.get(i);
            printer.print("\\section{");
            printer.print(getLatexListEntry(section.getTitle()));
            final String label = "chapter" + chapter + "_section" + i;
            printer.println("} \\label{" + label + "} \\hypertarget{" + label + "}{}");
            printer.println(getLatexListEntry(section.getIntroduction()));
            printer.println();
            printSubsections(section.getSubsectionList());
        }
    }

    /**
     * Print all given subsections.
     *
     * @param   nodes   List of subsections.
     */
    private void printSubsections(final SubsectionList nodes) {
        if (nodes == null) {
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            final SubsectionType subsectionType = nodes.get(i);
            if (subsectionType instanceof Node) {
                final Node node = (Node) subsectionType;
                printer.println(getLatexListEntry(node.getPrecedingText()));
                printer.println();
                printer.println("\\par");
                final String id = node.getId();
                final NodeType type = node.getNodeType();
                String title = null;
                if (node.getTitle() != null) {
                    title = getLatexListEntry(node.getTitle());
                }
                if (type instanceof Axiom) {
                    printAxiom((Axiom) type, title, id);
                } else if (type instanceof PredicateDefinition) {
                    printPredicateDefinition((PredicateDefinition) type, title, id);
                } else if (type instanceof FunctionDefinition) {
                    printFunctionDefinition((FunctionDefinition) type, title, id);
                } else if (type instanceof Proposition) {
                    printProposition((Proposition) type, title, id);
                } else if (type instanceof Rule) {
                    printRule((Rule) type, title, id);
                } else {
                    throw new RuntimeException((type != null ? "unknown type: "
                        + type.getClass().getName() : "node type empty"));
                }
                printer.println();
                printer.println(getLatexListEntry(node.getSucceedingText()));
            } else {
                final Subsection subsection = (Subsection) subsectionType;
                if (subsection.getTitle() != null) {
                    printer.print("\\subsection{");
                    printer.println(getLatexListEntry(subsection.getTitle()));
                    printer.println("}");
                }
                printer.println(getLatexListEntry(subsection.getLatex()));
            }
            printer.println();
            printer.println();
        }
    }

    /**
     * Print axiom.
     *
     * @param   axiom   Print this.
     * @param   title   Extra name.
     * @param   id      Label for marking this axiom.
     */
    private void printAxiom(final Axiom axiom, final String title, final String id) {
        printer.println("\\begin{ax}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        printFormula(axiom.getFormula().getElement());
        printer.println(getLatexListEntry(axiom.getDescription()));
        printer.println("\\end{ax}");
    }

    /**
     * Print top level formula. If the formula has the form <code>AND(.., .., ..)</code> the
     * formula is broken down in several labeled lines.
     *
     * @param   element     Formula to print.
     * @param   mainLabel   Main formula label.
     */
    private void printTopFormula(final Element element, final String mainLabel) {
        if (!element.isList() || !element.getList().getOperator().equals("AND")) {
            printFormula(element);
            return;
        }
        final ElementList list = element.getList();
        printer.println("\\mbox{}");
        printer.println("\\begin{longtable}{{@{\\extracolsep{\\fill}}p{0.9\\linewidth}l}}");
        for (int i = 0; i < list.size(); i++)  {
            final String label = (i < ALPHABET.length() ? "" + ALPHABET .charAt(i) : "" + i);
            printer.println("\\centering $" + getLatex(list.getElement(i)) + "$"
                + " & \\label{" + mainLabel + ":" + label + "} \\hypertarget{" + mainLabel + ":"
                + label + "}{} \\mbox{\\emph{(" + label + ")}} "
                + (i + 1 < list.size() ? "\\\\" : ""));
        }
        printer.println("\\end{longtable}");
    }

    /**
     * Print formula.
     *
     * @param   element Formula to print.
     */
    private void printFormula(final Element element) {
        printer.println("\\mbox{}");
        printer.println("\\begin{longtable}{{@{\\extracolsep{\\fill}}p{\\linewidth}}}");
        printer.println("\\centering $" + getLatex(element) + "$");
        printer.println("\\end{longtable}");
    }

    /**
     * Print predicate definition.
     *
     * @param   definition  Print this.
     * @param   title       Extra name.
     * @param   id          Label for marking this definition.
     */
    private void printPredicateDefinition(final PredicateDefinition definition, final String title,
            final String id) {
        final StringBuffer define = new StringBuffer("$$" + definition.getLatexPattern());
        final VariableList list = definition.getVariableList();
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Trace.trace(CLASS, this, "printDefinition", "replacing!");
                StringUtility.replace(define, "#" + (i + 1), getLatex(list.get(i)));
            }
        }
        if (definition.getFormula() != null) {
            printer.println("\\begin{defn}" + (title != null ? "[" + title + "]" : ""));
            printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
            define.append("\\ :\\leftrightarrow \\ ");
            define.append(getLatex(definition.getFormula().getElement()));
        } else {
            printer.println("\\begin{idefn}" + (title != null ? "[" + title + "]" : ""));
            printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        }
        define.append("$$");
        elementConverter.addPredicate(definition);
        Trace.param(CLASS, this, "printDefinition", "define", define);
        printer.println(define);
        printer.println(getLatexListEntry(definition.getDescription()));
        if (definition.getFormula() != null) {
            printer.println("\\end{defn}");
        } else {
            printer.println("\\end{idefn}");
        }
    }

    /**
     * Print function definition.
     *
     * @param   definition  Print this.
     * @param   title       Extra name.
     * @param   id          Label for marking this definition.
     */
    private void printFunctionDefinition(final FunctionDefinition definition, final String title,
            final String id) {
        final StringBuffer define = new StringBuffer("$$" + definition.getLatexPattern());
        final VariableList list = definition.getVariableList();
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Trace.trace(CLASS, this, "printDefinition", "replacing!");
                StringUtility.replace(define, "#" + (i + 1), getLatex(list.get(i)));
            }
        }
        if (definition.getTerm() != null) {
            printer.println("\\begin{defn}" + (title != null ? "[" + title + "]" : ""));
            printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
            define.append("\\ := \\ ");
            define.append(getLatex(definition.getTerm().getElement()));
        } else {
            printer.println("\\begin{idefn}" + (title != null ? "[" + title + "]" : ""));
            printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        }
        define.append("$$");
        elementConverter.addFunction(definition);
        Trace.param(CLASS, this, "printDefinition", "define", define);
        printer.println(define);
        printer.println(getLatexListEntry(definition.getDescription()));
        if (definition.getTerm() != null) {
            printer.println("\\end{defn}");
        } else {
            printer.println("\\end{idefn}");
        }
    }

    /**
     * Print proposition.
     *
     * @param   proposition Print this.
     * @param   title       Extra name.
     * @param   id          Label for marking this proposition.
     */
    private void printProposition(final Proposition proposition, final String title,
            final String id) {
        printer.println("\\begin{prop}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        printTopFormula(proposition.getFormula().getElement(), id);
        printer.println(getLatexListEntry(proposition.getDescription()));
        printer.println("\\end{prop}");
        if (proposition.getProofList() != null) {
            for (int i = 0; i < proposition.getProofList().size(); i++) {
                printer.println("\\begin{proof}");
                printer.println(getLatexListEntry(proposition.getProofList().get(i)
                    .getNonFormalProof()));
                printer.println("\\end{proof}");
            }
        }
    }

    /**
     * Print rule declaration.
     *
     * @param   rule        Print this.
     * @param   title       Extra name.
     * @param   id          Label for marking this proposition.
     */
    private void printRule(final Rule rule, final String title,
            final String id) {
        printer.println("\\begin{rul}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        printer.println(getLatexListEntry(rule.getDescription()));
        printer.println("\\end{rul}");

// TODO mime 20051210: are these informations equivalent to a formal proof?
/*
        if (null != rule.getLinkList()) {
            printer.println("\\begin{proof}");
            printer.println("Rule name: " + rule.getName());
            printer.println();
            printer.println();
            for (int i = 0; i < rule.getLinkList().size(); i++) {
                printer.println(rule.getLinkList().get(i));
            }
            printer.println("\\end{proof}");
        }
*/
        if (rule.getProofList() != null) {
            for (int i = 0; i < rule.getProofList().size(); i++) {
                printer.println("\\begin{proof}");
                printer.println(getLatexListEntry(rule.getProofList().get(i)
                    .getNonFormalProof()));
                printer.println("\\end{proof}");
            }
        }
    }

    /**
     * Get LaTeX element presentation.
     *
     * @param   element    Print this element.
     * @return  LaTeX form of element.
     */
    private String getLatex(final Element element) {
        return elementConverter.getLatex(element);
    }

    /**
     * Filters correct entry out of LaTeX list. Filter criterion is for example the correct
     * language.
     * TODO mime 20050205: filter level too?
     *
     * @param   list    List of LaTeX texts.
     * @return  Filtered text.
     */
    private String getLatexListEntry(final LatexList list) {
        if (list == null) {
            return "";
        }
        for (int i = 0; i < list.size(); i++) {
            if (language.equals(list.get(i).getLanguage())) {
                return getLatex(list.get(i));
            }
        }
        // assume entry with missing language as default
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLanguage() == null) {
                return getLatex(list.get(i));
            }
        }
        for (int i = 0; i < list.size(); i++) { // LATER mime 20050222: evaluate default?
            return "MISSING! OTHER: " + getLatex(list.get(i));
        }
        return "MISSING!";
    }

    /**
     * Get really LaTeX. Does some simple character replacements for umlauts.
     * TODO mime 20050205: filter more than German umlauts
     *
     * @param   latex   Unescaped text.
     * @return  Really LaTeX.
     */
    private String getLatex(final Latex latex) {
        if (latex == null || latex.getLatex() == null) {
            return "";
        }
        return LatexTextParser.transform(latex.getLatex());
    }

}
