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

import org.qedeq.base.utility.Enumerator;


/**
 * This class calculates a new truth value for a given formula for a given interpretation.
 *
 * @author  Michael Meyling
 */
public final class PredicateVariableInterpreter {

    /** List of predicate variables. */
    private List predicateVariables;

    /** List of counters for predicate variables. */
    private List predicateVariableCounters;

    /** Model contains entities. */
    private Model model;

    /** Counter for next calls for one cycle. */
    private double counter;

    /**
     * Constructor.
     *
     * @param   model   Model we work on.
     */
    public PredicateVariableInterpreter(final Model model) {
        this.model = model;
        predicateVariables = new ArrayList();
        predicateVariableCounters = new ArrayList();
    }

    private int getPredicateVariableSelection(final PredicateVariable var) {
        int selection;
        if (predicateVariables.contains(var)) {
            final int index = predicateVariables.indexOf(var);
            selection = ((Enumerator) predicateVariableCounters.get(index)).getNumber();
        } else {
//            System.out.println("added predicate variable " + var);
            selection = 0;
            predicateVariables.add(var);
            predicateVariableCounters.add(new Enumerator());
        }
        return selection;
    }

    /**
     * Get model predicate for predicate variable.
     *
     * @param   var     For this predicate variable.
     * @return  Predicate for model.
     */
    public Predicate getPredicate(final PredicateVariable var) {
        return model.getPredicate(var.getArgumentNumber(),
            getPredicateVariableSelection(var));
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public boolean next() {
        counter++;
        boolean next = true;
        for (int i = predicateVariables.size() - 1; i >= -1; i--) {
            if (i < 0) {
                next = false;
                counter = 0;
                break;
            }
            final PredicateVariable var = (PredicateVariable) predicateVariables.get(i);
            final Enumerator number = (Enumerator) predicateVariableCounters.get(i);
            if (number.getNumber() + 1 < model.getPredicateSize(var.getArgumentNumber())) {
                number.increaseNumber();
                break;
            } else {
                number.reset();
            }

        }
        return next;
    }

    public double getCompleteness() {
        double result = counter + 1;
        for (int i = 0; i < predicateVariableCounters.size(); i++) {
            final PredicateVariable var = (PredicateVariable) predicateVariables.get(i);
            result /= model.getPredicateSize(var.getArgumentNumber());
        }
//        System.out.println("PredicateCompleteness: " + result);
        return result;
    }


    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("predicate variables {");
        for (int i = 0; i < predicateVariables.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            PredicateVariable var = (PredicateVariable) predicateVariables.get(i);
            buffer.append(predicateVariables.get(i));
            buffer.append("=");
            buffer.append(getPredicate(var));
        }
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Clear variable interpretation.
     */
    public void clear() {
        predicateVariables.clear();
        predicateVariableCounters.clear();
    }


}
