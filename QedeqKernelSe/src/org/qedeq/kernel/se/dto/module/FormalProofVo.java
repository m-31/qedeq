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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLineList;


/**
 * Contains a formal proof for a proposition.
 *
 * @author  Michael Meyling
 */
public class FormalProofVo implements FormalProof {


    /** Proof lines. */
    private FormalProofLineList formalProofLineList;

    /**
     * Constructs an empty proof.
     */
    public FormalProofVo() {
        // nothing to do
    }

    /**
     * Set proof lines for formal proof.
     *
     * @param   formalProofLines    The proof lines.
     */
    public final void setFormalProofLineList(final FormalProofLineList formalProofLines) {
        this.formalProofLineList = formalProofLines;
    }

    public final FormalProofLineList getFormalProofLineList() {
        return formalProofLineList;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof FormalProofVo)) {
            return false;
        }
        final FormalProofVo other = (FormalProofVo) obj;
        return  EqualsUtility.equals(getFormalProofLineList(), other.getFormalProofLineList());
    }

    public int hashCode() {
        return (getFormalProofLineList() != null ? getFormalProofLineList().hashCode() : 0);
    }

    public String toString() {
        return "Formal Proof: " + getFormalProofLineList();
    }


}
