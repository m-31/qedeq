/* $Id: QedeqVo.java,v 1.9 2008/03/27 05:16:23 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.base.module.ChapterList;
import org.qedeq.kernel.base.module.Header;
import org.qedeq.kernel.base.module.LiteratureItemList;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * A complete qedeq module. This is the root value object.
 *
 * @version $Revision: 1.9 $
 * @author    Michael Meyling
 */
public class QedeqVo implements Qedeq {

    /** Header of module. */
    private Header header;

    /** All module chapters. */
    private ChapterListVo chapterList;

    /** Bibliography. */
    private LiteratureItemList literatureItemList;

    /**
     * Constructs a new empty qedeq module.
     */
    public QedeqVo() {
        // nothing to do
    }

    /**
     * Set header for this module.
     *
     * @param   header  Module header.
     */
    public final void setHeader(final HeaderVo header) {
        this.header = header;
    }

    public final Header getHeader() {
        return header;
    }

    /**
     * Set chapter list of this module.
     *
     * @param   chapters    Chapter list.
     */
    public final void setChapterList(final ChapterListVo chapters) {
        this.chapterList = chapters;
    }

    public final ChapterList getChapterList() {
        return chapterList;
    }

    /**
     * Add chapter to this module.
     *
     * @param   chapter Chapter to add.
     */
    public final void addChapter(final ChapterVo chapter) {
        if (chapterList == null) {
            chapterList = new ChapterListVo();
        }
        chapterList.add(chapter);
    }

    public LiteratureItemList getLiteratureItemList() {
        return literatureItemList;
    }

    /**
     * Set bibliography.
     *
     * @param   literatureItemList  Bibliography.
     */
    public void setLiteratureItemList(final LiteratureItemListVo literatureItemList) {
        this.literatureItemList = literatureItemList;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof QedeqVo)) {
            return false;
        }
        final QedeqVo other = (QedeqVo) obj;
        return  EqualsUtility.equals(getHeader(), other.getHeader())
            &&  EqualsUtility.equals(getChapterList(), other.getChapterList())
            &&  EqualsUtility.equals(getLiteratureItemList(), other.getLiteratureItemList());
    }

    public int hashCode() {
        return (getHeader() != null ? getHeader().hashCode() : 0)
            ^ (getChapterList() != null ? 1 ^ getChapterList().hashCode() : 0)
            ^ (getLiteratureItemList() != null ? 2 ^ getLiteratureItemList().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getHeader() + "\n\n");
        buffer.append(getChapterList() + "\n\n");
        buffer.append(getLiteratureItemList());
        return buffer.toString();
    }

}
