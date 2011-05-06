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
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.Hypothesis;


/**
 * Hypothesis that can be used as an assumption within a proof.
 *
 * @author  Michael Meyling
 */
public class HypothesisVo implements Hypothesis {

    /** Label for back references. Might be <code>null</code>. */
    private String label;

    /** Derived formula. */
    private Formula formula;

    /**
     * Constructs an proof line.
     *
     * @param   formula     New derived formula.
     */
    public HypothesisVo(final Formula formula) {
        this.label = null;
        this.formula = formula;
    }

    /**
     * Constructs an proof line.
     *
     * @param   label       Label for back references. Might be <code>null</code>.
     * @param   formula     New derived formula.
     */
    public HypothesisVo(final String label, final Formula formula) {
        this.label = label;
        this.formula = formula;
    }

    /**
     * Default constructor.
     */
    public HypothesisVo() {
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

    public boolean equals(final Object obj) {
        if (!(obj instanceof HypothesisVo)) {
            return false;
        }
        final HypothesisVo other = (HypothesisVo) obj;
        return  EqualsUtility.equals(label, other.label)
          && EqualsUtility.equals(formula, other.formula);
    }

    public int hashCode() {
        return (label != null ? label.hashCode() : 0)
           ^ (formula != null ?  1 ^ formula.hashCode() : 0);
    }

    public String toString() {
        return (label != null ? "[" + label + "] " : "    ") + getFormula() + " "
            + "Hypothesis";
    }

}
