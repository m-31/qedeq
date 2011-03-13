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

package org.qedeq.kernel.bo.logic.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A model for our mathematical world. It has entities, functions and predicates.
 * There are also predicate and function constants.
 *
 * @author  Michael Meyling
 */
public final class ThreeModel implements Model {

    /** This class. */
    private static final Class CLASS = ThreeModel.class;

    /** "Zero" or empty set. */
    public static final Entity ZERO = new Entity(0, "0", "{} or empty set");

    /** "One" or set that contains the empty set. */
    public static final Entity ONE = new Entity(1, "1", "{{}} or {0}");

    /** "Two" or set that contains "zero" and "one". */
    public static final Entity TWO = new Entity(2, "2", "{{}, {{}}} or {0, 1}");

    /** Map to zero. */
    public static final Function FUNCTION_ZERO = new Function(0, 99, "->0", "always 0") {
        public Entity map(final Entity[] entities) {
            return ZERO;
        }
    };

    /** Map to one. */
    public static final Function FUNCTION_ONE = new Function(0, 99, "->1", "always 1") {
        public Entity map(final Entity[] entities) {
            return ONE;
        }
    };

    /** Map to two. */
    public static final Function FUNCTION_TWO = new Function(0, 99, "->2", "always 2") {
        public Entity map(final Entity[] entities) {
            return TWO;
        }
    };

    /** Modulo 3. */
    public static final Function FUNCTION_MOD = new Function(0, 99, "% 3", "modulo 3") {
        public Entity map(final Entity[] entities) {
            int result = 0;
            for (int i = 0; i < entities.length; i++) {
                result += entities[i].getValue() % 3;
            }
            result = result % 3;
            switch (result) {
            case 0: return ZERO;
            case 1: return ONE;
            case 2: return TWO;
            default: return null;
            }
        }
    };

    /** +1 Modulo 3. */
    public static final Function FUNCTION_PLUS = new Function(0, 99, "+1 % 3", "plus 1 modulo 3") {
        public Entity map(final Entity[] entities) {
            int result = 1;
            for (int i = 0; i < entities.length; i++) {
                result += entities[i].getValue() % 3;
            }
            result = result % 3;
            switch (result) {
            case 0: return ZERO;
            case 1: return ONE;
            case 2: return TWO;
            default: return null;
            }
        }
    };

    /** Minimum. */
    public static final Function FUNCTION_MIN = new Function(0, 99, "min", "minimum") {
        public Entity map(final Entity[] entities) {
            int result = 2;
            for (int i = 0; i < entities.length; i++) {
                if (result > entities[i].getValue()) {
                    result = entities[i].getValue();
                }
            }
            switch (result) {
            case 0: return ZERO;
            case 1: return ONE;
            case 2: return TWO;
            default: return null;
            }
        }
    };

    /** Maximum. */
    public static final Function FUNCTION_MAX = new Function(0, 99, "max", "maximum") {
        public Entity map(final Entity[] entities) {
            int result = 0;
            for (int i = 0; i < entities.length; i++) {
                if (result < entities[i].getValue()) {
                    result = entities[i].getValue();
                }
            }
            switch (result) {
            case 0: return ZERO;
            case 1: return ONE;
            case 2: return TWO;
            default: return null;
            }
        }
    };

    /** Maximum. */
    private final Function functionClassList = new Function(0, 99, "{..}", "classList") {
        public Entity map(final Entity[] entities) {
            Entity result = ZERO;
            for (int i = 0; i < entities.length; i++) {
                switch (entities[i].getValue()) {
                case 0:
                    if (result.getValue() == 0) {
                        result = ONE;
                    }
                    break;
                case 1:
                    result = TWO;
                    break;
                case 2:
                    result = TWO;
                    break;
                default: throw new RuntimeException("unknown value for entity " + entities[i]);
                }
            }
            return result;
        }
    };

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

    /** Are the entities not ordered by < ? */
    public static final Predicate NOT_LESS = Predicate.not(LESS);

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

    /** Are the entities are not all the same? */
    public static final Predicate NOT_EQUAL = Predicate.not(EQUAL);

    /** Return true if all values are zero. */
    public static final Predicate IS_ZERO = new Predicate(0, 99, "=0", "is_zero") {
        public boolean calculate(final Entity[] entities) {
            boolean result = true;
            for (int i = 0; i < entities.length; i++) {
                result &= entities[i].getValue() == ZERO.getValue();
            }
            return result;
        }
    };

    /** Are the entities are all equal to 1? */
    public static final Predicate IS_ONE = new Predicate(0, 99, "=1", "is_one") {
        public boolean calculate(final Entity[] entities) {
            boolean result = true;
            for (int i = 0; i < entities.length; i++) {
                result &= entities[i].getValue() == ONE.getValue();
            }
            return result;
        }
    };

    /** Are the entities are all equal to 2? */
    public static final Predicate IS_TWO = new Predicate(0, 99, "= 2", "is_two") {
        public boolean calculate(final Entity[] entities) {
            boolean result = true;
            for (int i = 0; i < entities.length; i++) {
                result &= entities[i].getValue() == TWO.getValue();
            }
            return result;
        }
    };

    /** Are the entities are not all equal to 2? */
    public static final Predicate NOT_IS_TWO = Predicate.not(IS_TWO);



    /** List of all entities in this model. */
    private final List entities;

    /** List of functions for different argument numbers. */
    private final List functionPool;

    /** List of predicates for different argument numbers. */
    private final List predicatePool;

    /** Map of predicate constants. */
    private final Map predicateConstants;

    /** Map of function constants. */
    private final Map functionConstants;

    /**
     * Constructor.
     */
    public ThreeModel() {
        entities = new ArrayList();
        entities.add(ZERO);
        entities.add(ONE);
        entities.add(TWO);

        functionPool = new ArrayList();

        final List function0 = new ArrayList();
        functionPool.add(function0);
        function0.add(FUNCTION_ZERO);
        function0.add(FUNCTION_ONE);

        final List function1 = new ArrayList();
        functionPool.add(function1);
        function1.add(FUNCTION_ZERO);
        function1.add(FUNCTION_ONE);
        function1.add(FUNCTION_MOD);
        function1.add(FUNCTION_PLUS);

        final List function2 = new ArrayList();
        functionPool.add(function2);
        function2.add(FUNCTION_ZERO);
        function2.add(FUNCTION_ONE);
        function2.add(FUNCTION_MOD);
        function2.add(FUNCTION_PLUS);

        predicatePool = new ArrayList();

        final List predicate0 = new ArrayList();
        predicatePool.add(predicate0);
        predicate0.add(FALSE);
        predicate0.add(TRUE);

        final List predicate1 = new ArrayList();
        predicatePool.add(predicate1);
        predicate1.add(FALSE);
        predicate1.add(TRUE);
        predicate1.add(EVEN);
        predicate1.add(IS_ZERO);
        predicate1.add(IS_ONE);
        predicate1.add(IS_TWO);

        final List predicate2 = new ArrayList();
        predicatePool.add(predicate2);
        predicate2.add(FALSE);
        predicate2.add(TRUE);
        predicate2.add(EVEN);
        predicate2.add(LESS);
        predicate2.add(EQUAL);
        predicate2.add(IS_ZERO);
        predicate2.add(IS_ONE);
        predicate2.add(IS_TWO);

        predicateConstants = new HashMap();
        predicateConstants.put(new ModelPredicateConstant("TRUE", 0), TRUE);
        predicateConstants.put(new ModelPredicateConstant("FALSE", 0), FALSE);
        predicateConstants.put(new ModelPredicateConstant("equal", 2), EQUAL);
        predicateConstants.put(new ModelPredicateConstant("notEqual", 2), NOT_EQUAL);
        predicateConstants.put(new ModelPredicateConstant("in", 2), LESS);
        predicateConstants.put(new ModelPredicateConstant("notIn", 2), NOT_LESS);
        predicateConstants.put(new ModelPredicateConstant("isSet", 1), Predicate.not(IS_TWO));
        predicateConstants.put(new ModelPredicateConstant("subclass", 2), Predicate.or(LESS, EQUAL));

        functionConstants = new HashMap();
        functionConstants.put(new ModelFunctionConstant("emptySet", 0), FUNCTION_ZERO);
        functionConstants.put(new ModelFunctionConstant("RussellClass", 0), FUNCTION_TWO);
        functionConstants.put(new ModelFunctionConstant("intersection", 2), FUNCTION_MIN);
        functionConstants.put(new ModelFunctionConstant("union", 2), FUNCTION_MAX);
        functionConstants.put(new ModelFunctionConstant("universalClass", 0), FUNCTION_TWO);
        functionConstants.put(new ModelFunctionConstant("complement", 1), new Function(1, 1, "compement",
            "complement") {
            public Entity map(final Entity[] entities) {
                if (entities.length != 1) {
                    return ZERO;
                }
                switch (entities[0].getValue()) {
                case 0: return TWO;
                case 1: return TWO;
                case 2: return ZERO;
                default: throw new IllegalArgumentException("unknown entity value");
                }
            }
        });
        functionConstants.put(new ModelFunctionConstant("classList", 0), functionClassList);
        functionConstants.put(new ModelFunctionConstant("classList", 1), functionClassList);
        functionConstants.put(new ModelFunctionConstant("classList", 2), functionClassList);
        functionConstants.put(new ModelFunctionConstant("classList", 3), functionClassList);
        functionConstants.put(new ModelFunctionConstant("classList", 4), functionClassList);

    }

    public String getDescription() {
        return "This model has three entities. The first is element of the second, "
            + "the second element of the third.";
    }

    public int getEntitiesSize() {
        return entities.size();
    }

    public Entity getEntity(final int number) {
        return (Entity) entities.get(number);
    }

    public int getPredicateSize(final int size) {
        if (predicatePool.size() <=  size) {
            return 0;
        }
        return ((List) predicatePool.get(size)).size();
    }

    public Predicate getPredicate(final int size, final int number) {
        final List predicateForSize = (List) predicatePool.get(size);
        return (Predicate) predicateForSize.get(number);
    }

    public Predicate getPredicateConstant(final ModelPredicateConstant con) {
        return (Predicate) predicateConstants.get(con);
    }

    public int getFunctionSize(final int size) {
        if (functionPool.size() <=  size) {
            return 0;
        }
        return ((List) functionPool.get(size)).size();
    }

    public Function getFunction(final int size, final int number) {
        final List functionForSize = (List) functionPool.get(size);
        return (Function) functionForSize.get(number);
    }

    public Function getFunctionConstant(final ModelFunctionConstant con) {
        return (Function) functionConstants.get(con);
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
