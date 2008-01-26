/* $Id: ParserAction.java,v 1.3 2008/01/26 12:38:27 m31 Exp $
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
import java.io.FileNotFoundException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.qedeq.gui.se.pane.ParserPane;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;

/**
 * Show preferences window.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
class ParserAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = ParserAction.class;

    /** Controller reference. */
    private final QedeqController controller;

    /**
     * Constructor.
     *
     * @param   controller  Controller.
     */
    ParserAction(final QedeqController controller) {
        this.controller = controller;
    }

    public void actionPerformed(final ActionEvent e) {
        try {
            new ParserPane().show();
        } catch (FileNotFoundException ex) {
            Trace.fatal(CLASS, this, "actionPerformed", "Parser Window can not be opened", ex);
            JOptionPane.showMessageDialog(
                controller.getMainFrame(), "Parser Window can not be opened" + "\n"
                    + ex.toString(), "Error",
                    JOptionPane.ERROR_MESSAGE);;
        } catch (SourceFileExceptionList xl) {
            Trace.fatal(CLASS, this, "actionPerformed", "Parser Window can not be opened", xl);
            JOptionPane.showMessageDialog(
                controller.getMainFrame(), "Parser Window can not be opened" + "\n"
                    + xl.toString(), "Error",
                    JOptionPane.ERROR_MESSAGE);;
        }
    }

}
