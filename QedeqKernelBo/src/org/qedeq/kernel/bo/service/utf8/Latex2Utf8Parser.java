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

import org.qedeq.base.io.AbstractOutput;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.io.StringOutput;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.service.latex.LatexErrorCodes;

/**
 * Transform LaTeX into QEDEQ format.
 *
 * @author  Michael Meyling
 */
public final class Latex2Utf8Parser {

    /** This class. */
    private static final Class CLASS = Latex2Utf8Parser.class;

    /** These characters get a special treatment in LaTeX. */
    private static final String SPECIALCHARACTERS = "(),{}\\~%$&\'`^_-";

    /** Herein goes our output. */
    private final AbstractOutput output;

    /** Resolver for references. */
    private final ReferenceFinder finder;

    /** This is our input stream .*/
    private TextInput input;

    /** Math mode on? */
    private boolean mathMode = false;

    /** Mathfrak mode on? */
    private boolean mathfrak = false;

    /** Emphasize on? */
    private boolean emph = false;

    /** Bold on? */
    private boolean bold = false;

    /** Mathbb on? */
    private boolean mathbb = false;

    /** Offset for current TextInput to top level TextInput. */
    private int start;

    /** Stack for input parser. */
    private Stack inputStack = new Stack();

    /** Stack for math mode. */
    private Stack mathModeStack = new Stack();

    /** Stack for mathfrak mode. */
    private Stack mathfrakStack = new Stack();

    /** Stack for emphasize mode. */
    private Stack emphStack = new Stack();

    /** Stack for bold mode. */
    private Stack boldStack = new Stack();

    /** Stack for mathbb mode. */
    private Stack mathbbStack = new Stack();

    /** Stack for offset of current TextInput. */
    private Stack startStack = new Stack();

    /** Should I skip whitespace before printing the next token. */
    private boolean skipWhitespace;

    /**
     * Parse LaTeX text into QEDEQ module string.
     *
     * @param   finder  Finder for references.
     * @param   input   Parse this input.
     * @param   columns Maximum column number. Break (if possible) before.
     * @return  QEDEQ module string.
     */
    public static final String transform(final ReferenceFinder finder, final String input, final int columns) {
        final Latex2Utf8Parser parser = new Latex2Utf8Parser(finder, input);
        parser.output.setColumns(columns);
        return parser.getUtf8(input);
    }

    /**
     * Constructor.
     *
     * @param   finder  Finder for references.
     * @param   input   Parse this input.
     */
    private Latex2Utf8Parser(final ReferenceFinder finder, final String input) {
        this.finder = finder;
        this.output = new StringOutput();
    }

    /**
     * Get UTF-8 String out of LaTeX text.
     *
     * @param   text    LaTeX.
     * @return  UTF-8.
     */
    private String getUtf8(final String text) {
        parseAndPrint(0, text);
        output.flush();
        return output.toString();
    }

    /**
     * Do parsing and print result.
     *
     * @param   text    Parse this LaTeX text and print UTF-8 into output.
     */
    private void parseAndPrint(final int start, final String text) {
        // remember old:
        inputStack.push(input);
        mathModeStack.push(Boolean.valueOf(mathMode));
        mathfrakStack.push(Boolean.valueOf(mathfrak));
        emphStack.push(Boolean.valueOf(emph));
        boldStack.push(Boolean.valueOf(bold));
        mathbbStack.push(Boolean.valueOf(mathbb));
        startStack.push(new Integer(start));
        this.start = start;
        int current = start;
        try {
            this.input = new TextInput(text);
            boolean whitespace = false;
            while (!eof()) {
                current = start + input.getPosition();
                final int tokenBegin = current;
                String token = readToken();
                current = start + input.getPosition();
                final int tokenEnd = current;
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
                    parseBegin();
                } else if ("\\footnote".equals(token)) {
                    parseFootnote();
                } else if ("\\qref".equals(token)) {
                    parseQref();
                } else if ("$$".equals(token)) {
                    mathMode = true;
                    final String content = readTilToken(token);
                    println();
                    parseAndPrint(current, content);
                    println();
                    mathMode = false;
                } else if ("$".equals(token)) {
                    mathMode = true;
                    final String content = readTilToken(token);
                    parseAndPrint(tokenEnd, content);
                    mathMode = false;
                } else if ("\\mathfrak".equals(token)) {
                    if ('{' == getChar()) {
                        mathfrak = true;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(tokenEnd + 1, content);
                        mathfrak = false;
                    } else {
                        mathfrak = true;
                    }
                } else if ("\\mathbb".equals(token)) {
                    if ('{' == getChar()) {
                        mathbb = true;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(tokenEnd + 1, content);
                        mathbb = false;
                    } else {
                        mathbb = true;
                    }
                } else if ("\\emph".equals(token)) {
                    if ('{' == getChar()) {
                        emph = true;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(tokenEnd + 1, content);
                        output.addWs("\u2006");
                        emph = false;
                    } else {
                        emph = true;
                    }
                } else if ("\\textbf".equals(token)) {
                    if ('{' == getChar()) {
                        bold = true;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(tokenEnd + 1, content);
                        bold = false;
                    } else {
                        bold = true;
                    }
                } else if ("\\mbox".equals(token)) {
                    if ('{' == getChar()) {
                        final String content = readCurlyBraceContents();
                        parseAndPrint(tokenEnd + 1, content);
                    }
                } else if ("\\cline".equals(token)) {
                    if ('{' == getChar()) {
                        readCurlyBraceContents();
                        // ignore
                    }
                    output.println("_______________________________________");
                    println();
                } else if ("{".equals(token)) {
                    input.readInverse();
                    final String content = readCurlyBraceContents();
                    parseAndPrint(tokenEnd, content);
                } else if ("\\url".equals(token)) {
                    final String content = readCurlyBraceContents();
                    output.addToken(" " + content + " ");
                } else if ('{' == getChar() && ("\\index".equals(token) || "\\label".equals(token)
                        || token.equals("\\vspace") || token.equals("\\hspace")
                        || token.equals("\\vspace*") || token.equals("\\hspace*"))) {
                    // ignore content
                    readCurlyBraceContents();
                } else if ("_".equals(token) || "^".equals(token)) {
                    if (mathMode) {
                        String content;
                        if ('{' == getChar()) {
                            content = readCurlyBraceContents();
                        } else {
                            content = readToken();
                        }
                        if ("_".equals(token)) {
                            printSubscript(content);
                        } else {
                            printSuperscript(content);
                        }
                    } else {
                        print(token);
                    }
                } else {
                    print(token);
                }
            }
        } finally {
            input = (TextInput) inputStack.pop();
            mathMode = ((Boolean) mathModeStack.pop()).booleanValue();
            mathfrak = ((Boolean) mathfrakStack.pop()).booleanValue();
            emph = ((Boolean) emphStack.pop()).booleanValue();
            bold = ((Boolean) boldStack.pop()).booleanValue();
            this.start = ((Integer) startStack.pop()).intValue();
        }
    }

    /**
     * Parse after \footnote.
     */
    private void parseFootnote() {
        if ('{' == getChar()) {
            final int current = start + input.getPosition();
            final String content = readCurlyBraceContents();
            println();
            output.printWithoutSplit("          \u250C");
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            output.pushLevel("\u2502 ");
            println();
            parseAndPrint(current + 1, content);
            output.popLevel();
            output.popLevel();
            output.popLevel();
            output.popLevel();
            output.popLevel();
            output.popLevel();
            println();
            output.printWithoutSplit("          \u2514");
            println();
        }
    }

    /**
     * Transform <code>\qref{key}</code> entries into common LaTeX code.
     *
     * @param   text    Work on this text.
     * @return  Result of transforming \qref into text.
     */
    /**
     * Parse after \footnote.
     */
    private void parseQref() {
        final String method = "parseQref()";
        final int localStart1 = start + input.getPosition();
        if ('{' == getChar()) {
            final String content = readCurlyBraceContents();
            String ref = content.trim();
            Trace.param(CLASS, this, method, "ref", ref);
            if (ref.length() == 0) {
                finder.addWarning(LatexErrorCodes.QREF_EMPTY_CODE, LatexErrorCodes.QREF_EMPTY_MSG,
                    getSourcePosition(localStart1), getSourcePosition(start + input.getPosition()));
                return;
            }
            if (ref.length() > 1024) {
                finder.addWarning(LatexErrorCodes.QREF_END_NOT_FOUND_CODE,
                    LatexErrorCodes.QREF_END_NOT_FOUND_MSG,
                    getSourcePosition(localStart1), getSourcePosition(start + input.getPosition()));
                return;
            }
            if (ref.indexOf("{") >= 0) {
                finder.addWarning(LatexErrorCodes.QREF_END_NOT_FOUND_CODE,
                    LatexErrorCodes.QREF_END_NOT_FOUND_MSG,
                    getSourcePosition(localStart1), getSourcePosition(start + input.getPosition()));
                return;
            }

            // exists a sub reference?
            String sub = "";
            int localStart2 = start + input.getPosition();
            if ('[' == input.getChar(0)) {
                read();
                sub = readTilToken("]");
            }
            String display = finder.getExternalReference(ref, sub, getSourcePosition(localStart1),
                getSourcePosition(localStart2));
            output.addToken(display);
        }
    }


    /**
     * Parse after \begin.
     */
    private void parseBegin() {
        final String kind = readCurlyBraceContents();   // ignore
        int current = start + input.getPosition();
        final String content = readSection(kind);
        current = start + input.getPosition();
        if ("eqnarray".equals(kind)
            || "eqnarray*".equals(kind)
            || "equation*".equals(kind)) {
            mathMode = true;
            parseAndPrint(current, content);
            println();
            mathMode = false;
        } else if ("quote".equals(kind)) {
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            println();
            parseAndPrint(current, content);
            println();
            output.popLevel();
            output.popLevel();
            output.popLevel();
        } else {
            parseAndPrint(current, content);
        }
    }

    private void printSubscript(final String content) {
        output.addToken(Latex2Utf8Specials.transform2Subscript(content));
    }

    private void printSuperscript(final String content) {
        output.addToken(Latex2Utf8Specials.transform2Superscript(content));
    }

    /**
     * Read until section ends with \{kind}.
     *
     * @param   kind    Look for the end of this.
     * @return  Read text.
     */
    private String readSection(final String kind) {
        if ('{' == getChar()) { // skip content
            readCurlyBraceContents();
        }
        if ('{' == getChar()) { // skip content
            readCurlyBraceContents();
        }
        final StringBuffer buffer = new StringBuffer();
        int localStart = start + input.getPosition();
        do {
            final String item = readToken();
            if (item == null) {
                Trace.fatal(CLASS, this, "readSection", "not found: " + "\\end{" + kind + "}",
                    new IllegalArgumentException("from " + localStart + " to " + start + input.getPosition()
                    + input.getPosition()));
                break;
            }
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
     * @return  Read text before token.
     */
    private String readTilToken(final String token) {
        final long localStart = start + input.getPosition();
        final StringBuffer buffer = new StringBuffer();
        do {
            final String item = readToken();
            if (item == null) {
                Trace.fatal(CLASS, this, "readSection", "not found: " + token,
                    new IllegalArgumentException("from " + localStart + " to " + start
                    + input.getPosition()));
                break;
            }
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
                final char c = (char) getChar();
                if (Character.isDigit(c)) {
                    token.append((char) read());
                    if (Character.isDigit((char) getChar())) {
                        continue;
                    }
                    break;
                }
                if (Character.isLetter((char) c)) {
                    token.append((char) read());
                    if (Character.isLetter((char) getChar())) {
                        continue;
                    }
                    break;
                }
                if (SPECIALCHARACTERS.indexOf(c) >= 0) {
                    switch (c) {
                    case '&':
                    case '{':
                    case '}':
                    case '~':
                    case '_':
                    case '^':
                        token.append((char) read());
                        break;
                    case '$':
                    case '\'':
                    case '`':
                    case '-':
                        token.append((char) read());
                        if (c == getChar()) {
                            continue;
                        }
                        break;
                    case '%':
                        token.append((char) read());
                        if (c == getChar()) {
                            // we must skip till end of line
                            token.append(readln());
//                            System.out.println("skipping comment:");
//                            System.out.println(token);
                            token.setLength(0);
                            continue;
                        }
                        break;
                    case '\\':
                        if (' ' == getChar()) {
                            token.append("\\");
                            token.append((char) read());
                            break;
                        }
                        final String t = readBackslashToken();
                        token.append(t);
                        break;
                    default:
                        read();
                        token.append((char) c);
                    }
                    break;
                }
                token.append((char) read());
                if ('_' == getChar() || '^' == getChar()) {
                    token.append((char) read());
                    continue;
                }
                break;
            } while (!eof());
            Trace.param(CLASS, this, method, "Read token", token);
//            System.out.println("< " + token);
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
        read(); // read \
        if (eof()) {
            Trace.param(CLASS, this, method, "return", null);
            Trace.end(CLASS, this, method);
            return null;
        }
        if (!Character.isLetter((char) getChar())) {
            Trace.param(CLASS, this, method, "return", (char) getChar());
            Trace.end(CLASS, this, method);
            return "\\" + ((char) read());
        }
        final StringBuffer buffer = new StringBuffer("\\");
        do {
            buffer.append((char) read());
        } while (!eof() && (Character.isLetter((char) getChar()) || '*' == (char) getChar()));
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
//        System.out.println("> " + token);
        if (token.trim().length() == 0) {
            if (skipWhitespace) {
                return;
            }
        }
        skipWhitespace = false;
        if (token.equals("\\par")) {
            println();
            println();
            skipWhitespace = true;
        } else if (token.equals("\\\\")) {
            println();
        } else if (token.equals("&")) {
            output.addWs(" ");
        } else if (token.equals("\\-")) {
            // ignore
        } else if (token.equals("--")) {
            output.addToken("\u2012");
        } else if (token.equals("`")) {
            output.addWs("\u2018");
        } else if (token.equals("'")) {
            output.addToken("\u2019");
        } else if (token.equals("\\neq")) {
            output.addToken("\u2260");
        } else if (token.equals("\\in")) {
            output.addToken("\u2208");
        } else if (token.equals("\\forall")) {
            output.addToken("\u2200");
        } else if (token.equals("\\exists")) {
            output.addToken("\u2203");
        } else if (token.equals("\\emptyset")) {
            output.addToken("\u2205");
        } else if (token.equals("\\rightarrow")) {
            output.addToken("\u2192");
        } else if (token.equals("\\Rightarrow")) {
            output.addToken("\u21D2");
        } else if (token.equals("\\leftrightarrow")) {
            output.addToken("\u2194");
        } else if (token.equals("\\Leftarrow")) {
            output.addToken("\u21D0");
        } else if (token.equals("\\Leftrightarrow")) {
            output.addToken("\u21D4");
        } else if (token.equals("\\langle")) {
            output.addToken("\u2329");
        } else if (token.equals("\\rangle")) {
            output.addToken("\u232A");
        } else if (token.equals("\\land") || token.equals("\\vee")) {
            output.addToken("\u2227");
        } else if (token.equals("\\lor") || token.equals("\\wedge")) {
            output.addToken("\u2228");
        } else if (token.equals("\\bar")) {
            output.addToken("\u203E");
        } else if (token.equals("\\bigcap")) {
            output.addToken("\u22C2");
        } else if (token.equals("\\cap")) {
            output.addToken("\u2229");
        } else if (token.equals("\\bigcup")) {
            output.addToken("\u22C3");
        } else if (token.equals("\\cup")) {
            output.addToken("\u222A");
        } else if (token.equals("\\in")) {
            output.addToken("\u2208");
        } else if (token.equals("\\notin")) {
            output.addToken("\u2209");
        } else if (token.equals("\\alpha")) {
            output.addToken("\u03B1");
        } else if (token.equals("\\beta")) {
            output.addToken("\u03B2");
        } else if (token.equals("\\phi")) {
            output.addToken("\u03C6");
        } else if (token.equals("\\psi")) {
            output.addToken("\u03C8");
        } else if (token.equals("\\Omega")) {
            output.addToken("\u03A9");
        } else if (token.equals("\\omega")) {
            output.addToken("\u03C9");
        } else if (token.equals("\\subseteq")) {
            output.addToken("\u2286");
        } else if (token.equals("\\{")) {
            output.addToken("{");
        } else if (token.equals("\\}")) {
            output.addToken("}");
        } else if (token.equals("\\ ")) {
            output.addWs(" ");
        } else if (token.equals("~")) {
            output.addToken("\u00A0");
        } else if (token.equals("\\quad")) {
            output.addWs("\u2000");
        } else if (token.equals("\\,")) {
            output.addWs("\u2009");
        } else if (token.equals("\\neg") || token.equals("\\not")) {
            output.addToken("\u00AC");
        } else if (token.equals("\\bot")) {
            output.addToken("\u22A5");
        } else if (token.equals("\\top")) {
            output.addToken("\u22A4");
        } else if (token.equals("''") || token.equals("\\grqq")) {
            output.addToken("\u201D");
        } else if (token.equals("``") || token.equals("\\glqq")) {
            skipWhitespace = true;
            output.addToken("\u201E");
        } else if (token.equals("\\ldots")) {
            output.addToken("...");
        } else if (token.equals("\\overline")) {    // TODO 20101018 m31: we assume set complement
            output.addToken("\u2201");
        } else {
            if (mathfrak) {
                mathfrak(token);
            } else if (mathbb) {
                mathbb(token);
            } else if (emph) {
                emph(token);
            } else if (bold) {
                bold(token);
            } else {
                if (isWs(token)) {
                    output.addWs(token);
                } else {
                    output.addToken(token);
                }
            }
        }
    }

    /**
     * Write token chars in mathbb mode.
     *
     * @param   token   Chars to write.
     */
    private void emph(final String token) {
        if (isWs(token)) {
            output.addWs(Latex2Utf8Specials.transform2Emph(token));
        } else {
            output.addToken(Latex2Utf8Specials.transform2Emph(token));
        }
    }

    /**
     * Write token chars in mathbb mode.
     *
     * @param   token   Chars to write.
     */
    private void mathbb(final String token) {
        for (int i = 0; i < token.length(); i++) {
            final char c = token.charAt(i);
            switch (c) {
            case 'C': output.addToken("\u2102");
                break;
            case 'H': output.addToken("\u210D");
                break;
            case 'N': output.addToken("\u2115");
                break;
            case 'P': output.addToken("\u2119");
                break;
            case 'Q': output.addToken("\u211A");
                break;
            case 'R': output.addToken("\u211D");
                break;
            case 'Z': output.addToken("\u2124");
                break;
            default:
                if (Character.isWhitespace(c)) {
                    output.addWs("" + c);
                } else {
                    output.addToken("" + c);
                }
            }
        }
    }

    private boolean isWs(final String token) {
        return token == null || token.trim().length() == 0;
    }

    /**
     * Write token chars in mathfrak mode.
     *
     * @param   token   Chars to write.
     */
    private void mathfrak(final String token) {
        if (isWs(token)) {
            output.addWs(Latex2Utf8Specials.transform2Mathfrak(token));
        } else {
            output.addToken(Latex2Utf8Specials.transform2Mathfrak(token));
        }
    }

    /**
     * Write token in bold mode.
     *
     * @param   token   Chars to write.
     */
    private void bold(final String token) {
        if (isWs(token)) {
            output.addWs(Latex2Utf8Specials.transform2Bold(token));
        } else {
            output.addToken(Latex2Utf8Specials.transform2Bold(token));
        }
    }

    /**
     * Print end of line.
     */
    private final void println() {
        output.println();
    }

    /**
     * Reads a single character and does not change the reading
     * position.
     *
     * @return  character read, if there are no more chars
     *          <code>-1</code> is returned
     */
    protected final int getChar() {
        return input.getChar();
    }

    /**
     * Reads a single character and increments the reading position
     * by one.
     *
     * @return  character read, if there are no more chars
     *          <code>-1</code> is returned
     */
    protected final int read() {
        return input.read();
    }

    /**
     * Read until end of line.
     *
     * @return  Characters read.
     */
    protected final String readln() {
        StringBuffer result = new StringBuffer();
        int c;
        while (TextInput.EOF != (c = read())) {
            if (c == '\n') {
                break;
            }
            result.append((char) c);
        }
        return result.toString();
    }

    /**
     * Are there still any characters to read?
     *
     * @return  Anything left for reading further?
     */
    public final boolean eof() {
        return input.isEmpty();
    }

    /**
     * Convert byte position into row and column information.
     *
     * @param   position    Find this byte position.
     * @return  Row and column information.
     */
    public SourcePosition getSourcePosition(final int position) {
        return input.getPosition(position);
    }

}
