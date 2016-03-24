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

package org.qedeq.gui.se.control;

import org.qedeq.kernel.se.common.SourceFileException;

/**
 * Listener for warning selection events.
 *
 * @author  Michael Meyling
 */
public interface WarningSelectionListener {

    /**
     * This warning was selected.
     *
     * @param   number      Selected warning number. Starts with 0.
     * @param   sf          Selected warning.
     */
    public void selectWarning(int number, SourceFileException sf);

}
