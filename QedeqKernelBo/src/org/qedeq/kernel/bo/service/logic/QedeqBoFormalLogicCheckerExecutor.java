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
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.FormulaCheckerFactoryImpl;
import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.logic.common.FormulaCheckerFactory;
import org.qedeq.kernel.bo.logic.common.FunctionKey;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.PredicateKey;
import org.qedeq.kernel.bo.logic.wf.FormulaCheckerImpl;
import org.qedeq.kernel.bo.logic.wf.FormulaUtility;
import org.qedeq.kernel.bo.logic.wf.FunctionConstant;
import org.qedeq.kernel.bo.logic.wf.PredicateConstant;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.CheckLevel;
import org.qedeq.kernel.se.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.se.common.IllegalModuleDataException;
import org.qedeq.kernel.se.common.LogicalModuleState;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
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
        getQedeqBo().setLogicalProgressState(LogicalModuleState.STATE_EXTERNAL_CHECKING);
        final SourceFileExceptionList sfl = new DefaultSourceFileExceptionList();
        KernelModuleReferenceList list = (KernelModuleReferenceList) getQedeqBo().getRequiredModules();
        for (int i = 0; i < list.size(); i++) {
            Trace.trace(CLASS, "check(DefaultQedeqBo)", "checking label", list.getLabel(i));
            final QedeqBoFormalLogicCheckerExecutor checker = new QedeqBoFormalLogicCheckerExecutor(getPlugin(),
                    list.getKernelQedeqBo(i), getParameters());
            checker.executePlugin();
            if (!list.getKernelQedeqBo(i).isChecked()) {
                ModuleDataException md = new CheckRequiredModuleException(
                    LogicErrors.MODULE_IMPORT_CHECK_FAILED_CODE,
                    LogicErrors.MODULE_IMPORT_CHECK_FAILED_TEXT
                    + list.getQedeqBo(i).getModuleAddress(),
                    list.getModuleContext(i));
                sfl.add(getQedeqBo().createSourceFileException(getPlugin(), md));
            }
        }
        // has at least one import errors?
        if (sfl.size() > 0) {
            getQedeqBo().setLogicalFailureState(LogicalModuleState.STATE_EXTERNAL_CHECKING_FAILED, sfl);
            final String msg = "Check of logical correctness failed for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
                + "\"";
            QedeqLog.getInstance().logFailureReply(msg, sfl.getMessage());
            return Boolean.FALSE;
        }
        getQedeqBo().setLogicalProgressState(LogicalModuleState.STATE_INTERNAL_CHECKING);
        getQedeqBo().setExistenceChecker(existence);
        try {
            traverse();
        } catch (SourceFileExceptionList e) {
            getQedeqBo().setLogicalFailureState(LogicalModuleState.STATE_INTERNAL_CHECKING_FAILED, e);
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
        // we start checking
        getNodeBo().setWellFormed(CheckLevel.UNCHECKED);
        if (axiom.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = axiom.getFormula();
            LogicalCheckExceptionList list =
                checkerFactory.createFormulaChecker().checkFormula(
                    formula.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
        } else {
            getNodeBo().setWellFormed(CheckLevel.FAILURE);
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
        // we start checking
        getNodeBo().setWellFormed(CheckLevel.UNCHECKED);
        final PredicateKey predicateKey = new PredicateKey(definition.getName(),
            definition.getArgumentNumber());
        // we misuse a do loop to be able to break
        do {
            if (existence.predicateExists(predicateKey)) {
                addError(new IllegalModuleDataException(
                    LogicErrors.PREDICATE_ALREADY_DEFINED_CODE,
                    LogicErrors.PREDICATE_ALREADY_DEFINED_TEXT + predicateKey,
                    getCurrentContext()));
                break;
            }
            if (definition.getFormula() == null) {
                addError(new IllegalModuleDataException(
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_CODE,
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_TEXT,
                    getCurrentContext()));
                break;
            }
            final Element completeFormula = definition.getFormula().getElement();
            if (completeFormula == null) {
                addError(new IllegalModuleDataException(
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_CODE,
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_TEXT,
                    getCurrentContext()));
                break;
            }
            setLocationWithinModule(context + ".getFormula().getElement()");
            if (completeFormula.isAtom()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_CODE,
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_TEXT,
                    getCurrentContext()));
                break;
            }
            final ElementList equi = completeFormula.getList();
            final String operator = equi.getOperator();
            if (!operator.equals(FormulaCheckerImpl.EQUIVALENCE_OPERATOR)
                    || equi.size() != 2) {
                addError(new IllegalModuleDataException(
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_CODE,
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_TEXT,
                    getCurrentContext()));
                break;
            }
            setLocationWithinModule(context + ".getFormula().getElement().getList().getElement(0)");
            if (equi.getElement(0).isAtom()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_PREDICATE_CONSTANT_CODE,
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_PREDICATE_CONSTANT_TEXT,
                    getCurrentContext()));
                break;
            }
            final ElementList predicate =  equi.getElement(0).getList();
            if (predicate.getOperator() != FormulaCheckerImpl.PREDICATE_CONSTANT) {
                addError(new IllegalModuleDataException(
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_PREDICATE_CONSTANT_CODE,
                    LogicErrors.PREDICATE_DEFINITION_NEEDS_PREDICATE_CONSTANT_TEXT,
                    getCurrentContext()));
                break;
            }
            final Element definingFormula =  equi.getElement(1);

            final ElementSet free = FormulaUtility.getFreeSubjectVariables(definingFormula);
            for (int i = 0; i < predicate.size(); i++) {
                setLocationWithinModule(context
                    + ".getFormula().getElement().getList().getElement(0).getList().getElement(" + i + ")");
                if (i == 0) {
                    if (!predicate.getElement(0).isAtom()
                            || !EqualsUtility.equals(definition.getName(),
                            predicate.getElement(0).getAtom().getString())) {
                        addError(new IllegalModuleDataException(
                            LogicErrors.MUST_HAVE_NAME_OF_PREDICATE_CODE,
                            LogicErrors.MUST_HAVE_NAME_OF_PREDICATE_TEXT
                            + StringUtility.quote(definition.getName()) + " - "
                            + StringUtility.quote(predicate.getElement(0).getAtom().getString()),
                            getCurrentContext()));
                        continue;
                    }
                } else if (!FormulaUtility.isSubjectVariable(predicate.getElement(i))) {
                    addError(new IllegalModuleDataException(
                        LogicErrors.MUST_BE_A_SUBJECT_VARIABLE_CODE,
                        LogicErrors.MUST_BE_A_SUBJECT_VARIABLE_TEXT + predicate.getElement(i),
                        getCurrentContext()));
                    continue;
                }
                setLocationWithinModule(context
                        + ".getFormula().getElement().getList().getElement(1)");
                if (i != 0 && !free.contains(predicate.getElement(i))) {
                    addError(new IllegalModuleDataException(
                        LogicErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_CODE,
                        LogicErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_TEXT + predicate.getElement(i),
                        getCurrentContext()));
                }
            }
            setLocationWithinModule(context + ".getFormula().getElement()");
            if (predicate.size() - 1 != free.size()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_CODE,
                    LogicErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_TEXT,
                    getCurrentContext()));
            }
            setLocationWithinModule(context + ".getFormula().getElement().getList().getElement(1)");
            final LogicalCheckExceptionList list = checkerFactory.createFormulaChecker().checkFormula(
                definingFormula, getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
            if (list.size() > 0) {
                break;
            }
            setLocationWithinModule(context + ".getFormula().getElement().getList()");
            final PredicateConstant constant = new PredicateConstant(predicateKey,
                 equi, getCurrentContext());
            setLocationWithinModule(context);
            if (existence.predicateExists(predicateKey)) {
                addError(new IllegalModuleDataException(
                    LogicErrors.PREDICATE_ALREADY_DEFINED_CODE,
                    LogicErrors.PREDICATE_ALREADY_DEFINED_TEXT
                            + predicateKey, getCurrentContext()));
                break;
            }
            existence.add(constant);
            // a final check, we don't expect any errors here, but hey - we want to be very sure!
            setLocationWithinModule(context + ".getFormula().getElement().getList()");
            final LogicalCheckExceptionList errorlist = checkerFactory.createFormulaChecker()
                .checkFormula(completeFormula, getCurrentContext(), existence);
            for (int i = 0; i < errorlist.size(); i++) {
                addError(errorlist.get(i));
            }
        } while (false);

        // check if we just found the identity operator
        setLocationWithinModule(context);
        if ("2".equals(predicateKey.getArguments())
                && ExistenceChecker.NAME_EQUAL.equals(predicateKey.getName())) {
            existence.setIdentityOperatorDefined(predicateKey.getName(),
                (KernelQedeqBo) getQedeqBo(), getCurrentContext());
        }
        // if we found no errors this node is ok
        if (!getNodeBo().isNotWellFormed()) {
            getNodeBo().setWellFormed(CheckLevel.SUCCESS);
        }
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialPredicateDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        // we start checking
        getNodeBo().setWellFormed(CheckLevel.UNCHECKED);
        final PredicateKey predicateKey = new PredicateKey(
                definition.getName(), definition.getArgumentNumber());
        setLocationWithinModule(context);
        if (existence.predicateExists(predicateKey)) {
            addError(new IllegalModuleDataException(
                LogicErrors.PREDICATE_ALREADY_DEFINED_CODE,
                LogicErrors.PREDICATE_ALREADY_DEFINED_TEXT
                        + predicateKey, getCurrentContext()));
        }
        existence.add(definition);
        // check if we just found the identity operator
        if ("2".equals(predicateKey.getArguments())
                && ExistenceChecker.NAME_EQUAL.equals(predicateKey.getName())) {
            existence.setIdentityOperatorDefined(predicateKey.getName(),
                    (KernelQedeqBo) getQedeqBo(), getCurrentContext());
        }
        // if we found no errors this node is ok
        if (!getNodeBo().isNotWellFormed()) {
            getNodeBo().setWellFormed(CheckLevel.SUCCESS);
        }
        setBlocked(true);
    }

    public void visitLeave(final InitialPredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialFunctionDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        // we start checking
        getNodeBo().setWellFormed(CheckLevel.UNCHECKED);
        final FunctionKey function = new FunctionKey(definition.getName(),
            definition.getArgumentNumber());
        if (existence.functionExists(function)) {
            addError(new IllegalModuleDataException(
                LogicErrors.FUNCTION_ALREADY_DEFINED_CODE,
                LogicErrors.FUNCTION_ALREADY_DEFINED_TEXT + function,
                getCurrentContext()));
        }
        existence.add(definition);
        setLocationWithinModule(context);
        // if we found no errors this node is ok
        if (!getNodeBo().isNotWellFormed()) {
            getNodeBo().setWellFormed(CheckLevel.SUCCESS);
        }
        setBlocked(true);
    }

    public void visitLeave(final InitialFunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        // we start checking
        getNodeBo().setWellFormed(CheckLevel.UNCHECKED);
        final FunctionKey function = new FunctionKey(definition.getName(),
                definition.getArgumentNumber());
        // we misuse a do loop to be able to break
        do {
            if (existence.functionExists(function)) {
                addError(new IllegalModuleDataException(
                    LogicErrors.FUNCTION_ALREADY_DEFINED_CODE,
                    LogicErrors.FUNCTION_ALREADY_DEFINED_TEXT
                        + function, getCurrentContext()));
                break;
            }
            if (definition.getFormula() == null) {
                addError(new IllegalModuleDataException(
                    LogicErrors.NO_DEFINITION_FORMULA_FOR_FUNCTION_CODE,
                    LogicErrors.NO_DEFINITION_FORMULA_FOR_FUNCTION_TEXT,
                    getCurrentContext()));
                break;
            }
            final Formula formulaArgument = definition.getFormula();
            setLocationWithinModule(context + ".getFormula()");
            if (formulaArgument.getElement() == null || formulaArgument.getElement().isAtom()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.NO_DEFINITION_FORMULA_FOR_FUNCTION_CODE,
                    LogicErrors.NO_DEFINITION_FORMULA_FOR_FUNCTION_TEXT,
                    getCurrentContext()));
                break;
            }
            final ElementList formula = formulaArgument.getElement().getList();
            setLocationWithinModule(context + ".getFormula().getElement().getList()");
            if (!existence.identityOperatorExists()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.IDENTITY_OPERATOR_MUST_BE_DEFINED_FIRST_CODE,
                    LogicErrors.IDENTITY_OPERATOR_MUST_BE_DEFINED_FIRST_TEXT,
                    getCurrentContext()));
                break;
            }
            if (!FormulaCheckerImpl.PREDICATE_CONSTANT.equals(formula.getOperator())) {
                addError(new IllegalModuleDataException(
                    LogicErrors.DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_CODE,
                    LogicErrors.DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_TEXT,
                    getCurrentContext()));
                break;
            }
            if (formula.size() != 3) {
                addError(new IllegalModuleDataException(
                    LogicErrors.DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_CODE,
                    LogicErrors.DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_TEXT,
                    getCurrentContext()));
                break;
            }
            if (!formula.getElement(0).isAtom()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_CODE,
                    LogicErrors.DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_TEXT,
                    getCurrentContext()));
                break;
            }
            if (!EqualsUtility.equals(existence.getIdentityOperator(),
                    formula.getElement(0).getAtom().getString())) {
                addError(new IllegalModuleDataException(
                    LogicErrors.DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_CODE,
                    LogicErrors.DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_TEXT,
                    getCurrentContext()));
                break;
            }
            setLocationWithinModule(context + ".getFormula().getElement().getList().getElement(1)");
            if (formula.getElement(1).isAtom()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_CODE,
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_TEXT,
                    getCurrentContext()));
                break;
            }
            final ElementList functionConstant = formula.getElement(1).getList();
            if (!FormulaCheckerImpl.FUNCTION_CONSTANT.equals(functionConstant.getOperator())) {
                addError(new IllegalModuleDataException(
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_CODE,
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_TEXT,
                    getCurrentContext()));
                break;
            }
            setLocationWithinModule(context
                + ".getFormula().getElement().getList().getElement(1).getList()");
            final int size = functionConstant.size();
            if (!("" + (size - 1)).equals(definition.getArgumentNumber())) {
                addError(new IllegalModuleDataException(
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_CODE,
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_TEXT,
                    getCurrentContext()));
                break;
            }
            setLocationWithinModule(context
                + ".getFormula().getElement().getList().getElement(1).getList().getElement(0)");
            if (!functionConstant.getElement(0).isAtom()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_CODE,
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_TEXT,
                    getCurrentContext()));
                break;
            }
            if (!EqualsUtility.equals(definition.getName(),
                    functionConstant.getElement(0).getAtom().getString())) {
                addError(new IllegalModuleDataException(
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_CODE,
                    LogicErrors.FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_TEXT,
                    getCurrentContext()));
                break;
            }
            setLocationWithinModule(context + ".getFormula().getElement().getList().getElement(2)");
            if (formula.getElement(2).isAtom()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.SECOND_OPERAND_MUST_BE_A_TERM_CODE,
                    LogicErrors.SECOND_OPERAND_MUST_BE_A_TERM_TEXT,
                    getCurrentContext()));
                break;
            }
            final ElementList term = formula.getElement(2).getList();
            setLocationWithinModule(context + ".getFormula().getElement().getList().getElement(1)");
            final ElementSet free = FormulaUtility.getFreeSubjectVariables(term);
            if (size - 1 != free.size()) {
                addError(new IllegalModuleDataException(
                    LogicErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_CODE,
                    LogicErrors.NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_TEXT,
                    getCurrentContext()));
                break;
            }
            if (functionConstant.getElement(0).isList()
                    || !EqualsUtility.equals(function.getName(),
                    functionConstant.getElement(0).getAtom().getString())) {
            }
            for (int i = 1; i < size; i++) {
                setLocationWithinModule(context + ".getFormula().getElement().getList().getElement(1)"
                    + ".getList().get(" + i + ")");
                if (!FormulaUtility.isSubjectVariable(functionConstant.getElement(i))) {
                    addError(new IllegalModuleDataException(
                        LogicErrors.MUST_BE_A_SUBJECT_VARIABLE_CODE,
                        LogicErrors.MUST_BE_A_SUBJECT_VARIABLE_TEXT
                            + functionConstant.getElement(i), getCurrentContext()));
                }
                if (!free.contains(functionConstant.getElement(i))) {
                    addError(new IllegalModuleDataException(
                        LogicErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_CODE,
                        LogicErrors.SUBJECT_VARIABLE_OCCURS_NOT_FREE_TEXT
                            + functionConstant.getElement(i), getCurrentContext()));
                }
            }
            setLocationWithinModule(context + ".getFormula().getElement().getList().getElement(2)");
            final LogicalCheckExceptionList list = checkerFactory.createFormulaChecker()
                .checkTerm(term, getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
            if (list.size() > 0) {
                break;
            }
            setLocationWithinModule(context + ".getFormula().getElement().getList()");
            existence.add(new FunctionConstant(function, formula, getCurrentContext()));
            // a final check, we don't expect any errors here, but hey - we want to be very sure!
            final LogicalCheckExceptionList listComplete = checkerFactory.createFormulaChecker()
                .checkFormula(formulaArgument.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < listComplete.size(); i++) {
                addError(listComplete.get(i));
            }
        } while (false);
        setLocationWithinModule(context);
        // if we found no errors this node is ok
        if (!getNodeBo().isNotWellFormed()) {
            getNodeBo().setWellFormed(CheckLevel.SUCCESS);
        }
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
        // we start checking
        getNodeBo().setWellFormed(CheckLevel.UNCHECKED);
        if (proposition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = proposition.getFormula();
            LogicalCheckExceptionList list = checkerFactory.createFormulaChecker().checkFormula(
                formula.getElement(), getCurrentContext(), existence);
            for (int i = 0; i < list.size(); i++) {
                addError(list.get(i));
            }
        }
        if (proposition.getFormalProofList() != null) {
            for (int i = 0; i < proposition.getFormalProofList().size(); i++) {
                final FormalProof proof = proposition.getFormalProofList().get(i);
                if (proof != null) {
                    final FormalProofLineList list = proof.getFormalProofLineList();
                    if (list != null) {
                        for (int j = 0; j < list.size(); j++) {
                            final FormalProofLine line = list.get(j);
                            if (line != null) {
                                final Formula formula = line.getFormula();
                                if (formula != null) {
                                    setLocationWithinModule(context + ".getFormalProofList().get("
                                       + i + ").getFormalProofLineList().get(" + j
                                       + ").getFormula().getElement()");
                                    LogicalCheckExceptionList elist
                                        = checkerFactory.createFormulaChecker().checkFormula(
                                        formula.getElement(), getCurrentContext(), existence);
                                    for (int k = 0; k < elist.size(); k++) {
                                        addError(elist.get(k));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {  // no formula
            getNodeBo().setWellFormed(CheckLevel.FAILURE);
        }
        setLocationWithinModule(context);
        // if we found no errors this node is ok
        if (!getNodeBo().isNotWellFormed()) {
            getNodeBo().setWellFormed(CheckLevel.SUCCESS);
        }
        setBlocked(true);
    }

    public void visitLeave(final Proposition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        if (rule == null) {
            return;
        }
        // we start checking
        getNodeBo().setWellFormed(CheckLevel.UNCHECKED);
        if (rule.getName() != null) {
            if ("SET_DEFINION_BY_FORMULA".equals(rule.getName())) {
                // TODO mime 20080114: check if this rule can be proposed
                existence.setClassOperatorModule((KernelQedeqBo) getQedeqBo(),
                    getCurrentContext());
            }
        } else {
            getNodeBo().setWellFormed(CheckLevel.FAILURE);
        }
        // if we found no errors this node is ok
        if (!getNodeBo().isNotWellFormed()) {
            getNodeBo().setWellFormed(CheckLevel.SUCCESS);
        }
        setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        setBlocked(false);
    }

    protected void addError(final ModuleDataException me) {
        if (getNodeBo() != null) {
            getNodeBo().setWellFormed(CheckLevel.FAILURE);
        }
        super.addError(me);
    }

    protected void addError(final SourceFileException me) {
        if (getNodeBo() != null) {
            getNodeBo().setWellFormed(CheckLevel.FAILURE);
        }
        super.addError(me);
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
