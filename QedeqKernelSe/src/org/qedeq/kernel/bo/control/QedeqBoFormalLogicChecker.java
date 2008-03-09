/* $Id: QedeqBoFormalLogicChecker.java,v 1.6 2008/01/26 12:39:09 m31 Exp $
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

import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.bo.logic.ExistenceChecker;
import org.qedeq.kernel.bo.logic.FormulaChecker;
import org.qedeq.kernel.bo.logic.Function;
import org.qedeq.kernel.bo.logic.Predicate;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.IllegalModuleDataException;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public final class QedeqBoFormalLogicChecker extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = QedeqBoFormalLogicChecker.class;

    /** Existence checker for predicate and function constants. */
    private ModuleConstantsExistenceChecker existence;

    /**
     * Constructor.
     *
     * @param   prop              QEDEQ module properties object.
     */
    private QedeqBoFormalLogicChecker(final KernelQedeqBo prop) {
        super(prop);
    }

    /**
     * Checks if all formulas of a QEDEQ module are well formed.
     *
     * @param   prop                QEDEQ module properties object.
     * @throws  SourceFileExceptionList      Major problem occurred.
     */
    public static void check(final KernelQedeqBo prop)
            throws SourceFileExceptionList {
        if (prop.isChecked()) {
            return;
        }
        if (!prop.hasLoadedRequiredModules()) {
            throw new IllegalStateException("QEDEQ module has not loaded with required files: "
                + prop);
        }
        prop.setLogicalProgressState(LogicalState.STATE_EXTERNAL_CHECKING);
        DefaultModuleReferenceList list = (DefaultModuleReferenceList) prop.getRequiredModules();
        for (int i = 0; i < list.size(); i++) {
            try {
                Trace.trace(CLASS, "check(DefaultQedeqBo)", "checking label",
                    list.getLabel(i));
                check((KernelQedeqBo) list.getQedeqBo(i));
            } catch (SourceFileExceptionList e) {   // TODO mime 20080114: hard coded codes
                ModuleDataException md = new CheckRequiredModuleException(11231,
                    "import check failed: " + list.getQedeqBo(i).getModuleAddress(),
                    list.getModuleContext(i));
                final SourceFileExceptionList sfl = prop.createSourceFileExceptionList(md);
                prop.setLogicalFailureState(LogicalState.STATE_EXTERNAL_CHECKING_FAILED, sfl);
                throw e;
            }
        }
        prop.setLogicalProgressState(LogicalState.STATE_INTERNAL_CHECKING);
        final QedeqBoFormalLogicChecker checker = new QedeqBoFormalLogicChecker(prop);
        try {
            checker.traverse();
        } catch (SourceFileExceptionList sfl) {
            prop.setLogicalFailureState(LogicalState.STATE_INTERNAL_CHECKING_FAILED, sfl);
            throw sfl;
        }
        prop.setChecked(checker.existence);
    }

    protected void traverse() throws DefaultSourceFileExceptionList {
        try {
            this.existence = new ModuleConstantsExistenceChecker(getQedeqBo());
        } catch (ModuleDataException me) {
            addModuleDataException(me);
            throw getSourceFileExceptionList();
        }
        super.traverse();
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        if (axiom.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = axiom.getFormula();
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final Axiom axiom) {
        setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final Predicate predicate = new Predicate(definition.getName(),
            definition.getArgumentNumber());
        if (existence.predicateExists(predicate)) {
            throw new IllegalModuleDataException(HigherLogicalErrors.PREDICATE_ALREADY_DEFINED,
                HigherLogicalErrors.PREDICATE_ALREADY_DEFINED_TEXT + predicate,
                getCurrentContext());
        }
        if ("2".equals(predicate.getArguments())
                && ExistenceChecker.NAME_EQUAL.equals(predicate.getName())) {
            existence.setIdentityOperatorDefined(true, predicate.getName());
        }
        if (definition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = definition.getFormula();
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
        }
        existence.add(definition);
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final Function function = new Function(definition.getName(),
            definition.getArgumentNumber());
        if (existence.functionExists(function)) {
            throw new IllegalModuleDataException(HigherLogicalErrors.FUNCTION_ALREADY_DEFINED,
                HigherLogicalErrors.FUNCTION_ALREADY_DEFINED_TEXT + function,
                getCurrentContext());
        }
        if (definition.getTerm() != null) {
            setLocationWithinModule(context + ".getTerm().getElement()");
            final Term term = definition.getTerm();
            FormulaChecker.checkTerm(term.getElement(), getCurrentContext(), existence);
        }
        existence.add(definition);
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        setBlocked(false);
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
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final Proposition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        if (rule == null) {
            return;
        }
        if (rule.getName() != null) {
            if ("SET_DEFINION_BY_FORMULA".equals(rule.getName())) {
                // LATER mime 20080114: check if this rule can be proposed
                existence.setClassOperatorExists(true);
            }
        }
        setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        setBlocked(false);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

}
