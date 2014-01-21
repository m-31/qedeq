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

package org.qedeq.base.io;


/**
 * Listener for loading completeness.
 *
 * @author  Michael Meyling
 */
public interface LoadingListener {

    /**
     * Get major version number.
     *
     * @param   completeness  Loading completeness changed to this value.
     *                        This is a value between 0 and 1.
     */
    public void loadingCompletenessChanged(final double completeness);

}
