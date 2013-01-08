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
public final class SixDynamicModel extends DynamicModel {

    /** "Zero" or empty set. */
    public static final Entity EMPTY = new Entity(0, "{}", "{} or empty set");

    /** "One" or set that contains the empty set. */
    public static final Entity ZERO_ONE = new Entity(1, "1", "1");

    /** "Two" or set that contains "zero" and "one". */
    public static final Entity ZERO_TWO = new Entity(2, "2", "2");

    /** "One" or set that contains the empty set. */
    public static final Entity ONE_ONE = new Entity(3, "{1}", "{1}");

    /** "Two" or set that contains "zero" and "one". */
    public static final Entity ONE_TWO = new Entity(4, "{2}", "{2}");

    /** "Two" or set that contains "zero" and "one". */
    public static final Entity TWO_ONE_TWO = new Entity(5, "{1, 2}", "{1, 2}");

    /** Map to empty class. */
    public static final Function FUNCTION_EMPTY = Function.createConstant(EMPTY);

    /** Map to 1. */
    public static final Function FUNCTION_ZERO_ONE = Function.createConstant(ZERO_ONE);

    /** Map to 2. */
    public static final Function FUNCTION_ZERO_TWO = Function.createConstant(ZERO_TWO);

    /** Map to {1}. */
    public static final Function FUNCTION_ONE_ONE = Function.createConstant(ONE_ONE);

    /** Map to {2}. */
    public static final Function FUNCTION_ONE_TWO = Function.createConstant(ONE_TWO);

    /** Map to {1, 2}. */
    public static final Function FUNCTION_TWO_ONE_TWO = Function.createConstant(TWO_ONE_TWO);

    /** Return true if all values are zero. */
    public static final Predicate IS_EMPTY = Predicate.isEntity(EMPTY);

    /** Return true if all values are 1. */
    public static final Predicate IS_ZERO_ONE = Predicate.isEntity(ZERO_ONE);

    /** Return true if all values are 2. */
    public static final Predicate IS_ZERO_TWO = Predicate.isEntity(ZERO_TWO);

    /** Return true if all values are {1}. */
    public static final Predicate IS_ONE_ONE = Predicate.isEntity(ONE_ONE);

    /** Return true if all values are {2}. */
    public static final Predicate IS_ONE_TWO = Predicate.isEntity(ONE_TWO);

    /** Return true if all values are {2}. */
    public static final Predicate IS_TWO_ONE_TWO = Predicate.isEntity(TWO_ONE_TWO);

    /** Are the entities are not all equal to {2}? */
    public static final Predicate NOT_IS_ONE_TWO = Predicate.not(IS_ONE_TWO);

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
    public SixDynamicModel() {
        super("six elements");

        addEntity(EMPTY);
        addEntity(ZERO_ONE);
        addEntity(ZERO_TWO);
        addEntity(ONE_ONE);
        addEntity(ONE_TWO);
        addEntity(TWO_ONE_TWO);

        addFunction(0, FUNCTION_EMPTY);
        addFunction(0, FUNCTION_ZERO_ONE);
        addFunction(0, FUNCTION_ZERO_TWO);
        addFunction(0, FUNCTION_ONE_ONE);
        addFunction(0, FUNCTION_ONE_TWO);
        addFunction(0, FUNCTION_TWO_ONE_TWO);

        addFunction(1, FUNCTION_EMPTY);
        addFunction(1, FUNCTION_ZERO_ONE);
        addFunction(1, functionModulo3);
        addFunction(1, functionPlus1Modulo3);

        addFunction(2, FUNCTION_EMPTY);
        addFunction(2, FUNCTION_ZERO_ONE);
        addFunction(2, functionModulo3);
        addFunction(2, functionPlus1Modulo3);

        addPredicate(0, FALSE);
        addPredicate(0, TRUE);

        addPredicate(1, FALSE);
        addPredicate(1, TRUE);
        addPredicate(1, EVEN);
        addPredicate(1, IS_EMPTY);
        addPredicate(1, IS_ZERO_ONE);
        addPredicate(1, IS_ZERO_TWO);
        addPredicate(1, IS_ONE_ONE);
        addPredicate(1, IS_TWO_ONE_TWO);

        addPredicate(2, FALSE);
        addPredicate(2, TRUE);
        addPredicate(2, EVEN);
        addPredicate(2, LESS);
        addPredicate(2, EQUAL);
        addPredicate(2, IS_EMPTY);
        addPredicate(2, IS_ZERO_ONE);
        addPredicate(2, IS_ZERO_TWO);
        addPredicate(2, IS_ONE_ONE);
        addPredicate(2, IS_ONE_TWO);

        addPredicateConstant(new ModelPredicateConstant("in", 2), new Predicate(2, 2, "in", "isSet") {
            public boolean calculate(final Entity[] entities) {
                boolean result = false;
                final int a = entities[0].getValue();
                final int b = entities[1].getValue();
                if (a == 1 && (b == 3 || b == 5)) {
                    result = true;
                }
                if (a == 2 && (b == 4 || b == 5)) {
                    result = true;
                }
                return result;
            }
        });
    }

    public String getDescription() {
        return "This model has six entities: {}, {1}, {2}, {{1}}, {{2}} and {1, 2}.";
    }

    public Entity comprehension(final Entity[] array) {
        Entity result = EMPTY;
        for (int i = 0; i < array.length; i++) {
            switch (array[i].getValue()) {
            case 0:
            case 3:
            case 4:
            case 5: break;
            case 1: if (result.getValue() != 5) {
                        if (result.getValue() == 0) {
                            result = ONE_ONE;
                        } else if (result.getValue() == 4) {
                            result = TWO_ONE_TWO;
                        }
                    }
                    break;
            case 2: if (result.getValue() != 5) {
                        if (result.getValue() == 0) {
                            result = ONE_TWO;
                        } else if (result.getValue() == 4) {
                            result = TWO_ONE_TWO;
                        }
                    }
                    break;
            default: throw new RuntimeException("unknown value for entity " + array[i]);
            }
        }
        return result;
    }

}
