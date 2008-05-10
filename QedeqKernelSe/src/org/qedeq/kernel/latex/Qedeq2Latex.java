/* $Id: Qedeq2Latex.java,v 1.50 2008/03/27 05:16:25 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.qedeq.kernel.bo.control.ControlVisitor;
import org.qedeq.kernel.bo.control.DefaultModuleAddress;
import org.qedeq.kernel.bo.control.KernelQedeqBo;
import org.qedeq.kernel.bo.control.QedeqBoDuplicateLanguageChecker;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.dto.module.NodeVo;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.StringUtility;
import org.qedeq.kernel.utility.TextOutput;


/**
 * Transfer a QEDEQ module into a LaTeX file.
 * <p>
 * <b>This is just a quick written generator. No parsing or validation
 * of inline LaTeX text is done. This class just generates some LaTeX output to be able to
 * get a visual impression of a QEDEQ module.</b>
 * <p>
 * TODO mime 20080330: we should be able to generate a authors help LaTeX document that contains the
 *                     labels in the generated document. So referencing is a lot easier...
 *
 * @version $Revision: 1.50 $
 * @author  Michael Meyling
 */
public final class Qedeq2Latex extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = Qedeq2Latex.class;

    /** Output goes here. */
    private final TextOutput printer;

    /** Filter text to get and produce text in this language. */
    private final String language;

    /** Filter for this detail level. TODO mime 20080330: not used yet, but we should use it */
    private final String level;

    /** Transformer to get LaTeX out of {@link Element}s. */
    private final Element2Latex elementConverter;

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
     * @param   prop            QEDEQ BO object.
     * @param   printer         Print herein.
     * @param   language        Filter text to get and produce text in this language only.
     * @param   level           Filter for this detail level. LATER mime 20050205: not supported
     *                          yet.
     */
    private Qedeq2Latex(final KernelQedeqBo prop, final TextOutput printer,
            final String language, final String level) {
        super(prop);
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
        this.elementConverter = new Element2Latex((prop.hasLoadedRequiredModules()
            ? prop.getRequiredModules() : null));
    }

    /**
     * Prints a LaTeX representation of given QEDEQ module into a given output stream.
     *
     * @param   prop            QEDEQ module properties object.
     * @param   printer         Print herein.
     * @param   language        Filter text to get and produce text in this language only.
     * @param   level           Filter for this detail level. LATER mime 20050205: not supported
     *                          yet.
     * @throws  DefaultSourceFileExceptionList Major problem occurred.
     * @throws  IOException
     */
    public static void print(final KernelQedeqBo prop,
            final TextOutput printer, final String language, final String level)
            throws DefaultSourceFileExceptionList, IOException {
        // first we try to get more information about required modules and their predicates..
        try {
            KernelContext.getInstance().loadRequiredModules(prop.getModuleAddress());
            KernelContext.getInstance().checkModule(prop.getModuleAddress());
        } catch (Exception e) {
            // we continue and ignore external predicates
            Trace.trace(CLASS, "print(QedeqBo, TextOutput, String, String)", e);
        }
        final Qedeq2Latex converter = new Qedeq2Latex(prop, printer,
            language, level);
        try {
            converter.traverse();
        } finally {
            printer.flush();
        }
        if (printer.checkError()) {
            throw printer.getError();
        }
        // TODO mime 20080111: just for testing purpose the following check is
        // integrated here after the LaTeX print. The checking results should be maintained
        // later on as additional information to a module. (Warnings...)
        QedeqBoDuplicateLanguageChecker.check(prop);

    }

    public final void visitEnter(final Qedeq qedeq) {
        printer.println("% -*- TeX:" + language.toUpperCase() + " -*-");
        printer.println("%%% ====================================================================");
        printer.println("%%% @LaTeX-file    " + printer.getName());
        printer.println("%%% Generated from " + getQedeqBo().getModuleAddress());
        printer.println("%%% Generated at   " + getTimestamp());
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
        final LatexList tit = header.getTitle();
        printer.print("\\title{");
        printer.print(getLatexListEntry(tit));
        printer.println("}");
        printer.println("\\author{");
        final AuthorList authors = getQedeqBo().getQedeq().getHeader().getAuthorList();
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
        final String url = getQedeqBo().getUrl().toString();
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
            printer.println("\\url{" + url + "}");
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
     * @param   address         Current module address.
     * @param   specification   Find URL for this location list.
     * @return  URL or <code>""</code> if none (valid?) was found.
     */
    private String getUrl(final ModuleAddress address, final Specification specification) {
        final LocationList list = specification.getLocationList();
        if (list == null || list.size() <= 0) {
            return "";
        }
        try {
            return DefaultModuleAddress.getModulePaths(address,
                specification)[0].getURL().toString();
        } catch (IOException e) {
            return "";
        }
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
/* LATER mime 20070131: use this information?
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
/* LATER mime 20070131: use this information?
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
/* LATER mime 20070131: filter level and kind
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
                Trace.trace(CLASS, this, "printPredicateDefinition", "replacing!");
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
        // we always save the definition, even if there already exists an entry
        elementConverter.addPredicate(definition);
        Trace.param(CLASS, this, "printPredicateDefinition", "define", define);
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
                Trace.trace(CLASS, this, "printFunctionDefinition", "replacing!");
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
        // we always save the definition, even if there already exists an entry
        elementConverter.addFunction(definition);
        Trace.param(CLASS, this, "printFunctionDefinition", "define", define);
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

// LATER mime 20051210: are these informations equivalent to a formal proof?
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
        final ImportList imports = getQedeqBo().getQedeq().getHeader().getImportList();
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
                    printer.print("\\url{" + getUrl(getQedeqBo().getModuleAddress(), spec) + "}");
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
        final UsedByList usedby = getQedeqBo().getQedeq().getHeader().getUsedByList();
        if (usedby != null && usedby.size() > 0) {
            printer.println();
            printer.println();
            printer.println("%% QEDEQ modules that use this one:");
            for (int i = 0; i < usedby.size(); i++) {
                final Specification spec = usedby.get(i);
                printer.print("\\bibitem{" + spec.getName() + "} ");
                printer.print(getLatex(spec.getName()));
                final String url = getUrl(getQedeqBo().getModuleAddress(), spec);
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
        return elementConverter.getLatex(element);
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
     * Get really LaTeX. Does some simple character replacements for umlauts. Also transforms
     * <code>\qref{key}</code> into LaTeX.
     *
     * @param   latex   Unescaped text.
     * @return  Really LaTeX.
     */
    private String getLatex(final Latex latex) {
        if (latex == null || latex.getLatex() == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(escapeUmlauts(latex.getLatex()));

        // LATER mime 20080324: check if LaTeX is correct and no forbidden tags are used

        transformQref(result);

        return result.toString();
    }

    /**
     * Transform <code>\qref{key}</code> entries into common LaTeX code.
     *
     * LATER mime 20080324: write JUnitTests to be sure that no runtime exceptions are thrown if
     * reference is at end of latex etc.
     *
     * @param   result  Work on this buffer.
     */
    private void transformQref(final StringBuffer result) {
        final String method = "transformQref(StringBuffer)";
        int pos1 = 0;
        final String qref = "\\qref{";
        try {
            while (0 <= (pos1 = result.indexOf(qref))) {
                int pos = pos1 + qref.length();
                int pos2 = result.indexOf("}", pos);
                if (pos2 < 0) {
                    break;
                    // just let LaTeX find the errors
                    // LATER mime 20080325: add warning
                }
                String ref = result.substring(pos, pos2);
                Trace.param(CLASS, this, method, "1 ref", ref);

                // exists a sub reference?
                String sub = "";
                final int posb = pos2 + 1;
                if (posb < result.length() && result.charAt(posb) == '[') {
                    pos2 = result.indexOf("]", posb + 1);
                    if (pos2 < 0) {
                        break;
                        // just let LaTeX find the errors
                        // LATER mime 20080325: add warning
                    }
                    sub = result.substring(posb + 1, pos2);
                    Trace.param(CLASS, this, method, "sub", sub);
                }

                // get module label (if any)
                String label = "";
                int dot = ref.indexOf(".");
                if (dot >= 0) {
                    label = ref.substring(0, dot);
                    ref = ref.substring(dot + 1);
                }
                if (label.length() == 0) {
                    if (getQedeqBo().getKernelRequiredModules().getQedeqBo(ref) != null) {
                        label = ref;
                        ref = "";
                    }
                }
                Trace.param(CLASS, this, method, "2 ref", ref);
                Trace.param(CLASS, this, method, "2 label", label);
                KernelQedeqBo prop = getQedeqBo();
                if (label.length() > 0) {
                    prop = prop.getKernelRequiredModules().getKernelQedeqBo(label);
                }
                NodeVo node = null;
                if (prop != null) {
                    if (prop.getLabels() != null) {
                        node = prop.getLabels().getNode(ref);
                    } else {
                        Trace.info(CLASS, this, method, "no labels found");
                    }
                }
                if (node == null) {
                    Trace.info(CLASS, this, method, "node not found for " + ref);
                }
                if (label.length() <= 0) {
                    final String display = getDisplay(ref, node);
                    result.replace(pos1, pos2 + 1, display + "~\\ref{" + ref + "}"
                        + (sub.length() > 0 ? " (" + sub + ")" : ""));
                } else {
                    if (ref.length() <= 0) {
                        result.replace(pos1, pos2 + 1, "\\url{" + getPdfLink(prop) + "}");
                    } else {
                        final String display = getDisplay(ref, node);
                        result.replace(pos1, pos2 + 1, "\\hyperref{" + getPdfLink(prop) + "}{}{"
                            + ref + (sub.length() > 0 ? ":" + sub : "")
                            + "}{" + display + (sub.length() > 0 ? " (" + sub + ")" : "") + "}");
                    }
                }
            }
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, method, e);
        }
    }

    /**
     * Get text to display for a link.
     * TODO mime 20080330: refactor language dependent code
     *
     * @param   ref     Reference label.
     * @param   node    Node to link to. Might be <code>null</code>.
     * @return  Display text.
     */
    private String getDisplay(final String ref, final NodeVo node) {
        String display = ref;
        if (node != null) {
            if (node.getName() != null) {
                display = getLatexListEntry(node.getName());
            } else {
                if (node.getNodeType() instanceof Axiom) {
                    if ("de".equals(language)) {
                        display = "Axiom";
                    } else {
                        display = "axiom";
                    }
                } else if (node.getNodeType() instanceof Proposition) {
                    if ("de".equals(language)) {
                        display = "Proposition";
                    } else {
                        display = "proposition";
                    }
                } else if (node.getNodeType() instanceof FunctionDefinition) {
                    if ("de".equals(language)) {
                        display = "Definition";
                    } else {
                        display = "definition";
                    }
                } else if (node.getNodeType() instanceof PredicateDefinition) {
                    if ("de".equals(language)) {
                        display = "Definition";
                    } else {
                        display = "definition";
                    }
                } else if (node.getNodeType() instanceof Rule) {
                    if ("de".equals(language)) {
                        display = "Regel";
                    } else {
                        display = "rule";
                    }
                } else {
                    if ("de".equals(language)) {
                        display = "Unbekannt";
                    } else {
                        display = "unknown";
                    }
                }
            }
        }
        return display;
    }

    /**
     * Get URL to for PDF version of module.
     *
     * @param   prop    Get URL for this QEDEQ module.
     * @return  URL to PDF.
     */
    private String getPdfLink(final KernelQedeqBo prop) {
        final String url = prop.getUrl().toString();
        final int dot = url.lastIndexOf(".");
        return url.substring(0, dot) + "_" + language + ".pdf";
    }

    /**
     * Get LaTeX from string. Escapes common characters.
     *
     * @param   text   Unescaped text.
     * @return  LaTeX.
     */
    private String getLatex(final String text) {
        final StringBuffer buffer = new StringBuffer(text);
        StringUtility.deleteLineLeadingWhitespace(buffer);
        StringUtility.replace(buffer, "\\", "\\textbackslash");
        StringUtility.replace(buffer, "$", "\\$");
        StringUtility.replace(buffer, "&", "\\&");
        StringUtility.replace(buffer, "%", "\\%");
        StringUtility.replace(buffer, "#", "\\#");
        StringUtility.replace(buffer, "_", "\\_");
        StringUtility.replace(buffer, "{", "\\{");
        StringUtility.replace(buffer, "}", "\\}");
        StringUtility.replace(buffer, "~", "\\textasciitilde");
        StringUtility.replace(buffer, "^", "\\textasciicircum");
        StringUtility.replace(buffer, "<", "\\textless");
        StringUtility.replace(buffer, ">", "\\textgreater");
        return escapeUmlauts(buffer.toString());
    }

    /**
     * Get really LaTeX. Does some simple character replacements for umlauts.
     * LATER mime 20050205: filter more than German umlauts
     *
     * @param   nearlyLatex   Unescaped text.
     * @return  Really LaTeX.
     */
    private String escapeUmlauts(final String nearlyLatex) {
        if (nearlyLatex == null) {
            return "";
        }
        final StringBuffer buffer = new StringBuffer(nearlyLatex);
        StringUtility.deleteLineLeadingWhitespace(buffer);
        StringUtility.replace(buffer, "\u00fc", "{\\\"u}");
        StringUtility.replace(buffer, "\u00f6", "{\\\"o}");
        StringUtility.replace(buffer, "\u00e4", "{\\\"a}");
        StringUtility.replace(buffer, "\u00dc", "{\\\"U}");
        StringUtility.replace(buffer, "\u00d6", "{\\\"O}");
        StringUtility.replace(buffer, "\u00c4", "{\\\"A}");
        StringUtility.replace(buffer, "\u00df", "{\\ss}");
        return buffer.toString();
    }

}
