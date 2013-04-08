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
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.model.CalculateTruth;
import org.qedeq.kernel.bo.logic.model.HeuristicErrorCodes;
import org.qedeq.kernel.bo.logic.model.HeuristicException;
import org.qedeq.kernel.bo.logic.model.Model;
import org.qedeq.kernel.bo.logic.model.ModelFunctionConstant;
import org.qedeq.kernel.bo.logic.model.ModelPredicateConstant;
import org.qedeq.kernel.bo.logic.model.SixDynamicModel;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @author  Michael Meyling
 */
public final class HeuristicCheckerExecutor extends ControlVisitor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = HeuristicCheckerExecutor.class;

    /** Model we use for our heuristic tests. */
    private Model model;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ module object.
     * @param   parameters  Execution parameters.
     */
    HeuristicCheckerExecutor(final PluginBo plugin, final KernelQedeqBo qedeq,
            final Parameters parameters) {
        super(plugin, qedeq);
        final String method = "HeuristicChecker(PluginBo, KernelQedeqBo, Map)";
        final String modelClass = parameters.getString("model");
        if (modelClass != null && modelClass.length() > 0) {
            try {
                Class cl = Class.forName(modelClass);
                model = (Model) cl.newInstance();
            } catch (ClassNotFoundException e) {
                Trace.fatal(CLASS, this, method, "Model class not in class path: "
                    + modelClass, e);
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                Trace.fatal(CLASS, this, method, "Model class could not be instanciated: "
                    + modelClass, e);
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, access for instantiation failed for model: "
                    + modelClass, e);
                throw new RuntimeException(e);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, instantiation failed for model: " + modelClass, e);
                throw new RuntimeException(e);
            }
        }
        // fallback is the default model
        if (model == null) {
            model = new SixDynamicModel();
        }
    }

    public Object executePlugin(final InternalServiceProcess process, final Object data) {
        final String method = "executePlugin)";
        try {
            QedeqLog.getInstance().logRequest("Heuristic test", getQedeqBo().getUrl());
            traverse(process);
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
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(),
                getWarningList());
        }
        return null;
    }

    /**
     * Check truth value in our model. If it is no tautology an warning is added.
     * This also happens if our model doesn't support an operator found in the formula.
     *
     * @param   test    Test formula.
     */
    private void test(final Element test) {
        try {
            if (!CalculateTruth.isTautology(getCurrentContext(), model, test)) {
                addWarning(new HeuristicException(HeuristicErrorCodes.EVALUATED_NOT_TRUE_CODE,
                    HeuristicErrorCodes.EVALUATED_NOT_TRUE_TEXT, getCurrentContext()));
            }
        } catch (HeuristicException e) {
            addWarning(e);
        }
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
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
        final String method = "visitEnter(PredicateDefinition)";
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            ModelPredicateConstant predicate = new ModelPredicateConstant(definition.getName(),
                Integer.parseInt(definition.getArgumentNumber()));
            if (null == model.getPredicateConstant(predicate)) {
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_TEXT + predicate,
                    getCurrentContext()));
            }
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

    public void visitLeave(final InitialPredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        setLocationWithinModule(context + ".getFormula().getElement()");
        test(definition.getFormula().getElement());
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialFunctionDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(FunctionDefinition)";
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            ModelFunctionConstant function = new ModelFunctionConstant(definition.getName(),
                Integer.parseInt(definition.getArgumentNumber()));
            if (null == model.getFunctionConstant(function)) {
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                    HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_TEXT + function,
                    getCurrentContext()));
            }
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

    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(FunctionDefinition)";
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            ModelFunctionConstant function = new ModelFunctionConstant(
                    definition.getName(), Integer.parseInt(definition
                            .getArgumentNumber()));
            if (null == model.getFunctionConstant(function)) {
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                        HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                        HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_TEXT
                                + function, getCurrentContext()));
            } else {
                setLocationWithinModule(context + ".getFormula().getElement()");
                test(definition.getFormula().getElement());
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
            final Element test = proposition.getFormula().getElement();
            test(test);
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final Proposition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        setBlocked(false);
    }

}
