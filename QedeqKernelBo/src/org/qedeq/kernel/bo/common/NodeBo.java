/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.module.NodeVo;
import org.qedeq.kernel.se.visitor.QedeqNumbers;

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
     * Get numbers of node.
     *
     * @return  Node numbers.
     */
    public QedeqNumbers getNumbers();

    /**
     * Get module context the node is within.
     *
     * @return  The module context the node is within.
     */
    public ModuleContext getModuleContext();

    /**
     * Was this node checked successfully for formal correctness? This means checking the formal
     * syntax of the node formulas. This includes all formulas. LaTeX correctness doesn't play any
     * role. Nodes without formal formulas return always <code>true</code>.
     *
     * @return  <code>true</code> if the check was successful.
     */
    public boolean isWellFormed();

    /**
     * Was this node checked unsuccessfully for formal correctness? This means checking the formal
     * syntax of the node formulas. This includes all formulas. LaTeX correctness doesn't play any
     * role. Nodes without formal formulas return always <code>false</code>.
     *
     * @return  <code>true</code> if the check was not successful.
     */
    public boolean isNotWellFormed();

    /**
     * This means that at least one formal proof was successfully checked for correctness and a
     * rule declaration was possible.
     * If we have no proposition and no rule we always get <code>true</code>.
     *
     * @return  <code>true</code> if the check was successful.
     */
    public boolean isProved();

    /**
     * This means for propositions that at least not even one formal proof could be successfully
     * checked for correctness. For rules we get the question answered if a declaration is ok.
     * If we have no proposition and no rule we always get <code>false</code>.
     *
     * @return  <code>true</code> if the check was not successful.
     */
    public boolean isNotProved();

    /**
     * Has this node a formula?
     *
     * @return  Formula.
     */
    public boolean hasFormula();

    /**
     * Get formula of node. Can only be not <code>null</code> if this node is an Axiom,
     * PredicateDefinition, FunctionDefinition or Proposition.
     *
     * @return  Node formula.
     */
    public Element getFormula();

    /**
     * Get node.
     *
     * @return  Node.
     */
    public NodeVo getNodeVo();

}

