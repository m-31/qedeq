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

package org.qedeq.gui.se.main;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.qedeq.gui.se.control.QedeqController;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.gui.se.util.MenuHelper;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

/**
 * Menu bar and pull-down menus.
 *
 * @author  Michael Meyling
 * @version $Revision: 1.4 $
 */
public class QedeqMenuBar extends JMenuBar {

    /** Reference to controller. */
    private QedeqController controller;

    /**
     * Constructor.
     *
     * @param   controller  Controller reference.
     * @param   options     GUI options to create look specific design.
     */
    public QedeqMenuBar(final QedeqController controller, final GuiOptions options) {
        this.controller = controller;

        putClientProperty(Options.HEADER_STYLE_KEY,
                              options.getMenuBarHeaderStyle());
        putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY,
                              options.getMenuBarPlasticBorderStyle());
        putClientProperty(WindowsLookAndFeel.BORDER_STYLE_KEY,
                              options.getMenuBarWindowsBorderStyle());
        putClientProperty(PlasticLookAndFeel.IS_3D_KEY,
                              options.getMenuBar3DHint());

        add(createFileMenu());
        add(createCheckMenu());
        add(createTransformMenu());
        add(createToolsMenu());
        add(createHelpMenu());
    }

    /**
     * Create "File" menu.
     *
     * @return  Created menu.
     */
    private JMenu createFileMenu() {
        JMenuItem item;

        JMenu menu = MenuHelper.createMenu("File", 'F');

        item = MenuHelper.createMenuItem("Load from Web", 'W');
        item.addActionListener(controller.getAddAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/list-add.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Load local File", 'F');
        item.addActionListener(controller.getAddFileAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/document-open.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Load all from QEDEQ", 'Q');
        item.addActionListener(controller.getAddAllModulesFromQedeqAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/go-home.png"));
        menu.add(item);

        menu.addSeparator();

        item = MenuHelper.createMenuItem("Remove module", 'R');
        item.addActionListener(controller.getRemoveModuleAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/edit-cut.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Clear Buffer", 'C');
        item.addActionListener(controller.getRemoveLocalBufferAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/edit-delete.png"));
        menu.add(item);

        if (!MenuHelper.isQuitInOSMenu()) {
            menu.addSeparator();
            item = MenuHelper.createMenuItem("Exit", 'E');
            item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/system-log-out.png"));
            item.addActionListener(controller.getExitAction());
            menu.add(item);
        }
        return menu;
    }


    /**
     * Create "Check" menu.
     *
     * @return  Menu.
     */
    private JMenu createCheckMenu() {
        JMenuItem item;

        JMenu menu = MenuHelper.createMenu("Check", 'C');

        item = MenuHelper.createMenuItem("Check Mathematical Logic", 'M');
        item.addActionListener(controller.getCheckLogicAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/media-record.png"));
        menu.add(item);

        return menu;
    }


    /**
     * Create "Plugins" menu.
     *
     * @return  Menu.
     */
    private JMenu createTransformMenu() {
        final JMenu menu = MenuHelper.createMenu("Plugins", 'P');
        JMenuItem item;
        item = MenuHelper.createMenuItem("Plugin Preferences", 'S');
        item.addActionListener(controller.getPluginPreferencesAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/categories/preferences-desktop.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Remove Plugin Results", 'R');
        item.addActionListener(controller.getRemovePluginResultsAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/edit-clear.png"));
        menu.add(item);

        menu.addSeparator();
        final JMenuItem[] pluginMenu = controller.getPluginMenuEntries();
        for (int i = 0; i < pluginMenu.length; i++) {
            menu.add(pluginMenu[i]);
        }
        return menu;
    }


    /**
     * Create "Tools" menu.
     *
     * @return  Menu.
     */
    private JMenu createToolsMenu() {
        JMenuItem item;

        JMenu menu = MenuHelper.createMenu("Tools", 'o');

        item = MenuHelper.createMenuItem("LaTeX to QEDEQ", 'L');
        item.addActionListener(controller.getParserAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/actions/format-indent-more.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Preferences", 'P');
        item.addActionListener(controller.getPreferencesAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/categories/preferences-system.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Threads", 'T');
        item.addActionListener(controller.getProcessViewAction());
        item.setIcon(GuiHelper.readImageIcon("tango/16x16/categories/applications-system.png"));
        menu.add(item);

        return menu;
    }


    /**
     * Create help menu.
     *
     * @return  Help menu.
     */
    private JMenu createHelpMenu() {
        JMenu menu = MenuHelper.createMenu("Help", 'H');

        JMenuItem item;
        item = MenuHelper.createMenuItem("Help Contents",
            GuiHelper.readImageIcon("tango/16x16/apps/help-browser.png"), 'H');
        item.addActionListener(controller.getHelpAction());
        menu.add(item);
        if (!MenuHelper.isAboutInOSMenu()) {
            menu.addSeparator();
            item = MenuHelper.createMenuItem("About",
                GuiHelper.readImageIcon("qedeq/16x16/qedeq.png"), 'a');
            item.addActionListener(controller.getAboutAction());
            menu.add(item);
        }
        return menu;
    }

}
