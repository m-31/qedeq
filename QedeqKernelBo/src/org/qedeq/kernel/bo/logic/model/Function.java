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
 * One function for our model.
 *
 * @author  Michael Meyling
 */
public abstract class Function {

    /** Map to zero. */
    public static final Function ZERO = new Function(0, 99, "->0", "always 0") {
        public Entity map(final Entity[] entities) {
            return Entity.ZERO;
        }
    };

    /** Map to one. */
    public static final Function ONE = new Function(0, 99, "->1", "always 1") {
        public Entity map(final Entity[] entities) {
            return Entity.ONE;
        }
    };

    /** Modulo 3. */
    public static final Function MOD = new Function(0, 99, "% 3", "modulo 3") {
        public Entity map(final Entity[] entities) {
            int result = 0;
            for (int i = 0; i < entities.length; i++) {
                result += entities[i].getValue() % 3;
            }
            result = result % 3;
            switch (result) {
            case 0: return Entity.ZERO;
            case 1: return Entity.ONE;
            case 2: return Entity.TWO;
            default: return null;
            }
        }
    };

    /** +1 Modulo 3. */
    public static final Function PLUS = new Function(0, 99, "+1 % 3", "plus 1 modulo 3") {
        public Entity map(final Entity[] entities) {
            int result = 1;
            for (int i = 0; i < entities.length; i++) {
                result += entities[i].getValue() % 3;
            }
            result = result % 3;
            switch (result) {
            case 0: return Entity.ZERO;
            case 1: return Entity.ONE;
            case 2: return Entity.TWO;
            default: return null;
            }
        }
    };


    /** Minimum number of arguments this function has. */
    private final int minimum;

    /** Maximum number of arguments this function has. */
    private final int maximum;

    /** Display text. */
    private final String display;

    /** Description for this function. */
    private final String description;

    /**
     * Constructor.
     *
     * @param   minimum         Minimum number of arguments this function has.
     * @param   maximum         Maximum number of arguments this function has.
     * @param   display         Show this to represent the function within outputs.
     * @param   description     Description for this function.
     */
    public Function(final int minimum, final int maximum, final String display,
            final String description) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.display = display;
        this.description = description;
    }

    /**
     * Get minimum number of arguments this function has.
     *
     * @return  Minimum number of arguments for this function.
     */
    public int getMinimumArgumentNumber() {
        return minimum;
    }

    /**
     * Get maximum number of arguments this function has.
     *
     * @return  Maximum number of arguments for this function.
     */
    public int getMaximumArgumentNumber() {
        return maximum;
    }

    /**
     * Get display text.
     *
     * @return  Representation of this function for textual output.
     */
    public String toString() {
        return display;
    }

    /**
     * Get description.
     *
     * @return  Description of this function.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Calculate truth value.
     *
     * @param   entities    Calculate function for this entities.
     * @return  Truth value.
     */
    public abstract Entity map(Entity[] entities);

}
