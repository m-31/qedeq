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

package org.qedeq.kernel.se.dto.module;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.LinkList;


/**
 * List of links. A link is just a string that identifies something.
 *
 * @author  Michael Meyling
 */
public class LinkListVo implements LinkList {

    /** Contains all list elements. */
    private final List list;

    /**
     * Constructs an empty list of links.
     */
    public LinkListVo() {
        this.list = new ArrayList();

    }

    /**
     * Add link to list.
     *
     * @param   id  Identifier to add.
     */
    public final void add(final String id) {
        list.add(id);
    }

    public final int size() {
        return list.size();
    }

    public final String get(final int index) {
        return (String) list.get(index);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof LinkListVo)) {
            return false;
        }
        final LinkListVo otherList = (LinkListVo) obj;
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
        final StringBuffer buffer = new StringBuffer("List of links:\n");
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            buffer.append((i + 1) + ":\n");
            buffer.append(get(i) != null ? get(i) : null);
        }
        return buffer.toString();
    }

}
