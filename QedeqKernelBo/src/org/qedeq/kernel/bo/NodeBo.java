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

package org.qedeq.kernel.bo;

import org.qedeq.kernel.base.module.NodeType;
import org.qedeq.kernel.dto.module.NodeVo;


/**
 * Represents a module and its states.
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
     * Get rule number if the {@link NodeType} is accordingly. Throws {@link UnsupportedOperationException}
     *
     * @return  Rule number. Should start with 1.
     * @throws  UnsupportedOperationException   This is no rule node.
     */
    public int getRuleNumber();

    /**
     * Get axiom number if the {@link NodeType} is accordingly.
     * Throws {@link UnsupportedOperationException}
     *
     * @return  Axiom number. Should start with 1.
     * @throws  UnsupportedOperationException   This is no axiom node.
     */
    public int getAxiomNumber();

    /**
     * Get proposition number if the {@link NodeType} is accordingly.
     * Throws {@link UnsupportedOperationException}.
     *
     * @return  Proposition number. Should start with 1.
     * @throws  UnsupportedOperationException   This is no proposition node.
     */
    public int getPropositionNumber();

    /**
     * Get function definition number if the {@link NodeType} is accordingly.
     * Throws {@link UnsupportedOperationException}.
     *
     * @return  Function definition number. Should start with 1.
     * @throws  UnsupportedOperationException   This is no function definition node.
     */
    public int getFunctionDefinitionNumber();

    /**
     * Get predicate definition number if the {@link NodeType} is accordingly.
     * Throws {@link UnsupportedOperationException}.
     *
     * @return  Predicate definition number. Should start with 1.
     * @throws  UnsupportedOperationException   This is no function definition node.
     */
    public int getPredicateDefinitionNumber();

    /**
     * Get node.
     *
     * @return  Node.
     */
    public NodeVo getNodeVo();

}
