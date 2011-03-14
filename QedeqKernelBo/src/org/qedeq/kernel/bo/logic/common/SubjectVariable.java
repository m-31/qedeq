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
 * One subject variable. This class is mainly used in the model area.
 * For this kernel he normal form of a subject variable is an
 * {@link org.qedeq.kernel.se.base.list.ElementList}
 * with an {@link org.qedeq.kernel.se.base.list.Atom} with the name of the subject variable.
 *
 * @author  Michael Meyling
 */
public class SubjectVariable {

    /** Text to identify the subject variable. */
    private final String name;

    /**
     * Constructor.
     *
     * @param   name        Show this to represent the subject variable within outputs.
     */
    public SubjectVariable(final String name) {
        this.name = name;
    }

    /**
     * Get subject variable name.
     *
     * @return  Subject variable name.
     */
    public String getName() {
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(final Object other) {
        if (!(other instanceof SubjectVariable)) {
            return false;
        }
        final SubjectVariable var = (SubjectVariable) other;
        return EqualsUtility.equals(name, var.name);
    }

    public String toString() {
        return name;
    }

}
