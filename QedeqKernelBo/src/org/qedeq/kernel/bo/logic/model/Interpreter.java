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

    /** Model contains entities, functions, predicates. */
    private final Model model;

    /** Interpret subject variables. */
    private final SubjectVariableInterpreter subjectVariableInterpreter;

    /** Interpret predicate variables. */
    private final PredicateVariableInterpreter predicateVariableInterpreter;

    /** Interpret function variables. */
    private final FunctionVariableInterpreter functionVariableInterpreter;

    /**
     * Constructor.
     */
    public Interpreter() {
        model = new Model();
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
        if (formula.isAtom()) {
            throw new HeuristicException(HeuristicErrorCodes.WRONG_CALLING_CONVENTION_CODE,
                HeuristicErrorCodes.WRONG_CALLING_CONVENTION_MSG, moduleContext);
        }
        final String context = moduleContext.getLocationWithinModule();
        moduleContext.setLocationWithinModule(context + ".getList()");
        final ElementList list = formula.getList();
        final String op = list.getOperator();
        boolean result;
        if (Operators.CONJUNCTION_OPERATOR.equals(op)) {
            result = true;
            for (int i = 0; i < list.size(); i++) {
                moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result &= calculateValue(moduleContext, list.getElement(i));
            }
        } else if (Operators.DISJUNCTION_OPERATOR.equals(op)) {
            result = false;
            for (int i = 0; i < list.size(); i++) {
                moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result |= calculateValue(moduleContext, list.getElement(i));
            }
        } else if (Operators.EQUIVALENCE_OPERATOR.equals(op)) {
            result = true;
            boolean value = false;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                    if (value != calculateValue(moduleContext, list.getElement(i))) {
                        result = false;
                    }
                } else {
                    moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                    value = calculateValue(moduleContext, list.getElement(i));
                }
            }
        } else if (Operators.IMPLICATION_OPERATOR.equals(op)) {
            result = false;
            for (int i = 0; i < list.size(); i++) {
                moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                if (i < list.size() - 1) {
                    result |= !calculateValue(moduleContext, list.getElement(i));
                } else {
                    result |= calculateValue(moduleContext, list.getElement(i));
                }
            }
        } else if (Operators.NEGATION_OPERATOR.equals(op)) {
            result = true;
            for (int i = 0; i < list.size(); i++) {
                moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result &= !calculateValue(moduleContext, list.getElement(i));
            }
        } else if (Operators.PREDICATE_VARIABLE.equals(op)) {
            final PredicateVariable var = new PredicateVariable(list.getElement(0).getAtom().getString(),
                    list.size() - 1);
            moduleContext.setLocationWithinModule(context + ".getList()");
            result = predicateVariableInterpreter.getPredicate(var)
                .calculate(getEntities(moduleContext, list));
        } else if (Operators.UNIVERSAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = true;
            ElementList variable = list.getElement(0).getList();
            final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
            subjectVariableInterpreter.addSubjectVariable(var);
            for (int i = 0; i < model.getEntitiesSize(); i++) {
                moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                if (list.size() == 2) {
                    result &= calculateValue(moduleContext, list.getElement(1));
                } else {  // must be 3
                    result &= !calculateValue(moduleContext, list.getElement(1))
                        || calculateValue(moduleContext, list.getElement(2));
                }
                if (!result) {
                    break;
                }
                subjectVariableInterpreter.increaseSubjectVariableSelection(var);
            }
            subjectVariableInterpreter.removeSubjectVariable(var);
        } else if (Operators.EXISTENTIAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = false;
            ElementList variable = list.getElement(0).getList();
            final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
            subjectVariableInterpreter.addSubjectVariable(var);
            for (int i = 0; i < model.getEntitiesSize(); i++) {
                moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                if (list.size() == 2) {
                    result |= calculateValue(moduleContext, list.getElement(1));
                } else {  // must be 3
                    result |= calculateValue(moduleContext, list.getElement(1))
                        && calculateValue(moduleContext, list.getElement(2));
                }
                if (result) {
                    break;
                }
                subjectVariableInterpreter.increaseSubjectVariableSelection(var);
            }
            subjectVariableInterpreter.removeSubjectVariable(var);
        } else if (Operators.UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = false;
            ElementList variable = list.getElement(0).getList();
            final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
            subjectVariableInterpreter.addSubjectVariable(var);
            for (int i = 0; i < model.getEntitiesSize(); i++) {
                boolean val;
                moduleContext.setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                if (list.size() == 2) {
                    val = calculateValue(moduleContext, list.getElement(1));
                } else {  // must be 3
                    val = calculateValue(moduleContext, list.getElement(1))
                        && calculateValue(moduleContext, list.getElement(2));
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
        } else if (Operators.PREDICATE_CONSTANT.equals(op)) {
            final String text = stripReference(list.getElement(0).getAtom().getString());
            final PredicateVariable var = new PredicateVariable(text,
                list.size() - 1);
            Predicate predicate = model.getPredicateConst(var);
            if (predicate == null) {
                moduleContext.setLocationWithinModule(context + ".getList().getOperator()");
                throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_MSG + var, moduleContext);
            }
            moduleContext.setLocationWithinModule(context + ".getList()");
            result = predicate.calculate(getEntities(moduleContext, list));
        } else {
            moduleContext.setLocationWithinModule(context + ".getList().getOperator()");
            throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_OPERATOR_CODE,
                HeuristicErrorCodes.UNKNOWN_OPERATOR_MSG + op, moduleContext);
        }
        moduleContext.setLocationWithinModule(context);
        return result;
    }

    /**
     * Interpret terms.
     *
     * @param   moduleContext   Where we are within an module.
     * @param   terms    Interpret these terms. The first entry is stripped.
     * @return  Values.
     * @throws  HeuristicException evaluation failed.
     */
    private Entity[] getEntities(final ModuleContext moduleContext, final ElementList terms)
            throws HeuristicException {
        final String context = moduleContext.getLocationWithinModule();
        final Entity[] result =  new Entity[terms.size() - 1];    // strip first argument
        for (int i = 0; i < result.length; i++) {
            moduleContext.setLocationWithinModule(context + ".getElement(" + i + 1 + ")");
            result[i] = getEntity(moduleContext, terms.getElement(i + 1));
        }
        moduleContext.setLocationWithinModule(context);
        return result;
    }

    /**
     * Interpret term.
     *
     * @param   moduleContext   Where we are within an module.
     * @param   term    Interpret this term.
     * @return  Value.
     * @throws  HeuristicException evaluation failed.
     */
    private Entity getEntity(final ModuleContext moduleContext, final Element term)
            throws  HeuristicException {
        if (!term.isList()) {
            throw new RuntimeException("a term should be a list: " + term);
        }
        final String context = moduleContext.getLocationWithinModule();
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
            moduleContext.setLocationWithinModule(context + ".getList()");
            result = function.map(getEntities(moduleContext, termList));
        } else {
            moduleContext.setLocationWithinModule(context + ".getList().getOperator()");
            throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_TERM_OPERATOR_CODE,
                HeuristicErrorCodes.UUNKNOWN_TERM_OPERATOR_MSG + op, moduleContext);
        }
        moduleContext.setLocationWithinModule(context);
        return result;
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
     * @param   operator    Might refernce to an external module.
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
