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
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.ReasonType;


/**
 * Contains a formal proof for a proposition.
 *
 * @author  Michael Meyling
 */
public class FormalProofLineVo implements FormalProofLine {

    /** Label for back references. Might be <code>null</code>. */
    private String label;

    /** Rule that is used for deriving. */
    private ReasonType reasonType;

    /** Derived formula. */
    private Formula formula;

    /**
     * Constructs an proof line.
     *
     * @param   formula     New derived formula.
     * @param   reasonType  Rule that was used to derive the formula.
     */
    public FormalProofLineVo(final Formula formula, final ReasonType reasonType) {
        this.label = null;
        this.reasonType = reasonType;
        this.formula = formula;
    }

    /**
     * Constructs an proof line.
     *
     * @param   label       Label for back references. Might be <code>null</code>.
     * @param   formula     New derived formula.
     * @param   reasonType  Rule that was used to derive the formula.
     */
    public FormalProofLineVo(final String label, final Formula formula, final ReasonType reasonType) {
        this.label = label;
        this.reasonType = reasonType;
        this.formula = formula;
    }

    /**
     * Default constructor.
     */
    public FormalProofLineVo() {
        // nothing to do
    }

    public Formula getFormula() {
        return formula;
    }

    /**
     * Set proof line formula.
     *
     * @param   formula Set formula.
     */
    public void setFormula(final Formula formula) {
        this.formula = formula;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Set label for proof line.
     *
     * @param   label   Set this label.
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    public ReasonType getReasonType() {
        return reasonType;
    }

    /**
     * Set reason type for proof line.
     *
     * @param   reasonType      Set this reason type.
     */
    public void setReasonType(final ReasonType reasonType) {
        this.reasonType = reasonType;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof FormalProofLineVo)) {
            return false;
        }
        final FormalProofLineVo other = (FormalProofLineVo) obj;
        return  EqualsUtility.equals(label, other.label)
          && EqualsUtility.equals(formula, other.formula)
          && EqualsUtility.equals(reasonType, other.reasonType);
    }

    public int hashCode() {
        return (label != null ? label.hashCode() : 0)
           ^ (formula != null ?  1 ^ formula.hashCode() : 0)
           ^ (reasonType != null ?  2 ^ reasonType.hashCode() : 0);
    }

    public String toString() {
        return (label != null ? "[" + label + "] " : "    ") + getFormula() + " "
            + getReasonType();
    }

}
