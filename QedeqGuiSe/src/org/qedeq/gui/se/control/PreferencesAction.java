/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.gui.se.pane.PreferencesDialog;

/**
 * Show preferences window.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
class PreferencesAction extends AbstractAction {

    /** Controller reference. */
    private final QedeqController controller;

    /**
     * Constructor.
     *
     * @param   controller  Controller.
     */
    PreferencesAction(final QedeqController controller) {
        this.controller = controller;
    }

    public void actionPerformed(final ActionEvent e) {
        new Preferences("Preferences").show();  // FIXME mime 20100222: remove if new code is working
        new PreferencesDialog(controller.getMainFrame()).setVisible(true);
    }

}
