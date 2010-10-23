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
public final class DynamicModel implements Model {

    /** This class. */
    private static final Class CLASS = DynamicModel.class;

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

    /** Map to one. */
    /** Modulo 3. */
    public static final Function FUNCTION_MOD = new Function(0, 99, "% 3", "modulo 3") {
        public Entity map(final Entity[] entities) {
            int result = 0;
            for (int i = 0; i < entities.length; i++) {
                result += entities[i].getValue() % 3;
            }
            result = result % 3;
            return value2Entity(result);
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
            return value2Entity(result);
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
            return value2Entity(result);
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
            return value2Entity(result);
        }
    };

    /** Maximum. */
    public static final Function FUNCTION_CLASS_LIST = new Function(0, 99, "{..}", "classList") {
        public Entity map(final Entity[] entities) {
            return comprehension(entities);
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
    public DynamicModel() {
        entities = new ArrayList();
        for (int i = 0; i < 5; i++) {
            entities.add(value2Entity(i));
        }

        functionPool = new ArrayList();

        final List function0 = new ArrayList();
        functionPool.add(function0);
        function0.add(FUNCTION_EMPTY);
        function0.add(FUNCTION_ZERO_ONE);
        function0.add(FUNCTION_ZERO_TWO);
        function0.add(FUNCTION_ONE_ONE);
        function0.add(FUNCTION_ONE_TWO);
        function0.add(FUNCTION_TWO_ONE_TWO);

        final List function1 = new ArrayList();
        functionPool.add(function1);
        function1.add(FUNCTION_EMPTY);
        function1.add(FUNCTION_ZERO_ONE);
        function1.add(FUNCTION_MOD);
        function1.add(FUNCTION_PLUS);

        final List function2 = new ArrayList();
        functionPool.add(function2);
        function2.add(FUNCTION_EMPTY);
        function2.add(FUNCTION_ZERO_ONE);
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
        predicate1.add(IS_EMPTY);
        predicate1.add(IS_ZERO_ONE);
        predicate1.add(IS_ZERO_TWO);
        predicate1.add(IS_ONE_ONE);
        predicate1.add(IS_TWO_ONE_TWO);

        final List predicate2 = new ArrayList();
        predicatePool.add(predicate2);
        predicate2.add(FALSE);
        predicate2.add(TRUE);
        predicate2.add(EVEN);
        predicate2.add(LESS);
        predicate2.add(EQUAL);
        predicate2.add(IS_EMPTY);
        predicate2.add(IS_ZERO_ONE);
        predicate2.add(IS_ZERO_TWO);
        predicate2.add(IS_ONE_ONE);
        predicate2.add(IS_ONE_TWO);

        predicateConstants = new HashMap();
        predicateConstants.put(new PredicateConstant("TRUE", 0), TRUE);
        predicateConstants.put(new PredicateConstant("FALSE", 0), FALSE);
        predicateConstants.put(new PredicateConstant("equal", 2), EQUAL);
        predicateConstants.put(new PredicateConstant("notEqual", 2), NOT_EQUAL);
        predicateConstants.put(new PredicateConstant("in", 2), new Predicate(2, 2, "in", "isSet") {
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
        functionConstants = new HashMap();
    }

    /**
     * Add a predicate constant.
     *
     * @param   constant    Add for this constant.
     * @param   predicate   This interpretation.
     */
    public void addPredicateConstant(final PredicateConstant constant, final Predicate predicate) {
        predicateConstants.put(constant, predicate);
    }

    /**
     * Add a function constant.
     *
     * @param   constant    Add for this constant.
     * @param   function    This interpretation.
     */
    public void addFunctionConstant(final FunctionConstant constant, final Function function) {
        functionConstants.put(constant, function);
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

    public Predicate getPredicateConstant(final PredicateConstant con) {
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

    public Function getFunctionConstant(final FunctionConstant con) {
        return (Function) functionConstants.get(con);
    }

    private static Entity value2Entity(final int value) {
        switch (value) {
        case 0: return EMPTY;
        case 1: return ZERO_ONE;
        case 2: return ZERO_TWO;
        case 3: return ONE_ONE;
        case 4: return ONE_TWO;
        case 5: return TWO_ONE_TWO;
        default: throw new RuntimeException("unknown entity for value " + value);
        }
    }

    private static Entity comprehension(final Entity[] array) {
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

    public Entity map(final Entity[] array) {
        return comprehension(array);
    }

}
