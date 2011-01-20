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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.qedeq.base.io.ResourceLoaderUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.util.ExternalLinkContentViewerUI;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.context.KernelContext;

/**
 * Show help window.
 *
 * @author  Michael Meyling
 */
class HelpAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = HelpAction.class;

    /** Controller reference. */
    private final QedeqController controller;

    /**
     * Constructor.
     *
     * @param   controller  Controller.
     */
    HelpAction(final QedeqController controller) {
        this.controller = controller;
    }

    public void actionPerformed(final ActionEvent e) {

        String pathToHS = "resources/help/Help.hs";
        URL hsURL = null;
        HelpSet hs = null;
        try {
            hsURL = ResourceLoaderUtility.getResourceUrl(pathToHS);
            hs = new HelpSet(null, hsURL);
        } catch (Exception ex) {
            Trace.fatal(CLASS, this, "actionPerformed(ActionEvent)",
                "Problems loading help set.", ex);
            JOptionPane.showMessageDialog(
                controller.getMainFrame(), "Help for "
                    + KernelContext.getInstance().getDescriptiveKernelVersion() + "\n\n"
                    + "Still missing" + "\n\n", "Help",
                    JOptionPane.INFORMATION_MESSAGE,
                    GuiHelper.readImageIcon("qedeq/32x32/qedeq.png"));
            return;
        }
        // integrate native browser into JHelp
        final UIDefaults table = UIManager.getDefaults();
        final Object[] uiDefaults = {"HelpContentViewerUI",
            ExternalLinkContentViewerUI.class.getName()
        };
        table.putDefaults(uiDefaults);
        final JHelp jHelp = new JHelp(hs);
        final JDialog dialog = new JDialog(controller.getMainFrame(), "Help", true);
        dialog.getContentPane().add("Center", jHelp);
        dialog.setSize(new Dimension(950, 700));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

}
