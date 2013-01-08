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

package org.qedeq.gui.se.util;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Various menu utility methods.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class MenuHelper  {

    /**
     * Hidden constructor.
     */
    private MenuHelper() {
        // nothing to do
    }

    /**
     * Checks and answers whether the quit action has been moved to an
     * operating system specific menu, e.g. the OS X application menu.
     *
     * @return true if the quit action is in an OS-specific menu
     */
    public static boolean isQuitInOSMenu() {
        return false;
    }

    public static JMenu createMenu(final String text, final char mnemonic) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    public static JMenuItem createMenuItem(final String text) {
        return new JMenuItem(text);
    }

    public static JMenuItem createMenuItem(final String text, final char mnemonic) {
        return new JMenuItem(text, mnemonic);
    }

    public static JMenuItem createMenuItem(final String text, final char mnemonic,
            final KeyStroke key) {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.setAccelerator(key);
        return menuItem;
    }

    public static JMenuItem createMenuItem(final String text, final Icon icon) {
        return new JMenuItem(text, icon);
    }

    public static JMenuItem createMenuItem(final String text, final Icon icon,
            final char mnemonic) {
        final JMenuItem menuItem = new JMenuItem(text, icon);
        menuItem.setMnemonic(mnemonic);
        return menuItem;
    }

    public static JMenuItem createMenuItem(final String text, final Icon icon, final char mnemonic,
            final KeyStroke key) {
        final JMenuItem menuItem = createMenuItem(text, icon, mnemonic);
        menuItem.setAccelerator(key);
        return menuItem;
    }

    public static JRadioButtonMenuItem createRadioButtonMenuItem(final String text,
            final boolean selected) {
        return new JRadioButtonMenuItem(text, selected);
    }

    public static JCheckBoxMenuItem createCheckBoxMenuItem(final String text,
            final boolean selected) {
        return new JCheckBoxMenuItem(text, selected);
    }

    /**
     * Checks and answers whether the about action has been moved to an
     * operating system specific menu, e.g. the OS X application menu.
     *
     * @return true if the about action is in an OS-specific menu
     */
    public static boolean isAboutInOSMenu() {
        return false;
    }

    /**
     * Creates and returns a JRadioButtonMenuItem
     * with the given enablement and selection state.
     *
     * @param   text        Text to show.
     * @param   enabled     Enablement state.
     * @param   selected    Selection state.
     * @return  Created element.
     */
    public static JRadioButtonMenuItem createRadioItem(final String text, final boolean enabled,
            final boolean selected) {
        JRadioButtonMenuItem item = createRadioButtonMenuItem(text, selected);
        item.setEnabled(enabled);
        item.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                JRadioButtonMenuItem source = (JRadioButtonMenuItem) e.getSource();
                source.setText(text);
            }
        });
        return item;
    }

    /**
     * Creates and returns a JCheckBoxMenuItem
     * with the given enablement and selection state.
     *
     * @param   text        Text to show.
     * @param   enabled     Enablement state.
     * @param   selected    Selection state.
     * @return  Created element.
     */
    public static JCheckBoxMenuItem createCheckItem(final String text, final boolean enabled,
            final boolean selected) {
        JCheckBoxMenuItem item = createCheckBoxMenuItem(text, selected);
        item.setEnabled(enabled);
        item.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
                source.setText(text);
            }
        });
        return item;
    }

}
