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

package org.qedeq.kernel.se.common;

/**
 * An instance of this interface can answer the question which percentage is already finished.
 *
 * @author  Michael Meyling
 */
public interface ServiceCompleteness {

    /**
     * Get calculated visit percentage.
     * This is a monotonically nondecreasing visit function.
     *
     * @return  Value between 0 and 100.
     */
    public double getVisitPercentage();

    /**
     * Get readable description of current location.
     *
     * @return  Description.
     */
    public String getLocationDescription();

}
