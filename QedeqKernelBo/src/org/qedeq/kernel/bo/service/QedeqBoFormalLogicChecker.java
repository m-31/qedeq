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

package org.qedeq.kernel.bo.service;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.bo.logic.FormulaChecker;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.logic.wf.Function;
import org.qedeq.kernel.bo.logic.wf.HigherLogicalErrors;
import org.qedeq.kernel.bo.logic.wf.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.wf.Predicate;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.IllegalModuleDataException;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.1 $
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
     * @param   plugin  This plugin we work for.
     * @param   prop    QEDEQ module properties object.
     */
    private QedeqBoFormalLogicChecker(final Plugin plugin, final KernelQedeqBo prop) {
        super(plugin, prop);
    }

    /**
     * Checks if all formulas of a QEDEQ module are well formed.
     *
     * @param   prop                QEDEQ module properties object.
     * @throws  SourceFileExceptionList      Major problem occurred.
     */
    public static void check(final DefaultKernelQedeqBo prop)
            throws SourceFileExceptionList {
        if (prop.isChecked()) {
            return;
        }
        if (!prop.hasLoadedRequiredModules()) {
            throw new IllegalStateException("QEDEQ module has not loaded with required files: "
                + prop);
        }
        prop.setLogicalProgressState(LogicalState.STATE_EXTERNAL_CHECKING);
        KernelModuleReferenceList list = (KernelModuleReferenceList) prop.getRequiredModules();
        final QedeqBoFormalLogicChecker checker = new QedeqBoFormalLogicChecker(new Plugin() {

            public String getPluginDescription() {
                return "checks mathematical correctness";
            }

            public String getPluginName() {
                return "Verifier";
            }
        }, prop);
        for (int i = 0; i < list.size(); i++) {
            try {
                Trace.trace(CLASS, "check(DefaultQedeqBo)", "checking label",
                    list.getLabel(i));
                check((DefaultKernelQedeqBo) list.getKernelQedeqBo(i));
            } catch (SourceFileExceptionList e) {   // TODO mime 20080114: hard coded codes
                ModuleDataException md = new CheckRequiredModuleException(11231,
                    "import check failed: " + list.getQedeqBo(i).getModuleAddress(),
                    list.getModuleContext(i));
                final SourceFileExceptionList sfl = prop.createSourceFileExceptionList(checker.getPlugin(), md);
                prop.setLogicalFailureState(LogicalState.STATE_EXTERNAL_CHECKING_FAILED, sfl);
                throw e;
            }
        }
        prop.setLogicalProgressState(LogicalState.STATE_INTERNAL_CHECKING);
        try {
            checker.traverse();
        } catch (SourceFileExceptionList sfl) {
            prop.setLogicalFailureState(LogicalState.STATE_INTERNAL_CHECKING_FAILED, sfl);
            throw sfl;
        }
        prop.setChecked(checker.existence);
    }

    public void traverse() throws DefaultSourceFileExceptionList {
        try {
            this.existence = new ModuleConstantsExistenceChecker(getQedeqBo());
        } catch (ModuleDataException me) {
            addError(me);
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
            LogicalCheckExceptionList list =
                FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
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
        // FIXME mime 20080324: check that no reference (also node references) with same name exist

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
            LogicalCheckExceptionList list =
                FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
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
        // TODO mime 20080324: check that no reference (also node references) with same name exist
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
            LogicalCheckExceptionList list =
                FormulaChecker.checkTerm(term.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
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
            LogicalCheckExceptionList list =
                FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
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
