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

package org.qedeq.kernel.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.base.module.AuthorList;
import org.qedeq.kernel.base.module.Header;
import org.qedeq.kernel.base.module.ImportList;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.base.module.UsedByList;


/**
 * Header of a qedeq file. The header specifies such things as the location of the file,
 * the title and abstract of that module, imports and exports.
 *
 * @version $Revision: 1.9 $
 * @author  Michael Meyling
 */
public class HeaderVo implements Header {

    /** Module specification. */
    private Specification specification;

    /** Module title. */
    private LatexList title;

    /** Module "abstract". */
    private LatexList summary;

    /** Author list. */
    private AuthorList authorList;

    /** List of imported modules. */
    private ImportList importList;

    /** List of modules that depend on current one. */
    private UsedByList usedByList;

    /** Email address of module administrator. */
    private String email;

    /**
     * Constructs a new module header.
     */
    public HeaderVo() {
        // nothing to do
    }

    /**
     * Set module specification.
     * @param   specification  Module specification.
     */
    public final void setSpecification(final SpecificationVo specification) {
        this.specification = specification;
    }

    public final Specification getSpecification() {
        return specification;
    }

    /**
     * Set module title.
     *
     * @param   title   Module title texts.
     */
    public final void setTitle(final LatexListVo title) {
        this.title = title;
    }

    public final LatexList getTitle() {
        return title;
    }

    /**
     * Set module summary text.
     *
     * @param   summary Module summary text.
     */
    public final void setSummary(final LatexListVo summary) {
        this.summary = summary;
    }

    public final LatexList getSummary() {
        return summary;
    }

    /**
     * Set list of authors of this module.
     *
     * @param   authors Author list.
     */
    public final void setAuthorList(final AuthorListVo authors) {
        this.authorList = authors;
    }

    public final AuthorList getAuthorList() {
        return authorList;
    }

    /**
     * Set list of imports needed for this module.
     *
     * @param   imports Module import list.
     */
    public final void setImportList(final ImportListVo imports) {
        this.importList = imports;
    }

    public final ImportList getImportList() {
        return importList;
    }

    /**
     * Set list of known modules that need this module.
     *
     * @param   usedby  List of modules.
     */
    public final void setUsedByList(final UsedByListVo usedby) {
        this.usedByList = usedby;
    }

    public final UsedByList getUsedByList() {
        return usedByList;
    }

    /**
     * Set email address of module administrator.
     *
     * @param   email   Email address.
     */
    public final void setEmail(final String email) {
        this.email = email;
    }

    public final String getEmail() {
        return email;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof HeaderVo)) {
            return false;
        }
        final HeaderVo other = (HeaderVo) obj;
        return  EqualsUtility.equals(getSpecification(), other.getSpecification())
            && EqualsUtility.equals(getTitle(), other.getTitle())
            && EqualsUtility.equals(getSummary(), other.getSummary())
            && EqualsUtility.equals(getAuthorList(), other.getAuthorList())
            && EqualsUtility.equals(getImportList(), other.getImportList())
            && EqualsUtility.equals(getUsedByList(), other.getUsedByList())
            && EqualsUtility.equals(getEmail(), other.getEmail());
    }

    public int hashCode() {
        return (getSpecification() != null ? getSpecification().hashCode() : 0)
            ^ (getTitle() != null ? 1 ^ getTitle().hashCode() : 0)
            ^ (getSummary() != null ? 2 ^ getSummary().hashCode() : 0)
            ^ (getAuthorList() != null ? 3 ^ getAuthorList().hashCode() : 0)
            ^ (getImportList() != null ? 4 ^ getImportList().hashCode() : 0)
            ^ (getUsedByList() != null ? 5 ^ getUsedByList().hashCode() : 0)
            ^ (getEmail() != null ? 6 ^ getEmail().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer("Header\n");
        buffer.append(getSpecification() + "\n");
        buffer.append("Title: ");
        buffer.append(getTitle() + "\n\n");
        buffer.append("Abstract: ");
        buffer.append(getSummary() + "\n\n");
        buffer.append(getAuthorList() + "\n");
        buffer.append(getImportList() + "\n");
        buffer.append(getUsedByList() + "\n");
        if (getEmail() != null) {
            buffer.append("\nModule email: <" + getEmail() + ">");
        }
        return buffer.toString();
    }

}
