/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.base.module;


/**
 * A node carries mathematical knowledge.
 *
 * @version   $Revision: 1.9 $
 * @author    Michael Meyling
 */
public interface Node extends SubsectionType {

    /**
     * Get label of node. The node is referenced with this label.
     *
     * @return  Returns the label.
     */
    public String getId();

    /**
     * Get level of node. Higher levels contain additional informations.
     *
     * @return  Returns the level.
     */
    public String getLevel();

    /**
     * Set name of node. Could be used as an readable reference, e.g. "Axiom of Choice".
     *
     * @return  Returns the name.
     */
    public LatexList getName();

    /**
     * Get title of subsection.
     *
     * @return  Returns the name.
     */
    public LatexList getTitle();

    /**
     * Get text before the formula. Get the preceding LaTeX text. This text comes before a
     * theorem, definition etc. but belongs to this node regards content.
     *
     * @return  Returns the preceding LaTeX text.
     */
    public LatexList getPrecedingText();

    /**
     * Get node content. This is for example a concrete theorem or definition. The main
     * information of a node could be found here.
     *
     * @return  Returns the nodeType.
     */
    public NodeType getNodeType();

    /**
     * Get text after the formula. Get the succeeding LaTeX text. This text comes after
     * a theorem, definition etc. but belongs to this node regards content.
     *
     * @return  Returns the succeedingText.
     */
    public LatexList getSucceedingText();
}
