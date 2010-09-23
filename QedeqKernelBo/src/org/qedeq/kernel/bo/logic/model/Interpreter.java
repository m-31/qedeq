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

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.bo.logic.wf.Operators;


/**
 * This class calculates a new truth value for a given formula for a given interpretation.
 *
 * @author  Michael Meyling
 */
public final class Interpreter {

    /** List of subject variables. */
    private List subjectVariables;

    /** List of predicate variables. */
    private List predicateVariables;

    /** Model contains entities. */
    private Model model;

    /**
     * Constructor.
     */
    public Interpreter() {
        predicateVariables = new ArrayList();
        subjectVariables = new ArrayList();
        model = new Model();
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
            int selection = -1;
            final PredicateVariable var = new PredicateVariable(list.getElement(0).getAtom().getString(),
                    list.size() - 1, 0);
            if (predicateVariables.contains(var)) {
                int index = predicateVariables.indexOf(var);
                selection = ((PredicateVariable) predicateVariables.get(index)).getSelection();
            } else {
                System.out.println("added predvar " + var);
                predicateVariables.add(var);
                selection = 0;
            }
            Predicate predicate = model.getPredicate(var.getArgumentNumber(), selection);
            result = predicate.calculate(getEntities(list));
        } else if (Operators.UNIVERSAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = true;
            ElementList variable = list.getElement(0).getList();
            final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString(), 0);
            subjectVariables.add(var);
            for (int i = 0; i < model.getEntitiesSize(); i++) {
                var.setSelection(i);
                if (list.size() == 2) {
                    result &= calculateValue(list.getElement(1));
                } else {  // must be 3
                    result &= !calculateValue(list.getElement(1)) || calculateValue(list.getElement(2));
                }
                if (!result) {
                    break;
                }
            }
            subjectVariables.remove(var);
        } else if (Operators.EXISTENTIAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = false;
            ElementList variable = list.getElement(0).getList();
            final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString(), 0);
            subjectVariables.add(var);
            for (int i = 0; i < model.getEntitiesSize(); i++) {
                var.setSelection(i);
                if (list.size() == 2) {
                    result |= calculateValue(list.getElement(1));
                } else {  // must be 3
                    result |= calculateValue(list.getElement(1)) && calculateValue(list.getElement(2));
                }
                if (result) {
                    break;
                }
            }
            subjectVariables.remove(var);
        } else if (Operators.UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = false;
            ElementList variable = list.getElement(0).getList();
            final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString(), 0);
            subjectVariables.add(var);
            for (int i = 0; i < model.getEntitiesSize(); i++) {
                var.setSelection(i);
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
            }
            subjectVariables.remove(var);
        } else if (Operators.PREDICATE_CONSTANT.equals(op)) {
            final PredicateVariable var = new PredicateVariable(list.getElement(0).getAtom().getString(),
                list.size() - 1, 0);
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

    private Entity[] getEntities(final ElementList list) {
        final Entity[] result =  new Entity[list.size() - 1];    // strip first argument
        for (int i = 0; i < result.length; i++) {
            result[i] = getEntity(list.getElement(i + 1));
        }
        return result;
    }

    private Entity getEntity(final Element element) {
        final ElementList list = element.getList(); // FIXME test before
        final String op = list.getOperator();
        Entity result = null;
        int selection = -1;
        if (Operators.SUBJECT_VARIABLE.equals(op)) {
            final SubjectVariable var = new SubjectVariable(list.getElement(0).getAtom().getString(), 0);
            if (subjectVariables.contains(var)) {
                final int index = subjectVariables.indexOf(var);
                selection = ((SubjectVariable) subjectVariables.get(index)).getSelection();
            } else {
                System.out.println("added subject variable " + var);
                selection = 0;
                subjectVariables.add(var);
            }
            result = model.getEntity(selection);
        }
        return result;
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public boolean next() {
        System.out.println("iterate");
        boolean next = false;
        for (int i = subjectVariables.size() - 1; i >= -1; i--) {
            if (i < 0) {
                next = true;
                break;
            }
            final SubjectVariable var = (SubjectVariable) subjectVariables.get(i);
            int selection = var.getSelection() + 1;
            if (selection < model.getEntitiesSize()) {
                var.setSelection(selection);
                break;
            } else {
                var.setSelection(0);
            }
        }
        if (next) {
            next = false;
            for (int i = predicateVariables.size() - 1; i >= -1; i--) {
                if (i < 0) {
                    next = true;
                    break;
                }
                final PredicateVariable var = (PredicateVariable) predicateVariables.get(i);
                int selection = var.getSelection() + 1;
                if (selection < model.getPredicateSize(var.getArgumentNumber())) {
                    var.setSelection(selection);
                    break;
                } else {
                    var.setSelection(0);
                }
            }
        }
        return !next;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        for (int i = 0; i < predicateVariables.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(predicateVariables.get(i));
        }
        buffer.append("}");
        buffer.append("{");
        for (int i = 0; i < subjectVariables.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(subjectVariables.get(i));
        }
        buffer.append("}");
        return buffer.toString();
    }



}
