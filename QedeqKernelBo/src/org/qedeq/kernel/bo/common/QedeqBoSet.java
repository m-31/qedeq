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

package org.qedeq.kernel.bo.common;

import java.util.Iterator;


/**
 * An instance of this interface represents a set of {@link QedeqBo}s.
 *
 * @author  Michael Meyling
 */
public interface QedeqBoSet {

    /**
     * Is element in set?
     *
     * @param   element    QedeqBo to check for.
     * @return  Is <code>element</code> in this set?
     * @throws  IllegalArgumentException if the element was a
     *          NullPointer
     */
    public boolean contains(final QedeqBo element);

    /**
     * Is this set empty?
     *
     * @return  Is this set empty?
     */
    public boolean isEmpty();

    /**
     * Get number of elements.
     *
     * @return  Number of elements in this set.
     */
    public int size();

    /**
     * Returns an iterator over the elements in this set.  The elements are
     * returned in no particular order (unless this set is an instance of some
     * class that provides a guarantee).
     *
     * @return  Iterator over the {@link QedeqBo} elements in this set.
     */
    public Iterator iterator();

    /**
     * Returns list of QEDEQ module names.
     *
     * @return  Comma separated list.
     */
    public String asShortList();

    /**
     * Returns list of QEDEQ module URLs.
     *
     * @return  Comma separated list.
     */
    public String asLongList();

}
