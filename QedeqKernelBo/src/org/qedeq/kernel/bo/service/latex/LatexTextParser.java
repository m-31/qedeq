/* $Id: LatexTextParser.java,v 1.1 2008/07/26 07:58:28 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.latex;

import org.qedeq.base.io.TextInput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.parser.MementoTextInput;

/**
 * Transform LaTeX into QEDEQ format.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class LatexTextParser {

    /** This class. */
    private static final Class CLASS = LatexTextParser.class;

    /** These characters get a special treatment in LaTeX. */
    private static final String SPECIALCHARACTERS = "(),{}\\~%$&";

    /** This is our input stream .*/
    private MementoTextInput input;

    /** Herein goes our output. */
    private StringBuffer output;

    /**
     * Parse LaTeX text into QEDEQ module string.
     *
     * @param   input   Parse this input.
     * @return  QEDEQ module string.
     */
    public static final String transform(final String input) {
        final LatexTextParser parser = new LatexTextParser(input);
        return parser.parse();
    }

    /**
     * Constructor.
     *
     * @param   input   Parse this input.
     */
    private LatexTextParser(final String input) {
        this.input =  new MementoTextInput(new TextInput(input));
        this.output = new StringBuffer();
    }

    /**
     * Do parsing.
     *
     * @return  QEDEQ module string.
     */
    private String parse() {
        while (!eof()) {
            final String token = readToken();
            if ("\\begin".equals(token)) {
                final String curly = readCurlyBraceContents();
                if ("eqnarray".equals(curly)) {
                    printMathTillEnd(curly);
                } else if ("eqnarray*".equals(curly)) {
                    printMathTillEnd(curly);
                } else if ("equation".equals(curly)) {
                    printMathTillEnd(curly);
                } else if ("equation*".equals(curly)) {
                    printMathTillEnd(curly);
                } else {
                    print(token + "{" + curly + "}");
                }
            } else if ("$$".equals(token)) {
                println();
                println("<MATH>");
                printMathTillToken(token);
                println("\\,</MATH>");
                println();
            } else if ("$".equals(token)) {
                print("<MATH>");
                printMathTillToken(token);
                print("\\,</MATH>");
            } else {
                print(token);
            }
        }
        return output.toString();
    }

    private void printMathTillEnd(final String curly) {
        final StringBuffer buffer = new StringBuffer();
        do {
            final String item = readToken();
            if ("\\end".equals(item)) {
                final String curly2 = readCurlyBraceContents();
                if (curly.equals(curly2)) {
                    break;
                }
                buffer.append(item + "{" + curly2 + "}");
            } else {
                buffer.append(item);
            }
        } while (true);

/*
        println("\\begin{" + curly + "}");
        println(buffer);
        println("\\end{" + curly + "}");
        println();
*/
        printMath(buffer);
    }

    /**
     * Print math content till <code>token</code> occurs.
     *
     * @param   token   Terminator token.
     */
    private void printMathTillToken(final String token) {
        final StringBuffer buffer = new StringBuffer();
        do {
            final String item = readToken();
            if (token.equals(item)) {
                break;
            } else {
                buffer.append(item);
            }
        } while (true);
        printMath(buffer);
    }

    /**
     * Print math content.
     *
     * @param   buffer  This should be printed as mathematical content.
     */
    private void printMath(final StringBuffer buffer) {
        print(buffer.toString());
    }

    /**
     * Read next token from input stream.
     *
     * @return  Read token.
     */
    protected final String readToken() {
        final String method = "readToken()";
        Trace.begin(CLASS, this, method);
        StringBuffer token = new StringBuffer();
        try {
            do {
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
                if (Character.isLetter((char) c)) {
                    token.append((char) readChar());
                    if (Character.isLetter((char) getChar())) {
                        continue;
                    }
                    break;
                }
                if (SPECIALCHARACTERS.indexOf(c) >= 0) {
                    switch (c) {
                    case '&':
                    case '%':
                    case '{':
                    case '}':
                    case '~':
                        token.append((char) readChar());
                        break;
                    case '$':
                        token.append((char) readChar());
                        if ('$' == getChar()) {
                            continue;
                        }
                        break;
                    case '\\':
                        final String t = readBackslashToken();
                        token.append(t);
                        if ('_' == getChar() || '^' == getChar()) {
                            token.append((char) readChar());
                            continue;
                        }
                        break;
                    default:
                        readChar();
                        token.append((char) c);
                    }
                    break;
                }
                token.append((char) readChar());
                if ('_' == getChar() || '^' == getChar()) {
                    token.append((char) readChar());
                    continue;
                }
                break;
            } while (!eof());
            Trace.param(CLASS, this, method, "Read token", token);
            return (token != null ? token.toString() : null);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Get token that starts with a backlash. The backslash itself is removed from the token.
     *
     * @return  Token (without backslash).
     */
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
            return "\\" + ((char) readChar());
        }
        final StringBuffer buffer = new StringBuffer("\\");
        do {
            buffer.append((char) readChar());
        } while (!eof() && Character.isLetter((char) getChar()));
        Trace.param(CLASS, this, method, "return", buffer.toString());
        Trace.end(CLASS, this, method);
        return buffer.toString();
    }

    /**
     * Read contents that is within { .. }.
     *
     * @return  Contents.
     */
    private String readCurlyBraceContents() {
        final String first = readToken();
        if (!"{".equals(first)) {
            throw new IllegalArgumentException("\"{\" expected, but was: \"" + first + "\"");
        }
        final StringBuffer buffer = new StringBuffer();
        String next;
        int level = 1;
        while (level > 0) {
            next = readToken();
            if ("{".equals(next)) {
                level++;
            } else if ("}".equals(next)) {
                level--;
            }
            if (level <= 0) {
                break;
            }
            buffer.append(next);
        }
        return buffer.toString();
    }

    /**
     * Print <code>line</code> to output stream.
     *
     * @param   line    Print this.
     */
    private final void print(final String line) {
        output.append(line);
    }

    /**
     * Print end of line.
     */
    private final void println() {
        println("");
    }

    /**
     * Print <code>line</code> and start new line to output stream.
     *
     * @param   line    Print this.
     */
    private final void println(final String line) {
        print(line);
        print("\n");
    }

    /**
     * Read next token from input but don't move reading position.
     *
     * @return  Token read, is <code>null</code> if end of data reached.
     */
    public final String getToken() {
        markPosition();
        final String result = readToken();
        rewindPosition();
        return result;
    }

    /**
     * Remember current position.
     */
    protected final void markPosition() {
        input.markPosition();
    }

    /**
     * Rewind to previous marked position. Also clears the mark.
     *
     * @return  Current position before pop.
     */
    protected final long rewindPosition() {
        return input.rewindPosition();
    }

    /**
     * Forget last remembered position.
     */
    protected final void clearMark() {
        input.clearMark();
    }

    /**
     * Get byte position.
     *
     * @return  Position.
     */
    protected long getPosition() {
        return input.getPosition();
    }

    /**
     * Reads a single character and does not change the reading
     * position.
     *
     * @return  character read, if there are no more chars
     *          <code>Character.MAX_VALUE</code> is returned
     */
    protected final int getChar() {
        return input.getChar();
    }

    /**
     * Reads a single character and increments the reading position
     * by one.
     *
     * @return  character read, if there are no more chars
     *          <code>Character.MAX_VALUE</code> is returned
     */
    protected final int readChar() {
        return input.readChar();
    }

    /**
     * Are there still any characters to read?
     *
     * @return  Anything left for reading further?
     */
    public final boolean eof() {
        return input.eof();
    }

    /**
     * Get rewind stack size.
     *
     * @return  Rewind stack size.
     */
    public final int getRewindStackSize() {
        return input.getRewindStackSize();
    }

}
