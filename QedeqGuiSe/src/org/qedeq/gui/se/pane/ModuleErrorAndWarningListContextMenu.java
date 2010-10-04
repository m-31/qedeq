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

package org.qedeq.gui.se.pane;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.gui.se.util.MenuHelper;


/**
 *  * Context menu for error and warnings list.
 *
 * @author  Michael Meyling
 */
public final class ModuleErrorAndWarningListContextMenu extends JPopupMenu {

    /**
     * Constructor.
     *
     * @param   listener    Actions for copying.
     */
    public ModuleErrorAndWarningListContextMenu(final ActionListener listener) {
        JMenuItem copy = MenuHelper.createMenuItem("Copy", 'C');
        copy.addActionListener(listener);
        copy.setActionCommand("copy");
        copy.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/edit-copy.png"));
        this.add(copy);
    }

}
