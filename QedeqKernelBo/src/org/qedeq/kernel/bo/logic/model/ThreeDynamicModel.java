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
 * A model for our mathematical world. It has entities, functions and predicates.
 * There are also predicate and function constants.
 *
 * @author  Michael Meyling
 */
public final class ThreeDynamicModel extends DynamicModel {

    /** "Zero" or empty set. */
    public static final Entity ZERO = new Entity(0, "{}", "{} or empty set");

    /** "One" or set that contains the empty set. */
    public static final Entity ONE = new Entity(1, "{{}}", "{{}} or 1");

    /** "Two" or set that contains "zero" and "one". */
    public static final Entity TWO = new Entity(2, "{{}, {{}}}", "{{}, {{}}} or 2");

    /** Map to empty class. */
    public static final Function FUNCTION_ZERO = Function.createConstant(ZERO);

    /** Map to 1. */
    public static final Function FUNCTION_ONE = Function.createConstant(ONE);

    /** Map to 2. */
    public static final Function FUNCTION_TWO = Function.createConstant(TWO);

    /** Return true if all values are zero. */
    public static final Predicate IS_ZERO = Predicate.isEntity(ZERO);

    /** Return true if all values are 1. */
    public static final Predicate IS_ONE = Predicate.isEntity(ONE);

    /** Return true if all values are 2. */
    public static final Predicate IS_TWO = Predicate.isEntity(TWO);

    /** Map to one. */
    /** Modulo 3. */
    private final Function functionModulo3 = new Function(0, 99, "% 3", "modulo 3") {
        public Entity map(final Entity[] entities) {
            int result = 0;
            for (int i = 0; i < entities.length; i++) {
                result += entities[i].getValue() % 3;
            }
            result = result % 3;
            return getEntity(result);
        }
    };

    /** +1 Modulo 3. */
    private final Function functionPlus1Modulo3 = new Function(0, 99, "+1 % 3", "plus 1 modulo 3") {
        public Entity map(final Entity[] entities) {
            int result = 1;
            for (int i = 0; i < entities.length; i++) {
                result += entities[i].getValue() % 3;
            }
            result = result % 3;
            return getEntity(result);
        }
    };


    /**
     * Constructor.
     */
    public ThreeDynamicModel() {
        super("three elements");

        addEntity(ZERO);
        addEntity(ONE);
        addEntity(TWO);

        addFunction(0, FUNCTION_ZERO);
        addFunction(0, FUNCTION_ONE);
        addFunction(0, FUNCTION_TWO);

        addFunction(1, FUNCTION_ZERO);
        addFunction(1, FUNCTION_ONE);
        addFunction(1, functionModulo3);
        addFunction(1, functionPlus1Modulo3);

        addFunction(2, FUNCTION_ZERO);
        addFunction(2, FUNCTION_ONE);
        addFunction(2, functionModulo3);
        addFunction(2, functionPlus1Modulo3);

        addPredicate(0, FALSE);
        addPredicate(0, TRUE);

        addPredicate(1, FALSE);
        addPredicate(1, TRUE);
        addPredicate(1, EVEN);
        addPredicate(1, IS_ZERO);
        addPredicate(1, IS_ONE);
        addPredicate(1, IS_TWO);

        addPredicate(2, FALSE);
        addPredicate(2, TRUE);
        addPredicate(2, EVEN);
        addPredicate(2, LESS);
        addPredicate(2, EQUAL);
        addPredicate(2, IS_ZERO);
        addPredicate(2, IS_ONE);
        addPredicate(2, IS_TWO);

        addPredicateConstant(new ModelPredicateConstant("in", 2), LESS);
    }

    public String getDescription() {
        return "This model has three entities: {}, {{}}, {{}, {{}}}.";
    }

    public Entity comprehension(final Entity[] array) {
        Entity result = ZERO;
        for (int i = 0; i < array.length; i++) {
            switch (array[i].getValue()) {
            case 0:
                if (result.getValue() == 0) {
                    result = ONE;
                }
                break;
            case 1:
                result = TWO;
                break;
            case 2:
                break;
            default: throw new RuntimeException("unknown value for entity " + array[i]);
            }
        }
        return result;
    }

}
