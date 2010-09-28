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
 * Variables with arguments.
 *
 * @author  Michael Meyling
 */
public class Variable {

    /** Text to identify the variable. */
    private final String name;

    /** Argument number for variable. */
    private final int number;

    /**
     * Constructor.
     *
     * @param   name    Text to identify the variable.
     * @param   number  Argument number for variable.
     */
    public Variable(final String name, final int number) {
        this.name = name;
        this.number = number;
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
        if (!(other instanceof Variable)) {
            return false;
        }
        if (!other.getClass().equals(getClass())) {
            return false;
        }
        final Variable var = (Variable) other;
        return number == var.number && EqualsUtility.equals(name, var.name);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(name);
        if (number > 0) {
            buffer.append("(");
            for (int i = 0; i < number; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append("*");
            }
            buffer.append(")");
        }
        return buffer.toString();
    }

}