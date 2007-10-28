/* $Id: QedeqTreeCellEditor.java,v 1.1 2007/08/21 20:44:59 m31 Exp $
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

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Editor for a JTree.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class QedeqTreeCellEditor extends DefaultCellEditor {

    private final JTextField editor;

    private ModuleElement editorValue;

    public QedeqTreeCellEditor(final JTextField editor) {
        super(editor);
        this.editor = editor;
    }


    public Component getTreeCellEditorComponent(final JTree tree, final Object value,
            final boolean isSelected, final boolean expanded, final boolean leaf, final int row) {

        final JTextField aEditor = (JTextField) super.getTreeCellEditorComponent(
            tree, value, isSelected, expanded, leaf, row);
        final ModuleElement unit;
        if (value instanceof DefaultMutableTreeNode) {
            unit = (ModuleElement) ((DefaultMutableTreeNode)
                value).getUserObject();
            editorValue = unit;
            aEditor.setText(unit.getName());
        }
        return aEditor;

    }

    public boolean stopCellEditing() {
        try {
            editorValue.setName(editor.getText());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
            editor.selectAll();
            return false;
        }
        //TODO Error handling
        return super.stopCellEditing();
    }

    public Object getCellEditorValue() {
        return editorValue;
    }

}
