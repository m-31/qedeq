/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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
 * Contains a formal proof for a proposition.
 *
 * @author  Michael Meyling
 */
public interface FormalProof {

    /**
     * Get text before the formula. Get the preceding LaTeX text. This text comes before a
     * formal proof.
     *
     * @return  Returns the preceding LaTeX text.
     */
    public LatexList getPrecedingText();

    /**
     *  Get proof content.
     *
     * @return  LaTeX proof text.
     */
    public FormalProofLineList getFormalProofLineList();

    /**
     * Get text after the formula. Get the succeeding LaTeX text. This text comes after
     * a formal proof.
     *
     * @return  Returns the succeedingText.
     */
    public LatexList getSucceedingText();

}
