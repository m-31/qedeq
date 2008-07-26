/* $Id: HtmlPane.java,v 1.6 2008/07/26 07:57:44 m31 Exp $
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
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.DebugGraphics;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.ModuleAddress;

/**
 * HTML view for module.
 *
 * TODO mime 20070606: not implemented yet
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */

public class HtmlPane extends JPanel {

    /** This class. */
    private static final Class CLASS = HtmlPane.class;

    /** Module properties for selected module. */
    private QedeqBo prop;

//    private JEditorPane html = new JEditorPane();
// TODO test example:
    /** The HTML pane. */
    private JEditorPane html = new JEditorPane("text/html", "\u1200");

    /** View blocked? */
    private boolean blocked = false;

    /** Current module address. */
    private ModuleAddress currentAddress = null;

    /** Current loading state. */
    private LoadingState currentState;


    /**
     * Creates new Panel.
     */
    public HtmlPane(final QedeqBo prop) {
        super(false);
        this.prop = prop;
        this.currentAddress = prop == null ? null : prop.getModuleAddress();
        this.currentState = prop == null ? null : prop.getLoadingState();
        setupView();
        updateView();
    }


    /**
     * Assembles the gui components of the panel.
     */
    private final void setupView() {
        html.setDragEnabled(true);
        html.setAutoscrolls(true);
        html.setCaretPosition(0);
        html.setEditable(false);
        html.getCaret().setVisible(false);
        html.setFocusable(true);
        html.setDebugGraphicsOptions(DebugGraphics.LOG_OPTION);
// TODO work out:
//        html.addHyperlinkListener(createHyperLinkListener());
        final JScrollPane scroller = new JScrollPane();
        final JViewport vp = scroller.getViewport();
        vp.add(html);
        this.setLayout(new BorderLayout(1, 1));
        this.add(scroller);

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
    public void setModel(final QedeqBo prop) {
        Trace.trace(CLASS, this, "setModel", prop);
        this.prop = prop;
    }


    public QedeqBo getModel() {
        return this.prop;
    }



    /**
     * Update from model.
     */
    public synchronized void updateView() {
        Trace.begin(CLASS, this, "updateView");
        boolean refresh = false;
        if (prop == null && this.currentAddress != null) {
            refresh = true;
        }
        if (prop != null && !prop.getModuleAddress().equals(this.currentAddress)) {
            this.currentAddress = prop.getModuleAddress();
            refresh = true;
        }
        if (prop != null && this.currentState != prop.getLoadingState()) {
            this.currentState = prop.getLoadingState();
            refresh = true;
        }
        if (refresh && this.prop != null && (this.prop.getLoadingState()
                == LoadingState.STATE_LOADED) && !this.blocked) {
            this.blocked = true;
            try {
//                final Module2JHtml converter = new Module2JHtml(prop.getModuleAddress());
//                converter.writeModule();
                html.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
                html.setText("<html><body>"                    + "\u2227 Hallo \u2227 Werter \u2227 \u2227 \u2227"
                    + "<br>"                    + "<table>"
                    + "<tr><td>Hei</td><td>ho</td></tr>"                    + "<tr><td>Ostern</td><td>weihnacht</td></tr>"
                    + "</table>"                    + "</body></html>");
//                html.setPage(converter.getGeneratedUrl());
//                html.setText(converter.getText());

            } catch (Exception e) {
                Trace.trace(CLASS, this, "updateView", e);
                html.setText("");
            }
            this.blocked = false;
        } else {
            html.setText("");
        }
        this.repaint();
        Trace.end(CLASS, this, "updateView");
    }



    public HyperlinkListener createHyperLinkListener() {
        return new HyperlinkListener() {
            public void hyperlinkUpdate(final HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (e instanceof HTMLFrameHyperlinkEvent) {
                        ((HTMLDocument) html.getDocument()).processHTMLFrameHyperlinkEvent(
                            (HTMLFrameHyperlinkEvent) e);
                    } else {
                        try {
                            html.setPage(e.getURL());
                        } catch (IOException ioe) {
                            System.out.println("IOE: " + ioe);
                        }
                    }
                }
            }
        };
    }


}
