/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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
 * A model for our mathematical world. It has only one entity.
 *
 * @author  Michael Meyling
 */
public final class UnaryDynamicModel extends DynamicModel {

    /** "Zero" or empty class. */
    public static final Entity ZERO = new Entity(0, "0", "{} or empty set");

    /** Map to zero. */
    public static final Function FUNCTION_ZERO = new Function(0, 99, "->0", "always 0") {
        public Entity map(final Entity[] entities) {
            return ZERO;
        }
    };

    /**
     * Constructor.
     */
    public UnaryDynamicModel() {
        super("one element");
        addEntity(ZERO);

        // we don't need to add functions because we always return FUNCTION_ZERO

        addPredicate(0, FALSE);
        addPredicate(0, TRUE);
        addPredicate(1, FALSE);
        addPredicate(1, TRUE);
        addPredicate(2, FALSE);
        addPredicate(2, TRUE);
        addPredicate(3, FALSE);
        addPredicate(3, TRUE);

        addPredicateConstant(new ModelPredicateConstant("in", 2), FALSE);

    }

    public String getDescription() {
        return "This model has only one entity. The \" is element of\" relation is never fullfilled.";
    }

    public int getFunctionSize(final int size) {
        return 1;
    }

    public Function getFunction(final int size, final int number) {
        return FUNCTION_ZERO;
    }

    public Function getFunctionConstant(final ModelFunctionConstant con) {
        return FUNCTION_ZERO;
    }

    public Entity comprehension(final Entity[] array) {
        return ZERO;
    }

}
