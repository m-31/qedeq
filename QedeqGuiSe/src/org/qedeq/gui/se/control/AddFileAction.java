/* $Id: AddFileAction.java,v 1.2 2007/10/07 16:39:59 m31 Exp $
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
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.qedeq.gui.se.pane.QedeqGuiConfig;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;

/**
 * Load new module from local file.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
class AddFileAction extends AbstractAction {

    /** Controller. */
    private final QedeqController controller;

    /** Start directory for file load. */
    private File file = QedeqGuiConfig.getInstance().getFileBrowserStartDirecty();

    /**
     * Constructor.
     *
     * @param   controller
     */
    AddFileAction(final QedeqController controller) {
        this.controller = controller;
    }

    public void actionPerformed(final ActionEvent e) {
        Trace.trace(this, "actionPerformed", e);
        JFileChooser chooser = new JFileChooser(file);
        final FileFilter filter = new FileFilter() {
            public boolean accept(final File f) {
                if (f.isDirectory() || f.getPath().endsWith(".xml")) {
                    return true;
                }
                return false;
            }

            // description of this filter
            public String getDescription() {
                return "Qedeq Modules";
            }
        };

        chooser.setFileFilter(filter);
        final int returnVal = chooser.showOpenDialog(controller.getMainFrame());

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        final String url;

        try {
//              TODO causes problems: leads to "%20" entries for spaces
//                url = chooser.getSelectedFile().toURI().toURL().toString();
//              these must be converted like:
//                "URI File=" + new File(new URI(this.address)).getAbsoluteFile()
            url = chooser.getSelectedFile().toURL().toString();

            // remember directory
            file = chooser.getSelectedFile().getParentFile();
            QedeqGuiConfig.getInstance().setFileBrowserStartDirecty(file);
        } catch (MalformedURLException ex) {
            Trace.trace(this, "actionPerformed", ex);
            return;
        }

        controller.addToModuleHistory(url);
        final Thread thread = new Thread() {
            public void run() {
                try {
                    QedeqLog.getInstance().logRequest("Load module \"" + url + "\"");
                    final QedeqBo module
                        = KernelContext.getInstance().loadModule(url);
                    QedeqLog.getInstance().logSuccessfulReply("Module \""
                        + module.getModuleAddress().getFileName()
                        + "\" was successfully loaded and checked.");
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
