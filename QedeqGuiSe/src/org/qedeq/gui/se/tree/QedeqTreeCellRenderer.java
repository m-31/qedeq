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

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.util.AnimatedGifCreator;
import org.qedeq.gui.se.util.DecoratedIcon;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.state.AbstractState;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.state.FormallyProvedState;
import org.qedeq.kernel.se.state.LoadingState;
import org.qedeq.kernel.se.state.WellFormedState;

import furbelow.AnimatedIcon;

/**
 * Renderer for a JTree.
 *
 * @author  Michael Meyling
 */
public final class QedeqTreeCellRenderer extends DefaultTreeCellRenderer {

    /** This class. */
    private static final Class CLASS = QedeqTreeCellRenderer.class;

    /** Status icon. */
    private static ImageIcon startIcon
        = createImageIcon("module_start.gif");

    /** Status icon. */
    private static ImageIcon loadedIcon
        = createImageIcon("module_loaded.gif");

    /** Status icon. */
    private static ImageIcon loadedRequiredIcon
        = createImageIcon("module_loaded_required.gif");

    /** Status icon. */
    private static ImageIcon wellFormedIcon
        = createImageIcon("module_checked.gif");

    /** Status icon. */
    private static ImageIcon formallyProvedIcon
        = createImageIcon("module_checked2.gif");

    /** Status icon. */
    private static AnimatedIcon startNextIcon
        = new AnimatedIcon(createImageIcon("next_module_start.gif"));

    private static AnimatedIcon startFlashIcon
        = new AnimatedIcon(createImageIcon("flash_module_start.gif"));

    /** Status icon. */
    private static AnimatedIcon loadedNextIcon
        = new AnimatedIcon(createImageIcon("next_module_loaded.gif"));

    /** Status icon. */
    private static AnimatedIcon loadedFlashIcon
        = new AnimatedIcon(createImageIcon("flash_module_loaded.gif"));

    /** Status icon. */
    private static AnimatedIcon loadedRequiredNextIcon
        = new AnimatedIcon(createImageIcon("next_module_loaded_required.gif"));

    /** Status icon. */
    private static AnimatedIcon loadedRequiredFlashIcon
        = new AnimatedIcon(createImageIcon("flash_module_loaded_required.gif"));

    /** Status icon. */
    private static AnimatedIcon wellFormedNextIcon
        = new AnimatedIcon(createImageIcon("next_module_checked.gif"));

    /** Status icon. */
    private static AnimatedIcon wellFormedFlashIcon
        = new AnimatedIcon(createImageIcon("flash_module_checked.gif"));

    /** Status icon. */
    private static AnimatedIcon formallyProvedFlashIcon
        = new AnimatedIcon(createImageIcon("flash_module_checked2.gif"));

    /** Status icon. */
    private static ImageIcon basicErrorOverlayIcon
        = GuiHelper.readImageIcon("eclipse/error_co_dl.gif");

    /** Status icon. */
    private static ImageIcon pluginErrorOverlayIcon
        = GuiHelper.readImageIcon("eclipse/error_co_ur.gif");

    /** Status icon. */
    private static ImageIcon pluginWarningOverlayIcon
        = GuiHelper.readImageIcon("eclipse/warning_co_ur.gif");

    static ImageIcon createImageIcon(final String name) {
        return GuiHelper.readImageIcon("qedeq/16x16/" + name);

    }

    static AnimatedIcon createAnimatedIcon(final String current, final String next) {
        return AnimatedGifCreator.createAnimatedIcon(current, next);

    }


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
        Trace.param(CLASS, this, method, row + " is selected", isSelected);
        Trace.param(CLASS, this, method, row + " is expanded", expanded);
        Trace.param(CLASS, this, method, row + " hasFocus", hasFocus);
        Trace.param(CLASS, this, method, row + " leaf", leaf);
        Trace.param(CLASS, this, method, row + " maxSelectionRow", tree.getMaxSelectionRow());
        Trace.param(CLASS, this, method, row + " selectionCount", tree.getSelectionCount());
        Trace.param(CLASS, this, method, row + " rowCount", tree.getRowCount());
        Trace.param(CLASS, this, method, row + " tree path", tree.getPathForRow(row));

        ModuleElement unit;
        QedeqBo prop;
/* m31 20080502: Debugging code
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
        super.getTreeCellRendererComponent(tree, value, isSelected, expanded,
            leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode) {
            if (((DefaultMutableTreeNode) value).getUserObject()
                    instanceof ModuleElement) {
                unit = (ModuleElement) ((DefaultMutableTreeNode) value).getUserObject();
                setText(unit.getName());
            } else {
                prop = (QedeqBo) ((DefaultMutableTreeNode) value).getUserObject();
                final String text = prop.getName();
                setText(text);
                final AbstractState currentState = prop.getCurrentState();
                final Plugin plugin = prop.getCurrentlyRunningPlugin();
                if (prop.isLoaded()) {
                    setToolTipText(prop.getUrl().toString());
                } else {
                    setToolTipText(GuiHelper.getToolTipText(prop.getStateDescription()));
                }
//                setIcon(null);
//                System.out.println(prop.getStateDescription());
//                System.out.println("current state=" + currentState + " " + currentState.getClass());
//                System.out.println("lasz suc.stat=" + succesfullState + " " + succesfullState.getClass());
//                System.out.println();
                if (currentState == LoadingState.STATE_DELETED) {
                    setIcon(null);
                } else if (currentState == LoadingState.STATE_UNDEFINED) {
                    if (plugin == null) {
                        setIcon(prop, startIcon);
                    } else {
                        setIcon(startFlashIcon);
                    }
                } else if (currentState == LoadingState.STATE_LOADED) {
                    if (plugin == null) {
                        setIcon(prop, loadedIcon);
                    } else {
                        setIcon(loadedFlashIcon);
                    }
                } else if (currentState instanceof LoadingState) {
                    if (currentState.isFailure()) {
                        if (plugin == null) {
                            setIcon(prop, startIcon);
                        } else {
                            setIcon(startFlashIcon);
                        }
                    } else {
                        setIcon(startNextIcon);
                    }
                } else if (currentState == DependencyState.STATE_LOADED_REQUIRED_MODULES) {
                    if (plugin == null) {
                        setIcon(prop, loadedRequiredIcon);
                    } else {
                        setIcon(loadedRequiredFlashIcon);
                    }
                } else if (currentState instanceof DependencyState) {
                    if (currentState.isFailure()) {
                        if (plugin == null) {
                            setIcon(prop, loadedIcon);
                        } else {
                            setIcon(loadedFlashIcon);
                        }
                    } else {
                        setIcon(loadedNextIcon);
                    }
                } else if (currentState == WellFormedState.STATE_CHECKED) {
                    if (plugin == null) {
                        setIcon(prop, wellFormedIcon);
                    } else {
                        setIcon(wellFormedFlashIcon);
                    }
                } else if (currentState instanceof WellFormedState) {
                    if (currentState.isFailure()) {
                        if (plugin == null) {
                            setIcon(prop, loadedRequiredIcon);
                        } else {
                            setIcon(loadedRequiredFlashIcon);
                        }
                    } else {
                        setIcon(loadedRequiredNextIcon);
                    }
                } else if (currentState == FormallyProvedState.STATE_CHECKED) {
                    if (plugin == null) {
                        setIcon(prop, formallyProvedIcon);
                    } else {
                        setIcon(formallyProvedFlashIcon);
                    }
                } else if (currentState instanceof FormallyProvedState) {
                    if (currentState.isFailure()) {
                        if (plugin == null) {
                            setIcon(prop, wellFormedIcon);
                        } else {
                            setIcon(wellFormedFlashIcon);
                        }
                    } else {
                        setIcon(wellFormedNextIcon);
                    }
                } else {    // unknown loading state
                    throw new IllegalStateException("unknown module state: "
                        + currentState.getText());
                }

            }
        }
    // LATER m31 20080502: do we want to leave it for our alternative "paint" or do we delete it?
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

    // LATER m31 20080502: do we want to rename it back to "paint" or do we delete it?
    /*
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

    private void setIcon(final QedeqBo qedeq, final ImageIcon icon) {
        final DecoratedIcon ic = new DecoratedIcon(icon);
        if (qedeq.hasBasicFailures()) {
            ic.decorate(basicErrorOverlayIcon);
        }
// FIXME 20130220 m31: we have to separate basic failures from plugin errors, here a method is missing
//        if (qedeq.hasErrors()) {
//            ic.decorate(pluginErrorOverlayIcon);
//        }
        if (qedeq.hasWarnings()) {
            ic.decorate(pluginWarningOverlayIcon);
        }
        setIcon(ic);
    }


}
