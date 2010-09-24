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
 * One entity in our model.
 *
 * @author  Michael Meyling
 */
public final class Entity {

    /** "Zero" or empty set. */
    public static final Entity ZERO = new Entity(0, "0", "{} or empty set");

    /** "One" or set that contains the empty set. */
    public static final Entity ONE = new Entity(1, "1", "{{}} or {0}");

    /** "Two" or set that contains "zero" and "one". */
    public static final Entity TWO = new Entity(2, "2", "{{}, {{}}} or {0, 1}");

    /** Value. This can be used for calculating truth or other values. Each value should
     * be unique to an entity.*/
    private final int value;

    /** Display text. */
    private final String display;

    /** Description for this entity. */
    private final String description;

    /**
     * Constructor.
     * @param   value       This can be used for calculating truth or other values. Each value
     *                      should be unique to an entity.
     * @param   display     Show this to represent the entity within outputs.
     * @param   description Description for this entity.
     */
    public Entity(final int value, final String display, final String description) {
        this.value = value;
        this.display = display;
        this.description = description;
    }

    /**
     * Get value.  This can be used for calculating truth or other values. Each value
     * should be unique to an entity.
     *
     * @return  Unique value for this entity.
     */
    public int getValue() {
        return value;
    }

    /**
     * Get display text.
     *
     * @return  Representation of this entity for textual output.
     */
    public String toString() {
        return display;
    }

    /**
     * Get description.
     *
     * @return  Description of this entity.
     */
    public String getDescription() {
        return description;
    }

}
