/* $Id: LatexVo.java,v 1.10 2008/07/26 07:59:35 m31 Exp $
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

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.base.module.Latex;


/**
 * LaTeX text part.
 *
 * @version $Revision: 1.10 $
 * @author  Michael Meyling
 */
public class LatexVo implements Latex {

    /** Text language. */
    private String language;

    /** LaTeX text. */
    private String latex;

    /**
     * Constructs a LaTeX text part.
     *
     * @param language  Language of this part.
     * @param latex     LaTeX text.
     */
    public LatexVo(final String language, final String latex) {
        this.language = language;
        this.latex = latex;
    }

    /**
     * Constructs an empty LaTeX text part.
     */
    public LatexVo() {
        // nothing to do
    }

    /**
     * Set text language. Examples are <code>en</code>, <code>de</code>.
     *
     * @param   language    Language.
     */
    public final void setLanguage(final String language) {
        this.language = language;
    }

    public final String getLanguage() {
        return language;
    }

    /**
     * Set LaTeX text.
     *
     * @param   latex   LaTeX text.
     */
    public final void setLatex(final String latex) {
        this.latex = latex;
    }

    public final String getLatex() {
        return latex;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof LatexVo)) {
            return false;
        }
        final LatexVo other = (LatexVo) obj;
        return  EqualsUtility.equals(getLanguage(), other.getLanguage())
            &&  EqualsUtility.equals(getLatex(), other.getLatex());
    }

    public int hashCode() {
        return (getLanguage() != null ? getLanguage().hashCode() : 0)
            ^ (getLatex() != null ? 1 ^ getLatex().hashCode() : 0);
    }

    public String toString() {
        return "\"" + getLanguage() + "\":" + getLatex();
    }

}
