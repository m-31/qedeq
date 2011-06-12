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
import org.qedeq.kernel.se.common.ModuleContext;


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
    public boolean isProvedFormula(String reference);

    /**
     * Get reference formula in a normalized format.
     *
     * @param   reference   Reference to axiom, definition or proposition.
     * @return  Already proved formula.
     */
    public Element getNormalizedReferenceFormula(String reference);


    /**
     * Get formula in a normalized format.
     *
     * @param   element   Local formula to normalize.
     * @return  Normalized formula.
     */
    public Element getNormalizedFormula(Element element);


    /**
     * Is this a local proof line reference?
     *
     * @param   reference   Local proof line reference to check for.
     * @return  Is this a local proof line reference for the caller?
     */
    public boolean isLocalProofLineReference(String reference);

    /**
     * Get local for proof line reference.
     *
     * @param   reference   Local proof line reference to check for.
     * @return  Local proof line for the caller. Might be <code>null</code>.
     */
    public Element getNormalizedLocalProofLineReference(String reference);

    /**
     * Module context for proof line reference.
     *
     * @param   reference   Local proof line reference to check for.
     * @return  Local proof line reference for the caller. Might be <code>null</code>.
     */
    public ModuleContext getReferenceContext(String reference);

}
