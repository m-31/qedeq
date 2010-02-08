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

package org.qedeq.kernel.common;

import java.io.Serializable;


/**
 * Describes an area of an URL contents.
 *
 * @version $Revision: 1.4 $
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
     * @param   address         For identifying source.
     * @param   startPosition   Start position. Must not be <code>null</code>.
     * @param   endPosition     Start position. Must not be <code>null</code>.
     */
    public SourceArea(final String address, final SourcePosition startPosition,
            final SourcePosition endPosition) {
        this.address = address;
        if (startPosition == null || endPosition == null) {
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


    public final String toString() {
        return getAddress()
            + (getStartPosition() != null ? ":" + getStartPosition().getLine() + ":"
            + getStartPosition().getColumn() : "")
            + (getEndPosition() != null ? ":" + getEndPosition().getLine() + ":"
            + getEndPosition().getColumn() : "");
    }

}
