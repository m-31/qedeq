/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.LogicalState;

/**
 * Renderer for a JTree.
 *
 * @version $Revision: 1.8 $
 * @author  Michael Meyling
 */
public final class QedeqTreeCellRenderer extends DefaultTreeCellRenderer {

    /** This class. */
    private static final Class CLASS = QedeqTreeCellRenderer.class;

    /** Status icon. */
    private static ImageIcon webLoadingIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_web_loading.gif"));

    /** Status icon. */
    private static ImageIcon webLoadingErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_web_loading_error.gif"));

    /** Status icon. */
    private static ImageIcon fileLoadingIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_file_loading.gif"));

    /** Status icon. */
    private static ImageIcon fileLoadingErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_file_loading_error.gif"));

    /** Status icon. */
    private static ImageIcon memoryLoadingIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_memory_loading.gif"));

    /** Status icon. */
    private static ImageIcon memoryLoadingErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_memory_loading_error.gif"));

    /** Status icon. */
    private static ImageIcon loadedIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_loaded.gif"));

    /** Status icon. */
    private static ImageIcon loadingRequiredIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_loading_required.gif"));

    /** Status icon. */
    private static ImageIcon loadingRequiredErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_loading_required_error.gif"));

    /** Status icon. */
    private static ImageIcon loadedRequiredIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_loaded_required.gif"));

    /** Status icon. */
    private static ImageIcon checkingRequiredIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_checking_required.gif"));

    /** Status icon. */
    private static ImageIcon checkingRequiredErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_checking_required_error.gif"));

    /** Status icon. */
    private static ImageIcon checkingIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_checking.gif"));

    /** Status icon. */
    private static ImageIcon checkingErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_checking_error.gif"));

    /** Status icon. */
    private static ImageIcon checkedIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_checked.gif"));

// LATER mime 20080502: do we want to leave it for our alternative "paint" or do we delete it?
//
//    /** Color to use for the foreground for selected nodes. */
//    private Color textSelectionColor = UIManager.getColor("Tree.selectionForeground");
//
//    /** Color to use for the foreground for non-selected nodes. */
//    private Color textNonSelectionColor = UIManager.getColor("Tree.textForeground");
//
//    /** Color to use for the background when a node is selected. */
//    private Color backgroundSelectionColor = UIManager.getColor("Tree.selectionBackground");
//
//    /** Color to use for the background when the node isn't selected. */
//    private Color backgroundNonSelectionColor = UIManager.getColor("Tree.textBackground");

    public QedeqTreeCellRenderer() {
        super();
    }

    public synchronized Component getTreeCellRendererComponent(final JTree tree,
            final Object value, final boolean isSelected,
            final boolean expanded, final boolean leaf, final int row,
            final boolean hasFocus) {

        final String method = "getTreeCellRendererComponent";
        Trace.paramInfo(CLASS, this, method, row + " is selected", isSelected);
        Trace.paramInfo(CLASS, this, method, row + " is expanded", expanded);
        Trace.paramInfo(CLASS, this, method, row + " hasFocus", hasFocus);
        Trace.paramInfo(CLASS, this, method, row + " leaf", leaf);
        Trace.paramInfo(CLASS, this, method, row + " maxSelectionRow", tree.getMaxSelectionRow());
        Trace.paramInfo(CLASS, this, method, row + " selectionCount", tree.getSelectionCount());
        Trace.paramInfo(CLASS, this, method, row + " rowCount", tree.getRowCount());
        Trace.paramInfo(CLASS, this, method, row + " tree path", tree.getPathForRow(row));

        ModuleElement unit;
        QedeqBo prop;
/* mime 20080502: Debugging code
        TreeModel model = tree.getModel();
        BasicTreeUI ui = (BasicTreeUI) tree.getUI();
        VariableHeightLayoutCache vhlc = null;
        Vector visibleNodes = null;
        try {
            vhlc = (VariableHeightLayoutCache) IoUtility.getFieldContentSuper(ui, "treeState");
            visibleNodes = (Vector) IoUtility.getFieldContentSuper(vhlc, "visibleNodes");
            System.out.println("vectorSize = " + visibleNodes.size());
            for (int i = 0; i < visibleNodes.size(); i++) {
                System.out.println("  " + visibleNodes.get(i));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
*/
        super.getTreeCellRendererComponent(
            tree, value, isSelected, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode) {
            if (((DefaultMutableTreeNode) value).getUserObject()
                    instanceof ModuleElement) {
                unit = (ModuleElement) ((DefaultMutableTreeNode)
                    value).getUserObject();
                setText(unit.getName());
            } else {
                prop = (QedeqBo) ((DefaultMutableTreeNode)
                    value).getUserObject();
                String text = prop.getName();
                setText(text);
                final LoadingState loadingState = prop.getLoadingState();
                final DependencyState dependencyState = prop.getDependencyState();
                final LogicalState logicalState = prop.getLogicalState();

                if (loadingState == LoadingState.STATE_LOADED) {
                    setToolTipText(prop.getUrl().toString());
                } else {
                    setToolTipText(prop.getStateDescription());
                }

                setIcon(null);
                if (loadingState == LoadingState.STATE_DELETED) {
                    // do nothing;
                } else if (loadingState == LoadingState.STATE_LOADING_FROM_WEB) {
                    setIcon(webLoadingIcon);
                } else if (loadingState == LoadingState.STATE_LOADING_FROM_WEB_FAILED) {
                    setIcon(webLoadingErrorIcon);
                } else if (loadingState == LoadingState.STATE_LOADING_FROM_LOCAL_FILE) {
                    setIcon(fileLoadingIcon);
                } else if (loadingState == LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED) {
                    setIcon(fileLoadingErrorIcon);
                } else if (loadingState == LoadingState.STATE_LOADING_FROM_BUFFER) {
                    setIcon(fileLoadingIcon);
                } else if (loadingState == LoadingState.STATE_LOADING_FROM_BUFFER_FAILED) {
                    setIcon(fileLoadingErrorIcon);
                } else if (loadingState == LoadingState.STATE_LOADING_INTO_MEMORY) {
                    setIcon(memoryLoadingIcon);
                } else if (loadingState == LoadingState.STATE_LOADING_INTO_MEMORY_FAILED) {
                    setIcon(memoryLoadingErrorIcon);
                } else if (loadingState == LoadingState.STATE_LOADED) {
                    setIcon(loadedIcon);
                    if (dependencyState != DependencyState.STATE_UNDEFINED) {
                        if (dependencyState == DependencyState.STATE_LOADING_REQUIRED_MODULES) {
                            setIcon(loadingRequiredIcon);
                        } else if (dependencyState
                                == DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED) {
                            setIcon(loadingRequiredErrorIcon);
                        } else if (dependencyState
                                == DependencyState.STATE_LOADED_REQUIRED_MODULES) {
                            setIcon(loadedRequiredIcon);
                            if (logicalState != LogicalState.STATE_UNCHECKED) {
                                if (logicalState == LogicalState.STATE_EXTERNAL_CHECKING) {
                                 setIcon(checkingRequiredIcon);
                                } else if (logicalState
                                        == LogicalState.STATE_EXTERNAL_CHECKING_FAILED) {
                                    setIcon(checkingRequiredErrorIcon);
                                } else if (logicalState == LogicalState.STATE_INTERNAL_CHECKING) {
                                    setIcon(checkingIcon);
                                } else if (logicalState
                                        == LogicalState.STATE_INTERNAL_CHECKING_FAILED) {
                                    setIcon(checkingErrorIcon);
                                } else if (logicalState == LogicalState.STATE_CHECKED) {
                                    setIcon(checkedIcon);
                                } else {    // unknown logical state
                                    throw new IllegalStateException("unknown module state: "
                                        + logicalState.getText());
                                }
                            }
                        } else {    // unknown dependency state
                            throw new IllegalStateException("unknown module state: "
                                + dependencyState.getText());
                        }
                    }
                } else {    // unknown loading state
                    throw new IllegalStateException("unknown module state: "
                        + loadingState.getText());
                }
            }
        }
    // LATER mime 20080502: do we want to leave it for our alternative "paint" or do we delete it?
/*
        if (isSelected) {
            setBackground(backgroundSelectionColor);
            setForeground(textSelectionColor);
        } else {
            setBackground(backgroundNonSelectionColor);
            setForeground(textNonSelectionColor);
        }
*/
// mime 20080430: comment out, debug code!
//        Trace.trace(CLASS, this, method, "-- global info");
//        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
//        Trace.param(CLASS, this, method, "-- child", node.getChildCount());
//        for (int i = 0; i < node.getChildCount(); i++) {
//            Trace.param(CLASS, this, method, "--- node" + i,  node.getChildAt(i));
//            Trace.param(CLASS, this, method, "--- child count" + i,
//                node.getChildAt(i).getChildCount());
//        }

        return this;
    }

    // LATER mime 20080502: do we want to rename it back to "paint" or do we delete it?
    /**
     * paint is subclassed to draw the background correctly.  JLabel
     * currently does not allow backgrounds other than white, and it
     * will also fill behind the icon. Something that isn't desirable.
     */
   public void paintttttt(final Graphics g) {
       final String method = "paint";
       Trace.param(CLASS, this, method, "label", getText());
       Icon currentI = getIcon();
       if (currentI != null && getText() != null) {
           int offset = (currentI.getIconWidth() + getIconTextGap());
           g.setColor(getBackground());
           if (getComponentOrientation().isLeftToRight()) {
                g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
           } else {
                g.fillRect(0, 0, getWidth() - 1 - offset, getHeight() - 1);
           }
       } else {
           g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
       }
       super.paint(g);
    }

}
