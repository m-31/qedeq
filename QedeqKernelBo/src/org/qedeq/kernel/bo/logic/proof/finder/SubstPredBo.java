/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic.proof.finder;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.SubstPred;


/**
 * Usage of rule for substitute predicate variable.
 *
 * @author  Michael Meyling
 */
public class SubstPredBo implements SubstPred {

    /** Reference to previously proven formula. */
    private int n;

    /** Function variable that will be substituted. */
    private Element predicateVariable;

    /** Replacement formula. */
    private Element substituteFormula;

    /**
     * Constructs an reason.
     *
     * @param   n                           Reference to a valid formula.
     * @param   predicateVariable           Predicate variable that will be substituted.
     * @param   substituteFormula           Replacement formula.
     */

    public SubstPredBo(final int n, final Element predicateVariable,
            final Element substituteFormula) {
        this.n = n;
        this.predicateVariable = predicateVariable;
        this.substituteFormula = substituteFormula;
    }

    public SubstPred getSubstPred() {
        return this;
    }

    public String getReference() {
        return "" + n;
    }

    /**
     * Set formula reference.
     *
     * @param   n   Reference to formula.
     */
    public void setN(final int n) {
        this.n = n;
    }

    /**
     * Get formula reference.
     *
     * @return  Reference to formula.
     */
    public int getN() {
        return n;
    }

    public String[] getReferences() {
        return new String[] {getReference()};
    }

    public Element getPredicateVariable() {
        return predicateVariable;
    }

    public Element getSubstituteFormula() {
        return substituteFormula;
    }

    public String getName() {
        return "SubstPred";
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof SubstPred)) {
            return false;
        }
        final SubstPred other = (SubstPred) obj;
        return EqualsUtility.equals(getReference(), other.getReference())
            && EqualsUtility.equals(predicateVariable, other.getPredicateVariable())
            && EqualsUtility.equals(substituteFormula, other.getSubstituteFormula());
    }

    public int hashCode() {
        return getReference().hashCode()
            ^ (2 ^ getPredicateVariable().hashCode())
            ^ (3 ^ getSubstituteFormula().hashCode());
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("SubstPred");
        result.append(" (");
        result.append(getReference());
        if (getPredicateVariable() != null) {
            result.append(", ");
            result.append(getPredicateVariable());
        }
        if (getSubstituteFormula() != null) {
            result.append("by ");
            result.append(getSubstituteFormula());
        }
        result.append(")");
        return result.toString();
    }

}
