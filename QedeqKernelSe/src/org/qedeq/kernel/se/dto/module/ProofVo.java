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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.Proof;


/**
 * Contains a non formal proof for a proposition or rule.
 *
 * @author  Michael Meyling
 */
public class ProofVo implements Proof {

    /** Kind of proof. */
    private String kind;

    /** Proof detail level. */
    private String level;

    /** LaTeX text for non formal proof. */
    private LatexList nonFormalProof;

    /**
     * Constructs an empty proof.
     */
    public ProofVo() {
        // nothing to do
    }

    public String getKind() {
        return kind;
    }

    /**
     * Set kind of proof. E.g. "informal".
     *
     * @param   kind    Set proof type.
     */
    public final void setKind(final String kind) {
        this.kind = kind;
    }

    public String getLevel() {
        return level;
    }

    /**
     * Set proof level. Higher proof levels contain more detailed proofs.
     *
     * @param   level   Proof level.
     */
    public final void setLevel(final String level) {
        this.level = level;
    }

    /**
     * Set LaTeX text for non formal proof.
     *
     * @param   nonFormalProof  The proof text.
     */
    public final void setNonFormalProof(final LatexList nonFormalProof) {
        this.nonFormalProof = nonFormalProof;
    }

    public final LatexList getNonFormalProof() {
        return nonFormalProof;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ProofVo)) {
            return false;
        }
        final ProofVo other = (ProofVo) obj;
        return  EqualsUtility.equals(getKind(), other.getKind())
            && EqualsUtility.equals(getLevel(), other.getLevel())
            && EqualsUtility.equals(getNonFormalProof(), other.getNonFormalProof());
    }

    public int hashCode() {
        return (getKind() != null ? getKind().hashCode() : 0)
            ^ (getLevel() != null ? 1 ^ getLevel().hashCode() : 0)
            ^ (getNonFormalProof() != null ? 2 ^ getNonFormalProof().hashCode() : 0);
    }

    public String toString() {
        return "Proof (" + getKind() + ", " + getLevel() +  "): " + getNonFormalProof();
    }

}
