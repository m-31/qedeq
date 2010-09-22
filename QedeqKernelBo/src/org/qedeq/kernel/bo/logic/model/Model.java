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

    /**
     * Constructor.
     */
    public Model() {
        entities = new ArrayList();
        entities.add(Entity.ZERO);
        entities.add(Entity.ONE);
        entities.add(Entity.TWO);

        functionPool = new ArrayList();

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
        predicateConstants.put(new PredicateVariable("equal", 2, 0), Predicate.EQUAL);
        predicateConstants.put(new PredicateVariable("in", 2, 0), Predicate.LESS);

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

    public Predicate getPredicateConst(PredicateVariable var) {
        return (Predicate) predicateConstants.get(var);
    }

}
