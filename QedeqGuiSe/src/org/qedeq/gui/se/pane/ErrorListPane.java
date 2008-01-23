/* $Id: ErrorListPane.java,v 1.4 2007/12/21 23:34:47 m31 Exp $
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
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.ModuleEventListener;
import org.qedeq.kernel.trace.Trace;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Shows QEDEQ module specific error pane.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */

public class ErrorListPane extends JPanel implements ModuleEventListener {

    /** This class. */
    private static final Class CLASS = ErrorListPane.class;

    /** This text field holds the error descriptions. */
    private JTextPane error = new JTextPane() {

        /**
         *  Because text shoudn't be wrapped we don't was to track the viewport width.
         **/
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

    };

    /** Write with this font attributes. */
    private final SimpleAttributeSet errorAttrs = new SimpleAttributeSet();

    /** For this module properties the errors are shown. */
    private ModuleProperties prop;

    /**
     * Creates new panel.
     */
    public ErrorListPane(final ModuleProperties prop) {
        super(false);
        setModel(prop);
        setupView();

        StyleContext sc = new StyleContext();
        DefaultStyledDocument doc = new DefaultStyledDocument(sc);
        error.setDocument(doc);

    }

    /**
     * Assembles the GUI components of the panel.
     */
    private final void setupView() {
        FormLayout layout = new FormLayout(
            "min:grow",
            "0:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
//        builder.setDefaultDialogBorder();
        builder.setBorder(BorderFactory.createEmptyBorder());
        builder.setRowGroupingEnabled(true);

        CellConstraints cc = new CellConstraints();
        builder.appendRow(new RowSpec("0:grow"));

        error.setEditable(false);
        error.setEnabled(true);
        error.getCaret().setVisible(false);
        error.setFont(new Font("monospaced", Font.PLAIN, 12));
        error.setDragEnabled(true);
        error.setCaretPosition(0);
        error.setFocusable(true);
        error.putClientProperty("JTextArea.infoBackground", Boolean.TRUE);

        final JScrollPane scrollPane = new JScrollPane(error);
        builder.add(scrollPane,
            cc.xywh(builder.getColumn(), builder.getRow(), 1, 2, "fill, fill"));

        StyleConstants.setForeground(this.errorAttrs, Color.red);
        // because we override getScrollableTracksViewportWidth:
        scrollPane.getViewport().setBackground(Color.white);
        StyleConstants.setBackground(this.errorAttrs, Color.white);  //FIXME

        this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(final ComponentEvent e) {
                Trace.trace(CLASS, this, "componentHidden", e);
            }
            public void componentShown(final ComponentEvent e) {
                Trace.trace(CLASS, this, "componentHidden", e);
            }
        });


    }

    /**
     * Set new model. To make the new model visible {@link #updateView} must be called.
     *
     * @param   prop
     */
    public void setModel(final ModuleProperties prop) {
        Trace.trace(CLASS, this, "setModel", prop);
        this.prop = prop;
    }


    public ModuleProperties getModel() {
        return this.prop;
    }

    /**
     * Update from model.
     */
    public synchronized void updateView() {
        final String method = "updateView";
        Trace.begin(CLASS, this, method);
        try {
            error.getDocument().remove(0, error.getDocument().getLength());
            if (prop != null && prop.getException() != null) {
                SourceFileExceptionList list = prop.getException();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getSourceArea() != null) {
                        final URL url = list.get(i).getSourceArea().getAddress();
                        final ModuleAddress address;
                        try {
                            address = KernelContext.getInstance().getModuleAddress(url);
                            final File local = KernelContext.getInstance()
                                .getLocalFilePath(address);
                            error.getDocument().insertString(error.getDocument().getLength(),
                                list.get(i).getDescription(local),
                                errorAttrs);
                        } catch (IOException e) {
                            Trace.fatal(CLASS, this, method, "unexpected problem", e);
                            error.getDocument().insertString(error.getDocument().getLength(),
                                list.get(i).getMessage(),
                                errorAttrs);
                        }
                    } else {
                        error.getDocument().insertString(error.getDocument().getLength(),
                            list.get(i).getMessage(),
                            errorAttrs);
                    }
                    error.getDocument().insertString(error.getDocument().getLength(),
                        "\n", errorAttrs);
                }
            }
        } catch (BadLocationException e) {
            Trace.trace(CLASS, this, "updateView", e);
        }
        this.repaint();
    }

    public void addModule(final ModuleProperties prop) {
        // TODO mime 20070829: what identifies a ModuleProperties, the moduleAddress? why not
        // use equals?
        if (this.prop != null && prop.getUrl().equals(this.prop.getUrl())) {
            updateView();
        }
    }

    public void stateChanged(final ModuleProperties prop) {
        if (this.prop != null && prop.getUrl().equals(this.prop.getUrl())) {
            updateView();
        }
    }

    public void removeModule(final ModuleProperties prop) {
        this.prop = null;
        updateView();
    }


}
