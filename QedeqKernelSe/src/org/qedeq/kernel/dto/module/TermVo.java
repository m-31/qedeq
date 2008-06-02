/* $Id: TermVo.java,v 1.4 2008/03/27 05:16:23 m31 Exp $
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

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.module.Term;


/**
 * Wraps a fterm. Such an object is build out of
 * {@link org.qedeq.kernel.base.list.Element}s.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public class TermVo implements Term {

    /** Formula or term. */
    private Element element;

    /**
     * Constructs a term.
     *
     * @param   element    Element that should be a term.
     */
    public TermVo(final Element element) {
        this.element = element;
    }

    /**
     * Empty constructor.
     */
    public TermVo() {
        // nothing to do
    }

    /**
     * Set term.
     *
     * @param   element Term.
     */
    public final void setElement(final Element element) {
        this.element = element;
    }

    public final Element getElement() {
        return element;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof TermVo)) {
            return false;
        }
        final TermVo other = (TermVo) obj;
        return EqualsUtility.equals(getElement(), other.getElement());
    }

    public int hashCode() {
        return (getElement() != null ? getElement().hashCode() : 0);
    }

    public String toString() {
        if (getElement() != null) {
            return getElement().toString();
        }
        return "";
    }

}
