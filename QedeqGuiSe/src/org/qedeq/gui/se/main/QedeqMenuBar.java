/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.gui.se.pane.QedeqGuiConfig;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.gui.se.util.MenuHelper;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

/**
 * Menu bar and pull-down menus.
 *
 * @author  Michael Meyling
 */
public class QedeqMenuBar extends JMenuBar {

    /** Reference to controller. */
    private QedeqController controller;

    /** Icon resolution. Currently supported: "16x16", "22x22" and "32x32". */
    private String resolution = QedeqGuiConfig.getInstance().getIconSize();

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
        item.setToolTipText("Load QEDEQ module from anywhere in the Web");
        item.addActionListener(controller.getAddAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/list-add.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Load local File", 'F');
        item.setToolTipText("Load QEDEQ module from file system");
        item.addActionListener(controller.getAddFileAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/document-open.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Load all from QEDEQ.org", 'Q');
        item.setToolTipText("Load main set of QEDEQ modules from project home page");
        item.addActionListener(controller.getAddAllModulesFromQedeqAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/go-home.png"));
        menu.add(item);

        menu.addSeparator();

        item = MenuHelper.createMenuItem("Remove module", 'R');
        item.setToolTipText("Unload selected QEDEQ modules. Changes status of dependent modules. "
            + "Local module buffer is not affected.");
        item.addActionListener(controller.getRemoveModuleAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/edit-cut.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Clear Buffer", 'C');
        item.setToolTipText("Unload all QEDEQ modules and clear the local module buffer.");
        item.addActionListener(controller.getRemoveLocalBufferAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/edit-delete.png"));
        menu.add(item);

        if (!MenuHelper.isQuitInOSMenu()) {
            menu.addSeparator();
            item = MenuHelper.createMenuItem("Exit", 'E');
            item.setToolTipText("Leave application");
            item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/system-log-out.png"));
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

        item = MenuHelper.createMenuItem("Check Well-Formedness", 'W');
        item.setToolTipText(
            "Check if all formulas are well formed within selected QEDEQ modules. This includes dependency checking.");
        item.addActionListener(controller.getCheckWellFormedAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/status/software-update-available.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Check Fully-Formally Proved", 'F');
        item.setToolTipText(
            "Check if all propositions have formal correct proofss within selected QEDEQ modules. "
            + "This includes dependency checking.");
        item.addActionListener(controller.getCheckFormallyProvedAction());
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/run.png"));
        menu.add(item);

        return menu;
    }


    /**
     * Create "Plugins" menu.
     *
     * @return  Menu.
     */
    private JMenu createTransformMenu() {
        final JMenu menu = MenuHelper.createMenu("Process", 'P');
        JMenuItem item;
        item = MenuHelper.createMenuItem("Plugin Preferences", 'S');
        item.addActionListener(controller.getPluginPreferencesAction());
        item.setToolTipText("Set parameters for specific plugins.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/categories/preferences-desktop.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("View Processes", 'V');
        item.addActionListener(controller.getProcessViewAction());
        item.setToolTipText("Show all processes and their stage.");
        item.setIcon(GuiHelper.readImageIcon("oil/" + resolution + "/apps/utilities-system-monitor.png"));
        menu.add(item);

        menu.addSeparator();

        final JMenuItem[] pluginMenu = controller.getPluginMenuEntries();
        for (int i = 0; i < pluginMenu.length; i++) {
            menu.add(pluginMenu[i]);
        }

        menu.addSeparator();

        item = MenuHelper.createMenuItem("Remove Plugin Results", 'R');
        item.addActionListener(controller.getRemovePluginResultsAction());
        item.setToolTipText(
            "Remove all warnings and errors that were produced by all plugin executions for the selected module.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/edit-clear.png"));
        menu.add(item);

        menu.addSeparator();

        item = MenuHelper.createMenuItem("Terminate All Processes", 'T');
        item.addActionListener(controller.getTerminateAllAction());
        item.setToolTipText(
            "Terminate all currently running processes.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/process-stop.png"));
        menu.add(item);

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
        item.addActionListener(controller.getLatexParserAction());
        item.setToolTipText("Enter LaTeX formulas and transform them into QEDEQ syntax.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/format-indent-more.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Text to QEDEQ", 'T');
        item.addActionListener(controller.getTextParserAction());
        item.setToolTipText("Enter text formulas and transform them into QEDEQ syntax.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/format-indent-more.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Proof Text to QEDEQ", 'R');
        item.addActionListener(controller.getProofTextParserAction());
        item.setToolTipText("Enter proof text and transform it into QEDEQ syntax.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/actions/format-indent-more.png"));
        menu.add(item);

        item = MenuHelper.createMenuItem("Preferences", 'P');
        item.addActionListener(controller.getPreferencesAction());
        item.setToolTipText("Edit various application parameters.");
        item.setIcon(GuiHelper.readImageIcon("tango/" + resolution + "/categories/preferences-system.png"));
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
            GuiHelper.readImageIcon("tango/" + resolution + "/apps/help-browser.png"), 'H');
        item.setToolTipText("Integrated help system.");
        item.addActionListener(controller.getHelpAction());
        menu.add(item);
        if (!MenuHelper.isAboutInOSMenu()) {
            menu.addSeparator();
            item = MenuHelper.createMenuItem("About",
                GuiHelper.readImageIcon("qedeq/" + resolution  + "/qedeq.png"), 'a');
            item.addActionListener(controller.getAboutAction());
            menu.add(item);
        }
        return menu;
    }

}
