/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.unicode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.base.io.AbstractOutput;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.DateUtility;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.ServiceProcess;
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
import org.qedeq.kernel.se.base.module.Formula;
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
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.visitor.QedeqNumbers;


/**
 * Transfer a QEDEQ module into unicode text.
 * <p>
 * <b>This is just a quick written generator. This class just generates some text output to be able
 * to get a visual impression of a QEDEQ module.</b>
 *
 * @author  Michael Meyling
 */
public class Qedeq2UnicodeVisitor extends ControlVisitor implements ReferenceFinder {

    /** This class. */
    private static final Class CLASS = Qedeq2UnicodeVisitor.class;

    /** Output goes here. */
    private AbstractOutput printer;

    /** Filter text to get and produce text in this language. */
    private String language;

    /** Filter for this detail level. */
//    private String level;

    /** Should additional information be put into LaTeX output? E.g. QEDEQ reference names. */
    private final boolean info;

    /** Current node id. */
    private String id;

    /** Current node title. */
    private String title;

    /** Sub context like "getIntroduction()". */
    private String subContext = "";

    /** Maximum column number. If zero no line breaking is done automatically. */
    private int maxColumns;

    /** Should warning messages be generated if LaTeX problems occur? */
    private boolean addWarnings;

    /** Should only names and formulas be be printed? */
    private final boolean brief;

    /** Alphabet for tagging. */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /** Printing data for a single formal proof line. */
    private ProofLineData lineData = new ProofLineData();

    /** This is the maximal formula width. All proof line formulas that are bigger are wrapped. */
    private int formulaWidth = 60;

    /** This is the maximal reason width. All proof line reasons that are bigger are wrapped. */
    private int reasonWidth = 35;

    /** Tabulation string. */
    private String tab = "";

    /**
     * Constructor.
     *
     * @param   plugin          This plugin we work for.
     * @param   prop            QEDEQ BO object.
     * @param   info            Write reference info?
     * @param   maximumColumn   Maximum column before automatic break.
     *                          0 means no automatic break.
     * @param   addWarnings     Should warning messages be generated
     *                          if LaTeX problems occur?
     * @param   brief           Should only names and formulas be be printed?
     */
    public Qedeq2UnicodeVisitor(final Plugin plugin, final KernelQedeqBo prop,
            final boolean info, final int maximumColumn, final boolean addWarnings,
            final boolean brief) {
        super(plugin, prop);
        this.info = info;
        this.maxColumns = maximumColumn;
        this.addWarnings = addWarnings;
        this.brief = brief;
    }

    /**
     * Gives a UTF-8 representation of given QEDEQ module as InputStream.
     *
     * @param   process     We run in this process.
     * @param   printer     Print herein.
     * @param   language    Filter text to get and produce text in this language only.
     * @param   level       Filter for this detail level. LATER 20100205 m31: not yet supported
     *                      yet.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException     File IO failed.
     */
    public void generateUtf8(final ServiceProcess process, final AbstractOutput printer, final String language,
            final String level) throws SourceFileExceptionList, IOException {
        setParameters(printer, language);
        try {
            traverse(process);
        } finally {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        }
    }

    /**
     * Set parameters.
     *
     * @param   printer     Print herein.
     * @param   language    Choose this language.
     */
    public void setParameters(final AbstractOutput printer,
            final String language) {
        this.printer = printer;
        this.printer.setColumns(maxColumns);
        // TODO 20110208 m31: perhaps we should have some config parameters for those percentage splittings
        if (maxColumns <= 0) {
            formulaWidth = 80;
            reasonWidth = 50;
        } else if (maxColumns <= 50) {
            this.printer.setColumns(50);
            formulaWidth = 21;
            reasonWidth = 21;
        } else if (maxColumns <= 100) {
            formulaWidth = (maxColumns - 8) * 50 / 100;
            reasonWidth = (maxColumns - 8) * 50 / 100;
        } else if (maxColumns <= 120) {
            reasonWidth = 46 + (maxColumns - 100) / 5;
            formulaWidth = maxColumns - 8 - reasonWidth;
        } else {
            reasonWidth = 50 + (maxColumns - 120) / 10;
            formulaWidth = maxColumns - 8 - reasonWidth;
        }
//        System.out.println("maxColums    =" + this.printer.getColumns());
//        System.out.println("formulaWidth =" + this.formulaWidth);
//        System.out.println("reasonWidth  =" + this.reasonWidth);
        if (language == null) {
            this.language = "en";
        } else {
            this.language = language;
        }
//        if (level == null) {
//            this.level = "1";
//        } else {
//            this.level = level;
//        }

        init();
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
        if (printer instanceof TextOutput) {
            printer.println("================================================================================");
            printer.println("UTF-8-file     " + ((TextOutput) printer).getName());
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
        final QedeqNumbers numbers = getCurrentNumbers();
        if (numbers.isChapterNumbering()) {
            if ("de".equals(language)) {
                printer.println("Kapitel " + numbers.getChapterNumber() + " ");
            } else {
                printer.println("Chapter " + numbers.getChapterNumber() + " ");
            }
            printer.println();
            printer.println();
        }
        underlineBig(getLatexListEntry("getTitle()", chapter.getTitle()));
        printer.println();
        if (chapter.getIntroduction() != null && !brief) {
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
        if (!getBlocked()) {
            printer.println();
            printer.println("___________________________________________________");
            printer.println();
            printer.println();
        }
        setBlocked(false);
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
        final QedeqNumbers numbers = getCurrentNumbers();
        final StringBuffer buffer = new StringBuffer();
        if (numbers.isChapterNumbering()) {
            buffer.append(numbers.getChapterNumber());
        }
        if (numbers.isSectionNumbering()) {
            if (buffer.length() > 0) {
                buffer.append(".");
            }
            buffer.append(numbers.getSectionNumber());
        }
        if (buffer.length() > 0 && section.getTitle() != null) {
            buffer.append(" ");
        }
        buffer.append(getLatexListEntry("getTitle()", section.getTitle()));
        underline(buffer.toString());
        printer.println();
        if (section.getIntroduction() != null && !brief) {
            printer.append(getLatexListEntry("getIntroduction()", section.getIntroduction()));
            printer.println();
            printer.println();
            printer.println();
        }
    }

    public void visitLeave(final Section section) {
        if (!getBlocked()) {
            printer.println();
        }
        setBlocked(false);
    }

    public void visitEnter(final Subsection subsection) {
        if (brief) {
            return;
        }
        final QedeqNumbers numbers = getCurrentNumbers();
        final StringBuffer buffer = new StringBuffer();
        if (numbers.isChapterNumbering()) {
            buffer.append(numbers.getChapterNumber());
        }
        if (numbers.isSectionNumbering()) {
            if (buffer.length() > 0) {
                buffer.append(".");
            }
            buffer.append(numbers.getSectionNumber());
        }
        if (buffer.length() > 0) {
            buffer.append(".");
        }
        buffer.append(numbers.getSubsectionNumber());
        if (buffer.length() > 0 && subsection.getTitle() != null) {
            buffer.append(" ");
        }
        if (subsection.getTitle() != null) {
            printer.print(buffer.toString());
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
        if (node.getPrecedingText() != null && !brief) {
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
        if (node.getSucceedingText() != null && !brief) {
            printer.append(getLatexListEntry("getSucceedingText()", node.getSucceedingText()));
            printer.println();
            printer.println();
        }
        printer.println();
    }

    public void visitEnter(final Axiom axiom) {
        final QedeqNumbers numbers = getCurrentNumbers();
        printer.print("\u2609 ");
        printer.print("Axiom " + numbers.getAxiomNumber());
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
        final QedeqNumbers numbers = getCurrentNumbers();
        printer.print("\u2609 ");
        printer.print("Proposition " + numbers.getPropositionNumber());
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
        if (brief) {
            setBlocked(true);
            return;
        }
        if ("de".equals(language)) {
            printer.println("Beweis:");
        } else {
            printer.println("Proof:");
        }
        printer.append(getLatexListEntry("getNonFormalProof()", proof.getNonFormalProof()));
    }

    public void visitLeave(final Proof proof) {
        printer.println();
        printer.println("q.e.d.");
        printer.println();
        setBlocked(false);
    }

    public void visitEnter(final FormalProof proof) {
        if (brief) {
            setBlocked(true);
            return;
        }
        if ("de".equals(language)) {
            printer.println("Beweis (formal):");
        } else {
            printer.println("Proof (formal):");
        }
    }

    public void visitLeave(final FormalProof proof) {
        if (!brief) {
            printer.println("q.e.d.");
            printer.println();
        }
        setBlocked(false);
    }


    public void visitEnter(final FormalProofLine line) {
        lineData.init();
        if (line.getLabel() != null) {
            lineData.setLineLabel(line.getLabel());
        }
        if (line.getReason() != null) {
            setReason(line.getReason().toString());
        }
        setFormula(line.getFormula());
    }

    public void visitLeave(final FormalProofLine line) {
        linePrintln();
    }

    /**
     * Print formula and reason.
     */
    private void linePrintln() {
        if (!lineData.containsData()) {
            return;
        }
        if (lineData.getLineLabel().length() > 0) {
            printer.print(StringUtility.alignRight("(" + lineData.getLineLabel() + ")", 5) + " ");
        }
        for (int i = 0; i < lineData.lines(); i++) {
            printer.skipToColumn(6);
            printer.print(tab);
            if (i < lineData.getFormula().length) {
                printer.print(lineData.getFormula()[i]);
            }
            if (i < lineData.getReason().length) {
                printer.skipToColumn(6 + 2 + formulaWidth);
                printer.print(lineData.getReason()[i]);
            }
            printer.println();
        }
        lineData.init();
    }

    public void visitEnter(final ConditionalProof r) throws ModuleDataException {
        lineData.init();
        printer.skipToColumn(6);
        printer.print(tab);
        printer.println("Conditional Proof");
        tab = tab + "  ";
    }

    public void visitLeave(final ConditionalProof proof) {
        tab = StringUtility.substring(tab, 0, tab.length() - 2);
    }

    public void visitEnter(final Hypothesis hypothesis) {
        lineData.init();
        if (hypothesis.getLabel() != null) {
            lineData.setLineLabel(hypothesis.getLabel());
        }
        setReason("Hypothesis");
        setFormula(hypothesis.getFormula());
    }

    public void visitLeave(final Hypothesis hypothesis) {
        linePrintln();
    }

    public void visitEnter(final Conclusion conclusion) {
        tab = StringUtility.substring(tab, 0, tab.length() - 2);
        lineData.init();
        if (conclusion.getLabel() != null) {
            lineData.setLineLabel(conclusion.getLabel());
        }
        setReason("Conclusion");
        setFormula(conclusion.getFormula());
        linePrintln();
    }

    public void visitLeave(final Conclusion conclusion) {
        linePrintln();
        tab = tab + "  ";
    }

    private void setReason(final String reasonString) {
        final List list = new ArrayList();
        int index = 0;
        while (index < reasonString.length()) {
            list.add(StringUtility.substring(reasonString, index, reasonWidth));
            index += reasonWidth;
        }
        lineData.setReason((String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
    }

    private void setFormula(final Formula f) {
        if (f != null && f.getElement() != null) {
            lineData.setFormula(getUtf8(f.getElement(),
                formulaWidth - tab.length()));
        }
    }

    private String getReference(final String reference) {
        return getReference(reference, "getReference()");
    }

    private String getReference(final String reference, final String subContext) {
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            getCurrentContext().setLocationWithinModule(context + "." + subContext);
            return (getReferenceLink(reference, null, null));
        } finally {
            getCurrentContext().setLocationWithinModule(context);
        }
    }

    public void visitEnter(final ModusPonens r) throws ModuleDataException {
        String buffer = r.getName();
        boolean one = false;
        if (r.getReference1() != null) {
            buffer += " " + getReference(r.getReference1(), "getReference1()");
            one = true;
        }
        if (r.getReference1() != null) {
            if (one) {
                buffer += ",";
            }
            buffer += " " + getReference(r.getReference2(), "getReference2()");
        }
        setReason(buffer);
    }

    public void visitEnter(final Add r) throws ModuleDataException {
        String buffer = r.getName();
        if (r.getReference() != null) {
            buffer += " " + getReference(r.getReference());
        }
        setReason(buffer);
    }

    public void visitEnter(final Rename r) throws ModuleDataException {
        String buffer = r.getName();
        if (r.getOriginalSubjectVariable() != null) {
            buffer += " " + getUtf8(r.getOriginalSubjectVariable());
        }
        if (r.getReplacementSubjectVariable() != null) {
            buffer += " by " + getUtf8(r.getReplacementSubjectVariable());
        }
        if (r.getReference() != null) {
            buffer += " in " + getReference(r.getReference());
        }
        setReason(buffer);
    }

    public void visitEnter(final SubstFree r) throws ModuleDataException {
        String buffer = r.getName();
        if (r.getSubjectVariable() != null) {
            buffer += " " + getUtf8(r.getSubjectVariable());
        }
        if (r.getSubstituteTerm() != null) {
            buffer += " by " + getUtf8(r.getSubstituteTerm());
        }
        if (r.getReference() != null) {
            buffer += " in " + getReference(r.getReference());
        }
        setReason(buffer);
    }

    public void visitEnter(final SubstFunc r) throws ModuleDataException {
        String buffer = r.getName();
        if (r.getFunctionVariable() != null) {
            buffer += " " + getUtf8(r.getFunctionVariable());
        }
        if (r.getSubstituteTerm() != null) {
            buffer += " by " + getUtf8(r.getSubstituteTerm());
        }
        if (r.getReference() != null) {
            buffer += " in " + getReference(r.getReference());
        }
        setReason(buffer);
    }

    public void visitEnter(final SubstPred r) throws ModuleDataException {
        String buffer = r.getName();
        if (r.getPredicateVariable() != null) {
            buffer += " " + getUtf8(r.getPredicateVariable());
        }
        if (r.getSubstituteFormula() != null) {
            buffer += " by " + getUtf8(r.getSubstituteFormula());
        }
        if (r.getReference() != null) {
            buffer += " in " + getReference(r.getReference());
        }
        setReason(buffer);
    }

    public void visitEnter(final Existential r) throws ModuleDataException {
        String buffer = r.getName();
        if (r.getSubjectVariable() != null) {
            buffer += " with " + getUtf8(r.getSubjectVariable());
        }
        if (r.getReference() != null) {
            buffer += " in " + getReference(r.getReference());
        }
        setReason(buffer);
    }

    public void visitEnter(final Universal r) throws ModuleDataException {
        String buffer = r.getName();
        if (r.getSubjectVariable() != null) {
            buffer += " with " + getQedeqBo().getElement2Utf8().getUtf8(
                r.getSubjectVariable());
        }
        if (r.getReference() != null) {
            buffer += " in " + getReference(r.getReference());
        }
        setReason(buffer);
    }

    public void visitEnter(final InitialPredicateDefinition definition) {
        final QedeqNumbers numbers = getCurrentNumbers();
        printer.print("\u2609 ");
        final StringBuffer buffer = new StringBuffer();
        if ("de".equals(language)) {
            buffer.append("initiale ");
        } else {
            buffer.append("initial ");
        }
        buffer.append("Definition " + (numbers.getPredicateDefinitionNumber()
            + numbers.getFunctionDefinitionNumber()));
        printer.print(buffer.toString());
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
        printer.println(getUtf8(definition.getPredCon()));
        printer.println();
        if (definition.getDescription() != null) {
            printer.append(getLatexListEntry("getDescription()", definition.getDescription()));
            printer.println();
            printer.println();
        }
    }

    public void visitEnter(final PredicateDefinition definition) {
        final QedeqNumbers numbers = getCurrentNumbers();
        printer.print("\u2609 ");
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Definition " + (numbers.getPredicateDefinitionNumber()
            + numbers.getFunctionDefinitionNumber()));
        printer.print(buffer.toString());
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
        printer.println(getUtf8(definition.getFormula().getElement()));
        printer.println();
        if (definition.getDescription() != null) {
            printer.append(getLatexListEntry("getDescription()", definition.getDescription()));
            printer.println();
            printer.println();
        }
    }

    public void visitEnter(final InitialFunctionDefinition definition) {
        final QedeqNumbers numbers = getCurrentNumbers();
        printer.print("\u2609 ");
        final StringBuffer buffer = new StringBuffer();
        if ("de".equals(language)) {
            buffer.append("initiale ");
        } else {
            buffer.append("initial ");
        }
        buffer.append("Definition " + (numbers.getPredicateDefinitionNumber()
                + numbers.getFunctionDefinitionNumber()));
        printer.print(buffer.toString());
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
        printer.println(getUtf8(definition.getFunCon()));
        printer.println();
        if (definition.getDescription() != null) {
            printer.println(getLatexListEntry("getDescription()", definition.getDescription()));
            printer.println();
        }
    }

    public void visitEnter(final FunctionDefinition definition) {
        final QedeqNumbers numbers = getCurrentNumbers();
        printer.print("\u2609 ");
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Definition " + (numbers.getPredicateDefinitionNumber()
                + numbers.getFunctionDefinitionNumber()));
        printer.print(buffer.toString());
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
        printer.println(getUtf8(definition.getFormula().getElement()));
        printer.println();
        if (definition.getDescription() != null) {
            printer.println(getLatexListEntry("getDescription()", definition.getDescription()));
            printer.println();
        }
    }

    public void visitEnter(final Rule rule) {
        final QedeqNumbers numbers = getCurrentNumbers();
        printer.print("\u2609 ");
        if ("de".equals(language)) {
            printer.print("Regel");
        } else {
            printer.print("Rule");
        }
        printer.print(" " + numbers.getRuleNumber());
        printer.print(" ");
        if (title != null && title.length() > 0) {
            printer.print(" (" + title + ")");
        }
        if (info) {
            printer.print("  [" + id + "]");
        }
        printer.println();
        printer.println((rule.getName() != null ? "  Name: " + rule.getName() : "")
            + (rule.getVersion() != null ? "  -  Version: " + rule.getVersion() : ""));
        printer.println();
        if (rule.getDescription() != null) {
            printer.append(getLatexListEntry("getDescription()", rule.getDescription()));
            printer.println();
            printer.println();
        }
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
                printer.print(" (" + linkList.get(i) + ")");
            }
        };
        printer.println();
    }

    public void visitEnter(final ChangedRuleList list) {
        if (list.size() <= 0) {
            return;
        }
        printer.println();
        if ("de".equals(language)) {
            printer.println("Die folgenden Regeln m\u00FCssen erweitert werden.");
        } else {
            if (!"en".equals(language)) {
                printer.println("%%% TODO unknown language: " + language);
            }
            printer.println("The following rules have to be extended.");
        }
        printer.println();
    }

    public void visitEnter(final ChangedRule rule) {
        printer.println((rule.getName() != null ? "  Name: " + rule.getName() : "")
            + (rule.getVersion() != null ? "  -  Version: " + rule.getVersion() : ""));
        printer.println();
        if (rule.getDescription() != null) {
            printer.append(getLatexListEntry("getDescription()", rule.getDescription()));
            printer.println();
            printer.println();
        }

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
            // OK, we didn't found the language, so we take the default language
            final String def = getQedeqBo().getOriginalLanguage();
            for (int i = 0; i < list.size(); i++) {
                if (EqualsUtility.equals(def, list.get(i).getLanguage())) {
                    if (method.length() > 0) {
                        subContext = method + ".get(" + i + ")";
                    }
                    return "MISSING! OTHER: " + getUtf8(list.get(i));
                }
            }
            // OK, we didn't find wanted and default language, so we take the first non empty one
            for (int i = 0; i < list.size(); i++) {
                if (method.length() > 0) {
                    subContext = method + ".get(" + i + ")";
                }
                if (null != list.get(i) && null != list.get(i).getLatex()
                        && list.get(i).getLatex().trim().length() > 0) {
                    return "MISSING! OTHER: " + getUtf8(list.get(i));
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
        if (addWarnings) {
            addWarning(new UnicodeException(code, msg, getCurrentContext(startDelta,
                endDelta)));
        }
    }

    /**
     * Add warning.
     *
     * @param   code        Warning code.
     * @param   msg         Warning message.
     */
    public void addWarning(final int code, final String msg) {
        Trace.param(CLASS, this, "addWarning", "msg", msg);
        if (addWarnings) {
            addWarning(new UnicodeException(code, msg, getCurrentContext()));
        }
    }

    /**
     * Get Utf8 element presentation.
     *
     * @param   element     Get presentation of this element.
     * @param   max         Maximum column.
     * @return  UTF-8 form of element.
     */
    protected String[] getUtf8(final Element element, final int max) {
        return getQedeqBo().getElement2Utf8().getUtf8(element, max);
    }


    /**
     * Get Utf8 element presentation.
     *
     * @param   element    Get presentation of this element.
     * @return  UTF-8 form of element.
     */
    protected String getUtf8(final Element element) {
        return getUtf8(getQedeqBo().getElement2Latex().getLatex(element));
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
        return Latex2UnicodeParser.transform(this, latex, maxColumns);
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

    public String getReferenceLink(final String reference, final SourcePosition start,
            final SourcePosition end) {
        final Reference ref = getReference(reference, getCurrentContext(start, end), addWarnings,
            false);

        if (ref.isNodeLocalReference() && ref.isSubReference()) {
            return "(" + ref.getSubLabel() + ")";
        }

        if (ref.isNodeLocalReference() && ref.isProofLineReference()) {
            return "(" + ref.getProofLineLabel() + ")";
        }

        if (!ref.isExternal()) {
            return getNodeDisplay(ref.getNodeLabel(), ref.getNode())
                + (ref.isSubReference() ? " (" + ref.getSubLabel() + ")" : "")
                + (ref.isProofLineReference() ? " (" + ref.getProofLineLabel() + ")" : "");
        }

        // do we have an external module reference without node?
        if (ref.isExternalModuleReference()) {
            return "[" + ref.getExternalQedeqLabel() + "]";
        }

        return getNodeDisplay(ref.getNodeLabel(), ref.getNode())
            + (ref.isSubReference() ? " (" + ref.getSubLabel() + ")" : "")
            + (ref.isProofLineReference() ? " (" + ref.getProofLineLabel() + ")" : "")
            + (ref.isExternal() ? " [" + ref.getExternalQedeqLabel() + "]" : "");
    }

    private String getNodeDisplay(final String label, final KernelNodeBo kNode) {
        return getNodeDisplay(label, kNode, language);
    }

}
