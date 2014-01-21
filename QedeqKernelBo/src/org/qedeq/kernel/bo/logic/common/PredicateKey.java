/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.base.utility.EqualsUtility;

/**
 * Predicate constant key, describing a predicate constant.
 *
 * @author  Michael Meyling
 */
public final class PredicateKey {

    /** Predicate name. */
    private String name;

    /** Predicate argument number. */
    private String arguments;

    /**
     * Constructor.
     *
     * @param   name        Predicate name.
     * @param   arguments   Predicate argument number.
     */
    public PredicateKey(final String name, final String arguments) {
        this.name = name;
        this.arguments = arguments;
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
     * Get predicate argument number.
     *
     * @return  Number of arguments.
     */
    public String getArguments() {
        return arguments;
    }

    public int hashCode() {
        return (getName() != null ? getName().hashCode() : 0)
            ^ (getArguments() != null ? getArguments().hashCode() : 0);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof PredicateKey)) {
            return false;
        }
        final PredicateKey other = (PredicateKey) obj;
        return EqualsUtility.equals(getName(), other.getName())
            && EqualsUtility.equals(getArguments(), other.getArguments());
    }

    public String toString() {
        return getName() + "[" + getArguments() + "]";
    }

}
