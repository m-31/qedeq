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

package org.qedeq.kernel.se.base.module;


/**
 * Describes a specification of a module, that means its name, versions and possible
 * "physical" locations. The combination of {@link #getName()} and
 * {@link #getRuleVersion()} defines the file name of that module.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public interface Specification {

    /**
     * Set module name.
     *
     * @param   name    Module name.
     */
    public void setName(String name);

    /**
     * Get module name.
     *
     * @return  Module name.
     */
    public String getName();

    /**
     * Set rule version, that is needed to verify the module.
     *
     * @param   ruleVersion Rule version.
     */
    public void setRuleVersion(String ruleVersion);

    /**
     * Get rule version, that is needed to verify the module.
     *
     * @return  Rule version.
     */
    public String getRuleVersion();

    /**
     * Get list of locations for the module.
     *
     * @return  List of locations for that module.
     */
    public LocationList getLocationList();
}
