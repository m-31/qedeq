/* $Id: CheckLogicAction.java,v 1.4 2007/12/21 23:34:47 m31 Exp $
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
import org.qedeq.kernel.bo.control.LoadRequiredModules;
import org.qedeq.kernel.bo.control.QedeqBoFormalLogicChecker;
import org.qedeq.kernel.bo.module.DependencyState;
import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.LogicalState;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;

/**
 * Check logical correctness of modules.
 */
class CheckLogicAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = CheckLogicAction.class;

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
        Trace.begin(CLASS, this, method);
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
                        if (LoadingState.STATE_LOADED.getCode()
                                > props[i].getLoadingState().getCode()) {
                            break;
                        }
                        try {
                            // FIXME mime 20070830: checking should be a method of KernelContext
                            //      also all conversion jobs to get an XmlFileExceptionList
                            //      should be made there!!!
                            QedeqLog.getInstance().logRequest("Check logical correctness for \""
                                + props[i].getUrl() + "\"");

                            LoadRequiredModules.loadRequired(props[i]);

                            QedeqBoFormalLogicChecker.check(props[i]);
                            QedeqLog.getInstance().logSuccessfulReply(
                                "Check of logical correctness successful for \""
                                + props[i].getUrl() + "\"");
                        } catch (final SourceFileExceptionList e) {
                            final String msg = "Check of logical correctness failed for \""
                                + props[i].getUrl() + "\"";
                            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
                        } catch (final RuntimeException e) {
                            final String msg = "Check of logical correctness failed for \""
                                + props[i].getUrl() + "\"";
                            Trace.fatal(CLASS, this, method, msg, e);
                            final SourceFileExceptionList xl =
                                new DefaultSourceFileExceptionList(e);
                            // TODO mime 20071031: every state must be able to change into
                            // a failure state, here we only assume two cases
                            if (!props[i].hasLoadedRequiredModules()) {
                                props[i].setDependencyFailureState(
                                    DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, xl);
                            } else {
                                props[i].setLogicalFailureState(
                                    LogicalState.STATE_EXTERNAL_CHECKING_FAILED, xl);
                            }
                            ModuleEventLog.getInstance().stateChanged(props[i]);
                            QedeqLog.getInstance().logFailureReply(msg, e.toString());
                        } catch (final Throwable e) {
                            final String msg = "Check of logical correctness failed for \""
                                + props[i].getUrl() + "\"";
                            Trace.fatal(CLASS, this, method, msg, e);
                            final SourceFileExceptionList xl =
                                new DefaultSourceFileExceptionList(e);
                            // TODO mime 20071031: every state must be able to change into
                            // a failure state, here we only assume two cases
                            if (props[i].isLoaded()) {  // FIXME wrong
                                props[i].setDependencyFailureState(
                                    DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, xl);
                            } else {
                                props[i].setLogicalFailureState(
                                    LogicalState.STATE_EXTERNAL_CHECKING_FAILED, xl);
                            }
                            ModuleEventLog.getInstance().stateChanged(props[i]);
                            QedeqLog.getInstance().logFailureReply(msg, e.toString());
                        }

                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

}
