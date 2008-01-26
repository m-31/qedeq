/* $Id: PreferencesAction.java,v 1.2 2008/01/26 12:38:27 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.qedeq.gui.se.pane.Preferences;

/**
 * Show preferences window.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
class PreferencesAction extends AbstractAction {

    /**
     * Constructor.
     *
     * @param   controller  Controller.
     */
    PreferencesAction() {
    }

    public void actionPerformed(final ActionEvent e) {
        new Preferences("Preferences").show();
    }

}
