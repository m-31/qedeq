/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.kernel.bo.common.NodeBo;
import org.qedeq.kernel.se.base.list.ElementList;


/**
 * Check if a reference is already proved.
 *
 * @author  Michael Meyling
 */
public interface ReferenceResolver {

    /**
     * Check if a reference is a proved formula.
     *
     * @param   reference   Reference to axiom, definition or proposition.
     * @return  Reference has a proved formula.
     */
    public boolean hasProvedFormula(String reference);

    /**
     * Check if a predicate is already defined.
     *
     * @param   reference   Reference to axiom, definition or proposition.
     * @return  Already proved formula.
     */
    public ElementList getReferenceFormula(String reference);

    /**
     * Get node.
     *
     * @param   reference   Reference to any {@link NodeBo}.
     * @return  Node.
     */
    public NodeBo getNode(String reference);


}
