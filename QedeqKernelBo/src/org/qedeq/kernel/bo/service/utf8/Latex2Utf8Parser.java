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

import org.qedeq.base.io.StringOutput;
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
    private static final String SPECIALCHARACTERS = "(),{}\\~%$&\'`^_-";

    /** Available subscript characters. */
    private static final String SUBSCRIPT_CHARACTERS = "0123456789()+-=";

    /** Available superscript characters. */
    private static final String SUPERSCRIPT_CHARACTERS = "0123456789()+-=n";

    /** Herein goes our output. */
    private StringOutput output;

    /** This is our input stream .*/
    private MementoTextInput input;

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

    /** Offset for current TextInput. */
    private long current;

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
    private Stack currentStack = new Stack();

    /** Should I skip whitespace before printing the next token. */
    private boolean skipWhitespace;

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
        this.output = new StringOutput();
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
        boldStack.push(Boolean.valueOf(bold));
        mathbbStack.push(Boolean.valueOf(mathbb));
        currentStack.push(new Long(current));
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
                    parseBegin();
                } else if ("\\footnote".equals(token)) {
                    parseFootnote();
                } else if ("$$".equals(token)) {
                    mathMode = true;
                    current += input.getPosition() + 1;
                    final String content = getTillToken(token);
                    println();
                    parseAndPrint(content);
                    println();
                    mathMode = false;
                } else if ("$".equals(token)) {
                    mathMode = true;
                    current += input.getPosition() + 1;
                    final String content = getTillToken(token);
                    parseAndPrint(content);
                    mathMode = false;
                } else if ("\\mathfrak".equals(token)) {
                    if ('{' == getChar()) {
                        mathfrak = true;
                        final String content = readCurlyBraceContents();
                        current += input.getPosition() + 1;
                        parseAndPrint(content);
                        mathfrak = false;
                    } else {
                        mathfrak = true;
                    }
                } else if ("\\mathbb".equals(token)) {
                    if ('{' == getChar()) {
                        mathbb = true;
                        final String content = readCurlyBraceContents();
                        current += input.getPosition() + 1;
                        parseAndPrint(content);
                        mathbb = false;
                    } else {
                        mathbb = true;
                    }
                } else if ("\\emph".equals(token)) {
                    if ('{' == getChar()) {
                        emph = true;
                        current += input.getPosition() + 1;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(content);
                        output.print("\u2006");
                        emph = false;
                    } else {
                        emph = true;
                    }
                } else if ("\\textbf".equals(token)) {
                    if ('{' == getChar()) {
                        bold = true;
                        current += input.getPosition() + 1;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(content);
                        bold = false;
                    } else {
                        bold = true;
                    }
                } else if ("\\mbox".equals(token)) {
                    if ('{' == getChar()) {
                        current += input.getPosition() + 1;
                        final String content = readCurlyBraceContents();
                        parseAndPrint(content);
                    }
                } else if ("\\cline".equals(token)) {
                    if ('{' == getChar()) {
                        current += input.getPosition() + 1;
                        readCurlyBraceContents();
                        // ignore
                    }
                    output.println("_______________________________________");
                    println();
                } else if ("{".equals(token)) {
                    input.readInverse();
                    current += input.getPosition() + 1;
                    final String content = readCurlyBraceContents();
                    parseAndPrint(content);
                } else if ("\\url".equals(token)) {
                    current += input.getPosition() + 1;
                    final String content = readCurlyBraceContents();
                    output.print(" ");
                    parseAndPrint(content);
                    output.print(" ");
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
            input = (MementoTextInput) inputStack.pop();
            mathMode = ((Boolean) mathModeStack.pop()).booleanValue();
            mathfrak = ((Boolean) mathfrakStack.pop()).booleanValue();
            emph = ((Boolean) emphStack.pop()).booleanValue();
            bold = ((Boolean) boldStack.pop()).booleanValue();
            current = ((Long) currentStack.pop()).longValue();
        }
    }

    /**
     * Parse after \footnote.
     */
    private void parseFootnote() {
        if ('{' == getChar()) {
            current += input.getPosition() + 1;
            final String content = readCurlyBraceContents();
            println();
            println();
            output.print("\u22D9");
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            println();
            parseAndPrint(content);
            output.popLevel();
            output.popLevel();
            output.popLevel();
            output.popLevel();
            output.popLevel();
            output.popLevel();
            println();
            output.print("\u22D8");
            println();
            println();
        }
    }

    /**
     * Parse after \begin.
     */
    private void parseBegin() {
        final String kind = readCurlyBraceContents();  // ignore
        current += input.getPosition();
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
            println();
            output.pushLevel();
            output.pushLevel();
            output.pushLevel();
            println();
            parseAndPrint(content);
            println();
            output.popLevel();
            output.popLevel();
            output.popLevel();
        } else {
            parseAndPrint(content);
        }
    }

    private void printSubscript(final String content) {
        boolean supported = true;
        for (int i = 0; i < content.length(); i++) {
            if (SUBSCRIPT_CHARACTERS.indexOf(content.charAt(i)) < 0) {
                supported = false;
                break;
            }
        }
        if (!supported) {
            output.print("_(" + content + ")");
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
                    output.print((char) (c - '0' + '\u2080'));
                    break;
                case '+':
                    output.print('\u208A');
                    break;
                case '-':
                    output.print('\u208B');
                    break;
                case '=':
                    output.print('\u208C');
                    break;
                case '(':
                    output.print('\u208D');
                    break;
                case ')':
                    output.print('\u208E');
                    break;
                default:
                    output.print(c);
                }
            }
        }
    }

    private void printSuperscript(final String content) {
        boolean supported = true;
        for (int i = 0; i < content.length(); i++) {
            if (SUPERSCRIPT_CHARACTERS.indexOf(content.charAt(i)) < 0) {
                supported = false;
                break;
            }
        }
        if (!supported) {
            output.print("_(" + content + ")");
        } else {
            for (int i = 0; i < content.length(); i++) {
                final char c = content.charAt(i);
                switch (c) {
                case '0':
                    output.print((char) (c - '0' + '\u2070'));
                    break;
                case '1':
                    output.print((char) (c - '0' + '\u00B9'));
                    break;
                case '2':
                    output.print((char) (c - '0' + '\u00B2'));
                    break;
                case '3':
                    output.print((char) (c - '0' + '\u00B3'));
                    break;
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    output.print((char) (c - '4' + '\u2074'));
                    break;
                case '+':
                    output.print('\u207A');
                    break;
                case '-':
                    output.print('\u207B');
                    break;
                case '=':
                    output.print('\u207C');
                    break;
                case '(':
                    output.print('\u207D');
                    break;
                case ')':
                    output.print('\u207E');
                    break;
                case 'n':
                    output.print('\u207F');
                    break;
                default:
                    output.print(c);
                }
            }
        }
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
        long start = current + input.getPosition();
        do {
            final String item = readToken();
            if (item == null) {
                Trace.fatal(CLASS, this, "readSection", "not found: " + "\\end{" + kind + "}",
                    new IllegalArgumentException("from " + start + " to " + current
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
     * @return  Read text.
     */
    private String getTillToken(final String token) {
        final long start = current + input.getPosition();
        final StringBuffer buffer = new StringBuffer();
        do {
            final String item = readToken();
            if (item == null) {
                Trace.fatal(CLASS, this, "readSection", "not found: " + token,
                    new IllegalArgumentException("from " + start + " to " + current
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
                    case '{':
                    case '}':
                    case '~':
                    case '_':
                    case '^':
                        token.append((char) readChar());
                        break;
                    case '$':
                    case '\'':
                    case '`':
                    case '-':
                        token.append((char) readChar());
                        if (c == getChar()) {
                            continue;
                        }
                        break;
                    case '%':
                        token.append((char) readChar());
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
                            token.append((char) readChar());
                            break;
                        }
                        final String t = readBackslashToken();
                        token.append(t);
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
            output.print(" ");
        } else if (token.equals("\\-")) {
            // ignore
        } else if (token.equals("--")) {
            output.print("\u2012");
        } else if (token.equals("`")) {
            output.print("\u2018");
        } else if (token.equals("'")) {
            output.print("\u2019");
        } else if (token.equals("\\neq")) {
            output.print("\u2260");
        } else if (token.equals("\\in")) {
            output.print("\u2208");
        } else if (token.equals("\\forall")) {
            output.print("\u2200");
        } else if (token.equals("\\exists")) {
            output.print("\u2203");
        } else if (token.equals("\\emptyset")) {
            output.print("\u2205");
        } else if (token.equals("\\rightarrow")) {
            output.print("\u2192");
        } else if (token.equals("\\Rightarrow")) {
            output.print("\u21D2");
        } else if (token.equals("\\leftrightarrow")) {
            output.print("\u2194");
        } else if (token.equals("\\Leftarrow")) {
            output.print("\u21D0");
        } else if (token.equals("\\Leftrightarrow")) {
            output.print("\u21D4");
        } else if (token.equals("\\land") || token.equals("\\vee")) {
            output.print("\u2227");
        } else if (token.equals("\\lor") || token.equals("\\wedge")) {
            output.print("\u2228");
        } else if (token.equals("\\bar")) {
            output.print("\u203E");
        } else if (token.equals("\\bigcap")) {
            output.print("\u22C2");
        } else if (token.equals("\\cap")) {
            output.print("\u2229");
        } else if (token.equals("\\bigcup")) {
            output.print("\u22C3");
        } else if (token.equals("\\cup")) {
            output.print("\u222A");
        } else if (token.equals("\\in")) {
            output.print("\u2208");
        } else if (token.equals("\\notin")) {
            output.print("\u2209");
        } else if (token.equals("\\alpha")) {
            output.print("\u03B1");
        } else if (token.equals("\\beta")) {
            output.print("\u03B2");
        } else if (token.equals("\\phi")) {
            output.print("\u03C6");
        } else if (token.equals("\\psi")) {
            output.print("\u03C8");
        } else if (token.equals("\\Omega")) {
            output.print("\u03A9");
        } else if (token.equals("\\omega")) {
            output.print("\u03C9");
        } else if (token.equals("\\subseteq")) {
            output.print("\u2286");
        } else if (token.equals("\\{")) {
            output.print("{");
        } else if (token.equals("\\}")) {
            output.print("}");
        } else if (token.equals("\\ ")) {
            output.print(" ");
        } else if (token.equals("~")) {
            output.print("\u00A0");
        } else if (token.equals("\\quad")) {
            output.print("\u2000");
        } else if (token.equals("\\,")) {
            output.print("\u2009");
        } else if (token.equals("\\neg") || token.equals("\\not")) {
            output.print("\u00AC");
        } else if (token.equals("\\bot")) {
            output.print("\u22A5");
        } else if (token.equals("\\top")) {
            output.print("\u22A4");
        } else if (token.equals("''") || token.equals("\\grqq")) {
            output.print("\u201D");
        } else if (token.equals("``") || token.equals("\\glqq")) {
            skipWhitespace = true;
            output.print("\u201E");
        } else if (token.equals("\\ldots")) {
            output.print("...");
        } else if (token.equals("\\overline")) {    // TODO 20101018 m31: we assume set complement
            output.print("\u2201");
        } else {
            if (mathfrak) {
                mathfrak(token);
            } else if (mathbb) {
                mathbb(token);
            } else if (emph) {
                for (int i = 0; i < token.length(); i++) {
                    output.print("\u2006");
                    output.print(token.charAt(i));
                }
            } else if (bold) {
                bold(token);
            } else {
                output.print(token);
            }
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
            case 'C': output.print("\u2102");
                break;
            case 'H': output.print("\u210D");
                break;
            case 'N': output.print("\u2115");
                break;
            case 'P': output.print("\u2119");
                break;
            case 'Q': output.print("\u211A");
                break;
            case 'R': output.print("\u211D");
                break;
            case 'Z': output.print("\u2124");
                break;
            default:
                output.print(c);
            }
        }
    }

    /**
     * Write token chars in mathfrak mode.
     *
     * @param   token   Chars to write.
     */
    private void mathfrak(final String token) {
        for (int i = 0; i < token.length(); i++) {
            final char c = token.charAt(i);
            switch (c) {
            case 'A': output.print("\u13AF");
                break;
            case 'B': output.print("\u212C");
                break;
            case 'b': output.print("\u13B2");
                break;
            case 'C': output.print("\u212D");
                break;
            case 'E': output.print("\u2130");
                break;
            case 'e': output.print("\u212F");
                break;
            case 'F': output.print("\u2131");
                break;
            case 'G': output.print("\u13B6");
                break;
            case 'g': output.print("\u210A");
                break;
            case 'L': output.print("\u2112");
                break;
            case 'l': output.print("\u2113");
                break;
            case 'M': output.print("\u2133");
                break;
            case 'o': output.print("\u2134");
                break;
            case 'P': output.print("\u2118");
                break;
            case 'R': output.print("\u211B");
                break;
            case 'S': output.print("\u093D");
                break;
            case 's': output.print("\u0D1F");
                break;
            case 'T': output.print("\u01AC");
                break;
            case 'V': output.print("\u01B2");
                break;
            case 'Y': output.print("\u01B3");
            break;
            case 'Z': output.print("\u2128");
                break;
            default:
                output.print(c);
            }
        }
    }

    /**
     * Write chars in bold mode.
     *
     * @param   token   Chars to write.
     */
    private void bold(final String token) {
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
                output.print((char) ('\uFF21' - 'A' + c));
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
                output.print((char) ('\uFF41' - 'a' + c));
                break;
            default:
                output.print(c);
            }
        }
    }

    /**
     * Print end of line.
     */
    private final void println() {
        output.println();
        output.levelPrint("");
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
    protected final int readChar() {
        return input.readChar();
    }

    /**
     * Read until end of line.
     *
     * @return  Characters read.
     */
    protected final String readln() {
        StringBuffer result = new StringBuffer();
        int c;
        while (TextInput.EOF != (c = readChar())) {
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
