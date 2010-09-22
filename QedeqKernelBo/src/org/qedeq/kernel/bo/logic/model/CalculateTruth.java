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
public final class CalculateTruth {

    /** Interpretation for variables. */
    private final Interpretation interpretation;

    /**
     * Constructor.
     */
    private CalculateTruth() {
        interpretation = new Interpretation();
    }

    /**
     * Test if given formula is a tautology. This is done by checking a model and
     * iterating through variable values.
     *
     * @param   formula         Formula.
     * @return  Is this formula a tautology according to our tests.
     */
    public static boolean isTautology(final Element formula) {
        final CalculateTruth calculator = new CalculateTruth();
        return calculator.calculateTautology(formula);
    }

    /**
     * Test if given formula is a tautology. This is done by checking a model and
     * iterating through variable values.
     *
     * @param   formula         Formula.
     * @return  Is this formula a tautology according to our tests.
     */
    private boolean calculateTautology(final Element formula) {
        boolean result = true;
        do {
            result &= calculateValue(formula);
            System.out.println(interpretation.toString());
            interpretation.iterate();
        } while (result && interpretation.iterationIsNotFinished());
        System.out.println("interpretation finished - and result is = " + result);
        return result;
    }

    /**
     * Calculate the truth value of a given formula is a tautology. This is done by checking with
     * a model and certain variable values.
     *
     * @param   formula         Formula.
     * @return  Truth value of formula.
     */
    private boolean calculateValue(final Element formula) {
        if (formula.isAtom()) {
            throw new IllegalArgumentException("wrong calling convention");
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
//        } else if (Operators.PREDICATE_VARIABLE.equals(op)) {
//            final String identifier = list.getElement(0).getAtom().getString();
//            result = interpretation.getPredValue(identifier);
        } else if (Operators.PREDICATE_VARIABLE.equals(op)) {
            result = interpretation.getFormulaValue(list);
        } else {
            // FIXME
            result = false;
        }
        return result;
    }

}
