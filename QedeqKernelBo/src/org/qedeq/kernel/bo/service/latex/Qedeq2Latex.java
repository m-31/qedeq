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

package org.qedeq.kernel.bo.service.latex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.DateUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.unicode.UnicodeErrorCodes;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.AuthorList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.Chapter;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Header;
import org.qedeq.kernel.se.base.module.Import;
import org.qedeq.kernel.se.base.module.ImportList;
import org.qedeq.kernel.se.base.module.Latex;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.LinkList;
import org.qedeq.kernel.se.base.module.LiteratureItem;
import org.qedeq.kernel.se.base.module.LiteratureItemList;
import org.qedeq.kernel.se.base.module.LocationList;
import org.qedeq.kernel.se.base.module.ModusPonens;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proof;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.base.module.Rename;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.base.module.Section;
import org.qedeq.kernel.se.base.module.SectionList;
import org.qedeq.kernel.se.base.module.Specification;
import org.qedeq.kernel.se.base.module.Subsection;
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.base.module.UsedByList;
import org.qedeq.kernel.se.base.module.VariableList;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.visitor.QedeqNumbers;


/**
 * Transfer a QEDEQ module into a LaTeX file.
 * <p>
 * <b>This is just a quick written generator. No parsing or validation
 * of inline LaTeX text is done. This class just generates some LaTeX output to be able to
 * get a visual impression of a QEDEQ module.</b>
 *
 * @author  Michael Meyling
 */
public final class Qedeq2Latex extends ControlVisitor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = Qedeq2Latex.class;

// TODO m31 20100316: check number area for error codes
// FIXME m31 20100803: add JUnit tests for all error codes

    /** Output goes here. */
    private TextOutput printer;

    /** Filter text to get and produce text in this language. */
    private String language;

    /** Filter for this detail level. */
//    private String level;

    /** Should additional information be put into LaTeX output? E.g. QEDEQ reference names. */
    private final boolean info;

    /** Current chapter number, starting with 0. */
    private int chapterNumber;

    /** Current section number, starting with 0. */
    private int sectionNumber;

    /** Current node id. */
    private String id;

    /** Current node title. */
    private String title;

    /** Sub context like "getIntroduction()". */
    private String subContext = "";

    /** Remembered proof line reason. */
    private String reason;

    /** Alphabet for tagging. */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        QEDEQ BO object.
     * @param   parameters  Parameters.
     */
    public Qedeq2Latex(final Plugin plugin, final KernelQedeqBo prop, final Map parameters) {
        super(plugin, prop);
        String infoString = null;
        if (parameters != null) {
            infoString = (String) parameters.get("info");
        }
        info = "true".equalsIgnoreCase(infoString);
    }

    public Object executePlugin() {
        final String method = "executePlugin(QedeqBo, Map)";
        try {
            QedeqLog.getInstance().logRequest("Generate LaTeX from \""
                + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
            final String[] languages = getSupportedLanguages(getQedeqBo());
            for (int j = 0; j < languages.length; j++) {
                language = languages[j];
//                level = "1";
                final String result = generateLatex(languages[j], "1").toString();
                if (languages[j] != null) {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "LaTeX for language \"" + languages[j]
                        + "\" was generated from \""
                        + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\" into \"" + result + "\"");
                } else {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "LaTeX for default language "
                        + "was generated from \""
                        + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\" into \"" + result + "\"");
                }
            }
        } catch (final SourceFileExceptionList e) {
            final String msg = "Generation failed for \""
                + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (IOException e) {
            final String msg = "Generation failed for \""
                + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Generation failed", "unexpected problem: "
                + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }
        return null;
    }

    /**
     * Get an input stream for the LaTeX creation.
     *
     * @param   language    Filter text to get and produce text in this language only.
     * @param   level       Filter for this detail level. LATER mime 20050205: not supported
     *                      yet.
     * @return  Resulting LaTeX.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException File IO failed.
     */
    public InputStream createLatex(final String language, final String level)
            throws SourceFileExceptionList, IOException {
        return new FileInputStream(generateLatex(language, level));
    }

    // TODO m31 20070704: this should be part of QedeqBo
    String[] getSupportedLanguages(final QedeqBo prop) {
        // FIXME m31 20070704: there should be a better way to
        // get all supported languages. Time for a new visitor?
        if (!prop.isLoaded()) {
            return new String[]{};
        }
        final LatexList list = prop.getQedeq().getHeader().getTitle();
        final String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i).getLanguage();
        }
        return result;
    }

    /**
     * Gives a LaTeX representation of given QEDEQ module as InputStream.
     *
     * @param   language    Filter text to get and produce text in this language only.
     *                      <code>null</code> is ok.
     * @param   level       Filter for this detail level. LATER mime 20050205: not supported
     *                      yet.
     * @return  Resulting LaTeX.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException     File IO failed.
     */
    public File generateLatex(final String language, final String level)
            throws SourceFileExceptionList, IOException {
        final String method = "generateLatex(String, String)";
        this.language = language;
//        this.level = level;
        // first we try to get more information about required modules and their predicates..
        try {
            KernelContext.getInstance().loadRequiredModules(getQedeqBo().getModuleAddress());
            KernelContext.getInstance().checkModule(getQedeqBo().getModuleAddress());
        } catch (Exception e) {
            // we continue and ignore external predicates
            Trace.trace(CLASS, method, e);
        }
        String tex = getQedeqBo().getModuleAddress().getFileName();
        if (tex.toLowerCase(Locale.US).endsWith(".xml")) {
            tex = tex.substring(0, tex.length() - 4);
        }
        if (language != null && language.length() > 0) {
            tex = tex + "_" + language;
        }
        // the destination is the configured destination directory plus the (relative)
        // localized file (or path) name
        File destination = new File(KernelContext.getInstance().getConfig()
            .getGenerationDirectory(), tex + ".tex").getCanonicalFile();

        init();

        try {
            // FIXME 20110204 m31: here we should choose an encoding; default is ISO-8859-1 and that might not be ok
            printer = new TextOutput(getQedeqBo().getName(), new FileOutputStream(destination));
            traverse();
        } finally {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
            if (printer != null) {
                printer.flush();
                printer.close();
            }
        }
        if (printer != null && printer.checkError()) {
            throw printer.getError();
        }
        try {
            QedeqBoDuplicateLanguageChecker.check(getPlugin(), getQedeqBo());
        } catch (SourceFileExceptionList warnings) {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), null, warnings);
        }
        return destination.getCanonicalFile();
    }

    /**
     * Reset counters and other variables. Should be executed before {@link #traverse()}.
     */
    protected void init() {
        chapterNumber = 0;
        sectionNumber = 0;
        id = null;
        title = null;
        subContext = "";
    }

    public final void visitEnter(final Qedeq qedeq) {
        printer.println("% -*- TeX"
            + (language != null ? ":" + language.toUpperCase(Locale.US) : "") + " -*-");
        printer.println("%%% ====================================================================");
        printer.println("%%% @LaTeX-file    " + printer.getName());
        printer.println("%%% Generated from " + getQedeqBo().getModuleAddress());
        printer.println("%%% Generated at   " + DateUtility.getTimestamp());
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
// TODO m31 20100313: validate different counter types
//            printer.println("\\newtheorem{thm}{Theorem}[chapter]");
            printer.println("\\newtheorem{thm}{Theorem}");
            printer.println("\\newtheorem{cor}[thm]{Korollar}");
            printer.println("\\newtheorem{lem}[thm]{Lemma}");
            printer.println("\\newtheorem{prop}[thm]{Proposition}");
            printer.println("\\newtheorem{ax}{Axiom}");
            printer.println("\\newtheorem{rul}{Regel}");
            printer.println();
            printer.println("\\theoremstyle{definition}");
            printer.println("\\newtheorem{defn}{Definition}");
            printer.println("\\newtheorem{idefn}[defn]{Initiale Definition}");
            printer.println();
            printer.println("\\theoremstyle{remark}");
            printer.println("\\newtheorem{rem}[thm]{Bemerkung}");
            printer.println("\\newtheorem*{notation}{Notation}");
        } else {
            if (!"en".equals(language)) {
                printer.println("%%% TODO unknown language: " + language);
            }
// TODO m31 20100313: validate different counter types
//            printer.println("\\newtheorem{thm}{Theorem}[chapter]");
            printer.println("\\newtheorem{thm}{Theorem}");
            printer.println("\\newtheorem{cor}[thm]{Corollary}");
            printer.println("\\newtheorem{lem}[thm]{Lemma}");
            printer.println("\\newtheorem{prop}[thm]{Proposition}");
            printer.println("\\newtheorem{ax}{Axiom}");
            printer.println("\\newtheorem{rul}{Rule}");
            printer.println();
            printer.println("\\theoremstyle{definition}");
            printer.println("\\newtheorem{defn}{Definition}");
            printer.println("\\newtheorem{idefn}[defn]{Initial Definition}");
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
        printer.println("\\addcontentsline{toc}{chapter}{\\indexname} \\printindex");
        printer.println();
        printer.println("\\end{document}");
        printer.println();
    }

    public void visitEnter(final Header header) {
        final LatexList tit = header.getTitle();
        printer.print("\\title{");
        printer.print(getLatexListEntry("getTitle()", tit));
        printer.println("}");
        printer.println("\\author{");
        final AuthorList authors = getQedeqBo().getQedeq().getHeader().getAuthorList();
        final StringBuffer authorList = new StringBuffer();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                authorList.append(", ");
                printer.println(", ");
            }
            final Author author = authors.get(i);
            final String name = author.getName().getLatex().trim();
            printer.print(name);
            authorList.append(name);
            String email = author.getEmail();
            if (email != null && email.trim().length() > 0) {
                authorList.append(" \\href{mailto:" + email + "}{" + email + "}");
            }
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
        final String url = getQedeqBo().getUrl();
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
            final String emailUrl = "\\href{mailto:" + email + "}{" + email + "}";
            printer.println("\\par");
            if ("de".equals(language)) {
                printer.println("Bei Fragen, Anregungen oder Bitte um Aufnahme in die Liste der"
                    + " abh{\"a}ngigen Module schicken Sie bitte eine EMail an die Adresse "
                    + emailUrl);
                printer.println();
                printer.println("\\par");
                printer.println("Die Autoren dieses Dokuments sind:");
                printer.println(authorList);
            } else {
                if (!"en".equals(language)) {
                    printer.println("%%% TODO unknown language: " + language);
                }
                printer.println("If you have any questions, suggestions or want to add something"
                    + " to the list of modules that use this one, please send an email to the"
                    + " address " + emailUrl);
                printer.println();
                printer.println("\\par");
                printer.println("The authors of this document are:");
                printer.println(authorList);
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
            return address.getModulePaths(specification)[0].getUrl();
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
        printer.print(getLatexListEntry("getTitle()", chapter.getTitle()));
        final String label = "chapter" + chapterNumber;
        printer.println("} \\label{" + label + "} \\hypertarget{" + label + "}{}");
        if (chapter.getNoNumber() != null && chapter.getNoNumber().booleanValue()) {
            printer.println("\\addcontentsline{toc}{chapter}{"
                + getLatexListEntry("getTitle()", chapter.getTitle()) + "}");
        }
        printer.println();
        if (chapter.getIntroduction() != null) {
            printer.println(getLatexListEntry("getIntroduction()", chapter.getIntroduction()));
            printer.println();
        }
    }

    public void visitLeave(final Chapter chapter) {
        printer.println("%% end of chapter " + getLatexListEntry("getTitle()", chapter.getTitle()));
        printer.println();
        chapterNumber++;    // increase global chapter number
        sectionNumber = 0;  // reset section number
    }

    public void visitLeave(final SectionList list) {
        printer.println();
    }

    public void visitEnter(final Section section) {
        printer.print("\\section");
        if (section.getNoNumber() != null && section.getNoNumber().booleanValue()) {
            printer.print("*");
        }
        printer.print("{");
        printer.print(getLatexListEntry("getTitle()", section.getTitle()));
        final String label = "chapter" + chapterNumber + "_section" + sectionNumber;
        printer.println("} \\label{" + label + "} \\hypertarget{" + label + "}{}");
        printer.println(getLatexListEntry("getIntroduction()", section.getIntroduction()));
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
            printer.println(getLatexListEntry("getTitle()", subsection.getTitle()));
            printer.println("}");
        }
        if (subsection.getId() != null) {
            printer.println("\\label{" + subsection.getId() + "} \\hypertarget{"
                + subsection.getId() + "}{}");
        }
        printer.println(getLatexListEntry("getLatex()", subsection.getLatex()));
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
        printer.println(getLatexListEntry("getPrecedingText()", node.getPrecedingText()));
        printer.println();
        id = node.getId();
        title = null;
        if (node.getTitle() != null) {
            title = getLatexListEntry("getTitle()", node.getTitle());
        }
    }

    public void visitLeave(final Node node) {
        printer.println();
        printer.println(getLatexListEntry("getSucceedingText()", node.getSucceedingText()));
        printer.println();
        printer.println();
    }

    public void visitEnter(final Axiom axiom) {
        printer.println("\\begin{ax}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        if (info) {
            printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
        }
        printFormula(axiom.getFormula().getElement());
        printer.println(getLatexListEntry("getDescription()", axiom.getDescription()));
        printer.println("\\end{ax}");
    }

    public void visitEnter(final Proposition proposition) {
        printer.println("\\begin{prop}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        if (info) {
            printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
        }
        printTopFormula(proposition.getFormula().getElement(), id);
        printer.println(getLatexListEntry("getDescription()", proposition.getDescription()));
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
        printer.println(getLatexListEntry("getNonFormalProof()", proof.getNonFormalProof()));
        printer.println("\\end{proof}");
    }

    public void visitEnter(final FormalProof proof) {
        printer.println("\\begin{proof}");
//        if ("de".equals(language)) {
//            printer.println("Beweis (formal):");
//        } else {
//            printer.println("Proof (formal):");
//        }
    }

    public void visitEnter(final FormalProofLineList lines) {
        printer.println("\\mbox{}\\\\");
        printer.println("\\begin{longtable}[h!]{r@{\\extracolsep{\\fill}}p{9cm}@{\\extracolsep{\\fill}}p{4cm}}");
    }

    public void visitLeave(final FormalProofLineList lines) {
        printer.println(" & & \\qedhere");
        printer.println("\\end{longtable}");
    }

    public void visitEnter(final FormalProofLine line) {
        if (line.getLabel() != null) {
            String display = getNodeBo().getNodeVo().getId() + ":"
                                + line.getLabel();
            printer.print("\\label{" + display + "} \\hypertarget{" + display
                + "}{} \\mbox{\\emph{(" + line.getLabel() + ")}} ");
        }
        printer.print(" \\ &  \\ ");
        if (line.getFormula() != null) {
            printer.print("$");
            printer.print(getQedeqBo().getElement2Latex().getLatex(line.getFormula().getElement()));
            printer.print("$");
        }
        printer.print(" \\ &  \\ ");
        if (line.getReason() != null) {
            reason = line.getReason().toString();
        } else {
            reason = "";
        }
    }

    public void visitLeave(final FormalProofLine line) {
        if (reason.length() > 0) {
            printer.print("{\\tiny ");
//            printer.print("dummy");
            printer.print(reason);
            printer.print("}");
        }
        printer.println(" \\\\ ");
    }

    private String getReference(final String reference) {
        return getReference(reference, "getReference()");
    }

    private String getReference(final String reference, final String subContext) {
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            getCurrentContext().setLocationWithinModule(context + "." + subContext);
            return (getReferenceLink(reference));
        } finally {
            getCurrentContext().setLocationWithinModule(context);
        }
    }

    public void visitEnter(final ModusPonens r) throws ModuleDataException {
        reason = r.getName();
        boolean one = false;
        if (r.getReference1() != null) {
            reason += " " + getReference(r.getReference1(), "getReference1()");
            one = true;
        }
        if (r.getReference1() != null) {
            if (one) {
                reason += ",";
            }
            reason += " " + getReference(r.getReference2(), "getReference2()");
        }
    }

    public void visitEnter(final Add r) throws ModuleDataException {
        reason = r.getName();
        if (r.getReference() != null) {
            reason += " " + getReference(r.getReference());
        }
    }

    public void visitEnter(final Rename r) throws ModuleDataException {
        reason = r.getName();
        if (r.getOriginalSubjectVariable() != null) {
            reason += " $" + getQedeqBo().getElement2Latex().getLatex(
                r.getOriginalSubjectVariable()) + "$";
        }
        if (r.getReplacementSubjectVariable() != null) {
            reason += " by $" + getQedeqBo().getElement2Latex().getLatex(
                r.getReplacementSubjectVariable()) + "$";
        }
        if (r.getReference() != null) {
            reason += " in " + getReference(r.getReference());
        }
    }

    public void visitEnter(final SubstFree r) throws ModuleDataException {
        reason = r.getName();
        if (r.getSubjectVariable() != null) {
            reason += " $" + getQedeqBo().getElement2Latex().getLatex(
                r.getSubjectVariable()) + "$";
        }
        if (r.getSubstituteTerm() != null) {
            reason += " by $" + getQedeqBo().getElement2Latex().getLatex(
                r.getSubstituteTerm()) + "$";
        }
        if (r.getReference() != null) {
            reason += " in " + getReference(r.getReference());
        }
    }

    public void visitEnter(final SubstFunc r) throws ModuleDataException {
        reason = r.getName();
        if (r.getFunctionVariable() != null) {
            reason += " $" + getQedeqBo().getElement2Latex().getLatex(
                r.getFunctionVariable()) + "$";
        }
        if (r.getSubstituteTerm() != null) {
            reason += " by $" + getQedeqBo().getElement2Latex().getLatex(
                r.getSubstituteTerm()) + "$";
        }
        if (r.getReference() != null) {
            reason += " in " + getReference(r.getReference());
        }
    }

    public void visitEnter(final SubstPred r) throws ModuleDataException {
        reason = r.getName();
        if (r.getPredicateVariable() != null) {
            reason += " $" + getQedeqBo().getElement2Latex().getLatex(
                r.getPredicateVariable()) + "$";
        }
        if (r.getSubstituteFormula() != null) {
            reason += " by $" + getQedeqBo().getElement2Latex().getLatex(
                r.getSubstituteFormula()) + "$";
        }
        if (r.getReference() != null) {
            reason += " in " + getReference(r.getReference());
        }
    }

    public void visitEnter(final Existential r) throws ModuleDataException {
        reason = r.getName();
        if (r.getSubjectVariable() != null) {
            reason += " with $" + getQedeqBo().getElement2Latex().getLatex(
                r.getSubjectVariable()) + "$";
        }
        if (r.getReference() != null) {
            reason += " in " + getReference(r.getReference());
        }
    }

    public void visitEnter(final Universal r) throws ModuleDataException {
        reason = r.getName();
        if (r.getSubjectVariable() != null) {
            reason += " with $" + getQedeqBo().getElement2Latex().getLatex(
                r.getSubjectVariable()) + "$";
        }
        if (r.getReference() != null) {
            reason += " in " + getReference(r.getReference());
        }
    }

    public void visitLeave(final FormalProof proof) {
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
            if (info) {
                printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
            }
            define.append("\\ :\\leftrightarrow \\ ");
            define.append(getLatex(definition.getFormula().getElement()));
        } else {
            printer.println("\\begin{idefn}" + (title != null ? "[" + title + "]" : ""));
            printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
            if (info) {
                printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
            }
        }
        define.append("$$");
        Trace.param(CLASS, this, "printPredicateDefinition", "define", define);
        printer.println(define);
        printer.println(getLatexListEntry("getDescription()", definition.getDescription()));
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
            if (info) {
                printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
            }
            define.append("\\ := \\ ");
            define.append(getLatex(definition.getTerm().getElement()));
        } else {
            printer.println("\\begin{idefn}" + (title != null ? "[" + title + "]" : ""));
            printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
            if (info) {
                printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
            }
        }
        define.append("$$");
        Trace.param(CLASS, this, "printFunctionDefinition", "define", define);
        printer.println(define);
        printer.println(getLatexListEntry("getDescription()", definition.getDescription()));
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
        if (info) {
            printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
        }
        printer.println(getLatexListEntry("getDescription()", rule.getDescription()));
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
                printer.println(getLatexListEntry("getProofList().get(" + i + ")", rule.getProofList().get(i)
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
        printer.println("\\backmatter");
        printer.println();
        printer.println("\\begin{thebibliography}{99}");
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
                    // TODO m31 20100727: get other informations like authors, title, etc
                    // TODO m31 20100727: link to pdf?
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
        printer.println(getLatexListEntry("getItem()", item.getItem()));
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
        return getQedeqBo().getElement2Latex().getLatex(element);
    }

    /**
     * Filters correct entry out of LaTeX list. Filter criterion is for example the correct
     * language.
     * TODO mime 20050205: filter level too?
     *
     * @param   method  This method was called. Used to get the correct sub context.
     *                  Should not be null. If it is empty the <code>subContext</code>
     *                  is not changed.
     * @param   list    List of LaTeX texts.
     * @return  Filtered text.
     */
    private String getLatexListEntry(final String method, final LatexList list) {
        if (list == null) {
            return "";
        }
        if (method.length() > 0) {
            subContext = method;
        }
        try {
            for (int i = 0; language != null && i < list.size(); i++) {
                if (language.equals(list.get(i).getLanguage())) {
                    if (method.length() > 0) {
                        subContext = method + ".get(" + i + ")";
                    }
                    return getLatex(list.get(i));
                }
            }
            // assume entry with missing language as default
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getLanguage() == null) {
                    if (method.length() > 0) {
                        subContext = method + ".get(" + i + ")";
                    }
                    return getLatex(list.get(i));
                }
            }
            for (int i = 0; i < list.size(); i++) { // LATER mime 20050222: evaluate default?
                if (method.length() > 0) {
                    subContext = method + ".get(" + i + ")";
                }
                return "MISSING! OTHER: " + getLatex(list.get(i));
            }
            return "MISSING!";
        } finally {
            if (method.length() > 0) {
                subContext = "";
            }
        }
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
        StringBuffer result = new StringBuffer(latex.getLatex());

        // LATER mime 20080324: check if LaTeX is correct and no forbidden tags are used

        transformQref(result);

        return escapeUmlauts(result.toString());
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
        final StringBuffer buffer = new StringBuffer(result.toString());
        final TextInput input = new TextInput(buffer);
        int last = 0;
        try {
            result.setLength(0);
            while (input.forward("\\qref{")) {
                final SourcePosition startPosition = input.getSourcePosition();
                final int start = input.getPosition();
                if (!input.forward("}")) {
                    addWarning(LatexErrorCodes.QREF_END_NOT_FOUND_CODE,
                        LatexErrorCodes.QREF_END_NOT_FOUND_TEXT, startPosition,
                        input.getSourcePosition());
                    continue;
                }
                String ref = input.getSubstring(start + "\\qref{".length(), input.getPosition()).trim();
                input.read();   // read }
                Trace.param(CLASS, this, method, "1 ref", ref);
                if (ref.length() == 0) {
                    addWarning(LatexErrorCodes.QREF_EMPTY_CODE, LatexErrorCodes.QREF_EMPTY_TEXT,
                        startPosition, input.getSourcePosition());
                    continue;
                }
                if (ref.length() > 1024) {
                    addWarning(LatexErrorCodes.QREF_END_NOT_FOUND_CODE,
                        LatexErrorCodes.QREF_END_NOT_FOUND_TEXT, startPosition,
                        input.getSourcePosition());
                    continue;
                }
                if (ref.indexOf("{") >= 0) {
                    addWarning(LatexErrorCodes.QREF_END_NOT_FOUND_CODE,
                        LatexErrorCodes.QREF_END_NOT_FOUND_TEXT, startPosition,
                        input.getSourcePosition());
                    continue;
                }
                // exists a sub reference?
                String sub = "";
                if ('[' == input.getChar(0)) {
                    input.read();   // read [
                    int posb = input.getPosition();
                    if (!input.forward("]")) {
                        addWarning(LatexErrorCodes.QREF_SUB_END_NOT_FOUND_CODE,
                            LatexErrorCodes.QREF_SUB_END_NOT_FOUND_TEXT,
                            startPosition, input.getSourcePosition());;
                            continue;
                    }
                    sub = buffer.substring(posb, input.getPosition());
                    input.read();   // read ]
                }
                final int end = input.getPosition();
                final SourcePosition endPosition = input.getSourcePosition();
                result.append(buffer.substring(last, start));
                result.append(getReference(ref, sub, startPosition, endPosition));
                last = end;
            }
        } finally { // thanks to findbugs
            IoUtility.close(input);
            if (last < buffer.length()) {
                result.append(buffer.substring(last));
            }
        }
    }

    private String getReference(final String reference, final String sub,
            final SourcePosition start, final SourcePosition end) {
        final String method = "getReference(String, String)";
        Trace.param(CLASS, this, method, "2 reference", reference);     // qreference within module
        Trace.param(CLASS, this, method, "2 sub", sub);                 // sub reference (if any)

        KernelNodeBo node = getNodeBo();

        if (node != null && node.isLocalLabel(reference)) {
            return "\\hyperref[" + node.getNodeVo().getId() + ":" + reference + "]{" + "("
                + reference + ")" + (sub.length() > 0 ? " (" + sub + ")" : "")
                + "}";
        }

        if (getQedeqBo().getLabels().isNode(reference)) {
            return "\\hyperref[" + reference + "]{"
                + getNodeDisplay(getQedeqBo().getLabels().getNode(reference))
                + (sub.length() > 0 ? " (" + sub + ")" : "")
                + "}";
        }

        // do we have an external module reference without node?
        if (getQedeqBo().getLabels().isModule(reference)) {
            return "\\url{" + getPdfLink(getQedeqBo()) + "}~\\cite{" + reference + "}";
            // if we want to show the text "description": \href{my_url}{description}
        }

        final String subDisplay = (sub.length() > 0 ? " (" + sub + ")" : "");
        final String fallback = "{\tt " + reference + subDisplay + "}";

        final String[] split = StringUtility.split(reference, ".");
        if (split.length <= 1 || split.length > 3) {
            if (split.length <= 1) {
                addWarning(UnicodeErrorCodes.NODE_REFERENCE_NOT_FOUND_CODE,
                    UnicodeErrorCodes.NODE_REFERENCE_NOT_FOUND_TEXT
                    + "\"" + reference + "\"");
            }
            if (split.length > 3) {
                // FIXME 20110209 m31: this must be LatexErrorCodes, but then we have duplicate code!
                addWarning(UnicodeErrorCodes.NODE_REFERENCE_HAS_MORE_THAN_TWO_DOTS_CODE,
                    UnicodeErrorCodes.NODE_REFERENCE_HAS_MORE_THAN_TWO_DOTS_TEXT
                    + "\"" + reference + "\"");
            }
            return fallback;
        }
        String moduleLabel = split[0];    // module import
        String nodeLabel = split[1];      // module intern node reference
        String lineLabel = "";            // node specific
        if (split.length == 3) {
            lineLabel = " (" + split[2] + ")";
        }

        // is the import module perhaps a node?
        if (getQedeqBo().getLabels().isNode(moduleLabel)) {
            // then there must be no line reference already!
            if (split.length == 3) {
                // FIXME 20110209 m31: new error code for unclear reference
                addWarning(UnicodeErrorCodes.NODE_REFERENCE_HAS_MORE_THAN_TWO_DOTS_CODE,
                    UnicodeErrorCodes.NODE_REFERENCE_HAS_MORE_THAN_TWO_DOTS_TEXT
                    + "\"" + reference + "\"");
                return fallback;
            }
            lineLabel = nodeLabel;
            nodeLabel = moduleLabel;
            moduleLabel = "";
            node = getQedeqBo().getLabels().getNode(nodeLabel);
            final String display = getDisplay(moduleLabel, node, false, false);
            return "\\hyperref[" + nodeLabel + (lineLabel.length() > 0 ? "." + lineLabel : "")
                + "]{" + display
                + (sub.length() > 0 ? " (" + sub + ")" : "")
                + (lineLabel.length() > 0 ? " (" + lineLabel + ")" : "") + "}";
        } else {
            final KernelQedeqBo prop = getQedeqBo().getKernelRequiredModules()
                .getKernelQedeqBo(moduleLabel);
            if (prop == null) {
                // FIXME 20110209 m31: might not be correct error description (qref???)
                addWarning(LatexErrorCodes.QREF_PARSING_EXCEPTION_CODE,
                    LatexErrorCodes.QREF_PARSING_EXCEPTION_TEXT
                    + ": " + "module not found for " + moduleLabel, start, end);
                return fallback;
            }
            node = prop.getLabels().getNode(nodeLabel);
            if (node == null) {
                // FIXME 20110209 m31: might not be correct error description (qref???)
                addWarning(LatexErrorCodes.QREF_PARSING_EXCEPTION_CODE, LatexErrorCodes.QREF_PARSING_EXCEPTION_TEXT
                        + ": " + "node not found for " + nodeLabel, start, end);
                return fallback;
            }
            final String display = getDisplay(moduleLabel, node, false, true);
            return "\\hyperref{" + getPdfLink(prop) + "}{}{"
                + moduleLabel + (sub.length() > 0 ? ":" + sub : "")
                + (lineLabel.length() > 0 ? ":" + lineLabel : "")
                + "}{" + display + (sub.length() > 0 ? " (" + sub + ")" : "")
                + (lineLabel.length() > 0 ? " (" + lineLabel + ")" : "")
                + "}~\\cite{" + moduleLabel + "}";
        }


//
//        // do we have an external module?
//        if (label.length() <= 0) {      // local reference
//            final String display = getDisplay(ref, node, false, false);
////                    result.replace(pos1, pos2 + 1, display + "~\\autoref{" + ref + "}"
//                return "\\hyperref[" + ref + "]{" + display + "~\\ref*{"
//                    + ref + "}}"
//                    + (sub.length() > 0 ? " (" + sub + ")" : "");
//        } else {                        // external reference
//            if (ref.length() <= 0) {
//                // we have an external module reference without node
//                return "\\url{" + getPdfLink(prop) + "}~\\cite{" + label + "}";
//                // if we want to show the text "description": \href{my_url}{description}
//            } else {
//                // we have an external module reference with node
//                final String display = getDisplay(ref, node, false, true);
//                return "\\hyperref{" + getPdfLink(prop) + "}{}{"
//                    + ref + (sub.length() > 0 ? ":" + sub : "")
//                    + "}{" + display + (sub.length() > 0 ? " (" + sub + ")" : "") + "}~\\cite{"
//                    + label + "}";
//            }
//        }
    }

//    public String getReferenceLink(final String reference, final String subReference,
//            final SourcePosition startPosition, final SourcePosition endPosition) {
//        final String method = "getReferenceLink(String, String, SourcePosition, SourcePosition)";
//
//        // get module label (if any)
//        String moduleLabel = "";
//        String localLabel = reference;
//        int dot = reference.indexOf(".");
//        if (dot >= 0) {
//            moduleLabel = reference.substring(0, dot);
//            localLabel = reference.substring(dot + 1);
//        }
//        String fix = "";
//        if (subReference != null && subReference.length() > 0) {
//            fix += " (" + subReference + ")";
//        }
//        KernelQedeqBo module = getQedeqBo();
//        if (moduleLabel.length() == 0) {
//            // check if local reference is in fact a module label
//            final KernelQedeqBo refModule = module.getKernelRequiredModules()
//                .getKernelQedeqBo(localLabel);
//            if (refModule != null) {
//                moduleLabel = localLabel;
//                localLabel = "";
//                module = refModule;
//                return "[" + moduleLabel + "]";
//            }
//        } else {
//            final KernelQedeqBo refModule = module.getKernelRequiredModules()
//                .getKernelQedeqBo(moduleLabel);
//            if (refModule == null) {
//                module = refModule;
//                Trace.info(CLASS, this, method, "module not found for " + moduleLabel);
//                addWarning(LatexErrorCodes.QREF_PARSING_EXCEPTION_CODE,
//                    LatexErrorCodes.QREF_PARSING_EXCEPTION_TEXT
//                    + ": " + "module not found for " + reference, startPosition,
//                    endPosition);
//                return moduleLabel + "?." + localLabel + fix;
//            }
//            module = refModule;
//        }
//        if (moduleLabel.length() > 0) {
//            fix += " [" + moduleLabel + "]";
//        }
//        final KernelNodeBo kNode = module.getLabels().getNode(localLabel);
//        if (kNode != null) {
//            return getNodeDisplay(kNode) + fix;
//        } else {
//            Trace.info(CLASS, this, method, "node not found for " + reference);
//            addWarning(LatexErrorCodes.QREF_PARSING_EXCEPTION_CODE,
//                LatexErrorCodes.QREF_PARSING_EXCEPTION_TEXT
//                + ": " + "node not found for " + reference, startPosition,
//                endPosition);
//            return localLabel + "?" + fix;
//        }
//    }

    public String getReferenceLink(final String reference) {
        return getReference(reference, "", null, null);
//        final String method = "getReferenceLink(String)";
//        final KernelNodeBo node = getNodeBo();
//
//        final String fallback = "[" + reference + "]";
//        if (node == null) {
//            addWarning(UnicodeErrorCodes.NODE_REFERENCE_NOT_FOUND_CODE,
//                    UnicodeErrorCodes.NODE_REFERENCE_NOT_FOUND_TEXT
//                    + "\"" + reference + "\"");
//            final String msg = "Programming error: this method should only be called when parsing a node";
//            Trace.fatal(CLASS, method, msg, new RuntimeException(msg));
//            return fallback;
//        }
//        if (node.isLocalLabel(reference)) {
//            return "(" + reference + ")";
//        }
//        if (getQedeqBo().getLabels().isNode(reference)) {
////            return getNodeDisplay(node);
//        }
//        String[] split = StringUtility.split(reference, ".");
//        if (split.length <= 1 || split.length > 3) {
//            if (split.length <= 1) {
//                addWarning(UnicodeErrorCodes.NODE_REFERENCE_NOT_FOUND_CODE,
//                    UnicodeErrorCodes.NODE_REFERENCE_NOT_FOUND_TEXT
//                    + "\"" + reference + "\"");
//            }
//            if (split.length > 3) {
//                addWarning(UnicodeErrorCodes.NODE_REFERENCE_HAS_MORE_THAN_TWO_DOTS_CODE,
//                    UnicodeErrorCodes.NODE_REFERENCE_HAS_MORE_THAN_TWO_DOTS_TEXT
//                    + "\"" + reference + "\"");
//            }
//            return fallback;
//        }
//        final String moduleLabel = split[0];
//        final String nodeLabel = split[1];
//        String lineLabel = "";
//        if (split.length == 3) {
//            lineLabel = " (" + split[2] + ")";
//        }
//        final KernelQedeqBo refModule = getQedeqBo().getKernelRequiredModules()
//            .getKernelQedeqBo(moduleLabel);
//        if (refModule == null) {
//            addWarning(UnicodeErrorCodes.MODULE_REFERENCE_NOT_FOUND_CODE,
//                    UnicodeErrorCodes.MODULE_REFERENCE_NOT_FOUND_TEXT
//                    + "\"" + reference + "\"");
//            return moduleLabel + "?." + nodeLabel + lineLabel;
//        }
//        lineLabel += " [" + moduleLabel + "]";
//        final KernelNodeBo kNode = refModule.getLabels().getNode(nodeLabel);
//        if (kNode != null) {
//            return getNodeDisplay(kNode) + lineLabel;
//        } else {
//            addWarning(UnicodeErrorCodes.NODE_REFERENCE_NOT_FOUND_CODE,
//                UnicodeErrorCodes.NODE_REFERENCE_NOT_FOUND_TEXT
//                + "\"" + reference + "\"");
//            Trace.info(CLASS, this, method, "node not found for " + reference);
//            return nodeLabel + "?" + lineLabel;
//        }
    }

    private String getNodeDisplay(final KernelNodeBo kNode) {
        String display = "";
        QedeqNumbers data = kNode.getNumbers();
        Node node = kNode.getNodeVo();
        if (node.getNodeType() instanceof Axiom) {
            if ("de".equals(language)) {
                display = "Axiom";
            } else {
                display = "axiom";
            }
            display += " " + data.getAxiomNumber();
        } else if (node.getNodeType() instanceof Proposition) {
            if ("de".equals(language)) {
                display = "Proposition";
            } else {
                display = "proposition";
            }
            display += " " + data.getPropositionNumber();
        } else if (node.getNodeType() instanceof FunctionDefinition) {
            if ("de".equals(language)) {
                display = "Definition";
            } else {
                display = "definition";
            }
            display += " " + (data.getPredicateDefinitionNumber() + data.getFunctionDefinitionNumber());
        } else if (node.getNodeType() instanceof PredicateDefinition) {
            if ("de".equals(language)) {
                display = "Definition";
            } else {
                display = "definition";
            }
            display += " " + (data.getPredicateDefinitionNumber() + data.getFunctionDefinitionNumber());
        } else if (node.getNodeType() instanceof Rule) {
            if ("de".equals(language)) {
                display = "Regel";
            } else {
                display = "rule";
            }
            display += " " + data.getRuleNumber();
        } else {
            if ("de".equals(language)) {
                display = "Unbekannt " + node.getId();
            } else {
                display = "unknown " + node.getId();
            }
        }
        return display;
    }

    /**
     * Get current module context. Uses sub context information.
     *
     * @param   startDelta  Skip position (relative to location start). Could be
     *                      <code>null</code>.
     * @param   endDelta    Mark until this column (relative to location start).
     *                      be <code>null</code>
     * @return  Current module context.
     */
    public ModuleContext getCurrentContext(final SourcePosition startDelta,
            final SourcePosition endDelta) {
        if (subContext.length() == 0) {
            return super.getCurrentContext();
        }
        final ModuleContext c = new ModuleContext(super.getCurrentContext().getModuleLocation(),
            super.getCurrentContext().getLocationWithinModule() + "." + subContext,
            startDelta, endDelta);
        return c;
    }

//    public ModuleContext getCurrentContext() {
//        throw new IllegalStateException("programming error");
//    }


    /**
     * Add warning.
     *
     * @param   code        Warning code.
     * @param   msg         Warning message.
     * @param   startDelta  Skip position (relative to location start). Could be
     *                      <code>null</code>.
     * @param   endDelta    Mark until this column (relative to location start).
     *                      be <code>null</code>
     */
    private void addWarning(final int code, final String msg, final SourcePosition startDelta,
            final SourcePosition endDelta) {
        Trace.param(CLASS, this, "addWarning", "msg", msg);
        addWarning(new LatexContentException(code, msg, getCurrentContext(startDelta, endDelta)));
    }

    /**
     * Add warning.
     *
     * @param   code        Warning code.
     * @param   msg         Warning message.
     *                      be <code>null</code>
     */
    private void addWarning(final int code, final String msg) {
        Trace.param(CLASS, this, "addWarning", "msg", msg);
        addWarning(new LatexContentException(code, msg, getCurrentContext(null, null)));
    }

    /**
     * Get text to display for a link.
     * LATER m31 20100308: refactor language dependent code
     *
     * @param   ref         Reference label.
     * @param   nodeBo      Reference to this node. Might be <code>null</code>.
     * @param   useName     Use name if it exists.
     * @param   external    Is this an external node?
     * @return  Display     text.
     */
    private String getDisplay(final String ref, final KernelNodeBo nodeBo, final boolean useName,
            final boolean external) {
        String display = ref;
        if (nodeBo != null) {
            Node node = nodeBo.getNodeVo();
            QedeqNumbers data = nodeBo.getNumbers();
            if (useName && node.getName() != null) {
                display = getLatexListEntry("", node.getName());
            } else {
                if (node.getNodeType() instanceof Axiom) {
                    if ("de".equals(language)) {
                        display = "Axiom";
                    } else {
                        display = "axiom";
                    }
                    if (external) {
                        display += " " + data.getAxiomNumber();
                    }
                } else if (node.getNodeType() instanceof Proposition) {
                    if ("de".equals(language)) {
                        display = "Proposition";
                    } else {
                        display = "proposition";
                    }
                    if (external) {
                        display += " " + data.getPropositionNumber();
                    }
                } else if (node.getNodeType() instanceof FunctionDefinition) {
                    if ("de".equals(language)) {
                        display = "Definition";
                    } else {
                        display = "definition";
                    }
                    if (external) {
                        display += " " + (data.getPredicateDefinitionNumber() + data.getFunctionDefinitionNumber());
                    }
                } else if (node.getNodeType() instanceof PredicateDefinition) {
                    if ("de".equals(language)) {
                        display = "Definition";
                    } else {
                        display = "definition";
                    }
                    if (external) {
                        display += " " + (data.getPredicateDefinitionNumber() + data.getFunctionDefinitionNumber());
                    }
                } else if (node.getNodeType() instanceof Rule) {
                    if ("de".equals(language)) {
                        display = "Regel";
                    } else {
                        display = "rule";
                    }
                    if (external) {
                        display += " " + data.getRuleNumber();
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
     * FIXME 20101222 m31: this doesn't work if module or referenced module
     *  languages don't match. E.g. if the referenced module has only the
     *  "en" or default (language=null) language and the current is written
     *  with language "de". Each KernelQedeqBo must have the language information
     *  as method. Every module must have at least one explicit language (!= null)
     *  and a default language (!= null). So we can ask a QEDEQ module: String getDefaultLanguage
     *  String[] getSupportedLanguages()
     *
     * @param   prop    Get URL for this QEDEQ module.
     * @return  URL to PDF.
     */
    private String getPdfLink(final KernelQedeqBo prop) {
        final String url = prop.getUrl();
        final int dot = url.lastIndexOf(".");
        return url.substring(0, dot) + (language != null ? "_" + language : "") + ".pdf";
    }

    /**
     * Get LaTeX from string. Escapes common characters.
     *
     * @param   text   Unescaped text.
     * @return  LaTeX.
     */
    private String getLatex(final String text) {
        final StringBuffer buffer = new StringBuffer(text);
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
     * Also gets rid of leading spaces if they are equal among the lines.
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
//        System.out.println("before");
//        System.out.println(buffer);
//        System.out.println();
//        System.out.println("after");
        StringUtility.deleteLineLeadingWhitespace(buffer);
//        System.out.println(buffer);
//        System.out.println("*******************************************************************");
        StringUtility.replace(buffer, "\u00fc", "{\\\"u}");
        StringUtility.replace(buffer, "\u00f6", "{\\\"o}");
        StringUtility.replace(buffer, "\u00e4", "{\\\"a}");
        StringUtility.replace(buffer, "\u00dc", "{\\\"U}");
        StringUtility.replace(buffer, "\u00d6", "{\\\"O}");
        StringUtility.replace(buffer, "\u00c4", "{\\\"A}");
        StringUtility.replace(buffer, "\u00df", "{\\ss}");
        return buffer.toString().trim();
    }

}
