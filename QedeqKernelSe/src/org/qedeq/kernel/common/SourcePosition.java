/* $Id: SourcePosition.java,v 1.1 2007/04/12 23:50:10 m31 Exp $
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
 * Describes a file position.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class SourcePosition implements Serializable {

    /** Address of input, for identifying source. */
    private final URL address;

    /** Local address of input, for loading source. */
    private final URL localAddress;

    /** Line number, starting with 1. */
    private int line;

    /** Column number, starting with 1. */
    private int column;

    /**
     * Constructs source position object.
     *
     * @param   localAddress    source address
     * @param   line            Line number.
     * @param   column          Column number.
     */
    public SourcePosition(final URL localAddress, final int line, final int column) {
        this.address = localAddress;
        this.localAddress = localAddress;
        this.line = line;
        this.column = column;
    }

    /**
     * Constructs file position object.
     *
     * @param   address         for identifying source
     * @param   localAddress    source address
     * @param   line            Line number.
     * @param   column          Column number.
     */
    public SourcePosition(final URL address, final URL localAddress,
            final int line, final int column) {
        this.address = address;
        this.localAddress = localAddress;
        this.line = line;
        this.column = column;
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
     * Get line number, starting with 1.
     *
     * @return  Line number.
     */
    public final int getLine() {
        return line;
    }

    /**
     * Get column number, starting with 1.
     *
     * @return  column number
     */
    public final int getColumn() {
        return column;
    }

// LATER mime 20050608: remove if no use
/*
    public final int findCaretPosition(final int line, final int column, final String source) {
        if (line == 1) {
            return 0;
        }
        int k = 1;
        for (int j = 0; j < source.length(); j++) {
            if (source.charAt(j) == '\n') {
                k++;
            }
            if (k == line) {
                j += column - 1;
                if (j > source.length()) {
                    j = source.length();
                }
                return j;
            }
        }
        return 0;
    }
*/

    public final String toString() {
        return getAddress() + ":" + getLine() + ":" + getColumn();
    }

}
