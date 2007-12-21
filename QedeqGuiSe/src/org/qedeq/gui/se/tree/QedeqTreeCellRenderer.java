/* $Id: QedeqTreeCellRenderer.java,v 1.4 2007/12/21 23:34:47 m31 Exp $
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.qedeq.kernel.bo.module.DependencyState;
import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.LogicalState;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.trace.Trace;

/**
 * Renderer for a JTree.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class QedeqTreeCellRenderer extends JLabel implements TreeCellRenderer {

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

    /** Color to use for the foreground for selected nodes. */
    private Color textSelectionColor = UIManager.getColor("Tree.selectionForeground");

    /** Color to use for the foreground for non-selected nodes. */
    private Color textNonSelectionColor = UIManager.getColor("Tree.textForeground");

    /** Color to use for the background when a node is selected. */
    private Color backgroundSelectionColor = UIManager.getColor("Tree.selectionBackground");

    /** Color to use for the background when the node isn't selected. */
    private Color backgroundNonSelectionColor = UIManager.getColor("Tree.textBackground");

    public QedeqTreeCellRenderer() {
        super();
    }

    public Component getTreeCellRendererComponent(final JTree tree,
            final Object value, final boolean isSelected,
            final boolean expanded, final boolean leaf, final int row,
            final boolean hasFocus) {

        final String method = "getTreeCellRendererComponent";
        Trace.param(this, method, row + " is selected", isSelected);
        Trace.param(this, method, row + " hasFocus", hasFocus);
        Trace.param(this, method, row + " leaf", leaf);
        Trace.param(this, method, row + " maxSelectionRow", tree.getMaxSelectionRow());
        Trace.param(this, method, row + " selectionCount", tree.getSelectionCount());
        Trace.param(this, method, row + " rowCount", tree.getRowCount());
        Trace.param(this, method, row + " tree path", tree.getPathForRow(row));

        ModuleElement unit;
        ModuleProperties prop;
        if (value instanceof DefaultMutableTreeNode) {
            if (((DefaultMutableTreeNode) value).getUserObject()
                    instanceof ModuleElement) {
                unit = (ModuleElement) ((DefaultMutableTreeNode)
                    value).getUserObject();
                setText(unit.getName());
                setToolTipText("here should stand a tool tip"); // LATER mime 20071022: add tool tip
            } else {
                prop = (ModuleProperties) ((DefaultMutableTreeNode)
                    value).getUserObject();
                String text = prop.getName();
                setText(text);
                final LoadingState loadingState = prop.getLoadingState();
                final DependencyState dependencyState = prop.getDependencyState();
                final LogicalState logicalState = prop.getLogicalState();

                if (loadingState == LoadingState.STATE_LOADED) {
                    setToolTipText(prop.getAddress());
                } else {
                    setToolTipText(prop.getStateDescription());
                }

                setIcon(null);
                if (loadingState == LoadingState.STATE_LOADING_FROM_WEB) {
                    setIcon(webLoadingIcon);
                } else if (loadingState == LoadingState.STATE_LOADING_FROM_WEB_FAILED) {
                    setIcon(webLoadingErrorIcon);
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
        if (isSelected) {
            setBackground(backgroundSelectionColor);
            setForeground(textSelectionColor);
        } else {
            setBackground(backgroundNonSelectionColor);
            setForeground(textNonSelectionColor);
        }

/*
        Trace.trace(this, method, "-- global info");
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Trace.param(this, method, "-- child", node.getChildCount());
        for (int i = 0; i < node.getChildCount(); i++) {
            Trace.param(this, method, "--- node" + i,  node.getChildAt(i));
            Trace.param(this, method, "--- child count" + i,  node.getChildAt(i).getChildCount());
        }
*/
        return this;
    }

    /**
     * paint is subclassed to draw the background correctly.  JLabel
     * currently does not allow backgrounds other than white, and it
     * will also fill behind the icon. Something that isn't desirable.
     */
   public void paint(final Graphics g) {
       final String method = "paint";
       Trace.param(this, method, "label", getText());
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
