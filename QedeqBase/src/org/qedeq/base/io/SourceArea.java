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

package org.qedeq.base.io;

import java.io.Serializable;



/**
 * Describes an area of an URL contents.
 *
 * @author  Michael Meyling
 */
public final class SourceArea implements Serializable {

    /** Address of input, for identifying source. */
    private final String address;

    /** Start position. */
    private final SourcePosition startPosition;

    /** End position. Might be <code>null</code>. */
    private final SourcePosition endPosition;

    /**
     * Constructs file position object.
     *
     * @param   address         For identifying source. Must not be <code>null</code>.
     * @param   startPosition   Start position. Must not be <code>null</code>.
     * @param   endPosition     Start position. Must not be <code>null</code>.
     */
    public SourceArea(final String address, final SourcePosition startPosition,
            final SourcePosition endPosition) {
        this.address = address;
        if (address == null || startPosition == null || endPosition == null) {
            throw new NullPointerException();
        }
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    /**
     * Get address (or something to identify it) of input source.
     *
     * @return  address of input source
     */
    public final String getAddress() {
        return this.address;
    }

    /**
     * Get start position.
     *
     * @return  Start position.
     */
    public final SourcePosition getStartPosition() {
        return startPosition;
    }

    /**
     * Get end position.
     *
     * @return  End position.
     */
    public final SourcePosition getEndPosition() {
        return endPosition;
    }

    public final int hashCode() {
        return getAddress().hashCode() ^ getStartPosition().hashCode() ^ getEndPosition().hashCode();
    }

    public final boolean equals(final Object obj) {
        if (!(obj instanceof SourceArea)) {
            return false;
        }
        final SourceArea other = (SourceArea) obj;
        return getAddress().equals(other.getAddress())
            && getStartPosition().equals(other.getStartPosition())
            && getEndPosition().equals(other.getEndPosition());
    }

    public final String toString() {
        return getAddress() + ":" + getStartPosition().getRow() + ":" + getStartPosition().getColumn()
            + ":" + getEndPosition().getRow() + ":" + getEndPosition().getColumn();
    }

}
