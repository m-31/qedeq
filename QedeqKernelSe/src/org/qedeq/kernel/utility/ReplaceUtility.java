/* $Id: ReplaceUtility.java,v 1.7 2007/02/25 20:05:38 m31 Exp $
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

package org.qedeq.kernel.utility;


/**
 * A collection of useful static methods for string replacement.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public final class ReplaceUtility {

    /**
     * Constructor, should never be called.
     */
    private ReplaceUtility() {
        // don't call me
    }

    /**
     * Replaces all occurrences of <code>search</code> in <code>text</code>
     * by <code>replace</code> and returns the result.
     *
     * @param   text    text to work on
     * @param   search  replace this text by <code>replace</code>
     * @param   replace replacement for <code>search</code>
     * @return  resulting string
     */
    public static String replace(final String text,
            final String search, final String replace) {

        final int len = search.length();
        if (len == 0) {
            return text;
        }
        final StringBuffer result = new StringBuffer();
        int pos1 = 0;
        int pos2;
        while (0 <= (pos2 = text.indexOf(search, pos1))) {
            result.append(text.substring(pos1, pos2));
            result.append(replace);
            pos1 = pos2 + len;
        }
        if (pos1 < text.length()) {
            result.append(text.substring(pos1));
        }
        return result.toString();
    }


    /**
     * Replaces all occurrences of <code>search</code> in <code>text</code>
     * by <code>replace</code> and returns the result.
     *
     * @param   text    text to work on
     * @param   search  replace this text by <code>replace</code>
     * @param   replace replacement for <code>search</code>
     */
    public static void replace(final StringBuffer text,
            final String search, final String replace) {

        final String result = replace(text.toString(), search, replace);
        text.setLength(0);
        text.append(result);
// TODO mime 20050205: check if the above could be replaced with:
/*
        final StringBuffer result = new StringBuffer();
        int pos1 = 0;
        int pos2;
        final int len = search.length();
        while (0 <= (pos2 = text.indexOf(search, pos1))) {
            result.append(text.substring(pos1, pos2));
            result.append(replace);
            pos1 = pos2 + len;
        }
        if (pos1 < text.length()) {
            result.append(text.substring(pos1));
        }
        text.setLength(0);
        text.append(result);
  */
      }


}
