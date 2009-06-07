/* $Id: TooFewArgumentsException.java,v 1.1 2008/07/26 07:58:30 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
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
 * To few arguments. More arguments were expected.
 *
 * @version $Revision: 1.1 $
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
