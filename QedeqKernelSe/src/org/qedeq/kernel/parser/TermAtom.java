/* $Id: TermAtom.java,v 1.1 2007/05/10 00:37:51 m31 Exp $
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

package org.qedeq.kernel.parser;


/**
 * Parsed atom, this is a term constant, or an formula constant.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class TermAtom {

    /** Atom value. */
    private final String value;

    /**
     * Constructor.
     *
     * @param   value   Term atom
     */
    public TermAtom(final String value) {
        this.value = value;
    }

    /**
     * Get atom value.
     *
     * @return  Value.
     */
    final String getValue() {
        return this.value;
    }

}
