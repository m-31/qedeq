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
 * Interpret function variables.
 *
 * @author  Michael Meyling
 */
public final class FunctionVariableInterpreter {

    /** Model contains entities, functions, predicates. */
    private Model model;

    /** List of function variables. */
    private List functionVariables;

    /** List of counters for function variables. */
    private List functionVariableCounters;

    /**
     * Constructor.
     *
     * @param   model   Model we work on.
     */
    public FunctionVariableInterpreter(final Model model) {
        this.model = model;
        functionVariables = new ArrayList();
        functionVariableCounters = new ArrayList();
    }

    /**
     * Get selection for function of model for given function variable.
     *
     * @param   var Function variable we want an interpretation for.
     * @return  Function selection number of model.
     */
    private int getFunctionVariableSelection(final FunctionVariable var) {
        int selection;
        if (functionVariables.contains(var)) {
            final int index = functionVariables.indexOf(var);
            selection = ((Enumerator) functionVariableCounters.get(index)).getNumber();
        } else {
//            System.out.println("added function variable " + var);
            selection = 0;
            functionVariables.add(var);
            functionVariableCounters.add(new Enumerator());
        }
        return selection;
    }

    /**
     * Get interpretation for function of model for given function variable.
     *
     * @param   var Function variable we want an interpretation for.
     * @return  Function.
     */
    public Function getFunction(final FunctionVariable var) {
        return model.getFunction(var.getArgumentNumber(),
            getFunctionVariableSelection(var));
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public boolean next() {
        boolean next = true;
        for (int i = functionVariables.size() - 1; i >= -1; i--) {
            if (i < 0) {
                next = false;
                break;
            }
            final FunctionVariable var = (FunctionVariable) functionVariables.get(i);
            final Enumerator number = (Enumerator) functionVariableCounters.get(i);
            if (number.getNumber() + 1 < model.getFunctionSize(var.getArgumentNumber())) {
                number.increaseNumber();
                break;
            } else {
                number.reset();
            }

        }
        return next;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("function variables {");
        for (int i = 0; i < functionVariables.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            FunctionVariable var = (FunctionVariable) functionVariables.get(i);
            buffer.append(functionVariables.get(i));
            buffer.append("=");
            buffer.append(getFunction(var));
        }
        buffer.append("}");
        return buffer.toString();
    }

    public void clear() {
        functionVariables.clear();
        functionVariableCounters.clear();
    }

}
