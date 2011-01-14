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

package org.qedeq.kernel.bo.service.logic;

import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.FormulaCheckerFactory;
import org.qedeq.kernel.bo.logic.FormulaCheckerFactoryImpl;
import org.qedeq.kernel.bo.logic.FormulaUtility;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.logic.wf.Function;
import org.qedeq.kernel.bo.logic.wf.HigherLogicalErrors;
import org.qedeq.kernel.bo.logic.wf.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.wf.Predicate;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.bo.service.CheckRequiredModuleException;
import org.qedeq.kernel.bo.service.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.service.LoadRequiredModules;
import org.qedeq.kernel.bo.service.ModuleConstantsExistenceChecker;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.IllegalModuleDataException;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.list.ElementSet;


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
    private ModuleConstantsExistenceChecker existence;

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
            return null;
        }
        QedeqLog.getInstance().logRequest(
                "Check logical correctness for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        if (!getQedeqBo().isLoaded()) {
            final String msg = "Check of logical correctness failed for \"" + getQedeqBo().getUrl()
            + "\"";
            QedeqLog.getInstance().logFailureReply(msg, "Module could not even be loaded.");
            return Boolean.FALSE;
        }
        try {
            LoadRequiredModules.loadRequired(getPlugin(), getDefaultKernelQedeqBo());
        } catch (SourceFileExceptionList sfl) {
            // FIXME 20110114 m31: use this? LoadRequired without exception?
        }
        if (!getQedeqBo().hasLoadedRequiredModules()) {
            final String msg = "Check of logical correctness failed for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
            + "\"";
            QedeqLog.getInstance().logFailureReply(msg, "Not all required modules could be loaded.");
            return Boolean.FALSE;
        }
        getDefaultKernelQedeqBo().setLogicalProgressState(LogicalState.STATE_EXTERNAL_CHECKING);
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
                sfl.add(getDefaultKernelQedeqBo().createSourceFileException(getPlugin(), md));
            }
        }
        // has at least one import errors?
        if (sfl.size() > 0) {
            getDefaultKernelQedeqBo().setLogicalFailureState(LogicalState.STATE_EXTERNAL_CHECKING_FAILED, sfl);
            return Boolean.FALSE;
        }
        getDefaultKernelQedeqBo().setLogicalProgressState(LogicalState.STATE_INTERNAL_CHECKING);
        try {
            traverse();
        } catch (SourceFileExceptionList e) {
            getDefaultKernelQedeqBo().setLogicalFailureState(LogicalState.STATE_INTERNAL_CHECKING_FAILED, e);
            return Boolean.FALSE;
        }
        getDefaultKernelQedeqBo().setChecked(existence);
        return Boolean.TRUE;
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

    private DefaultKernelQedeqBo getDefaultKernelQedeqBo() {
        return (DefaultKernelQedeqBo) getQedeqBo();
    }

}
