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
 * One entity in our model.
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
        predicateConstants.put(new PredicateConstant("in", 2), Predicate.NOT_IS_TWO);
        predicateConstants.put(new PredicateConstant("notIn", 2), Predicate.NOT_LESS);
        predicateConstants.put(new PredicateConstant("isSet", 1), Predicate.TRUE);
        predicateConstants.put(new PredicateConstant("subclass", 2), Predicate.LESS);

        functionConstants = new HashMap();
        functionConstants.put(new FunctionConstant("emptySet", 0), Function.ZERO);
        functionConstants.put(new FunctionConstant("intersection", 2), Function.MIN);
        functionConstants.put(new FunctionConstant("union", 2), Function.MIN);
        functionConstants.put(new FunctionConstant("universalClass", 0), Function.TWO);

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

}
