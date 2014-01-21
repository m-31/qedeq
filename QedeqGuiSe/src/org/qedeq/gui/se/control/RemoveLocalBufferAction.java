/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.bo.KernelContext;

/**
 * Remove all QEDEQ module files from memory and local buffer.
 */
class RemoveLocalBufferAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = RemoveLocalBufferAction.class;

    /**
     * Constructor.
     */
    RemoveLocalBufferAction() {
        // nothing to do
    }

    public void actionPerformed(final ActionEvent e) {
        Trace.trace(CLASS, this, "actionPerformed", e);

        final Thread thread = new Thread() {
            public void run() {
                KernelContext.getInstance().clearLocalBuffer();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
