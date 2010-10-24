/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.SystemUtils;


/**
 * Split given string into parts delimited by.
 *
 * @author  Michael Meyling
 */
public final class Splitter implements Iterator {

    final String text;

    final char delimiter;

    private int found;

    private int start;

    private String separator;

    private String token;

    /**
     * Constructor, should never be called.
     *
     * @param   text        Text to split.
     * @param   delimiter   Split at these points.
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
