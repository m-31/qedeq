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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.gui.se.control.SelectionListener;
import org.qedeq.gui.se.element.CPTextArea;
import org.qedeq.gui.se.util.CurrentLineHighlighterUtility;
import org.qedeq.gui.se.util.DocumentMarker;
import org.qedeq.gui.se.util.DocumentMarkerPainter;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * View source of QEDEQ module.
 *
 * @author  Michael Meyling
 */

public class QedeqPane extends JPanel implements SelectionListener {

    /** This class. */
    private static final Class CLASS = QedeqPane.class;

    /** Reference to module properties. */
    private QedeqBo prop;

    /** Here is the QEDEQ module source. */
    private JTextArea qedeq = new CPTextArea(false) {
        public String getToolTipText(final MouseEvent e) {
            if (errorMarker == null || warningMarker == null) {
                setToolTipText(null);
                return super.getToolTipText();
            }
            // --- determine locations ---
            TextUI mapper = qedeq.getUI();
            final int i = mapper.viewToModel(qedeq, e.getPoint());
            final List errNos =  errorMarker.getBlockNumbersForOffset(i);
            final List warningNos =  warningMarker.getBlockNumbersForOffset(i);
            if (errNos.size() == 0 && warningNos.size() == 0) {
                setToolTipText(null);
                return super.getToolTipText(e);
            }
            final StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < errNos.size(); j++) {
                if (j > 0) {
                    buffer.append("\n");
                }
                buffer.append((prop.getErrors().get(((Integer) errNos.get(j)).intValue())
                    .getMessage()));
            }
            for (int j = 0; j < warningNos.size(); j++) {
                if (j > 0) {
                    buffer.append("\n");
                }
                buffer.append((prop.getWarnings().get(((Integer) warningNos.get(j)).intValue())
                    .getMessage()));
            }
            setToolTipText(GuiHelper.getToolTipText(buffer.toString()));
            return getToolTipText();
        }
    };

    /** Our error highlighter for the text areas. */
    private DocumentMarker errorMarker;

    /** Our warning highlighter for the text areas. */
    private DocumentMarker warningMarker;

    /**
     * Creates new Panel.
     */
    public QedeqPane() {
        super(false);
        this.prop = null;
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
        qedeq.setToolTipText("");
        qedeq.putClientProperty("JTextArea.infoBackground", Boolean.TRUE);
        qedeq.setLineWrap(true); // TODO mime 20080509: make this configurable
    }

    /**
     * Set new model. To make the new model visible {@link #updateView} must be called.
     *
     * @param   prop    New QEDEQ module.
     */
    public synchronized void setModel(final QedeqBo prop) {
        Trace.trace(CLASS, this, "setModel", prop);
        if (!EqualsUtility.equals(this.prop, prop)) {
            this.prop = prop;
            Runnable setModel = new Runnable() {
                public void run() {
                    updateView();
                }
            };
            SwingUtilities.invokeLater(setModel);
        }
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
                if (prop.getModuleAddress().isFileAddress()) {
                    qedeq.setEditable(true);
                } else {
                    // TODO 20100319 m31: show "readonly" as label somewhere
                    qedeq.setEditable(false);
                }
                // we want the background be same even if area is not editable
                qedeq.setBackground(UIManager.getColor("TextArea.background"));
                qedeq.setCaretPosition(0);
                qedeq.getCaret().setSelectionVisible(true);

                // TODO m31 20100707: duplicate code, refactor
                warningMarker = new DocumentMarker(qedeq, new DocumentMarkerPainter(
                        GuiHelper.getWarningTextBackgroundColor()));
                final SourceFileExceptionList pw = prop.getWarnings();
                if (pw != null) {
                    for (int i = 0; i < pw.size(); i++) {
                        if (pw.get(i).getSourceArea() != null) {
                            try {
                                final SourceArea sa = pw.get(i).getSourceArea();
                                if (sa != null) {
                                    final int from = sa.getStartPosition().getRow() - 1;
                                    int to = sa.getEndPosition().getRow() - 1;
// for line marking only:
//                                    warningMarker.addWarningLines(from, to,
//                                        sa.getStartPosition().getColumn() - 1);
                                    warningMarker.addMarkedBlock(from,
                                        sa.getStartPosition().getColumn() - 1,
                                        to, sa.getEndPosition().getColumn() - 1);
                                    continue;
                                }
                            } catch (BadLocationException e) {  // should not occur
                                Trace.fatal(CLASS, this, "updateView", "Programming error?", e);
                            }
                        } else {
                            warningMarker.addEmptyBlock();
                        }
                    }
                }
                errorMarker = new DocumentMarker(qedeq, new DocumentMarkerPainter(
                    GuiHelper.getErrorTextBackgroundColor()));
                final SourceFileExceptionList pe = prop.getErrors();
                if (pe != null) {
                    for (int i = 0; i < pe.size(); i++) {
                        if (pe.get(i).getSourceArea() != null) {
                            try {
                                final SourceArea sa = pe.get(i).getSourceArea();
                                if (sa != null) {
                                    final int from = sa.getStartPosition().getRow() - 1;
                                    int to = sa.getEndPosition().getRow() - 1;
// for line marking only:
//                                    errorMarker.addMarkedLines(from, to,
//                                        sa.getStartPosition().getColumn() - 1);
                                    errorMarker.addMarkedBlock(from,
                                        sa.getStartPosition().getColumn() - 1,
                                        to, sa.getEndPosition().getColumn() - 1);
                                    continue;
                                }
                            } catch (BadLocationException e) {  // should not occur
                                Trace.fatal(CLASS, this, "updateView", "Programming error?", e);
                            }
                        } else {
                            errorMarker.addEmptyBlock();
                        }
                    }
                }
                Trace.trace(CLASS, this, "updateView", "Text updated");
            } catch (IOException ioException) {
                qedeq.setEditable(false);
                qedeq.setText("");
                Trace.trace(CLASS, this, "updateView", ioException);
            }
        } else {
            errorMarker = null;
            warningMarker = null;
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


// LATER m31 20100830: not used any longer
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
     * Jump to error location. Uses only <code>error</code> to select correct marker.
     *
     * @param   error   Selected error number. Starts with 0.
     * @param   sf      Selected error.
     */
    public synchronized void selectError(final int error, final SourceFileException sf) {
        if (errorMarker != null) {
            this.requestFocus();
            qedeq.requestFocus();
            qedeq.setCaretPosition(errorMarker.getLandmarkOffsetForBlock(error));
        } else {
            Trace.paramInfo(CLASS, "selectError", "errorMarker", "null");
        }
    }

    /**
     * Jump to warning location. Uses only <code>warning</code> to select correct marker.
     *
     * @param   warning Selected warning number. Starts with 0.
     * @param   sf      Selected warning.
     */
    public synchronized void selectWarning(final int warning, final SourceFileException sf) {
        int block = warning;
//        if (prop != null && prop.getErrors().size() > 0) {
//            block += prop.getErrors().size();
//        }
        if (warningMarker != null) {
            this.requestFocus();
            qedeq.requestFocus();
            qedeq.setCaretPosition(warningMarker.getLandmarkOffsetForBlock(block));
        } else {
            Trace.paramInfo(CLASS, "selectWarning", "warningMarker", "null");
        }
    }

}

