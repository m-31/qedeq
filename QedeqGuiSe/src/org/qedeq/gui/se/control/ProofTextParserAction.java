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

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.pane.ProofParserPane;
import org.qedeq.kernel.bo.parser.AsciiMathParser;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * Show preferences window.
 *
 * @author  Michael Meyling
 */
class ProofTextParserAction extends AbstractAction {

    /** This class. */
    private static final Class CLASS = ProofTextParserAction.class;

    /** Controller reference. */
    private final QedeqController controller;

    /**
     * Constructor.
     *
     * @param   controller  Controller.
     */
    ProofTextParserAction(final QedeqController controller) {
        this.controller = controller;
    }

    public void actionPerformed(final ActionEvent e) {
        final String resourceName = "proofTextMathOperators.xml";
        try {
            final ProofParserPane pane = new ProofParserPane("Proof Text", new AsciiMathParser(),
                "QEDEQ Proof Text Parser Sample",
                resourceName,
                "      A v A -> A\n"
                + "(1)  A -> A\n"
                + "(2)  (A -> C) -> ((B -> C) -> ((A v B) -> C))\n"
                + "(3)  (A -> C) -> ((A -> C) -> ((A v A) -> C))\n"
                + "(4)  (A -> A) -> ((A -> A) -> (/A v A) -> A))\n"
                );
            pane.setVisible(true);
        } catch (final SourceFileExceptionList xl) {
            Trace.fatal(CLASS, this, "actionPerformed", "Parser Window can not be opened", xl);
            JOptionPane.showMessageDialog(
                controller.getMainFrame(), "Parser Window can not be opened. There is a problem with \""
                + resourceName + "\"\n\n"
                    + "Just deleting this file in the config directory should fix the error."  + "\n\n"
                    + xl.toString(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (final RuntimeException ex) {
            Trace.fatal(CLASS, this, "actionPerformed", "unexpected problem", ex);
            JOptionPane.showMessageDialog(
                controller.getMainFrame(), "Parser Window can not be opened" + "\n"
                    + ex.toString(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
