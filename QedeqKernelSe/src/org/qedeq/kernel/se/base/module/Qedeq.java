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

package org.qedeq.kernel.se.base.module;


/**
 * A complete QEDEQ module. This is the root value object.
 *
 * @author    Michael Meyling
 */
public interface Qedeq {

    /**
     * Get module header.
     *
     * @return  Module header.
     */
    public Header getHeader();

    /**
     * Get all chapters.
     *
     * @return  List of chapters.
     */
    public ChapterList getChapterList();


    /**
     * Get bibliography.
     *
     * @return  literatureItemList  Bibliography.
     */
    public LiteratureItemList getLiteratureItemList();


}
