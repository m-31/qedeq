/* $Id: TooFewArgumentsException.java,v 1.3 2007/02/25 20:05:35 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.parser;

/**
 * To few arguments. More arguments were expected.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public class TooFewArgumentsException extends ParserException {

    /**
     * Constructor.
     *
     * @param   position    Error position.
     * @param   expected    Expected argument number.
     */
    public TooFewArgumentsException(final long position, final int expected) {
        super(position, "To few arguments. Expected: " + expected);
    }
}
