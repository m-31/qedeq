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

package org.qedeq.gui.se.main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.qedeq.gui.se.control.QedeqController;
import org.qedeq.gui.se.control.SelectionListenerList;
import org.qedeq.gui.se.tree.QedeqTreeCtrl;
import org.qedeq.gui.se.tree.QedeqTreeModel;
import org.qedeq.gui.se.tree.QedeqTreeView;
import org.qedeq.kernel.bo.log.ModuleEventLog;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.looks.Options;
import com.jgoodies.uif_lite.component.UIFSplitPane;

/**
 * Main pain of QEDEQ GUI window.
 *
 * @author  Michael Meyling
 * @version $Revision: 1.3 $
 */
public class QedeqMainPane extends JPanel {

    /**
     * Constructor.
     *
     * @param   controller  Enables controller access.
     */
    public QedeqMainPane(final QedeqController controller) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(Borders.DIALOG_BORDER);
        add(createSplits(controller));
    }

    /**
     * Creates the two main stripped split panes.
     *
     * @param   controller  Enables controller access.
     * @return  Splits.
     */
    private JComponent createSplits(final QedeqController controller) {

        final QedeqTreeModel treeModel = new QedeqTreeModel();
        ModuleEventLog.getInstance().addLog(treeModel);
        final QedeqTreeView treeView = new QedeqTreeView(treeModel);

        SelectionListenerList listener = new SelectionListenerList();
        final UpperTabbedView tabbedView = new UpperTabbedView(listener);
        listener.addListener(tabbedView);
        final LowerTabbedView lowerView = new LowerTabbedView(listener);

        tabbedView.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);

        final QedeqTreeCtrl treeCtrl = new QedeqTreeCtrl(treeView, treeModel, tabbedView, lowerView,
            controller);
        controller.setTreeCtrl(treeCtrl);

        final JComponent left = new JScrollPane(treeView);
        left.setPreferredSize(new Dimension(200, 200));

        final JComponent right = tabbedView;
        right.setPreferredSize(new Dimension(200, 150));

        final JComponent lower = lowerView;
        lower.setPreferredSize(new Dimension(200, 100));

        final JSplitPane horizontalSplit = UIFSplitPane.createStrippedSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            left,
            right);


        final JSplitPane verticalSplit = UIFSplitPane.createStrippedSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            horizontalSplit,
            lower);

        verticalSplit.setResizeWeight(0.80);
        return verticalSplit;
    }

}

