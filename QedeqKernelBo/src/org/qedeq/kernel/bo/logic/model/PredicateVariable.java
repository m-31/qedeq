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

import org.qedeq.base.utility.EqualsUtility;

/**
 * One predicate variable for our model.
 *
 * @author  Michael Meyling
 */
public class PredicateVariable {

    /** Text to identify the predicate variable. */
    private final String name;

    /** Argument number for predicate variable. */
    private final int number;

    /**
     * Constructor.
     *
     * @param   name        Show this to represent the predicate within outputs.
     * @param   number      Number of arguments this predicate has.
     */
    public PredicateVariable(final String name, final int number) {
        this.number = number;
        this.name = name;
    }

    /**
     * Get predicate name.
     *
     * @return  Predicate name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get number of arguments this predicate has.
     *
     * @return  Number of arguments for this predicate.
     */
    public int getArgumentNumber() {
        return number;
    }

    public int hashCode() {
        return name.hashCode() ^ number;
    }

    public boolean equals(final Object other) {
        if (!(other instanceof PredicateVariable)) {
            return false;
        }
        final PredicateVariable var = (PredicateVariable) other;
        return number == var.number && EqualsUtility.equals(name, var.name);
    }

    public String toString() {
        return name + "_" + number;
    }

}
