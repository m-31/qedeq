/* $Id: UpperTabbedView.java,v 1.4 2008/03/27 05:14:04 m31 Exp $
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

package org.qedeq.gui.se.main;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.qedeq.gui.se.control.ErrorSelectionListener;
import org.qedeq.gui.se.control.ErrorSelectionListenerList;
import org.qedeq.gui.se.pane.HtmlPane;
import org.qedeq.gui.se.pane.ModulePropertiesPane;
import org.qedeq.gui.se.pane.QedeqPane;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.trace.Trace;


/**
 * Upper tabbed pane view.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class UpperTabbedView extends JPanel implements ErrorSelectionListener {

    /** This class. */
    private static final Class CLASS = UpperTabbedView.class;

    /** Holds all tabs. */
    private JTabbedPane tabbedPane = new JTabbedPane();

    /** Source view of QEDEQ module. */
    private QedeqPane qedeqPane;

    /** HTML display of QEDEQ module. */
    private HtmlPane htmlPane;

    /** Currently active module properties. */
    private QedeqBo prop;

    /** Show properties and status of QEDEQ module. */
    private ModulePropertiesPane propertiesPane;

    /** Flag for showing the HTML pane. */
    private boolean viewHtml = false;


    /**
     * Constructor.
     */
    public UpperTabbedView() {
        try {
            propertiesPane = new ModulePropertiesPane(prop);
            qedeqPane = new QedeqPane(prop);
            htmlPane = new HtmlPane(prop);
            tabbedPane.addTab("State", null, propertiesPane, "Shows Status of Module");
            tabbedPane.addTab("QEDEQ", null, qedeqPane, "Shows Module in QEDEQ Syntax");
/*
            if (viewHtml) {
                addTab("Html", icon,  Factory.createStrippedScrollPane(htmlPane),
                    "Shows Module in Html-View");
            }
*/
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, "constructor", e);
            throw e;
        }
//        setSelectedIndex(0);

        // Add the tabbed pane to this panel.
        add(tabbedPane);
        setLayout(new GridLayout(1, 1));
        ErrorSelectionListenerList.getInstance().addListener(this);
    }

    public final void setHtmlView(final boolean enable) {
        viewHtml = enable;
        if (enable) {
            tabbedPane.addTab("Html", null,  htmlPane, "Shows Module in Html-View");
            htmlPane.updateView();
        } else {
            tabbedPane.remove(htmlPane);
        }
    }

    public boolean getHtmlView() {
        return viewHtml;
    }

    public void setQedeqModel(final QedeqBo model) {
        propertiesPane.setModel(model);
        qedeqPane.setModel(model);
        htmlPane.setModel(model);
        if (prop != model) {
            prop = model;
            updateView();
        }
    }

    public QedeqBo getQedeqModel() {
        return prop;
    }


    public final void updateView() {
        propertiesPane.updateView();
        qedeqPane.updateView();
        if (viewHtml) {
            htmlPane.updateView();
        }
    }


    public void setLineWrap(final boolean wrap) {
        qedeqPane.setLineWrap(wrap);
    }

    public boolean getLineWrap() {
        return qedeqPane.getLineWrap();
    }

    /**
     * Get edited QEDEQ text, if text is editable.
     *
     * @return  QEDEQ text.
     * @throws  IllegalStateException   QEDEQ text is not editable.
     */
    public String getEditedQedeq() {
        return qedeqPane.getEditedQedeq();
    }

    public void selectError(final int errorNumber, final SourceFileException sf) {
        tabbedPane.setSelectedComponent(qedeqPane);
    }

}
