/* $Id: CheckLogicAction.java,v 1.3 2007/10/07 16:39:59 m31 Exp $
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

import javax.swing.AbstractAction;

import org.qedeq.gui.se.tree.NothingSelectedException;
import org.qedeq.kernel.bo.control.QedeqBoFormalLogicChecker;
import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.LogicalState;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;

/**
 * Check logical correctness of modules.
 */
class CheckLogicAction extends AbstractAction {

    /** Controller reference. */
    private final QedeqController controller;

    /**
     * Constructor.
     *
     * @param   controller  Reference to controller.
     */
    CheckLogicAction(final QedeqController controller) {
        this.controller = controller;
    }

    /* inherited
     */
    public void actionPerformed(final ActionEvent e) {
        final String method = "actionPerformed";
        Trace.begin(this, method);
        try {
            final ModuleProperties[] props;
            try {
                props = controller.getSelected();
            } catch (NothingSelectedException ex) {
                controller.selectionError();
                return;
            }

            final Thread thread = new Thread() {
                public void run() {
                    for (int i = 0; i < props.length; i++) {
                        if (LoadingState.STATE_LOADED != props[i].getLoadingState()) {
                            break;
                        }
                        try {
                            // TODO mime 20070830: checking should be a method of KernelContext
                            //      also all conversion jobs to get an XmlFileExceptionList
                            //      should be made there!!!
                            QedeqLog.getInstance().logRequest("Check logical correctness for \""
                                + props[i].getAddress() + "\"");
                            props[i].setLogicalProgressState(LogicalState.STATE_INTERNAL_CHECKING);
                            ModuleEventLog.getInstance().stateChanged(props[i]);
                            QedeqBoFormalLogicChecker.check(KernelContext.getInstance()
                                .getLocalName(props[i].getModuleAddress()), props[i].getModule());
                            props[i].setLogicalProgressState(LogicalState.STATE_CHECKED);
                            ModuleEventLog.getInstance().stateChanged(props[i]);
                            QedeqLog.getInstance().logSuccessfulReply(
                                "Check of logical correctness successful for \""
                                + props[i].getAddress() + "\"");
                        } catch (ModuleDataException e) {
                            final String msg = "Check of logical correctness failed for \""
                                + props[i].getAddress() + "\"";
                            Trace.fatal(this, method, msg, e);

                            final XmlFileExceptionList xl =
                                ModuleDataException2XmlFileException.createXmlFileExceptionList(e,
                                props[i].getModule());
                            props[i].setLogicalFailureState(
                                LogicalState.STATE_INTERNAL_CHECK_FAILED, xl);
                            ModuleEventLog.getInstance().stateChanged(props[i]);
                            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        } finally {
            Trace.end(this, method);
        }
    }

}
