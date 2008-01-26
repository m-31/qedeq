/* $Id: QedeqPane.java,v 1.3 2008/01/26 12:39:50 m31 Exp $
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

package org.qedeq.kernel.rel.test.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;

import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourcePosition;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;


/**
 * View for QEDEQ XML files.
 *
 * @author      Michael Meyling
 */

public class QedeqPane extends JFrame {

    /** This class. */
    private static final Class CLASS = QedeqPane.class;

    private JTextArea qedeq = new JTextArea();

    private JTextArea error = new JTextArea();

    private JScrollPane qedeqScroller = new JScrollPane();

    private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    private JMenuBar menu = new JMenuBar();
    
    private final SourceFileException errorPosition;

    private File file;


    /**
     * Creates new Panel.
     * 
     * @param   errorPosition   Error position. 
     */
    public QedeqPane(final SourceFileException errorPosition) {
        super("QEDEQ XML File Editor");
        this.errorPosition = errorPosition;
        setupView();
        updateView();
    }


    /**
     * Assembles the GUI components of the panel.
     */
    private final void setupView() {
        final Container pane = getContentPane();
        qedeq.setDragEnabled(true);
        qedeq.setFont(new Font("monospaced", Font.PLAIN, pane.getFont().getSize()));
        qedeq.setAutoscrolls(true);
        qedeq.setCaretPosition(0);
        qedeq.setEditable(false);
        qedeq.getCaret().setVisible(false);
        qedeq.setLineWrap(true);
        qedeq.setWrapStyleWord(true);
        qedeq.setFocusable(true);

        error.setFont(new Font("monospaced", Font.PLAIN, pane.getFont().getSize()));
        error.setAutoscrolls(true);
        error.setCaretPosition(0);
        error.setEditable(false);
        error.getCaret().setVisible(false);
        error.setFocusable(true);
        error.setForeground(Color.RED);
        error.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                updateView();
            }

        });

        final JViewport port = qedeqScroller.getViewport();
        port.add(qedeq);

        final JScrollPane errorScroller = new JScrollPane();
        final JViewport port2 = errorScroller.getViewport();
        port2.add(error);

        pane.setLayout(new BorderLayout(1, 1));

        splitPane.setTopComponent(qedeqScroller);
        splitPane.setBottomComponent(errorScroller);
        splitPane.setResizeWeight(1);
        splitPane.setOneTouchExpandable(true);

        pane.add(splitPane);

        addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                Trace.trace(CLASS, this, "componentHidden", e);
            }
            public void componentShown(ComponentEvent e) {
                Trace.trace(CLASS, this, "componentShown", e);
            }
        });
        
        menu.removeAll();
        
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        {
            final JMenuItem save = new JMenuItem("Save");
            save.setMnemonic('S');
            save.addActionListener(new AbstractAction() {
                public final void actionPerformed(final ActionEvent action) {
                    try {
                        saveQedeq();
                    } catch (IOException e) {
                         Trace.trace(CLASS, QedeqPane.this, "actionPerformed", e);
                         JOptionPane.showMessageDialog(QedeqPane.this, e.getMessage(), "Alert",
                         JOptionPane.ERROR_MESSAGE);                     
                    }
                }
            });
            fileMenu.add(save);
        }
        menu.add(fileMenu);
        setJMenuBar(menu);
        setSize(600, 400);
    }

    /**
     * Set line wrap.
     * 
     * @param   wrap    Line wrap?
     */
    final public void setLineWrap(final boolean wrap) {
        qedeq.setLineWrap(wrap);
    }

    /**
     * Get line wrap.
     * 
     * @return  Line wrap?
     */
    final public boolean getLineWrap() {
        return qedeq.getLineWrap();
    }

    /**
     * Update from model.
     */
    final public synchronized void updateView() {
        final String method = "updateView()";
        Trace.begin(CLASS, this, method);
        if (errorPosition != null) {
            try {
                final ModuleAddress address = KernelContext.getInstance().getModuleAddress(
                    errorPosition.getSourceArea().getAddress());
                file = KernelContext.getInstance().getLocalFilePath(address);
                Trace.param(CLASS, this, method, "file", file);
                if (file.canRead()) {
                    final StringBuffer buffer = new StringBuffer();
                        IoUtility.loadFile(file, buffer);
                    qedeqScroller.getViewport().setViewPosition(new Point(0, 0));
                    qedeq.setText(buffer.toString());
                    if (file.canWrite()) {
                        qedeq.setEditable(true);
                    } else {
                        qedeq.setEditable(false);
                    }
                    error.setText(errorPosition.getDescription(file));
                    error.setCaretPosition(0);
                    highlightLine();
                    // reserve 3 text lines for error description
                    splitPane.setDividerLocation(splitPane.getHeight()
                        - splitPane.getDividerSize() -
                        error.getFontMetrics(error.getFont()).getHeight() * 3 - 4);
                    Trace.trace(CLASS, this, "updateView", "Text updated");
                } else {
                    throw new IOException("File " + file.getCanonicalPath() + " not readable!");
                }
            } catch (IOException ioException) {
                qedeq.setEditable(false);
                qedeq.setText("");
                error.setText("");
                splitPane.setDividerLocation(this.getHeight());
                Trace.trace(CLASS, this, method, ioException);
            }
        } else {
            qedeq.setEditable(false);
            qedeq.setText("");
            error.setText("");
            splitPane.setDividerLocation(this.getHeight());
            Trace.end(CLASS, this, method);
        }
        this.repaint();
    }

    /**
     * Get QEDEQ text, if QEDEQ text is editable.
     *
     * @return  Qedeq text.
     * @throws  IllegalStateException   QEDEQ text is not editable
     */
    public final String getEditedQedeq() {
        if (qedeq.isEditable()) {
            return qedeq.getText();
        }
        throw new IllegalStateException("no editable qedeq text");
    }


    /**
     * Was the content changed?
     *
     * @return  Content modified?
     */
    public final boolean isContentChanged() {
        return !this.qedeq.isEditable();
    }
    
    /**
     * Save panel contents in file.
     * 
     * @throws  IOException Saving data failed.
     */
    public final void saveQedeq() throws IOException {
        IoUtility.saveFile(file, qedeq.getText());
    }
    

// not used any longer
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

    private final void highlightLine() {
        if (errorPosition.getCause() instanceof ModuleDataException) {
            final int from = getAbsolutePosition(errorPosition.getSourceArea().getStartPosition());
            final int to = getAbsolutePosition(errorPosition.getSourceArea().getEndPosition());
            qedeq.setCaretPosition(0);
            qedeq.getCaret().setSelectionVisible(true);
            qedeq.setSelectedTextColor(Color.RED);
            qedeq.setSelectionColor(Color.YELLOW);
            qedeq.setCaretPosition(to);
            qedeq.moveCaretPosition(from);
            return;
        }
        final SourcePosition startPosition = errorPosition.getSourceArea().getStartPosition();
        final String method = "highlightLine()";
        Trace.param(CLASS, this, method, "position", startPosition);
        int j;
        try {
            j = qedeq.getLineStartOffset(startPosition.getLine() - 1);
        } catch (BadLocationException e) {
            Trace.trace(CLASS, this, method, e);
            j = 0;
        }
        int k;
        try {
            k = qedeq.getLineEndOffset(startPosition.getLine() - 1);
        } catch (BadLocationException e) {
            Trace.trace(CLASS, this, method, e);
            k = qedeq.getText().length();
        }
        Trace.trace(CLASS, this, method, "from " + j + " to " + k);

//// old, but working code:
//        final String s = qedeq.getText();
//        int j = i;
//        for (; j > 0 && s.charAt(j) != '\n'; j--) {
//        }
//        int k = i;
//        for (; k < s.length() && s.charAt(k) != '\n'; k++) {
//        }
//
        qedeq.setCaretPosition(0);
        qedeq.getCaret().setSelectionVisible(true);
        qedeq.setSelectedTextColor(Color.RED);
        qedeq.setSelectionColor(Color.YELLOW);
        qedeq.setCaretPosition(j);
        qedeq.moveCaretPosition(k);
        if (startPosition.getColumn() > 1) {
            qedeq.moveCaretPosition(j + startPosition.getColumn() - 1);
        }
    }


    /**
     * Get absolute position of given relative position.
     * 
     * @param   position    Relative position.
     * @return  Absolute position.
     */
    private int getAbsolutePosition(final SourcePosition position) {
        int j;
        try {
            j = qedeq.getLineStartOffset(position.getLine() - 1) + position.getColumn() - 1;
        } catch (BadLocationException e) {
            Trace.trace(CLASS, this, "getAbsolutePosition(SourcePosition)", e);
            j = 0;
        }
        return j;
    }


}
