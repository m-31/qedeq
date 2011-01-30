/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.tree.NothingSelectedException;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin;

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
                    final Map parameters = new HashMap();
                    parameters.put("info", "true");
                    for (int i = 0; i < props.length; i++) {
                        KernelContext.getInstance().executePlugin(Qedeq2LatexPlugin.CLASS.getName(),
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

}
