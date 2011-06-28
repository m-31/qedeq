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
import org.qedeq.kernel.se.base.module.ChangedRule;
import org.qedeq.kernel.se.base.module.LatexList;


/**
 * Rule change.
 *
 * @author  Michael Meyling
 */
public class ChangedRuleVo implements ChangedRule {

    /** Further proposition description. Normally <code>null</code>. */
    private LatexList description;

    /** Rule name. */
    private String name;

    /** Rule version. */
    private String version;

    /**
     * Constructs a new rule declaration.
     */
    public ChangedRuleVo() {
        // nothing to do
    }


    /**
     * Set rule name.
     *
     * @param   name    Rule name.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    /**
     * Set rule version.
     *
     * @param   version Rule version.
     */
    public final void setVersion(final String version) {
        this.version = version;
    }

    public final String getVersion() {
        return version;
    }

    /**
     * Set description.
     *
     * @param   description Description.
     */
    public final void setDescription(final LatexListVo description) {
        this.description = description;
    }

    public LatexList getDescription() {
        return description;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ChangedRule)) {
            return false;
        }
        final ChangedRule other = (ChangedRule) obj;
        return  EqualsUtility.equals(getName(), other.getName())
            && EqualsUtility.equals(getVersion(), other.getVersion())
            && EqualsUtility.equals(getDescription(), other.getDescription());
    }

    public int hashCode() {
        return (getName() != null ? 1 ^ getName().hashCode() : 0)
            ^ (getVersion() != null ? 2 ^ getVersion().hashCode() : 0)
            ^ (getDescription() != null ? 4 ^ getDescription().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Rule change: " + getName() + " [" + getVersion() + "]\n");
        buffer.append("\nDescription:\n");
        buffer.append(getDescription());
        return buffer.toString();
    }
}
