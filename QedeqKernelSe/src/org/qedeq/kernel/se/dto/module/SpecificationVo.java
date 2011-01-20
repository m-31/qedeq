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
import org.qedeq.kernel.se.base.module.LocationList;
import org.qedeq.kernel.se.base.module.Specification;


/**
 * Describes a specification of a module, that means its name, versions and possible
 * "physical" locations.
 * The combination of {@link #getName()} and {@link #getRuleVersion()} defines the
 * file name of that module.
 *
 * @version $Revision: 1.8 $
 * @author  Michael Meyling
 */
public class SpecificationVo implements Specification {

    /** Module name. */
    private String name;

    /** Rule version, that is needed to verify the module. */
    private String ruleVersion;

    /** List of locations for the module. */
    private LocationList locationList;

    /**
     * Constructs a module specification. The combination of <code>name</code> and
     * <code>ruleVersion</code> defines the file name of that module.
     *
     * @param   name        Module name.
     * @param   ruleVersion Rule version. This version is at least needed to verify that module.
     * @param   locations   List of locations for that module.
     */
    public SpecificationVo(final String name, final String ruleVersion,
            final LocationList locations) {
        this.name = name;
        this.ruleVersion = ruleVersion;
        this.locationList = locations;
    }

    /**
     * Constructs an empty module specification.
     */
    public SpecificationVo() {
        // nothing to do
    }

    public final void setName(final String name) {
       this.name = name;
    }

    public final String getName() {
       return name;
    }

    public final void setRuleVersion(final String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public final String getRuleVersion() {
        return ruleVersion;
    }

    /**
     * Set list of locations for the module.
     *
     * @param   locations   List of locations for that module.
     */
    public final void setLocationList(final LocationListVo locations) {
        this.locationList = locations;
    }

    public final LocationList getLocationList() {
        return locationList;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof SpecificationVo)) {
            return false;
        }
        final SpecificationVo other = (SpecificationVo) obj;
        return  EqualsUtility.equals(getName(), other.getName())
            &&  EqualsUtility.equals(getRuleVersion(), other.getRuleVersion())
            &&  EqualsUtility.equals(getLocationList(), other.getLocationList());
    }

    public int hashCode() {
        return (getName() != null ? getName().hashCode() : 0)
            ^ (getRuleVersion() != null ? 1 ^ getRuleVersion().hashCode() : 0)
            ^ (getLocationList() != null ? 1 ^ getLocationList().hashCode() : 0);
    }

    public String toString() {
        return "Name: " + name + ", Rule Version: " + ruleVersion + "\n" + locationList;
    }

}
