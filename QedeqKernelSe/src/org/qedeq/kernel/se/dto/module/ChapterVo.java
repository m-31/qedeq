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
import org.qedeq.kernel.se.base.module.Chapter;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.SectionList;


/**
 * Chapter.
 *
 * @version $Revision: 1.11 $
 * @author  Michael Meyling
 */
public class ChapterVo implements Chapter {

    /** Chapter title. */
    private LatexList title;

    /** No chapter number. */
    private Boolean noNumber;

    /** Chapter introduction. */
    private LatexList introduction;

    /** List of chapter sections. */
    private SectionListVo sectionList;

    /**
     * Constructs a new chapter.
     */
    public ChapterVo() {
        // nothing to do
    }

    /**
     * Set automatic chapter number off or on.
     *
     * @param   noNumber    No chapter numbering?
     */
    public final void setNoNumber(final Boolean noNumber) {
        this.noNumber = noNumber;
    }

    public final Boolean getNoNumber() {
        return noNumber;
    }

    /**
     * Set chapter title.
     *
     * @param   title   LaTeX list of chapter titles.
     */
    public final void setTitle(final LatexListVo title) {
        this.title = title;
    }

    public final LatexList getTitle() {
        return title;
    }

    /**
     * Set chapter introduction text.
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
     * Set list of sections.
     *
     * @param   sections    Section list.
     */
    public final void setSectionList(final SectionListVo sections) {
        this.sectionList = sections;
    }

    public final SectionList getSectionList() {
        return sectionList;
    }

    /**
     * Add section to list.
     *
     * @param   section Section to add.
     */
    public final void addSection(final SectionVo section) {
        if (sectionList == null) {
            sectionList = new SectionListVo();
        }
        sectionList.add(section);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ChapterVo)) {
            return false;
        }
        final ChapterVo other = (ChapterVo) obj;
        return EqualsUtility.equals(getNoNumber(), other.getNoNumber())
            && EqualsUtility.equals(getTitle(), other.getTitle())
            && EqualsUtility.equals(getIntroduction(), other.getIntroduction())
            && EqualsUtility.equals(getSectionList(), other.getSectionList());
    }

    public int hashCode() {
        return (getNoNumber() != null ? getNoNumber().hashCode() : 0)
            ^ (getTitle() != null ? getTitle().hashCode() : 0)
            ^ (getIntroduction() != null ? 1 ^ getIntroduction().hashCode() : 0)
            ^ (getSectionList() != null ? 2 ^ getSectionList().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Chapter noNumber: " + getNoNumber() + "\n");
        buffer.append("Chapter Title:\n");
        buffer.append(getTitle() + "\n\n");
        buffer.append("Introduction:\n");
        buffer.append(getIntroduction() + "\n\n");
        buffer.append(getSectionList() + "\n");
        return buffer.toString();
    }

}
