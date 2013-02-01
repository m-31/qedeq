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

package org.qedeq.gui.se.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

/**
 * Responsible for text field context menus.
 *
 * @author  Michael Meyling.
 *
 */
class ClipboardListener extends MouseAdapter implements ActionListener {

    /** Popup menu for editable fields. */
    private final JPopupMenu popEdit;

    /** Popup menu for non editable fields. */
    private final JPopupMenu popNoEdit;

    /** Reference to text field. */
    private final JTextComponent outer;


    /**
     * Constructor.
     *
     * @param   outer   Work with this field.
     */
    ClipboardListener(final JTextComponent outer) {
        this.outer = outer;

        popEdit = new JPopupMenu();

        final JMenuItem selectAll1 = new JMenuItem("Select All");
        selectAll1.addActionListener(this);
        selectAll1.setActionCommand("selectAll");
        popEdit.add(selectAll1);

        final JMenuItem copy1 = new JMenuItem("Copy");
        copy1.addActionListener(this);
        copy1.setActionCommand("copy");
        popEdit.add(copy1);

        final JMenuItem find1 = new JMenuItem("Find");
// FIXME 20130201 m31: didn't work :-(
//        find1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
        find1.addActionListener(this);
        find1.setActionCommand("find");
        popEdit.add(find1);

        final JMenuItem jmenuitem2 = new JMenuItem("Cut");
        jmenuitem2.addActionListener(this);
        jmenuitem2.setActionCommand("cut");
        popEdit.add(jmenuitem2);

        final JMenuItem jmenuitem3 = new JMenuItem("Paste");
        jmenuitem3.addActionListener(this);
        jmenuitem3.setActionCommand("paste");
        popEdit.add(jmenuitem3);

        popNoEdit = new JPopupMenu();

        final JMenuItem selectAll2 = new JMenuItem("Select All");
        selectAll2.addActionListener(this);
        selectAll2.setActionCommand("selectAll");
        popEdit.add(selectAll2);

        final JMenuItem copy2 = new JMenuItem("Copy");
        copy2.addActionListener(this);
        copy2.setActionCommand("copy");
        popEdit.add(copy2);

        final JMenuItem find2 = new JMenuItem("Find");
        find2.addActionListener(this);
        find2.setActionCommand("find");
        popEdit.add(find2);

        popNoEdit.add(selectAll2);

        popNoEdit.add(copy2);

        popNoEdit.add(find2);

    }

    /**
     * Add context menu item.
     *
     * @param   item    Add this menu entry.
     */
    public void addMenuItem(final JMenuItem item) {
        popEdit.add(item);
        popNoEdit.add(item);
    }

    public void mousePressed(final MouseEvent mouseevent) {
        if (mouseevent.getModifiers() != 16) {
            if (outer.isEditable()) {
                popEdit.show(outer, mouseevent.getX(), mouseevent.getY());
            } else {
                popNoEdit.show(outer, mouseevent.getX(), mouseevent.getY());
            }
        }
    }

    public void actionPerformed(final ActionEvent actionevent) {
        final String s = actionevent.getActionCommand();
        if (s.equals("copy")) {
            outer.copy();
        } else if (s.equals("cut")) {
            outer.cut();
        } else if (s.equals("paste")) {
            outer.paste();
        } else if (s.equals("selectAll")) {
            outer.selectAll();
        } else if (s.equals("find")) {
            find();
        }
    }

    private void find()  {
        FindDialog finder = new FindDialog(outer);
        finder.show();
    }

}
