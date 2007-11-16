/* $Id: AddAction.java,v 1.2 2007/10/07 16:39:59 m31 Exp $
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
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;

/**
 * Load QEDEQ module file.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
class AddAction extends AbstractAction {

    /** Reference to controller. */
    private final QedeqController controller;

    /**
     * Constructor.
     *
     * @param   controller  Reference to controller.
     */
    AddAction(final QedeqController controller) {
        this.controller = controller;
    }

    public void actionPerformed(final ActionEvent e) {
        Trace.trace(this, "actionPerformed", e);

        // Messages
        final Object[] message = new Object[2];
        message[0] = "Please choose or enter a module URL:";

        final JComboBox cb = new JComboBox();
        for (int i = 0; i < controller.getModuleHistory().size()
                && i < QedeqController.MAXIMUM_MODULE_HISTORY; i++) {
            cb.addItem(controller.getModuleHistory().get(i));
        }
        cb.setEditable(true);
        message[1] = cb;

        // Options
        final String[] options = {"Ok", "Cancel"};
        final int result = JOptionPane.showOptionDialog(
            controller.getMainFrame(),       // the parent that the dialog blocks
            message,                         // the dialog message array
            "Loading of a module",           // the title of the dialog window
            JOptionPane.CANCEL_OPTION,       // option type
            JOptionPane.QUESTION_MESSAGE,    // message type
            null,                            // optional icon, use null to use the default icon
            options,                         // options string array, will be made into buttons
            options[0]                       // option that should be made into a default button
        );
        final URL url;
        switch(result) {
        case 0:     // ok
            try {
                url = new URL((String) cb.getSelectedItem());
                controller.addToModuleHistory(url.toExternalForm());
            } catch (MalformedURLException e1) {
                Trace.trace(this, "actionPerformed", "no correct URL", e1);
                JOptionPane.showMessageDialog(
                    controller.getMainFrame(),       // the parent that the dialog blocks
                    "this is no valid URL: " + cb.getSelectedItem(), // message
                    "Error",                         // title
                    JOptionPane.ERROR_MESSAGE        // message type
                );
                return;
            }
            break;
        case 1:     // cancel, fall through default
        default:
            return;
        }
        final Thread thread = new Thread() {
            public void run() {
                try {
                    QedeqLog.getInstance().logRequest("Load module \"" + url + "\"");
                    final QedeqBo module
                        = KernelContext.getInstance().loadModule(url);

                    QedeqLog.getInstance().logSuccessfulReply("Module \""
                        + module.getModuleAddress().getFileName()
                        + "\" was successfully loaded.");

                } catch (final XmlFileExceptionList e) {
                    Trace.trace(controller, "actionPerformed", e);
                    QedeqLog.getInstance().logFailureReply(
                        "Loading failed", e.getMessage());
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

}
