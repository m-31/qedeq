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

package org.qedeq.kernel.bo.service.utf8;

import java.util.Stack;

import org.qedeq.base.io.TextInput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.parser.MementoTextInput;

/**
 * Transform LaTeX into QEDEQ format.
 *
 * @author  Michael Meyling
 */
public final class Latex2Utf8Parser {

    /** This class. */
    private static final Class CLASS = Latex2Utf8Parser.class;

    /** These characters get a special treatment in LaTeX. */
    private static final String SPECIALCHARACTERS = "(),{}\\~%$&\'`";

    /** Herein goes our output. */
    private StringBuffer output;

    /** This is our input stream .*/
    private MementoTextInput input;

    /** Math mode on? */
    private boolean mathMode = false;

    /** Mathfrak mode on? */
    private boolean mathfrak = false;

    /** Emphasize on? */
    private boolean emph = false;

    /** Stack for input parser. */
    private Stack inputStack = new Stack();

    /** Stack for math mode. */
    private Stack mathModeStack = new Stack();

    /** Stack for mathfrak mode. */
    private Stack mathfrakStack = new Stack();

    /** Stack for emphasize mode. */
    private Stack emphStack = new Stack();

    /**
     * Parse LaTeX text into QEDEQ module string.
     *
     * @param   input   Parse this input.
     * @return  QEDEQ module string.
     */
    public static final String transform(final String input) {
        final Latex2Utf8Parser parser = new Latex2Utf8Parser(input);
        parser.getUtf8(input);
        return parser.output.toString();
    }

    /**
     * Constructor.
     *
     * @param   input   Parse this input.
     */
    private Latex2Utf8Parser(final String input) {
        this.output = new StringBuffer();
    }

    /**
     * Get UTF-8 String out of LaTeX text.
     *
     * @param   text    LaTeX.
     * @return  UTF-8.
     */
    private String getUtf8(final String text) {
        parseAndPrint(text);
        return output.toString();
    }

    /**
     * Do parsing and print result.
     *
     * @param   text    Parse this LaTeX text and print UTF-8 into output.
     */
    private void parseAndPrint(final String text) {
        // remember old:
        inputStack.push(input);
        mathModeStack.push(Boolean.valueOf(mathMode));
        mathfrakStack.push(Boolean.valueOf(mathfrak));
        emphStack.push(Boolean.valueOf(emph));
        try {
            this.input = new MementoTextInput(new TextInput(text));
            boolean whitespace = false;
            while (!eof()) {
                String token = readToken();
                if (!token.startsWith("\\")) {
                    token = token.trim();
                }
                if (token.length() == 0) {
                    whitespace = true;
                    continue;
                }
                if (whitespace) {
                    print(" ");
                    whitespace = false;
                }
                if ("\\begin".equals(token)) {
                    final String kind = readCurlyBraceContents();
                    final String content = readSection(kind);
                    if ("eqnarray".equals(kind)
                        || "eqnarray*".equals(kind)
                        || "equation*".equals(kind)) {
                        mathMode = true;
                        parseAndPrint(content);
                        println();
                        mathMode = false;
                    } else if ("quote".equals(kind)) {
                        println();
                        parseAndPrint(content);
                        println();
                    }
                } else if ("$$".equals(token)) {
                    mathMode = true;
                    println();
                    String content = getTillToken(token);
                    parseAndPrint(content);
                    println();
                    mathMode = false;
                } else if ("$".equals(token)) {
                    mathMode = true;
                    String content = getTillToken(token);
                    parseAndPrint(content);
                    mathMode = false;
                } else if ("\\mathfrak".equals(token)) {
                    if ('{' == getChar()) {
                        mathfrak = true;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(content);
                        mathfrak = false;
                    } else {
                        mathfrak = true;
                    }
                } else if ("\\emph".equals(token)) {
                    if ('{' == getChar()) {
                        emph = true;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(content);
                        print(" ");
                        emph = false;
                    } else {
                        emph = true;
                    }
                } else if ("{".equals(token)) {
                    input.readInverse();
                    final String content = readCurlyBraceContents();
                    parseAndPrint(content);
                } else if ("\\index".equals(token)) {
                    // ignore
                } else {
                    print(token);
                }
            }
        } finally {
            input = (MementoTextInput) inputStack.pop();
            mathMode = ((Boolean) mathModeStack.pop()).booleanValue();
            mathfrak = ((Boolean) mathfrakStack.pop()).booleanValue();
            emph = ((Boolean) emphStack.pop()).booleanValue();
        }
    }

    /**
     * Read until section ends with \{kind}.
     *
     * @param   kind    Look for the end of this.
     * @return  Read text.
     */
    private String readSection(final String kind) {
        final StringBuffer buffer = new StringBuffer();
        do {
            final String item = readToken();
            if ("\\end".equals(item)) {
                final String curly2 = readCurlyBraceContents();
                if (kind.equals(curly2)) {
                    break;
                }
                buffer.append(item + "{" + curly2 + "}");
            } else {
                buffer.append(item);
            }
        } while (true);
        return buffer.toString();
    }

    /**
     * Get text till <code>token</code> occurs.
     *
     * @param   token   Terminator token.
     * @return  Read text.
     */
    private String getTillToken(final String token) {
        final StringBuffer buffer = new StringBuffer();
        do {
            final String item = readToken();
            if (token.equals(item)) {
                break;
            } else {
                buffer.append(item);
            }
        } while (true);
        return buffer.toString();
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
                    case '\'':
                        token.append((char) readChar());
                        if ('\'' == getChar()) {
                            continue;
                        }
                        break;
                    case '`':
                        token.append((char) readChar());
                        if ('`' == getChar()) {
                            continue;
                        }
                        break;
                    case '\\':
                        if (' ' == getChar()) {
                            token.append("\\");
                            token.append((char) readChar());
                            break;
                        }
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
            System.out.println(token);
            return (token != null ? token.toString() : null);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Get token that starts with a backlash.
     *
     * @return  Token with backslash.
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
     * Print <code>token</code> to output stream.
     *
     * @param   token    Print this for UTF-8.
     */
    private final void print(final String token) {
        if (token.equals("\\par")) {
            output.append("\n\n");
        } else if (token.equals("\\in")) {
            output.append("\u2208");
        } else if (token.equals("\\forall")) {
            output.append("\u2200");
        } else if (token.equals("\\exists")) {
            output.append("\u2203");
        } else if (token.equals("\\emptyset")) {
            output.append("\u2205 ");
        } else if (token.equals("\\rightarrow")) {
            output.append("\u2192 ");
        } else if (token.equals("\\leftrightarrow")) {
            output.append("\u2194 ");
        } else if (token.equals("\\land")) {
            output.append("\u22C0");
        } else if (token.equals("\\lor")) {
            output.append("\u22C1");
        } else if (token.equals("\\cap")) {
            output.append("\u22C2");
        } else if (token.equals("\\cup")) {
            output.append("\u22C3");
        } else if (token.equals("\\in")) {
            output.append("\u2208");
        } else if (token.equals("\\notin")) {
            output.append("\u2209");
        } else if (token.equals("\\alpha")) {
            output.append("\u03B1");
        } else if (token.equals("\\beta")) {
            output.append("\u03B2");
        } else if (token.equals("\\phi")) {
            output.append("\u03C6");
        } else if (token.equals("\\psi")) {
            output.append("\u03C8");
        } else if (token.equals("\\{")) {
            output.append("{");
        } else if (token.equals("\\}")) {
            output.append("}");
        } else if (token.equals("\\ ")) {
            output.append(" ");
        } else if (token.equals("~")) {
            output.append("\u00A0");
        } else if (token.equals("\\quad")) {
            output.append("\u2000");
        } else if (token.equals("\\,")) {
            output.append("\u2009");
        } else if (token.equals("''") || token.equals("\\grqq")) {
            output.append("\u201D");
        } else if (token.equals("``") || token.equals("\\glqq")) {
            output.append("\u201E");
        } else {
            if (mathfrak) {
                for (int i = 0; i < token.length(); i++) {
                    final char c = token.charAt(i);
                    switch (c) {
                    case 'M': output.append("\u2133");
                        break;
                    default:
                        output.append(c);
                    }
                }
            } else if (emph) {
                for (int i = 0; i < token.length(); i++) {
                    output.append("\u2006");
                    output.append(token.charAt(i));
/*
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
                        output.append((char) ('\uFF21' - 'A' + c));
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
                        output.append((char) ('\uFF41' - 'a' + c));
                        break;
                    default:
                        output.append(c);
                    }
*/
                }
            } else {
                output.append(token);
            }
        }
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
