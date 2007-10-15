/* $Id: SourceArea.java,v 1.1 2007/04/12 23:50:10 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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
import java.net.URL;


/**
 * Describes a file area.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class SourceArea implements Serializable {

    /** Address of input, for identifying source. */
    private final URL address;

    /** Local address of input, for loading source. */
    private final URL localAddress;

    /** Start position. */
    private final SourcePosition startPosition;

    /** End position. Might be <code>null</code>. */
    private final SourcePosition endPosition;

    /**
     * Constructs source area object.
     *
     * @param   localAddress    source address
     * @param   startPosition   Start position.
     * @param   endPosition     Start position.
     */
    public SourceArea(final URL localAddress, final SourcePosition startPosition,
            final SourcePosition endPosition) {
        this(localAddress, localAddress, startPosition, endPosition);
    }

    /**
     * Constructs file position object.
     *
     * @param   address         For identifying source.
     * @param   localAddress    Source address.
     * @param   startPosition   Start position.
     * @param   endPosition     Start position.
     */
    public SourceArea(final URL address, final URL localAddress,
            final SourcePosition startPosition, final SourcePosition endPosition) {
        this.address = address;
        this.localAddress = localAddress;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    /**
     * Get address (or something to identify it) of input source.
     *
     * @return  address of input source
     */
    public final URL getAddress() {
        return this.address;
    }

    /**
     * Get local address (or something to identify it) of input source.
     *
     * @return  local address of input source
     */
    public final URL getLocalAddress() {
        return this.localAddress;
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
