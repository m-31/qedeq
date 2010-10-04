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
public final class Model {

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
    public Model() {
        entities = new ArrayList();
        entities.add(Entity.ZERO);
        entities.add(Entity.ONE);
        entities.add(Entity.TWO);

        functionPool = new ArrayList();

        final List function0 = new ArrayList();
        functionPool.add(function0);
        function0.add(Function.ZERO);
        function0.add(Function.ONE);

        final List function1 = new ArrayList();
        functionPool.add(function1);
        function1.add(Function.ZERO);
        function1.add(Function.ONE);
        function1.add(Function.MOD);
        function1.add(Function.PLUS);

        final List function2 = new ArrayList();
        functionPool.add(function2);
        function2.add(Function.ZERO);
        function2.add(Function.ONE);
        function2.add(Function.MOD);
        function2.add(Function.PLUS);

        predicatePool = new ArrayList();

        final List predicate0 = new ArrayList();
        predicatePool.add(predicate0);
        predicate0.add(Predicate.FALSE);
        predicate0.add(Predicate.TRUE);

        final List predicate1 = new ArrayList();
        predicatePool.add(predicate1);
        predicate1.add(Predicate.FALSE);
        predicate1.add(Predicate.TRUE);
        predicate1.add(Predicate.EVEN);

        final List predicate2 = new ArrayList();
        predicatePool.add(predicate2);
        predicate2.add(Predicate.FALSE);
        predicate2.add(Predicate.TRUE);
        predicate1.add(Predicate.EVEN);
        predicate2.add(Predicate.LESS);
        predicate2.add(Predicate.EQUAL);

        predicateConstants = new HashMap();
        predicateConstants.put(new PredicateConstant("TRUE", 0), Predicate.TRUE);
        predicateConstants.put(new PredicateConstant("FALSE", 0), Predicate.FALSE);
        predicateConstants.put(new PredicateConstant("equal", 2), Predicate.EQUAL);
        predicateConstants.put(new PredicateConstant("notEqual", 2), Predicate.NOT_EQUAL);
        predicateConstants.put(new PredicateConstant("in", 2), Predicate.LESS);
        predicateConstants.put(new PredicateConstant("notIn", 2), Predicate.NOT_LESS);
        predicateConstants.put(new PredicateConstant("isSet", 1), new Predicate(2, 2, "isSet",
            "isSet") {
            public boolean calculate(final Entity[] entities) {
                if (entities.length == 1 && entities[0].getValue() != 2) {
                    return true;
                }
                return false;
            }
        });
        predicateConstants.put(new PredicateConstant("subclass", 2), Predicate.LESS);

        functionConstants = new HashMap();
        functionConstants.put(new FunctionConstant("emptySet", 0), Function.ZERO);
        functionConstants.put(new FunctionConstant("RussellClass", 0), Function.TWO);
        functionConstants.put(new FunctionConstant("intersection", 2), Function.MIN);
        functionConstants.put(new FunctionConstant("union", 2), Function.MIN);
        functionConstants.put(new FunctionConstant("universalClass", 0), Function.TWO);

    }

    /**
     * Get number of all entities in this model.
     *
     * @return  Number of entities.
     */
    public int getEntitiesSize() {
        return entities.size();
    }

    /**
     * Get entity <code>number</code>.
     *
     * @param   number  Get entity with this number.
     * @return  Entity.
     */
    public Entity getEntity(final int number) {
        return (Entity) entities.get(number);
    }

    /**
     * Get number of predicates with <code>size</code> number of arguments.
     *
     * @param   size    Number of arguments.
     * @return  Number of predicates in this model.
     */
    public int getPredicateSize(final int size) {
        if (predicatePool.size() <=  size) {
            return 0;
        }
        return ((List) predicatePool.get(size)).size();
    }

    /**
     * Get predicate of this model.
     *
     * @param   size    Number of arguments for predicate.
     * @param   number  Number of predicate.
     * @return  Predicate for this model.
     */
    public Predicate getPredicate(final int size, final int number) {
        final List predicateForSize = (List) predicatePool.get(size);
        return (Predicate) predicateForSize.get(number);
    }

    /**
     * Get predicate constant of this model.
     *
     * @param   con     Predicate constant we are looking for.
     * @return  Predicate for this model.
     */
    public Predicate getPredicateConstant(final PredicateConstant con) {
        return (Predicate) predicateConstants.get(con);
    }

    /**
     * Get number of functions for this model.
     *
     * @param   size    Number of arguments for function.
     * @return  Number of functions in this model.
     */
    public int getFunctionSize(final int size) {
        if (functionPool.size() <=  size) {
            return 0;
        }
        return ((List) functionPool.get(size)).size();
    }

    /**
     * Get function.
     *
     * @param   size    Number of arguments for function.
     * @param   number  Number of function.
     * @return  Function in this model.
     */
    public Function getFunction(final int size, final int number) {
        final List functionForSize = (List) functionPool.get(size);
        return (Function) functionForSize.get(number);
    }

    /**
     * Get function constant.
     *
     * @param   con     Function constant we are looking for.
     * @return  Function in this model.
     */
    public Function getFunctionConstant(final FunctionConstant con) {
        return (Function) functionConstants.get(con);
    }

    /**
     * Create entity out of entity list. This is a transformation of a list
     * of elements into a class containing these elements.
     *
     * @param   array   List of elements.
     * @return  Class that contains (exactly?) these elements.
     */
    public Entity map(final Entity[] array) {
        if (array.length == 0) {
            return Entity.ZERO;
        } else if (array.length == 1) {
            if (array[0].getValue() == Entity.TWO.getValue()) {
                return Entity.ZERO;
            } else if (array[0].getValue() == Entity.ONE.getValue()) {
                return Entity.TWO;
            } else if (array[0].getValue() == Entity.ZERO.getValue()) {
                return Entity.ONE;
            } else {
                return Entity.ZERO;
            }
        } else {
            return Entity.TWO;
        }
    }

}
