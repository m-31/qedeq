/* $Id: QedeqPane.java,v 1.2 2007/10/07 16:39:59 m31 Exp $
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
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.BadLocationException;

import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * View source of QEDEQ module.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */

public class QedeqPane extends JPanel {

    /** Reference to module properties. */
    private ModuleProperties prop;

    /** Here is the QEDEQ module source. */
    private JTextArea qedeq = new JTextArea();

    private JTextArea error = new JTextArea();

    private JScrollPane qedeqScroller = new JScrollPane();

    private JSplitPane splitPane = new JSplitPane(JSplitPane
        .VERTICAL_SPLIT);


    /**
     * Creates new Panel.
     */
    public QedeqPane(final ModuleProperties prop) {
        super(false);
        this.prop = prop;
        setupView3();
        updateView();
    }

    public void setupView3() {
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

    private void setupView2() {
        qedeq.setMargin(new Insets(6, 10, 4, 6));
        // Non-editable but shall use the editable background.
        qedeq.setEditable(false);

        qedeq.setDragEnabled(true);
        qedeq.setFont(new Font("monospaced", Font.PLAIN, 12));
        qedeq.setAutoscrolls(true);
        qedeq.setCaretPosition(0);
        qedeq.setEditable(false);
        qedeq.getCaret().setVisible(false);
        qedeq.setLineWrap(true);
        qedeq.setWrapStyleWord(true);
        qedeq.setFocusable(true);

        qedeq.putClientProperty("JTextArea.infoBackground", Boolean.TRUE);
        Component textPane = new JScrollPane(qedeq);

        FormLayout layout = new FormLayout(
                "fill:300dlu:grow",
                "fill:default:grow");
//                        "fill:100dlu:grow",
//                        "fill:56dlu:grow, 4dlu, p");
        JPanel panel = new JPanel(layout);
        CellConstraints cc = new CellConstraints();
        panel.setBorder(Borders.DIALOG_BORDER);
        panel.add(textPane, cc.xy(1, 1));
        this.add(panel);
    }

    /**
     * Assembles the gui components of the panel.
     */
    private final void setupView() {
        FormLayout layout = new FormLayout(
        "fill:50dlu:grow");    // columns
//        + "pref, 3dlu, pref");                  // rows

    DefaultFormBuilder builder = new DefaultFormBuilder(layout);
    builder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    builder.getPanel().setOpaque(false);
    builder.setDefaultDialogBorder();

        qedeq.setDragEnabled(true);
        qedeq.setFont(new Font("monospaced", Font.PLAIN, 12));  // TODO mime 20071220: hard coded
        qedeq.setAutoscrolls(true);
        qedeq.setCaretPosition(0);
        qedeq.setEditable(false);
        qedeq.getCaret().setVisible(false);
        qedeq.setLineWrap(true);
        qedeq.setWrapStyleWord(true);
        qedeq.setFocusable(true);

        builder.append(qedeq);

        add(builder.getPanel());


        error.setFont(new Font("monospaced", Font.PLAIN, 12));
        error.setAutoscrolls(true);
        error.setCaretPosition(0);
        error.setEditable(false);
        error.getCaret().setVisible(false);
        qedeq.setLineWrap(true); // TODO mime ????
        error.setFocusable(true);
        error.setForeground(Color.RED);
        error.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                updateView();
            }

        });

/*
        final JViewport port = this.qedeqScroller.getViewport();
        port.add(qedeq);

        final JScrollPane errorScroller = new JScrollPane();
        final JViewport port2 = errorScroller.getViewport();
        port2.add(error);

        this.setLayout(new BorderLayout(1, 1));

        splitPane.setTopComponent(qedeqScroller);
        splitPane.setBottomComponent(errorScroller);
        splitPane.setResizeWeight(1);
        splitPane.setOneTouchExpandable(true);

        this.add(splitPane);
        splitPane.setDividerLocation(this.getHeight());

        this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                Trace.trace(this, "componentHidden", e);
            }
            public void componentShown(ComponentEvent e) {
                Trace.trace(this, "componentHidden", e);
            }
        });

        this.add(qedeq);
*/
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
                        StringBuffer pointer = new StringBuffer();
                        if (pe.get(0).getSourceArea() != null) {
                            highlightLine(pe.get(0).getSourceArea().getStartPosition().getLine());
                            // reserve 3 text lines for error description
                            splitPane.setDividerLocation(splitPane.getHeight()
                                - splitPane.getDividerSize()
                                - error.getFontMetrics(error.getFont()).getHeight() * 3 - 4);
                            pointer.append(IoUtility.getSpaces(pe.get(0).getSourceArea()
                                .getStartPosition().getColumn() - 1));
                            pointer.append("^");
                        }
                        final URL local = (IoUtility.toUrl(
                            new File(KernelContext.getInstance()
                                .getLocalFilePath(prop.getModuleAddress()))));
                        // FIXME mime 20071128: the above code must be simpler!!!
                        //      prop.getModuleAddress().localizeInFileSystem(prop.getUrl());
                        error.setText(pe.get(0).getDescription(local) + "\n"
                                + pe.get(0).getLine(local) + "\n"
                                + pointer);
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
                        error.setText("");
                        splitPane.setDividerLocation(this.getHeight());
                    }
                    Trace.trace(this, "updateView", "Text updated");
                } else {
                    throw new IOException("File " + file.getCanonicalPath() + " not readable!");
                }
            } catch (IOException ioException) {
                qedeq.setEditable(false);
                qedeq.setText("");
                error.setText("");
                splitPane.setDividerLocation(this.getHeight());
                Trace.trace(this, "updateView", ioException);
            }
        } else {
            qedeq.setEditable(false);
            qedeq.setText("");
            error.setText("");
            splitPane.setDividerLocation(this.getHeight());
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

