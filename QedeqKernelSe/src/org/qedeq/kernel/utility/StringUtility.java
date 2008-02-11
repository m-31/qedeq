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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


/**
 * A collection of useful static methods for strings.
 *
 * LATER mime 20070101: use StringBuilder instead of StringBuffer if working under JDK 1.5
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public final class StringUtility {

    /**
     * Constructor, should never be called.
     */
    private StringUtility() {
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

    /**
     * Quotes a <code>String</code>. A a quote character &quot; is appended at the
     * beginning and the end of the <code>String</code>. If a quote character occurs
     * within the string it is replaced by two quotes.
     *
     * @param   unquoted    the unquoted <code>String</code>
     * @return  quoted <code>String</code>
     * @throws  NullPointerException if <code>unquoted == null</code>
     */
    public static String quote(final String unquoted) {
        String result = "\"";

        for (int i = 0; i < unquoted.length(); i++) {
            if (unquoted.charAt(i) == '\"') {
                result += "\"\"";
            } else {
                result += unquoted.charAt(i);
            }
        }
        result += '\"';
        return result;
    }

    /**
     * Tests if given <code>String</code> begins with a letter and contains
     * only letters and digits.
     *
     * @param   text    test this
     * @return  is <code>text</code> only made of letters and digits and has
     *          a leading letter?
     * @throws  NullPointerException if <code>text == null</code>
     */
    public static boolean isLetterDigitString(final String text) {
        if (text.length() <= 0) {
            return false;
        }
        if (!Character.isLetter(text.charAt(0))) {
            return false;
        }
        for (int i = 1; i < text.length(); i++) {
            if (!Character.isLetterOrDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get amount of spaces.
     *
     * @param   length  number of spaces
     * @return  String contains exactly <code>number</code> spaces
     */
    public static StringBuffer getSpaces(final int length) {
        final StringBuffer buffer = new StringBuffer(length >= 0 ? length : 0);
        for (int i = 0; i < length; i++) {
            buffer.append(' ');
        }
        return buffer;
    }

    /**
     * Get non qualified class name.
     *
     * @param   clazz   Class.
     * @return  Non qualified class name.
     */
    public static String getClassName(final Class clazz) {
        return clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
    }

    /**
     * Search for first line followed by whitespace and delete this string within the whole
     * text.
     * <p>
     * For example the following text
     *<pre>
     *       Do you know the muffin man,
     *       The muffin man, the muffin man,
     *       Do you know the muffin man,
     *       Who lives on Drury Lane?
     *</pre>
     * will be converted into:
     *<pre>
     *Do you know the muffin man,
     *The muffin man, the muffin man,
     *Do you know the muffin man,
     *Who lives on Drury Lane?
     *</pre>
     *
     * @param   buffer  Work on this text.
     */
    public static void deleteLineLeadingWhitespace(final StringBuffer buffer) {
        int start = -1;
        while (0 <= (start = buffer.indexOf("\n", start + 1))) {
            if (start + 1 < buffer.length() && '\n' != buffer.charAt(start + 1)) {
                break;
            }
        }
        if (start >= 0) {
            int next = start + 1;
            while (next < buffer.length() && Character.isWhitespace(buffer.charAt(next))
                    && '\n' != buffer.charAt(next)) {
                next++;
            }
            final String empty = buffer.substring(start, next);
            if (empty.length() > 0) {
                replace(buffer, empty, "\n");
            }
        }
    }

    /**
     * Return a String like it appears in an property file. Thus certain characters are escaped.
     *
     * @param   value   Escape this value.
     * @return  Escaped form.
     */
    public static String escapeProperty(final String value) {
        Properties newprops = new Properties();
        newprops.put("key", value);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            newprops.store(out, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            final String file = out.toString("ISO-8859-1");
            return file.substring(file.indexOf('\n') + 1 + "key=".length());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}
