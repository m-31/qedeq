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

package org.qedeq.kernel.se.common;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.utility.EqualsUtility;


/**
 * Type save {@link org.qedeq.kernel.se.common.SourceFileException} list.
 *
 * @author  Michael Meyling
 */
public class SourceFileExceptionList extends Exception {

    /** List with parse exceptions. */
    private final List exceptions = new ArrayList();

    /**
     * Constructor.
     */
    public SourceFileExceptionList() {
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public SourceFileExceptionList(final SourceFileException e) {
        add(e);
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public SourceFileExceptionList(final SourceFileExceptionList e) {
        if (e != null) {
            for (int i = 0; i < e.size(); i++) {
                add(e.get(i));
            }
        }
    }

    /**
     * Clear list.
     */
    public void clear() {
        exceptions.clear();
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public void add(final SourceFileException e) {
        if (size() == 0) {
            initCause(e);
        }
        if (!exceptions.contains(e)) {
            exceptions.add(e);
        }
    }

    /**
     * Add exceptions of given list if they are not already included.
     *
     * @param   e   Add exceptions of this list.
     */
    public void add(final SourceFileExceptionList e) {
        if (e == null) {
            return;
        }
        for (int i = 0; i < e.size(); i++) {
            if (size() == 0) {
                initCause(e.get(i));
            }
            if (!exceptions.contains(e.get(i))) {
                exceptions.add(e.get(i));
            }
        }
    }

    /**
     * Get number of collected exceptions.
     *
     * @return  Number of collected exceptions.
     */
    public final int size() {
        return exceptions.size();
    }

    /**
     * Get <code>i</code>-th exception.
     *
     * @param   i   Starts with 0 and must be smaller than {@link #size()}.
     * @return  Wanted exception.
     */
    public final SourceFileException get(final int i) {
        return (SourceFileException) exceptions.get(i);
    }

    /**
     * Get all exceptions.
     *
     * @return  All exceptions.
     */
    public final SourceFileException[] toArray() {
        return (SourceFileException[]) exceptions.toArray(
            new SourceFileException[exceptions.size()]);
    }

    public int hashCode() {
        int result = 71;
        for (int i = 0; i < size(); i++) {
            result = result ^ (get(i) != null ? get(i).hashCode() : 0);
        }
        return result;
    }

    public boolean equals(final Object object) {
        if (!(object instanceof SourceFileExceptionList)) {
            return false;
        }
        SourceFileExceptionList sfl = (SourceFileExceptionList) object;
        if (size() != sfl.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!EqualsUtility.equals(get(i), sfl.get(i))) {
                return false;
            }
        }
        return true;
    }

    public String getMessage() {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            final SourceFileException e = get(i);
            buffer.append(i).append(": ");
            buffer.append(e.toString());
        }
        return buffer.toString();
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName());
        buffer.append("\n");
        buffer.append(getMessage());
        return buffer.toString();
    }

}
