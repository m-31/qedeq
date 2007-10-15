/* $Id: PredicateExistenceChecker.java,v 1.2 2007/02/25 20:05:35 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic;


/**
 * Check if a predicate is already defined.
 *
 * @version $Revision: 1.2 $
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

}
