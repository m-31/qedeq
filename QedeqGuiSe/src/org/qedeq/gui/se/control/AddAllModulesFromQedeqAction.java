/* $Id: AddAllModulesFromQedeqAction.java,v 1.4 2008/07/26 07:57:44 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.log.QedeqLog;

/**
 * Add all modules from the QEDEQ web page.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
class AddAllModulesFromQedeqAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = AddAllModulesFromQedeqAction.class;

    public void actionPerformed(final ActionEvent e) {
        Trace.trace(CLASS, this, "actionPerformed", e);

        final Thread thread = new Thread() {
            public void run() {
                QedeqLog.getInstance().logRequest(
                    "Load all modules from \"http://www.qedeq.org\"");
                final boolean success = KernelContext.getInstance()
                    .loadAllModulesFromQedeq();
                if (success) {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "All modules from project page successfully loaded and checked again."
                        );
                } else {
                    QedeqLog.getInstance().logFailureReply(
                        "Some modules couldn't be loaded and checked.",
                        "see detailed status entries");
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

}
