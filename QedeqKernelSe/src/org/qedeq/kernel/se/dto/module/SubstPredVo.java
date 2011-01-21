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
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.SubstPred;


/**
 * Usage of rule for substitute predicate variable.
 *
 * @author  Michael Meyling
 */
public class SubstPredVo implements SubstPred {

    /** Reference to previously proven formula. */
    private String reference;

    /** Function variable that will be substituted. */
    private Element predicateVariable;

    /** Replacement formula. */
    private Element substituteFormula;

    /**
     * Constructs an reason.
     *
     * @param   reference                   Reference to a valid formula.
     * @param   predicateVariable           Predicate variable that will be substituted.
     * @param   substituteFormula           Replacement formula.
     */

    public SubstPredVo(final String reference, final Element predicateVariable,
            final Element substituteFormula) {
        this.reference = reference;
        this.predicateVariable = predicateVariable;
        this.substituteFormula = substituteFormula;
    }

    /**
     * Default constructor.
     */
    public SubstPredVo() {
        // nothing to do
    }

    public String getReference() {
        return reference;
    }

    /**
     * Set formula reference.
     *
     * @param   reference   Reference to formula.
     */
    public void setReference(final String reference) {
        this.reference = reference;
    }

    public String[] getReferences() {
        if (reference == null || reference.length() == 0) {
            return new String[] {};
        } else {
            return new String[] {reference };
        }
    }

    public Element getPredicateVariable() {
        return predicateVariable;
    }

    public Element getSubstituteFormula() {
        return substituteFormula;
    }

    public String getName() {
        return "SubstFree";
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof SubstPredVo)) {
            return false;
        }
        final SubstPredVo other = (SubstPredVo) obj;
        return EqualsUtility.equals(reference, other.reference)
            && EqualsUtility.equals(predicateVariable, other.predicateVariable)
            && EqualsUtility.equals(substituteFormula, other.substituteFormula);
    }

    public int hashCode() {
        return (reference != null ? reference.hashCode() : 0)
            ^ (predicateVariable != null ? 2 ^ predicateVariable.hashCode() : 0)
            ^ (substituteFormula != null ? 3 ^ substituteFormula.hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("SubstFree");
        if (reference != null || predicateVariable != null
                || substituteFormula != null) {
            result.append(" (");
            boolean w = false;
            if (reference != null) {
                result.append(reference);
                w = true;
            }
            if (predicateVariable != null) {
                if (w) {
                    result.append(", ");
                }
                result.append(predicateVariable);
                w = true;
            }
            if (substituteFormula != null) {
                if (w) {
                    result.append(", ");
                }
                result.append("by ");
                result.append(substituteFormula);
                w = true;
            }
            result.append(")");
        }
        return result.toString();
    }

}
