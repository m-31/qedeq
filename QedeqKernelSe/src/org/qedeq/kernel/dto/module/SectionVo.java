/* $Id: SectionVo.java,v 1.12 2007/05/10 00:37:50 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.Section;
import org.qedeq.kernel.base.module.SubsectionList;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Section of a qedeq file.
 *
 * @version $Revision: 1.12 $
 * @author  Michael Meyling
 */
public class SectionVo implements Section {

    /** No section number. */
    private Boolean noNumber;

    /** Section title. */
    private LatexList title;

    /** Section introduction. */
    private LatexList introduction;

    /** All subsections of this section.*/
    private SubsectionList subsectionList;

    /**
     * Constructs a new section.
     */
    public SectionVo() {
        // nothing to do
    }

    /**
     * Set no auto numbering for this section.
     *
     * @param   noNumber    Should this section be not numbered?
     */
    public final void setNoNumber(final Boolean noNumber) {
        this.noNumber = noNumber;
    }

    public final Boolean getNoNumber() {
        return noNumber;
    }

    /**
     * Set section title.
     *
     * @param   title   Section title.
     */
    public final void setTitle(final LatexListVo title) {
        this.title = title;
    }

    public final LatexList getTitle() {
        return title;
    }

    /**
     * Set LaTeX introduction text.
     *
     * @param   introduction    Introduction text.
     */
    public final void setIntroduction(final LatexListVo introduction) {
        this.introduction = introduction;
    }

    public final LatexList getIntroduction() {
        return introduction;
    }

    /**
     * Set list of subsections of this section.
     *
     * @param   subsections List of subsections.
     */
    public final void setSubsectionList(final SubsectionListVo subsections) {
        this.subsectionList = subsections;
    }

    public final SubsectionList getSubsectionList() {
        return subsectionList;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof SectionVo)) {
            return false;
        }
        final SectionVo other = (SectionVo) obj;
        return EqualsUtility.equals(getNoNumber(), other.getNoNumber())
            && EqualsUtility.equals(getTitle(), other.getTitle())
            && EqualsUtility.equals(getIntroduction(), other.getIntroduction())
            && EqualsUtility.equals(getSubsectionList(), other.getSubsectionList());
    }

    public int hashCode() {
        return (getNoNumber() != null ? getNoNumber().hashCode() : 0)
            ^ (getTitle() != null ? 1 ^ getTitle().hashCode() : 0)
            ^ (getIntroduction() != null ? 2 ^ getIntroduction().hashCode() : 0)
            ^ (getSubsectionList() != null ? 3 ^ getSubsectionList().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Section noNumbering: " + noNumber + "\n");
        buffer.append("Section Title: \n");
        buffer.append(getTitle() + "\n\n");
        buffer.append("Introduction: ");
        buffer.append(getIntroduction() + "\n\n");
        buffer.append(getSubsectionList() + "\n");
        return buffer.toString();
    }

}
