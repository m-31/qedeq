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
import org.qedeq.kernel.se.base.module.Existential;


/**
 * Usage of rule for existential generalization.
 *
 * @author  Michael Meyling
 */
public class ExistentialVo implements Existential {

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

    public ExistentialVo(final String reference, final Element subjectVariable) {
        this.reference = reference;
        this.subjectVariable = subjectVariable;
    }

    /**
     * Default constructor.
     */
    public ExistentialVo() {
        // nothing to do
    }

    public Existential getExistential() {
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
     * Set quantification subject variable.
     *
     * @param   subjectVariable Set free subject variable.
     */
    public void setSubjectVariable(final Element subjectVariable) {
        this.subjectVariable = subjectVariable;
    }

    public String getName() {
        return "Existential";
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ExistentialVo)) {
            return false;
        }
        final ExistentialVo other = (ExistentialVo) obj;
        return EqualsUtility.equals(reference, other.reference)
            && EqualsUtility.equals(subjectVariable, other.subjectVariable);
    }

    public int hashCode() {
        return (reference != null ? reference.hashCode() : 0)
            ^ (subjectVariable != null ? 2 ^ subjectVariable.hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(getName());
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
