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

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.bo.logic.model.DynamicInterpreter;
import org.qedeq.kernel.bo.logic.model.DynamicModel;
import org.qedeq.kernel.bo.logic.model.FunctionConstant;
import org.qedeq.kernel.bo.logic.model.HeuristicErrorCodes;
import org.qedeq.kernel.bo.logic.model.HeuristicException;
import org.qedeq.kernel.bo.logic.model.PredicateConstant;
import org.qedeq.kernel.bo.logic.wf.Operators;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.common.ModuleContext;
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
public final class DynamicHeuristicChecker extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = DynamicHeuristicChecker.class;

    /** Interpretation for variables. */
    private final DynamicInterpreter interpreter;

    /**
     * Constructor.
     *
     * @param   plugin  This plugin we work for.
     * @param   model   Check with this model.
     * @param   prop    QEDEQ module properties object.
     */
    private DynamicHeuristicChecker(final PluginBo plugin, final DynamicModel model,
            final KernelQedeqBo prop) {
        super(plugin, prop);
        this.interpreter = new DynamicInterpreter(model);
    }

    /**
     * Checks if all formulas of a QEDEQ module are tautologies in our model.
     *
     * @param   plugin  This plugin we work for.
     * @param   model   Check with this model.
     * @param   prop    QEDEQ module.
     * @throws  SourceFileExceptionList      Major problem occurred.
     */
    public static void check(final PluginBo plugin, final DynamicModel model, final KernelQedeqBo prop)
            throws SourceFileExceptionList {
        final DynamicHeuristicChecker checker = new DynamicHeuristicChecker(plugin, model, prop);
        try {
            checker.traverse();
        } catch (SourceFileExceptionList sfl) {
            throw sfl;
        } finally {
            prop.addPluginErrors(plugin, checker.getErrorList(), checker.getWarningList());
        }
    }

    /**
     * Check truth value in our model. If it is no tautology an warning is added.
     * This also happens if our model doesn't support an operator found in the formula.
     *
     * @param   test    Test formula.
     */
    private void test(final Element test) {
        try {
            if (!isTautology(getCurrentContext(), test)) {
                addWarning(new HeuristicException(HeuristicErrorCodes.EVALUATED_NOT_TRUE_CODE,
                    HeuristicErrorCodes.EVALUATED_NOT_TRUE_MSG, getCurrentContext()));
            }
        } catch (HeuristicException h) {
            // TODO 20101015 m31: better exception handling would be better!
            if (getCurrentContext().getModuleLocation().equals(h.getContext().getModuleLocation())) {
                addWarning(h);
            } else {
                addWarning(new HeuristicException(h.getErrorCode(), h.getMessage(),
                        getCurrentContext()));
            }
        } catch (RuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof HeuristicException) {
                // TODO 20101015 m31: better exception handling would be better!
                HeuristicException h = (HeuristicException) e.getCause();
                addWarning(new HeuristicException(h.getErrorCode(), h.getMessage(),
                    getCurrentContext()));
            } else {
                addWarning(new HeuristicException(HeuristicErrorCodes.RUNTIME_EXCEPTION_CODE,
                    HeuristicErrorCodes.RUNTIME_EXCEPTION_MSG + e, getCurrentContext()));
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
        do {
            result &= interpreter.calculateValue(moduleContext, formula);
//            System.out.println(interpreter.toString());
        } while (result && interpreter.next());
//        if (!result) {
//            System.out.println(interpreter);
//        }
//        System.out.println("interpretation finished - and result is = " + result);
        return result;
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
            if (definition.getFormula() != null) {

                // add new predicate constant
                setLocationWithinModule(context + ".getFormula().getElement()");
                final VariableList variableList = definition.getVariableList();
                final int size = (variableList == null ? 0 : variableList.size());
                interpreter.addPredicateConstant(predicate, variableList, definition.getFormula()
                    .getElement().getList());

                // test new predicate constant: must always be successful otherwise there
                // must be a programming error or the predicate definition is not formal correct
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
                setLocationWithinModule(context);
                test(new DefaultElementList(Operators.EQUIVALENCE_OPERATOR, compare));
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
            if (definition.getTerm() != null) {
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
                setLocationWithinModule(context);
                test(new DefaultElementList(Operators.PREDICATE_CONSTANT, equal));
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
