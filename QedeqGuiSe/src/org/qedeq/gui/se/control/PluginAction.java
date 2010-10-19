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

package org.qedeq.gui.se.control;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.tree.NothingSelectedException;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin;
import org.qedeq.kernel.bo.service.utf8.Qedeq2Utf8Plugin;
import org.qedeq.kernel.common.Plugin;

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

    /* inherited
     */
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

            final Thread thread = new Thread() {
                public void run() {
                    final Map parameters = new HashMap();
                    // FIXME m31 20100924: these are only LaTeX Parameters
                    parameters.put(Qedeq2LatexPlugin.class.getName() + "$info", "true");
                    parameters.put(Qedeq2Utf8Plugin.class.getName() + "$info", "true");
//                    parameters.put(HeuristicCheckerPlugin.class.getName() + "$model",
//                        ZeroModel.class.getName());
                    for (int i = 0; i < props.length; i++) {
                        KernelContext.getInstance().executePlugin(plugin.getPluginId(),
                            props[i].getModuleAddress(), parameters);
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ImageIcon getIcon() {
        if (plugin.getPluginName().endsWith("LaTeX")) {
            return GuiHelper.readImageIcon("tango/16x16/mimetypes/x-office-document.png");
        } else if (plugin.getPluginName().startsWith("Test")) {
            return GuiHelper.readImageIcon("tango/16x16/actions/system-search.png");
        } else if (plugin.getPluginName().endsWith("earch")) {
            return GuiHelper.readImageIcon("tango/16x16/categories/applications-system.png");
        } else {
            return GuiHelper.readImageIcon("tango/16x16/mimetypes/x-office-document.png");
        }
    }

}
