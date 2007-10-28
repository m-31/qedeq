/* $Id: RemoveLocalBufferAction.java,v 1.1 2007/08/21 20:44:58 m31 Exp $
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
import java.io.IOException;

import javax.swing.AbstractAction;

import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;

/**
 * Remove all QEDEQ module files from memory and local buffer.
 */
class RemoveLocalBufferAction extends AbstractAction {

    /** Controller. */
    private final QedeqController controller;

    /**
     * Constructor.
     *
     * @param controller
     */
    RemoveLocalBufferAction(final QedeqController controller) {
        this.controller = controller;
    }

    public void actionPerformed(final ActionEvent e) {
        Trace.trace(this, "actionPerformed", e);

        final Thread thread = new Thread() {
            public void run() {
                try {
                    QedeqLog.getInstance().logRequest(
                        "Clear local buffer from all QEDEQ files.");
                    KernelContext.getInstance().clearLocalBuffer();
                    QedeqLog.getInstance().logSuccessfulReply(
                        "Local buffer was cleared.");
                } catch (IOException e) {
                    Trace.fatal(controller, "actionPerformed", "IO access problem", e);
                    QedeqLog.getInstance().logFailureReply(
                        "IO access problem", e.getMessage());
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
