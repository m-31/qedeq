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
import java.util.List;

/**
 * One entity in our model.
 *
 * @author  Michael Meyling
 */
public final class Model {

    /** List of all entities in model. */
    private final List entities;

    /** List of functions for different argument numbers. */
    private final List functions;

    /** List of predicates for different argument numbers. */
    private final List predicates;

    /**
     * Constructor.
     */
    public Model() {
        entities = new ArrayList();
        entities.add(Entity.ZERO);
        entities.add(Entity.ONE);
        entities.add(Entity.TWO);

        functions = new ArrayList();

        predicates = new ArrayList();

        final List predicate0 = new ArrayList();
        predicates.add(predicate0);
        predicate0.add(Predicate.FALSE);
        predicate0.add(Predicate.TRUE);

        final List predicate1 = new ArrayList();
        predicates.add(predicate1);
        predicate1.add(Predicate.FALSE);
        predicate1.add(Predicate.TRUE);

        final List predicate2 = new ArrayList();
        predicates.add(predicate2);
        predicate2.add(Predicate.FALSE);
        predicate2.add(Predicate.TRUE);
        predicate2.add(Predicate.LESS);
        predicate2.add(Predicate.EQUAL);

    }

    public int getEntitiesSize() {
        return entities.size();
    }

    public Entity getEntity(final int number) {
        return (Entity) entities.get(number);
    }

    public int getPredicateSize(final int size) {
        if (predicates.size() <=  size) {
            return 0;
        }
        return ((List) predicates.get(size)).size();
    }

    public Predicate getPredicate(final int size, final int number) {
        return (Predicate) ((List) predicates.get(size)).get(number);
    }

}
