/* $Id: LiteratureItemVo.java,v 1.4 2007/05/10 00:37:50 m31 Exp $
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

import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.LiteratureItem;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Single literature reference.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public class LiteratureItemVo implements LiteratureItem {

    /** Reference to this object with this label. */
    private String label;

    /** Reference description. */
    private LatexList item;

    /**
     * Constructs a new section.
     */
    public LiteratureItemVo() {
        // nothing to do
    }

    /**
     * Set reference label for this literature reference.
     *
     * @param   label   Reference to this object with this label.
     */
    public final void setLabel(final String label) {
        this.label = label;
    }

    public final String getLabel() {
        return label;
    }

    /**
     * Set literature reference for this item.
     *
     * @param   item   literature reference.
     */
    public final void setItem(final LatexListVo item) {
        this.item = item;
    }

    public final LatexList getItem() {
        return item;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof LiteratureItemVo)) {
            return false;
        }
        final LiteratureItemVo other = (LiteratureItemVo) obj;
        return EqualsUtility.equals(getLabel(), other.getLabel())
            && EqualsUtility.equals(getItem(), other.getItem());
    }

    public int hashCode() {
        return (getLabel() != null ? getLabel().hashCode() : 0)
            ^ (getItem() != null ? 3 ^ getItem().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Item label: " + label + "\n");
        buffer.append(getItem() + "\n");
        return buffer.toString();
    }

}
