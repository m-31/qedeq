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

package org.qedeq.kernel.se.base.module;


/**
 * Encapsulate for different subsection types.
 *
 * @author  Michael Meyling
 */
public interface SubsectionType {

    /**
     * Get Node, if this is an instance of {@link Node}.
     *
     * @return  Node, maybe <code>null</code>.
     */
    public Node getNode();

    /**
     * Get subsection, if this is an instance of {@link Subsection}.
     *
     * @return  Subsection, maybe <code>null</code>.
     */
    public Subsection getSubsection();

}
