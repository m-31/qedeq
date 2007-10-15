/* $Id: QedeqBo.java,v 1.13 2007/08/21 21:03:30 m31 Exp $
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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.dto.module.ChapterListVo;
import org.qedeq.kernel.dto.module.ChapterVo;
import org.qedeq.kernel.dto.module.HeaderVo;
import org.qedeq.kernel.dto.module.LiteratureItemListVo;

/**
 * A complete QEDEQ module. This describes the root business object.
 *
 * TODO mime 20070704: should include the Qedeq DTO - and not have the same methods
 *
 * @version $Revision: 1.13 $
 * @author  Michael Meyling
 */
public interface QedeqBo extends Qedeq {

    /**
     * Set header of module.
     *
     * @param   header   Module header.
     */
    public void setHeader(final HeaderVo header);

    /**
     * Set chapter list of this module.
     *
     * @param   chapters    Chapter list.
     */
    public void setChapterList(final ChapterListVo chapters);

    /**
     * Add chapter to this module.
     *
     * @param   chapter Chapter to add.
     */
    public void addChapter(final ChapterVo chapter);

    /**
     * Set bibliography.
     *
     * @param   literatureItemList  Bibliography.
     */
    public void setLiteratureItemList(final LiteratureItemListVo literatureItemList);

    /**
     * Get module label associations for this module.
     *
     * @return  Label associations.
     */
    public ModuleLabels getModuleLabels();

    /**
     * Get physical addresses of this module.
     *
     * @return  Module address..
     */
    public ModuleAddress getModuleAddress();

    /**
     * Get physical addresses of this module.
     *
     * @param   address Module address.
     */
    public void setModuleAddress(final ModuleAddress address);

}
