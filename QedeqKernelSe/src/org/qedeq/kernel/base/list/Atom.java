/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.base.list;


/**
 * Element atom. This is a leaf of the composite {@link org.qedeq.kernel.base.list.Element}.
 *
 * @version $Revision: 1.3 $
 * @author    Michael Meyling
 */
public interface Atom extends Element {

    /**
     * Get the text.
     *
     * @return  text    Atom data.
     */
    public String getString();

}
