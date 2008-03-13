/* $Id: AxiomVo.java,v 1.10 2007/05/10 00:37:50 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.dto.module;

import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Axiom.
 *
 * @version $Revision: 1.10 $
 * @author  Michael Meyling
 */
public class AxiomVo implements Axiom {

    /** Axiom formula. */
    private Formula formula;

    /** Further proposition description. Normally <code>null</code>. */
    private LatexList description;

    /**
     * Constructs a new axiom.
     */
    public AxiomVo() {
        // nothing to do
    }

    public Axiom getAxiom() {
        return this;
    }

    public PredicateDefinition getPredicateDefinition() {
        return null;
    }

    public FunctionDefinition getFunctionDefinition() {
        return null;
    }

    public Proposition getProposition() {
        return null;
    }

    public Rule getRule() {
        return null;
    }

    /**
     * Set axiom formula.
     *
     * @param   formula Set axiom formula.
     */
    public final void setFormula(final FormulaVo formula) {
        this.formula = formula;
    }

    public final Formula getFormula() {
        return formula;
    }

    /**
     * Set description. Only necessary if formula is not self-explanatory.
     *
     * @param   description Description.
     */
    public final void setDescription(final LatexListVo description) {
        this.description = description;
    }

    public LatexList getDescription() {
        return description;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof AxiomVo)) {
            return false;
        }
        final AxiomVo other = (AxiomVo) obj;
        return  EqualsUtility.equals(getFormula(), other.getFormula())
            && EqualsUtility.equals(getDescription(), other.getDescription());
    }

    public int hashCode() {
        return (getFormula() != null ? getFormula().hashCode() : 0)
            ^ (getDescription() != null ? 1 ^ getDescription().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Axiom:\n");
        buffer.append(getFormula());
        buffer.append("\nDescription:\n");
        buffer.append(getDescription());
        return buffer.toString();
    }
}
