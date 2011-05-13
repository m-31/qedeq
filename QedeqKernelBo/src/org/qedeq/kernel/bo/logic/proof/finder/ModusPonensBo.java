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

package org.qedeq.kernel.bo.logic.proof.finder;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.ModusPonens;


/**
 * Modes Ponens usage.
 *
 * @author  Michael Meyling
 */
public class ModusPonensBo implements ModusPonens {

    /** Reference to a formula like A -> B. */
    private int n1;

    /** Usually reference to a formula like A. */
    private int n2;

    /**
     * Constructs a Modus Ponens argument.
     *
     * @param   n1  Usually reference to a formula like A -> B.
     * @param   n2  Usually reference to a formula like A.
     */
    public ModusPonensBo(final int n1, final int n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public ModusPonens getModusPonens() {
        return this;
    }

    public String getReference1() {
        return "" + n1;
    }

    /**
     * Set first formula reference.
     *
     * @param   n1  Reference to formula.
     */
    public void setN1(final int n1) {
        this.n1 = n1;
    }

    /**
     * Get first formula reference.
     *
     * @return  Reference to formula.
     */
    public int getN1() {
        return n1;
    }

    public String getReference2() {
        return "" + n2;
    }

    /**
     * Set second formula reference.
     *
     * @param   n2      Reference to formula.
     */
    public void setN2(final int n2) {
        this.n2 = n2;
    }

    /**
     * Get second formula reference.
     *
     * @return  Reference to formula.
     */
    public int getN2() {
        return n2;
    }

    public String[] getReferences() {
        return new String[] {getReference1(), getReference2()};
    }

    /**
     * Get references to previous lines.
     *
     * @return  List of references.
     */
    public int[] getLines() {
        return new int[] {getN1(), getN2()};
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
