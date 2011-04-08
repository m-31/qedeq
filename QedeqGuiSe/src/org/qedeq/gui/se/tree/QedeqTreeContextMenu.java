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

package org.qedeq.gui.se.tree;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.qedeq.gui.se.control.QedeqController;
import org.qedeq.gui.se.pane.QedeqGuiConfig;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.gui.se.util.MenuHelper;


/**
 * Context menu for nodes.
 *
 * @author  Michael Meyling
 */
public final class QedeqTreeContextMenu extends JPopupMenu {

    /**
     * Constructor.
     *
     * @param   controller  Reference holder to action information.
     */
    public QedeqTreeContextMenu(final QedeqController controller) {
        final String resolution = QedeqGuiConfig.getInstance().getIconSize();
        JMenuItem item = MenuHelper.createMenuItem("Remove", 'R');
        item.addActionListener(controller.getRemoveModuleAction());
        item.setToolTipText("Unload selected QEDEQ modules. Changes status of dependent modules. "
                + "Local module buffer is not affected.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/edit-cut.png"));
        this.add(item);

        this.addSeparator();

        item = MenuHelper.createMenuItem("Check Well-Formedness", 'W');
        item.setToolTipText(
            "Check if all formulas are well formed within selected QEDEQ modules. This includes dependency checking.");
        item.addActionListener(controller.getCheckLogicAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/run.png"));
        this.add(item);

        this.addSeparator();

        final JMenuItem[] pluginMenu = controller.getPluginMenuEntries();
        for (int i = 0; i < pluginMenu.length; i++) {
            this.add(pluginMenu[i]);
        }

        this.addSeparator();

        item = MenuHelper.createMenuItem("Remove Plugin Results", 'R');
        item.addActionListener(controller.getRemovePluginResultsAction());
        item.setToolTipText(
            "Remove all warnings and errors that were produced by all plugin executions for the selected module.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/edit-clear.png"));
        this.add(item);

        this.addSeparator();

        item = MenuHelper.createMenuItem("Load from Web", 'W');
        item.setToolTipText("Load QEDEQ module from anywhere in the Web");
        item.addActionListener(controller.getAddAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/list-add.png"));
        this.add(item);

        item = MenuHelper.createMenuItem("Load local File", 'F');
        item.setToolTipText("Load QEDEQ module from file system");
        item.addActionListener(controller.getAddFileAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/document-open.png"));
        this.add(item);
    }

}
