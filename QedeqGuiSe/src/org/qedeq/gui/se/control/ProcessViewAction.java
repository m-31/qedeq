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

package org.qedeq.gui.se.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.qedeq.gui.se.pane.ProcessWindow;

/**
 * Show process overview window.
 *
 * @author  Michael Meyling
 */
class ProcessViewAction extends AbstractAction {

    /**
     * Constructor.
     */
    ProcessViewAction() {
    }

    public void actionPerformed(final ActionEvent e) {
        final Runnable start = new Runnable() {
            public void run() {
                final ProcessWindow pw = new ProcessWindow();
                pw.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(start);
    }

}
