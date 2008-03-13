/* $Id: MakeLatexAction.java,v 1.4 2008/01/26 12:38:27 m31 Exp $
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

import javax.swing.AbstractAction;

import org.qedeq.gui.se.tree.NothingSelectedException;
import org.qedeq.kernel.bo.control.KernelQedeqBo;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.latex.Xml2Latex;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;

/**
 * Create LaTeX file out of selected QEDEQ module files.
 */
class MakeLatexAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = MakeLatexAction.class;

    /** Controller reference. */
    private final QedeqController controller;

    /**
     * Constructor.
     *
     * @param   controller  Reference to controller.
     */
    MakeLatexAction(final QedeqController controller) {
        this.controller = controller;
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
                    for (int i = 0; i < props.length; i++) {
                        try {
                            QedeqLog.getInstance().logRequest("Generate Latex from \""
                                + props[i].getUrl() + "\"");
                            final String[] languages = controller.getSupportedLanguages(props[i]);
                            for (int j = 0; j < languages.length; j++) {
                                final String result =
                                    Xml2Latex.generate((KernelQedeqBo) // FIXME mime 20080303
                                                                       // move into KernelServices
                                        props[i], null, languages[j], null);
                                if (languages[j] != null) {
                                    QedeqLog.getInstance().logSuccessfulReply(
                                        "LaTeX for language \"" + languages[j]
                                        + "\" was generated from \""
                                        + props[i].getUrl() + "\" into \"" + result + "\"");
                                } else {
                                    QedeqLog.getInstance().logSuccessfulReply(
                                        "LaTeX for default language "
                                        + "was generated from \""
                                        + props[i].getUrl() + "\" into \"" + result + "\"");
                                }
                            }
                        } catch (final SourceFileExceptionList e) {
                            final String msg = "Generation failed for \""
                                + props[i].getUrl() + "\"";
                            Trace.fatal(CLASS, this, method, msg, e);
                            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
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
