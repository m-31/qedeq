/* $Id: QedeqTreeModel.java,v 1.4 2008/01/26 12:38:27 m31 Exp $
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


import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.log.ModuleEventListener;
import org.qedeq.kernel.trace.Trace;


/**
 * Model for a JTree.
 *
 * A Model, which represents the underlying, logical structure of data in a
 * software application and the high-level class associated with it. The
 * object model does not contain any information about the user interface.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class QedeqTreeModel extends DefaultTreeModel implements ModuleEventListener {

    /** This class. */
    private static final Class CLASS = QedeqTreeModel.class;

    /**
     * Maps {@link org.qedeq.kernel.bo.module.ModuleAddress}es to {@link TreeNode}s..
     */
    private final Map module2Path = new HashMap();

    public QedeqTreeModel(final TreeNode root) {
        super(root);
    }

    /**
     * Creates tree model.
     */
    public QedeqTreeModel() {
        this(new QedeqTreeNode(new ModuleElement(), true));
    }

    /*  (non-Javadoc)
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(final TreeModelListener listener) {
        Trace.trace(CLASS, this, "addTreeModelListener", listener);
        super.addTreeModelListener(listener);
    }

    public void addModule(final ModuleProperties prop) {
        Runnable addModule = new Runnable() {
            public void run() {
                final QedeqTreeNode root = (QedeqTreeNode) getRoot();
                final QedeqTreeNode node = new QedeqTreeNode(prop, false);
                Trace.trace(CLASS, this, "addModule", "calls insertNodeInto=" + node);
                module2Path.put(prop.getModuleAddress(), new TreePath(node.getPath()));
                for (int i = 0; i < root.getChildCount(); i++) {
                    final QedeqTreeNode n = (QedeqTreeNode) root.getChildAt(i);
                    final ModuleProperties p = (ModuleProperties) n.getUserObject();
                    if (p.getModuleAddress().getFileName().compareTo(prop.getModuleAddress()
                            .getFileName()) > 0) {
                        insertNodeInto(node, root, i);
                        return;
                    }
                }
                insertNodeInto(node, root, root.getChildCount());
            }
        };

        SwingUtilities.invokeLater(addModule);

    }

    public void stateChanged(final ModuleProperties prop) {
        Runnable stateChanged = new Runnable() {
            public void run() {
                final TreePath path = (TreePath) module2Path.get(prop.getModuleAddress());
                if (path == null) {
                    throw new IllegalArgumentException("unknown property");
                }
                final QedeqTreeNode node = (QedeqTreeNode) path.getLastPathComponent();
                ModuleProperties sameProp = (ModuleProperties) node.getUserObject();
                if (!prop.equals(sameProp)) {
                    throw new IllegalStateException("should not happen");
                }
                Trace.trace(CLASS, this, "stateChanged", "nodeChanged=" + node);
                nodeChanged(node);
            }
        };

        SwingUtilities.invokeLater(stateChanged);
    }

    public void removeModule(final ModuleProperties prop) {
        Runnable removeModule = new Runnable() {
            public void run() {
                final TreePath path = (TreePath) module2Path.get(prop.getModuleAddress());
                if (path == null) {
                    throw new IllegalArgumentException("unknown property: "
                        + prop.getModuleAddress());
                }
                final QedeqTreeNode node = (QedeqTreeNode) path.getLastPathComponent();
                ModuleProperties sameProp = (ModuleProperties) node.getUserObject();
                if (!prop.equals(sameProp)) {
                    throw new IllegalStateException("should not happen");
                }
                module2Path.remove(prop.getModuleAddress());
                Trace.trace(CLASS, this, "removeModule", "nodeRemoved=" + node);
                removeNodeFromParent(node);
            }
        };

        SwingUtilities.invokeLater(removeModule);
    }

    public TreePath getSelectionPath(final ModuleProperties prop) {
        final TreePath path = (TreePath) module2Path.get(prop.getModuleAddress());
        if (path == null) {
            throw new IllegalArgumentException("unknown property");
        }
        final QedeqTreeNode node = (QedeqTreeNode) path.getLastPathComponent();
        ModuleProperties sameProp = (ModuleProperties) node.getUserObject();
        if (!prop.equals(sameProp)) {
            throw new IllegalStateException("should not happen");
        }
        return path;
    }

}
