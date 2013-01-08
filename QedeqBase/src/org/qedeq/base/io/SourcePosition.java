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

package org.qedeq.base.io;

import java.io.Serializable;

/**
 * Describes a file position within a text file.
 *
 * @author  Michael Meyling
 */
public final class SourcePosition implements Serializable {

    /** Begin of file. */
    public static final SourcePosition BEGIN = new SourcePosition(1, 1);

    /** Line number, starting with 1. */
    private int row;

    /** Column number, starting with 1. */
    private int column;

    /**
     * Constructs source position object.
     *
     * @param   row     Line number, starting with 1.
     * @param   column  Column number, starting with 1.
     */
    public SourcePosition(final int row, final int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Get line number, starting with 1.
     *
     * @return  Line number.
     */
    public final int getRow() {
        return row;
    }

    /**
     * Get column number, starting with 1.
     *
     * @return  column number
     */
    public final int getColumn() {
        return column;
    }

    public final int hashCode() {
        return getRow() ^ (getColumn() * 13);
    }

    public final boolean equals(final Object obj) {
        if (!(obj instanceof SourcePosition)) {
            return false;
        }
        final SourcePosition other = (SourcePosition) obj;
        return (getRow() == other.getRow())
            && (getColumn() == other.getColumn());
    }

    public final String toString() {
        return getRow() + ":" + getColumn();
    }

}
