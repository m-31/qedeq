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
     * @param   formula         Formula.
     * @return  Truth value of formula.
     */
    public boolean calculateValue(final Element formula) {
        if (formula.isAtom()) {
            throw new IllegalArgumentException("wrong calling convention: " + formula);
        }
        final ElementList list = formula.getList();
        final String op = list.getOperator();
        boolean result;
        if (Operators.CONJUNCTION_OPERATOR.equals(op)) {
            result = true;
            for (int i = 0; i < list.size(); i++) {
                result &= calculateValue(list.getElement(i));
            }
        } else if (Operators.DISJUNCTION_OPERATOR.equals(op)) {
            result = false;
            for (int i = 0; i < list.size(); i++) {
                result |= calculateValue(list.getElement(i));
            }
        } else if (Operators.EQUIVALENCE_OPERATOR.equals(op)) {
            result = true;
            boolean value = false;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    if (value != calculateValue(list.getElement(i))) {
                        result = false;
                    }
                } else {
                    value = calculateValue(list.getElement(i));
                }
            }
        } else if (Operators.IMPLICATION_OPERATOR.equals(op)) {
            result = false;
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    result |= !calculateValue(list.getElement(i));
                } else {
                    result |= calculateValue(list.getElement(i));
                }
            }
        } else if (Operators.NEGATION_OPERATOR.equals(op)) {
            result = true;
            for (int i = 0; i < list.size(); i++) {
                result &= !calculateValue(list.getElement(i));
            }
        } else if (Operators.PREDICATE_VARIABLE.equals(op)) {
            final PredicateVariable var = new PredicateVariable(list.getElement(0).getAtom().getString(),
                    list.size() - 1);
            result = predicateVariableInterpreter.getPredicate(var).calculate(getEntities(list));
        } else if (Operators.UNIVERSAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = true;
            ElementList variable = list.getElement(0).getList();
            final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
            subjectVariableInterpreter.addSubjectVariable(var);
            for (int i = 0; i < model.getEntitiesSize(); i++) {
                if (list.size() == 2) {
                    result &= calculateValue(list.getElement(1));
                } else {  // must be 3
                    result &= !calculateValue(list.getElement(1)) || calculateValue(list.getElement(2));
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
                if (list.size() == 2) {
                    result |= calculateValue(list.getElement(1));
                } else {  // must be 3
                    result |= calculateValue(list.getElement(1)) && calculateValue(list.getElement(2));
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
                if (list.size() == 2) {
                    val = calculateValue(list.getElement(1));
                } else {  // must be 3
                    val = calculateValue(list.getElement(1)) && calculateValue(list.getElement(2));
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
            final PredicateVariable var = new PredicateVariable(list.getElement(0).getAtom().getString(),
                list.size() - 1);
            Predicate predicate = model.getPredicateConst(var);
            if (predicate == null) {
                throw new RuntimeException("Unknown predicate constant: " + var);
            }
            result = predicate.calculate(getEntities(list));
        } else {
            throw new RuntimeException("unknown operator " + op);
        }
        return result;
    }

    /**
     * Interpret terms.
     *
     * @param   terms    Interpret these terms. The first entry is stripped.
     * @return  Values.
     */
    private Entity[] getEntities(final ElementList terms) {
        final Entity[] result =  new Entity[terms.size() - 1];    // strip first argument
        for (int i = 0; i < result.length; i++) {
            result[i] = getEntity(terms.getElement(i + 1));
        }
        return result;
    }

    /**
     * Interpret term.
     *
     * @param   term    Interpret this term.
     * @return  Value.
     */
    private Entity getEntity(final Element term) {
        if (!term.isList()) {
            throw new RuntimeException("a term should be a list: " + term);
        }
        final ElementList termList = term.getList();
        final String op = termList.getOperator();
        Entity result = null;
        if (Operators.SUBJECT_VARIABLE.equals(op)) {
            final SubjectVariable var = new SubjectVariable(termList.getElement(0).getAtom().getString());
            result = subjectVariableInterpreter.getEntity(var);
        } else if (Operators.FUNCTION_VARIABLE.equals(op)) {
            final FunctionVariable var = new FunctionVariable(termList.getElement(0).getAtom().getString(),
                    termList.size() - 1);
            Function function = functionVariableInterpreter.getFunction(var);
            result = function.map(getEntities(termList));
        }
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


}
