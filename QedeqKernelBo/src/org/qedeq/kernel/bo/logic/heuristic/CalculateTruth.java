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

package org.qedeq.kernel.bo.logic.heuristic;

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
     * Assures that two words are logically equivalent.
     *
     * @param   formula         Formula.
     * @throws  IllegalArgumentException  Check failed.
     */
    public static boolean isTautology(final Element formula) {
        final CalculateTruth calculator = new CalculateTruth();
        return calculator.calculateTautology(formula);
    }

    /**
     * Assures that two words are logically equivalent.
     *
     * @param   formula         Formula.
     * @throws  IllegalArgumentException  Check failed.
     */
    private boolean calculateTautology(final Element formula) {
        final CalculateTruth calculator = new CalculateTruth();
        boolean result = true;
        do {
            result &= calculateValue(formula);
            System.out.println(interpretation.toString());
            interpretation.iterate();
            System.out.println(interpretation.toString());
        } while (result && interpretation.iterationIsNotFinished());
        System.out.println("interpretation finished - and result is = " + result);
        return result;
    }

    /**
     * Assures that two words are logically equivalent.
     *
     * @param   formula         Formula.
     * @throws  IllegalArgumentException  Check failed.
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
        } else if (Operators.PREDICATE_VARIABLE.equals(op)) {
            final String identifier = list.getElement(0).getAtom().getString();
            if (list.size() > 1) {
                //FIXME
            }
            result = interpretation.getPredValue(identifier);
        } else {
            // FIXME
            result = false;
        }
        return result;
    }

}
