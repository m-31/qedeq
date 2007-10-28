/* $Id: QedeqBoFormalLogicChecker.java,v 1.4 2007/04/12 23:50:11 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.bo.logic.ExistenceChecker;
import org.qedeq.kernel.bo.logic.FormulaChecker;
import org.qedeq.kernel.bo.module.IllegalModuleDataException;
import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTransverser;
import org.qedeq.kernel.dto.module.PredicateDefinitionVo;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class QedeqBoFormalLogicChecker extends AbstractModuleVisitor
        implements ExistenceChecker {

    /** QEDEQ module input object. */
    private final QedeqBo original;

    /** Current context during creation. */
    private final QedeqNotNullTransverser transverser;

    /** Maps identifiers to {@link PredicateDefinition}s. */
    private final Map predicateDefinitions = new HashMap();

    /** Maps identifiers to {@link FunctionDefinition}s. */
    private final Map functionDefinitions = new HashMap();

    /** Is the class operator already defined? */
    private boolean setDefinitionByFormula;


    /**
     * Constructor.
     *
     * @param   globalContext     Module location information.
     * @param   qedeq             BO QEDEQ module object.
     */
    private QedeqBoFormalLogicChecker(final String globalContext, final QedeqBo qedeq) {
        transverser = new QedeqNotNullTransverser(globalContext, this);
        original = qedeq;
    }

    /**
     * Checks if all formulas of a QEDEQ module are well formed.
     *
     * @param   globalContext       Module location information.
     * @param   qedeq               Basic QEDEQ module object.
     * @throws  ModuleDataException      Major problem occured.
     */
    public static void check(final String globalContext, final QedeqBo qedeq)
            throws ModuleDataException {
        final QedeqBoFormalLogicChecker checker = new QedeqBoFormalLogicChecker(globalContext,
            qedeq);
        checker.check();
    }

    private void check() throws ModuleDataException {
        predicateDefinitions.clear();
        functionDefinitions.clear();
        setDefinitionByFormula = false;
        final PredicateDefinitionVo equal = new PredicateDefinitionVo();
        // FIXME mime 20070102: quick hack to have the logical identity operator
        equal.setArgumentNumber("2");
        equal.setName("equal");
        equal.setLatexPattern("#1 \\ =  \\ #2");
        predicateDefinitions.put(equal.getName() + "_" + equal.getArgumentNumber(), equal);
        // FIXME mime 20070102: quick hack to get the negation of the logical identity operator
        final PredicateDefinitionVo notEqual = new PredicateDefinitionVo();
        notEqual.setArgumentNumber("2");
        notEqual.setName("notEqual");
        notEqual.setLatexPattern("#1 \\ \\neq  \\ #2");
        predicateDefinitions.put(notEqual.getName() + "_" + notEqual.getArgumentNumber(), notEqual);
        transverser.accept(original.getQedeq());
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        if (axiom.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = axiom.getFormula();
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), this);
        }
        setLocationWithinModule(context);
        transverser.setBlocked(true);
    }

    public void visitLeave(final Axiom axiom) {
        transverser.setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final String key = definition.getName() + "_" + definition.getArgumentNumber();
        if (predicateDefinitions.get(key) != null) {
            throw new IllegalModuleDataException(HigherLogicalErrors.PREDICATE_ALREADY_DEFINED,
                HigherLogicalErrors.PREDICATE_ALREADY_DEFINED_TEXT, getCurrentContext());
        }
        if (definition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = definition.getFormula();
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), this);
        }
        predicateDefinitions.put(key, definition);
        setLocationWithinModule(context);
        transverser.setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        transverser.setBlocked(false);
    }

    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final String key = definition.getName() + "_" + definition.getArgumentNumber();
        if (functionDefinitions.get(key) != null) {
            throw new IllegalModuleDataException(HigherLogicalErrors.FUNCTION_ALREADY_DEFINED,
                HigherLogicalErrors.FUNCTION_ALREADY_DEFINED_TEXT, getCurrentContext());
        }
        if (definition.getTerm() != null) {
            setLocationWithinModule(context + ".getTerm().getElement()");
            final Term term = definition.getTerm();
            FormulaChecker.checkTerm(term.getElement(), getCurrentContext(), this);
        }
        predicateDefinitions.put(key, definition);
        setLocationWithinModule(context);
        transverser.setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        transverser.setBlocked(false);
    }

    public void visitEnter(final Proposition proposition)
            throws ModuleDataException {
        if (proposition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        if (proposition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = proposition.getFormula();
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), this);
        }
        setLocationWithinModule(context);
        transverser.setBlocked(true);
    }

    public void visitLeave(final Proposition definition) {
        transverser.setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        if (rule == null) {
            return;
        }
        if (rule.getName() != null) {
            if ("SET_DEFINION_BY_FORMULA".equals(rule.getName())) {
                setDefinitionByFormula = true;
            }
        }
        transverser.setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        transverser.setBlocked(false);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    /**
     * Get current context within original.
     *
     * @return  Current context.
     */
    public final ModuleContext getCurrentContext() {
        return transverser.getCurrentContext();
    }

    /**
     * Get original QEDEQ module.
     *
     * @return  Original QEDEQ module.
     */
    protected final Qedeq getQedeqOriginal() {
        return original.getQedeq();
    }

    public boolean predicateExists(final String name, final int arguments) {
        final PredicateDefinition definition = (PredicateDefinition) predicateDefinitions
            .get(name + "_" + arguments);
        return null != definition;
    }

    public boolean functionExists(final String name, final int arguments) {
        final FunctionDefinition definition = (FunctionDefinition) predicateDefinitions
            .get(name + "_" + arguments);
        return null != definition;
    }

    public boolean classOperatorExists() {
        return setDefinitionByFormula;
    }

    public boolean equalityOperatorExists() {
        return true;
    }

    public String getEqualityOperator() {
        return "equal";
    }

}
