/* $Id: QedeqTreeModel.java,v 1.5 2008/03/27 05:14:03 m31 Exp $
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

package org.qedeq.gui.se.tree;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.log.ModuleEventListener;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;


/**
 * Model for a JTree.
 *
 * A Model, which represents the underlying, logical structure of data in a
 * software application and the high-level class associated with it. The
 * object model does not contain any information about the user interface.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public final class QedeqTreeModel extends DefaultTreeModel implements ModuleEventListener {

    /** This class. */
    private static final Class CLASS = QedeqTreeModel.class;

    /**
     * Maps {@link org.qedeq.kernel.bo.module.ModuleAddress}es to {@link TreeNode}s..
     */
    private final Map address2Path = new HashMap();

    private QedeqTreeModel(final TreeNode root) {
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

    public void addModule(final QedeqBo prop) {
        Runnable addModule = new Runnable() {
            public void run() {
                synchronized (QedeqTreeModel.class) {
                System.out.println("adding " + prop);
                IoUtility.sleep();
                final QedeqTreeNode root = (QedeqTreeNode) getRoot();
                final QedeqTreeNode node = new QedeqTreeNode(prop, false);
                Trace.trace(CLASS, this, "addModule", "calls insertNodeInto=" + node);
                address2Path.put(prop.getModuleAddress(), new TreePath(node.getPath()));
                for (int i = 0; i < root.getChildCount(); i++) {
                    final QedeqTreeNode n = (QedeqTreeNode) root.getChildAt(i);
                    final QedeqBo p = (QedeqBo) n.getUserObject();
                    if (p.getModuleAddress().getFileName().compareTo(prop.getModuleAddress()
                            .getFileName()) > 0) {
                        Trace.param(CLASS, this, "addModule", "adding at", i);
                        IoUtility.sleep();
                        insertNodeInto(node, root, i);
                        IoUtility.sleep();
                        return;
                    }
                }
                Trace.param(CLASS, this, "addModule", "adding at end", root.getChildCount());
                IoUtility.sleep();
                insertNodeInto(node, root, root.getChildCount());
                IoUtility.sleep();
                }
            }
        };

//        try {
            SwingUtilities.invokeLater(addModule);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

    }

    public void stateChanged(final QedeqBo prop) {
        Runnable stateChanged = new Runnable() {
            public void run() {
//                synchronized (QedeqTreeModel.class){
//                final QedeqTreeNode root = (QedeqTreeNode) getRoot();
//                final QedeqTreeNode node = new QedeqTreeNode(prop, false);
//                node.setParent(root);
//                nodeChanged(node);
//                }
                synchronized (QedeqTreeModel.class){
                
                IoUtility.sleep();
                TreeNode root = (TreeNode) getRoot();
                final TreePath path = (TreePath) address2Path.get(prop.getModuleAddress());
                if (path == null) {
                    throw new IllegalArgumentException("unknown property: "
                        + prop.getModuleAddress());
                }
                final QedeqTreeNode node = (QedeqTreeNode) path.getLastPathComponent();
                QedeqBo sameProp = (QedeqBo) node.getUserObject();
                if (!prop.equals(sameProp)) {
                    throw new IllegalStateException("should not happen");
                }
                Trace.param(CLASS, this, "stateChanged", "nodeChanged", node);
                Trace.param(CLASS, this, "stateChanged", "state", prop.getStateDescription());
                IoUtility.sleep();
                nodeChanged(node);
                IoUtility.sleep();
                }
            }

        };

//        try {
            SwingUtilities.invokeLater(stateChanged);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public void removeModule(final QedeqBo prop) {
        Runnable removeModule = new Runnable() {
            public void run() {
/*        
                final QedeqTreeNode root = (QedeqTreeNode) getRoot();
                final QedeqTreeNode node = new QedeqTreeNode(prop, false);
                node.setParent(root);
                removeNodeFromParent(node);
*/                
       
                synchronized (QedeqTreeModel.class){
            
                final TreePath path = (TreePath) address2Path.get(prop.getModuleAddress());
                if (path == null) {
                    throw new IllegalArgumentException("unknown property: "
                        + prop.getModuleAddress());
                }
                final QedeqTreeNode node = (QedeqTreeNode) path.getLastPathComponent();
                QedeqBo sameProp = (QedeqBo) node.getUserObject();
                if (!prop.equals(sameProp)) {
                    throw new IllegalStateException("should not happen");
                }
                address2Path.remove(prop.getModuleAddress());
                Trace.trace(CLASS, this, "removeModule", "nodeRemoved=" + node);
                removeNodeFromParent(node);
                }
            }
        };

//        try {
            SwingUtilities.invokeLater(removeModule);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

}
