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
import java.util.Vector;

/**
 * A model for our mathematical world. It has entities, functions and predicates.
 * There are also predicate and function constants.
 *
 * @author  Michael Meyling
 */
public abstract class DynamicModel implements Model {

    /** This class. */
    private static final Class CLASS = DynamicModel.class;

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


    /** List of all entities in this model. */
    private final List entities;

    /** List of functions for different argument numbers. */
    private final Vector functionPool;

    /** List of predicates for different argument numbers. */
    private final Vector predicatePool;

    /** Map of predicate constants. */
    private final Map predicateConstants;

    /** Map of function constants. */
    private final Map functionConstants;

    /**
     * Constructor.
     */
    public DynamicModel() {
        entities = new ArrayList();

        functionPool = new Vector();


        predicatePool = new Vector();

        predicateConstants = new HashMap();
        predicateConstants.put(new PredicateConstant("TRUE", 0), TRUE);
        predicateConstants.put(new PredicateConstant("FALSE", 0), FALSE);
        predicateConstants.put(new PredicateConstant("equal", 2), EQUAL);
        predicateConstants.put(new PredicateConstant("notEqual", 2), NOT_EQUAL);
        functionConstants = new HashMap();
    }

    /**
     * Returns the description for the concrete model.
     *
     * @return  Description of concrete model.
     */
    public abstract String getDescription();

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

    // FIXME 20101120 m31: create entity here
    public void addEntity(final Entity entity) {
        if (entities.size() != entity.getValue()) {
            throw new RuntimeException("entity value should have been " + entities.size()
                + " but was " + entity.getValue());
        }
        entities.add(entity);
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
        final List list = (List) predicatePool.get(size);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public Predicate getPredicate(final int size, final int number) {
        final List predicateForSize = (List) predicatePool.get(size);
        return (Predicate) predicateForSize.get(number);
    }

    /**
     * Add a predicate for interpreting predicate variables.
     *
     * @param   size        Add for this function argument number.
     * @param   predicate   This interpretation.
     */
    public void addPredicate(final int size, final Predicate predicate) {
        if (getPredicateSize(size) == 0) {
            if (predicatePool.size() <= size) {
                for (int i = predicatePool.size(); i <= size; i++) {
                    predicatePool.add(new ArrayList());
                }
            }
        }
        List list = (List) predicatePool.get(size);
        list.add(predicate);
    }

    public Predicate getPredicateConstant(final PredicateConstant con) {
        return (Predicate) predicateConstants.get(con);
    }

    public int getFunctionSize(final int size) {
        if (functionPool.size() <=  size) {
            return 0;
        }
        final List list = (List) functionPool.get(size);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public Function getFunction(final int size, final int number) {
        final List functionForSize = (List) functionPool.get(size);
        return (Function) functionForSize.get(number);
    }

    /**
     * Add a function for interpreting function variables.
     *
     * @param   size        Add for this function argument number.
     * @param   function    This interpretation.
     */
    public void addFunction(final int size, final Function function) {
        if (getFunctionSize(size) == 0) {
            if (functionPool.size() <= size) {
                for (int i = functionPool.size(); i <= size; i++) {
                    functionPool.add(new ArrayList());
                }
            }
        }
        final List list = (List) functionPool.get(size);
        list.add(function);
    }

    public Function getFunctionConstant(final FunctionConstant con) {
        return (Function) functionConstants.get(con);
    }

    public Entity value2Entity(final int value) {
        return (Entity) entities.get(value);
    }

    // FIXME 20101120: rename to comprehension
    public abstract Entity map(final Entity[] array);

}
