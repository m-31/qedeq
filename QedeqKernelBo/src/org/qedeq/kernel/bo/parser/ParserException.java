/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.parser;

/**
 * Base for parser exceptions.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public abstract class ParserException extends Exception {

    /** Error position within input. */
    private final long position;

    /**
     * Constructor.
     *
     * @param   position    Error position within input.
     * @param   message     Error message.
     */
    public ParserException(final long position, final String message) {
        super(message);
        this.position = position;
    }

    /**
     * Get error position.
     *
     * @return  Error position.
     */
    public final long getPosition() {
        return position;
    }

}
