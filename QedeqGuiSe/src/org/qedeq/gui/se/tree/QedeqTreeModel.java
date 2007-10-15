/* $Id: QedeqTreeModel.java,v 1.2 2007/10/07 16:39:59 m31 Exp $
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
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class QedeqTreeModel extends DefaultTreeModel implements ModuleEventListener {

    /**
     * Properties of modules.
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
/*
        final PmiiTreeNode root = (PmiiTreeNode) getRoot();
        final PmiiTreeNode axioms = new PmiiTreeNode(
            new ModuleElement("Axioms", "Module 4",
            ModuleElement.ATOM), false);
        final PmiiTreeNode definitions = new PmiiTreeNode(
            new ModuleElement("Definitions", "Module 4",
            ModuleElement.ATOM), false);
        root.add(axioms);
        root.add(definitions);
*/
    }

    /*  (non-Javadoc)
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(final TreeModelListener listener) {
        Trace.trace(this, "addTreeModelListener", listener);
        super.addTreeModelListener(listener);
    }

    /* (non-Javadoc)
     * @see com.meyling.principia.module.ModuleListener#addModule(
     * com.meyling.principia.module.ModuleProperties)
     */
    public void addModule(final ModuleProperties prop) {
        final QedeqTreeNode root = (QedeqTreeNode) getRoot();
        final QedeqTreeNode node = new QedeqTreeNode(prop, false);
        Trace.trace(this, "addModule", "calls insertNodeInto=" + node);
        module2Path.put(prop.getAddress(), new TreePath(node.getPath()));
        for (int i = 0; i < root.getChildCount(); i++) {
            final QedeqTreeNode n = (QedeqTreeNode) root.getChildAt(i);
            final ModuleProperties p = (ModuleProperties) n.getUserObject();
            if (p.getModuleAddress().getFileName().compareTo(prop.getModuleAddress().getFileName())
                    > 0) {
                insertNodeInto(node, root, i);
                return;
            }
        }
        insertNodeInto(node, root, root.getChildCount());
    }


    /* (non-Javadoc)
     * @see com.meyling.principia.module.ModuleListener#stateChanged(
     * com.meyling.principia.module.ModuleProperties)
     */
    public void stateChanged(final ModuleProperties prop) {
        final TreePath path = (TreePath) module2Path.get(prop.getAddress());
        if (path == null) {
            throw new IllegalArgumentException("unknown property");
        }
        final QedeqTreeNode node = (QedeqTreeNode) path.getLastPathComponent();
        ModuleProperties sameProp = (ModuleProperties) node.getUserObject();
        if (!prop.equals(sameProp)) {
            throw new IllegalStateException("should not happen");
        }
        Trace.trace(this, "stateChanged", "nodeChanged=" + node);
        this.nodeChanged(node);
    }


    /* (non-Javadoc)
     * @see com.meyling.principia.module.ModuleListener#addModule(
     * com.meyling.principia.module.ModuleProperties)
     */
    public void removeModule(final ModuleProperties prop) {
        final TreePath path = (TreePath) module2Path.get(prop.getAddress());
        if (path == null) {
            throw new IllegalArgumentException("unknown property");
        }
        final QedeqTreeNode node = (QedeqTreeNode) path.getLastPathComponent();
        ModuleProperties sameProp = (ModuleProperties) node.getUserObject();
        if (!prop.equals(sameProp)) {
            throw new IllegalStateException("should not happen");
        }
        module2Path.remove(prop.getAddress());
        Trace.trace(this, "removeModule", "nodeRemoved=" + node);
        this.removeNodeFromParent(node);
    }


    public TreePath getSelectionPath(final ModuleProperties prop) {
        final TreePath path = (TreePath) module2Path.get(prop.getAddress());
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
