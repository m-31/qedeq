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

package org.qedeq.kernel.bo.service.utf8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.DateUtility;
import org.qedeq.base.utility.StringUtility;
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
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelNodeNumbers;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.bo.service.latex.Element2Latex;
import org.qedeq.kernel.bo.service.latex.LatexContentException;
import org.qedeq.kernel.bo.service.latex.LatexErrorCodes;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Transfer a QEDEQ module into a LaTeX file.
 * <p>
 * <b>This is just a quick written generator. No parsing or validation
 * of inline LaTeX text is done. This class just generates some LaTeX output to be able to
 * get a visual impression of a QEDEQ module.</b>
 *
 * @author  Michael Meyling
 */
public final class Qedeq2Utf8 extends ControlVisitor implements ReferenceFinder, PluginExecutor {

    /** This class. */
    private static final Class CLASS = Qedeq2Utf8.class;

    /** Output goes here. */
    private TextOutput printer;

    /** Filter text to get and produce text in this language. */
    private String language;

    /** Filter for this detail level. */
    private String level;

    /** Should additional information be put into LaTeX output? E.g. QEDEQ reference names. */
    private final boolean info;

    /** Transformer to get UTF-8 out of {@link Element}s. */
    private Element2Latex elementConverter;

    /** Current chapter number, starting with 1. */
    private int chapterNumber;

    /** Current section number, starting with 1. */
    private int sectionNumber;

    /** Current axiom number, starting with 1. */
    private int axiomNumber;

    /** Current definition number, starting with 1. */
    private int definitionNumber;

    /** Current proposition number, starting with 1. */
    private int propositionNumber;

    /** Current rule number, starting with 1. */
    private int ruleNumber;

    /** Current node id. */
    private String id;

    /** Current node title. */
    private String title;

    /** Sub context like "getIntroduction()". */
    private String subContext = "";

    /** Maximum column number. If zero no line breaking is done automatically. */
    private int maxColumns;

    /** Alphabet for tagging. */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        QEDEQ BO object.
     * @param   parameters  Plugin parameter.
     */
    Qedeq2Utf8(final Plugin plugin, final KernelQedeqBo prop, final Map parameters) {
        super(plugin, prop);
        final String method = "Qedeq2Utf8(Plugin, QedeqBo, Map)";
        String infoString = null;
        String maxColumnsString = "0";
        if (parameters != null) {
            infoString = (String) parameters.get("info");
            if (infoString == null) {
                infoString = "false";
            }
            maxColumnsString = (String) parameters.get("maximumColumn");
            if (maxColumnsString == null || maxColumnsString.length() == 0) {
                maxColumnsString = "80";
            }
        }
        info = "true".equalsIgnoreCase(infoString);
        maxColumns = 0;
        try {
            maxColumns = Integer.parseInt(maxColumnsString.trim());
        } catch (RuntimeException e) {
            // ignore
        }
    }

    public Object executePlugin() {
        final String method = "executePlugin()";
        final String ref = "\"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"";
        try {
            QedeqLog.getInstance().logRequest("Generate UTF-8 from " + ref);
            final String[] languages = getSupportedLanguages(getQedeqBo());
            for (int j = 0; j < languages.length; j++) {
                final String result = generateUtf8(languages[j], "1").toString();
                if (languages[j] != null) {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "UTF-8 for language \"" + languages[j]
                        + "\" was generated from " + ref + " into \"" + result + "\"");
                } else {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "UTF-8 for default language "
                        + "was generated from " + ref + " into \"" + result + "\"");
                }
            }
        } catch (final SourceFileExceptionList e) {
            final String msg = "Generation failed for " + ref;
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (IOException e) {
            final String msg = "Generation failed for " + ref;
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
     * Gives a UTF-8 representation of given QEDEQ module as InputStream.
     *
     * @param   language    Filter text to get and produce text in this language only.
     * @param   level       Filter for this detail level. LATER mime 20050205: not supported
     *                      yet.
     * @return  Resulting LaTeX.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException     File IO failed.
     */
    public File generateUtf8(final String language, final String level)
            throws SourceFileExceptionList, IOException {

        // first we try to get more information about required modules and their predicates..
        try {
            KernelContext.getInstance().loadRequiredModules(getQedeqBo().getModuleAddress());
            KernelContext.getInstance().checkModule(getQedeqBo().getModuleAddress());
        } catch (Exception e) {
            // we continue and ignore external predicates
            Trace.trace(CLASS, "generateUtf8(KernelQedeqBo, String, String)", e);
        }
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
        this.elementConverter = new Element2Latex((getQedeqBo().hasLoadedRequiredModules()
            ? getQedeqBo().getRequiredModules() : null));
        String txt = getQedeqBo().getModuleAddress().getFileName();
        if (txt.toLowerCase(Locale.US).endsWith(".xml")) {
            txt = txt.substring(0, txt.length() - 4);
        }
        if (language != null && language.length() > 0) {
            txt = txt + "_" + language;
        }
        // the destination is the configured destination directory plus the (relative)
        // localized file (or path) name
        File destination = new File(KernelContext.getInstance().getConfig()
            .getGenerationDirectory(), txt + ".txt").getCanonicalFile();

        init();

        try {
            printer = new TextOutput(getQedeqBo().getName(), new FileOutputStream(destination), "UTF-8");
            printer.setColumns(maxColumns);
            traverse();
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        } finally {
            if (printer != null) {
                printer.flush();
                printer.close();
            }
        }
        if (printer != null && printer.checkError()) {
            throw printer.getError();
        }
        return destination.getCanonicalFile();
    }

    /**
     * Reset counters and other variables. Should be executed before {@link #traverse()}.
     */
    protected void init() {
        printer = null;
        chapterNumber = 0;
        sectionNumber = 0;
        axiomNumber = 0;
        definitionNumber = 0;
        propositionNumber = 0;
        ruleNumber = 0;
        id = null;
        title = null;
        subContext = "";
    }

    // TODO m31 20070704: this should be part of QedeqBo
    String[] getSupportedLanguages(final QedeqBo prop) {
        // TODO m31 20070704: there should be a better way to
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

    public final void visitEnter(final Qedeq qedeq) {
        printer.println("================================================================================");
        printer.println("UTF-8-file     " + printer.getName());
        printer.println("Generated from " + getQedeqBo().getModuleAddress());
        printer.println("Generated at   " + DateUtility.getTimestamp());
        printer.println("================================================================================");
        printer.println();
        printer.println("If the characters of this document don't display correctly try opening this");
        printer.println("document within a webbrowser and if necessary change the encoding to");
        printer.println("Unicode (UTF-8)");
        printer.println();
        printer.println("Permission is granted to copy, distribute and/or modify this document");
        printer.println("under the terms of the GNU Free Documentation License, Version 1.2");
        printer.println("or any later version published by the Free Software Foundation;");
        printer.println("with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.");
        printer.println();
        printer.println();
        printer.println();
    }

    public final void visitLeave(final Qedeq qedeq) {
        printer.println();
    }

    public void visitEnter(final Header header) {
        final LatexList tit = header.getTitle();
        underlineBig(getLatexListEntry("getTitle()", tit));
        printer.println();
        final AuthorList authors = getQedeqBo().getQedeq().getHeader().getAuthorList();
        final StringBuffer authorList = new StringBuffer();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                authorList.append("    \n");
                printer.println(", ");
            }
            final Author author = authors.get(i);
            final String name = author.getName().getLatex().trim();
            printer.print(name);
            authorList.append("    " + name);
            String email = author.getEmail();
            if (email != null && email.trim().length() > 0) {
                authorList.append(" <" + email + ">");
            }
        }
        printer.println();
        printer.println();
        final String url = getQedeqBo().getUrl();
        if (url != null && url.length() > 0) {
            printer.println();
            if ("de".equals(language)) {
                printer.println("Die Quelle f\u00FCr dieses Dokument ist hier zu finden:");
            } else {
                if (!"en".equals(language)) {
                    printer.println("%%% TODO unknown language: " + language);
                }
                printer.println("The source for this document can be found here:");
            }
            printer.println();
            printer.println(url);
            printer.println();
        }
        {
            printer.println();
            if ("de".equals(language)) {
                printer.println("Die vorliegende Publikation ist urheberrechtlich gesch\u00FCtzt.");
            } else {
                if (!"en".equals(language)) {
                    printer.println("%%% TODO unknown language: " + language);
                }
                printer.println("Copyright by the authors. All rights reserved.");
            }
        }
        final String email = header.getEmail();
        if (email != null && email.length() > 0) {
            printer.println();
            printer.println();
            if ("de".equals(language)) {
                printer.println("Bei Fragen, Anregungen oder Bitte um Aufnahme in die Liste der"
                    + " abh\u00E4ngigen Module schicken Sie bitte eine EMail an die Adresse "
                    + email);
                printer.println();
                printer.println();
                printer.println("Die Autoren dieses Dokuments sind:");
                printer.println();
                printer.println(authorList);
            } else {
                if (!"en".equals(language)) {
                    printer.println("%%% TODO unknown language: " + language);
                }
                printer.println("If you have any questions, suggestions or want to add something"
                    + " to the list of modules that use this one, please send an email to the"
                    + " address " + email);
                printer.println();
                printer.println();
                printer.println("The authors of this document are:");
                printer.println(authorList);
            }
            printer.println();
        }
        printer.println();
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
        if (chapter.getNoNumber() == null || !chapter.getNoNumber().booleanValue()) {
            chapterNumber++;    // increase global chapter number
            if ("de".equals(language)) {
                printer.println("Kapitel " + chapterNumber + " ");
            } else {
                printer.println("Chapter " + chapterNumber + " ");
            }
            printer.println();
            printer.println();
        }
        underlineBig(getLatexListEntry("getTitle()", chapter.getTitle()));
        printer.println();
        if (chapter.getIntroduction() != null) {
            printer.append(getLatexListEntry("getIntroduction()", chapter.getIntroduction()));
            printer.println();
            printer.println();
            printer.println();
        }
    }

    public void visitLeave(final Header header) {
        printer.println();
        printer.println("___________________________________________________");
        printer.println();
        printer.println();
    }

    public void visitLeave(final Chapter chapter) {
        printer.println();
        printer.println("___________________________________________________");
        printer.println();
        printer.println();
    }

    public void visitLeave(final SectionList list) {
        sectionNumber = 0;  // reset section number
    }

    public void visitEnter(final Section section) {
        final StringBuffer buffer = new StringBuffer();
        if (section.getNoNumber() == null || !section.getNoNumber().booleanValue()) {
            sectionNumber++;    // increase global chapter number
            buffer.append(chapterNumber + "." + sectionNumber + " ");
        }
        buffer.append(getLatexListEntry("getTitle()", section.getTitle()));
        underline(buffer.toString());
        printer.println();
        if (section.getIntroduction() != null) {
            printer.append(getLatexListEntry("getIntroduction()", section.getIntroduction()));
            printer.println();
            printer.println();
            printer.println();
        }
    }

    public void visitLeave(final Section section) {
        printer.println();
    }

    public void visitEnter(final Subsection subsection) {
        if (subsection.getTitle() != null) {
            printer.print(getLatexListEntry("getTitle()", subsection.getTitle()));
        }
        if (subsection.getId() != null && info) {
            printer.print("  [" + subsection.getId() + "]");
        }
        if (subsection.getTitle() != null) {
            printer.println();
            printer.println();
        }
        printer.append(getLatexListEntry("getLatex()", subsection.getLatex()));
        printer.println();
        printer.println();
    }

    public void visitEnter(final Node node) {
        if (node.getPrecedingText() != null) {
            printer.append(getLatexListEntry("getPrecedingText()", node.getPrecedingText()));
            printer.println();
            printer.println();
        }
        id = node.getId();
        title = null;
        if (node.getTitle() != null) {
            title = getLatexListEntry("getTitle()", node.getTitle());
        }
    }

    public void visitLeave(final Node node) {
        if (node.getSucceedingText() != null) {
            printer.append(getLatexListEntry("getSucceedingText()", node.getSucceedingText()));
            printer.println();
            printer.println();
        }
        printer.println();
    }

    public void visitEnter(final Axiom axiom) {
        axiomNumber++;
        printer.print("\u2609 ");
        printer.print("Axiom " + axiomNumber);
        printer.print(" ");
        if (title != null && title.length() > 0) {
            printer.print(" (" + title + ")");
        }
        if (info) {
            printer.print("  [" + id + "]");
        }
        printer.println();
        printer.println();
        printer.print("     ");
        printFormula(axiom.getFormula().getElement());
        printer.println();
        if (axiom.getDescription() != null) {
            printer.append(getLatexListEntry("getDescription()", axiom.getDescription()));
            printer.println();
            printer.println();
        }
    }

    public void visitEnter(final Proposition proposition) {
        propositionNumber++;
        printer.print("\u2609 ");
        printer.print("Proposition " + propositionNumber);
        printer.print(" ");
        if (title != null && title.length() > 0) {
            printer.print(" (" + title + ")");
        }
        if (info) {
            printer.print("  [" + id + "]");
        }
        printer.println();
        printer.println();
        printTopFormula(proposition.getFormula().getElement(), id);
        printer.println();
        if (proposition.getDescription() != null) {
            printer.append(getLatexListEntry("getDescription()", proposition.getDescription()));
            printer.println();
            printer.println();
        }
    }

    public void visitEnter(final Proof proof) {
        if ("de".equals(language)) {
            printer.println("Beweis:");
        } else {
            printer.println("Proof:");
        }
        printer.append(getLatexListEntry("getNonFormalProof()", proof.getNonFormalProof()));
        printer.println();
        printer.println("q.e.d.");
        printer.println();
    }

    public void visitEnter(final PredicateDefinition definition) {
        definitionNumber++;
        printer.print("\u2609 ");
        final StringBuffer buffer = new StringBuffer();
        if (definition.getFormula() == null) {
            if ("de".equals(language)) {
                buffer.append("initiale ");
            } else {
                buffer.append("initial ");
            }
        }
        buffer.append("Definition " + definitionNumber);
        printer.print(buffer.toString());
        printer.print(" ");
        final StringBuffer define = new StringBuffer(getUtf8(definition.getLatexPattern()));
        final VariableList list = definition.getVariableList();
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Trace.trace(CLASS, this, "printPredicateDefinition", "replacing!");
                StringUtility.replace(define, "#" + (i + 1), getUtf8(list.get(i)));
            }
        }
        if (title != null && title.length() > 0) {
            printer.print(" (" + title + ")");
        }
        if (info) {
            printer.print("  [" + id + "]");
        }
        printer.println();
        printer.println();
        if (definition.getFormula() != null) {
            define.append("  \uFE55\u2194  ");
            define.append(getUtf8(definition.getFormula().getElement()));
        }
        // we always save the definition, even if there already exists an entry
        elementConverter.addPredicate(definition);
        Trace.param(CLASS, this, "printPredicateDefinition", "define", define);
        printer.print("     ");
        printer.println(define.toString());
        printer.println();
        if (definition.getDescription() != null) {
            printer.append(getLatexListEntry("getDescription()", definition.getDescription()));
            printer.println();
            printer.println();
        }
    }

    public void visitEnter(final FunctionDefinition definition) {
        definitionNumber++;
        printer.print("\u2609 ");
        final StringBuffer buffer = new StringBuffer();
        if (definition.getTerm() == null) {
            if ("de".equals(language)) {
                buffer.append("initiale ");
            } else {
                buffer.append("initial ");
            }
        }
        buffer.append("Definition " + definitionNumber);
        printer.print(buffer.toString());
        printer.print(" ");
        final StringBuffer define = new StringBuffer(getUtf8(definition.getLatexPattern()));
        final VariableList list = definition.getVariableList();
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Trace.trace(CLASS, this, "printFunctionDefinition", "replacing!");
                StringUtility.replace(define, "#" + (i + 1), getUtf8(list.get(i)));
            }
        }
        if (title != null && title.length() > 0) {
            printer.print(" (" + title + ")");
        }
        if (info) {
            printer.print("  [" + id + "]");
        }
        printer.println();
        printer.println();
        if (definition.getTerm() != null) {
            define.append("  \u2254  ");
            define.append(getUtf8(definition.getTerm().getElement()));
        }
        // we always save the definition, even if there already exists an entry
        elementConverter.addFunction(definition);
        Trace.param(CLASS, this, "printFunctionDefinition", "define", define);
        printer.print("     ");
        printer.println(define);
        printer.println();
        if (definition.getDescription() != null) {
            printer.println(getLatexListEntry("getDescription()", definition.getDescription()));
            printer.println();
        }
    }

    public void visitLeave(final FunctionDefinition definition) {
    }

    public void visitEnter(final Rule rule) {
        ruleNumber++;
        printer.print("\u2609 ");
        printer.print("Regel " + ruleNumber);
        printer.print(" ");
        if (title != null && title.length() > 0) {
            printer.print(" (" + title + ")");
        }
        if (info) {
            printer.print("  [" + id + "]");
        }
        printer.println();
        printer.println();
        if (rule.getDescription() != null) {
            printer.append(getLatexListEntry("getDescription()", rule.getDescription()));
            printer.println();
            printer.println();
        }
        if (rule.getProofList() != null) {
            for (int i = 0; i < rule.getProofList().size(); i++) {
                if ("de".equals(language)) {
                    printer.println("Beweis:");
                } else {
                    printer.println("Proof:");
                }
                printer.append(getLatexListEntry("getProofList().get(" + i + ")", rule.getProofList().get(i)
                        .getNonFormalProof()));
                printer.println();
                printer.println("q.e.d.");
                printer.println();
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
        printer.println();
        printer.println();
        if ("de".equals(language)) {
            underlineBig("Literaturverzeichnis");
        } else {
            underlineBig("Bibliography");
        }
        printer.println();
        printer.println();
        final ImportList imports = getQedeqBo().getQedeq().getHeader().getImportList();
        if (imports != null && imports.size() > 0) {
            printer.println();
            printer.println();
            if ("de".equals(language)) {
                printer.println("Benutzte andere QEDEQ-Module:");
            } else {
                printer.println("Used other QEDEQ modules:");
            }
            printer.println();
            for (int i = 0; i < imports.size(); i++) {
                final Import imp = imports.get(i);
                printer.print("[" + imp.getLabel() + "] ");
                final Specification spec = imp.getSpecification();
                printer.print(spec.getName());
                if (spec.getLocationList() != null && spec.getLocationList().size() > 0
                        && spec.getLocationList().get(0).getLocation().length() > 0) {
                    printer.print("  ");
                    printer.print(getUrl(getQedeqBo().getModuleAddress(), spec));
                }
                printer.println();
            }
            printer.println();
            printer.println();
            if ("de".equals(language)) {
                printer.println("Andere Referenzen:");
            } else {
                printer.println("Other references:");
            }
            printer.println();
        }
    }

    public void visitLeave(final LiteratureItemList list) {
        final UsedByList usedby = getQedeqBo().getQedeq().getHeader().getUsedByList();
        if (usedby != null && usedby.size() > 0) {
            printer.println();
            printer.println();
            if ("de".equals(language)) {
                printer.println("QEDEQ-Module, welche dieses hier verwenden:");
            } else {
                printer.println("QEDEQ modules that use this one:");
            }
            for (int i = 0; i < usedby.size(); i++) {
                final Specification spec = usedby.get(i);
                printer.print(spec.getName());
                final String url = getUrl(getQedeqBo().getModuleAddress(), spec);
                if (url != null && url.length() > 0) {
                    printer.print("  ");
                    printer.print(url);
                }
                printer.println();
            }
            printer.println();
            printer.println();
        }
        printer.println();
    }

    public void visitEnter(final LiteratureItem item) {
        printer.print("[" + item.getLabel() + "] ");
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
            printer.print("     ");
            printFormula(element);
            return;
        }
        final ElementList list = element.getList();
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
            printer.println("  (" + label + ")  " + getUtf8(list.getElement(i)));
        }
    }

    /**
     * Print formula.
     *
     * @param   element Formula to print.
     */
    private void printFormula(final Element element) {
        printer.println(getUtf8(element));
    }

    /**
     * Get Utf8 element presentation.
     *
     * @param   element    Get presentation of this element.
     * @return  UTF-8 form of element.
     */
    private String getUtf8(final Element element) {
        return getUtf8(elementConverter.getLatex(element));
    }

    /**
     * Filters correct entry out of LaTeX list. Filter criterion is for example the correct
     * language.
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
            for (int i = 0; i < list.size(); i++) {
                if (language.equals(list.get(i).getLanguage())) {
                    if (method.length() > 0) {
                        subContext = method + ".get(" + i + ")";
                    }
                    return getUtf8(list.get(i));
                }
            }
            // assume entry with missing language as default
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getLanguage() == null) {
                    if (method.length() > 0) {
                        subContext = method + ".get(" + i + ")";
                    }
                    return getUtf8(list.get(i));
                }
            }
            for (int i = 0; i < list.size(); i++) { // LATER mime 20050222: evaluate default?
                if (method.length() > 0) {
                    subContext = method + ".get(" + i + ")";
                }
                return "MISSING! OTHER: " + getUtf8(list.get(i));
            }
            return "MISSING!";
        } finally {
            if (method.length() > 0) {
                subContext = "";
            }
        }
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
    private ModuleContext getCurrentContext(final SourcePosition startDelta,
            final SourcePosition endDelta) {
        if (subContext.length() == 0) {
            return super.getCurrentContext();
        }
        final ModuleContext c = new ModuleContext(super.getCurrentContext().getModuleLocation(),
            super.getCurrentContext().getLocationWithinModule() + "." + subContext,
            startDelta, endDelta);
        return c;
    }

    /**
     * Add warning.
     *
     * @param   code        Warning code.
     * @param   msg         Warning message.
     * @param   startDelta  Skip position (relative to location start). Could be
     *                      <code>null</code>.
     * @param   endDelta    Mark until this column (relative to location start). Could be
     *                      be <code>null</code>
     */
    public void addWarning(final int code, final String msg, final SourcePosition startDelta,
            final SourcePosition endDelta) {
        Trace.param(CLASS, this, "addWarning", "msg", msg);
        addWarning(new LatexContentException(code, msg, getCurrentContext(startDelta, endDelta)));
    }

    /**
     * Get UTF-8 String for LaTeX.
     *
     * @param   latex   LaTeX we got.
     * @return  UTF-8 result string.
     */
    private String getUtf8(final Latex latex) {
        if (latex == null || latex.getLatex() == null) {
            return "";
        }
        return getUtf8(latex.getLatex());
    }

    /**
     * Transform LaTeX into UTF-8 String..
     *
     * @param   latex   LaTeX text.
     * @return  String.
     */
    private String getUtf8(final String latex) {
        if (latex == null) {
            return "";
        }
        return Latex2Utf8Parser.transform(this, latex, maxColumns);
    }

    /**
     * Print text in one line and print another line with = to underline it.
     *
     * @param   text    Line to print.
     */
    private void underlineBig(final String text) {
        printer.println(text);
        for (int i = 0; i  < text.length(); i++) {
            printer.print("\u2550");
        }
        printer.println();
    }

    /**
     * Print text in one line and print another line with = to underline it.
     *
     * @param   text    Line to print.
     */
    private void underline(final String text) {
        printer.println(text);
        for (int i = 0; i  < text.length(); i++) {
            printer.print("\u2015");
        }
        printer.println();
    }

    public String getReferenceLink(final String reference, final String subReference,
            final SourcePosition startPosition, final SourcePosition endPosition) {
        final String method = "getExternalReference(SourcePosition, SourcePosition, String, "
            + "String)";

        // get module label (if any)
        String moduleLabel = "";
        String localLabel = reference;
        int dot = reference.indexOf(".");
        if (dot >= 0) {
            moduleLabel = reference.substring(0, dot);
            localLabel = reference.substring(dot + 1);
        }
        String fix = "";
        if (subReference != null && subReference.length() > 0) {
            fix += " (" + subReference + ")";
        }
        KernelQedeqBo module = getQedeqBo();
        if (moduleLabel.length() == 0) {
            // check if local reference is in fact a module label
            final KernelQedeqBo refModule = module.getKernelRequiredModules()
                .getKernelQedeqBo(localLabel);
            if (refModule != null) {
                moduleLabel = localLabel;
                localLabel = "";
                module = refModule;
                return "[" + moduleLabel + "]";
            }
        } else {
            final KernelQedeqBo refModule = module.getKernelRequiredModules()
                .getKernelQedeqBo(moduleLabel);
            if (refModule == null) {
                module = refModule;
                Trace.info(CLASS, this, method, "module not found for " + moduleLabel);
                addWarning(LatexErrorCodes.QREF_PARSING_EXCEPTION_CODE,
                    LatexErrorCodes.QREF_PARSING_EXCEPTION_MSG
                    + ": " + "module not found for " + reference, startPosition,
                    endPosition);
                return moduleLabel + "?." + localLabel + fix;
            }
            module = refModule;
        }
        if (moduleLabel.length() > 0) {
            fix += " [" + moduleLabel + "]";
        }
        final KernelNodeBo kNode = module.getLabels().getNode(localLabel);
        if (kNode != null) {
            return getNodeDisplay(kNode) + fix;
        } else {
            Trace.info(CLASS, this, method, "node not found for " + reference);
            addWarning(LatexErrorCodes.QREF_PARSING_EXCEPTION_CODE,
                LatexErrorCodes.QREF_PARSING_EXCEPTION_MSG
                + ": " + "node not found for " + reference, startPosition,
                endPosition);
            return localLabel + "?" + fix;
        }
    }

    private String getNodeDisplay(final KernelNodeBo kNode) {
        String display = "";
        KernelNodeNumbers data = kNode.getNumbers();
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

}
