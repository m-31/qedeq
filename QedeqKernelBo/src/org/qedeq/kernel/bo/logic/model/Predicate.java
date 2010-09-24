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

/**
 * One predicate for our model.
 *
 * @author  Michael Meyling
 */
public abstract class Predicate {

    /** Always return false. */
    public static final Predicate FALSE = new Predicate(0, 99, "F", "always false") {
        public boolean calculate(final Entity[] entities) {
            return false;
        }
    };

    /** Always return true. */
    public static final Predicate TRUE = new Predicate(0, 99, "T", "always true") {
        public boolean calculate(final Entity[] entities) {
            return true;
        }
    };

    /** Return true if value is even. */
    public static final Predicate EVEN = new Predicate(0, 99, "| 2", "is even") {
        public boolean calculate(final Entity[] entities) {
            boolean result = true;
            for (int i = 0; i < entities.length; i++) {
                result &= entities[i].getValue() % 2 == 0;
            }
            return result;
        }
    };

    /** Are the entities ordered by < ? */
    public static final Predicate LESS = new Predicate(0, 99, "<", "less") {
        public boolean calculate(final Entity[] entities) {
            boolean result = true;
            for (int i = 0; i < entities.length - 1; i++) {
                result &= entities[i].getValue() < entities[i + 1].getValue();
            }
            return result;
        }
    };

    /** Are the entities are all the same? */
    public static final Predicate EQUAL = new Predicate(0, 99, "=", "equal") {
        public boolean calculate(final Entity[] entities) {
            boolean result = true;
            for (int i = 0; i < entities.length - 1; i++) {
                result &= entities[i].getValue() == entities[i + 1].getValue();
            }
            return result;
        }
    };

    /** Minimum number of arguments this predicate has. */
    private final int minimum;

    /** Maximum number of arguments this predicate has. */
    private final int maximum;

    /** Display text. */
    private final String display;

    /** Description for this predicate. */
    private final String description;

    /**
     * Constructor.
     *
     * @param   minimum         Minimum number of arguments this predicate has.
     * @param   maximum         Maximum number of arguments this predicate has.
     * @param   display         Show this to represent the predicate within outputs.
     * @param   description     Description for this predicate.
     */
    public Predicate(final int minimum, final int maximum, final String display,
            final String description) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.display = display;
        this.description = description;
    }

    /**
     * Get minimum number of arguments this predicate has.
     *
     * @return  Minimum number of arguments for this predicate.
     */
    public int getMinimumArgumentNumber() {
        return minimum;
    }

    /**
     * Get maximum number of arguments this predicate has.
     *
     * @return  Maximum umber of arguments for this predicate.
     */
    public int getMaximumArgumentNumber() {
        return maximum;
    }

    /**
     * Get display text.
     *
     * @return  Representation of this predicate for textual output.
     */
    public String toString() {
        return display;
    }

    /**
     * Get description.
     *
     * @return  Description of this predicate.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Calculate truth value.
     *
     * @param   entities    Calculate predicate for this entities.
     * @return  Truth value.
     */
    public abstract boolean calculate(Entity[] entities);

}
