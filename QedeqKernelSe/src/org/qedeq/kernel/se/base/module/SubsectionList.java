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
 * List of nodes. In LaTeX terms a node is something like an "subsection".
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public interface SubsectionList {

    /**
     * Get size of list.
     *
     * @return  List size.
     */
    public int size();

    /**
     * Get <code>index</code>-th element of list.
     *
     * @param   index   Index.
     * @return  Index-th element.
     */
    public SubsectionType get(int index);
}
