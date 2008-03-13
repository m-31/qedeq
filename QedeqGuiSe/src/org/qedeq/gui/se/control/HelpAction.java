/* $Id: HelpAction.java,v 1.1 2007/08/21 20:44:58 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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
import javax.swing.JOptionPane;

import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.context.KernelContext;

/**
 * Show help window.
 *
 * TODO mime 20070606: not implemented
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
class HelpAction extends AbstractAction {

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
        JOptionPane.showMessageDialog(
            controller.getMainFrame(), "Help for "
                + KernelContext.getInstance().getDescriptiveKernelVersion() + "\n\n"
                + "Still missing" + "\n\n", "Help",
                JOptionPane.INFORMATION_MESSAGE,
                GuiHelper.readImageIcon("qedeq/32x32/qedeq.png"));
    }

}
