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

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.Location;


/**
 * Describes the "physical" directory location for a module.
 * This is a full or relative URL like:
 * <code>http://www.qedeq.org/principia/0_01_06/</code> or <code>.</code>
 * or <code>file:///qedeq/</code>
 *
 * @author  Michael Meyling
 */
public class LocationVo implements Location {

    /** URL to "physical" directory location of module. */
    private String location;


    /**
     * Constructs a location description for a module. The <code>location</code>
     * parameter contains an URL that points to a directory.
     * This is a full or relative URL like:
     * <code>http://www.qedeq.org/principia/0_01_06/</code> or <code>.</code>
     * or <code>file:///qedeq/</code>
     * Here it is not tested that it is a formal correct URL.
     *
     * @param   location    URL directory location.
     */
    public LocationVo(final String location) {
        this.location = location;
    }

    /**
     * Constructs an empty location description for a module.
     */
    public LocationVo() {
        // nothing to do
    }

    /**
     * Set URL to "physical" directory location of module. The <code>location</code>
     * parameter contains an URL that points to a directory.
     * This is a full or relative URL like:
     * <code>http://www.qedeq.org/principia/0_01_06/</code> or <code>.</code>
     * or <code>file:///qedeq/</code>
     * Here it is not tested that it is a formal correct URL.
     *
     * @param   location    URL directory location.
     */
    public final void setLocation(final String location) {
        this.location = location;
    }

    public final String getLocation() {
        return location;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof LocationVo)) {
            return false;
        }
        final LocationVo other = (LocationVo) obj;
        return  EqualsUtility.equals(getLocation(), other.getLocation());
    }

    public int hashCode() {
        return (getLocation() != null ? getLocation().hashCode() : 0);
    }

    public String toString() {
        if (getLocation() == null) {
            return "";
        }
        return getLocation();
    }

}
