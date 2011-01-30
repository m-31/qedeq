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

package org.qedeq.kernel.bo.common;

import org.qedeq.kernel.se.dto.module.NodeVo;

/**
 * Represents a node and its properties.
 *
 * @author  Michael Meyling
 */
public interface NodeBo {

    /**
     * Get parent the node is in.
     *
     * @return  Parent module for this node.
     */
    public QedeqBo getParentQedeqBo();

    /**
     * Get chapter number the node is in.
     *
     * @return  Chapter number. Returns <code>-1</code> if there is no chapter number.
     */
    public int getChapterNumber();

    /**
     * Get number of rules before this node. Including this one, if node is of that type.
     *
     * @return  Rule number. Should start with 1.
     */
    public int getRuleNumber();

    /**
     * Get number of axioms before this node. Including this one, if node is of that type.
     *
     * @return  Axiom number. Should start with 1.
     */
    public int getAxiomNumber();

    /**
     * Get number of propositions before this node. Including this one, if node is of that type.
     *
     * @return  Proposition number. Should start with 1.
     */
    public int getPropositionNumber();

    /**
     * Get number of function definitions before this node. Including this one, if node is of that type.
     *
     * @return  Function definition number. Should start with 1.
     */
    public int getFunctionDefinitionNumber();

    /**
     * Get number of predicate definitions before this node. Including this one, if node is of that type.
     *
     * @return  Predicate definition number. Should start with 1.
     */
    public int getPredicateDefinitionNumber();

    /**
     * Get node.
     *
     * @return  Node.
     */
    public NodeVo getNodeVo();

}
