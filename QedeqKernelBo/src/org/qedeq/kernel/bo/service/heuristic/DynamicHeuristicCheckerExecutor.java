/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.heuristic;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.common.PluginBo;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.bo.logic.model.DynamicDirectInterpreter;
import org.qedeq.kernel.bo.logic.model.DynamicModel;
import org.qedeq.kernel.bo.logic.model.FourDynamicModel;
import org.qedeq.kernel.bo.logic.model.HeuristicErrorCodes;
import org.qedeq.kernel.bo.logic.model.HeuristicException;
import org.qedeq.kernel.bo.logic.model.ModelFunctionConstant;
import org.qedeq.kernel.bo.logic.model.ModelPredicateConstant;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.list.DefaultElementList;


/**
 * Check if formulas are valid in our model.
 *
 * @author  Michael Meyling
 */
public final class DynamicHeuristicCheckerExecutor extends ControlVisitor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = DynamicHeuristicCheckerExecutor.class;

    /** Interpretation for variables. */
    private final DynamicDirectInterpreter interpreter;

    /** Current condition. */
    private DefaultElementList condition;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ module object.
     * @param   parameters  Execution parameters.
     */
    DynamicHeuristicCheckerExecutor(final PluginBo plugin, final KernelQedeqBo qedeq,
            final Parameters parameters) {
        super(plugin, qedeq);
        final String method = "DynamicHeuristicChecker(PluginBo, QedeqBo, Map)";
        final String modelClass = parameters.getString("model");
        DynamicModel model = null;
        if (modelClass != null && modelClass.length() > 0) {
            try {
                Class cl = Class.forName(modelClass);
                model = (DynamicModel) cl.newInstance();
            } catch (ClassNotFoundException e) {
                Trace.fatal(CLASS, this, method, "Model class not in class path: "
                    + modelClass, e);
            } catch (InstantiationException e) {
                Trace.fatal(CLASS, this, method, "Model class could not be instanciated: "
                    + modelClass, e);
            } catch (IllegalAccessException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, access for instantiation failed for model: "
                    + modelClass, e);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, instantiation failed for model: " + modelClass, e);
            }
        }
        // fallback is the default model
        if (model == null) {
            model = new FourDynamicModel();
        }
        this.interpreter = new DynamicDirectInterpreter(qedeq, model);
    }

    public Object executePlugin() {
        final String method = "executePlugin()";
        try {
            QedeqLog.getInstance().logRequest("Dynamic heuristic test", getQedeqBo().getUrl());
            // first we try to get more information about required modules and their predicates..
            try {
                getServices().checkWellFormedness(getQedeqBo().getModuleAddress());
            } catch (Exception e) {
                // we continue and ignore external predicates
                Trace.trace(CLASS, method, e);
            }
            condition = new DefaultElementList(Operators.CONJUNCTION_OPERATOR);
            traverse();
            QedeqLog.getInstance().logSuccessfulReply(
                "Heuristic test succesfull", getQedeqBo().getUrl());
        } catch (final SourceFileExceptionList e) {
            final String msg = "Test failed";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(), e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Test failed", getQedeqBo().getUrl(), "unexpected problem: "
                + (e.getMessage() != null ? e.getMessage() : e.toString()));
        } finally {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        }
        return null;
    }

    /**
     * Check truth value in our model. If it is no tautology an warning is added.
     * This also happens if our model doesn't support an operator found in the formula.
     *
     * @param   test            Test formula.
     */
    private void test(final Element test) {
        boolean useCondition = condition.size() > 0; //Assume that we start with an implication,
                            // but the real context begins after skipping ".getList().getElement(1)"
        try {
            Element toast = test;
            if (condition.size() > 0) {
                final DefaultElementList withCondition = new DefaultElementList(Operators.IMPLICATION_OPERATOR);
                withCondition.add(condition);
                withCondition.add(test);
                toast = withCondition;
            }
            if (!isTautology(getCurrentContext(), toast)) {
                addWarning(new HeuristicException(HeuristicErrorCodes.EVALUATED_NOT_TRUE_CODE,
                    HeuristicErrorCodes.EVALUATED_NOT_TRUE_TEXT + " (\""
                        + interpreter.getModel().getName() + "\")", getCurrentContext()));
            }
        } catch (HeuristicException h) {
            // TODO 20101015 m31: better exception handling would be better!
            final String begin = getCurrentContext().getLocationWithinModule();
            // is the error context at the same location? if not we have a problem with a referenced
            // predicate or function constant and we take the currrent context instead
            if (!getCurrentContext().getModuleLocation().equals(h.getContext().getModuleLocation())
                    || !h.getContext().getLocationWithinModule().startsWith(begin)) {
                addWarning(new HeuristicException(h.getErrorCode(), h.getMessage(),
                        getCurrentContext()));
            } else {
                String further = h.getContext().getLocationWithinModule().substring(begin.length());
                if (useCondition) {
                    if (further.startsWith(".getList().getElement(1)")) {
                        further = further.substring(".getList().getElement(1)".length());
                        addWarning(new HeuristicException(h.getErrorCode(), h.getMessage(),
                            new ModuleContext(h.getContext().getModuleLocation(), begin + further)));
                    } else {    // must be an error in the condition and for that we have no context
                        addWarning(new HeuristicException(h.getErrorCode(), h.getMessage(),
                                getCurrentContext()));
                    }
                } else {
                    addWarning(h);
                }
            }
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, "test(Element)", "unexpected runtime exception", e);
            if (e.getCause() != null && e.getCause() instanceof HeuristicException) {
                // TODO 20101015 m31: better exception handling would be better!
                HeuristicException h = (HeuristicException) e.getCause();
                addWarning(new HeuristicException(h.getErrorCode(), h.getMessage(),
                    getCurrentContext()));
            } else {
                addWarning(new HeuristicException(HeuristicErrorCodes.RUNTIME_EXCEPTION_CODE,
                    HeuristicErrorCodes.RUNTIME_EXCEPTION_TEXT + e, getCurrentContext()));
            }
        }
    }

    /**
     * Test if given formula is a tautology. This is done by checking a model and
     * iterating through variable values.
     *
     * @param   moduleContext   Here we are within a module.
     * @param   formula         Formula.
     * @return  Is this formula a tautology according to our tests.
     * @throws  HeuristicException  Evaluation failed.
     */
    private boolean isTautology(final ModuleContext moduleContext, final Element formula)
            throws HeuristicException {
        boolean result = true;
        ModuleContext context = moduleContext;
        try {
            do {
                result &= interpreter.calculateValue(new ModuleContext(context), formula);
    //            System.out.println(interpreter.toString());
            } while (result && interpreter.next());
//        if (!result) {
//            System.out.println(interpreter);
//        }
//        System.out.println("interpretation finished - and result is = " + result);
        } finally {
            interpreter.clearVariables();
        }
        return result;
    }


    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        QedeqLog.getInstance().logMessageState("\ttesting axiom", getQedeqBo().getUrl());
        if (axiom.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Element test = axiom.getFormula().getElement();
            test(test);
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final Axiom axiom) {
        setBlocked(false);
    }

    public void visitEnter(final InitialPredicateDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(InitialPredicateDefinition)";
        if (definition == null) {
            return;
        }
        QedeqLog.getInstance().logMessageState("\ttesting initial predicate definition",
            getQedeqBo().getUrl());
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            ModelPredicateConstant predicate = new ModelPredicateConstant(
                    definition.getName(), Integer.parseInt(definition
                            .getArgumentNumber()));
            // check if model contains predicate
            if (!interpreter.hasPredicateConstant(predicate)) {
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                        HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                        HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_TEXT
                                + predicate, getCurrentContext()));
            }
        } catch (NumberFormatException e) {
            Trace.fatal(CLASS, this, method, "not suported argument number: "
                    + definition.getArgumentNumber(), e);
            setLocationWithinModule(context + ".getArgumentNumber()");
            addWarning(new HeuristicException(
                    HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_CODE,
                    HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_TEXT
                            + definition.getArgumentNumber(),
                    getCurrentContext()));
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final InitialPredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(PredicateDefinition)";
        if (definition == null) {
            return;
        }
        QedeqLog.getInstance().logMessageState("\ttesting predicate definition",
            getQedeqBo().getUrl());
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            // test new predicate constant: must always be successful otherwise there
            // must be a programming error or the predicate definition is not formal correct
            setLocationWithinModule(context + ".getFormula().getElement()");
            test(definition.getFormula().getElement());
        } catch (NumberFormatException e) {
            Trace.fatal(CLASS, this, method, "not suported argument number: "
                + definition.getArgumentNumber(), e);
            setLocationWithinModule(context + ".getArgumentNumber()");
            addWarning(new HeuristicException(HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_CODE,
                HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_TEXT + definition.getArgumentNumber(),
                getCurrentContext()));
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialFunctionDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(InitialFunctionDefinition)";
        if (definition == null) {
            return;
        }
        QedeqLog.getInstance().logMessageState("\ttesting initial function definition",
            getQedeqBo().getUrl());
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            ModelFunctionConstant function = new ModelFunctionConstant(definition.getName(),
                    Integer.parseInt(definition.getArgumentNumber()));
            if (!interpreter.hasFunctionConstant(function)) {
                // check if model contains predicate
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                        HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                        HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_TEXT
                                + function, getCurrentContext()));
            }
        } catch (NumberFormatException e) {
            Trace.fatal(CLASS, this, method, "not suported argument number: "
                    + definition.getArgumentNumber(), e);
            setLocationWithinModule(context + ".getArgumentNumber()");
            addWarning(new HeuristicException(
                    HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_CODE,
                    HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_TEXT
                            + definition.getArgumentNumber(),
                    getCurrentContext()));
        }
        setLocationWithinModule(context);
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
        QedeqLog.getInstance().logMessageState("\ttesting function definition",
            getQedeqBo().getUrl());
        final String context = getCurrentContext().getLocationWithinModule();
        // test new predicate constant: must always be successful otherwise there
        // must be a programming error or the predicate definition is not formal correct
        setLocationWithinModule(context + ".getFormula().getElement()");
        test(definition.getFormula().getElement());
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Node node) {
        QedeqLog.getInstance().logMessageState(super.getLocationDescription(),
            getQedeqBo().getUrl());
    }

    public void visitEnter(final Proposition proposition)
            throws ModuleDataException {
        if (proposition == null) {
            return;
        }
        QedeqLog.getInstance().logMessageState("\ttesting proposition", getQedeqBo().getUrl());
        final String context = getCurrentContext().getLocationWithinModule();
        if (proposition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Element test = proposition.getFormula().getElement();
            test(test);
        }
        setLocationWithinModule(context);
    }

    public void visitLeave(final Proposition definition) {
    }

    public void visitEnter(final FormalProofLine line)
            throws ModuleDataException {
        if (line == null) {
            return;
        }
        QedeqLog.getInstance().logMessageState("\t\ttesting line " + line.getLabel(),
            getQedeqBo().getUrl());
        final String context = getCurrentContext().getLocationWithinModule();
        if (line.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            test(line.getFormula().getElement());
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final FormalProofLine line) {
        setBlocked(false);
    }

    public void visitEnter(final ConditionalProof line)
            throws ModuleDataException {
        if (line == null) {
            return;
        }
        // add hypothesis to list of conditions
        if (line.getHypothesis() != null && line.getHypothesis().getFormula() != null
                && line.getHypothesis().getFormula().getElement() != null) {
            condition.add(line.getHypothesis().getFormula().getElement());
            QedeqLog.getInstance().logMessageState("\t\tadd condit. "
                + line.getHypothesis().getLabel(), getQedeqBo().getUrl());
        }
    }

    public void visitLeave(final ConditionalProof line) {
        if (line == null) {
            return;
        }
        // remove hypothesis of list of conditions
        if (line.getHypothesis() != null && line.getHypothesis().getFormula() != null
                && line.getHypothesis().getFormula().getElement() != null) {
            condition.remove(condition.size() - 1);
        }
        QedeqLog.getInstance().logMessageState("\t\ttesting line "
            + line.getConclusion().getLabel(), getQedeqBo().getUrl());
        final String context = getCurrentContext().getLocationWithinModule();
        if (line.getConclusion().getFormula() != null) {
            setLocationWithinModule(context + ".getConclusion().getFormula().getElement()");
            final Element test = line.getConclusion().getFormula().getElement();
            test(test);
        }
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        setBlocked(false);
    }

    public String getLocationDescription() {
        return super.getLocationDescription() + "\n" + interpreter.toString();
    }

}
