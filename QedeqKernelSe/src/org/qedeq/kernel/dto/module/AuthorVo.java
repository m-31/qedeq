/* $Id: AuthorVo.java,v 1.7 2007/05/10 00:37:50 m31 Exp $
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

import org.qedeq.kernel.base.module.Author;
import org.qedeq.kernel.base.module.Latex;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Describes a QEDEQ module author.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public class AuthorVo implements Author {

    /** Author name. */
    private Latex name;

    /** Email address of author. */
    private String email;

    /**
     * Constructs an author.
     *
     * @param   name    Author name.
     * @param   email   Author's email address.
     */
    public AuthorVo(final LatexVo name, final String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Constructs an empty author.
     */
    public AuthorVo() {
        // nothing to do
    }

    /**
     * Set name of author.
     *
     * @param   name    Author name.
     */
    public final void setName(final LatexVo name) {
        this.name = name;
    }

    public final Latex getName() {
        return name;
    }

    /**
     * Set author's email address.
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
        if (!(obj instanceof AuthorVo)) {
            return false;
        }
        final AuthorVo other = (AuthorVo) obj;
        return  EqualsUtility.equals(getName(), other.getName())
            &&  EqualsUtility.equals(getEmail(), other.getEmail());
    }

    public int hashCode() {
        return (getName() != null ? getName().hashCode() : 0)
            ^ (getEmail() != null ? 1 ^ getEmail().hashCode() : 0);
    }

    public String toString() {
        return getName() + (getEmail() != null ? "<" + getEmail() + ">" : "");
    }

}
