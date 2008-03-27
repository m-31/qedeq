/* $Id: LiteratureItemListVo.java,v 1.5 2008/03/27 05:16:23 m31 Exp $
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

package org.qedeq.kernel.dto.module;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.base.module.LiteratureItem;
import org.qedeq.kernel.base.module.LiteratureItemList;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * List of literature references.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class LiteratureItemListVo implements LiteratureItemList {

    /** Contains all list elements. */
    private final List list;

    /**
     * Constructs an empty list of authors.
     */
    public LiteratureItemListVo() {
        this.list = new ArrayList();

    }

    /**
     * Add literature reference to list.
     *
     * @param   item    Add this reference.
     */
    public final void add(final LiteratureItemVo item) {
        list.add(item);
    }

    public final int size() {
        return list.size();
    }

    public final LiteratureItem get(final int index) {
        return (LiteratureItem) list.get(index);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof LiteratureItemListVo)) {
            return false;
        }
        final LiteratureItemListVo otherList = (LiteratureItemListVo) obj;
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
        final StringBuffer buffer = new StringBuffer("Bibliography:\n");
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
