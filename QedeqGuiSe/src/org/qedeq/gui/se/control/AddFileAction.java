/* $Id: AddFileAction.java,v 1.4 2008/01/26 12:38:27 m31 Exp $
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
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.qedeq.gui.se.pane.QedeqGuiConfig;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;

/**
 * Load new module from local file.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
class AddFileAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = AddFileAction.class;

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
        Trace.trace(CLASS, this, "actionPerformed", e);
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
        final ModuleAddress address;

        try {
//              TODO causes problems: leads to "%20" entries for spaces
//                url = chooser.getSelectedFile().toURI().toURL().toString();
//              these must be converted like:
//                "URI File=" + new File(new URI(this.address)).getAbsoluteFile()
            // remember directory
            file = chooser.getSelectedFile().getParentFile();

            QedeqGuiConfig.getInstance().setFileBrowserStartDirecty(file);
            address = KernelContext.getInstance().getModuleAddress(chooser.getSelectedFile());
        } catch (IOException ie) {
            Trace.trace(CLASS, this, "actionPerformed", "no correct URL", ie);
            JOptionPane.showMessageDialog(
                controller.getMainFrame(),       // the parent that the dialog blocks
                "this is no valid QEDEQ file: " + chooser.getSelectedFile()
                + "\n" + ie.getMessage(), // message
                "Error",                         // title
                JOptionPane.ERROR_MESSAGE        // message type
            );
            return;
        }

        controller.addToModuleHistory(address.getURL().toString());
        final Thread thread = new Thread() {
            public void run() {
                try {
               // FIXME mime 20071231: move logging out of gui!
                    QedeqLog.getInstance().logRequest("Load module \"" + address + "\"");
                    final QedeqBo prop
                        = KernelContext.getInstance().loadModule(address);
                    QedeqLog.getInstance().logSuccessfulReply("Module \""
                        + prop.getModuleAddress().getFileName()
                        + "\" was successfully loaded.");
                } catch (final SourceFileExceptionList e) {
                    Trace.trace(CLASS, controller, "actionPerformed", e);
                    QedeqLog.getInstance().logFailureReply(
                        "Loading failed", e.getMessage());
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

}
