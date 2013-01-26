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
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.SubstFree;


/**
 * Usage of rule for substitute free subject variable.
 *
 * @author  Michael Meyling
 */
public class SubstFreeVo implements SubstFree {

    /** Reference to previously proven formula. */
    private String reference;

    /** Free subject variable that will be replaced. */
    private Element subjectVariable;

    /** Replacement term. */
    private Element substituteTerm;

    /**
     * Constructs an reason.
     *
     * @param   reference                   Reference to a valid formula.
     * @param   subjectVariable             Bound subject variable that will be substituted.
     * @param   substituteTerm              Replacement term.
     */

    public SubstFreeVo(final String reference, final Element subjectVariable,
            final Element substituteTerm) {
        this.reference = reference;
        this.subjectVariable = subjectVariable;
        this.substituteTerm = substituteTerm;
    }

    /**
     * Default constructor.
     */
    public SubstFreeVo() {
        // nothing to do
    }

    public SubstFree getSubstFree() {
        return this;
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
        if (reference == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            return new String[] {reference };
        }
    }

    public Element getSubjectVariable() {
        return subjectVariable;
    }

    /**
     * Get subject variable that will be substituted.
     *
     * @param   subjectVariable Subject variable that will be replaced.
     */
    public void setSubjectVariable(final Element subjectVariable) {
        this.subjectVariable = subjectVariable;
    }

    public Element getSubstituteTerm() {
        return substituteTerm;
    }

    /**
     * Set substitution term.
     *
     * @param   substituteTerm  New term.
     */
    public void setSubstituteTerm(final Element substituteTerm) {
        this.substituteTerm = substituteTerm;
    }

    public String getName() {
        return "SubstFree";
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof SubstFreeVo)) {
            return false;
        }
        final SubstFreeVo other = (SubstFreeVo) obj;
        return EqualsUtility.equals(reference, other.reference)
            && EqualsUtility.equals(subjectVariable, other.subjectVariable)
            && EqualsUtility.equals(substituteTerm, other.substituteTerm);
    }

    public int hashCode() {
        return (reference != null ? reference.hashCode() : 0)
            ^ (subjectVariable != null ? 2 ^ subjectVariable.hashCode() : 0)
            ^ (substituteTerm != null ? 3 ^ substituteTerm.hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(getName());
        if (reference != null || subjectVariable != null
                || substituteTerm != null) {
            result.append(" (");
            boolean w = false;
            if (reference != null) {
                result.append(reference);
                w = true;
            }
            if (subjectVariable != null) {
                if (w) {
                    result.append(", ");
                }
                result.append(subjectVariable);
                w = true;
            }
            if (substituteTerm != null) {
                if (w) {
                    result.append(", ");
                }
                result.append("by ");
                result.append(substituteTerm);
                w = true;
            }
            result.append(")");
        }
        return result.toString();
    }

}
