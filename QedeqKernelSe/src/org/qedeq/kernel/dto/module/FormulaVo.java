/* $Id: FormulaVo.java,v 1.3 2007/05/10 00:37:50 m31 Exp $
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

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Wraps a formula. Such an object is build out of
 * {@link org.qedeq.kernel.base.list.Element}s.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public class FormulaVo implements Formula {

    /** Formula. */
    private Element element;

    /**
     * Constructs a formula.
     *
     * @param   element    Element that should be a formula.
     */
    public FormulaVo(final Element element) {
        this.element = element;
    }

    /**
     * Empty constructor.
     */
    public FormulaVo() {
        // nothing to do
    }

    /**
     * Set formula.
     *
     * @param   element Formula.
     */
    public final void setElement(final Element element) {
        this.element = element;
    }

    public final Element getElement() {
        return element;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof FormulaVo)) {
            return false;
        }
        final FormulaVo other = (FormulaVo) obj;
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
