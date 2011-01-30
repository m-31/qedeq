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

package org.qedeq.kernel.bo.module;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.ModuleReferenceList;
import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.service.DefaultKernelQedeqBo;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.module.FunctionDefinitionVo;
import org.qedeq.kernel.se.dto.module.PredicateDefinitionVo;


/**
 * Transfer a QEDEQ formulas into LaTeX text.
 *
 * @author  Michael Meyling
 *
 * TODO 20101207 m31: separate data container from transformer! The container part should go into ModuleLabels
 */
public final class Element2Latex {

    /** External QEDEQ module references. */
    private ModuleReferenceList references;

    /** Maps predicate identifiers to {@link PredicateDefinition}s. */
    private final Map predicateDefinitions = new HashMap();

    /** Maps predicate identifiers to {@link PredicateDefinition}s. Contains default definitions
     * as a fallback.*/
    private final Map backupPredicateDefinitions = new HashMap();

    /** Maps predicate identifiers to {@link ModuleContext}s. */
    private final Map predicateContexts = new HashMap();

    /** Maps function identifiers to {@link FunctionDefinition}s. */
    private final Map functionDefinitions = new HashMap();

    /** Maps function identifiers to {@link FunctionDefinition}s. Contains default definitions. */
    private final Map backupFunctionDefinitions = new HashMap();

    /** Maps predicate identifiers to {@link ModuleContext}s. */
    private final Map functionContexts = new HashMap();

    /** Maps operator strings to {@link ElementList} to LaTeX mappers. */
    private final Map elementList2ListType = new HashMap();

    /** For mapping an unknown operator. */
    private final ListType unknown = new Unknown();

    /**
     * Constructor.
     */
    public Element2Latex() {
        this(null);
    }

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

        fillBackup();

    }

    /**
     * Fill backup predicate list (if required modules could not be loaded, or the predicate is
     * not yet defined.
     */
    private void fillBackup() {
        // logical identity operator
        addBackupPredicate(ExistenceChecker.NAME_EQUAL, "2", "#1 \\ = \\ #2");

        // negation of the logical identity operator
        addBackupPredicate("notEqual", "2", "#1 \\ \\neq \\ #2");

        addBackupPredicate("in", "2", "#1 \\in #2");
        addBackupPredicate("notIn", "2", "#1 \\notin #2");
        addBackupPredicate("isSet", "1", "\\mathfrak{M}(#1)");
        addBackupPredicate("subclass", "2", "#1 \\ \\subseteq \\ #2");
        addBackupPredicate("isOrderedPair", "1", "\\mbox{isOrderedPair}(#1)");
        addBackupPredicate("isRelation", "1", "\\mathfrak{Rel}(#1)");
        addBackupPredicate("isFunction", "1", "\\mathfrak{Funct}(#1)");

        addBackupFunction("RussellClass", "0", "\\mathfrak{Ru}");
        addBackupFunction("universalClass", "0", "\\mathfrak{V}");
        addBackupFunction("emptySet", "0", "\\emptyset");
        addBackupFunction("union", "2", "(#1 \\cup #2)");
        addBackupFunction("intersection", "2", "(#1 \\cap #2)");
        addBackupFunction("complement", "1", "\\overline{#1}");
        addBackupFunction("classList", "1", "\\{ #1 \\}");
        addBackupFunction("classList", "2", "\\{ #1, #2 \\}");
        addBackupFunction("setProduct", "1", "\\bigcap \\ #1");
        addBackupFunction("setSum", "1", "\\bigcup \\ #1");
        addBackupFunction("power", "1", "\\mathfrak{P}(#1)");
        addBackupFunction("orderedPair", "2", "\\langle #1, #2 \\rangle");
        addBackupFunction("cartesianProduct", "2", "( #1 \\times #2)");
        addBackupFunction("domain", "1", "\\mathfrak{Dom}(#1)");
        addBackupFunction("range", "1", "\\mathfrak{Rng}(#1)");
        addBackupFunction("successor", "1", "#1'");

    }

    /**
     * Add predicate to backup list.
     *
     * @param   name            Predicate name.
     * @param   argNumber       Number of arguments.
     * @param   latexPattern    This is the latex presentation. Variables are marked by "#1", "#2"
     *                          and so on.
     */
    private void addBackupPredicate(final String name, final String argNumber,
            final String latexPattern) {
        final String key = name + "_" + argNumber;
        final PredicateDefinitionVo predicate = new PredicateDefinitionVo();
        predicate.setArgumentNumber(argNumber);
        predicate.setName(name);
        predicate.setLatexPattern(latexPattern);
        backupPredicateDefinitions.put(key, predicate);
    }

    /**
     * Add function to backup list.
     *
     * @param   name            Function name.
     * @param   argNumber       Number of arguments.
     * @param   latexPattern    This is the LaTex presentation. Variables are marked by "#1", "#2"
     *                          and so on.
     */
    private void addBackupFunction(final String name, final String argNumber,
            final String latexPattern) {
        final String key = name + "_" + argNumber;
        final FunctionDefinitionVo function = new FunctionDefinitionVo();
        function.setArgumentNumber(argNumber);
        function.setName(name);
        function.setLatexPattern(latexPattern);
        backupFunctionDefinitions.put(key, function);
    }

    /**
     * Add predicate definition. If such a definition already exists it is overwritten.
     *
     * @param   definition  Definition to add.
     * @param   context     Here the definition stands.
     */
    public void addPredicate(final PredicateDefinition definition, final ModuleContext context) {
        final String identifier = definition.getName() + "_" + definition.getArgumentNumber();
        getPredicateDefinitions().put(identifier, definition);
        predicateContexts.put(identifier, new ModuleContext(context));
    }

    /**
     * Get predicate definition.
     *
     * @param   name            Predicate name.
     * @param   argumentNumber  Number of predicate arguments.
     * @return  Definition. Might be <code>null</code>.
     */
    public PredicateDefinition getPredicate(final String name, final int argumentNumber) {
        return (PredicateDefinition) getPredicateDefinitions().get(name + "_" + argumentNumber);
    }

    /**
     * Get predicate context. This is only a copy.
     *
     * @param   name            Predicate name.
     * @param   argumentNumber  Number of predicate arguments.
     * @return  Module context. Might be <code>null</code>.
     */
    public ModuleContext getPredicateContext(final String name, final int argumentNumber) {
        return new ModuleContext((ModuleContext) predicateContexts.get(name + "_" + argumentNumber));
    }

    /**
     * Add function definition. If such a definition already exists it is overwritten.
     *
     * @param   definition  Definition to add.
     * @param   context     Here the definition stands.
     */
    public void addFunction(final FunctionDefinition definition, final ModuleContext context) {
        final String identifier = definition.getName() + "_" + definition.getArgumentNumber();
        getFunctionDefinitions().put(identifier, definition);
        functionContexts.put(identifier, new ModuleContext(context));
    }

    /**
     * Get function definition.
     *
     * @param   name            Function name.
     * @param   argumentNumber  Number of function arguments.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionDefinition getFunction(final String name, final int argumentNumber) {
        return (FunctionDefinition) getFunctionDefinitions().get(name + "_" + argumentNumber);
    }

    /**
     * Get function context. This is only a copy.
     *
     * @param   name            Function name.
     * @param   argumentNumber  Number of function arguments.
     * @return  Module context. Might be <code>null</code>.
     */
    public ModuleContext getFunctionContext(final String name, final int argumentNumber) {
        return new ModuleContext((ModuleContext) functionContexts.get(name + "_" + argumentNumber));
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
     * Set list of external QEDEQ module references.
     *
     * @param   references  External QEDEQ module references.
     */
    public void setModuleReferences(final ModuleReferenceList references) {
        this.references = references;
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
     * Get default definition mapping of predicate definitions. Our last hope.
     *
     * @return  Mapping of predicate definitions.
     */
    Map getBackupPredicateDefinitions() {
        return this.backupPredicateDefinitions;
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
     * Get default definition mapping of function definitions. Our last hope.
     *
     * @return  Mapping of predicate definitions.
     */
    Map getBackupFunctionDefinitions() {
        return this.backupFunctionDefinitions;
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
            String identifier = name + "_" + (arguments);
            // LATER 20060922 m31: is only working for definition name + argument number
            //  if argument length is dynamic this dosen't work
            PredicateDefinition definition = (PredicateDefinition)
                Element2Latex.this.getPredicateDefinitions().get(identifier);
            if (definition == null) {
                // try external modules
                try {
                    final int external = name.indexOf(".");
                    if (external >= 0) {
                        final String shortName = name.substring(external + 1);
                        identifier = shortName + "_" + (arguments);
                        if (Element2Latex.this.getReferences() != null
                            && Element2Latex.this.getReferences().size() > 0) {
                            final String label = name.substring(0, external);
                            final DefaultKernelQedeqBo newProp = (DefaultKernelQedeqBo)
                                Element2Latex.this.getReferences().getQedeqBo(label);
                            if (newProp != null) {
                                if (newProp.getExistenceChecker().predicateExists(shortName,
                                        arguments)) {
                                    definition = newProp.getExistenceChecker()
                                        .getPredicate(shortName, arguments);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // try failed...
                }
            }
            // we try our backup
            if (definition == null) {
                definition = (PredicateDefinition) Element2Latex.this.getBackupPredicateDefinitions()
                    .get(identifier);
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
            String identifier = name + "_" + (arguments);
            // LATER 20060922 m31: is only working for definition name + argument number
            //  if argument length is dynamic this dosen't work
            FunctionDefinition definition = (FunctionDefinition)
                Element2Latex.this.getFunctionDefinitions().get(identifier);
            if (definition == null) {
                // try external modules
                try {
                    final int external = name.indexOf(".");
                    if (external >= 0) {
                        final String shortName = name.substring(external + 1);
                        identifier = shortName + "_" + (arguments);
                        if (Element2Latex.this.getReferences() != null
                                && Element2Latex.this.getReferences().size() > 0) {
                            final String label = name.substring(0, external);
                            final DefaultKernelQedeqBo newProp = (DefaultKernelQedeqBo)
                                Element2Latex.this.getReferences().getQedeqBo(label);
                            if (newProp != null) {
                                if (newProp.getExistenceChecker().functionExists(shortName,
                                        arguments)) {
                                    definition = newProp.getExistenceChecker()
                                        .getFunction(shortName, arguments);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // try failed...
                }
            }
            // we try our backup
            if (definition == null) {
                definition = (FunctionDefinition) Element2Latex.this.getBackupFunctionDefinitions()
                    .get(identifier);
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
    static class Var implements ListType {
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
            final String infix = "\\ " + latex + "\\ ";
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
