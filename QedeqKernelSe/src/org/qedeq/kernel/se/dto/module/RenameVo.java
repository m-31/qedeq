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
import org.qedeq.kernel.se.base.module.Rename;


/**
 * Usage of rule for rename bound subject variable.
 *
 * @author  Michael Meyling
 */
public class RenameVo implements Rename {

    /** Reference to previously proven formula. */
    private String reference;

    /** Bound subject variable that will be replaced. */
    private Element originalSubjectVariable;

    /** Replacement subject variable. */
    private Element replacementSubjectVariable;

    /** This bound occurrence should be replaced. If this value is 0, the replacement position
     * is not specified. */
    private int occurrence;

    /**
     * Constructs an addition argument.
     *
     * @param   reference                   Reference to a valid formula.
     * @param   originalSubjectVariable     Bound subject variable that will be replaced.
     * @param   replacementSubjectVariable  Replacement subject variable.
     * @param   occurrence                  This bound occurrence should be replaced. If this
     *                                      value is 0, the replacement position
     *                                      is not specified. */

    public RenameVo(final String reference, final Element originalSubjectVariable,
            final Element replacementSubjectVariable, final int occurrence) {
        this.reference = reference;
        this.originalSubjectVariable = originalSubjectVariable;
        this.replacementSubjectVariable = replacementSubjectVariable;
        this.occurrence = occurrence;
    }

    /**
     * Default constructor.
     */
    public RenameVo() {
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

    public int getOccurence() {
        return occurrence;
    }

    public Element getOriginalSubjectVariable() {
        return originalSubjectVariable;
    }

    public Element getReplacementSubjectVariable() {
        return replacementSubjectVariable;
    }
    public String getName() {
        return "Rename";
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof RenameVo)) {
            return false;
        }
        final RenameVo other = (RenameVo) obj;
        return EqualsUtility.equals(reference, other.reference)
            && EqualsUtility.equals(originalSubjectVariable, other.originalSubjectVariable)
            && EqualsUtility.equals(replacementSubjectVariable, other.replacementSubjectVariable)
            && (occurrence == other.occurrence);
    }

    public int hashCode() {
        return (reference != null ? reference.hashCode() : 0)
            ^ (originalSubjectVariable != null ? 2 ^ originalSubjectVariable.hashCode() : 0)
            ^ (replacementSubjectVariable != null ? 3 ^ replacementSubjectVariable.hashCode() : 0)
            ^ (4 ^ occurrence);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("Rename");
        if (reference != null || originalSubjectVariable != null
                || replacementSubjectVariable != null) {
            result.append(" (");
            boolean w = false;
            if (reference != null) {
                result.append(reference);
                w = true;
            }
            if (originalSubjectVariable != null) {
                if (w) {
                    result.append(", ");
                }
                result.append(originalSubjectVariable);
                w = true;
            }
            if (replacementSubjectVariable != null) {
                if (w) {
                    result.append(", ");
                }
                result.append("by ");
                result.append(replacementSubjectVariable);
                w = true;
            }
            if (occurrence != 0) {
                if (w) {
                    result.append(", ");
                }
                result.append("occurence: ");
                result.append(occurrence);
                w = true;
            }
            result.append(")");
        }
        return result.toString();
    }


}
