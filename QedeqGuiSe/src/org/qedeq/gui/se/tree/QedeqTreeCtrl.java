/* $Id: QedeqTreeCtrl.java,v 1.3 2007/10/07 16:39:59 m31 Exp $
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

package org.qedeq.gui.se.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.qedeq.gui.se.control.QedeqController;
import org.qedeq.gui.se.main.LowerTabbedView;
import org.qedeq.gui.se.main.UpperTabbedView;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.trace.Trace;


/**
 * Controller for a certain JTree.
 *
 * A Controller, which represents the classes connecting the model and the view, and is used to
 * communicate between classes in the model and view.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public final class QedeqTreeCtrl implements TreeModelListener {

    private final QedeqTreeView treeView;

    /** Tree model. */
    private final QedeqTreeModel treeModel;

    private final UpperTabbedView pane;

    private final LowerTabbedView  lower;

    private final ActionListener removeAction;


    public QedeqTreeCtrl(final QedeqTreeView treeView, final QedeqTreeModel treeModel,
            final UpperTabbedView pane, final LowerTabbedView lowerView,
            final QedeqController controller) {

        this.treeView = treeView;
        this.treeModel = treeModel;
        this.treeModel.addTreeModelListener(this);
        this.treeView.addActionCommandToContextMenus(new QedeqActionCommand());
        this.treeView.treeAddMouseListener(new QedeqMouseListener());
        this.treeView.addTreeSelectionListener(new SelectionChangedCommand());
        this.removeAction = new RemoveAction();
        // TODO mime 20071024: inform others per listener about this event
        this.pane = pane;
        this.lower = lowerView;
    }


    public final ActionListener getRemoveAction() {
        return this.removeAction;
    }

    public final ModuleProperties[] getSelected() throws NothingSelectedException {
        final String method = "getSelected";
        Trace.begin(this, method);
        try {
            TreePath[] selected = treeView.getSelectionPaths();
            final List list = new ArrayList();
            if (selected != null && selected.length > 0) {
                Trace.trace(this, "actionPerformed", "selection=" + selected[selected.length - 1]);
                for (int i = 0; i < selected.length; i++) {
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                             (selected[i].getLastPathComponent());
                    if (node instanceof QedeqTreeNode) {
                        final ModuleProperties prop = (ModuleProperties) node.getUserObject();
                        list.add(prop);
                    }
                }
            }
            if (list.size() <= 0) {
                throw new NothingSelectedException();
            }
            return (ModuleProperties[]) list.toArray(new ModuleProperties[0]);
        } catch (RuntimeException ex) {
            Trace.trace(this, method, ex);
            throw new NothingSelectedException();
        } finally {
            Trace.end(this, method);
        }
    }

    public final void setSelected(final ModuleProperties prop) {
        final String method = "setSelected";
        Trace.begin(this, method);
        try {
            treeView.setSelectionPath(treeModel.getSelectionPath(prop));
            treeView.setSelectionRow(2);
        } catch (RuntimeException ex) {
            Trace.trace(this, method, ex);
        } finally {
            Trace.end(this, method);
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


    /**
     * Changes node view, depending from chosen object.
     */
    private class SelectionChangedCommand implements TreeSelectionListener {

        public SelectionChangedCommand()  {
        }

        public void valueChanged(final TreeSelectionEvent event) {
            // TODO mime 20071024: inform others per listener about this event
            Trace.trace(this, "valueChanged", event);
            TreePath path = event.getPath();
            QedeqTreeNode treeNode = (QedeqTreeNode) path.getLastPathComponent();
            String pathStr = treeNode.toString();
            if (event.isAddedPath()
                    && treeNode.getUserObject() instanceof ModuleProperties) {
                ModuleProperties prop = (ModuleProperties) treeNode.getUserObject();
                pane.setQedeqModel(prop);
                lower.setQedeqModel(prop);
            } else {
                pane.setQedeqModel(null);
                lower.setQedeqModel(null);
            }
        }
    }


    private class QedeqActionCommand implements ActionListener {

        public void actionPerformed(final ActionEvent event) {
            final String method = "actionPerformed";
            Trace.param(this, method, "action", event);

            if (event.getActionCommand() == QedeqTreeView.DELETE_ACTION)  {     // delete
                getRemoveAction().actionPerformed(event);
/*
            } else if (event.getActionCommand() == QedeqTreeView.REFRESH_ACTION)  {    // refresh
                // TODO
            } else if (event.getActionCommand() == PmiiTreeView.ADD_ACTION) {
                getAddAction().actionPerformed(event);
                // TODO
            } else if (event.getActionCommand() == PmiiTreeView.HTML_ACTION) {
                getHtmlAction().actionPerformed(event);
                // TODO
*/
            }
        }
    }

    /**
     * RemoveAction removes the selected node from the tree.  If
     * The root or nothing is selected nothing is removed.
     */
    class RemoveAction extends Object implements ActionListener {

        /**
         * Removes the selected item as long as it isn't root.
         */
       public void actionPerformed(final ActionEvent e) {
           try {
               ModuleProperties[] moduleProperties = getSelected();
               for (int i = 0; i < moduleProperties.length; i++) {
                   KernelContext.getInstance().removeModule(moduleProperties[i].getAddress());
               }
        } catch (NothingSelectedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException io) {
            // TODO Auto-generated catch block
            io.printStackTrace();
        }
       }

    }



    /**
     * RemoveAction removes the selected node from the tree.  If
     * The root or nothing is selected nothing is removed.
     */
    class RemoveAction2 extends Object implements ActionListener {

        /**
          * Removes the selected item as long as it isn't root.
          */
        public void actionPerformed(final ActionEvent e) {
            TreePath[] selected = treeView.getSelectionPaths();

            if (selected != null && selected.length > 0) {
                TreePath shallowest;

                // The remove process consists of the following steps:
                // 1 - find the shallowest selected TreePath, the shallowest
                //     path is the path with the smallest number of path
                //     components.
                // 2 - Find the siblings of this TreePath
                // 3 - Remove from selected the TreePaths that are descendants
                //     of the paths that are going to be removed. They will
                //     be removed as a result of their ancestors being
                //     removed.
                // 4 - continue until selected contains only null paths.
                while ((shallowest = findShallowestPath(selected)) != null) {
                    removeSiblings(shallowest, selected);
                }
            }
        }

        /**
         * Removes the sibling TreePaths of <code>path</code>, that are
         * located in <code>paths</code>.
         */
        private void removeSiblings(final TreePath path, final TreePath[] paths) {
            // Find the siblings
            if (path.getPathCount() == 1) {
                // Special case, set the root to null
                for (int counter = paths.length - 1; counter >= 0; counter--) {
                    paths[counter] = null;
                }
                treeModel.setRoot(null);
            } else {
                // Find the siblings of path.
                final TreePath parent = path.getParentPath();
                MutableTreeNode parentNode = (MutableTreeNode) parent.
                                getLastPathComponent();
                ArrayList toRemove = new ArrayList();
                int depth = parent.getPathCount();

                // First pass, find paths with a parent TreePath of parent
                for (int counter = paths.length - 1; counter >= 0; counter--) {
                    if (paths[counter] != null && paths[counter].
                              getParentPath().equals(parent)) {
                        toRemove.add(paths[counter]);
                        paths[counter] = null;
                    }
                }

                // Second pass, remove any paths that are descendants of the
                // paths that are going to be removed. These paths are
                // implicitly removed as a result of removing the paths in
                // toRemove
                int rCount = toRemove.size();
                for (int counter = paths.length - 1; counter >= 0; counter--) {
                    if (paths[counter] != null) {
                        for (int rCounter = rCount - 1; rCounter >= 0;
                             rCounter--) {
                            if (((TreePath) toRemove.get(rCounter)).
                                           isDescendant(paths[counter])) {
                                paths[counter] = null;
                            }
                        }
                    }
                }

                // Sort the siblings based on position in the model
                if (rCount > 1) {
                    Collections.sort(toRemove, (Comparator) new PositionComparator());
                }
                int[] indices = new int[rCount];
                Object[] removedNodes = new Object[rCount];
                for (int counter = rCount - 1; counter >= 0; counter--) {
                    removedNodes[counter] = ((TreePath) toRemove.get(counter))
                        .getLastPathComponent();
                    indices[counter] = treeModel.getIndexOfChild(
                        parentNode, removedNodes[counter]);
                    parentNode.remove(indices[counter]);
                }
                treeModel.nodesWereRemoved(parentNode, indices, removedNodes);
            }
        }


        /**
         * Returns the TreePath with the smallest path count in
         * <code>paths</code>. Will return null if there is no non-null
         * TreePath is <code>paths</code>.
         */
        private TreePath findShallowestPath(final TreePath[] paths) {
            int shallowest = -1;
            TreePath shallowestPath = null;

            for (int counter = paths.length - 1; counter >= 0; counter--) {
                if (paths[counter] != null) {
                    if (shallowest != -1) {
                        if (paths[counter].getPathCount() < shallowest) {
                            shallowest = paths[counter].getPathCount();
                            shallowestPath = paths[counter];
                            if (shallowest == 1) {
                                return shallowestPath;
                            }
                        }
                    } else {
                        shallowestPath = paths[counter];
                        shallowest = paths[counter].getPathCount();
                    }
                }
            }
            return shallowestPath;
        }


        /**
         * An Comparator that bases the return value on the index of the
         * passed in objects in the TreeModel.
         * <p>
         * This is actually rather expensive, it would be more efficient
         * to extract the indices and then do the comparision.
         */
        private class PositionComparator implements Comparator {

            public int compare(final Object o1, final Object o2) {
                final TreePath p1 = (TreePath) o1;
                int o1Index = treeModel.getIndexOfChild(p1.getParentPath().
                          getLastPathComponent(), p1.getLastPathComponent());
                TreePath p2 = (TreePath) o2;
                int o2Index = treeModel.getIndexOfChild(p2.getParentPath().
                          getLastPathComponent(), p2.getLastPathComponent());
                return o1Index - o2Index;
            }

        }

    }



    private class QedeqMouseListener extends MouseAdapter {

        public void mousePressed(final java.awt.event.MouseEvent evt) {

            final TreePath path =  treeView.getPathForLocation(evt.getX(), evt.getY());

            if (SwingUtilities.isRightMouseButton(evt)) {
                if (path != null) {
                    treeView.setSelectionPath(path);
                } // TODO other ContextMenu if no selection was done
                treeView.getContextMenu().show(evt.getComponent(),
                    evt.getX(), evt.getY());
            }
//            super.mousePressed(evt);
        }

    }

    public void treeNodesChanged(final TreeModelEvent e) {
        DefaultMutableTreeNode node;
        node = (DefaultMutableTreeNode)
                 (e.getTreePath().getLastPathComponent());

        /*
         * If the event lists children, then the changed
         * node is the child of the node we've already
         * gotten.  Otherwise, the changed node and the
         * specified node are the same.
         */

        try {
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode) node.getChildAt(index);
        } catch (NullPointerException exc) {
        }

        Trace.trace(this, "treeNodesChanged", node.getUserObject());

        TreePath path = e.getTreePath();
        QedeqTreeNode treeNode = (QedeqTreeNode) path.getLastPathComponent();

        pane.updateView();      // TODO
/*
        if (treeNode.getUserObject() instanceof ModuleProperties) {
            ModuleProperties prop = (ModuleProperties) treeNode.getUserObject();
    //        pane.setModel(prop);
            pane.updateView();      // TODO
    //        if (pane.getModel() == prop) {
    //            pane.updateView();
    //        }
        } else {
//            pane.setModel(null);
        }
*/

    }

    public void treeNodesInserted(final TreeModelEvent e) {
        Trace.begin(this, "treeNodesInserted");
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            (e.getTreePath().getLastPathComponent());
        if (((DefaultMutableTreeNode) treeModel.getRoot()).getChildCount() == 1) {
            treeView.expandPath(new TreePath(treeModel.getRoot()));
        }
        Trace.end(this, "treeNodesInserted");
    }


    public void treeNodesRemoved(final TreeModelEvent e) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            (e.getTreePath().getLastPathComponent());
        Trace.trace(this, "treeNodeRemoved", node.getUserObject());
    }


    public void treeStructureChanged(final TreeModelEvent e) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            (e.getTreePath().getLastPathComponent());
        Trace.trace(this, "treeStructureChanged", node.getUserObject());
    }


}

