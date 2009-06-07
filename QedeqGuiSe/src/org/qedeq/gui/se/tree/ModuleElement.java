/* $Id: ModuleElement.java,v 1.3 2008/03/27 05:14:03 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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


/**
 * Some kind of Element of a Module.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public final class ModuleElement {

    /** Is this an {@link #ATOM}? */
    private boolean atom = false;

    /** Name of the group this element belongs to. */
    private String listName;

    /** Name of this element.*/
    private String name;

    /**
     * Constructs a new ModuleElement object.
     *
     */
    public ModuleElement() {

        this.name = null;
        this.listName = null;
        this.atom = false;

    }

    /**
     * Constructs a new ModuleElement object.
     *
     * @param   name    name of this element
     * @param   listName   element belongs to this list
     * @param   atom    is this element an atom?
     */
    public ModuleElement(final String name, final String listName,
            final boolean atom) {
        this.name = name;
        this.listName = listName;
        this.atom = atom;
    }

    /**
     * @return
     */
    public final String getName() {
        return name;
    }

    /**
     *
     * @param
     * @return
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * @param
     * @return
     */
    public final String toString() {
        return super.toString() + ":" + listName + ";" + name + ";" + atom;
    }

}
