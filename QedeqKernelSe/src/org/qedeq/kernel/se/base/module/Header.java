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
 * Header of a QEDEQ file. The header specifies such things as the location of the file,
 * the title and abstract of that module, imports and exports.
 *
 * @author  Michael Meyling
 */
public interface Header {

    /**
     * Get module specification.
     *
     * @return  Module specification.
     */
    public Specification getSpecification();

    /**
     * Get module title.
     *
     * @return  Module title.
     */
    public LatexList getTitle();

    /**
     * Get module summary.
     *
     * @return  Module abstract.
     */
    public LatexList getSummary();

    /**
     * Get author list.
     *
     * @return  Module author list.
     */
    public AuthorList getAuthorList();

    /**
     * Get list of needed modules.
     *
     * @return  Import list.
     */
    public ImportList getImportList();

    /**
     * Get list of modules, that use this module.
     *
     * @return  Used by list.
     */
    public UsedByList getUsedByList();

    /**
     * Get email address of module administrator.
     *
     * @return  Email address of module administrator.
     */
    public String getEmail();
}
