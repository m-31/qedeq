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

package org.qedeq.gui.se.tree;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * View for certain JTree.
 *
 * A View, which is a collection of classes representing the elements in the user interface
 * (all of the things the user can see and respond to on the screen, such as buttons, display
 * boxes, and so forth).
 *
 * @author  Michael Meyling
 */
public final class QedeqTreeView extends JPanel {

    /** Reference to JTree. */
    private final JTree theTree;

    /**
     * Constructor.
     *
     * @param   treeModel   model for this tree
     */
    public QedeqTreeView(final TreeModel treeModel) {
        super(new GridLayout(1, 1));
        // don't change these values without looking at the paint method of the TreeCellRenderer
        theTree = new JTree(treeModel);
        theTree.setEditable(false);
        theTree.setRootVisible(false);
        theTree.setExpandsSelectedPaths(true);
        theTree.setScrollsOnExpand(true);
        theTree.setShowsRootHandles(true);
        theTree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        // enable tool tips for the tree
        ToolTipManager.sharedInstance().registerComponent(theTree);

        theTree.setCellRenderer(new QedeqTreeCellRenderer());
        /* Make tree ask for the height of each row. */
        theTree.setRowHeight(-1);

        this.add(theTree);
        this.setMinimumSize(new Dimension(150, 100));
    }

    /* @see JTree#expandPath
     */
    public final void expandPath(final TreePath path) {
        theTree.expandPath(path);
    }

    /* @see JTree#addTreeSelectionListener
     */
    public final void addTreeSelectionListener(final TreeSelectionListener listener) {
         theTree.addTreeSelectionListener(listener);
    }


    /* @see JTree#setSelectionRow
     */
    public void setSelectionRow(final int row) {
        theTree.setSelectionRow(row);
    }

    /* @see JTree#setSelectionPath
     */
    public void setSelectionPath(final TreePath path) {
        theTree.setSelectionPath(path);
    }

    /* @see JTree#getSelectionPath
     */
    public TreePath getSelectionPath() {
        return theTree.getSelectionPath();
    }

    /* @see JTree#getSelectionPaths
     */
    public TreePath[] getSelectionPaths() {
        return theTree.getSelectionPaths();
    }

    /* @see JTree#getPathForLocation
     */
    public TreePath getPathForLocation(final int x, final int y) {
        return theTree.getPathForLocation(x, y);
    }

    /* @see JTree#startEditingAtPath
     */
    public void startEditingAtPath(final TreePath path) {
        theTree.startEditingAtPath(path);
    }

    /* @see JTree#treeAddMouseListener
     */
    public void treeAddMouseListener(final MouseListener mouseListener) {
        theTree.addMouseListener(mouseListener);
    }

}
