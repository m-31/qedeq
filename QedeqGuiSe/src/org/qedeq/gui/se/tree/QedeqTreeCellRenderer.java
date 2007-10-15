/* $Id: QedeqTreeCellRenderer.java,v 1.3 2007/10/07 16:39:59 m31 Exp $
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

import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.LogicalState;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.trace.Trace;

/**
 * Renderer for a JTree.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public final class QedeqTreeCellRenderer extends JLabel implements TreeCellRenderer {

    private static ImageIcon webLoadingIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_web_loading.gif"));

    private static ImageIcon webLoadingErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_web_loading_error.gif"));

    private static ImageIcon bufferLoadingIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_buffer_loading.gif"));

    private static ImageIcon bufferLoadingErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_buffer_loading_error.gif"));

    private static ImageIcon memoryLoadingIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_memory_loading.gif"));

    private static ImageIcon memoryLoadingErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_memory_loading_error.gif"));

    private static ImageIcon loadedIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_loaded.gif"));

    private static ImageIcon checkingIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_checking.gif"));

    private static ImageIcon checkingErrorIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_checking_error.gif"));

    private static ImageIcon checkedIcon = new ImageIcon(
        QedeqTreeCellRenderer.class.getResource(
            "/images/qedeq/16x16/module_checked.gif"));

    /** Whether or not the item that was last configured is selected. */
    private boolean selected;

    // Colors
    /** Color to use for the foreground for selected nodes. */
    private Color textSelectionColor = UIManager.getColor("Tree.selectionForeground");

    /** Color to use for the foreground for non-selected nodes. */
    private Color textNonSelectionColor = UIManager.getColor("Tree.textForeground");

    /** Color to use for the background when a node is selected. */
    private Color backgroundSelectionColor = UIManager.getColor("Tree.selectionBackground");

    /** Color to use for the background when the node isn't selected. */
    private Color backgroundNonSelectionColor = UIManager.getColor("Tree.textBackground");

    private boolean visible;

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
                setToolTipText("here should stand a tool tip");  // TODO
            } else {
                prop = (ModuleProperties) ((DefaultMutableTreeNode)
                    value).getUserObject();
                String text = prop.getName();
                setText(text);
                final LoadingState state = prop.getLoadingState();

                if (state == LoadingState.STATE_LOADED) {
                    setToolTipText(prop.getAddress());
                } else {
                    setToolTipText(prop.getStateDescription());
                }

                if (state == LoadingState.STATE_LOADING_FROM_WEB) {
                    setIcon(webLoadingIcon);
                } else if (state == LoadingState.STATE_LOADING_FROM_WEB_FAILED) {
                    setIcon(webLoadingErrorIcon);
                } else if (state == LoadingState.STATE_LOADING_FROM_BUFFER) {
                    setIcon(bufferLoadingIcon);
                } else if (state == LoadingState.STATE_LOADING_FROM_BUFFER_FAILED) {
                    setIcon(bufferLoadingErrorIcon);
                } else if (state == LoadingState.STATE_LOADING_INTO_MEMORY) {
                    setIcon(memoryLoadingIcon);
                } else if (state == LoadingState.STATE_LOADING_INTO_MEMORY_FAILED) {
                    setIcon(memoryLoadingErrorIcon);
                } else if (state == LoadingState.STATE_LOADED) {
                    if (prop.getLogicalState() == LogicalState.STATE_UNCHECKED) {
                        setIcon(loadedIcon);
                    } else if (prop.getLogicalState() == LogicalState.STATE_EXTERNAL_CHECKING
                            || prop.getLogicalState() == LogicalState.STATE_INTERNAL_CHECKING) {
                        setIcon(checkingIcon);
                    } else if (prop.getLogicalState() == LogicalState.STATE_EXTERNAL_CHECK_FAILED
                            || prop.getLogicalState() == LogicalState.STATE_INTERNAL_CHECK_FAILED) {
                        setIcon(checkingErrorIcon);
                    } else if (prop.getLogicalState() == LogicalState.STATE_CHECKED) {
                        setIcon(checkedIcon);
                    } else {
                        setIcon(null);
                        Trace.trace(this, "getTreeCellRendererComponent",
                            "unknown module state: " + prop.getLogicalState().getText());
                        throw new IllegalStateException("unknown module state: "
                           + prop.getLogicalState().getText());
                    }
                } else {
                    setIcon(null);
                    Trace.trace(this, "getTreeCellRendererComponent",
                        "unknown module state: " + prop.getLogicalState().getText());
                    throw new IllegalStateException("unknown module state: " + state.getText());
                }
            }
        }
        if (isSelected) {
            setForeground(textSelectionColor);
        } else {
            setForeground(textNonSelectionColor);
        }

        /* Update the selected flag for the next paint. */
        this.selected = isSelected;

        /* Update visibility flag for the next paint. This is a hack that works for certain
         * tree parameters (no visible root, just leafs in the root node). */
        visible = tree.getPathForRow(row) != null;
        if (visible) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
            if (row < 0 || row >= node.getChildCount()) {
                visible = false;
            }
        }

        Trace.trace(this, method, "-- global info");
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Trace.param(this, method, "-- child", node.getChildCount());
        for (int i = 0; i < node.getChildCount(); i++) {
            Trace.param(this, method, "--- node" + i,  node.getChildAt(i));
            Trace.param(this, method, "--- child count" + i,  node.getChildAt(i).getChildCount());
        }
        return this;
    }

    /**
     * paint is subclassed to draw the background correctly.  JLabel
     * currently does not allow backgrounds other than white, and it
     * will also fill behind the icon.  Something that isn't desirable.
     */
   public void paint(final Graphics g) {
       final String method = "paint";
       Trace.param(this, method, "selected", selected);
       Trace.param(this, method, "label", getText());
       Trace.param(this, method, "visible", visible);
       Color bColor;
       Icon currentI = getIcon();

        if (visible) {
            if (selected) {
                bColor = backgroundSelectionColor;
                setForeground(textSelectionColor);
            } else {
                bColor = backgroundNonSelectionColor;
                setForeground(textNonSelectionColor);
            }
            g.setColor(bColor);
            if (currentI != null && getText() != null) {
                int offset = (currentI.getIconWidth() + getIconTextGap());
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

}
