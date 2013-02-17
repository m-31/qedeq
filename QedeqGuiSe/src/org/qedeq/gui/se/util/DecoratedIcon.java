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

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * Stack various icons. This enables us to decorate a basic icon.
 *
 * @author  Michael Meyling
 */
public class DecoratedIcon extends ImageIcon {

    /** This is the basic icon. */
    private ImageIcon basic;

    /** List of decorator icons. */
    private List overlays;

    /**
     * Constructor.
     *
     * @param   basic   Basic icon.
     */
    public DecoratedIcon(final ImageIcon basic) {
        super(basic.getImage());
        this.basic = basic;
        this.overlays = new ArrayList();
    }

    /**
     * Decorate basic icon with this one.
     *
     * @param   decorator   Decorator icon.
     */
    public void decorate(final ImageIcon decorator) {
        overlays.add(decorator);
    }

    public synchronized void paintIcon(final Component c, final Graphics g, final int x,
            final int y) {
        basic.paintIcon(c, g, x, y);
        for (int i = 0; i < overlays.size(); i++) {
            final ImageIcon icon = (ImageIcon) overlays.get(i);
            icon.paintIcon(c, g, x, y);
        }
    }

}
