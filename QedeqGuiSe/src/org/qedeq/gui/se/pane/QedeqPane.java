/* $Id: QedeqPane.java,v 1.5 2008/03/27 05:14:03 m31 Exp $
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

package org.qedeq.gui.se.pane;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.BadLocationException;

import org.qedeq.gui.se.control.ErrorSelectionListener;
import org.qedeq.gui.se.control.ErrorSelectionListenerList;
import org.qedeq.gui.se.util.CurrentLineHighlighterUtility;
import org.qedeq.gui.se.util.DocumentMarker;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.trace.Trace;

/**
 * View source of QEDEQ module.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */

public class QedeqPane extends JPanel implements ErrorSelectionListener {

    /** This class. */
    private static final Class CLASS = QedeqPane.class;

    /** Reference to module properties. */
    private QedeqBo prop;

    /** Here is the QEDEQ module source. */
    private JTextArea qedeq = new JTextArea() {
        public String getToolTipText(final MouseEvent e) {
            if (marker == null) {
                setToolTipText(null);
                return super.getToolTipText();
            }
            // --- determine locations ---
            TextUI mapper = qedeq.getUI();
            final int i = mapper.viewToModel(qedeq, e.getPoint());
            final List errNos =  marker.getBlockNumbersForOffset(i);
            if (errNos.size() == 0) {
                setToolTipText(null);
                return super.getToolTipText(e);
            }
            final StringBuffer buffer = new StringBuffer();
            buffer.append("<html>");
            for (int j = 0; j < errNos.size(); j++) {
                if (j > 0) {
                    buffer.append("<br>");
                }
                buffer.append((prop.getException().get(((Integer) errNos.get(j)).intValue())
                    .getMessage()));    // TODO mime 20080417: escape for HTML presenatation
            }
            buffer.append("</html>");
            setToolTipText(buffer.toString());
            return getToolTipText();
        }
    };

    /** Marks error areas. */
    private DocumentMarker marker;

    /**
     * Creates new Panel.
     */
    public QedeqPane(final QedeqBo prop) {
        super(false);
        this.prop = prop;
        setupView();
        updateView();
        // add this instance as error selection listener
        ErrorSelectionListenerList.getInstance().addListener(this);
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
        qedeq.setToolTipText("");
        qedeq.putClientProperty("JTextArea.infoBackground", Boolean.TRUE);
        qedeq.setLineWrap(true); // FIXME
    }

    /**
     * Set new model. To make the new model visible {@link #updateView} must be called.
     *
     * @param   prop
     */
    public void setModel(final QedeqBo prop) {
        Trace.trace(CLASS, this, "setModel", prop);
        this.prop = prop;
        updateView();
    }


    public QedeqBo getModel() {
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
                qedeq.setText(KernelContext.getInstance().getSource(prop.getModuleAddress()));
                CurrentLineHighlighterUtility.install(qedeq);
                qedeq.setLineWrap(getLineWrap());
                if (prop.hasFailures()) {
                    final SourceFileExceptionList pe = prop.getException();
                    if (prop.getModuleAddress().isFileAddress()) {
                        qedeq.setEditable(true);
                    } else {
                        qedeq.setEditable(false);
                    }
                    qedeq.setCaretPosition(0);
                    qedeq.getCaret().setSelectionVisible(true);
                    marker = new DocumentMarker(qedeq);
                    for (int i = 0; i < pe.size(); i++) {
                        if (pe.get(i).getSourceArea() != null) {
                            try {
                                final SourceArea sa = pe.get(i).getSourceArea();
                                if (sa != null) {
                                    final int from = sa.getStartPosition().getLine() - 1;
                                    int to = sa.getEndPosition().getLine() - 1;
// for line marking only:
//                                    marker.addMarkedLines(from, to,
//                                        sa.getStartPosition().getColumn() - 1);
                                    marker.addMarkedBlock(from,
                                        sa.getStartPosition().getColumn() - 1,
                                        to, sa.getEndPosition().getColumn() - 1);
                                    continue;
                                }
                            } catch (BadLocationException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        marker.addEmptyBlock();

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
            } catch (IOException ioException) {
                qedeq.setEditable(false);
                qedeq.setText("");
                Trace.trace(CLASS, this, "updateView", ioException);
            }
        } else {
            marker = null;
            CurrentLineHighlighterUtility.uninstall(qedeq);
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

//    public void selectError(final int number) {
//        if (prop != null && prop.getException() != null && number < prop.getException().size()) {
//            final SourceFileException sfe = prop.getException().get(number);
//            if (sfe.getSourceArea() != null && sfe.getSourceArea().getStartPosition() != null) {
//                highlightLine(sfe.getSourceArea().getStartPosition().getLine());
//            }
//        }
//    }
*/

    /**
     * Jump to error location. Uses only <code>errorNumber</code> to select correct marker.
     *
     * @param   errorNumber Selected error number. Starts with 0.
     * @param   sf          Selected error.
     */
    public void selectError(final int errorNumber, final SourceFileException sf) {
        if (marker != null) {
            this.requestFocus();
            qedeq.requestFocus();
            qedeq.setCaretPosition(marker.getLandmarkOffsetForBlock(errorNumber));
        }
    }


}

