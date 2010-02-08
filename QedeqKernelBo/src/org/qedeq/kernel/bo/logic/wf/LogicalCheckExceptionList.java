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

package org.qedeq.kernel.bo.logic.wf;

import java.util.ArrayList;
import java.util.List;

/**
 * Type save {@link org.qedeq.kernel.bo.logic.wf.LogicalCheckException} list.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class LogicalCheckExceptionList {

    /** List with parse exceptions. */
    private final List exceptions = new ArrayList();

    /**
     * Constructor.
     */
    public LogicalCheckExceptionList() {
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public void add(final LogicalCheckException e) {
        exceptions.add(e);
    }

    /**
     * Get number of collected exceptions.
     *
     * @return  Number of collected exceptions.
     */
    public int size() {
        return exceptions.size();
    }

    /**
     * Get <code>i</code>-th exception.
     *
     * @param   i   Starts with 0 and must be smaller than {@link #size()}.
     * @return  Wanted exception.
     */
    public LogicalCheckException get(final int i) {
        return (LogicalCheckException) exceptions.get(i);
    }

    /**
     * Contains this list any errors?
     *
     * @return  Do I contain any errors?
     */
    public boolean hasErrors() {
        return size() > 0;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            final LogicalCheckException e = get(i);
            buffer.append(i).append(": ");
            buffer.append(e.toString());
        }
        return buffer.toString();
    }

}
