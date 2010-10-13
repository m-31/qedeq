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
public final class DefaultModel implements Model {

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
    public DefaultModel() {
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

        final List predicate2 = new ArrayList();
        predicatePool.add(predicate2);
        predicate2.add(FALSE);
        predicate2.add(TRUE);
        predicate1.add(EVEN);
        predicate2.add(LESS);
        predicate2.add(EQUAL);

        predicateConstants = new HashMap();
        predicateConstants.put(new PredicateConstant("TRUE", 0), TRUE);
        predicateConstants.put(new PredicateConstant("FALSE", 0), FALSE);
        predicateConstants.put(new PredicateConstant("equal", 2), EQUAL);
        predicateConstants.put(new PredicateConstant("notEqual", 2), NOT_EQUAL);
        predicateConstants.put(new PredicateConstant("in", 2), LESS);
        predicateConstants.put(new PredicateConstant("notIn", 2), NOT_LESS);
        predicateConstants.put(new PredicateConstant("isSet", 1), new Predicate(2, 2, "isSet",
            "isSet") {
            public boolean calculate(final Entity[] entities) {
                if (entities.length == 1 && entities[0].getValue() != 2) {
                    return true;
                }
                return false;
            }
        });
        predicateConstants.put(new PredicateConstant("subclass", 2), Predicate.or(LESS, EQUAL));

        functionConstants = new HashMap();
        functionConstants.put(new FunctionConstant("emptySet", 0), FUNCTION_ZERO);
        functionConstants.put(new FunctionConstant("RussellClass", 0), FUNCTION_TWO);
        functionConstants.put(new FunctionConstant("intersection", 2), FUNCTION_MIN);
        functionConstants.put(new FunctionConstant("union", 2), FUNCTION_MAX);
        functionConstants.put(new FunctionConstant("universalClass", 0), FUNCTION_TWO);

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

    public Entity map(final Entity[] array) {
        if (array.length == 0) {
            return ZERO;
        } else if (array.length == 1) {
            if (array[0].getValue() == TWO.getValue()) {
                return ZERO;
            } else if (array[0].getValue() == ONE.getValue()) {
                return TWO;
            } else if (array[0].getValue() == ZERO.getValue()) {
                return ONE;
            } else {
                return ZERO;
            }
        } else {
            return TWO;
        }
    }

}
