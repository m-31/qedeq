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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.LatexList;


/**
 * Contains a formal proof for a proposition.
 *
 * @author  Michael Meyling
 */
public class FormalProofVo implements FormalProof {

    /** Preceding LaTeX text. This text comes before the formal proof. */
    private LatexList precedingText;

    /** Proof lines. */
    private FormalProofLineList formalProofLineList;

    /** Succeeding LaTeX text. This text comes after the formal proof. */
    private LatexList succeedingText;

    /**
     * Constructs an empty proof.
     */
    public FormalProofVo() {
        // nothing to do
    }

    /**
     * Set preceding LaTeX text. This text comes before a formal proof.
     *
     * @param   precedingText   Preceding LaTeX text.
     */
    public final void setPrecedingText(final LatexListVo precedingText) {
        this.precedingText = precedingText;
    }

    public final LatexList getPrecedingText() {
        return precedingText;
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

    /**
     * Set succeeding LaTeX text. This text comes after a formal proof.
     *
     * @param   succeedingText  Succeeding LaTeX text.
     */
    public final void setSucceedingText(final LatexListVo succeedingText) {
        this.succeedingText = succeedingText;
    }

    public final LatexList getSucceedingText() {
        return succeedingText;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof FormalProofVo)) {
            return false;
        }
        final FormalProofVo other = (FormalProofVo) obj;
        return EqualsUtility.equals(getPrecedingText(), other.getPrecedingText())
          && EqualsUtility.equals(getFormalProofLineList(), other.getFormalProofLineList())
          && EqualsUtility.equals(getSucceedingText(), other.getSucceedingText());
    }

    public int hashCode() {
        return (getPrecedingText() != null ? getPrecedingText().hashCode() : 0)
            ^ (getFormalProofLineList() != null ? 2 ^ getFormalProofLineList().hashCode() : 0)
            ^ (getSucceedingText() != null ? 3 ^ getSucceedingText().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer result = new StringBuffer();
        if (getPrecedingText() != null) {
            result.append(getPrecedingText());
        }
        if (getFormalProofLineList() != null) {
            result.append("Formal Proof: " + getFormalProofLineList());
        }
        if (getSucceedingText() != null) {
            result.append(getSucceedingText());
        }
        return result.toString();
    }


}
