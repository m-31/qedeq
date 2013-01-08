/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.pane.QedeqGuiConfig;
import org.qedeq.gui.se.pane.TextPaneWindow;
import org.qedeq.gui.se.tree.NothingSelectedException;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.Plugin;

/**
 * Execute plugin for selected QEDEQ module files.
 */
public class PluginAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = PluginAction.class;

    /** Controller reference. */
    private final QedeqController controller;

    /** Start this plugin. */
    private Plugin plugin;

    /** Icon resolution. */
    private String resolution = QedeqGuiConfig.getInstance().getIconSize();

    /**
     * Constructor.
     *
     * @param   controller  Reference to controller.
     * @param   plugin      Start action for this plugin.
     */
    PluginAction(final QedeqController controller, final Plugin plugin) {
        this.controller = controller;
        this.plugin = plugin;
    }

    public void actionPerformed(final ActionEvent e) {
        final String method = "actionPerformed";
        Trace.begin(CLASS, this, method);
        try {
            final QedeqBo[] props;
            try {
                props = controller.getSelected();
            } catch (NothingSelectedException ex) {
                controller.selectionError();
                return;
            }

            for (int i = 0; i < props.length; i++) {
                final QedeqBo prop = props[i];
                final Thread thread = new Thread() {
                    public void run() {
                        final Object result = KernelContext.getInstance().executePlugin(
                            plugin.getPluginId(),
                            prop.getModuleAddress());
                        if (result instanceof String) {
                            final Runnable showTextResult = new Runnable() {
                                public void run() {
                                    (new TextPaneWindow(plugin.getPluginActionName(),
                                        PluginAction.this.getIcon(),
                                        (String) result)).setVisible(true);
                                }
                            };
                            SwingUtilities.invokeLater(showTextResult);
                        }
                    }
                };
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
            }
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Get plugin we work for.
     *
     * @return  The plugin we work for.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    public ImageIcon getIcon() {
        if (plugin.getPluginActionName().endsWith("LaTeX")) {
            return GuiHelper.readImageIcon("tango/" + resolution + "/mimetypes/x-office-document.png");
        } else if (-1 < plugin.getPluginActionName().indexOf("euristic")) {
            return GuiHelper.readImageIcon("tango/" + resolution + "/apps/accessories-calculator.png");
        } else if (plugin.getPluginActionName().endsWith("earch")) {
            return GuiHelper.readImageIcon("tango/" + resolution + "/categories/applications-system.png");
        } else if (-1 < plugin.getPluginActionName().indexOf("how")) {
            return GuiHelper.readImageIcon("tango/" + resolution + "/actions/edit-find.png");
        } else if (-1 < plugin.getPluginActionName().indexOf("odel")) {
            return GuiHelper.readImageIcon("oil/" + resolution + "/apps/accessories-calculator-3.png");
        } else if (-1 < plugin.getPluginActionName().indexOf("UTF-8")) {
            return GuiHelper.readImageIcon("tango/" + resolution + "/mimetypes/text-x-generic.png");
        } else if (-1 < plugin.getPluginActionName().indexOf("heck")
                && -1 < plugin.getPluginActionName().indexOf("roofs")) {
            return GuiHelper.readImageIcon("tango/" + resolution + "/actions/run.png");
        } else if (-1 < plugin.getPluginActionName().indexOf("ind")
                && -1 < plugin.getPluginActionName().indexOf("roofs")) {
            return GuiHelper.readImageIcon("oil/" + resolution + "/apps/development-java-3.png");
        } else {
            return GuiHelper.readImageIcon("tango/" + resolution + "/actions/edit-find.png");
        }
    }

}
