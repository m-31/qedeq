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
import org.qedeq.kernel.se.base.module.Universal;


/**
 * Usage of rule for universal generalization.
 *
 * @author  Michael Meyling
 */
public class UniversalVo implements Universal {

    /** Reference to previously proven formula. Usually like A -&gt; B. */
    private String reference;

    /** Subject variable that we will quantify about. */
    private Element subjectVariable;

    /**
     * Constructs an reason.
     *
     * @param   reference                   Reference to a valid formula.
     * @param   subjectVariable             Subject variable that we will quantify about.
     */

    public UniversalVo(final String reference, final Element subjectVariable) {
        this.reference = reference;
        this.subjectVariable = subjectVariable;
    }

    /**
     * Default constructor.
     */
    public UniversalVo() {
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

    public Element getSubjectVariable() {
        return subjectVariable;
    }

    /**
     * Set quantification subject variable.
     *
     * @param   subjectVariable Set free subject variable.
     */
    public void setSubjectVariable(final Element subjectVariable) {
        this.subjectVariable = subjectVariable;
    }

    public String getName() {
        return "Universal";
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof UniversalVo)) {
            return false;
        }
        final UniversalVo other = (UniversalVo) obj;
        return EqualsUtility.equals(reference, other.reference)
            && EqualsUtility.equals(subjectVariable, other.subjectVariable);
    }

    public int hashCode() {
        return (reference != null ? reference.hashCode() : 0)
            ^ (subjectVariable != null ? 2 ^ subjectVariable.hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("SubstFree");
        if (reference != null || subjectVariable != null) {
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
            result.append(")");
        }
        return result.toString();
    }

}
