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

package org.qedeq.kernel.bo.logic.model;

import org.qedeq.base.utility.Enumerator;
import org.qedeq.kernel.bo.logic.common.SubjectVariable;

/**
 * One subject variable allocation for our model.
 *
 * @author  Michael Meyling
 */
public class SubjectVariableAllocation {

    /** This subject variable. */
    private final SubjectVariable variable;

    /** Reference to current interpretation entity. */
    private final Enumerator value;

    /** Is this a fixed interpretatin? */
    private boolean fixed;

    /**
     * Constructor.
     *
     * @param   variable    Subject variable.
     */
    public SubjectVariableAllocation(final SubjectVariable variable) {
        this.variable = variable;
        this.value = new Enumerator();
        this.fixed = false;
    }

    /**
     * Constructor.
     *
     * @param   variable    Subject variable.
     * @param   value       Value for subject variable.
     */
    public SubjectVariableAllocation(final SubjectVariable variable, final int value) {
        this.variable = variable;
        this.value = new Enumerator(value);
        this.fixed = true;
    }

    /**
     * Get subject variable.
     *
     * @return  Subject variable.
     */
    public SubjectVariable getVariable() {
        return variable;
    }

    /**
     * Is this a fixed interpretation?
     *
     * @return  Is this a fixed interpretation?
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * Get reference to current model entity.
     *
     * @return  Entity number.
     */
    public int getValue() {
        return value.getNumber();
    }

    /**
     * Increase model entity number.
     */
    public void increaseNumber() {
        if (fixed) {
            throw new IllegalStateException("variable could not iterate: " + toString());
        }
        value.increaseNumber();
    }

    /**
     * Reset model entity number.
     */
    public void resetNumber() {
        if (fixed) {
            throw new IllegalStateException("variable could not iterate: " + toString());
        }
        value.reset();
    }

    public String toString() {
        return variable.toString() + "=" + value.getNumber();
    }

}
