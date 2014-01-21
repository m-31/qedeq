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
 * Describes the "physical" directory location for a module.
 * This is a full or relative URL like:
 * <code>http://www.qedeq.org/principia/0_01_06/</code> or <code>.</code>
 * or <code>file:///qedeq/</code>
 *
 * @author  Michael Meyling
 */
public interface Location {

    /**
     * Get location for a module. The <code>location</code>
     * parameter contains an URL that points to a directory.
     * This is a full or relative URL like:
     * <code>http://www.qedeq.org/principia/0_01_06/</code> or <code>.</code>
     * or <code>file:///qedeq/</code>
     *
     * @return  URL directory location.
     */
    public String getLocation();
}
