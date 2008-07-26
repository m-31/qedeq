/* $Id: Element2Latex.java,v 1.1 2008/07/26 07:58:28 m31 Exp $
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

package org.qedeq.kernel.bo.service.latex;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.bo.ModuleReferenceList;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.module.DefaultExistenceChecker;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.dto.module.PredicateDefinitionVo;
import org.qedeq.kernel.visitor.AbstractModuleVisitor;


/**
 * Transfer a QEDEQ formulas into LaTeX text.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class Element2Latex extends AbstractModuleVisitor {

    /** External QEDEQ module references. */
    private final ModuleReferenceList references;

    /** Maps identifiers to {@link PredicateDefinition}s. */
    private final Map predicateDefinitions = new HashMap();

    /** Maps identifiers to {@link FunctionDefinition}s. */
    private final Map functionDefinitions = new HashMap();

    /** Maps operator strings to {@link ElementList} to LaTeX mappers. */
    private final Map elementList2ListType = new HashMap();

    /** For mapping an unknown operator. */
    private final ListType unknown = new Unknown();

    /**
     * Constructor.
     *
     * @param   references  External QEDEQ module references.
     */
    public Element2Latex(final ModuleReferenceList references) {
        this.references = references;

        this.elementList2ListType.put("PREDVAR", new Predvar());
        this.elementList2ListType.put("FUNVAR", new Funvar());
        this.elementList2ListType.put("PREDCON", new Predcon());
        this.elementList2ListType.put("FUNCON", new Funcon());
        this.elementList2ListType.put("VAR", new Var());

        this.elementList2ListType.put("AND", new BinaryLogical("\\land"));
        this.elementList2ListType.put("OR", new BinaryLogical("\\lor"));
        this.elementList2ListType.put("IMPL", new BinaryLogical("\\rightarrow"));
        this.elementList2ListType.put("EQUI", new BinaryLogical("\\leftrightarrow"));

        this.elementList2ListType.put("FORALL", new Quantifier("\\forall"));
        this.elementList2ListType.put("EXISTS", new Quantifier("\\exists"));
        this.elementList2ListType.put("EXISTSU", new Quantifier("\\exists!"));

        this.elementList2ListType.put("NOT", new Not());
        this.elementList2ListType.put("CLASS", new Class());
        this.elementList2ListType.put("CLASSLIST", new Classlist());

        // TODO mime 20080126: wrong spelled and not used any longer (?)
        this.elementList2ListType.put("QUANTOR_INTERSECTION", new QuantorIntersection());
        this.elementList2ListType.put("QUANTOR_UNION", new QuantorUnion());

        // quick hack to have the logical identity operator
        final String nameEqual = ExistenceChecker.NAME_EQUAL;
        final String argNumberEqual = "2";
        final String keyEqual = nameEqual + "_" + argNumberEqual;
        if (!getPredicateDefinitions().containsKey(keyEqual)) {
            final PredicateDefinitionVo equal = new PredicateDefinitionVo();
            equal.setArgumentNumber(argNumberEqual);
            equal.setName(nameEqual);
            equal.setLatexPattern("#1 \\ =  \\ #2");
            getPredicateDefinitions().put(keyEqual, equal);
        }

        // LATER mime 20080107: quick hack to get the negation of the logical identity operator
        final String nameNotEqual = "notEqual";
        final String argNumberNotEqual = "2";
        final String keyNotEqual = nameNotEqual + "_" + argNumberNotEqual;
        if (!getPredicateDefinitions().containsKey(keyNotEqual)) {
            final PredicateDefinitionVo notEqual = new PredicateDefinitionVo();
            notEqual.setArgumentNumber("2");
            notEqual.setName("notEqual");
            notEqual.setLatexPattern("#1 \\ \\neq  \\ #2");
            getPredicateDefinitions().put(keyNotEqual, notEqual);
        }
    }

    /**
     * Add predicate definition. If such a definition already exists it is overwritten.
     *
     * @param   definition  Definition to add.
     */
    public void addPredicate(final PredicateDefinition definition) {
        getPredicateDefinitions().put(definition.getName() + "_" + definition.getArgumentNumber(),
            definition);
    }

    /**
     * Add function definition. If such a definition already exists it is overwritten.
     *
     * @param   definition  Definition to add.
     */
    public void addFunction(final FunctionDefinition definition) {
        getFunctionDefinitions().put(definition.getName() + "_" + definition.getArgumentNumber(),
            definition);
    }

    /**
     * Get LaTeX element presentation.
     *
     * @param   element    Print this element.
     * @return  LaTeX form of element.
     */
    public String getLatex(final Element element) {
        return getLatex(element, true);
    }

    /**
     * Get LaTeX element presentation.
     *
     * @param   element Print this element.
     * @param   first   First level?
     * @return  LaTeX form of element.
     */
    String getLatex(final Element element, final boolean first) {
        if (element.isAtom()) {
            return element.getAtom().getString();
        }
        final ElementList list = element.getList();

        ListType converter = (ListType) elementList2ListType.get(list.getOperator());

        if (converter == null) {
            converter = this.unknown;
        }
        return converter.getLatex(list, first);

    }

    /**
     * Get list of external QEDEQ module references.
     *
     * @return  External QEDEQ module references.
     */
    ModuleReferenceList getReferences() {
        return this.references;
    }

    /**
     * Get mapping of predicate definitions.
     *
     * @return  Mapping of predicate definitions.
     */
    Map getPredicateDefinitions() {
        return this.predicateDefinitions;
    }

    /**
     * Get mapping of function definitions.
     *
     * @return  Mapping of function definitions.
     */
    Map getFunctionDefinitions() {
        return this.functionDefinitions;
    }

    /**
     * Describes the interface for an {@link ElementList} to LaTeX converter.
     */
    interface ListType {

        /**
         * Transform list into LaTeX.
         *
         * @param   list    This list shall be transformed.
         * @param   first   Is the resulting LaTeX formula or term at top level? If so we possibly
         *                  can omit some brackets.
         * @return  LaTeX formula or term.
         */
        public String getLatex(ElementList list, boolean first);
    }

    /**
     * Transformer for a predicate variable.
     */
    class Predvar implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String identifier = list.getElement(0).getAtom().getString();
            buffer.append(identifier);
            if (list.size() > 1) {
                buffer.append("(");
                for (int i = 1; i < list.size(); i++) {
                    buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                    if (i + 1 < list.size()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")");
            }
            return buffer.toString();
        }
    }

    /**
     * Transformer for a function variable.
     */
    class Funvar implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String identifier = list.getElement(0).getAtom().getString();
            buffer.append(identifier);
            if (list.size() > 1) {
                buffer.append("(");
                for (int i = 1; i < list.size(); i++) {
                    buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                    if (i + 1 < list.size()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")");
            }
            return buffer.toString();
        }
    }

    /**
     * Transformer for a predicate constant.
     */
    class Predcon implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String name = list.getElement(0).getAtom().getString();
            final int arguments = list.size() - 1;
            final String identifier = name + "_" + (arguments);
            // TODO mime 20060922: is only working for definition name + argument number
            //  if argument length is dynamic this dosen't work
            PredicateDefinition definition = (PredicateDefinition)
                Element2Latex.this.getPredicateDefinitions().get(identifier);
            if (definition == null) {
                // try external modules
                try {
                    final int external = name.indexOf(".");
                    if (external >= 0 && Element2Latex.this.getReferences() != null
                            && Element2Latex.this.getReferences().size() > 0) {
                        final String label = name.substring(0, external);
                        final KernelQedeqBo newProp = (KernelQedeqBo)
                            Element2Latex.this.getReferences().getQedeqBo(label);
                        if (newProp != null) {
                            final String shortName = name.substring(external + 1);
                            if (newProp.getExistenceChecker().predicateExists(shortName,
                                    arguments)) {
                                // FIXME mime 20080120: Quick and very dirty!
                                final DefaultExistenceChecker checker = (DefaultExistenceChecker)
                                    newProp.getExistenceChecker();
                                definition = checker.getPredicate(shortName, arguments);
                            }
                        }
                    }
                } catch (Exception e) {
                    // try failed...
                }
            }
            if (definition != null) {
                final StringBuffer define = new StringBuffer(definition.getLatexPattern());
                for (int i = list.size() - 1; i >= 1; i--) {
                    StringUtility.replace(define, "#" + i, Element2Latex.this.getLatex(
                        list.getElement(i), false));
                }
                buffer.append(define);
            } else {
                buffer.append(identifier);
                buffer.append("(");
                for (int i = 1; i < list.size(); i++) {
                    buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                    if (i + 1 < list.size()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")");
            }
            return buffer.toString();
        }
    }

    /**
     * Transformer for a function constant.
     */
    class Funcon implements ListType {

        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String name = list.getElement(0).getAtom().getString();
            final int arguments = list.size() - 1;
            final String identifier = name + "_" + (arguments);
            // TODO mime 20060922: is only working for definition name + argument number
            //  if argument length is dynamic this dosen't work
            FunctionDefinition definition = (FunctionDefinition)
                Element2Latex.this.getFunctionDefinitions().get(identifier);
            if (definition == null) {
                // try external modules
                try {
                    final int external = name.indexOf(".");
                    if (external >= 0 && Element2Latex.this.getReferences() != null
                            && Element2Latex.this.getReferences().size() > 0) {
                        final String label = name.substring(0, external);
                        final KernelQedeqBo newProp = (KernelQedeqBo)
                            Element2Latex.this.getReferences().getQedeqBo(label);
                        if (newProp != null) {
                            final String shortName = name.substring(external + 1);
                            if (newProp.getExistenceChecker().functionExists(shortName,
                                    arguments)) {
                                // FIXME mime 20080120: Quick and very dirty!
                                final DefaultExistenceChecker checker = (DefaultExistenceChecker)
                                    newProp.getExistenceChecker();
                                definition = checker.getFunction(shortName, arguments);
                            }
                        }
                    }
                } catch (Exception e) {
                    // try failed...
                }
            }
            if (definition != null) {
                final StringBuffer define = new StringBuffer(definition.getLatexPattern());
                for (int i = list.size() - 1; i >= 1; i--) {
                    StringUtility.replace(define, "#" + i, Element2Latex.this.
                        getLatex(list.getElement(i), false));
                }
                buffer.append(define);
            } else {
                buffer.append(identifier);
                buffer.append("(");
                for (int i = 1; i < list.size(); i++) {
                    buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                    if (i + 1 < list.size()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")");
            }
            return buffer.toString();
        }
    }

    /**
     * Transformer for a subject variable.
     */
    class Var implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
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
        }
    }

    /**
     * Transformer for a binary logical operator written in infix notation.
     */
    class BinaryLogical implements ListType {

        /** LaTeX for operator. */
        private final String latex;

        /**
         * Constructor.
         *
         * @param   latex   LaTeX for operator.
         */
        BinaryLogical(final String latex) {
            this.latex = latex;
        }

        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String infix = "\\ " + latex + " \\ ";
            if (!first) {
                buffer.append("(");
            }
            // we also handle it if it has not exactly two operands
            for (int i = 0; i < list.size(); i++) {
                buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(infix);
                }
            }
            if (!first) {
                buffer.append(")");
            }
            return buffer.toString();
        }
    }

    /**
     * Transformer for a quantifier operator.
     */
    class Quantifier implements ListType {

        /** LaTeX for quantifier. */
        private final String latex;

        /**
         * Constructor.
         *
         * @param   latex   LaTeX for quantifier.
         */
        Quantifier(final String latex) {
            this.latex = latex;
        }

        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(latex + " ");
            for (int i = 0; i < list.size(); i++) {
                if (i != 0 || (i == 0 && list.size() <= 2)) {
                    buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                }
                if (i + 1 < list.size()) {
                    buffer.append("\\ ");
                }
                if (list.size() > 2 && i == 1) {
                    buffer.append("\\ ");
                }
            }
            return buffer.toString();
        }
    }

    /**
     * Transformer for negation.
     */
    class Not implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String prefix = "\\neg ";
            buffer.append(prefix);
            for (int i = 0; i < list.size(); i++) {
                buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
            }
            return buffer.toString();
        }
    }

    /**
     * Transformer for a class operator.
     */
    class Class implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String prefix = "\\{ ";
            buffer.append(prefix);
            for (int i = 0; i < list.size(); i++) {
                buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(" \\ | \\ ");
                }
            }
            buffer.append(" \\} ");
            return buffer.toString();
        }
    }

    /**
     * Transformer for class list operator.
     */
    class Classlist implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String prefix = "\\{ ";
            buffer.append(prefix);
            for (int i = 0; i < list.size(); i++) {
                buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(", \\ ");
                }
            }
            buffer.append(" \\} ");
            return buffer.toString();
        }
    }

    /**
     * Transformer for a quantifier intersection.
     */
    class QuantorIntersection implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String prefix = "\\bigcap";
            buffer.append(prefix);
            if (0 < list.size()) {
                buffer.append("{").append(Element2Latex.this.getLatex(list.getElement(0), false))
                .append("}");
            }
            for (int i = 1; i < list.size(); i++) {
                buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(" \\ \\ ");
                }
            }
            buffer.append(" \\} ");
            return buffer.toString();
        }
    }

    /**
     * LATER mime 20080126: needed?
     */
    class QuantorUnion implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            final String prefix = "\\bigcup";
            buffer.append(prefix);
            if (0 < list.size()) {
                buffer.append("{").append(Element2Latex.this.getLatex(list.getElement(0), false))
                .append("}");
            }
            for (int i = 1; i < list.size(); i++) {
                buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(" \\ \\ ");
                }
            }
            buffer.append(" \\} ");
            return buffer.toString();
        }
    }

    /**
     * LATER mime 20080126: needed?
     */
    class Unknown implements ListType {
        public String getLatex(final ElementList list, final boolean first) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(list.getOperator());
            buffer.append("(");
            for (int i = 0; i < list.size(); i++) {
                buffer.append(Element2Latex.this.getLatex(list.getElement(i), false));
                if (i + 1 < list.size()) {
                    buffer.append(", ");
                }
            }
            buffer.append(")");
            return buffer.toString();
        }
    }


}
