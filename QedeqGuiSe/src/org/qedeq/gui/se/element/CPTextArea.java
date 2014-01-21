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

package org.qedeq.gui.se.element;


import javax.swing.JMenuItem;
import javax.swing.JTextArea;


/**
 * TextField with Cut and Paste.
 *
 * @author  Michael Meyling
 */
public class CPTextArea extends JTextArea {

    /** Here is our context menu. */
    private final ClipboardListener clipboardactivator;

    /**
     * Constructor with initial text.
     *
     * @param   editable    Is this text area editable.
     */
    public CPTextArea(final boolean editable) {
        super();
        setDragEnabled(true);
        setEditable(editable);
        clipboardactivator = new ClipboardListener(this);
        addMouseListener(clipboardactivator);
    }

    /**
     * Constructor with initial text.
     *
     * @param   initialText Initial value.
     * @param   editable    Is this text area editable.
     */
    public CPTextArea(final String initialText, final boolean editable) {
        super();
        setDragEnabled(true);
        setEditable(editable);
        clipboardactivator = new ClipboardListener(this);
        addMouseListener(clipboardactivator);
        setText(initialText);
    }

    /**
     * Add context menu item.
     *
     * @param   item    Add this menu entry.
     */
    public void addMenuItem(final JMenuItem item) {
        clipboardactivator.addMenuItem(item);
    }

}
