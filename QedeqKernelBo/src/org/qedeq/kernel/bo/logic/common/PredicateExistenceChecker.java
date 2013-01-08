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

package org.qedeq.kernel.bo.logic.common;


/**
 * Check if a predicate is already defined.
 *
 * @author  Michael Meyling
 */
public interface PredicateExistenceChecker {

    /**
     * Check if a predicate is already defined.
     *
     * @param   name        Predicate name.
     * @param   arguments   Number of operands for the predicate.
     * @return  Predicate is defined.
     */
    public boolean predicateExists(String name, int arguments);

    /**
     * Check if a predicate is already defined.
     *
     * @param   predicate   Predicate.
     * @return  Predicate is defined.
     */
    public boolean predicateExists(PredicateKey predicate);

    /**
     * Check if given predicate key has an initial predicate definition.
     *
     * @param   predicate   Predicate.
     * @return  Predicate is defined and is an initial predicate definition.
     */
    public boolean isInitialPredicate(final PredicateKey predicate);

}
