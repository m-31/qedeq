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
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Hypothesis;


/**
 * Usage of conditional proof method. If you can derive the proposition A out of
 * the assumed formulas then the following formula is true:
 * conjunction of the assumed formulas implies A.
 *
 * @author  Michael Meyling
 */
public class ConditionalProofVo implements ConditionalProof {

    /** Hypothesis. */
    private Hypothesis hypothesis;

    /** Proof lines term. */
    private FormalProofLineList proofLines;

    /**
     * Constructs an reason.
     *
     * @param   hypothesis             Free subject variable that will be replaced.
     * @param   proofLines             Bound subject variable that will be substituted.
     */
    public ConditionalProofVo(final Hypothesis hypothesis, final FormalProofLineList proofLines) {
        this.hypothesis = hypothesis;
        this.proofLines = proofLines;
    }

    /**
     * Default constructor.
     */
    public ConditionalProofVo() {
        // nothing to do
    }

    public Hypothesis getHypothesis() {
        return hypothesis;
    }

    /**
     * Set hypothesis.
     *
     * @param   hypothesis  Reference to formula.
     */
    public void setHypothesis(final Hypothesis hypothesis) {
        this.hypothesis = hypothesis;
    }

    public String[] getReferences() {
        return new String[0];
    }

    public FormalProofLineList getFormalProofLineList() {
        return proofLines;
    }

    /**
     * Set formal proof lines.
     *
     * @param   proofLines  Proof lines that might use hypothesis.
     */
    public void setFormalProofLinesList(final FormalProofLineList proofLines) {
        this.proofLines = proofLines;
    }

    public String getName() {
        return "CP";
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ConditionalProofVo)) {
            return false;
        }
        final ConditionalProofVo other = (ConditionalProofVo) obj;
        return EqualsUtility.equals(hypothesis, other.hypothesis)
            && EqualsUtility.equals(proofLines, other.proofLines);
    }

    public int hashCode() {
        return (hypothesis != null ? hypothesis.hashCode() : 0)
            ^ (proofLines != null ? 2 ^ proofLines.hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("CP");
        if (hypothesis != null || proofLines != null) {
            result.append(" (");
            if (hypothesis != null) {
                result.append("\n");
                result.append(hypothesis);
            }
            if (proofLines != null) {
                result.append("\n");
                result.append(proofLines);
            }
            result.append("\n");
            result.append(")");
        }
        return result.toString();
    }

}
