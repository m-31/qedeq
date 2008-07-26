/* $Id: LogPane.java,v 1.8 2008/07/26 07:57:44 m31 Exp $
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
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.log.LogListener;

/**
 * Global log. All events are displayed here.
 *
 * @version $Revision: 1.8 $
 * @author  Michael Meyling
 */

public class LogPane extends JPanel implements LogListener {

    /** This class. */
    private static final Class CLASS = LogPane.class;

    /** The log panel. */
    private JTextPane textPane = new JTextPane();

    /** Text attributes for errors. */
    private final SimpleAttributeSet errorAttrs = new SimpleAttributeSet();

    /** Text attributes for success messages. */
    private final SimpleAttributeSet successAttrs = new SimpleAttributeSet();

    /** Text attributes for information messages. */
    private final SimpleAttributeSet messageAttrs = new SimpleAttributeSet();

    /** Text attributes for requests. */
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
        final JScrollPane scroller = new JScrollPane();
        final JViewport vp = scroller.getViewport();
        vp.add(textPane);
        setLayout(new BorderLayout(1, 1));
        add(scroller);

        addComponentListener(new ComponentAdapter() {
            public void componentHidden(final ComponentEvent e) {
                Trace.trace(CLASS, this, "componentHidden", e);
            }
            public void componentShown(final ComponentEvent e) {
                Trace.trace(CLASS, this, "componentHidden", e);
            }
        });

        StyleConstants.setForeground(
            errorAttrs, Color.red);
        StyleConstants.setForeground(
            successAttrs, Color.darkGray);
        StyleConstants.setForeground(
            messageAttrs, Color.black);
        StyleConstants.setForeground(
            requestAttrs, Color.blue);
    }

    public final void logMessageState(final String text, final URL url) {

        final Runnable runLater = new Runnable() {
            public void run() {
                try {
                    textPane.getDocument().insertString(textPane.getDocument().getLength(),
                        text + "\n\t" + url + "\n", messageAttrs);
                } catch (BadLocationException e) {
                    Trace.trace(CLASS, this, "logMessageState", e);
                }
                rework();
            }
        };
        SwingUtilities.invokeLater(runLater);
    }

    public final void logFailureState(final String text, final URL url, final String description) {

        final Runnable runLater = new Runnable() {
            public void run() {
            try {
                textPane.getDocument().insertString(textPane.getDocument().getLength(),
                    text + "\n\t" + url + "\n", errorAttrs);
            } catch (BadLocationException e) {
                Trace.trace(CLASS, this, "logFailureState", e);
            }
            rework();
            }
        };
        SwingUtilities.invokeLater(runLater);
    }

    public final void logSuccessfulState(final String text, final URL url) {

        final Runnable runLater = new Runnable() {
            public void run() {
                try {
                    textPane.getDocument().insertString(textPane.getDocument().getLength(),
                        text + "\n\t" + url + "\n", successAttrs);
                } catch (BadLocationException e) {
                    Trace.trace(CLASS, this, "logSuccessfulState", e);
                }
                rework();
            }
        };
        SwingUtilities.invokeLater(runLater);
    }

    public void logRequest(final String text) {

        final Runnable runLater = new Runnable() {
            public void run() {
                try {
                    textPane.getDocument().insertString(textPane.getDocument().getLength(),
                        text + "\n", requestAttrs);
                } catch (BadLocationException e) {
                    Trace.trace(CLASS, this, "logRequest", e);
                }
                rework();
            }
        };
        SwingUtilities.invokeLater(runLater);
    }

    public void logSuccessfulReply(final String text) {

        final Runnable runLater = new Runnable() {
            public void run() {
                try {
                    textPane.getDocument().insertString(textPane.getDocument().getLength(),
                        text + "\n", successAttrs);
                } catch (BadLocationException e) {
                    Trace.trace(CLASS, this, "logSuccessfulReply", e);
                }
                rework();
            }
        };
        SwingUtilities.invokeLater(runLater);
    }

    public void logFailureReply(final String text, final String description) {

        final Runnable runLater = new Runnable() {
            public void run() {
                try {
                    textPane.getDocument().insertString(textPane.getDocument().getLength(),
                        text +  "\n\t" + description + "\n", errorAttrs);
                } catch (BadLocationException e) {
                    Trace.trace(CLASS, this, "logFailureReply", e);
                }
                rework();
            }
        };
        SwingUtilities.invokeLater(runLater);
    }

    public void logMessage(final String text) {

        final Runnable runLater = new Runnable() {
            public void run() {
                try {
                    textPane.getDocument().insertString(textPane.getDocument().getLength(),
                        text + "\n", messageAttrs);
                } catch (BadLocationException e) {
                    Trace.trace(CLASS, this, "logFailureReply", e);
                }
                rework();
            }
        };
        SwingUtilities.invokeLater(runLater);
    }

    private void rework() {
        if (QedeqGuiConfig.getInstance().isAutomaticLogScroll()) {
            textPane.setCaretPosition(textPane.getDocument().getLength());
        }
    }

}
