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

package org.qedeq.kernel.se.base.module;


/**
 * Proposition.
 *
 * @version $Revision: 1.10 $
 * @author  Michael Meyling
 */
public interface Proposition extends NodeType {

    /**
     * Get formula that is a proposition.
     *
     * @return  Proposition formula.
     */
    public Formula getFormula();

    /**
     * Get description. Only necessary if formula is not self-explanatory.
     *
     * @return  Description.
     */
    public LatexList getDescription();

    /**
     * Get non formal proofs for proposition.
     *
     * @return  Proposition proofs.
     */
    public ProofList getProofList();

    /**
     * Get formal proofs for proposition.
     *
     * @return  Proposition proofs.
     */
    public FormalProofList getFormalProofList();

}
