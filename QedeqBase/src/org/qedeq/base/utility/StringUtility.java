/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.SystemUtils;



/**
 * A collection of useful static methods for strings.
 *
 * LATER mime 20070101: use StringBuilder instead of StringBuffer if working under JDK 1.5
 *
 * @author  Michael Meyling
 */
public final class StringUtility {

    /** For trimming with zeros. */
    static final String FORMATED_ZERO = "00000000000000000000";

    /** For trimming with spaces. */
    static final String FORMATED_SPACES = "                    ";

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
     * @param   text    text to work on, can be <code>null</code>
     * @param   search  replace this text by <code>replace</code>, can be <code>null</code>
     * @param   replace replacement for <code>search</code>, can be <code>null</code>
     * @return  resulting string (is never <code>null</code>)
     */
    public static String replace(final String text,
            final String search, final String replace) {

        if (text == null) {
            return "";
        }
        final int len = search != null ? search.length() : 0;
        if (len == 0) {
            return text;
        }
        final StringBuffer result = new StringBuffer();
        int pos1 = 0;
        int pos2;
        while (0 <= (pos2 = text.indexOf(search, pos1))) {
            result.append(text.substring(pos1, pos2));
            if (replace != null) {
                result.append(replace);
            }
            pos1 = pos2 + len;
        }
        if (pos1 < text.length()) {
            result.append(text.substring(pos1));
        }
        return result.toString();
    }

    /**
     * Replaces all occurrences of <code>search</code> in <code>text</code>
     * by <code>replace</code>.
     *
     * @param   text    Text to work on. Must not be <code>null</code>.
     * @param   search  replace this text by <code>replace</code>. Can be <code>null</code>.
     * @param   replace replacement for <code>search</code>. Can be <code>null</code>.
     * @throws  NullPointerException    <code>text</code> is <code>null</code>.
     */
    public static void replace(final StringBuffer text,
            final String search, final String replace) {
        if (search == null || search.length() <= 0) {
            return;
        }
        final StringBuffer result = new StringBuffer(text.length() + 16);
        int pos1 = 0;
        int pos2;
        final int len = search.length();
        while (0 <= (pos2 = text.indexOf(search, pos1))) {
            result.append(text.substring(pos1, pos2));
            result.append(replace != null ? replace : "");
            pos1 = pos2 + len;
        }
        if (pos1 < text.length()) {
            result.append(text.substring(pos1));
        }
        text.setLength(0);
        text.append(result);
    }

    /**
     * Return substring of text. Position might be negative if length is big enough. If the string
     * limits are exceeded this method returns at least all characters within the boundaries.
     * If no characters are within the given limits an empty string is returned.
     *
     * @param   text        Text to work on. Must not be <code>null</code>.
     * @param   position    Starting position. Might be negative.
     * @param   length      Maximum length to get.
     * @return  Substring of maximum length <code>length</code> and starting with position.
     * @throws  NullPointerException    <code>text</code> is <code>null</code>.
     */
    public static String substring(final String text, final int position, final int length) {
        final int start = Math.max(0, position);
        int l = position + length - start;
        if (l <= 0) {
            return "";
        }
        int end = start + l;
        if (end < text.length()) {
            return text.substring(start, end);
        }
        return text.substring(start);
    }

    /**
     * Returns a readable presentation of an Object array. Something like ("a", null, 13)" if we have
     * the "a", null, 13. Objects of type {@link CharSequence} are quoted.
     *
     * @param   list  List of Objects.
     * @return  List notation.
     */
    public static String toString(final Object[] list) {
        final StringBuffer buffer = new StringBuffer(30);
        buffer.append("(");
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                if (list[i] instanceof CharSequence) {
                    buffer.append("\"");
                    buffer.append(list[i].toString());
                    buffer.append("\"");
                } else {
                    buffer.append(String.valueOf(list[i]));
                }
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * Returns a readable presentation of a Set. Something like {"a", null, "13} if we have
     * "a", null, 13. Objects of type {@link CharSequence} are quoted.
     *
     * @param   set Set of objects.
     * @return  Set notation for toString() results.
     */
    public static String toString(final Set set) {
        final StringBuffer buffer = new StringBuffer(30);
        buffer.append("{");
        if (set != null) {
            Iterator e = set.iterator();
            boolean notFirst = false;
            while (e.hasNext()) {
                if (notFirst) {
                    buffer.append(", ");
                } else {
                    notFirst = true;
                }
                final Object obj = e.next();
                if (obj instanceof CharSequence) {
                    buffer.append("\"");
                    buffer.append(String.valueOf(obj));
                    buffer.append("\"");
                } else {
                    buffer.append(String.valueOf(obj));
                }
            }
        }
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Returns a readable presentation of a Map. Something like "{a=2, b=null, c=12}" if the
     * Map contains (a, 2), (b, null), (c, 12).
     *
     * @param   map Map of objects mappings.
     * @return  Set notation for toString() results.
     */
    public static String toString(final Map map) {
        final StringBuffer buffer = new StringBuffer(30);
        buffer.append("{");
        if (map != null) {
            Iterator e = map.entrySet().iterator();
            boolean notFirst = false;
            while (e.hasNext()) {
                if (notFirst) {
                    buffer.append(", ");
                } else {
                    notFirst = true;
                }
                final Map.Entry entry = (Map.Entry) e.next();
                buffer.append(String.valueOf(entry.getKey()));
                buffer.append("=");
                final Object value = entry.getValue();
                if (value instanceof CharSequence) {
                    buffer.append("\"");
                    buffer.append(String.valueOf(value));
                    buffer.append("\"");
                } else {
                    buffer.append(String.valueOf(value));
                }
            }
        }
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Evaluates toString at the elements of a Set and returns them as line separated Strings.
     * After the last element no carriage return is inserted.
     *
     * @param   set Set of objects.
     * @return  List of toString() results.
     */
    public static String asLines(final Set set) {
        final StringBuffer buffer = new StringBuffer(30);
        if (set != null) {
            Iterator e = set.iterator();
            boolean notFirst = false;
            while (e.hasNext()) {
                if (notFirst) {
                    buffer.append("\n");
                } else {
                    notFirst = true;
                }
                buffer.append(String.valueOf(e.next()));
            }
        }
        return buffer.toString();
    }


    /**
     * Quotes a <code>String</code>. A a quote character &quot; is appended at the
     * beginning and the end of the <code>String</code>. If a quote character occurs
     * within the string it is replaced by two quotes.
     *
     * @param   unquoted    the unquoted <code>String</code>, must not be <code>null</code>
     * @return  quoted <code>String</code>
     * @throws  NullPointerException if <code>unquoted == null</code>
     */
    public static String quote(final String unquoted) {
        StringBuffer result = new StringBuffer(unquoted.length() + 4);
        result.append('\"');

        for (int i = 0; i < unquoted.length(); i++) {
            if (unquoted.charAt(i) == '\"') {
                result.append("\"\"");
            } else {
                result.append(unquoted.charAt(i));
            }
        }
        result.append('\"');
        return result.toString();
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
     * Get last dot separated string part.
     *
     * @param   full    String with dots in it. Also <code>null</code> is accepted.
     * @return  All after the last dot in <code>full</code>. Is never <code>null</code>.
     */
    public static String getLastDotString(final String full) {
        if (full == null) {
            return "";
        }
        final int p = full.lastIndexOf('.');
        if (p < 0) {
            return full;
        }
        return full.substring(p + 1);
    }

    /**
     * Get last two dot separated string part.
     *
     * @param   full    String with dots in it. Also <code>null</code> is accepted.
     * @return  All after the next to last dot in <code>full</code>. Is never <code>null</code>.
     */
    public static String getLastTwoDotStrings(final String full) {
        if (full == null) {
            return "";
        }
        final int p = full.lastIndexOf('.');
        if (p < 1) {
            return full;
        }
        final int q = full.lastIndexOf('.', p - 1);
        if (q < 0) {
            return full;
        }
        return full.substring(q + 1);
    }

    /**
     * Get non qualified class name.
     *
     * @param   clazz   Class.
     * @return  Non qualified class name.
     */
    public static String getClassName(final Class clazz) {
        return getLastDotString(clazz.getName());
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
        int current = 0;
        int lastLf = -1;

        // detect position of last line feed before content starts (lastLf)
        while (current < buffer.length()) {
            if (!Character.isWhitespace(buffer.charAt(current))) {
                break;
            }
            if ('\n' == buffer.charAt(current)) {
                lastLf = current;
            }
            current++;
        }
        // string from last whitespace line feed until first non whitespace
        final String empty = buffer.substring(lastLf + 1, current);

        // delete this string out of the text
        if (empty.length() > 0) {
//            System.out.println(string2Hex(empty));
            buffer.delete(lastLf + 1 , current);    // delete first occurence
            replace(buffer, "\n" + empty, "\n");    // delete same whitespace on all following lines
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
            // should never occur
            throw new RuntimeException(e);
        }
        try {
            final String file = out.toString("ISO-8859-1");
            return file.substring(file.indexOf('\n') + 1 + "key=".length()).trim();
        } catch (UnsupportedEncodingException e) {
            // should never occur
            throw new RuntimeException(e);
        }
    }

    /**
     * Trim an integer with leading spaces to a given maximum length. If <code>length</code> is
     * shorter than the String representation of <code>number</code> no digit is cut. So the
     * resulting String might be longer than <code>length</code>.
     *
     * @param   number  Format this long.
     * @param   length  Maximum length. Must not be bigger than 20 and less than 1.
     * @return  String with minimum <code>length</code>, trimmed with leading spaces.
     */
    public static final String alignRight(final long number, final int length) {
        return alignRight("" + number, length);
    }

    /**
     * Trim a String with leading spaces to a given maximum length.
     *
     * @param   string  Format this string.
     * @param   length  Maximum length. Must not be bigger than 20 and less than 1.
     * @return  String with minimum <code>length</code>, trimmed with leading spaces.
     */
    public static final String alignRight(final String string, final int length) {
        if (length > FORMATED_SPACES.length()) {
            throw new IllegalArgumentException("maximum length " + FORMATED_SPACES + " exceeded: "
                + length);
        }
        if (length < 1) {
            throw new IllegalArgumentException("length must be bigger than 0: " + length);
        }
        final String temp = FORMATED_SPACES + string;
        return temp.substring(Math.min(temp.length() - length, FORMATED_SPACES.length()));
    }

    /**
     * Trim an integer with leading zeros to a given maximum length.
     *
     * @param   number  Format this long.
     * @param   length  Maximum length. Must not be bigger than 20 and less than 1.
     * @return  String with minimum <code>length</code>, trimmed with leading spaces.
     */
    public static final String format(final long number, final int length) {
        if (length > FORMATED_ZERO.length()) {
            throw new IllegalArgumentException("maximum length " + FORMATED_ZERO + " exceeded: "
                + length);
        }
        if (length < 1) {
            throw new IllegalArgumentException("length must be bigger than 0: " + length);
        }
        final String temp = FORMATED_ZERO + number;
        return temp.substring(Math.min(temp.length() - length, FORMATED_ZERO.length()));
    }

    /**
     * Get a hex string representation for an byte array.
     *
     * @param   data    <code>byte</code> array to work on
     * @return  hex     String of hex codes.
     */
    public static String byte2Hex(final byte[] data) {

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String b = Integer.toHexString(255 & data[i]);
            if (i != 0) {
                if (0 != i % 16) {
                    buffer.append(" ");
                } else {
                    buffer.append("\n");
                }
            }
            if (b.length() < 2) {
                buffer.append("0");
            }
            buffer.append(b.toUpperCase(Locale.US));
        }
        return buffer.toString();
    }

    /**
     * Get a hex string representation for a String. Uses UTF-8 encoding.
     *
     * @param   data    String to work on
     * @return  hex     String of hex codes.
     */
    public static String string2Hex(final String data) {
        try {
            return byte2Hex(data.getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            // should not happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a hex string representation for a String.
     *
     * @param   data    String to work on
     * @param   encoding    Use this String encoding.
     * @return  hex     String of hex codes.
     * @throws  UnsupportedEncodingException    Encoding not supported.
     */
    public static String string2Hex(final String data, final String encoding)
            throws UnsupportedEncodingException {
        return byte2Hex(data.getBytes(encoding));
    }

    /**
     * Get a byte array of a hex string representation.
     *
     * @param   hex     Hex string representation of data.
     * @return  Data array.
     * @throws  IllegalArgumentException Padding wrong or illegal hexadecimal character.
     */
    public static byte[] hex2byte(final String hex) {

        StringBuffer buffer = new StringBuffer(hex.length());
        char c;
        for (int i = 0; i < hex.length(); i++) {
            c = hex.charAt(i);
            if (!Character.isWhitespace(c)) {
                if (!Character.isLetterOrDigit(c)) {
                    throw new IllegalArgumentException("Illegal hex char");
                }
                buffer.append(c);
            }
        }
        if (buffer.length() % 2 != 0) {
            throw new IllegalArgumentException("Bad padding");
        }
        byte[] result = new byte[buffer.length() / 2];
        for (int i = 0; i < buffer.length() / 2; i++) {
            try {
                result[i] = (byte) Integer.parseInt(buffer.substring(2 * i, 2 * i + 2), 16);
            } catch (Exception e) {
                throw new IllegalArgumentException("Illegal hex char");
            }
        }
        return result;
    }

    /**
     * Get a String out of a hex string representation. Uses UTF-8 encoding.
     *
     * @param   hex     Hex string representation of data.
     * @return  Data as String.
     * @throws  IllegalArgumentException Padding wrong or illegal hexadecimal character.
     */
    public static String hex2String(final String hex) {
        try {
            return new String(hex2byte(hex), "UTF8");
        } catch (UnsupportedEncodingException e) {
            // should not happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a String out of a hex string representation.
     *
     * @param   hex         Hex string representation of data.
     * @param   encoding    Use this String encoding.
     * @return  Data as String.
     * @throws  UnsupportedEncodingException    Encoding not supported.
     * @throws  IllegalArgumentException    Padding wrong or illegal hexadecimal character.
     */
    public static String hex2String(final String hex, final String encoding)
        throws UnsupportedEncodingException {
        return new String(hex2byte(hex), encoding);
    }

    /**
     * Get platform dependent line separator. (<code>&quot;\n&quot;</code> on UNIX).
     *
     * @return  Platform dependent line separator.
     */
    public static String getSystemLineSeparator() {
        return (SystemUtils.LINE_SEPARATOR != null ? SystemUtils.LINE_SEPARATOR
            : "\n");
    }

    /**
     * Creates String with platform dependent line ends. If <code>text</code> is <code>null</code>
     * or empty nothing is changed. At the end of the String the platform dependent line end is
     * added whether or not the original text ends with such a sequence.
     *
     * @param  text Text with CR or CR LF as line end markers. Might be <code>null</code>.
     * @return Text with platform dependent line ends.
     */
    public static String useSystemLineSeparator(final String text) {
        if (text == null) {
            return null;
        }
        final StringBuffer buffer = new StringBuffer(text.length());
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final String separator = getSystemLineSeparator();
        String line;
        try {
            while (null != (line = reader.readLine())) {
                buffer.append(line);
                buffer.append(separator);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer.toString();
    }

    /**
     * Split String by given delimiter.
     * "a:b:c" is converted to "a", "b", "c".
     * "a:b:c:" is converted to "a", "b", "c", "".
     *
     * @param   text        Text to split.
     * @param   delimiter   Split at these points.
     * @return  Split text.
     */
    public static String[] split(final String text, final String delimiter) {
        final List list = new ArrayList();
        int start = 0;
        int found = -delimiter.length();
        while (-1 < (found = text.indexOf(delimiter, start))) {
            list.add(text.substring(start, found));
            start = found + delimiter.length();
        }
        list.add(text.substring(start));
        return (String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * <p>Escapes the characters in a <code>String</code> using XML entities.</p>
     *
     * <p>For example: <tt>"bread" & "butter"</tt> =>
     * <tt>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</tt>.
     * </p>
     *
     * <p>Supports only the five basic XML entities (gt, lt, quot, amp, apos).
     * Does not support DTDs or external entities.</p>
     *
     * <p>Note that unicode characters greater than 0x7f are currently escaped to
     * their numerical \\u equivalent. This may change in future releases. </p>
     *
     * @param   value   The <code>String</code> to escape, may be null.
     * @return  A new escaped <code>String</code>, <code>null</code> if null string input
     * @see #unescapeXml(java.lang.String)
     */
    public static String escapeXml(final String value) {
        return StringEscapeUtils.escapeXml(value);
    }

    /**
     * <p>Unescapes a string containing XML entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes.</p>
     *
     * <p>Supports only the five basic XML entities (gt, lt, quot, amp, apos).
     * Does not support DTDs or external entities.</p>
     *
     * <p>Note that numerical \\u unicode codes are unescaped to their respective
     *    unicode characters. This may change in future releases. </p>
     *
     * @param   value   The <code>String</code> to unescape, may be null.
     * @return  A new unescaped <code>String</code>, <code>null</code> if null string input
     * @see #escapeXml(String)
     */
    public static String unescapeXml(final String value) {
        return StringEscapeUtils.unescapeXml(value);
    }

    /**
     * Does a given string is not an element of a given string array?
     *
     * @param   lookFor Look for this string.
     * @param   array   The array we look through.
     * @return  Is the given string not an element of the array?
     */
    public static boolean isNotIn(final String lookFor, final String[] array) {
        if (lookFor == null || lookFor.length() <= 0) {
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            if (lookFor.equals(array[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Does a given string is an element of a given string array?
     *
     * @param   lookFor Look for this string.
     * @param   array   The array we look through.
     * @return  Is the given string an element of the array?
     */
    public static boolean isIn(final String lookFor, final String[] array) {
        return !isNotIn(lookFor, array);
    }


}
