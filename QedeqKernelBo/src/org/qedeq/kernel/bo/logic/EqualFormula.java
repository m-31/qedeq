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

package org.qedeq.kernel.bo.logic;

import org.qedeq.kernel.base.list.Element;


/**
 * Encapsulates a logical formula.
 * LATER mime 20050205: what for is this anyway???
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
final class EqualFormula {

    /** Element that defines the logical content. */
    private final Element formula;


    /**
     * Constructor.
     *
     * @param   formula     Element that defines the logical content.
     */
    EqualFormula(final Element formula) {
        this.formula = formula;
    }

    public String toString() {
        return formula.toString();
    }

}
