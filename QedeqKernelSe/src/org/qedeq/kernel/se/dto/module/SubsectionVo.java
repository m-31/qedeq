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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.Subsection;


/**
 * Subsection of a qedeq file.
 *
 * @version $Revision: 1.9 $
 * @author  Michael Meyling
 */
public class SubsectionVo implements Subsection {

    /** Label for referencing. */
    private String id;

    /** Level of that subsection. Higher levels contain additional informations. */
    private String level;

    /** Title of this subsection. */
    private LatexList title;

    /** LaTeX text.  */
    private LatexList latex;

    /**
     * Constructs a new empty subsection.
     */
    public SubsectionVo() {
        // nothing to do
    }

    /**
     * Set label for this subsection.
     *
     * @param   label   Label for referencing.
     */
    public final void setId(final String label) {
        this.id = label;
    }

    public final String getId() {
        return id;
    }

    /**
     * Set level for this section. Higher levels contain additional informations
     *
     * @param   level   Level of that subsection.
     */
    public final void setLevel(final String level) {
        this.level = level;
    }

    public final String getLevel() {
        return level;
    }

    /**
     * Set title of this subsection.
     *
     * @param   title   Subsection title.
     */
    public final void setTitle(final LatexListVo title) {
        this.title = title;
    }

    public final LatexList getTitle() {
        return title;
    }

    /**
     * Set LaTeX text for this subsection.
     *
     * @param   latexText   LaTeX text.
     */
    public final void setLatex(final LatexListVo latexText) {
        this.latex = latexText;
    }

    public final LatexList getLatex() {
        return latex;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof SubsectionVo)) {
            return false;
        }
        final SubsectionVo subsection = (SubsectionVo) obj;
        return  EqualsUtility.equals(getId(), subsection.getId())
            &&  EqualsUtility.equals(getLevel(), subsection.getLevel())
            &&  EqualsUtility.equals(getTitle(), subsection.getTitle())
            &&  EqualsUtility.equals(getLatex(), subsection.getLatex());
    }

    public int hashCode() {
        return (getId() != null ? getId().hashCode() : 0)
            ^ (getLevel() != null ? 1 ^ getLevel().hashCode() : 0)
            ^ (getTitle() != null ? 2 ^ getTitle().hashCode() : 0)
            ^ (getLatex() != null ? 3 ^ getLatex().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Subsection\t");
        buffer.append("Label: " + getId());
        buffer.append("\tLevel: " + getLevel() + "\n");
        buffer.append("Title: ");
        buffer.append(getTitle() + "\n\n");
        buffer.append("Pre: ");
        buffer.append(getLatex() + "\n\n");
        return buffer.toString();
    }

}
