/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.pane.LogPane;
import org.qedeq.gui.se.pane.ModuleErrorListPane;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.log.ModuleEventLog;
import org.qedeq.kernel.bo.log.QedeqLog;


/**
 * Lower tabbed pane view.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public final class LowerTabbedView extends JPanel {

    /** This class. */
    private static final Class CLASS = LowerTabbedView.class;

    /** Holds all panes. */
    private JTabbedPane tabbedPane = new JTabbedPane();

    /** Global logging events. */
    private LogPane logPane;

    /** Selected module specific errors. */
    private ModuleErrorListPane errorListPane;

    /** Selected module has this properties. */
    private QedeqBo prop;

    /**
     * Constructor.
     */
    public LowerTabbedView() {
        try {
            logPane = new LogPane();
            QedeqLog.getInstance().addLog(logPane);

            errorListPane = new ModuleErrorListPane();
            ModuleEventLog.getInstance().addLog(errorListPane);

            tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);

//            tabbedPane.add(Factory.createStrippedScrollPane(logPane), "Log");
            tabbedPane.add(logPane, "Log");
            tabbedPane.add(errorListPane, "Errors");
        } catch (RuntimeException e) {
            Trace.trace(CLASS, this, "constructor", e);
            throw e;
        }
        add(tabbedPane);
        setLayout(new GridLayout(1, 1));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    public void setQedeqModel(final QedeqBo model) {
        prop = model;
        errorListPane.setModel(model);
    }

    public QedeqBo getQedeqModel() {
        return prop;
    }

}

