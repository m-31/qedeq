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
import org.qedeq.kernel.se.base.module.ModusPonens;


/**
 * Modes Ponens usage.
 *
 * @author  Michael Meyling
 */
public class ModusPonensVo implements ModusPonens {

    /** Usually reference to a formula like A -> B. */
    private String reference1;

    /** Usually reference to a formula like A. */
    private String reference2;

    /**
     * Constructs a Modus Ponens argument.
     *
     * @param   reference1  Usually reference to a formula like A -> B.
     * @param   reference2  Usually reference to a formula like A.
     */
    public ModusPonensVo(final String reference1, final String reference2) {
        this.reference1 = reference1;
        this.reference2 = reference2;
    }

    /**
     * Default constructor.
     */
    public ModusPonensVo() {
        // nothing to do
    }

    public ModusPonens getModusPonens() {
        return this;
    }

    public String getReference1() {
        return reference1;
    }

    /**
     * Set first formula reference.
     *
     * @param   reference1  Reference to formula.
     */
    public void setReference1(final String reference1) {
        this.reference1 = reference1;
    }

    public String getReference2() {
        return reference2;
    }

    /**
     * Set second formula reference.
     *
     * @param   reference2  Reference to formula.
     */
    public void setReference2(final String reference2) {
        this.reference2 = reference2;
    }

    public String[] getReferences() {
        if (reference1 == null || reference1.length() == 0) {
            if (reference2 == null || reference2.length() == 0) {
                return ArrayUtils.EMPTY_STRING_ARRAY;
            } else {
                return new String[] {reference2 };
            }
        } else {
            if (reference2 == null || reference2.length() == 0) {
                return new String[] {reference1 };
            } else {
                return new String[] {reference1, reference2 };
            }
        }
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ModusPonens)) {
            return false;
        }
        final ModusPonens other = (ModusPonens) obj;
        return  EqualsUtility.equals(getReference1(), other.getReference1())
          && EqualsUtility.equals(getReference2(), other.getReference2());
    }

    public int hashCode() {
        return (getReference1() != null ? getReference1().hashCode() : 0)
           + (getReference2() != null ?  1 ^ getReference2().hashCode() : 0);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("MP");
        if (getReference1() != null || getReference2() != null) {
            result.append(" (");
            if (getReference1() != null) {
                result.append(getReference1());
            }
            if (getReference2() != null) {
                if (getReference1() != null) {
                    result.append(", ");
                }
                result.append(getReference2());
            }
            result.append(")");
        }
        return result.toString();
    }

    public String getName() {
        return "MP";
    }

}
