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

package org.qedeq.kernel.bo.parser;

/**
 * Closing bracket expected but is missing.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class ClosingBracketMissingException extends ParserException {

    /**
     * Constructor.
     *
     * @param   position    Error position.
     * @param   bracket     Expected bracket.
     * @param   foundToken  Found token.
     */
    public ClosingBracketMissingException(final long position, final String bracket,
            final String foundToken) {
        super(position, "Closing bracket for \"" + bracket + "\" expected. Found: \"" + foundToken
            + "\"");
    }
}
