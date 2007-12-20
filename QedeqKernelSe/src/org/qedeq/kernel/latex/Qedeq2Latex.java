/* $Id: Qedeq2Latex.java,v 1.47 2007/05/10 00:37:53 m31 Exp $
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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.base.module.Author;
import org.qedeq.kernel.base.module.AuthorList;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Chapter;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.Header;
import org.qedeq.kernel.base.module.Import;
import org.qedeq.kernel.base.module.ImportList;
import org.qedeq.kernel.base.module.Latex;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.LinkList;
import org.qedeq.kernel.base.module.LiteratureItem;
import org.qedeq.kernel.base.module.LiteratureItemList;
import org.qedeq.kernel.base.module.LocationList;
import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proof;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Section;
import org.qedeq.kernel.base.module.SectionList;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.base.module.Subsection;
import org.qedeq.kernel.base.module.UsedByList;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTransverser;
import org.qedeq.kernel.dto.module.PredicateDefinitionVo;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.ReplaceUtility;
import org.qedeq.kernel.utility.TextOutput;


/**
 * Transfer a QEDEQ module into a LaTeX file.
 * <p>
 * <b>TODO mime 20070131: This is just a quick written generator. No parsing or validation
 * of inline LaTeX text is done. No references to other QEDEQ modules are resolved. This class just
 * generates some LaTeX output to be able to get a visual impression of a QEDEQ module.</b>
 * <p>
 * This generator operates operates against the interface declaration of a QEDEQ module.
 * A business object is not yet required.
 *
 *
 * @version $Revision: 1.47 $
 * @author  Michael Meyling
 */
public final class Qedeq2Latex extends AbstractModuleVisitor {

    /** Transverse QEDEQ module with this transverser. */
    private final QedeqNotNullTransverser transverser;

    /** Output goes here. */
    private final TextOutput printer;

    /** QEDEQ BO object to work on. */
    private final QedeqBo qedeqBo;

    /** Filter text to get and produce text in this language. */
    private final String language;

    /** Filter for this detail level. TODO mime 20050205: not used yet. */
    private final String level;

    /** Maps identifiers to {@link PredicateDefinition}s. */
    private final Map predicateDefinitions = new HashMap();

    /** Maps identifiers to {@link FunctionDefinition}s. */
    private final Map functionDefinitions = new HashMap();

    /** Current chapter number, starting with 0. */
    private int chapterNumber;

    /** Current section number, starting with 0. */
    private int sectionNumber;

    /** Current node id. */
    private String id;

    /** Current node title. */
    private String title;

    /** Alphabet for tagging. */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Constructor.
     *
     * @param   qedeq           QEDEQ BO object.
     * @param   globalContext   Module location information.
     * @param   printer         Print herein.
     * @param   language        Filter text to get and produce text in this language only.
     * @param   level           Filter for this detail level. TODO mime 20050205: not supported yet.
     */
    private Qedeq2Latex(final QedeqBo qedeq, final URL globalContext,
            final TextOutput printer, final String language, final String level) {
        this.qedeqBo = qedeq;
        this.transverser = new QedeqNotNullTransverser(globalContext, this);
        this.printer = printer;
        if (language == null) {
            this.language = "en";
        } else {
            this.language = language;
        }
        if (level == null) {
            this.level = "1";
        } else {
            this.level = level;
        }
        final PredicateDefinitionVo equal = new PredicateDefinitionVo();
        // LATER mime 20050224: quick hack to have the logical identity operator
        equal.setArgumentNumber("2");
        equal.setName("equal");
        equal.setLatexPattern("#1 \\ =  \\ #2");
        predicateDefinitions.put("equal_2", equal);
        // TODO mime 20060822: quick hack to get the negation of the logical identity operator
        final PredicateDefinitionVo notEqual = new PredicateDefinitionVo();
        notEqual.setArgumentNumber("2");
        notEqual.setName("notEqual");
        notEqual.setLatexPattern("#1 \\ \\neq  \\ #2");
        predicateDefinitions.put("notEqual_2", notEqual);
    }

    /**
     * Prints a LaTeX representation of given QEDEQ module into a given output stream.
     *
     * @param   globalContext   Module location information.
     * @param   qedeq           Basic QEDEQ module object.
     * @param   printer         Print herein.
     * @param   language        Filter text to get and produce text in this language only.
     * @param   level           Filter for this detail level. TODO mime 20050205: not supported yet.
     * @throws  ModuleDataException Major problem occurred.
     * @throws  IOException
     */
    public static void print(final URL globalContext, final QedeqBo qedeq,
            final TextOutput printer, final String language, final String level)
            throws ModuleDataException, IOException {
        final Qedeq2Latex converter = new Qedeq2Latex(qedeq, globalContext, printer,
            language, level);
        converter.printLatex();
    }

    /**
     * Prints a LaTeX file into a given output stream.
     *
     * @throws  IOException         Writing failed.
     * @throws  ModuleDataException Exception during transversion.
     */
    private final void printLatex() throws IOException, ModuleDataException {
        transverser.accept(qedeqBo.getQedeq());
        printer.flush();
        if (printer.checkError()) {
            throw printer.getError();
        }
    }

    public final void visitEnter(final Qedeq qedeq) {
        printer.println("% -*- TeX:" + language.toUpperCase() + " -*-");
        printer.println("%%% ====================================================================");
        printer.println("%%% @LaTeX-file  " + printer.getName());
        printer.println("%%% Generated at " + getTimestamp());
        printer.println("%%% ====================================================================");
        printer.println();
        printer.println(
            "%%% Permission is granted to copy, distribute and/or modify this document");
        printer.println("%%% under the terms of the GNU Free Documentation License, Version 1.2");
        printer.println("%%% or any later version published by the Free Software Foundation;");
        printer.println(
            "%%% with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.");
        printer.println();
        printer.println("\\documentclass[a4paper,german,10pt,twoside]{book}");
        if ("de".equals(language)) {
            printer.println("\\usepackage[german]{babel}");
        } else {
            if (!"en".equals(language)) {
                printer.println("%%% TODO unknown language: " + language);
            }
            printer.println("\\usepackage[english]{babel}");
        }
        printer.println("\\usepackage{makeidx}");
        printer.println("\\usepackage{amsmath,amsthm,amssymb}");
        printer.println("\\usepackage{color}");
        printer.println("\\usepackage[bookmarks,bookmarksnumbered,bookmarksopen,");
        printer.println("   colorlinks,linkcolor=webgreen,pagebackref]{hyperref}");
        printer.println("\\definecolor{webgreen}{rgb}{0,.5,0}");
        printer.println("\\usepackage{graphicx}");
        printer.println("\\usepackage{xr}");
        printer.println("\\usepackage{epsfig,longtable}");
        printer.println("\\usepackage{tabularx}");
        printer.println();
        if ("de".equals(language)) {
            printer.println("\\newtheorem{thm}{Theorem}[chapter]");
            printer.println("\\newtheorem{cor}[thm]{Korollar}");
            printer.println("\\newtheorem{lem}[thm]{Lemma}");
            printer.println("\\newtheorem{prop}[thm]{Proposition}");
            printer.println("\\newtheorem{ax}{Axiom}");
            printer.println("\\newtheorem{rul}{Regel}");
            printer.println();
            printer.println("\\theoremstyle{definition}");
            printer.println("\\newtheorem{defn}[thm]{Definition}");
            printer.println("\\newtheorem{idefn}[thm]{Initiale Definition}");
            printer.println();
            printer.println("\\theoremstyle{remark}");
            printer.println("\\newtheorem{rem}[thm]{Bemerkung}");
            printer.println("\\newtheorem*{notation}{Notation}");
        } else {
            if (!"en".equals(language)) {
                printer.println("%%% TODO unknown language: " + language);
            }
            printer.println("\\newtheorem{thm}{Theorem}[chapter]");
            printer.println("\\newtheorem{cor}[thm]{Corollary}");
            printer.println("\\newtheorem{lem}[thm]{Lemma}");
            printer.println("\\newtheorem{prop}[thm]{Proposition}");
            printer.println("\\newtheorem{ax}{Axiom}");
            printer.println("\\newtheorem{rul}{Rule}");
            printer.println();
            printer.println("\\theoremstyle{definition}");
            printer.println("\\newtheorem{defn}[thm]{Definition}");
            printer.println("\\newtheorem{idefn}[thm]{Initial Definition}");
            printer.println();
            printer.println("\\theoremstyle{remark}");
            printer.println("\\newtheorem{rem}[thm]{Remark}");
            printer.println("\\newtheorem*{notation}{Notation}");
        }
        printer.println();
        printer.println("\\addtolength{\\textheight}{7\\baselineskip}");
        printer.println("\\addtolength{\\topmargin}{-5\\baselineskip}");
        printer.println();
        printer.println("\\setlength{\\parindent}{0pt}");
        printer.println();
        printer.println("\\frenchspacing \\sloppy");
        printer.println();
        printer.println("\\makeindex");
        printer.println();
        printer.println();
    }

    public final void visitLeave(final Qedeq qedeq) {
        printer.println("\\backmatter");
        printer.println();
        printer.println("\\addcontentsline{toc}{chapter}{\\indexname} \\printindex");
        printer.println();
        printer.println("\\end{document}");
        printer.println();
    }

    public void visitEnter(final Header header) {
        final LatexList title = header.getTitle();
        printer.print("\\title{");
        printer.print(getLatexListEntry(title));
        printer.println("}");
        printer.println("\\author{");
        final AuthorList authors = qedeqBo.getQedeq().getHeader().getAuthorList();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                printer.println(", ");
            }
            final Author author = authors.get(i);
            printer.print(author.getName().getLatex());
// TODO mime 20070206            if (author.getEmail() != null)
        }
        printer.println();
        printer.println("}");
        printer.println();
        printer.println("\\begin{document}");
        printer.println();
        printer.println("\\maketitle");
        printer.println();
        printer.println("\\setlength{\\parskip}{5pt plus 2pt minus 1pt}");
        printer.println("\\mbox{}");
        printer.println("\\vfill");
        printer.println();
        final String url = getUrl(header.getSpecification());
        if (url != null && url.length() > 0) {
            printer.println("\\par");
            if ("de".equals(language)) {
                printer.println("Die Quelle f{\"ur} dieses Dokument ist hier zu finden:");
            } else {
                if (!"en".equals(language)) {
                    printer.println("%%% TODO unknown language: " + language);
                }
                printer.println("The source for this document can be found here:");
            }
            printer.println("\\par");
            printer.println("\\url{" + getUrl(header.getSpecification()) + "}");
            printer.println();
        }
        {
            printer.println("\\par");
            if ("de".equals(language)) {
                printer.println("Die vorliegende Publikation ist urheberrechtlich gesch{\"u}tzt.");
            } else {
                if (!"en".equals(language)) {
                    printer.println("%%% TODO unknown language: " + language);
                }
                printer.println("Copyright by the authors. All rights reserved.");
            }
        }
        final String email = header.getEmail();
        if (email != null && email.length() > 0) {
            final String emailUrl = "\\url{mailto:" + email + "}";
            printer.println("\\par");
            if ("de".equals(language)) {
                printer.println("Bei Fragen, Anregungen oder Bitte um Aufnahme in die Liste der"
                    + " abh{\"a}ngigen Module schicken Sie bitte eine EMail an die Addresse "
                    + emailUrl);
            } else {
                if (!"en".equals(language)) {
                    printer.println("%%% TODO unknown language: " + language);
                }
                printer.println("If you have any questions, suggestions or want to add something"
                    + " to the list of modules that use this one, please send an email to the"
                    + " address " + emailUrl);
            }
            printer.println();
        }
        printer.println("\\setlength{\\parskip}{0pt}");
        printer.println("\\tableofcontents");
        printer.println();
        printer.println("\\setlength{\\parskip}{5pt plus 2pt minus 1pt}");
        printer.println();
    }

    /**
     * Get URL for QEDEQ XML module.
     *
     * @param   specification   Find URL for this location list.
     * @return  URL or <code>null</code> if none (valid?) was found.
     */
    private String getUrl(final Specification specification) {
        final LocationList list = specification.getLocationList();
        if (list == null || list.size() <= 0
                || list.get(0).getLocation().length() <= "http://a.b".length()) {
            return "";
        }
        String location = list.get(0).getLocation();
        if (!location.endsWith("/")) {
            location += "/";
        }
        location += specification.getName();
        location += ".xml";
        return location;
    }

    public void visitEnter(final Chapter chapter) {
        printer.print("\\chapter");
        if (chapter.getNoNumber() != null && chapter.getNoNumber().booleanValue()) {
            printer.print("*");
        }
        printer.print("{");
        printer.print(getLatexListEntry(chapter.getTitle()));
        final String label = "chapter" + chapterNumber;
        printer.println("} \\label{" + label + "} \\hypertarget{" + label + "}{}");
        if (chapter.getNoNumber() != null && chapter.getNoNumber().booleanValue()) {
            printer.println("\\addcontentsline{toc}{chapter}{"
                + getLatexListEntry(chapter.getTitle()) + "}");
        }
        printer.println();
        if (chapter.getIntroduction() != null) {
            printer.println(getLatexListEntry(chapter.getIntroduction()));
            printer.println();
        }
    }

    public void visitLeave(final Chapter chapter) {
        printer.println("%% end of chapter " + getLatexListEntry(chapter.getTitle()));
        printer.println();
        chapterNumber++;    // increase global chapter number
        sectionNumber = 0;  // reset section number
    }

    public void visitLeave(final SectionList list) {
        printer.println();
    }

    public void visitEnter(final Section section) {
/* TODO mime 20070131: use this information?
        if (section.getNoNumber() != null) {
           printer.print(" noNumber=\"" + section.getNoNumber().booleanValue() + "\"");
        }
*/
        printer.print("\\section{");
        printer.print(getLatexListEntry(section.getTitle()));
        final String label = "chapter" + chapterNumber + "_section" + sectionNumber;
        printer.println("} \\label{" + label + "} \\hypertarget{" + label + "}{}");
        printer.println(getLatexListEntry(section.getIntroduction()));
        printer.println();
    }

    public void visitLeave(final Section section) {
        sectionNumber++;    // increase global section number
    }

    public void visitEnter(final Subsection subsection) {
/* TODO mime 20070131: use this information?
        if (subsection.getId() != null) {
            printer.print(" id=\"" + subsection.getId() + "\"");
        }
        if (subsection.getLevel() != null) {
            printer.print(" level=\"" + subsection.getLevel() + "\"");
        }
*/
        if (subsection.getTitle() != null) {
            printer.print("\\subsection{");
            printer.println(getLatexListEntry(subsection.getTitle()));
            printer.println("}");
        }
        if (subsection.getId() != null) {
            printer.println("\\label{" + subsection.getId() + "} \\hypertarget{"
                + subsection.getId() + "}{}");
        }
        printer.println(getLatexListEntry(subsection.getLatex()));
    }

    public void visitLeave(final Subsection subsection) {
        printer.println();
        printer.println();
    }

    public void visitEnter(final Node node) {
/** TODO mime 20070131: level filter
        if (node.getLevel() != null) {
            printer.print(" level=\"" + node.getLevel() + "\"");
        }
*/
        printer.println("\\par");
        printer.println(getLatexListEntry(node.getPrecedingText()));
        printer.println();
        id = node.getId();
        title = null;
        if (node.getTitle() != null) {
            title = getLatexListEntry(node.getTitle());
        }
    }

    public void visitLeave(final Node node) {
        printer.println();
        printer.println(getLatexListEntry(node.getSucceedingText()));
        printer.println();
        printer.println();
    }

    public void visitEnter(final Axiom axiom) {
        printer.println("\\begin{ax}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        printFormula(axiom.getFormula().getElement());
        printer.println(getLatexListEntry(axiom.getDescription()));
        printer.println("\\end{ax}");
    }

    public void visitEnter(final Proposition proposition) {
        printer.println("\\begin{prop}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        printTopFormula(proposition.getFormula().getElement(), id);
        printer.println(getLatexListEntry(proposition.getDescription()));
        printer.println("\\end{prop}");
    }

    public void visitEnter(final Proof proof) {
/* TODO mime 20070131: filter level and kind
        if (proof.getKind() != null) {
            printer.print(" kind=\"" + proof.getKind() + "\"");
        }
        if (proof.getLevel() != null) {
            printer.print(" level=\"" + proof.getLevel() + "\"");
        }
*/
        printer.println("\\begin{proof}");
        printer.println(getLatexListEntry(proof.getNonFormalProof()));
        printer.println("\\end{proof}");
    }

    public void visitEnter(final PredicateDefinition definition) {
        final StringBuffer define = new StringBuffer("$$" + definition.getLatexPattern());
        final VariableList list = definition.getVariableList();
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Trace.trace(this, "printPredicateDefinition", "replacing!");
                ReplaceUtility.replace(define, "#" + (i + 1), getLatex(list.get(i)));
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
        predicateDefinitions.put(definition.getName() + "_" + definition.getArgumentNumber(),
            definition);
        Trace.param(this, "printPredicateDefinition", "define", define);
        printer.println(define);
        printer.println(getLatexListEntry(definition.getDescription()));
        if (definition.getFormula() != null) {
            printer.println("\\end{defn}");
        } else {
            printer.println("\\end{idefn}");
        }
    }

    public void visitEnter(final FunctionDefinition definition) {
        final StringBuffer define = new StringBuffer("$$" + definition.getLatexPattern());
        final VariableList list = definition.getVariableList();
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Trace.trace(this, "printFunctionDefinition", "replacing!");
                ReplaceUtility.replace(define, "#" + (i + 1), getLatex(list.get(i)));
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
        functionDefinitions.put(definition.getName() + "_" + definition.getArgumentNumber(),
            definition);
        Trace.param(this, "printFunctionDefinition", "define", define);
        printer.println(define);
        printer.println(getLatexListEntry(definition.getDescription()));
        if (definition.getTerm() != null) {
            printer.println("\\end{defn}");
        } else {
            printer.println("\\end{idefn}");
        }
    }

    public void visitLeave(final FunctionDefinition definition) {
    }

    public void visitEnter(final Rule rule) {
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

    public void visitLeave(final Rule rule) {
    }

    public void visitEnter(final LinkList linkList) {
        if (linkList.size() <= 0) {
            return;
        }
        if ("de".equals(language)) {
            printer.println("Basierend auf: ");
        } else {
            if (!"en".equals(language)) {
                printer.println("%%% TODO unknown language: " + language);
            }
            printer.println("Based on: ");
        }
        for (int i = 0; i < linkList.size(); i++) {
            if (linkList.get(i) != null) {
                printer.print(" \\ref{" + linkList.get(i) + "}");
            }
        };
        printer.println();
    }

    public void visitEnter(final LiteratureItemList list) {
        printer.println("\\begin{thebibliography}{99}");
        // TODO mime 20060926: remove language dependency
        if ("de".equals(language)) {
            printer.println("\\addcontentsline{toc}{chapter}{Literaturverzeichnis}");
        } else {
            if (!"en".equals(language)) {
                printer.println("%%% TODO unknown language: " + language);
            }
            printer.println("\\addcontentsline{toc}{chapter}{Bibliography}");
        }
        final ImportList imports = qedeqBo.getQedeq().getHeader().getImportList();
        if (imports != null && imports.size() > 0) {
            printer.println();
            printer.println();
            printer.println("%% Used other QEDEQ modules:");
            for (int i = 0; i < imports.size(); i++) {
                final Import imp = imports.get(i);
                printer.print("\\bibitem{" + imp.getLabel() + "} ");
                final Specification spec = imp.getSpecification();
                printer.print(getLatex(spec.getName()));
                if (spec.getLocationList() != null && spec.getLocationList().size() > 0
                        && spec.getLocationList().get(0).getLocation().length() > 0) {
                    printer.print(" ");
                    // TODO mime 20070205: later on here must stand the location that was used
                    //   to verify the document contents
                    // TODO mime 20070205: convert relative address into absolute
                    printer.print("\\url{" + spec.getLocationList().get(0).getLocation() + "}");
                }
                printer.println();
            }
            printer.println();
            printer.println();
            printer.println("%% Other references:");
            printer.println();
        }
    }

    public void visitLeave(final LiteratureItemList list) {
        final UsedByList usedby = qedeqBo.getQedeq().getHeader().getUsedByList();
        if (usedby != null && usedby.size() > 0) {
            printer.println();
            printer.println();
            printer.println("%% QEDEQ modules that use this one:");
            for (int i = 0; i < usedby.size(); i++) {
                final Specification spec = usedby.get(i);
                printer.print("\\bibitem{" + spec.getName() + "} ");
                printer.print(getLatex(spec.getName()));
                final String url = getUrl(spec);
                if (url != null && url.length() > 0) {
                    printer.print(" ");
                    printer.print("\\url{" + url + "}");
                }
                printer.println();
            }
            printer.println();
            printer.println();
        }
        printer.println("\\end{thebibliography}");
    }

    public void visitEnter(final LiteratureItem item) {
        printer.print("\\bibitem{" + item.getLabel() + "} ");
        printer.println(getLatexListEntry(item.getItem()));
        printer.println();
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
            String label = "";
            if (list.size() >= ALPHABET.length() * ALPHABET.length()) {
                label = "" + (i + 1);
            } else {
                if (list.size() > ALPHABET.length()) {
                    final int div = (i / ALPHABET.length());
                    label = "" + ALPHABET.charAt(div);
                }
                label += ALPHABET.charAt(i % ALPHABET.length());
            }
//            final String label = (i < ALPHABET.length() ? "" + ALPHABET .charAt(i) : "" + i);
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
     * Get LaTeX element presentation.
     *
     * @param   element    Print this element.
     * @return  LaTeX form of element.
     */
    private String getLatex(final Element element) {
        return getLatex(element, true);
    }

    /**
     * Get LaTeX element presentation.
     *
     * @param   element Print this element.
     * @param   first   First level?
     * @return  LaTeX form of element.
     */
    private String getLatex(final Element element, final boolean first) {
        final StringBuffer buffer = new StringBuffer();
        if (element.isAtom()) {
            return element.getAtom().getString();
        }
        final ElementList list = element.getList();
        if (list.getOperator().equals("PREDCON")) {
            final String identifier = list.getElement(0).getAtom().getString() + "_"
                + (list.size() - 1);
            // TODO mime 20060922: is only working for definition name + argument number
            //  if argument length is dynamic this dosen't work
            if (predicateDefinitions.containsKey(identifier)) {
                final PredicateDefinition definition = (PredicateDefinition)
                predicateDefinitions.get(identifier);
                final StringBuffer define = new StringBuffer(definition.getLatexPattern());
                for (int i = list.size() - 1; i >= 1; i--) {
                    ReplaceUtility.replace(define, "#" + i, getLatex(list.getElement(i), false));
                }
                buffer.append(define);
            } else {
                buffer.append(identifier);
                buffer.append("(");
                for (int i = 1; i < list.size(); i++) {
                    buffer.append(getLatex(list.getElement(i), false));
                    if (i + 1 < list.size()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")");
            }
        } else if (list.getOperator().equals("PREDVAR")) {
            final String identifier = list.getElement(0).getAtom().getString();
            buffer.append(identifier);
            if (list.size() > 1) {
                buffer.append("(");
                for (int i = 1; i < list.size(); i++) {
                    buffer.append(getLatex(list.getElement(i), false));
                    if (i + 1 < list.size()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")");
            }
        } else if (list.getOperator().equals("FUNCON")) {
            final String identifier = list.getElement(0).getAtom().getString() + "_"
                + (list.size() - 1);
            // TODO mime 20060922: is only working for definition name + argument number
            //  if argument length is dynamic this dosen't work
            if (functionDefinitions.containsKey(identifier)) {
                final FunctionDefinition definition = (FunctionDefinition)
                    functionDefinitions.get(identifier);
                final StringBuffer define = new StringBuffer(definition.getLatexPattern());
                for (int i = list.size() - 1; i >= 1; i--) {
                    ReplaceUtility.replace(define, "#" + i, getLatex(list.getElement(i), false));
                }
                buffer.append(define);
            } else {
                buffer.append(identifier);
                buffer.append("(");
                for (int i = 1; i < list.size(); i++) {
                    buffer.append(getLatex(list.getElement(i), false));
                    if (i + 1 < list.size()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")");
            }
        } else if (list.getOperator().equals("FUNVAR")) {
            final String identifier = list.getElement(0).getAtom().getString();
            buffer.append(identifier);
            if (list.size() > 1) {
                buffer.append("(");
                for (int i = 1; i < list.size(); i++) {
                    buffer.append(getLatex(list.getElement(i), false));
                    if (i + 1 < list.size()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")");
            }
        } else if (list.getOperator().equals("VAR")) {
            final String text = list.getElement(0).getAtom().getString();
            // interpret variable identifier as number
            try {
                final int index = Integer.parseInt(text);
                final String newText = "" + index;
                if (!text.equals(newText) || newText.startsWith("-")) {
                    throw new NumberFormatException("This is no allowed number: " + text);
                }
                switch (index) {
                case 1:
                    return "x";
                case 2:
                    return "y";
                case 3:
                    return "z";
                case 4:
                    return "u";
                case 5:
                    return "v";
                case 6:
                    return "w";
                default:
                    return "x_" + (index - 6);
                }
            } catch (NumberFormatException e) {
                // variable identifier is no number, just take it as it is
                return text;
            }
        } else if (list.getOperator().equals("AND") || list.getOperator().equals("OR")
                || list.getOperator().equals("EQUI") || list.getOperator().equals("IMPL")) {
            final String infix;
            if (list.getOperator().equals("AND")) {
                infix = "\\ \\land \\ ";
            } else if (list.getOperator().equals("OR")) {
                infix = "\\ \\lor \\ ";
            } else if (list.getOperator().equals("EQUI")) {
                infix = "\\ \\leftrightarrow \\ ";
            } else {
                infix = "\\ \\rightarrow \\ ";
            }
            if (!first) {
                buffer.append("(");
            }
            for (int i = 0; i < list.size(); i++) {
                buffer.append(getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(infix);
                }
            }
            if (!first) {
                buffer.append(")");
            }
        } else if (list.getOperator().equals("FORALL") || list.getOperator().equals("EXISTS")
                || list.getOperator().equals("EXISTSU")) {
            final String prefix;
            if (list.getOperator().equals("FORALL")) {
                prefix = "\\forall ";
            } else if (list.getOperator().equals("EXISTS")) {
                prefix = "\\exists ";
            } else {
                prefix = "\\exists! ";
            }
            buffer.append(prefix);
            for (int i = 0; i < list.size(); i++) {
                if (i != 0 || (i == 0 && list.size() <= 2)) {
                    buffer.append(getLatex(list.getElement(i), false));
                }
                if (i + 1 < list.size()) {
                    buffer.append("\\ ");
                }
                if (list.size() > 2 && i == 1) {
                    buffer.append("\\ ");
                }
            }
        } else if (list.getOperator().equals("NOT")) {
            final String prefix = "\\neg ";
            buffer.append(prefix);
            for (int i = 0; i < list.size(); i++) {
                buffer.append(getLatex(list.getElement(i), false));
            }
        } else if (list.getOperator().equals("CLASS")) {
            final String prefix = "\\{ ";
            buffer.append(prefix);
            for (int i = 0; i < list.size(); i++) {
                buffer.append(getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(" \\ | \\ ");
                }
            }
            buffer.append(" \\} ");
        } else if (list.getOperator().equals("CLASSLIST")) {
            final String prefix = "\\{ ";
            buffer.append(prefix);
            for (int i = 0; i < list.size(); i++) {
                buffer.append(getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(", \\ ");
                }
            }
            buffer.append(" \\} ");
        } else if (list.getOperator().equals("QUANTOR_INTERSECTION")) {
            final String prefix = "\\bigcap";
            buffer.append(prefix);
            if (0 < list.size()) {
                buffer.append("{").append(getLatex(list.getElement(0), false)).append("}");
            }
            for (int i = 1; i < list.size(); i++) {
                buffer.append(getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(" \\ \\ ");
                }
            }
            buffer.append(" \\} ");
        } else if (list.getOperator().equals("QUANTOR_UNION")) {
            final String prefix = "\\bigcup";
            buffer.append(prefix);
            if (0 < list.size()) {
                buffer.append("{").append(getLatex(list.getElement(0), false)).append("}");
            }
            for (int i = 1; i < list.size(); i++) {
                buffer.append(getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(" \\ \\ ");
                }
            }
            buffer.append(" \\} ");
        } else {
            buffer.append(list.getOperator());
            buffer.append("(");
            for (int i = 0; i < list.size(); i++) {
                buffer.append(getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(", ");
                }
            }
            buffer.append(")");
        }
        return buffer.toString();
    }

    /**
     * Get timestamp.
     *
     * @return  Current timestamp.
     */
    private String getTimestamp() {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss,SSS");
        return formatter.format(new Date());
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
     *
     * @param   latex   Unescaped text.
     * @return  Really LaTeX.
     */
    private String getLatex(final Latex latex) {
        if (latex == null || latex.getLatex() == null) {
            return "";
        }
        return escapeUmlauts(latex.getLatex());
    }

    /**
     * Get LaTeX from string. Escapes common characters.
     *
     * @param   text   Unescaped text.
     * @return  LaTeX.
     */
    private String getLatex(final String text) {
        final StringBuffer buffer = new StringBuffer(text);
        IoUtility.deleteLineLeadingWhitespace(buffer);
        ReplaceUtility.replace(buffer, "\\", "\\textbackslash");
        ReplaceUtility.replace(buffer, "$", "\\$");
        ReplaceUtility.replace(buffer, "&", "\\&");
        ReplaceUtility.replace(buffer, "%", "\\%");
        ReplaceUtility.replace(buffer, "#", "\\#");
        ReplaceUtility.replace(buffer, "_", "\\_");
        ReplaceUtility.replace(buffer, "{", "\\{");
        ReplaceUtility.replace(buffer, "}", "\\}");
        ReplaceUtility.replace(buffer, "~", "\\textasciitilde");
        ReplaceUtility.replace(buffer, "^", "\\textasciicircum");
        ReplaceUtility.replace(buffer, "<", "\\textless");
        ReplaceUtility.replace(buffer, ">", "\\textgreater");
        ReplaceUtility.replace(buffer, "\u00fc", "{\\\"u}");
        ReplaceUtility.replace(buffer, "\u00f6", "{\\\"o}");
        ReplaceUtility.replace(buffer, "\u00e4", "{\\\"a}");
        ReplaceUtility.replace(buffer, "\u00dc", "{\\\"U}");
        ReplaceUtility.replace(buffer, "\u00d6", "{\\\"O}");
        ReplaceUtility.replace(buffer, "\u00c4", "{\\\"A}");
        ReplaceUtility.replace(buffer, "\u00df", "{\\ss}");
        return buffer.toString();
    }

    /**
     * Get really LaTeX. Does some simple character replacements for umlauts.
     * TODO mime 20050205: filter more than German umlauts
     *
     * @param   nearlyLatex   Unescaped text.
     * @return  Really LaTeX.
     */
    private String escapeUmlauts(final String nearlyLatex) {
        if (nearlyLatex == null) {
            return "";
        }
        final StringBuffer buffer = new StringBuffer(nearlyLatex);
        IoUtility.deleteLineLeadingWhitespace(buffer);
        ReplaceUtility.replace(buffer, "\u00fc", "{\\\"u}");
        ReplaceUtility.replace(buffer, "\u00f6", "{\\\"o}");
        ReplaceUtility.replace(buffer, "\u00e4", "{\\\"a}");
        ReplaceUtility.replace(buffer, "\u00dc", "{\\\"U}");
        ReplaceUtility.replace(buffer, "\u00d6", "{\\\"O}");
        ReplaceUtility.replace(buffer, "\u00c4", "{\\\"A}");
        ReplaceUtility.replace(buffer, "\u00df", "{\\ss}");
        return buffer.toString();
    }

}
