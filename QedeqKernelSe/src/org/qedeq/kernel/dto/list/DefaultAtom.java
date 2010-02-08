/* This file is part of the project "Hilbert II" - http://www.qedeq.org
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

package org.qedeq.kernel.dto.list;

import org.qedeq.kernel.base.list.Atom;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;


/**
 * An object of this class represents a text string.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class DefaultAtom implements Atom {

    /** The plain text. */
    private final String text;

    /**
     * Constructs an <code>Atom</code>.
     *
     * @param   text    Atom string.
     * @throws  IllegalArgumentException    <code>text</code> is a NullPointer.
     */
    public DefaultAtom(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("a NullPointer is no valid text");
        }
        this.text = text;
    }

    public final String getString() {
        return text;
    }

    public final boolean isAtom() {
        return true;
    }

    public final Atom getAtom() {
        return this;
    }

    public final boolean isList() {
        return false;
    }

    public final ElementList getList() {
        throw new ClassCastException("this is no " + ElementList.class.getName()
            + ", but a " + this.getClass().getName());
    }

    public final Element copy() {
        return new DefaultAtom(text);
    }

    public final Element replace(final Element search, final Element replacement) {
        if (this.equals(search)) {
            return replacement.copy();
        }
        return this.copy();
    }

    public final String toString() {
        StringBuffer result = new StringBuffer();
        result.append("\"");

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\"') {
                result.append("\"\"");
            } else {
                result.append(text.charAt(i));
            }
        }
        result.append('\"');
        return result.toString();

    }

    public final boolean equals(final Object object) {
        if (object instanceof DefaultAtom) {
            return ((DefaultAtom) object).text.equals(text);
        }
        return false;
    }

    public final int hashCode() {
        return toString().hashCode();
    }

}
