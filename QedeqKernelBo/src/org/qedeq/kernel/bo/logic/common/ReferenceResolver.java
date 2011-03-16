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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.kernel.se.base.list.Element;


/**
 * Resolver for references from formal proof lines. Specific to a certain formal proof.
 *
 * @author  Michael Meyling
 */
public interface ReferenceResolver {

    /**
     * Check if a reference is a proved formula.
     *
     * @param   reference   Reference to axiom, definition or proposition.
     * @return  Reference has a proved formula.
     */
    public boolean hasProvedFormula(String reference);

    /**
     * Get reference formula in a normalized format.
     *
     * @param   reference   Reference to axiom, definition or proposition.
     * @return  Already proved formula.
     */
    public Element getNormalizedReferenceFormula(String reference);


    /**
     * Get reference formula in an local format.
     *
     * @param   formula   Local formula to normalize.
     * @return  Normalized formula.
     */
    public Element getNormalizedFormula(Element formula);


    /**
     * Set line number of last proof line, that is ok. All previous proof lines
     * are also ok.
     *
     * @param   proofLineNumber Till this number (including this one) all proof
     *                          lines were successfully checked.
     */
    public void setLastProved(final int proofLineNumber);

}
