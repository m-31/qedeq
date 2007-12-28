/* $Id: QedeqPane.java,v 1.3 2007/12/21 23:34:47 m31 Exp $
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

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.BadLocationException;

import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * View source of QEDEQ module.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */

public class QedeqPane extends JPanel {

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
        FormLayout layout = new FormLayout(
                "min:grow",
                "0:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.setDefaultDialogBorder();
        builder.setRowGroupingEnabled(true);

        CellConstraints cc = new CellConstraints();
        builder.appendRow(new RowSpec("0:grow"));

        qedeq.setEditable(false);
        qedeq.putClientProperty("JTextArea.infoBackground", Boolean.TRUE);
        builder.add(new JScrollPane(qedeq),
            cc.xywh(builder.getColumn(), builder.getRow(), 1, 2, "fill, fill"));

    }

    /**
     * Set new model. To make the new model visible {@link #updateView} must be called.
     *
     * @param   prop
     */
    public void setModel(final ModuleProperties prop) {
        Trace.trace(this, "setModel", prop);
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
        Trace.begin(this, "updateView");
        if (prop != null) {
            try {
                final File file = new File(KernelContext.getInstance().getLocalFilePath(
                    prop.getModuleAddress()));
                if (file.canRead()) {
                    final StringBuffer buffer = new StringBuffer();
                        IoUtility.loadFile(file, buffer);
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
                        // TODO is this a valid setting for the default colors?
                        qedeq.setSelectedTextColor(MetalLookAndFeel.getHighlightedTextColor());
                        qedeq.setSelectionColor(MetalLookAndFeel.getTextHighlightColor());
                    }
                    Trace.trace(this, "updateView", "Text updated");
                } else {
                    throw new IOException("File " + file.getCanonicalPath() + " not readable!");
                }
            } catch (IOException ioException) {
                qedeq.setEditable(false);
                qedeq.setText("");
                Trace.trace(this, "updateView", ioException);
            }
        } else {
            qedeq.setEditable(false);
            qedeq.setText("");
            Trace.end(this, "updateView");
        }
        this.repaint();
    }


    /**
     * Get QEDEQ text, if QEDEQ text is editable.
     *
     * @return  qedeq text
     * @throws  IllegalStateException   qedeq text is not editable
     */
    public final String getEditedQedeq() {
        if (qedeq.isEditable()) {
            return this.qedeq.getText();
        } else {
            throw new IllegalStateException("no editable qedeq text");
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
        Trace.param(this, "highlightLine", "line", line);
        int j;
        try {
            j = qedeq.getLineStartOffset(line - 1);
        } catch (BadLocationException e) {
            Trace.trace(this, "highlightLine", e);
            j = 0;
        }
        int k;
        try {
            k = qedeq.getLineEndOffset(line - 1);
        } catch (BadLocationException e) {
            Trace.trace(this, "highlightLine", e);
            k = qedeq.getText().length();
        }
        Trace.trace(this, "highlightLine", "from " + j + " to " + k);

        qedeq.setCaretPosition(j);
        qedeq.moveCaretPosition(k);
    }


}

