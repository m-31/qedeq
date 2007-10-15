/* $Id: DefaultQedeqBo.java,v 1.1 2007/05/10 00:37:51 m31 Exp $
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

package org.qedeq.kernel.bo.load;

import org.qedeq.kernel.base.module.ChapterList;
import org.qedeq.kernel.base.module.Header;
import org.qedeq.kernel.base.module.LiteratureItemList;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.dto.module.ChapterListVo;
import org.qedeq.kernel.dto.module.ChapterVo;
import org.qedeq.kernel.dto.module.HeaderVo;
import org.qedeq.kernel.dto.module.LiteratureItemListVo;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * A complete QEDEQ module. This is the root business object.
 *
 * TODO mime 20070131: shouldn't be a globalContext also an attribute of this class?
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class DefaultQedeqBo implements QedeqBo {

    /** Header of module. */
    private HeaderVo header;

    /** All module chapters. */
    private ChapterListVo chapterList;

    /** All module labels and their business objects. */
    private final ModuleLabels moduleLabels;

    /** Bibliography. */
    private LiteratureItemList literatureItemList;

    /** Module address information. */
    private ModuleAddress moduleAddress;

    /**
     * Constructs a new empty QEDEQ module.
     */
    public DefaultQedeqBo() {
        moduleLabels = new ModuleLabels();
    }

    /**
     * Set header of module.
     *
     * @param   header   Module header.
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

    /**
     * Set bibliography.
     *
     * @param   literatureItemList  Bibliography.
     */
    public void setLiteratureItemList(final LiteratureItemListVo literatureItemList) {
        this.literatureItemList = literatureItemList;
    }

    public LiteratureItemList getLiteratureItemList() {
        return literatureItemList;
    }

    /**
     * Get module label associations for this module.
     *
     * @return  Label associations.
     */
    public final ModuleLabels getModuleLabels() {
        return moduleLabels;
    }

    /**
     * Get physical addresses of this module.
     *
     * @return  Module address..
     */
    public final ModuleAddress getModuleAddress() {
        return moduleAddress;
    }

    /**
     * Get physical addresses of this module.
     *
     * @param   address Module address.
     */
    public final void setModuleAddress(final ModuleAddress address) {
        this.moduleAddress = address;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof DefaultQedeqBo)) {
            return false;
        }
        final DefaultQedeqBo other = (DefaultQedeqBo) obj;
        return  EqualsUtility.equals(getHeader(), other.getHeader())
            &&  EqualsUtility.equals(getChapterList(), other.getChapterList())
            &&  EqualsUtility.equals(getLiteratureItemList(), other.getLiteratureItemList())
            &&  EqualsUtility.equals(getModuleAddress(), other.getModuleAddress());
    }

    public int hashCode() {
        return (getHeader() != null ? getHeader().hashCode() : 0)
            ^ (getChapterList() != null ? 1 ^ getChapterList().hashCode() : 0)
            ^ (getLiteratureItemList() != null ? 2 ^ getLiteratureItemList().hashCode() : 0)
            ^ (getModuleAddress() != null ? 3 ^ getModuleAddress().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getModuleAddress() + "\n");
        buffer.append(getHeader() + "\n\n");
        buffer.append(getChapterList() + "\n\n");
        buffer.append(getLiteratureItemList());
        return buffer.toString();
    }

}
