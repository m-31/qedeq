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

package org.qedeq.kernel.common;


/**
 * List of {@link org.qedeq.kernel.common.SourceFileException}s.
 *
 * @author  Michael Meyling
 */
public abstract class SourceFileExceptionList extends Exception {

    /**
     * Get number of collected exceptions.
     *
     * @return  Number of collected exceptions.
     */
    public abstract int size();

    /**
     * Get <code>i</code>-th exception.
     *
     * @param   i   Starts with 0 and must be smaller than {@link #size()}.
     * @return  Wanted exception.
     */
    public abstract SourceFileException get(int i);

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public abstract void add(SourceFileException e);

    /**
     * Get all exceptions.
     *
     * @return  All exceptions.
     */
    public abstract SourceFileException[] toArray();

}
