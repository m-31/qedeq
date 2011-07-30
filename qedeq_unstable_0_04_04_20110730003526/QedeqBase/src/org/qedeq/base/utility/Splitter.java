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

package org.qedeq.base.utility;

import java.util.Iterator;


/**
 * Split given string into parts delimited by space.
 *
 * @author  Michael Meyling
 */
public final class Splitter implements Iterator {

    /** Text to split. */
    private final String text;

    /** Separating delimiter. */
    private final char delimiter;

    /** Last found position. */
    private int found;

    /** Start searching from here. */
    private int start;

    /** Current found separator string. Consists of (perhaps several) delimiters. */
    private String separator;

    /** Current found token. */
    private String token;

    /**
     * Constructor, should never be called.
     *
     * @param   text        Text to split.
     */
    public Splitter(final String text) {
        this.text = text;
        delimiter = ' ';
        iterate();
    }

    /**
     * Split String by given delimiter.
     * "a b c" is converted to "a", " ", "b", " ", "c".
     * "a b c " is converted to "a", " ", "b", " ", "c", " ".
     *
     * @return  Split text.
     */
    public Object next() {
        return nextToken();
    }

    /**
     * Qualified next method.
     *
     * @return  Get next token.
     */
    public String nextToken() {
        if (token != null) {
            final String result = token;
            token = null;
            return result;
        }
        if (separator != null) {
            final String result = separator;
            separator = null;
            iterate();
            return result;
        }
        return null;
    }

    /**
     * Iterate token and whitespace.
     */
    private void iterate() {
        found = text.indexOf(delimiter, start);
        if (-1 < found) {
            token = text.substring(start, found);
            start = found;
            while (found < text.length() && text.charAt(found) == ' ') {
                found++;
            }
            separator = text.substring(start, found);
            start = found;
            if (found >= text.length()) {
                found = -1;
            }
        } else {
            separator = null;
            token = text.substring(start);
        }
        if (token.length() == 0) {
            token = null;
        }
    }

    public boolean hasNext() {
        return token != null  || separator != null;
    }


    public void remove() {
        throw new UnsupportedOperationException("this iterator doesn't support this method");
    }

}
