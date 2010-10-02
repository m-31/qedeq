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
import org.qedeq.kernel.common.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleContext;


/**
 * This class calculates a new truth value for a given formula for a given interpretation.
 *
 * @author  Michael Meyling
 */
public final class CalculateTruth {

    /** Interpretation for variables. */
    private final Interpreter interpretation;

    /**
     * Constructor.
     */
    private CalculateTruth() {
        interpretation = new Interpreter();
    }

    /**
     * Test if given formula is a tautology. This is done by checking a model and
     * iterating through variable values.
     *
     * @param   formula         Formula.
     * @return  Is this formula a tautology according to our tests.
     * @throws  HeuristicException  Evaluation failed.
     */
    public static boolean isTautology(final Element formula) throws HeuristicException {
        final CalculateTruth calculator = new CalculateTruth();
        return calculator.calculateTautology(new ModuleContext(new DefaultModuleAddress()),
            formula);
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
    public static boolean isTautology(final ModuleContext moduleContext, final Element formula)
            throws HeuristicException {
        final CalculateTruth calculator = new CalculateTruth();
        return calculator.calculateTautology(moduleContext, formula);
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
    private boolean calculateTautology(final ModuleContext moduleContext, final Element formula)
            throws HeuristicException {
        boolean result = true;
        do {
            result &= interpretation.calculateValue(moduleContext, formula);
//            System.out.println(interpretation.toString());
        } while (result && interpretation.next());
        if (!result) {
            System.out.println(interpretation);
        }
        System.out.println("interpretation finished - and result is = " + result);
        return result;
    }


}
