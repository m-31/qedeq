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

package org.qedeq.kernel.se.common;

/**
 * Describes whiche state a QEDEQ module is currently in.
 *
 * @author  Michael Meylin
 */
public interface State {

    /**
     * Get meaning of module state.
     *
     * @return meaning of module state.
     */
    public String getText();

    /**
     * Is this a failure state?
     *
     * @return is this a failure state?
     */
    public boolean isFailure();

    /**
     * Get module state code.
     *
     * @return Module state.
     */
    public int getCode();

}
