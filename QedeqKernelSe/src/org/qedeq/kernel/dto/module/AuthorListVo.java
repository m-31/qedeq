/* $Id: AuthorListVo.java,v 1.9 2008/07/26 07:59:35 m31 Exp $
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

package org.qedeq.kernel.dto.module;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.base.module.Author;
import org.qedeq.kernel.base.module.AuthorList;


/**
 * List of authors.
 *
 * @version $Revision: 1.9 $
 * @author  Michael Meyling
 */
public class AuthorListVo implements AuthorList {

    /** Contains all list elements. */
    private final List list;

    /**
     * Constructs an empty list of authors.
     */
    public AuthorListVo() {
        this.list = new ArrayList();
    }

    /**
     * Add author to list.
     *
     * @param   author  Add this author.
     */
    public final void add(final AuthorVo author) {
        list.add(author);
    }

    public final int size() {
        return list.size();
    }

    public final Author get(final int index) {
        return (Author) list.get(index);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof AuthorListVo)) {
            return false;
        }
        final AuthorListVo otherList = (AuthorListVo) obj;
        if (size() != otherList.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!EqualsUtility.equals(get(i), otherList.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < size(); i++) {
            hash = hash ^ (i + 1);
            if (get(i) != null) {
                hash = hash ^ get(i).hashCode();
            }
        }
        return hash;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer("List of authors:\n");
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            buffer.append((i + 1) + ":\t");
            buffer.append(get(i) != null ? get(i).toString() : null);
        }
        return buffer.toString();
    }

}
