/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.gui.se.tree;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.control.QedeqController;
import org.qedeq.gui.se.main.LowerTabbedView;
import org.qedeq.gui.se.main.UpperTabbedView;
import org.qedeq.kernel.bo.common.QedeqBo;


/**
 * Controller for a certain JTree.
 *
 * A Controller, which represents the classes connecting the model and the view, and is used to
 * communicate between classes in the model and view.
 *
 * TODO mime 20080126: rather work with listeners than referencing other views
 *
 * @author  Michael Meyling
 */
public final class QedeqTreeCtrl implements TreeModelListener {

    /** This class. */
    private static final Class CLASS = QedeqTreeCtrl.class;

    /** Tree view. */
    private final QedeqTreeView treeView;

    /** Tree model. */
    private final QedeqTreeModel treeModel;

    /** Context menu. */
    private final QedeqTreeContextMenu contextMenu;

    /** Reference to view. */
    private final UpperTabbedView pane;

    /** Reference to view. */
    private final LowerTabbedView lower;


    /**
     * Tree controller.
     *
     * @param   treeView    View.
     * @param   treeModel   Model.
     * @param   pane        Dependent view.
     * @param   lowerView   Dependent view.
     * @param   controller  Main controller.
     */
    public QedeqTreeCtrl(final QedeqTreeView treeView, final QedeqTreeModel treeModel,
            final UpperTabbedView pane, final LowerTabbedView lowerView,
            final QedeqController controller) {

        this.treeView = treeView;
        this.treeModel = treeModel;
        this.treeModel.addTreeModelListener(this);
        this.contextMenu = new QedeqTreeContextMenu(controller);
        this.treeView.treeAddMouseListener(new QedeqMouseListener());
        this.treeView.addTreeSelectionListener(new SelectionChangedCommand());
        // LATER mime 20071024: inform others per listener about this event
        this.pane = pane;
        this.lower = lowerView;
    }

    /**
     * Get all selected QedeqBos.
     *
     * @return  Selected QedeqBos.
     * @throws  NothingSelectedException    Nothing was selected.
     */
    public final QedeqBo[] getSelected() throws NothingSelectedException {
        final String method = "getSelected";
        Trace.begin(CLASS, this, method);
        try {
            TreePath[] selected = treeView.getSelectionPaths();
            final List list = new ArrayList();
            if (selected != null && selected.length > 0) {
                Trace.trace(CLASS, this, "actionPerformed",
                    "selection=" + selected[selected.length - 1]);
                for (int i = 0; i < selected.length; i++) {
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                             (selected[i].getLastPathComponent());
                    if (node instanceof QedeqTreeNode) {
                        final QedeqBo prop = (QedeqBo) node.getUserObject();
                        list.add(prop);
                    }
                }
            }
            if (list.size() <= 0) {
                throw new NothingSelectedException();
            }
            return (QedeqBo[]) list.toArray(new QedeqBo[0]);
        } catch (RuntimeException ex) {
            Trace.trace(CLASS, this, method, ex);
            throw new NothingSelectedException();
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Get edited QEDEQ text.
     *
     * @return  QEDEQ text
     * @throws  IllegalStateException   text is not editable
     */
    public final String getEditedQedeq() {
        return this.pane.getEditedQedeq();
    }

    public void treeNodesChanged(final TreeModelEvent e) {
        Trace.param(CLASS, this, "treeNodesChanged", "event", e);
        final Runnable runLater = new Runnable() {
            public void run() {
                try {
                    pane.updateView();
                } catch (RuntimeException ex) {
                    Trace.fatal(CLASS, this, "treeNodesChanged", "unexpected problem", ex);
                }
            }
        };
        SwingUtilities.invokeLater(runLater);
        Trace.end(CLASS, this, "treeNodesChanged");
    }

    public void treeNodesInserted(final TreeModelEvent e) {
        Trace.begin(CLASS, this, "treeNodesInserted");
        // 20080501 mime: solution of an old problem:
        // because the root is invisible its children are invisible too. to make them visible
        // we must expand the the path of the root node. but this is possible only if there is
        // already a child node there. so if the first node is added we must expand the
        // path of root. when the model sends us this event we can not simply expand the path
        // instantly because if we do so, the new node is added twice! This is done for example for
        // VariableHeightLayoutCache.visibleNodes in the two methods
        // setExpandedState(TreePath path, boolean isExpanded) (called by expandPath) and in
        // (treeNodesInserted(TreeModelEvent e) (called by DefaultTreeModel.nodesWereInserted(
        // TreeNode node, int[] childIndices)
        // To solve this dilemma we simply generate a new event that is handled later on
        final Runnable runLater = new Runnable() {
            public void run() {
                try {
                    if (((DefaultMutableTreeNode) treeModel.getRoot()).getChildCount() > 0) {
                        Trace.trace(CLASS, this, "treeNodesInserted", "expandPath");
                        treeView.expandPath(new TreePath(treeModel.getRoot()));
                    }
                } catch (RuntimeException ex) {
                    Trace.fatal(CLASS, this, "treeNodesInserted", "unexpected problem", ex);
                }
            }
        };
        SwingUtilities.invokeLater(runLater);
        Trace.end(CLASS, this, "treeNodesInserted");
    }


    public void treeNodesRemoved(final TreeModelEvent e) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            (e.getTreePath().getLastPathComponent());
        Trace.trace(CLASS, this, "treeNodeRemoved", node.getUserObject());
    }


    public void treeStructureChanged(final TreeModelEvent e) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            (e.getTreePath().getLastPathComponent());
        Trace.trace(CLASS, this, "treeStructureChanged", node.getUserObject());
    }


    /**
     * Changes node view, depending from chosen object.
     */
    private class SelectionChangedCommand implements TreeSelectionListener {

        public SelectionChangedCommand()  {
        }

        public void valueChanged(final TreeSelectionEvent event) {
            // TODO mime 20071024: inform others per listener about this event
            Trace.trace(CLASS, this, "valueChanged", event);
            TreePath path = event.getPath();
            QedeqTreeNode treeNode = (QedeqTreeNode) path.getLastPathComponent();
            if (event.isAddedPath()
                    && treeNode.getUserObject() instanceof QedeqBo) {
                QedeqBo prop = (QedeqBo) treeNode.getUserObject();
                pane.setQedeqModel(prop);
                lower.setQedeqModel(prop);
            } else {
                pane.setQedeqModel(null);
                lower.setQedeqModel(null);
            }
        }
    }

    /**
     * Handle mouse events.
     */
    private class QedeqMouseListener extends MouseAdapter {

        public void mousePressed(final java.awt.event.MouseEvent evt) {

            if (SwingUtilities.isRightMouseButton(evt)) {
                try {
                    getSelected();
                } catch (NothingSelectedException e) {
                    final TreePath path =  treeView.getPathForLocation(evt.getX(), evt.getY());
                    if (path != null) {
                        treeView.setSelectionPath(path);
                    } // TODO mime 20080126: other ContextMenu if no selection was done
                }
                contextMenu.show(evt.getComponent(),
                    evt.getX(), evt.getY());
            }
//            super.mousePressed(evt);
        }

    }

}
