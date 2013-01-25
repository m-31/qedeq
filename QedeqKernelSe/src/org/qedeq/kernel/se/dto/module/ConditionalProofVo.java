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

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.Conclusion;
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.Hypothesis;
import org.qedeq.kernel.se.base.module.Reason;


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
    private FormalProofLineList formalProofLineList;

    /** Conclusion. */
    private Conclusion conclusion;

    /**
     * Constructs an reason.
     *
     * @param   hypothesis              Free subject variable that will be replaced.
     * @param   proofLines              Bound subject variable that will be substituted.
     * @param   conclusion              Conclusion.
     */
    public ConditionalProofVo(final Hypothesis hypothesis, final FormalProofLineList proofLines,
            final Conclusion conclusion) {
        this.hypothesis = hypothesis;
        this.formalProofLineList = proofLines;
        this.conclusion = conclusion;
    }

    /**
     * Default constructor.
     */
    public ConditionalProofVo() {
        // nothing to do
    }

    public ConditionalProof getConditionalProof() {
        return this;
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
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    /**
     * Set formal proof lines.
     *
     * @param   list    Proof lines that might use hypothesis.
     */
    public void setFormalProofLineList(final FormalProofLineList list) {
        this.formalProofLineList = list;
    }

    public FormalProofLineList getFormalProofLineList() {
        return formalProofLineList;
    }

    public Conclusion getConclusion() {
        return conclusion;
    }

    /**
     * Set conclusion.
     *
     * @param   conclusion  New derived formula.
     */
    public void setConclusion(final Conclusion conclusion) {
        this.conclusion = conclusion;
    }

    public Formula getFormula() {
        if (conclusion == null)  {
            return null;
        }
        return conclusion.getFormula();
    }

    public String getLabel() {
        if (conclusion == null)  {
            return null;
        }
        return conclusion.getLabel();
    }

    public String getName() {
        return "CP";
    }

    public Reason getReason() {
        return this;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ConditionalProofVo)) {
            return false;
        }
        final ConditionalProofVo other = (ConditionalProofVo) obj;
        return EqualsUtility.equals(hypothesis, other.hypothesis)
            && EqualsUtility.equals(formalProofLineList, other.formalProofLineList)
            && EqualsUtility.equals(conclusion, other.conclusion);
    }

    public int hashCode() {
        return (hypothesis != null ? hypothesis.hashCode() : 0)
            ^ (formalProofLineList != null ? 2 ^ formalProofLineList.hashCode() : 0)
            ^ (conclusion != null ? 4 ^ conclusion.hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(getName());
        if (hypothesis != null || formalProofLineList != null || conclusion != null) {
            result.append(" (");
            if (hypothesis != null) {
                result.append("\n");
                result.append(hypothesis);
            }
            if (formalProofLineList != null) {
                result.append("\n");
                result.append(formalProofLineList);
            }
            if (conclusion != null) {
                result.append("\n");
                result.append(conclusion);
            }
            result.append("\n");
            result.append(")");
        }
        return result.toString();
    }

}
