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

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.Add;


/**
 * Usage of formula addition.
 *
 * @author  Michael Meyling
 */
public class AddVo implements Add {

    /** Reference to previously proven formula. */
    private String reference;

    /**
     * Constructs an addition argument.
     *
     * @param   reference  Reference to a valid formula.
     */
    public AddVo(final String reference) {
        this.reference = reference;
    }

    /**
     * Default constructor.
     */
    public AddVo() {
        // nothing to do
    }

    public Add getAdd() {
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
        if (reference == null || reference.length() == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            return new String[] {reference };
        }
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof AddVo)) {
            return false;
        }
        final AddVo other = (AddVo) obj;
        return  EqualsUtility.equals(reference, other.reference);
    }

    public int hashCode() {
        return (reference != null ? reference.hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("Add");
        if (reference != null) {
            result.append(" (");
            if (reference != null) {
                result.append(reference);
            }
            result.append(")");
        }
        return result.toString();
    }

    public String getName() {
        return "Add";
    }

}
