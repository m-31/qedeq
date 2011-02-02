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

package org.qedeq.kernel.bo.service.logic;

import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.FormulaCheckerFactoryImpl;
import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.logic.common.FormulaCheckerFactory;
import org.qedeq.kernel.bo.logic.common.Function;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.Predicate;
import org.qedeq.kernel.bo.logic.wf.FormulaUtility;
import org.qedeq.kernel.bo.logic.wf.HigherLogicalErrors;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.base.module.Term;
import org.qedeq.kernel.se.base.module.VariableList;
import org.qedeq.kernel.se.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.se.common.IllegalModuleDataException;
import org.qedeq.kernel.se.common.LogicalState;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.list.ElementSet;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 * This plugin assumes all required modules are loaded!
 *
 * @author  Michael Meyling
 */
public final class QedeqBoFormalLogicCheckerExecutor extends ControlVisitor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = QedeqBoFormalLogicCheckerExecutor.class;

    /** Existence checker for predicate and function constants. */
    private ModuleConstantsExistenceCheckerImpl existence;

    /** Factory for generating new checkers. */
    private FormulaCheckerFactory checkerFactory = null;

    /** Parameters for checker. */
    private Map parameters;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ BO object.
     * @param   parameters  Parameters.
     */
    QedeqBoFormalLogicCheckerExecutor(final Plugin plugin, final KernelQedeqBo qedeq,
            final Map parameters) {
        super(plugin, qedeq);
        final String method = "QedeqBoFormalLogicChecker(Plugin, KernelQedeqBo, Map)";
        this.parameters = parameters;
        final String checkerFactoryClass
            = (parameters != null ? (String) parameters.get("checkerFactory") : null);
        if (checkerFactoryClass != null && checkerFactoryClass.length() > 0) {
            try {
                Class cl = Class.forName(checkerFactoryClass);
                checkerFactory = (FormulaCheckerFactory) cl.newInstance();
            } catch (ClassNotFoundException e) {
                Trace.fatal(CLASS, this, method, "FormulaCheckerFactory class not in class path: "
                    + checkerFactoryClass, e);
            } catch (InstantiationException e) {
                Trace.fatal(CLASS, this, method, "FormulaCheckerFactory class could not be instanciated: "
                    + checkerFactoryClass, e);
            } catch (IllegalAccessException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, access for instantiation failed for model: "
                    + checkerFactoryClass, e);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, instantiation failed for model: " + checkerFactoryClass, e);
            }
        }
        // fallback is the default checker factory
        if (checkerFactory == null) {
            checkerFactory = new FormulaCheckerFactoryImpl();
        }
    }

    private Map getParameters() {
        return parameters;
    }

    public Object executePlugin() {
        if (getQedeqBo().isChecked()) {
            return Boolean.TRUE;
        }
        QedeqLog.getInstance().logRequest(
                "Check logical correctness for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        KernelContext.getInstance().loadModule(getQedeqBo().getModuleAddress());
        if (!getQedeqBo().isLoaded()) {
            final String msg = "Check of logical correctness failed for \"" + getQedeqBo().getUrl()
            + "\"";
            QedeqLog.getInstance().logFailureReply(msg, "Module could not even be loaded.");
            return Boolean.FALSE;
        }
        KernelContext.getInstance().loadRequiredModules(getQedeqBo().getModuleAddress());
        if (!getQedeqBo().hasLoadedRequiredModules()) {
            final String msg = "Check of logical correctness failed for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
            + "\"";
            QedeqLog.getInstance().logFailureReply(msg, "Not all required modules could be loaded.");
            return Boolean.FALSE;
        }
        getQedeqBo().setLogicalProgressState(LogicalState.STATE_EXTERNAL_CHECKING);
        final SourceFileExceptionList sfl = new DefaultSourceFileExceptionList();
        KernelModuleReferenceList list = (KernelModuleReferenceList) getQedeqBo().getRequiredModules();
        for (int i = 0; i < list.size(); i++) {
            Trace.trace(CLASS, "check(DefaultQedeqBo)", "checking label", list.getLabel(i));
            final QedeqBoFormalLogicCheckerExecutor checker = new QedeqBoFormalLogicCheckerExecutor(getPlugin(),
                    list.getKernelQedeqBo(i), getParameters());
            checker.executePlugin();
            if (!list.getKernelQedeqBo(i).isChecked()) {
                ModuleDataException md = new CheckRequiredModuleException(
                    HigherLogicalErrors.MODULE_IMPORT_CHECK_FAILED_CODE,
                    HigherLogicalErrors.MODULE_IMPORT_CHECK_FAILED_TEXT
                    + list.getQedeqBo(i).getModuleAddress(),
                    list.getModuleContext(i));
                sfl.add(getQedeqBo().createSourceFileException(getPlugin(), md));
            }
        }
        // has at least one import errors?
        if (sfl.size() > 0) {
            getQedeqBo().setLogicalFailureState(LogicalState.STATE_EXTERNAL_CHECKING_FAILED, sfl);
            final String msg = "Check of logical correctness failed for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
                + "\"";
            QedeqLog.getInstance().logFailureReply(msg, sfl.getMessage());
            return Boolean.FALSE;
        }
        getQedeqBo().setLogicalProgressState(LogicalState.STATE_INTERNAL_CHECKING);
        getQedeqBo().setExistenceChecker(existence);
        try {
            traverse();
        } catch (SourceFileExceptionList e) {
            getQedeqBo().setLogicalFailureState(LogicalState.STATE_INTERNAL_CHECKING_FAILED, e);
            final String msg = "Check of logical correctness failed for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
                + "\"";
            QedeqLog.getInstance().logFailureReply(msg, sfl.getMessage());
            return Boolean.FALSE;
        }
        getQedeqBo().setChecked(existence);
        QedeqLog.getInstance().logSuccessfulReply(
                "Check of logical correctness successful for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        return Boolean.TRUE;
    }

    public void traverse() throws SourceFileExceptionList {
        try {
            this.existence = new ModuleConstantsExistenceCheckerImpl(getQedeqBo());
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
                checkerFactory.createFormulaChecker().checkFormula(
                    formula.getElement(), getCurrentContext(), existence);
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
        // FIXME 20110121 m31: a predicate definition (or function definition) should
        //                    be a simple formula that fulfills certain constraints.
        //                    As there are:
        //                    1. top level is an equivalence relation
        //                    2. first argument is a predicate constant
        //                    3. the predicate constant has only subject variables as
        //                       as arguments
        //                    4. these subject variables are pairwise different from
        //                       each other
        //                    5. the predicate constant doesn't occur in the the second
        //                       top level argument
        //
        //                    Printing these predicate (or function) definition
        //                    inserts an ":" as for TRUE :<-> A v -A
        //                    So printing gets an "definition top level" parameter.
        //
        //                    This should solve some context problems during checking
        //                    wellness or model confirmity
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final Predicate predicate = new Predicate(definition.getName(),
            definition.getArgumentNumber());
        if (existence.predicateExists(predicate)) {
            addError(new IllegalModuleDataException(HigherLogicalErrors.PREDICATE_ALREADY_DEFINED_CODE,
                HigherLogicalErrors.PREDICATE_ALREADY_DEFINED_TEXT + predicate,
                getCurrentContext()));
        }
        if (definition.getFormula() != null) {
            final Formula formula = definition.getFormula();
            final VariableList variableList = definition.getVariableList();
            final int size = (variableList == null ? 0 : variableList.size());
            final ElementSet free = FormulaUtility.getFreeSubjectVariables(formula.getElement());
            for (int i = 0; i < size; i++) {
                setLocationWithinModule(context + ".getVariableList().get(" + i + ")");
                if (!FormulaUtility.isSubjectVariable(variableList.get(i))) {
                    addError(new IllegalModuleDataException(
                        HigherLogicalErrors.MUST_BE_A_SUBJECT_VARIABLE_CODE,
                        HigherLogicalErrors.MUST_BE_A_SUBJECT_VARIABLE_TEXT + variableList.get(i),
                        getCurrentContext()));
                }
                if (!free.contains(variableList.get(i))) {
                    addError(new IllegalModuleDataException(
                        HigherLogicalErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_CODE,
                        HigherLogicalErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_TEXT + variableList.get(i),
                        getCurrentContext()));
                }
            }
            setLocationWithinModule(context);
            if (size != free.size()) {
                addError(new IllegalModuleDataException(
                    HigherLogicalErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_CODE,
                    HigherLogicalErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_TEXT,
                    getCurrentContext()));
            }
            setLocationWithinModule(context + ".getFormula().getElement()");
            LogicalCheckExceptionList list = checkerFactory.createFormulaChecker().checkFormula(
                formula.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
        }
        existence.add(definition);
        if ("2".equals(predicate.getArguments())
                && ExistenceChecker.NAME_EQUAL.equals(predicate.getName())) {
            existence.setIdentityOperatorDefined(predicate.getName(),
                (KernelQedeqBo) getQedeqBo(), getCurrentContext());
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
            addError(new IllegalModuleDataException(HigherLogicalErrors.FUNCTION_ALREADY_DEFINED_CODE,
                HigherLogicalErrors.FUNCTION_ALREADY_DEFINED_TEXT + function,
                getCurrentContext()));
        }
        if (definition.getTerm() != null) {
            final Term term = definition.getTerm();
            final VariableList variableList = definition.getVariableList();
            final int size = (variableList == null ? 0 : variableList.size());
            final ElementSet free = FormulaUtility.getFreeSubjectVariables(term.getElement());
            for (int i = 0; i < size; i++) {
                setLocationWithinModule(context + ".getVariableList().get(" + i + ")");
                if (!FormulaUtility.isSubjectVariable(variableList.get(i))) {
                    addError(new IllegalModuleDataException(
                        HigherLogicalErrors.MUST_BE_A_SUBJECT_VARIABLE_CODE,
                        HigherLogicalErrors.MUST_BE_A_SUBJECT_VARIABLE_TEXT + variableList.get(i),
                        getCurrentContext()));
                }
                if (!free.contains(variableList.get(i))) {
                    addError(new IllegalModuleDataException(
                        HigherLogicalErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_CODE,
                        HigherLogicalErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_TEXT + variableList.get(i),
                        getCurrentContext()));
                }
            }
            setLocationWithinModule(context);
            if (size != free.size()) {
                addError(new IllegalModuleDataException(
                    HigherLogicalErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_CODE,
                    HigherLogicalErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_TEXT,
                    getCurrentContext()));
            }
            setLocationWithinModule(context + ".getTerm().getElement()");
            LogicalCheckExceptionList list = checkerFactory.createFormulaChecker().checkTerm(
                term.getElement(), getCurrentContext(), existence);
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
            LogicalCheckExceptionList list = checkerFactory.createFormulaChecker().checkFormula(
                formula.getElement(), getCurrentContext(), existence);
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
                existence.setClassOperatorModule((KernelQedeqBo) getQedeqBo(),
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
