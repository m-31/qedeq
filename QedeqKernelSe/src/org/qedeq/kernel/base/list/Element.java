/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.base.list;


/**
 * An element is either a list or an atom. Each list has an operator and
 * contains elements which are also elements. A list has a size and their
 * elements can be accessed by their position. An atom carries textual
 * data, has no operator and no size in the previous sense.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public interface Element {

    /**
     * Is this an atom?
     *
     * @return  <code>true</code> if this is an instance of {@link Atom}.
     */
    public boolean isAtom();

    /**
     * Return this element as an {@link Atom}.
     *
     * @return  This is an instance of {@link Atom}.
     * @throws  ClassCastException  This is no instance of {@link Atom}.
     */
    public Atom getAtom();

    /**
     * Is this an {@link ElementList}?
     *
     * @return  <code>true</code> if this is an instance of {@link ElementList}.
     */
    public boolean isList();

    /**
     * Return this element as an ElementList.
     *
     * @return  This as an instance of {@link ElementList}.
     * @throws  ClassCastException  This is no instance of {@link ElementList}.
     */
    public ElementList getList();

    /**
     * Is this object equal to the given one?
     *
     * @param   object     to compare with
     * @return  Is <code>object</code> equal to this one?
     */
    public boolean equals(Object object);

    /**
     * Calculates the hash code.
     *
     * @return  Hash code of this object
     */
    public int hashCode();

    /**
     * Returns an identical object (maybe "this").
     *
     * @return Copy of this object.
     */
    public Element copy();

    /**
     * Creates and returns a copy of this object, but
     * replaces anything that {@link #equals} <code>argument</code>
     * with a {@link #copy} of <code>replacement</code>.
     *
     * @param   search      Check for occurrence of this.
     * @param   replacement Replace with this.
     * @return  Copy with replacements.
     */
    public Element replace(Element search, Element replacement);

    /**
     * Get show this in <code>String</code> form.
     *
     * @return  Readable list.
     */
    public String toString();

}
