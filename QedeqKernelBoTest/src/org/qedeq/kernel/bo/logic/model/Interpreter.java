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

package org.qedeq.kernel.bo.logic.model;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.bo.logic.wf.Operators;
import org.qedeq.kernel.common.ModuleContext;


/**
 * This class calculates a new truth value for a given formula for a given interpretation.
 *
 * @author  Michael Meyling
 */
public final class Interpreter {

    /** This class. */
    private static final Class CLASS = Interpreter.class;

    /** Model contains entities, functions, predicates. */
    private final Model model;

    /** Interpret subject variables. */
    private final SubjectVariableInterpreter subjectVariableInterpreter;

    /** Interpret predicate variables. */
    private final PredicateVariableInterpreter predicateVariableInterpreter;

    /** Interpret function variables. */
    private final FunctionVariableInterpreter functionVariableInterpreter;

    /** Module context. Here were are currently. */
    private ModuleContext moduleContext;

    /** For formatting debug trace output. */
    private final StringBuffer deepness = new StringBuffer();

    /**
     * Constructor.
     *
     * @param   model   We work with this model.
     */
    public Interpreter(final Model model) {
        this.model = model;
        subjectVariableInterpreter = new SubjectVariableInterpreter(model);
        predicateVariableInterpreter = new PredicateVariableInterpreter(model);
        functionVariableInterpreter = new FunctionVariableInterpreter(model);
    }

    /**
     * Calculate the truth value of a given formula is a tautology. This is done by checking with
     * a model and certain variable values.
     *
     * @param   moduleContext   Where we are within an module.
     * @param   formula         Formula.
     * @return  Truth value of formula.
     * @throws  HeuristicException      We couldn't calculate the value.
     */
    public boolean calculateValue(final ModuleContext moduleContext, final Element formula)
            throws  HeuristicException {
        this.moduleContext = moduleContext;
        return calculateValue(formula);
    }

    /**
     * Calculate the truth value of a given formula is a tautology. This is done by checking with
     * a model and certain variable values.
     *
     * @param   formula         Formula.
     * @return  Truth value of formula.
     * @throws  HeuristicException      We couldn't calculate the value.
     */
    private boolean calculateValue(final Element formula) throws  HeuristicException {
        final String method = "calculateValue(Element)";
        if (Trace.isDebugEnabled(CLASS)) {
            Trace.param(CLASS, this, method, deepness.toString() + "formula", formula);
            deepness.append("-");
        }
        if (formula.isAtom()) {
            throw new HeuristicException(HeuristicErrorCodes.WRONG_CALLING_CONVENTION_CODE,
                HeuristicErrorCodes.WRONG_CALLING_CONVENTION_MSG, moduleContext);
        }
        final String context = getLocationWithinModule();
        setLocationWithinModule(context + ".getList()");
        final ElementList list = formula.getList();
        final String op = list.getOperator();
        boolean result;
        if (Operators.CONJUNCTION_OPERATOR.equals(op)) {
            result = true;
            for (int i = 0; i < list.size(); i++) {
                setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result &= calculateValue(list.getElement(i));
            }
        } else if (Operators.DISJUNCTION_OPERATOR.equals(op)) {
            result = false;
            for (int i = 0; i < list.size(); i++) {
                setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result |= calculateValue(list.getElement(i));
            }
        } else if (Operators.EQUIVALENCE_OPERATOR.equals(op)) {
            result = true;
            boolean value = false;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                    if (value != calculateValue(list.getElement(i))) {
                        result = false;
                    }
                } else {
                    setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                    value = calculateValue(list.getElement(i));
                }
            }
        } else if (Operators.IMPLICATION_OPERATOR.equals(op)) {
            result = false;
            for (int i = 0; i < list.size(); i++) {
                setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                if (i < list.size() - 1) {
                    result |= !calculateValue(list.getElement(i));
                } else {
                    result |= calculateValue(list.getElement(i));
                }
            }
        } else if (Operators.NEGATION_OPERATOR.equals(op)) {
            result = true;
            for (int i = 0; i < list.size(); i++) {
                setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result &= !calculateValue(list.getElement(i));
            }
        } else if (Operators.PREDICATE_VARIABLE.equals(op)) {
            final PredicateVariable var = new PredicateVariable(list.getElement(0).getAtom().getString(),
                    list.size() - 1);
            setLocationWithinModule(context + ".getList()");
            result = predicateVariableInterpreter.getPredicate(var)
                .calculate(getEntities(list));
        } else if (Operators.UNIVERSAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = handleUniversalQuantifier(list);
        } else if (Operators.EXISTENTIAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = handleExistentialQuantifier(list);
        } else if (Operators.UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = handleUniqueExistentialQuantifier(list);
        } else if (Operators.PREDICATE_CONSTANT.equals(op)) {
            final String text = stripReference(list.getElement(0).getAtom().getString());
            final PredicateConstant var = new PredicateConstant(text,
                list.size() - 1);
            Predicate predicate = model.getPredicateConstant(var);
            if (predicate == null) {
                setLocationWithinModule(context + ".getList().getOperator()");
                throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_MSG + var, moduleContext);
            }
            setLocationWithinModule(context + ".getList()");
            result = predicate.calculate(getEntities(list));
        } else {
            setLocationWithinModule(context + ".getList().getOperator()");
            throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_OPERATOR_CODE,
                HeuristicErrorCodes.UNKNOWN_OPERATOR_MSG + op, moduleContext);
        }
        setLocationWithinModule(context);
        if (Trace.isDebugEnabled(CLASS)) {
            deepness.setLength(deepness.length() > 0 ? deepness.length() - 1 : 0);
            Trace.param(CLASS, this, method, deepness.toString() + "result ", result);
        }
        return result;
    }

    /**
     * Handle universal quantifier operator.
     *
     * @param   list    Work on this formula.
     * @return  result  Calculated truth value.
     * @throws  HeuristicException  Calculation not possible.
     */
    private boolean handleUniversalQuantifier(final ElementList list) throws HeuristicException {
        final String context = getLocationWithinModule();
        boolean result = true;
        final ElementList variable = list.getElement(0).getList();
        final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
        subjectVariableInterpreter.addSubjectVariable(var);
        for (int i = 0; i < model.getEntitiesSize(); i++) {
            if (list.size() == 2) {
                setLocationWithinModule(context + ".getList().getElement(1)");
                result &= calculateValue(list.getElement(1));
            } else {  // must be 3
                setLocationWithinModule(context + ".getList().getElement(1)");
                final boolean result1 = calculateValue(list.getElement(1));
                setLocationWithinModule(context + ".getList().getElement(2)");
                final boolean result2 = calculateValue(list.getElement(2));
                result &= !result1 || result2;
            }
            if (!result) {
                break;
            }
            subjectVariableInterpreter.increaseSubjectVariableSelection(var);
        }
        subjectVariableInterpreter.removeSubjectVariable(var);
        return result;
    }

    /**
     * Handle existential quantifier operator.
     *
     * @param   list    Work on this formula.
     * @return  result  Calculated truth value.
     * @throws  HeuristicException  Calculation not possible.
     */
    private boolean handleExistentialQuantifier(final ElementList list) throws HeuristicException {
        final String context = getLocationWithinModule();
        boolean result = false;
        final ElementList variable = list.getElement(0).getList();
        final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
        subjectVariableInterpreter.addSubjectVariable(var);
        for (int i = 0; i < model.getEntitiesSize(); i++) {
            if (list.size() == 2) {
                setLocationWithinModule(context + ".getList().getElement(1)");
                result |= calculateValue(list.getElement(1));
            } else {  // must be 3
                setLocationWithinModule(context + ".getList().getElement(1)");
                final boolean result1 = calculateValue(list.getElement(1));
                setLocationWithinModule(context + ".getList().getElement(2)");
                final boolean result2 = calculateValue(list.getElement(2));
                result |= result1 && result2;
            }
            if (result) {
                break;
            }
            subjectVariableInterpreter.increaseSubjectVariableSelection(var);
        }
        subjectVariableInterpreter.removeSubjectVariable(var);
        return result;
    }

    /**
     * Handle unique existential quantifier operator.
     *
     * @param   list    Work on this formula.
     * @return  result  Calculated truth value.
     * @throws  HeuristicException  Calculation not possible.
     */
    private boolean handleUniqueExistentialQuantifier(final ElementList list) throws HeuristicException {
        final String context = getLocationWithinModule();
        boolean result = false;
        final ElementList variable = list.getElement(0).getList();
        final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
        subjectVariableInterpreter.addSubjectVariable(var);
        for (int i = 0; i < model.getEntitiesSize(); i++) {
            boolean val;
            if (list.size() == 2) {
                setLocationWithinModule(context + ".getList().getElement(1)");
                val = calculateValue(list.getElement(1));
            } else {  // must be 3
                setLocationWithinModule(context + ".getList().getElement(1)");
                final boolean result1 = calculateValue(list.getElement(1));
                setLocationWithinModule(context + ".getList().getElement(2)");
                final boolean result2 = calculateValue(list.getElement(2));
                val = result1 && result2;
            }
            if (val) {
                if (result) {
                    result = false;
                    break;
                } else {
                    result = true;
                }
            }
            subjectVariableInterpreter.increaseSubjectVariableSelection(var);
        }
        subjectVariableInterpreter.removeSubjectVariable(var);
        return result;
    }

    /**
     * Interpret terms.
     *
     * @param   terms    Interpret these terms. The first entry is stripped.
     * @return  Values.
     * @throws  HeuristicException evaluation failed.
     */
    private Entity[] getEntities(final ElementList terms)
            throws HeuristicException {
        final String context = getLocationWithinModule();
        final Entity[] result =  new Entity[terms.size() - 1];    // strip first argument
        for (int i = 0; i < result.length; i++) {
            setLocationWithinModule(context + ".getElement(" + (i + 1) + ")");
            result[i] = calculateTerm(terms.getElement(i + 1));
        }
        setLocationWithinModule(context);
        return result;
    }

    /**
     * Interpret term.
     *
     * @param   term    Interpret this term.
     * @return  Value.
     * @throws  HeuristicException evaluation failed.
     */
    private Entity calculateTerm(final Element term)
            throws  HeuristicException {
        final String method = "calculateTerm(Element) ";
        if (Trace.isDebugEnabled(CLASS)) {
            Trace.param(CLASS, this, method, deepness.toString() + "term   ", term);
            deepness.append("-");
        }
        if (!term.isList()) {
            throw new RuntimeException("a term should be a list: " + term);
        }
        final String context = getLocationWithinModule();
        final ElementList termList = term.getList();
        final String op = termList.getOperator();
        Entity result = null;
        if (Operators.SUBJECT_VARIABLE.equals(op)) {
            final String text = stripReference(termList.getElement(0).getAtom().getString());
            final SubjectVariable var = new SubjectVariable(text);
            result = subjectVariableInterpreter.getEntity(var);
        } else if (Operators.FUNCTION_VARIABLE.equals(op)) {
            final FunctionVariable var = new FunctionVariable(termList.getElement(0).getAtom().getString(),
                termList.size() - 1);
            Function function = functionVariableInterpreter.getFunction(var);
            setLocationWithinModule(context + ".getList()");
            result = function.map(getEntities(termList));
        } else if (Operators.FUNCTION_CONSTANT.equals(op)) {
            final String text = stripReference(termList.getElement(0).getAtom().getString());
            final FunctionConstant var = new FunctionConstant(text,
                termList.size() - 1);
            Function function = model.getFunctionConstant(var);
            if (function == null) {
                setLocationWithinModule(context + ".getList().getOperator()");
                throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_MSG + var, moduleContext);
            }
            setLocationWithinModule(context + ".getList()");
            result = function.map(getEntities(termList));
        } else if (Operators.CLASS_OP.equals(op)) {
            List fullfillers = new ArrayList();
            ElementList variable = termList.getElement(0).getList();
            final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
            subjectVariableInterpreter.addSubjectVariable(var);
            final PredicateConstant isSetPredicate = new PredicateConstant("isSet", 1);
            Predicate isSet = model.getPredicateConstant(isSetPredicate);
            if (isSet == null) {
                throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_TERM_OPERATOR_CODE,
                        HeuristicErrorCodes.UNKNOWN_TERM_OPERATOR_MSG + "isSet(*)", moduleContext);
            }
            for (int i = 0; i < model.getEntitiesSize(); i++) {
                setLocationWithinModule(context + ".getList().getElement(1)");
                if (calculateValue(termList.getElement(1))
                        && isSet.calculate(new Entity[] {model.getEntity(i)})) {
                    fullfillers.add(model.getEntity(i));
                }
                subjectVariableInterpreter.increaseSubjectVariableSelection(var);
            }
            result = model.comprehension((Entity[]) fullfillers.toArray(new Entity[] {}));
            subjectVariableInterpreter.removeSubjectVariable(var);
        } else {
            setLocationWithinModule(context + ".getList().getOperator()");
            throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_TERM_OPERATOR_CODE,
                HeuristicErrorCodes.UNKNOWN_TERM_OPERATOR_MSG + op, moduleContext);
        }
        setLocationWithinModule(context);
        if (Trace.isDebugEnabled(CLASS)) {
            deepness.setLength(deepness.length() > 0 ? deepness.length() - 1 : 0);
            Trace.param(CLASS, this, method, deepness.toString() + "result ", result);
        }
        return result;
    }

    private String getLocationWithinModule() {
        return moduleContext.getLocationWithinModule();
    }

    private void setLocationWithinModule(final String localContext) {
        moduleContext.setLocationWithinModule(localContext);
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public boolean next() {
        return subjectVariableInterpreter.next() || predicateVariableInterpreter.next()
            || functionVariableInterpreter.next();
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Current interpretation:\n");
        buffer.append("\t" + predicateVariableInterpreter + "\n");
        buffer.append("\t" + subjectVariableInterpreter + "\n");
        buffer.append("\t" + functionVariableInterpreter);
        return buffer.toString();
    }

    /**
     * Strips the reference to external modules.
     *
     * @param   operator    Might have reference to an external module.
     * @return  Operator as local reference.
     */
    public String stripReference(final String operator) {
        final int index = operator.lastIndexOf(".");
        if (index >= 0 && index + 1 < operator.length()) {
            return operator.substring(index + 1);
        }
        return operator;
    }

}
