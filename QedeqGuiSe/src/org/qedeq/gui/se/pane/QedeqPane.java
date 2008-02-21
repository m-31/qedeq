/* $Id: QedeqPane.java,v 1.4 2008/01/26 12:38:27 m31 Exp $
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

package org.qedeq.gui.se.pane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.BadLocationException;

import org.qedeq.kernel.common.ModuleProperties;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;

/**
 * View source of QEDEQ module.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */

public class QedeqPane extends JPanel {

    /** This class. */
    private static final Class CLASS = QedeqPane.class;

    /** Reference to module properties. */
    private ModuleProperties prop;

    /** Here is the QEDEQ module source. */
    private JTextArea qedeq = new JTextArea();

    /**
     * Creates new Panel.
     */
    public QedeqPane(final ModuleProperties prop) {
        super(false);
        this.prop = prop;
        setupView();
        updateView();
    }

    /**
     * Assembles the GUI components of the panel.
     */
    public void setupView() {

        final JScrollPane scroller = new JScrollPane();
        final JViewport vp = scroller.getViewport();
        vp.add(qedeq);
        this.setLayout(new BorderLayout(0, 0));
        this.add(scroller);
        setBorder(BorderFactory.createEmptyBorder());
        qedeq.setEditable(false);
        qedeq.putClientProperty("JTextArea.infoBackground", Boolean.TRUE);

    }

    /**
     * Set new model. To make the new model visible {@link #updateView} must be called.
     *
     * @param   prop
     */
    public void setModel(final ModuleProperties prop) {
        Trace.trace(CLASS, this, "setModel", prop);
        this.prop = prop;
        updateView();
    }


    public ModuleProperties getModel() {
        return prop;
    }


    public void setLineWrap(final boolean wrap) {
        qedeq.setLineWrap(wrap);
    }

    public boolean getLineWrap() {
        return qedeq.getLineWrap();
    }

    /**
     * Update from model.
     */
    public synchronized void updateView() {
        Trace.begin(CLASS, this, "updateView");
        if (prop != null) {
            try {
                final File file = KernelContext.getInstance().getLocalFilePath(
                    prop.getModuleAddress());
                if (file.canRead()) {
                    final StringBuffer buffer = new StringBuffer();
                        IoUtility.loadFile(file, buffer, IoUtility.getWorkingEncoding(
                            prop.getEncoding()));
//                    this.qedeqScroller.getViewport().setViewPosition(new Point(0, 0));
                    qedeq.setText(buffer.toString());
                    if (prop.hasFailures()) {
                        final SourceFileExceptionList pe = prop.getException();
                        if (prop.getModuleAddress().isFileAddress()) {
                            qedeq.setEditable(true);
                        } else {
                            qedeq.setEditable(false);
                        }
                        qedeq.setCaretPosition(0);
                        qedeq.getCaret().setSelectionVisible(true);
                        qedeq.setSelectedTextColor(Color.RED);
                        qedeq.setSelectionColor(Color.YELLOW);
                        if (pe.get(0).getSourceArea() != null) {
                            highlightLine(pe.get(0).getSourceArea().getStartPosition().getLine());
                        }
                    } else {    // TODO if file module is edited status must change
                        if (prop.getModuleAddress().isFileAddress()) {
                            qedeq.setEditable(true);
                        } else {
                            qedeq.setEditable(false);
                        }
                        qedeq.getCaret().setSelectionVisible(false);
                        qedeq.setCaretPosition(0);
                        // LATER mime 20080131: is this a valid setting for the default colors?
                        qedeq.setSelectedTextColor(MetalLookAndFeel.getHighlightedTextColor());
                        qedeq.setSelectionColor(MetalLookAndFeel.getTextHighlightColor());
                    }
                    Trace.trace(CLASS, this, "updateView", "Text updated");
                } else {
                    throw new IOException("File " + file.getCanonicalPath() + " not readable!");
                }
            } catch (IOException ioException) {
                qedeq.setEditable(false);
                qedeq.setText("");
                Trace.trace(CLASS, this, "updateView", ioException);
            }
        } else {
            qedeq.setEditable(false);
            qedeq.setText("");
            Trace.end(CLASS, this, "updateView");
        }
        this.repaint();
    }


    /**
     * Get QEDEQ text, if QEDEQ text is editable.
     *
     * @return  QEDEQ text source.
     * @throws  IllegalStateException   QEDEQ text is not editable.
     */
    public final String getEditedQedeq() {
        if (qedeq.isEditable()) {
            return this.qedeq.getText();
        } else {
            throw new IllegalStateException("no editable QEDEQ text");
        }
    }


    /**
     * Was the content changed?
     *
     * @return  content modified?
     */
    public final boolean isContentChanged() {
        return !this.qedeq.isEditable();
    }


// TODO not used any longer
/*
    private final int findCaretPosition(final int i) {
        if (i == 1) {
            return 0;
        }
        final String s = qedeq.getText();
        int j = 0;
        int k = 0;
        for (; j < s.length(); j++) {
            if (s.charAt(j) == '\n') {
                k++;
            }
            if (k == i - 1) {
                return j + 1;
            }
        }
        return 0;
    }
*/
    private final void highlightLine(final int line) {
        Trace.param(CLASS, this, "highlightLine", "line", line);
        int j;
        try {
            j = qedeq.getLineStartOffset(line - 1);
        } catch (BadLocationException e) {
            Trace.trace(CLASS, this, "highlightLine", e);
            j = 0;
        }
        int k;
        try {
            k = qedeq.getLineEndOffset(line - 1);
        } catch (BadLocationException e) {
            Trace.trace(CLASS, this, "highlightLine", e);
            k = qedeq.getText().length();
        }
        Trace.trace(CLASS, this, "highlightLine", "from " + j + " to " + k);

        qedeq.setCaretPosition(j);
        qedeq.moveCaretPosition(k);
    }


}

