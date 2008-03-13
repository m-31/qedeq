/* $Id: CPTextArea.java,v 1.1 2007/10/07 16:40:00 m31 Exp $
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

package org.qedeq.gui.se.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;


/**
 * TextField with Cut and Paste.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class CPTextArea extends JTextArea {

    /**
     * Constructor.
     */
    public CPTextArea() {
        ClipboardListener clipboardactivator = new ClipboardListener(this);
        addMouseListener(clipboardactivator);
    }

    /**
     * Constructor with initial text.
     *
     * @param   initialText Initial value.
     */
    public CPTextArea(final String initialText) {
        setDragEnabled(true);
        ClipboardListener clipboardactivator = new ClipboardListener(this);
        addMouseListener(clipboardactivator);
        setText(initialText);
    }

    /**
     * Clipboard listener.
     *
     * @version $Revision: 1.1 $
     * @author    Michael Meyling
     */
    private class ClipboardListener extends MouseAdapter implements ActionListener {

        /** Popup menu. */
        private final JPopupMenu popedit;

        /** Reference to text field. */
        private final CPTextArea outer;


        /**
         * Constructor.
         *
         * @param   outer   Work with this field.
         */
        ClipboardListener(final CPTextArea outer) {
            this.outer = outer;
            popedit = new JPopupMenu();
            JMenuItem jmenuitem = new JMenuItem("Copy");
            jmenuitem.addActionListener(this);
            jmenuitem.setActionCommand("copy");
            JMenuItem jmenuitem1 = new JMenuItem("Cut");
            jmenuitem1.addActionListener(this);
            jmenuitem1.setActionCommand("cut");
            JMenuItem jmenuitem2 = new JMenuItem("Paste");
            jmenuitem2.addActionListener(this);
            jmenuitem2.setActionCommand("paste");
            popedit.add(jmenuitem);
            popedit.add(jmenuitem1);
            popedit.add(jmenuitem2);
        }

        public void mousePressed(final MouseEvent mouseevent) {
            if (mouseevent.getModifiers() != 16) {
                popedit.show(outer, mouseevent.getX(), mouseevent.getY());
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
            }
        }

    }

}
