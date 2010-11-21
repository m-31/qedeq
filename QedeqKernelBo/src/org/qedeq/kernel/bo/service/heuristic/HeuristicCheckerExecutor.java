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

package org.qedeq.kernel.bo.service.heuristic;

import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.model.CalculateTruth;
import org.qedeq.kernel.bo.logic.model.FunctionConstant;
import org.qedeq.kernel.bo.logic.model.HeuristicErrorCodes;
import org.qedeq.kernel.bo.logic.model.HeuristicException;
import org.qedeq.kernel.bo.logic.model.Model;
import org.qedeq.kernel.bo.logic.model.PredicateConstant;
import org.qedeq.kernel.bo.logic.model.ThreeDynamicModelOne;
import org.qedeq.kernel.bo.logic.wf.Operators;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.list.DefaultAtom;
import org.qedeq.kernel.dto.list.DefaultElementList;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.1 $
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
    HeuristicCheckerExecutor(final PluginBo plugin, final KernelQedeqBo qedeq, final Map parameters) {
        super(plugin, qedeq);
        final String method = "HeuristicChecker(PluginBo, KernelQedeqBo, Map)";
        final String modelClass = (parameters != null ? (String) parameters.get("model") : null);
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
            model = new ThreeDynamicModelOne();
        }
    }

    public Object executePlugin() {
        final String method = "executePlugin)";
        final String ref = "\"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"";
        try {
            QedeqLog.getInstance().logRequest("Heuristic test for " + ref);
            traverse();
            QedeqLog.getInstance().logSuccessfulReply(
                "Heuristic test succesfull for " + ref);
        } catch (final SourceFileExceptionList e) {
            final String msg = "Test failed for " + ref;
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Test failed for " + ref, "unexpected problem: "
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
                    HeuristicErrorCodes.EVALUATED_NOT_TRUE_MSG, getCurrentContext()));
            }
        } catch (HeuristicException e) {
            addWarning(e);
        }
    }

    /**
     * Check truth value in our model. If it is no tautology an warning is added.
     * This also happens if our model doesn't support an operator found in the formula.
     *
     * @param   context Mark this local context if it is no tautology.
     * @param   test    Test formula.
     */
    private void test(final String context, final Element test) {
        try {
            if (!CalculateTruth.isTautology(getCurrentContext(), model, test)) {
                setLocationWithinModule(context);
                addWarning(new HeuristicException(HeuristicErrorCodes.EVALUATED_NOT_TRUE_CODE,
                    HeuristicErrorCodes.EVALUATED_NOT_TRUE_MSG, getCurrentContext()));
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

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(PredicateDefinition)";
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            PredicateConstant predicate = new PredicateConstant(definition.getName(),
                Integer.parseInt(definition.getArgumentNumber()));
            if (null == model.getPredicateConstant(predicate)) {
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_MSG + predicate,
                    getCurrentContext()));
            } else if (definition.getFormula() != null) {
                setLocationWithinModule(context + ".getFormula().getElement()");
                final VariableList variableList = definition.getVariableList();
                final int size = (variableList == null ? 0 : variableList.size());
                final Element[] elements = new Element[size + 1];
                elements[0] = new DefaultAtom(definition.getName());
                for (int i = 0; i < size; i++) {
                    elements[i + 1]  = variableList.get(i);
                }
                final Element left = new DefaultElementList(Operators.PREDICATE_CONSTANT,
                   elements);
                final Element[] compare = new Element[2];
                compare[0] = left;
                compare[1] = definition.getFormula().getElement();
                test(context, new DefaultElementList(Operators.EQUIVALENCE_OPERATOR, compare));
            }
        } catch (NumberFormatException e) {
            Trace.fatal(CLASS, this, method, "not suported argument number: "
                + definition.getArgumentNumber(), e);
            setLocationWithinModule(context + ".getArgumentNumber()");
            addWarning(new HeuristicException(HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_CODE,
                HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_MSG + definition.getArgumentNumber(),
                getCurrentContext()));
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }


    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(FunctionDefinition)";
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            FunctionConstant function = new FunctionConstant(definition.getName(),
                Integer.parseInt(definition.getArgumentNumber()));
            if (null == model.getFunctionConstant(function)) {
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                    HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_MSG + function,
                    getCurrentContext()));
            } else if (definition.getTerm() != null) {
                setLocationWithinModule(context + ".getTerm().getElement()");
                final VariableList variableList = definition.getVariableList();
                final int size = (variableList == null ? 0 : variableList.size());
                final Element[] elements = new Element[size + 1];
                elements[0] = new DefaultAtom(definition.getName());
                for (int i = 0; i < size; i++) {
                    elements[i + 1]  = variableList.get(i);
                }
                final Element left = new DefaultElementList(Operators.FUNCTION_CONSTANT,
                   elements);
                final Element[] equal = new Element[3];
                equal[0] = new DefaultAtom("equal");
                equal[1] = left;
                equal[2] = definition.getTerm().getElement();
                test(context, new DefaultElementList(Operators.PREDICATE_CONSTANT, equal));
            }
        } catch (NumberFormatException e) {
            Trace.fatal(CLASS, this, method, "not suported argument number: "
                + definition.getArgumentNumber(), e);
            setLocationWithinModule(context + ".getArgumentNumber()");
            addWarning(new HeuristicException(HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_CODE,
                HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_MSG + definition.getArgumentNumber(),
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

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

}
