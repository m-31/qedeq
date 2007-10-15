/* $Id: LogPane.java,v 1.3 2007/10/07 16:39:59 m31 Exp $
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.qedeq.kernel.log.LogListener;
import org.qedeq.kernel.trace.Trace;

/**
 * Global log. All events are displayed here.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */

public class LogPane extends JPanel implements LogListener {

    private JTextPane textPane = new JTextPane();

    private final SimpleAttributeSet errorAttrs = new SimpleAttributeSet();

    private final SimpleAttributeSet successAttrs = new SimpleAttributeSet();

    private final SimpleAttributeSet messageAttrs = new SimpleAttributeSet();

    private final SimpleAttributeSet requestAttrs = new SimpleAttributeSet();


    /**
     * Creates new panel.
     */
    public LogPane() {
        super(false);
        setupView();
        StyleContext sc = new StyleContext();
        DefaultStyledDocument doc = new DefaultStyledDocument(sc);
        textPane.setDocument(doc);
    }



    /**
     * Assembles the GUI components of the panel.
     */
    private final void setupView() {
        textPane.setDragEnabled(true);
        textPane.setAutoscrolls(true);
        textPane.setCaretPosition(0);
        textPane.setEditable(false);
        textPane.getCaret().setVisible(false);
//      final DefaultCaret caret = (DefaultCaret) text.getCaret();
//      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);  // LATER mime JRE 1.5
        textPane.setFocusable(true);
        textPane.setFocusable(true);
        final JScrollPane scroller = new JScrollPane();
        final JViewport vp = scroller.getViewport();
        vp.add(textPane);
        this.setLayout(new BorderLayout(1, 1));
        this.add(scroller);

        this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(final ComponentEvent e) {
                Trace.trace(this, "componentHidden", e);
            }
            public void componentShown(final ComponentEvent e) {
                Trace.trace(this, "componentHidden", e);
            }
        });

        StyleConstants.setForeground(
            this.errorAttrs, Color.red);
        StyleConstants.setForeground(
            this.successAttrs, Color.darkGray);
        StyleConstants.setForeground(
            this.messageAttrs, Color.black);
        StyleConstants.setForeground(
            this.requestAttrs, Color.blue);
    }

    public final void logMessageState(final String text, final URL url) {
        try {
            this.textPane.getDocument().insertString(this.textPane.getDocument().getLength(),
                text + "\n\t" + url + "\n", this.messageAttrs);
        } catch (BadLocationException e) {
            Trace.trace(this, "logMessageState", e);
        }
        rework();
    }

    public final void logFailureState(final String text, final URL url, final String description) {
        try {
            this.textPane.getDocument().insertString(this.textPane.getDocument().getLength(),
                text + "\n\t" + url + "\n", this.errorAttrs);
        } catch (BadLocationException e) {
            Trace.trace(this, "logFailureState", e);
        }
        rework();
    }

    public final void logSuccessfulState(final String text, final URL url) {
        try {
            this.textPane.getDocument().insertString(this.textPane.getDocument().getLength(),
                text + "\n\t" + url + "\n", this.successAttrs);
        } catch (BadLocationException e) {
            Trace.trace(this, "logSuccessfulState", e);
        }
        rework();
    }

    public void logRequest(final String text) {
        try {
            this.textPane.getDocument().insertString(this.textPane.getDocument().getLength(),
                text + "\n", this.requestAttrs);
        } catch (BadLocationException e) {
            Trace.trace(this, "logRequest", e);
        }
        rework();
    }

    public void logSuccessfulReply(final String text) {
        try {
            this.textPane.getDocument().insertString(this.textPane.getDocument().getLength(),
                text + "\n", this.successAttrs);
        } catch (BadLocationException e) {
            Trace.trace(this, "logSuccessfulReply", e);
        }
        rework();
    }

    public void logFailureReply(final String text, final String description) {
        try {
            this.textPane.getDocument().insertString(this.textPane.getDocument().getLength(),
                text + "\n", this.errorAttrs);
        } catch (BadLocationException e) {
            Trace.trace(this, "logFailureReply", e);
        }
        rework();
    }

    public void logMessage(final String text) {
        try {
            this.textPane.getDocument().insertString(this.textPane.getDocument().getLength(),
                text + "\n", this.messageAttrs);
        } catch (BadLocationException e) {
            Trace.trace(this, "logFailureReply", e);
        }
        rework();
    }

    private void rework() {
        if (QedeqGuiConfig.getInstance().isAutomaticLogScroll()) {
            textPane.setCaretPosition(textPane.getDocument().getLength());
        }
    }

}
