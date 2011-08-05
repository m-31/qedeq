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

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.DateUtility;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.Reference;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.AuthorList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.ChangedRule;
import org.qedeq.kernel.se.base.module.ChangedRuleList;
import org.qedeq.kernel.se.base.module.Chapter;
import org.qedeq.kernel.se.base.module.Conclusion;
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Header;
import org.qedeq.kernel.se.base.module.Hypothesis;
import org.qedeq.kernel.se.base.module.Import;
import org.qedeq.kernel.se.base.module.ImportList;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
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
import org.qedeq.kernel.se.base.module.SubsectionList;
import org.qedeq.kernel.se.base.module.SubsectionType;
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.base.module.UsedByList;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.RuleKey;
import org.qedeq.kernel.se.common.SourceFileExceptionList;


/**
 * Transfer a QEDEQ module into a LaTeX file.
 * <p>
 * <b>This is just a quick written generator. No parsing or validation
 * of inline LaTeX text is done. This class just generates some LaTeX output to be able to
 * get a visual impression of a QEDEQ module.</b>
 *
 * @author  Michael Meyling
 */
public final class Qedeq2LatexExecutor extends ControlVisitor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = Qedeq2LatexExecutor.class;

// TODO m31 20100316: check number area for error codes
// TODO m31 20100803: add JUnit tests for all error codes

    /** Output goes here. */
    private TextOutput printer;

    /** Filter text to get and produce text in this language. */
    private String language;

    /** Filter for this detail level. */
//    private String level;

    /** Should additional information be put into LaTeX output? E.g. QEDEQ reference names. */
    private final boolean info;

    /** Should only names and formulas be be printed? */
    private final boolean brief;

    /** Current node id. */
    private String id;

    /** Current node title. */
    private String title;

    /** Sub context like "getIntroduction()". */
    private String subContext = "";

    /** Remembered proof line label. */
    private String label = "";

    /** Remembered proof line formula. */
    private String formula = "";

    /** Remembered proof line reason. */
    private String reason = "";

    /** Remembered proof line tabulator level. */
    private int tabLevel = 0;

    /** Alphabet for tagging. */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        QEDEQ BO object.
     * @param   parameters  Parameters.
     */
    public Qedeq2LatexExecutor(final Plugin plugin, final KernelQedeqBo prop, final Parameters parameters) {
        super(plugin, prop);
        info = parameters.getBoolean("info");
        brief = parameters.getBoolean("brief");
    }

    public Object executePlugin() {
        final String method = "executePlugin(QedeqBo, Map)";
        try {
            QedeqLog.getInstance().logRequest("Generate LaTeX", getQedeqBo().getUrl());
            final String[] languages = getQedeqBo().getSupportedLanguages();
            for (int j = 0; j < languages.length; j++) {
                language = languages[j];
//                level = "1";
                final String result = generateLatex(languages[j], "1").toString();
                if (languages[j] != null) {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "LaTeX for language \"" + languages[j]
                        + "\" was generated from into \"" + result + "\"", getQedeqBo().getUrl());
                } else {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "LaTeX for default language "
                        + "was generated into \"" + result + "\"", getQedeqBo().getUrl());
                }
            }
        } catch (final SourceFileExceptionList e) {
            final String msg = "Generation failed";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(), e.getMessage());
        } catch (IOException e) {
            final String msg = "Generation failed";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(), e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Generation failed", getQedeqBo().getUrl(), "unexpected problem: "
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
            getServices().loadRequiredModules(getQedeqBo().getModuleAddress());
            getServices().checkModule(getQedeqBo().getModuleAddress());
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
        File destination = new File(getServices().getConfig()
            .getGenerationDirectory(), tex + ".tex").getCanonicalFile();

        init();

        try {
            // TODO 20110204 m31: here we should choose the correct encoding; perhaps GUI configurable?
            if ("de".equals(language)) {
                printer = new TextOutput(getQedeqBo().getName(), new FileOutputStream(destination),
                    "ISO-8859-1") {
                    public void append(final String txt) {
                        super.append(escapeUmlauts(txt));
                    }
                };
            } else {
                printer = new TextOutput(getQedeqBo().getName(), new FileOutputStream(destination),
                    "UTF-8") {
                    public void append(final String txt) {
                        super.append(escapeUmlauts(txt));
                    }
                };
            }
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
        printer.println("\\usepackage{xr}");
        printer.println("\\usepackage{tabularx}");
//        printer.println("\\usepackage{epsfig,longtable}");
//        printer.println("\\usepackage{ltabptch}");    // not installed on our server
        printer.println("\\usepackage[bookmarks=true,bookmarksnumbered,bookmarksopen,");
        printer.println("   unicode=true,colorlinks=true,linkcolor=webgreen,");
        printer.println("   pagebackref=true,pdfnewwindow=true,pdfstartview=FitH]{hyperref}");
        printer.println("\\definecolor{webgreen}{rgb}{0,.5,0}");
        printer.println("\\usepackage{epsfig,longtable}");
        printer.println("\\usepackage{graphicx}");
        printer.println("\\usepackage[all]{hypcap}");
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
        // check if we print only brief and test for non text subnodes
        if (brief) {
            boolean hasFormalContent = false;
            do {
                final SectionList sections = chapter.getSectionList();
                if (sections == null) {
                    break;
                }
                for (int i = 0; i < sections.size() && !hasFormalContent; i++) {
                    final Section section = sections.get(i);
                    if (section == null) {
                        continue;
                    }
                    final SubsectionList subSections = section.getSubsectionList();
                    if (subSections == null) {
                        continue;
                    }
                    for (int j = 0; j < subSections.size(); j++) {
                        final SubsectionType subSection = subSections.get(j);
                        if (!(subSection instanceof Subsection)) {
                            hasFormalContent = true;
                            break;
                        }
                    }
                }
            } while (false);
            if (!hasFormalContent) {
                setBlocked(true);
                return;
            }
        }
        printer.print("\\chapter");
        if (chapter.getNoNumber() != null && chapter.getNoNumber().booleanValue()) {
            printer.print("*");
        }
        printer.print("{");
        printer.print(getLatexListEntry("getTitle()", chapter.getTitle()));
        final String chapterLabel = "chapter" + getCurrentNumbers().getAbsoluteChapterNumber();
        printer.println("} \\label{" + chapterLabel + "} \\hypertarget{" + chapterLabel + "}{}");
        if (chapter.getNoNumber() != null && chapter.getNoNumber().booleanValue()) {
            printer.println("\\addcontentsline{toc}{chapter}{"
                + getLatexListEntry("getTitle()", chapter.getTitle()) + "}");
        }
        printer.println();
        if (chapter.getIntroduction() != null && !brief) {
            printer.println(getLatexListEntry("getIntroduction()", chapter.getIntroduction()));
            printer.println();
        }
    }

    public void visitLeave(final Chapter chapter) {
        printer.println("%% end of chapter " + getLatexListEntry("getTitle()", chapter.getTitle()));
        printer.println();
        setBlocked(false);
    }

    public void visitLeave(final SectionList list) {
        printer.println();
    }

    public void visitEnter(final Section section) {
        // check if we print only brief and test for non text subnodes
        if (brief) {
            boolean hasFormalContent = false;
            do {
                final SubsectionList subSections = section.getSubsectionList();
                if (subSections == null) {
                    break;
                }
                for (int j = 0; j < subSections.size(); j++) {
                    final SubsectionType subSection = subSections.get(j);
                    if (!(subSection instanceof Subsection)) {
                        hasFormalContent = true;
                        break;
                    }
                }
            } while (false);
            if (!hasFormalContent) {
                setBlocked(true);
                return;
            }
        }
        printer.print("\\section");
        if (section.getNoNumber() != null && section.getNoNumber().booleanValue()) {
            printer.print("*");
        }
        printer.print("{");
        printer.print(getLatexListEntry("getTitle()", section.getTitle()));
        final String chapterLabel = "chapter" + getCurrentNumbers().getAbsoluteChapterNumber()
            + "_section" + getCurrentNumbers().getAbsoluteSectionNumber();
        printer.println("} \\label{" + chapterLabel + "} \\hypertarget{" + chapterLabel + "}{}");
        if (section.getIntroduction() != null && !brief) {
            printer.println(getLatexListEntry("getIntroduction()", section.getIntroduction()));
            printer.println();
        }
    }

    public void visitLeave(final Section section) {
        setBlocked(false);
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
        if (brief) {
            return;
        }
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
        if (brief) {
            return;
        }
        printer.println();
        printer.println();
    }

    public void visitEnter(final Node node) {
/** TODO mime 20070131: level filter
        if (node.getLevel() != null) {
            printer.print(" level=\"" + node.getLevel() + "\"");
        }
*/
        if (node.getPrecedingText() != null && !brief) {
            printer.println("\\par");
            printer.println(getLatexListEntry("getPrecedingText()", node.getPrecedingText()));
            printer.println();
        }
        id = node.getId();
        title = null;
        if (node.getTitle() != null) {
            title = getLatexListEntry("getTitle()", node.getTitle());
        }
    }

    public void visitLeave(final Node node) {
        printer.println();
        if (node.getSucceedingText() != null && !brief) {
            printer.println(getLatexListEntry("getSucceedingText()", node.getSucceedingText()));
            printer.println();
        }
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
        if (brief) {
            setBlocked(true);
            return;
        }
        printer.println("\\begin{proof}");
        printer.println(getLatexListEntry("getNonFormalProof()", proof.getNonFormalProof()));
        printer.println("\\end{proof}");
    }

    public void visitLeave(final Proof proof) {
        setBlocked(false);
    }

    public void visitEnter(final FormalProof proof) {
        if (brief) {
            setBlocked(true);
            return;
        }
        tabLevel = 0;
        printer.println("\\begin{proof}");
//        if ("de".equals(language)) {
//            printer.println("Beweis (formal):");
//        } else {
//            printer.println("Proof (formal):");
//        }
    }

    public void visitEnter(final FormalProofLineList lines) {
        if (tabLevel == 0) {
            printer.println("\\mbox{}\\\\");
            printer.println("\\begin{longtable}[h!]{r@{\\extracolsep{\\fill}}p{9cm}@{\\extracolsep{\\fill}}p{4cm}}");
        }
    }

    public void visitLeave(final FormalProofLineList lines) {
        if (tabLevel == 0) {
            printer.println(" & & \\qedhere");
            printer.println("\\end{longtable}");
        }
    }

    public void visitEnter(final FormalProofLine line) {
        if (line.getLabel() != null) {
            label = line.getLabel();
        } else {
            label = "";
        }
        if (line.getFormula() != null) {
            formula = "$" + getQedeqBo().getElement2Latex().getLatex(line.getFormula().getElement()) + "$";
        } else {
            formula = "";
        }
        if (line.getReason() != null) {
            reason = line.getReason().toString();
        } else {
            reason = "";
        }
    }

    public void visitLeave(final FormalProofLine line) {
        if (brief) {
            return;
        }
        linePrintln();
    }

    /**
     * Print proof line made out of label, formula and reason.
     */
    private void linePrintln() {
        if (formula.length() == 0 && reason.length() == 0) {
            return;
        }
        if (label.length() > 0) {
            String display = getNodeBo().getNodeVo().getId() + "!" + label;
            printer.print("\\label{" + display + "} \\hypertarget{" + display
                + "}{\\mbox{(" + label + ")}} ");
        }
        printer.print(" \\ &  \\ ");
        for (int i = 0; i < tabLevel; i++) {
            printer.print("\\mbox{\\qquad}");
        }
        if (formula.length() > 0) {
            printer.print(formula);
        }
        printer.print(" \\ &  \\ ");
        if (reason.length() > 0) {
            printer.print("{\\tiny ");
            printer.print(reason);
            printer.print("}");
        }
        printer.println(" \\\\ ");
        reason = "";
        formula = "";
        label = "";
    }

    private String getReference(final String reference) {
        return getReference(reference, "getReference()");
    }

    private String getReference(final String reference, final String subContext) {
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            getCurrentContext().setLocationWithinModule(context + "." + subContext);
            return (getReference(reference, null, null));
        } finally {
            getCurrentContext().setLocationWithinModule(context);
        }
    }

    public void visitEnter(final ModusPonens r) throws ModuleDataException {
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
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
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
        if (r.getReference() != null) {
            reason += " " + getReference(r.getReference());
        }
    }

    public void visitEnter(final Rename r) throws ModuleDataException {
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
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
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
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
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
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
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
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
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
        if (r.getSubjectVariable() != null) {
            reason += " with $" + getQedeqBo().getElement2Latex().getLatex(
                r.getSubjectVariable()) + "$";
        }
        if (r.getReference() != null) {
            reason += " in " + getReference(r.getReference());
        }
    }

    public void visitEnter(final Universal r) throws ModuleDataException {
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
        if (r.getSubjectVariable() != null) {
            reason += " with $" + getQedeqBo().getElement2Latex().getLatex(
                r.getSubjectVariable()) + "$";
        }
        if (r.getReference() != null) {
            reason += " in " + getReference(r.getReference());
        }
    }

    public void visitEnter(final ConditionalProof r) throws ModuleDataException {
        if (brief) {
            return;
        }
        reason = getRuleReference(r.getName());
        printer.print(" \\ &  \\ ");
        for (int i = 0; i < tabLevel; i++) {
            printer.print("\\mbox{\\qquad}");
        }
        printer.println("Conditional Proof");
        printer.print(" \\ &  \\ ");
        printer.println(" \\\\ ");
        tabLevel++;
    }

    public void visitLeave(final ConditionalProof proof) {
        if (brief) {
            return;
        }
        tabLevel--;
    }

    public void visitEnter(final Hypothesis hypothesis) {
        if (brief) {
            return;
        }
        reason = "Hypothesis";
        if (hypothesis.getLabel() != null) {
            label = hypothesis.getLabel();
        }
        if (hypothesis.getFormula() != null) {
            formula = "$" + getQedeqBo().getElement2Latex().getLatex(
                hypothesis.getFormula().getElement()) + "$";
        }
    }

    public void visitLeave(final Hypothesis hypothesis) {
        if (brief) {
            return;
        }
        linePrintln();
    }

    public void visitEnter(final Conclusion conclusion) {
        if (brief) {
            return;
        }
        tabLevel--;
        reason = "Conclusion";
        if (conclusion.getLabel() != null) {
            label = conclusion.getLabel();
        }
        if (conclusion.getFormula() != null) {
            formula = "$" + getQedeqBo().getElement2Latex().getLatex(
                    conclusion.getFormula().getElement()) + "$";
        }
    }

    public void visitLeave(final Conclusion conclusion) {
        if (brief) {
            return;
        }
        linePrintln();
        tabLevel++;
    }

    public void visitLeave(final FormalProof proof) {
        if (!getBlocked()) {
            printer.println("\\end{proof}");
        }
        setBlocked(false);
    }

    public void visitEnter(final InitialPredicateDefinition definition) {
        printer.println("\\begin{idefn}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        if (info) {
            printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
        }
        printer.print("$$");
        printer.println(getLatex(definition.getPredCon()));
        printer.println("$$");
        printer.println(getLatexListEntry("getDescription()", definition.getDescription()));
        printer.println("\\end{idefn}");
    }

    public void visitEnter(final PredicateDefinition definition) {
        printer.println("\\begin{defn}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        if (info) {
            printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
        }
        printer.print("$$");
        printer.print(getLatex(definition.getFormula().getElement()));
        printer.println("$$");
        printer.println(getLatexListEntry("getDescription()", definition.getDescription()));
        printer.println("\\end{defn}");
    }

    public void visitEnter(final InitialFunctionDefinition definition) {
        printer.println("\\begin{idefn}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        if (info) {
            printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
        }
        printer.print("$$");
        printer.print(getLatex(definition.getFunCon()));
        printer.println("$$");
        printer.println(getLatexListEntry("getDescription()", definition.getDescription()));
        printer.println("\\end{defn}");
    }

    public void visitEnter(final FunctionDefinition definition) {
        printer.println("\\begin{defn}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        if (info) {
            printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
        }
        printer.print("$$");
        printer.print(getLatex(definition.getFormula().getElement()));
        printer.println("$$");
        printer.println("\\end{defn}");
    }

    public void visitLeave(final FunctionDefinition definition) {
    }

    public void visitEnter(final Rule rule) {
        printer.println("\\begin{rul}" + (title != null ? "[" + title + "]" : ""));
        printer.println("\\label{" + id + "} \\hypertarget{" + id + "}{}");
        if (info) {
            printer.println("{\\tt \\tiny [\\verb]" + id + "]]}");
        }
        printer.println();
        printer.println("\\par");
        printer.println("{\\em "
            + (rule.getName() != null ? "  Name: \\verb]" + rule.getName() + "]" : "")
            + (rule.getVersion() != null ? "  -  Version: \\verb]" + rule.getVersion() + "]" : "")
            + "}");
        printer.println();
        printer.println();
        printer.println(getLatexListEntry("getDescription()", rule.getDescription()));
        printer.println("\\end{rul}");
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
                printer.print(" " + getReference(linkList.get(i), "get(" + i + ")"));
            }
        };
        printer.println();
    }

    public void visitEnter(final ChangedRuleList list) {
        if (list.size() <= 0) {
            return;
        }
        if ("de".equals(language)) {
            printer.println("Die folgenden Regeln m\00FCssen erweitert werden.");
        } else {
            if (!"en".equals(language)) {
                printer.println("%%% TODO unknown language: " + language);
            }
            printer.println("The following rules have to be extended.");
        }
        printer.println();
    }

    public void visitEnter(final ChangedRule rule) {
        printer.println("\\par");
        printer.println("\\label{" + id + "!" + rule.getName() + "} \\hypertarget{" + id + "!"
                + rule.getName() + "}{}");
        printer.print("{\\em "
            + (rule.getName() != null ? "  Name: \\verb]" + rule.getName() + "]" : "")
            + (rule.getVersion() != null ? "  -  Version: \\verb]" + rule.getVersion() + "]" : ""));
        RuleKey old = getLocalRuleKey(rule.getName());
        if (old == null && getQedeqBo().getExistenceChecker() != null) {
            old = getQedeqBo().getExistenceChecker().getParentRuleKey(rule.getName());
        }
        if (old != null) {
            printer.print("  -  Old Version: "
                + getRuleReference(rule.getName(), old.getVersion()));
        }
        printer.println("}");
        rule.getName();
        printer.println();
        if (rule.getDescription() != null) {
            printer.println(getLatexListEntry("getDescription()", rule.getDescription()));
            printer.println();
            printer.println();
        }
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
//                    printer.print("\\url{" + getUrl(getQedeqBo().getModuleAddress(), spec) + "}");
                    printer.print("\\url{" + getPdfLink((KernelQedeqBo) getQedeqBo()
                        .getLabels().getReferences().getQedeqBo(imp.getLabel())) + "}");
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
            String newLabel = "";
            if (list.size() >= ALPHABET.length() * ALPHABET.length()) {
                newLabel = "" + (i + 1);
            } else {
                // TODO 20110303 m31: this dosn't work if we have more than 26 * 26 elements
                if (list.size() > ALPHABET.length()) {
                    final int div = (i / ALPHABET.length());
                    newLabel = "" + ALPHABET.charAt(div);
                }
                newLabel += ALPHABET.charAt(i % ALPHABET.length());
            }
//            final String label = (i < ALPHABET.length() ? "" + ALPHABET .charAt(i) : "" + i);
            printer.println("\\centering $" + getLatex(list.getElement(i)) + "$"
                + " & \\label{" + mainLabel + "/" + newLabel + "} \\hypertarget{" + mainLabel + "/"
                + newLabel + "}{} \\mbox{\\emph{(" + newLabel + ")}} "
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
            // OK, we didn't found the language, so we take the default language
            final String def = getQedeqBo().getOriginalLanguage();
            for (int i = 0; i < list.size(); i++) {
                if (EqualsUtility.equals(def, list.get(i).getLanguage())) {
                    if (method.length() > 0) {
                        subContext = method + ".get(" + i + ")";
                    }
                    return "MISSING! OTHER: " + getLatex(list.get(i));
                }
            }
            // OK, we didn't find wanted and default language, so we take the first non empty one
            for (int i = 0; i < list.size(); i++) {
                if (method.length() > 0) {
                    subContext = method + ".get(" + i + ")";
                }
                if (null != list.get(i) && null != list.get(i).getLatex()
                        && list.get(i).getLatex().trim().length() > 0) {
                    return "MISSING! OTHER: " + getLatex(list.get(i));
                }
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
                final int end = input.getPosition();
                final SourcePosition endPosition = input.getSourcePosition();
                result.append(buffer.substring(last, start));
                result.append(getReference(ref, startPosition, endPosition));
                last = end;
            }
        } finally { // thanks to findbugs
            IoUtility.close(input);
            if (last < buffer.length()) {
                result.append(buffer.substring(last));
            }
        }
    }

    private String getReference(final String reference, final SourcePosition start, final SourcePosition end) {
        final String method = "getReference(String, String)";
        Trace.param(CLASS, this, method, "2 reference", reference);     // qreference within module

        final Reference ref = getReference(reference, getCurrentContext(start, end), true, false);
        if (ref.isNodeLocalReference() && ref.isSubReference()) {
            return "\\hyperlink{" + ref.getNodeLabel() + "/" + ref.getSubLabel() + "}{" + "("
                + ref.getSubLabel() + ")" + "}";
        }

        if (ref.isNodeLocalReference() && ref.isProofLineReference()) {
            return "\\hyperlink{" + ref.getNodeLabel() + "!" + ref.getProofLineLabel() + "}{" + "("
                + ref.getProofLineLabel() + ")" + "}";
        }

        if (!ref.isExternal()) {
            return "\\hyperlink{" + ref.getNodeLabel()
                + (ref.isSubReference() ? "/" + ref.getSubLabel() : "")
                + (ref.isProofLineReference() ? "!" + ref.getProofLineLabel() : "")
                + "}{"
                + getNodeDisplay(ref.getNodeLabel(), ref.getNode())
                + (ref.isSubReference() ? " (" + ref.getSubLabel() + ")" : "")
                + (ref.isProofLineReference() ? " (" + ref.getProofLineLabel() + ")" : "")
                + "}";
        }

        // do we have an external module reference without node?
        if (ref.isExternalModuleReference()) {
            return "\\url{" + getPdfLink(ref.getExternalQedeq()) + "}~\\cite{"
                + ref.getExternalQedeqLabel() + "}";
            // if we want to show the text "description": \href{my_url}{description}
        }

        String external = getPdfLink(ref.getExternalQedeq());
        if (external.startsWith("file://")) {
            external = "file:" + external.substring("file://".length());
        }
        return "\\hyperref{" +  external + "}{}{"
            + ref.getNodeLabel()
            + (ref.isSubReference() ? "/" + ref.getSubLabel() : "")
            + (ref.isProofLineReference() ? "!" + ref.getProofLineLabel() : "")
            + "}{" + getNodeDisplay(ref.getNodeLabel(), ref.getNode())
                + (ref.isSubReference() ? " (" + ref.getSubLabel() + ")" : "")
                + (ref.isProofLineReference() ? " (" + ref.getProofLineLabel() + ")" : "")
            + "}~\\cite{" + ref.getExternalQedeqLabel() + "}";
    }

    private String getNodeDisplay(final String label, final KernelNodeBo kNode) {
        return StringUtility.replace(getNodeDisplay(label, kNode, language), " ", "~");
    }

    private String getRuleReference(final String ruleName) {
        return getRuleReference(ruleName, ruleName);
    }

    private String getRuleReference(final String ruleName, final String caption) {
        final String method = "getRuleReference(String, String)";
        Trace.param(CLASS, this, method, "ruleName", ruleName);
        RuleKey key = getLocalRuleKey(ruleName);
        if (key == null && getQedeqBo().getExistenceChecker() != null) {
            key = getQedeqBo().getExistenceChecker().getParentRuleKey(ruleName);
        }
        if (key == null) {
            key = getQedeqBo().getLabels().getRuleKey(ruleName);
        }
        KernelQedeqBo qedeq = getQedeqBo();
        if (getQedeqBo().getExistenceChecker() != null) {
            qedeq = getQedeqBo().getExistenceChecker().getQedeq(key);
        }
        String localRef = getQedeqBo().getLabels().getRuleLabel(key);
        final String refRuleName = qedeq.getLabels().getRule(key).getName();
        if (!ruleName.equals(refRuleName)) {
            localRef += "!" + ruleName;
        }
        qedeq.getLabels().getRule(key).getName();
        boolean local = getQedeqBo().equals(qedeq);
        if (local) {
            return "\\hyperlink{" + localRef + "}{" + caption + "}";
        }
        String external = getPdfLink(qedeq);
        if (external.startsWith("file://")) {
            external = "file:" + external.substring("file://".length());
        }
        return "\\hyperref{" + external + "}{}{" + caption + "}{"
            + localRef + "}";
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

// TODO 20110214 m31: decide about removal
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
     * Get URL to for PDF version of module. If the referenced module doesn't
     * support the current language we switch to the original language.
     *
     * @param   prop    Get URL for this QEDEQ module.
     * @return  URL to PDF.
     */
    private String getPdfLink(final KernelQedeqBo prop) {
        if (prop == null) {
            return "";
        }
        final String url = prop.getUrl();
        final int dot = url.lastIndexOf(".");
        if (prop.isSupportedLanguage(language)) {
            return url.substring(0, dot) + "_" + language + ".pdf";
        }
        final String a = prop.getOriginalLanguage();
        return url.substring(0, dot) + (a.length() > 0 ? "_" + a : "") + ".pdf";
    }

    /**
     * Get LaTeX from string. Escapes common characters.
     * Also gets rid of leading spaces if they are equal among the lines.
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
        StringUtility.deleteLineLeadingWhitespace(buffer);
        return escapeUmlauts(buffer.toString().trim());
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
//        System.out.println("before");
//        System.out.println(buffer);
//        System.out.println();
//        System.out.println("after");
//        System.out.println(buffer);
//        System.out.println("*******************************************************************");
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
