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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.base.module.NodeType;


/**
 * Special subsection of a QEDEQ file.
 *
 * @author  Michael Meyling
 */
public class NodeVo implements Node {

    /** Module unique label for referencing. */
    private String id;

    /** Level of that node. Higher levels contain additional informations. */
    private String level;

    /** Node name. Could be used as an readable reference, e.g. "Axiom of Choice". */
    private LatexList name;

    /** Title of this subsection. */
    private LatexList title;

    /** Preceding LaTeX text. This text comes before a theorem, definition etc.
     * but belongs to this node regards content. */
    private LatexList precedingText;

    /** Contains the concrete theorem or definition or else. */
    private NodeType nodeType;

    /** Succeeding LaTeX text. This text comes after a theorem, definition etc.
     * but belongs to this node regards content. */
    private LatexList succeedingText;

    /**
     * Constructs a new empty node.
     */
    public NodeVo() {
        // nothing to do
    }

    /**
     * Set label for this node. Referencing must use this label.
     *
     * @param   id  Label for referencing.
     */
    public final void setId(final String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }

    /**
     * Set node level.
     *
     * @param   level   Level of this node.
     */
    public final void setLevel(final String level) {
        this.level = level;
    }

    public final String getLevel() {
        return level;
    }

    /**
     * Set node name. Could be used as an readable reference, e.g. "Axiom of Choice".
     *
     * @param   name    Name of this node.
     */
    public final void setName(final LatexListVo name) {
        this.name = name;
    }

    public final LatexList getName() {
        return name;
    }

    /**
     * Set node title.
     *
     * @param   title   Title of node.
     */
    public final void setTitle(final LatexListVo title) {
        this.title = title;
    }

    public final LatexList getTitle() {
        return title;
    }

    /**
     * Set preceding LaTeX text. This text comes before a theorem, definition etc.
     * but belongs to this node regards content.
     *
     * @param   precedingText   Preceding LaTeX text.
     */
    public final void setPrecedingText(final LatexListVo precedingText) {
        this.precedingText = precedingText;
    }

    public final LatexList getPrecedingText() {
        return precedingText;
    }

    /**
     * Set the concrete theorem or definition or else.
     *
     * @param   nodeType    An instance of {@link NodeType}.
     */
    public final void setNodeType(final NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public final NodeType getNodeType() {
        return nodeType;
    }

    /**
     * Set succeeding LaTeX text. This text comes after a theorem, definition etc.
     * but belongs to this node regards content.
     *
     * @param   succeedingText  Succeeding LaTeX text.
     */
    public final void setSucceedingText(final LatexListVo succeedingText) {
        this.succeedingText = succeedingText;
    }

    public final LatexList getSucceedingText() {
        return succeedingText;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof NodeVo)) {
            return false;
        }
        final NodeVo node = (NodeVo) obj;
        return  EqualsUtility.equals(getId(), node.getId())
            &&  EqualsUtility.equals(getLevel(), node.getLevel())
            &&  EqualsUtility.equals(getName(), node.getName())
            &&  EqualsUtility.equals(getTitle(), node.getTitle())
            &&  EqualsUtility.equals(getPrecedingText(), node.getPrecedingText())
            &&  EqualsUtility.equals(getNodeType(), node.getNodeType())
            &&  EqualsUtility.equals(getSucceedingText(), node.getSucceedingText());
    }

    public int hashCode() {
        return (getId() != null ? getId().hashCode() : 0)
            ^ (getLevel() != null ? 1 ^ getLevel().hashCode() : 0)
            ^ (getName() != null ? 2 ^ getName().hashCode() : 0)
            ^ (getTitle() != null ? 3 ^ getTitle().hashCode() : 0)
            ^ (getPrecedingText() != null ? 4 ^ getPrecedingText().hashCode() : 0)
            ^ (getNodeType() != null ? 5 ^ getNodeType().hashCode() : 0)
            ^ (getSucceedingText() != null ? 6 ^ getSucceedingText().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Node\t");
        buffer.append("Id: " + getId());
        buffer.append("\tLevel: " + getLevel() + "\n");
        buffer.append("Name: ");
        buffer.append(getName() + "\n\n");
        buffer.append("Title: ");
        buffer.append(getTitle() + "\n\n");
        buffer.append("Pre: ");
        buffer.append(getPrecedingText() + "\n\n");
        buffer.append("NodeType: ");
        buffer.append(getNodeType() + "\n\n");
        buffer.append("Suc: ");
        buffer.append(getSucceedingText() + "\n\n");
        return buffer.toString();
    }

}
