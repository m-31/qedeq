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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A model for our mathematical world. It has only one entity.
 *
 * @author  Michael Meyling
 */
public final class UnaryModel implements Model {

    /** "Zero" or empty class. */
    public static final Entity ZERO = new Entity(0, "0", "{} or empty set");

    /** Map to zero. */
    public static final Function FUNCTION_ZERO = new Function(0, 99, "->0", "always 0") {
        public Entity map(final Entity[] entities) {
            return ZERO;
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

    /** List of all entities in this model. */
    private final List entities;

    /** List of functions for different argument numbers. */
    private final List functionPool;

    /** List of predicates for different argument numbers. */
    private final List predicatePool;

    /** Map of predicate constants. */
    private final Map predicateConstants;

    /**
     * Constructor.
     */
    public UnaryModel() {
        entities = new ArrayList();
        entities.add(ZERO);

        functionPool = new ArrayList();

        final List function0 = new ArrayList();
        functionPool.add(function0);
        function0.add(FUNCTION_ZERO);

        final List function1 = new ArrayList();
        functionPool.add(function1);
        function1.add(FUNCTION_ZERO);

        final List function2 = new ArrayList();
        functionPool.add(function2);
        function2.add(FUNCTION_ZERO);

        predicatePool = new ArrayList();

        final List predicate0 = new ArrayList();
        predicatePool.add(predicate0);
        predicate0.add(FALSE);
        predicate0.add(TRUE);

        final List predicate1 = new ArrayList();
        predicatePool.add(predicate1);
        predicate1.add(FALSE);
        predicate1.add(TRUE);

        final List predicate2 = new ArrayList();
        predicatePool.add(predicate2);
        predicate2.add(FALSE);
        predicate2.add(TRUE);
        predicate2.add(LESS);
        predicate2.add(EQUAL);

        predicateConstants = new HashMap();
        predicateConstants.put(new ModelPredicateConstant("TRUE", 0), TRUE);
        predicateConstants.put(new ModelPredicateConstant("FALSE", 0), FALSE);
        predicateConstants.put(new ModelPredicateConstant("equal", 2), EQUAL);
        predicateConstants.put(new ModelPredicateConstant("notEqual", 2), NOT_EQUAL);
        predicateConstants.put(new ModelPredicateConstant("in", 2), FALSE);
        predicateConstants.put(new ModelPredicateConstant("notIn", 2), TRUE);
        predicateConstants.put(new ModelPredicateConstant("isSet", 1), FALSE);
        predicateConstants.put(new ModelPredicateConstant("subclass", 2), TRUE);
        predicateConstants.put(new ModelPredicateConstant("isOrderedPair", 1), TRUE);
        predicateConstants.put(new ModelPredicateConstant("isRelation", 1), TRUE);
        predicateConstants.put(new ModelPredicateConstant("isFunction", 1), TRUE);

    }

    public String getDescription() {
        return "This model has only one entity. The \" is element of\" relation is never fullfilled.";
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
