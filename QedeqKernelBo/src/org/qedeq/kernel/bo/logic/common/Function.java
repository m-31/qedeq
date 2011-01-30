/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
 * Function constant key, describing a function constant.
 *
 * @author  Michael Meyling
 */
public final class Function {

    /** Function name. */
    private String name;

    /** Function argument number. */
    private String arguments;

    /**
     * Constructor.
     *
     * @param   name        Function name.
     * @param   arguments   Function argument number.
     */
    public Function(final String name, final String arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * Get function name.
     *
     * @return  Function name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get function argument number.
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
        if (!(obj instanceof Function)) {
            return false;
        }
        final Function other = (Function) obj;
        return EqualsUtility.equals(getName(), other.getName())
            && EqualsUtility.equals(getArguments(), other.getArguments());
    }

    public String toString() {
        return getName() + "[" + getArguments() + "]";
    }


}
