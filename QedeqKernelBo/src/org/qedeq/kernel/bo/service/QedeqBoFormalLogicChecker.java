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
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.bo.logic.FormulaChecker;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.logic.wf.Function;
import org.qedeq.kernel.bo.logic.wf.HigherLogicalErrors;
import org.qedeq.kernel.bo.logic.wf.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.wf.Predicate;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.IllegalModuleDataException;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.list.ElementSet;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class QedeqBoFormalLogicChecker extends ControlVisitor implements Plugin {

    /** This class. */
    private static final Class CLASS = QedeqBoFormalLogicChecker.class;

    /** Existence checker for predicate and function constants. */
    private ModuleConstantsExistenceChecker existence;

    /**
     * Constructor.
     *
     * @param   prop    QEDEQ module properties object.
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
        final QedeqBoFormalLogicChecker checker = new QedeqBoFormalLogicChecker(prop);
        for (int i = 0; i < list.size(); i++) {
            try {
                Trace.trace(CLASS, "check(DefaultQedeqBo)", "checking label", list.getLabel(i));
                check((DefaultKernelQedeqBo) list.getKernelQedeqBo(i));
            } catch (SourceFileExceptionList e) {   // TODO mime 20080114: hard coded codes
                ModuleDataException md = new CheckRequiredModuleException(
                    HigherLogicalErrors.MODULE_IMPORT_CHECK_FAILED_CODE,
                    HigherLogicalErrors.MODULE_IMPORT_CHECK_FAILED_MSG
                    + list.getQedeqBo(i).getModuleAddress(),
                    list.getModuleContext(i));
                final SourceFileExceptionList sfl = prop.createSourceFileExceptionList(checker,
                    md);
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

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginName() {
        return "Verifier";
    }

    public String getPluginDescription() {
        return "checks mathematical correctness";
    }

    public void traverse() throws SourceFileExceptionList {
        try {
            this.existence = new ModuleConstantsExistenceChecker(getQedeqBo());
        } catch (ModuleDataException me) {
            addError(me);
            throw getErrorList();
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
        final String context = getCurrentContext().getLocationWithinModule();
        final Predicate predicate = new Predicate(definition.getName(),
            definition.getArgumentNumber());
        if (existence.predicateExists(predicate)) {
            addError(new IllegalModuleDataException(HigherLogicalErrors.PREDICATE_ALREADY_DEFINED,
                HigherLogicalErrors.PREDICATE_ALREADY_DEFINED_TEXT + predicate,
                getCurrentContext()));
        }
        if (definition.getFormula() != null) {
            final Formula formula = definition.getFormula();
            final VariableList variableList = definition.getVariableList();
            final int size = (variableList == null ? 0 : variableList.size());
            final ElementSet free = FormulaChecker.getFreeSubjectVariables(formula.getElement());
            for (int i = 0; i < size; i++) {
                setLocationWithinModule(context + ".getVariableList().get(" + i + ")");
                if (!FormulaChecker.isSubjectVariable(variableList.get(i))) {
                    addError(new IllegalModuleDataException(
                        HigherLogicalErrors.MUST_BE_A_SUBJECT_VARIABLE_CODE,
                        HigherLogicalErrors.MUST_BE_A_SUBJECT_VARIABLE_MSG + variableList.get(i),
                        getCurrentContext()));
                }
                if (!free.contains(variableList.get(i))) {
                    addError(new IllegalModuleDataException(
                        HigherLogicalErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_CODE,
                        HigherLogicalErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_MSG + variableList.get(i),
                        getCurrentContext()));
                }
            }
            setLocationWithinModule(context);
            if (size != free.size()) {
                addError(new IllegalModuleDataException(
                    HigherLogicalErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_CODE,
                    HigherLogicalErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_MSG,
                    getCurrentContext()));
            }
            setLocationWithinModule(context + ".getFormula().getElement()");
            LogicalCheckExceptionList list =
                FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
        }
        existence.add(definition);
        if ("2".equals(predicate.getArguments())
                && ExistenceChecker.NAME_EQUAL.equals(predicate.getName())) {
            existence.setIdentityOperatorDefined(predicate.getName(),
                (DefaultKernelQedeqBo) getQedeqBo(), getCurrentContext());
        }
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
            addError(new IllegalModuleDataException(HigherLogicalErrors.FUNCTION_ALREADY_DEFINED,
                HigherLogicalErrors.FUNCTION_ALREADY_DEFINED_TEXT + function,
                getCurrentContext()));
        }
        if (definition.getTerm() != null) {
            final Term term = definition.getTerm();
            final VariableList variableList = definition.getVariableList();
            final int size = (variableList == null ? 0 : variableList.size());
            final ElementSet free = FormulaChecker.getFreeSubjectVariables(term.getElement());
            for (int i = 0; i < size; i++) {
                setLocationWithinModule(context + ".getVariableList().get(" + i + ")");
                if (!FormulaChecker.isSubjectVariable(variableList.get(i))) {
                    addError(new IllegalModuleDataException(
                        HigherLogicalErrors.MUST_BE_A_SUBJECT_VARIABLE_CODE,
                        HigherLogicalErrors.MUST_BE_A_SUBJECT_VARIABLE_MSG + variableList.get(i),
                        getCurrentContext()));
                }
                if (!free.contains(variableList.get(i))) {
                    addError(new IllegalModuleDataException(
                        HigherLogicalErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_CODE,
                        HigherLogicalErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_MSG + variableList.get(i),
                        getCurrentContext()));
                }
            }
            setLocationWithinModule(context);
            if (size != free.size()) {
                addError(new IllegalModuleDataException(
                    HigherLogicalErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_CODE,
                    HigherLogicalErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_MSG,
                    getCurrentContext()));
            }
            setLocationWithinModule(context + ".getTerm().getElement()");
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
                existence.setClassOperatorModule((DefaultKernelQedeqBo) getQedeqBo(),
                    getCurrentContext());
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
