/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.basis.ControlVisitor;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.AuthorList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.ChangedRule;
import org.qedeq.kernel.se.base.module.Chapter;
import org.qedeq.kernel.se.base.module.Conclusion;
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
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
import org.qedeq.kernel.se.base.module.Location;
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
import org.qedeq.kernel.se.base.module.Specification;
import org.qedeq.kernel.se.base.module.Subsection;
import org.qedeq.kernel.se.base.module.SubsectionList;
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Term;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.base.module.UsedByList;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.SourceFileExceptionList;


/**
 * This class prints a QEDEQ module in XML format in an output stream.
 *
 * @author  Michael Meyling
 */
public final class Qedeq2Xml extends ControlVisitor implements ModuleService {

    /** Output goes here. */
    private TextOutput printer;

    /**
     * Constructor.
     *
     * @param   plugin  This plugin we work for.
     * @param   bo      QEDEQ BO.
     * @param   printer Print herein.
     */
    public Qedeq2Xml(final ModuleService plugin, final KernelQedeqBo bo, final TextOutput printer) {
        super(plugin, bo);
        this.printer = printer;
    }

    /**
     * Prints a XML representation of given QEDEQ module into a given output stream.
     *
     * @param   process             Service process we work for.
     * @param   plugin              Plugin we work for.
     * @param   bo                  BO QEDEQ module object.
     * @param   printer             Print herein.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException         Writing failed.
     */
    public static void print(final InternalServiceJob process,
            final ModuleService plugin, final KernelQedeqBo bo, final TextOutput printer) throws
            SourceFileExceptionList, IOException {
        final Qedeq2Xml converter = new Qedeq2Xml(plugin, bo, printer);
        try {
            converter.traverse(process);
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
            printer.print(" email=\"" + StringUtility.escapeXml(header.getEmail()) + "\"");
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
            printer.print(" name=\"" + StringUtility.escapeXml(specification.getName()) + "\"");
        }
        if (specification.getName() != null) {
            printer.print(" ruleVersion=\"" + StringUtility.escapeXml(specification.getRuleVersion()) + "\"");
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
            if (last.indexOf(".getChangedRuleList().get(") < 0) {
                printer.println("<DESCRIPTION>");
            }
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
            if (last.indexOf(".getChangedRuleList().get(") < 0) {
                printer.println("</DESCRIPTION>");
            }
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
            printer.addToken("  "); // we must fool the printer, this is token not whitespace!
            final String tabs = printer.getLevel();
            printer.clearLevel();
            // escape ]]>
            final String data = StringUtility.replace(latex.getLatex(),
                "]]>", "]]]]><![CDATA[>");
            printer.println(StringUtility.useSystemLineSeparator(data).trim());
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
            printer.print(" email=\"" + StringUtility.escapeXml(author.getEmail()) + "\"");
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
            printer.print(" label=\"" + StringUtility.escapeXml(imp.getLabel()) + "\"");
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
            printer.print(" id=\"" + StringUtility.escapeXml(subsection.getId()) + "\"");
        }
        if (subsection.getLevel() != null) {
            printer.print(" level=\"" + StringUtility.escapeXml(subsection.getLevel()) + "\"");
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
            printer.print(" id=\"" + StringUtility.escapeXml(node.getId()) + "\"");
        }
        if (node.getLevel() != null) {
            printer.print(" level=\"" + StringUtility.escapeXml(node.getLevel()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Node node) {
        printer.popLevel();
        printer.println("</NODE>");
    }

    public void visitEnter(final Axiom axiom) {
        printer.print("<AXIOM");
        if (axiom.getDefinedOperator() != null) {
            printer.print(" definedOperator=\"" + StringUtility.escapeXml(axiom.getDefinedOperator()) + "\"");
        }
        printer.println(">");
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
            printer.print(" kind=\"" + StringUtility.escapeXml(proof.getKind()) + "\"");
        }
        if (proof.getLevel() != null) {
            printer.print(" level=\"" + StringUtility.escapeXml(proof.getLevel()) + "\"");
        }
        printer.println(">");
    }

    public void visitLeave(final Proof proof) {
        printer.println("</PROOF>");
    }

    public void visitEnter(final FormalProof proof) {
        printer.println("<FORMAL_PROOF>");
        printer.pushLevel();
    }

    public void visitLeave(final FormalProof proof) {
        printer.popLevel();
        printer.println("</FORMAL_PROOF>");
    }

    public void visitEnter(final FormalProofLineList proof) {
        printer.println("<LINES>");
        printer.pushLevel();
    }

    public void visitLeave(final FormalProofLineList proof) {
        printer.popLevel();
        printer.println("</LINES>");
    }

    public void visitEnter(final FormalProofLine line) {
        printer.print("<L");
        if (line.getLabel() != null) {
            printer.print(" label=\"" + StringUtility.escapeXml(line.getLabel()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final FormalProofLine line) {
        printer.popLevel();
        printer.println("</L>");
    }

    public void visitEnter(final ModusPonens reason) {
        printer.print("<MP");
        if (reason.getReference1() != null) {
            printer.print(" ref1=\"" + StringUtility.escapeXml(reason.getReference1()) + "\"");
        }
        if (reason.getReference2() != null) {
            printer.print(" ref2=\"" + StringUtility.escapeXml(reason.getReference2()) + "\"");
        }
    }

    public void visitLeave(final ModusPonens reason) {
        printer.println("/>");
    }

    public void visitEnter(final Add reason) {
        printer.print("<ADD");
        if (reason.getReference() != null) {
            printer.print(" ref=\"" + StringUtility.escapeXml(reason.getReference()) + "\"");
        }
    }

    public void visitLeave(final Add reason) {
        printer.println("/>");
    }

    public void visitEnter(final Rename reason) {
        printer.print("<RENAME");
        if (reason.getReference() != null) {
            printer.print(" ref=\"" + StringUtility.escapeXml(reason.getReference()) + "\"");
        }
        if (reason.getOccurrence() != 0) {
            printer.print(" occurrence=\"" + StringUtility.escapeXml("" + reason.getOccurrence()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Rename reason) {
        printer.popLevel();
        printer.println("</RENAME>");
    }

    public void visitEnter(final SubstFree reason) {
        printer.print("<SUBST_FREE");
        if (reason.getReference() != null) {
            printer.print(" ref=\"" + StringUtility.escapeXml(reason.getReference()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final SubstFree reason) {
        printer.popLevel();
        printer.println("</SUBST_FREE>");
    }

    public void visitEnter(final SubstFunc reason) {
        printer.print("<SUBST_FUNVAR");
        if (reason.getReference() != null) {
            printer.print(" ref=\"" + StringUtility.escapeXml(reason.getReference()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final SubstFunc reason) {
        printer.popLevel();
        printer.println("</SUBST_FUNVAR>");
    }

    public void visitEnter(final SubstPred reason) {
        printer.print("<SUBST_PREDVAR");
        if (reason.getReference() != null) {
            printer.print(" ref=\"" + StringUtility.escapeXml(reason.getReference()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final SubstPred reason) {
        printer.popLevel();
        printer.println("</SUBST_PREDVAR>");
    }

    public void visitEnter(final Existential reason) {
        printer.print("<EXISTENTIAL");
        if (reason.getReference() != null) {
            printer.print(" ref=\"" + StringUtility.escapeXml(reason.getReference()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Existential reason) {
        printer.popLevel();
        printer.println("</EXISTENTIAL>");
    }

    public void visitEnter(final Universal reason) {
        printer.print("<UNIVERSAL");
        if (reason.getReference() != null) {
            printer.print(" ref=\"" + StringUtility.escapeXml(reason.getReference()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Universal reason) {
        printer.popLevel();
        printer.println("</UNIVERSAL>");
    }

    public void visitEnter(final ConditionalProof reason) {
        printer.println("<CP>");
        printer.pushLevel();
    }

    public void visitLeave(final ConditionalProof reason) {
        printer.popLevel();
        printer.println("</CP>");
    }

    public void visitEnter(final Hypothesis hypothesis) {
        printer.print("<HYPOTHESIS");
        if (hypothesis.getLabel() != null) {
            printer.print(" label=\"" + StringUtility.escapeXml(hypothesis.getLabel()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Hypothesis hypothesis) {
        printer.popLevel();
        printer.println("</HYPOTHESIS>");
    }

    public void visitEnter(final Conclusion conclusion) {
        printer.print("<CONCLUSION");
        if (conclusion.getLabel() != null) {
            printer.print(" label=\"" + StringUtility.escapeXml(conclusion.getLabel()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final Conclusion conclusion) {
        printer.popLevel();
        printer.println("</CONCLUSION>");
    }

    public void visitEnter(final InitialPredicateDefinition definition) {
        printer.print("<DEFINITION_PREDICATE_INITIAL");
        if (definition.getArgumentNumber() != null) {
            printer.print(" arguments=\"" + StringUtility.escapeXml(definition.getArgumentNumber()) + "\"");
        }
        if (definition.getName() != null) {
            printer.print(" name=\"" + StringUtility.escapeXml(definition.getName()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (definition.getLatexPattern() != null) {
            printer.println("<LATEXPATTERN>" + StringUtility.escapeXml(definition.getLatexPattern())
                + "</LATEXPATTERN>");
        }
    }

    public void visitLeave(final InitialPredicateDefinition definition) {
        printer.popLevel();
        printer.println("</DEFINITION_PREDICATE_INITIAL>");
    }

    public void visitEnter(final PredicateDefinition definition) {
        printer.print("<DEFINITION_PREDICATE");
        if (definition.getArgumentNumber() != null) {
            printer.print(" arguments=\"" + StringUtility.escapeXml(definition.getArgumentNumber()) + "\"");
        }
        if (definition.getName() != null) {
            printer.print(" name=\"" + StringUtility.escapeXml(definition.getName()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (definition.getLatexPattern() != null) {
            printer.println("<LATEXPATTERN>" + StringUtility.escapeXml(definition.getLatexPattern())
                + "</LATEXPATTERN>");
        }
    }

    public void visitLeave(final PredicateDefinition definition) {
        printer.popLevel();
        printer.println("</DEFINITION_PREDICATE>");
    }

    public void visitEnter(final InitialFunctionDefinition definition) {
        printer.print("<DEFINITION_FUNCTION_INITIAL");
        if (definition.getArgumentNumber() != null) {
            printer.print(" arguments=\"" + StringUtility.escapeXml(definition.getArgumentNumber()) + "\"");
        }
        if (definition.getName() != null) {
            printer.print(" name=\"" + StringUtility.escapeXml(definition.getName()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
        if (definition.getLatexPattern() != null) {
            printer.println("<LATEXPATTERN>" + definition.getLatexPattern()
                + "</LATEXPATTERN>");
        }
    }

    public void visitLeave(final InitialFunctionDefinition definition) {
        printer.popLevel();
        printer.println("</DEFINITION_FUNCTION_INITIAL>");
    }

    public void visitEnter(final FunctionDefinition definition) {
        printer.print("<DEFINITION_FUNCTION");
        if (definition.getArgumentNumber() != null) {
            printer.print(" arguments=\"" + StringUtility.escapeXml(definition.getArgumentNumber()) + "\"");
        }
        if (definition.getName() != null) {
            printer.print(" name=\"" + StringUtility.escapeXml(definition.getName()) + "\"");
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
            printer.print(" name=\"" + StringUtility.escapeXml(rule.getName()) + "\"");
        }
        if (rule.getVersion() != null) {
            printer.print(" version=\"" + StringUtility.escapeXml(rule.getVersion()) + "\"");
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
                printer.print(" id=\"" + StringUtility.escapeXml(linkList.get(i)) + "\"");
            }
            printer.println("/>");
        };
    }

    public void visitEnter(final ChangedRule rule) {
        printer.print("<CHANGED_RULE");
        if (rule.getName() != null) {
            printer.print(" name=\"" + StringUtility.escapeXml(rule.getName()) + "\"");
        }
        if (rule.getVersion() != null) {
            printer.print(" version=\"" + StringUtility.escapeXml(rule.getVersion()) + "\"");
        }
        printer.println(">");
    }

    public void visitLeave(final ChangedRule rule) {
        printer.println("</CHANGED_RULE>");
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
                    printer.print(" id=\"" + StringUtility.escapeXml(atom) + "\"");
                } else if ("PREDCON".equals(operator) || "FUNCON".equals(operator)) {
                    printer.print(" ref=\"" + StringUtility.escapeXml(atom) + "\"");
                } else {
                    printer.print(" unknown=\"" + StringUtility.escapeXml(atom) + "\"");
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
            printer.print(" label=\"" + StringUtility.escapeXml(item.getLabel()) + "\"");
        }
        printer.println(">");
        printer.pushLevel();
    }

    public void visitLeave(final LiteratureItem item) {
        printer.popLevel();
        printer.println("</ITEM>");
    }

    public String getServiceId() {
        return this.getClass().getName();
    }

    public String getServiceAction() {
        return "generate XML";
    }

    public String getServiceDescription() {
        return "Transformes QEDEQ module into XML data";
    }

}
