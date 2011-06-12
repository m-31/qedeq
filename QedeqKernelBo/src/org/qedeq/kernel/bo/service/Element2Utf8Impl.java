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

package org.qedeq.kernel.bo.service;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.service.unicode.Latex2UnicodeParser;
import org.qedeq.kernel.se.base.list.Element;


/**
 * Transfer a QEDEQ formulas into UTF-8 text.
 *
 * @author  Michael Meyling
 */
public final class Element2Utf8Impl implements Element2Utf8 {

    /** We use this converter. */
    private Element2LatexImpl converter;

    /**
     * Constructor.
     *
     * @param   converter   This converter can produce at least LaTeX.
     */
    public Element2Utf8Impl(final Element2LatexImpl converter) {
        this.converter = converter;
    }

    public String getUtf8(final Element element) {
        return getUtf8(element, 0)[0];
    }

    public String[] getUtf8(final Element element, final int maxCols) {
        final String result = Latex2UnicodeParser.transform(null, converter.getLatex(element), 0);
        if (maxCols <= 0 || result.length()  < maxCols) {
            return new String[] {result};
        }
        final List list = new ArrayList();
        int index = 0;
        while (index < result.length()) {
            list.add(StringUtility.substring(result, index, maxCols));
            index += maxCols;
        }
        return (String[]) list.toArray(new String[] {});
    }

}
