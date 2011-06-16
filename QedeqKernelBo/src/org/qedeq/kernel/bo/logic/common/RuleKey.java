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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.base.utility.EqualsUtility;

/**
 * Rule key, describing a certain version of a rule.
 *
 * @author  Michael Meyling
 */
public final class RuleKey {

    /** Rule name. */
    private String name;

    /** Rule version. */
    private String version;

    /**
     * Constructor.
     *
     * @param   name        Rule name.
     * @param   version     Rule argument number.
     */
    public RuleKey(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    /**
     * Get rule name.
     *
     * @return  Rule name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get rule version.
     *
     * @return  Rule version.
     */
    public String getVersion() {
        return version;
    }

    public int hashCode() {
        return (getName() != null ? getName().hashCode() : 0)
            ^ (getVersion() != null ? getVersion().hashCode() : 0);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof RuleKey)) {
            return false;
        }
        final RuleKey other = (RuleKey) obj;
        return EqualsUtility.equals(getName(), other.getName())
            && EqualsUtility.equals(getVersion(), other.getVersion());
    }

    public String toString() {
        return getName() + " [" + getVersion() + "]";
    }


}
