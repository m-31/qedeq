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

package org.qedeq.kernel.se.base.module;


/**
 * Rule.
 *
 * @author  Michael Meyling
 */
public interface Rule extends NodeType {

    /**
     * Get rule name.
     *
     * @return  Name of rule.
     */
    public String getName();

    /**
     * Get rule version.
     *
     * @return  Version of rule.
     */
    public String getVersion();

    /**
     * Get rule description.
     *
     * @return  Description.
     */
    public LatexList getDescription();

    /**
     * Get list of rules that are modified by this rule.
     *
     * @return  List of rule changes.
     */
    public ChangedRuleList getChangedRuleList();

    /**
     * Get links necessary for having this rule.
     *
     * @return  List of ids.
     */
    public LinkList getLinkList();

    /**
     * Get proofs for rule declaration.
     *
     * @return  Rule proof.
     */
    public ProofList getProofList();

}
