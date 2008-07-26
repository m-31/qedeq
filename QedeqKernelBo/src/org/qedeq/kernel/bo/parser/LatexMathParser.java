/* $Id: LatexMathParser.java,v 1.1 2008/07/26 07:58:30 m31 Exp $
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

package org.qedeq.kernel.bo.parser;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.io.TextInput;
import org.qedeq.base.trace.Trace;

/*
 * TODO mime 20080118: refactor
 *
 * Whitespace   LaTeX form, could be eaten
 *              \t
 *              \r
 *              \n
 *              \\
 *              \\,
 *              &
 *              \\\\
 *              \\par
 *              \\quad
 *              \\qquad
 *
 * Separator    only one allowed, before and after only whitespace is possible
 *              ,
 *              |
 *              $$
 * Separator should be read as tokens.
 *
 * Problem: If some atom like is followed by "(" it should be taken as an
 * (function) operator. But if we start with readToken we don't see the "("
 * character.
 *
 * Problem: Could whitespace be recognized?
 * Translating whitespace tokens into spaces is not easy, one has to know the
 * end of the whitespace token.
 * Possible solution:
 *  function read token (LaTeX specific)
 *    skip real whitespace (" ", "\t", "\r", "\n")
 *    read char
 *    case char
 *     "\\" read characters or numbers (check LaTeX Syntax)
 *          "{", "}", "(", ")" are also allowed
 *          resulting string is token
 * LaTeX command definition modifies above:
 * Die meisten LATEX-Befehle haben eines der beiden folgenden Formate: Entweder sie beginnen
 * mit einem Backslash (\) und haben dann einen nur aus Buchstaben bestehenden Namen, der durch
 * ein oder mehrere Leerzeichen oder durch ein nachfolgendes Sonderzeichen oder eine Ziffer beendet
 * wird; oder sie bestehen aus einem Backslash und genau einem Sonderzeichen oder einer Ziffer.
 * Gross- und Kleinbuchstaben haben auch in Befehlsnamen verschiedene Bedeutung. Wenn man nach
 * einem Befehlsnamen eine Leerstelle erhalten will, muss man "{}" zur Beendigung des Befehlsnamens
 * oder einen eigenen Befehl f\u00fcr die Leerstelle verwenden.
 */

/**
 * Parse LaTeX term or formula data into {@link org.qedeq.kernel.bo.parser.Term}s.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class LatexMathParser extends MathParser {

    /** This class. */
    private static final Class CLASS = LatexMathParser.class;

    /** Characters with special LaTeX meaning. */
    private static final String SPECIALCHARACTERS = "(),{}\\~%$&";

    /** Counter for token whitespace lines. */
    private int tokenWhiteSpaceLines;

    /**
     * Constructor.
     *
     * @param   input       Parse this input.
     * @param   operators   List of operators.
     */
    public LatexMathParser(final TextInput input, final List operators) {
        super(new MementoTextInput(input), operators);
    }


    /**
     * Constructor.
     *
     * @param   buffer      Parse this input.
     * @param   operators   List of operators.
     */
    public LatexMathParser(final StringBuffer buffer, final List operators) {
        this(new TextInput(buffer), operators);
    }

    protected final String readToken() {
        final String method = "readToken()";
        Trace.begin(CLASS, this, method);
        StringBuffer token = new StringBuffer();
        tokenWhiteSpaceLines = 0;
        try {
            do {
                tokenWhiteSpaceLines += readPureWhitespace();
                if (tokenWhiteSpaceLines > 1) {
                    break;
                }
                if (eof()) {
                    if (token.length() <= 0) {
                        token = null;
                    }
                    break;
                }
                final int c = getChar();
                if (Character.isDigit((char) c)) {
                    token.append((char) readChar());
                    if (Character.isDigit((char) getChar())) {
                        continue;
                    }
                    break;
                }
                if (SPECIALCHARACTERS.indexOf(c) >= 0) {
                    switch (c) {
                    case '&':
                    case '%':
                    case '~':
                    case '$':   // TODO mime 20060504 or break in this case?
                        readChar();
                        continue;
                    case '\\':
                        final String t = readBackslashToken();
                        if (t.equals(" ") || t.equals("quad") || t.equals("qquad")) {
                            continue;
                        }
                        token.append(t);
                        if ('_' == getChar() || '^' == getChar()) {
                            token.append((char) readChar());
                            continue;
                        }
                        break;
                    case '{':
                        readChar();
                        token.append("(");
                        break;
                    case '}':
                        readChar();
                        token.append(")");
                        break;
                    default:
                        readChar();
                        token.append((char) c);
                        if ('_' == getChar() || '^' == getChar()) {
                            token.append((char) readChar());
                            continue;
                        }
                    }
                    break;
                }
                token.append((char) readChar());
                if ('_' == getChar() || '^' == getChar()) {
                    token.append((char) readChar());
                    continue;
                }
                break;
/*
                String operator = null;
                markPosition();
                while (!eof() && (Character.isLetterOrDigit((char) getChar()) || '_' == getChar()
                        || '^' == getChar())) {
                    token.append((char) readChar());
                    if (null != getOperator(token.toString())) {
                        operator = token.toString();
                        clearMark();
                        markPosition();
                    }
                }
                if (operator != null) {
                    rewindPosition();
                    token.setLength(0);
                    token.append(operator);
                } else {
                    clearMark();
                }
*/
            } while (!eof());
            Trace.param(CLASS, this, method, "return token", token);
            return (token != null ? token.toString() : null);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    private String readBackslashToken() {
        final String method = "readBackslashToken()";
        Trace.begin(CLASS, this, method);
        if (getChar() != '\\') {
            throw new IllegalArgumentException("\\ expected");
        }
        readChar(); // read \
        if (eof()) {
            Trace.param(CLASS, this, method, "return", null);
            Trace.end(CLASS, this, method);
            return null;
        }
        if (!Character.isLetter((char) getChar())) {
            Trace.param(CLASS, this, method, "return", (char) getChar());
            Trace.end(CLASS, this, method);
            return "" + ((char) readChar());
        }
        final StringBuffer buffer = new StringBuffer();
        do {
            buffer.append((char) readChar());
        } while (!eof() && Character.isLetter((char) getChar()));
        Trace.param(CLASS, this, method, "return", buffer.toString());
        Trace.end(CLASS, this, method);
        return buffer.toString();
    }

    private int readPureWhitespace() {
        int lines = 0;
        while (getChar() != -1 && Character.isWhitespace((char) getChar())) {
            if ('\n' == (char) getChar()) {
                lines++;
            }
            readChar();
        }
        return lines;
    }

    protected final Operator getOperator(final String token) {
        Operator result = null;
        if (token == null) {
            return null;
        }
        for (int i = 0; i < getOperators().size(); i++) {
            if (token.equals(((Operator) getOperators().get(i)).getStartSymbol())) {
                result = (Operator) getOperators().get(i);
                break;
            }
        }
        if (result != null) {
            return result;
        }
        // mime 20080725: no operator found -> return subject variable
        if (SPECIALCHARACTERS.indexOf(token) < 0) {
            return new Operator(token, null, null, "VAR", token, 200, 0, 0);
        }
        return null;
    }

    protected final List getOperators(final String token) {
        final List result = new ArrayList();
        if (token == null) {
            return result;
        }
        for (int i = 0; i < getOperators().size(); i++) {
            if (token.equals(((Operator) getOperators().get(i)).getStartSymbol())) {
                result.add(getOperators().get(i));
            }
        }
        // mime 20080725: no operator found -> return subject variable
        if (result.size() <= 0 && SPECIALCHARACTERS.indexOf(token) < 0) {
            result.add(new Operator(token, null, null, "VAR", token, 200, 0, 0));
        }
        return result;
    }

    protected boolean eot(final String token) {
        return token == null || token.trim().length() == 0;
    }

}
