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
import org.qedeq.kernel.se.base.module.Import;
import org.qedeq.kernel.se.base.module.Specification;


/**
 * Module import. Every needed module must be referenced as an module import.
 *
 * @author  Michael Meyling
 */
public class ImportVo implements Import {

    /** Label for the imported module. All references to that module must have this prefix. */
    private String label;

    /** Specification of imported module. Includes location information. */
    private Specification specification;

    /**
     * Constructs a new import.
     *
     * @param   label           Label for this import. All references to that module must
     *                          have this prefix.
     * @param   specification   Import specification. Includes location information.
     */
    public ImportVo(final String label, final SpecificationVo specification) {
        this.label = label;
        this.specification = specification;
    }

    /**
     * Constructs an empty import.
     */
    public ImportVo() {
        // nothing to do
    }

    /**
     * Set label for this import module. All references to this module must have this
     * prefix.
     *
     * @param   label   Prefix for this imported module.
     */
    public final void setLabel(final String label) {
        this.label = label;
    }

    public final String getLabel() {
        return label;
    }

    /**
     * Set specification of this imported module. Contains location information.
     *
     * @param   specification   Module specification.
     */
    public final void setSpecification(final SpecificationVo specification) {
        this.specification = specification;
    }

    public final Specification getSpecification() {
        return specification;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ImportVo)) {
            return false;
        }
        final ImportVo other = (ImportVo) obj;
        return  EqualsUtility.equals(getLabel(), other.getLabel())
            &&  EqualsUtility.equals(getSpecification(), other.getSpecification());
    }

    public int hashCode() {
        return (getLabel() != null ? getLabel().hashCode() : 0)
            ^ (getSpecification() != null ? 1 ^ getSpecification().hashCode() : 0);
    }

    public String toString() {
        return label + ":" + specification;
    }

}
