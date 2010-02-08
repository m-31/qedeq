/* This file is part of the project "Hilbert II" - http://www.qedeq.org
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

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.common.ModuleAddress;

/**
 * Load QEDEQ module file.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
class AddAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = AddAction.class;

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
        Trace.trace(CLASS, this, "actionPerformed", e);

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
        final ModuleAddress address;
        switch(result) {
        case 0:     // ok
            try {
                address = KernelContext.getInstance().getModuleAddress(
                    (String) cb.getSelectedItem());
                controller.addToModuleHistory(address.toString());
            } catch (IOException ie) {
                Trace.trace(CLASS, this, "actionPerformed", "no correct URL", ie);
                JOptionPane.showMessageDialog(
                    controller.getMainFrame(),       // the parent that the dialog blocks
                    "this is no valid URL: " + cb.getSelectedItem()
                    + "\n" + ie.getMessage(), // message
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
                KernelContext.getInstance().loadModule(address);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

}
