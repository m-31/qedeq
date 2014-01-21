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

package org.qedeq.kernel.bo.service.unicode;


/**
 * Special LaTeX into UTF-8 transformations.
 *
 * @author  Michael Meyling
 */
public final class Latex2UnicodeSpecials {

    /** Available subscript characters. */
    private static final String SUBSCRIPT_CHARACTERS = "0123456789()+-=";

    /** Available superscript characters. */
    private static final String SUPERSCRIPT_CHARACTERS = "0123456789()+-=n";

    /**
     * Constructor.
     */
    private Latex2UnicodeSpecials() {
        // never used
    }

    /**
     * Transform into bold characters.
     *
     * @param   token   String to transform.
     * @return  Result of transformation.
     */
    public static String transform2Bold(final String token) {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < token.length(); i++) {
            final char c = token.charAt(i);
            switch (c) {
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                buffer.append((char) ('\uFF21' - 'A' + c));
                break;
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                buffer.append((char) ('\uFF41' - 'a' + c));
                break;
            default:
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    /**
     * Transform into mathfrak characters.
     *
     * @param   token   String to transform.
     * @return  Result of transformation.
     */
    public static String transform2Mathfrak(final String token) {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < token.length(); i++) {
            final char c = token.charAt(i);
            switch (c) {
            case 'A': buffer.append("\u13AF");
                break;
            case 'B': buffer.append("\u212C");
                break;
            case 'b': buffer.append("\u13B2");
                break;
            case 'C': buffer.append("\u212D");
                break;
            case 'E': buffer.append("\u2130");
                break;
            case 'e': buffer.append("\u212F");
                break;
            case 'F': buffer.append("\u2131");
                break;
            case 'G': buffer.append("\u13B6");
                break;
            case 'g': buffer.append("\u210A");
                break;
            case 'L': buffer.append("\u2112");
                break;
            case 'l': buffer.append("\u2113");
                break;
            case 'M': buffer.append("\u2133");
                break;
            case 'o': buffer.append("\u2134");
                break;
            case 'P': buffer.append("\u2118");
                break;
            case 'R': buffer.append("\u211B");
                break;
            case 'S': buffer.append("\u093D");
                break;
            case 's': buffer.append("\u0D1F");
                break;
            case 'T': buffer.append("\u01AC");
                break;
            case 'V': buffer.append("\u01B2");
                break;
            case 'Y': buffer.append("\u01B3");
            break;
            case 'Z': buffer.append("\u2128");
                break;
            default:
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    /**
     * Transform into subscript characters.
     *
     * @param   content String to transform.
     * @return  Result of transformation.
     */
    public static String transform2Subscript(final String content) {
        final StringBuffer buffer = new StringBuffer();
        boolean supported = true;
        for (int i = 0; i < content.length(); i++) {
            if (SUBSCRIPT_CHARACTERS.indexOf(content.charAt(i)) < 0) {
                supported = false;
                break;
            }
        }
        if (!supported) {
            if (content.length() == 1) {
                buffer.append("_" + content);
            } else {
                buffer.append("_(" + content + ")");
            }
        } else {
            for (int i = 0; i < content.length(); i++) {
                final char c = content.charAt(i);
                switch (c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    buffer.append((char) (c - '0' + '\u2080'));
                    break;
                case '+':
                    buffer.append('\u208A');
                    break;
                case '-':
                    buffer.append('\u208B');
                    break;
                case '=':
                    buffer.append('\u208C');
                    break;
                case '(':
                    buffer.append('\u208D');
                    break;
                case ')':
                    buffer.append('\u208E');
                    break;
                default:
                    buffer.append(c);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Transform into superscript characters.
     *
     * @param   content String to transform.
     * @return  Result of transformation.
     */
    public static String transform2Superscript(final String content) {
        final StringBuffer buffer = new StringBuffer();
        boolean supported = true;
        for (int i = 0; i < content.length(); i++) {
            if (SUPERSCRIPT_CHARACTERS.indexOf(content.charAt(i)) < 0) {
                supported = false;
                break;
            }
        }
        if (!supported) {
            if (content.length() == 1) {
                buffer.append("^" + content);
            } else {
                buffer.append("^(" + content + ")");
            }
        } else {
            for (int i = 0; i < content.length(); i++) {
                final char c = content.charAt(i);
                switch (c) {
                case '0':
                    buffer.append((char) (c - '0' + '\u2070'));
                    break;
                case '1':
                    buffer.append((char) (c - '0' + '\u00B9'));
                    break;
                case '2':
                    buffer.append((char) (c - '0' + '\u00B2'));
                    break;
                case '3':
                    buffer.append((char) (c - '0' + '\u00B3'));
                    break;
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    buffer.append((char) (c - '4' + '\u2074'));
                    break;
                case '+':
                    buffer.append('\u207A');
                    break;
                case '-':
                    buffer.append('\u207B');
                    break;
                case '=':
                    buffer.append('\u207C');
                    break;
                case '(':
                    buffer.append('\u207D');
                    break;
                case ')':
                    buffer.append('\u207E');
                    break;
                case 'n':
                    buffer.append('\u207F');
                    break;
                default:
                    buffer.append(c);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Transform into emph characters.
     *
     * @param   token   String to transform.
     * @return  Result of transformation.
     */
    public static String transform2Emph(final String token) {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < token.length(); i++) {
//            buffer.append('\u2006');
            buffer.append(' ');
            buffer.append(token.charAt(i));
        }
        return buffer.toString();
    }
}
