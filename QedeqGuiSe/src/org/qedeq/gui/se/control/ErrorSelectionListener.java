/* $Id: ErrorSelectionListener.java,v 1.1 2008/05/15 21:26:46 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.common.SourceFileException;

/**
 * Listener for error selection events.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public interface ErrorSelectionListener {

    /**
     * This error was selected.
     *
     * @param   errorNumber Selected error number. Starts with 0.
     * @param   sf          Selected error.
     */
    public void selectError(int errorNumber, SourceFileException sf);

}
