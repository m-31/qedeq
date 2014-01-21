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

package org.qedeq.kernel.se.dto.module;

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.SubstFunc;


/**
 * Usage of rule for substitute function variable.
 *
 * @author  Michael Meyling
 */
public class SubstFuncVo implements SubstFunc {

    /** Reference to previously proven formula. */
    private String reference;

    /** Function variable that will be substituted. */
    private Element functionVariable;

    /** Replacement term. */
    private Element substituteTerm;

    /**
     * Constructs an reason.
     *
     * @param   reference                   Reference to a valid formula.
     * @param   functionVariable            Function variable that will be substituted.
     * @param   substituteFormula           Replacement term.
     */

    public SubstFuncVo(final String reference, final Element functionVariable,
            final Element substituteFormula) {
        this.reference = reference;
        this.functionVariable = functionVariable;
        this.substituteTerm = substituteFormula;
    }

    /**
     * Default constructor.
     */
    public SubstFuncVo() {
        // nothing to do
    }

    public SubstFunc getSubstFunc() {
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
        }
        return new String[] {reference };
    }

    public Element getFunctionVariable() {
        return functionVariable;
    }

    /**
     * Set function variable that will be substituted.
     *
     * @param   functionVariable  Function variable that will be replaced.
     */
    public void setFunctionVariable(final Element functionVariable) {
        this.functionVariable = functionVariable;
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
        return "SubstFun";
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof SubstFuncVo)) {
            return false;
        }
        final SubstFuncVo other = (SubstFuncVo) obj;
        return EqualsUtility.equals(reference, other.reference)
            && EqualsUtility.equals(functionVariable, other.functionVariable)
            && EqualsUtility.equals(substituteTerm, other.substituteTerm);
    }

    public int hashCode() {
        return (reference != null ? reference.hashCode() : 0)
            ^ (functionVariable != null ? 2 ^ functionVariable.hashCode() : 0)
            ^ (substituteTerm != null ? 3 ^ substituteTerm.hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(getName());
        if (reference != null || functionVariable != null
                || substituteTerm != null) {
            result.append(" (");
            boolean w = false;
            if (reference != null) {
                result.append(reference);
                w = true;
            }
            if (functionVariable != null) {
                if (w) {
                    result.append(", ");
                }
                result.append(functionVariable);
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
