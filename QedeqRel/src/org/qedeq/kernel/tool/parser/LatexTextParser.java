/* $Id: LatexTextParser.java,v 1.10 2008/07/26 08:03:15 m31 Exp $
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

package org.qedeq.kernel.tool.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.ResourceLoaderUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.parser.LatexMathParser;
import org.qedeq.kernel.bo.parser.MementoTextInput;
import org.qedeq.kernel.bo.parser.ParserException;
import org.qedeq.kernel.bo.parser.Term;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.xml.handler.parser.LoadXmlOperatorListUtility;

/**
 * Transform LaTeX into QEDEQ format.
 *
 * @version $Revision: 1.10 $
 * @author  Michael Meyling
 */
public final class LatexTextParser {

    /** This class. */
    private static final Class CLASS = LatexTextParser.class;

    private static final String SPECIALCHARACTERS = "(),{}\\~%$&";

    private MementoTextInput input;

    private TextOutput output;
    
    /** List of operators. */
    private List operators;

    public static void main(final String[] args) {
        TextInput input = null;
        try {
            {
                input = new TextInput(new File(args[0]), IoUtility.getDefaultEncoding());
                final String textName = "test_text.xml";
                final TextOutput printer = new TextOutput(textName, new FileOutputStream(textName));
                new LatexTextParser(input, printer, false,
                    LoadXmlOperatorListUtility.getOperatorList(new File(
                        ResourceLoaderUtility.getResourceUrl(
                        "org/qedeq/kernel/tool/parser/mengenlehreMathOperators.xml").getPath())));
            }
            {
                input = new TextInput(new File(args[0]), IoUtility.getDefaultEncoding());
                final String mathName = "test_math.xml";
                final TextOutput printer = new TextOutput(mathName, new FileOutputStream(mathName));
                new LatexTextParser(input, printer, true,
                    LoadXmlOperatorListUtility.getOperatorList(new File(
                        ResourceLoaderUtility.getResourceUrl(
                        "org/qedeq/kernel/tool/parser/mengenlehreMathOperators.xml").getPath())));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println(input.getRow() + ":" + input.getColumn() + ":");
            System.out.println(e.getMessage());
            System.out.println(input.getLine().replace('\t', ' ').replace('\015', ' '));
            final StringBuffer pointer = StringUtility.getSpaces(input.getColumn());
            pointer.append('^');
            System.out.println(pointer);
        }
    }

    /**
     * Constructor.
     *
     * @param   input       Parse this input.
     * @param   output      Resulting output.
     * @param   mathParsing Should mathematical expressions be parsed?
     * @param   operators   Operator list for parsing.
     * @throws XmlFilePositionException
     */
    public LatexTextParser(final TextInput input, final TextOutput output,
            final boolean mathParsing, final List operators)
            throws SourceFileException {
        this.input =  new MementoTextInput(input);
        this.output = output;
        this.operators = operators;

        printQedeqHeader();
        while (!eof()) {
            final String token = getToken();
            if (token != null && token.startsWith("\\chapter")) {
                break;
            }
            readToken();
        }
        int chapter = 0;
        int section = 0;
        boolean introduction = true;
        while (!eof()) {
            final String token = readToken();
//            System.err.println("token: " + token);
            if (mathParsing && "\\begin".equals(token)) {
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
            } else if (mathParsing && "$$".equals(token)) {
                printMathTillToken(token);
            } else if (mathParsing && "$".equals(token)) {
                printMathTillToken(token);
            } else if ("\\chapter".equals(token)) {
                if (chapter > 0) {
                    println("      ]]>");
                    println("    </LATEX>");
                    if (introduction) {
                        println("  </INTRODUCTION>");
                    }
                    if (section > 0) {
                        println("    </SECTION>");
                    }
                    println("</CHAPTER>");
                }
                boolean noNumber = false;
                if ("*".equals(getToken())) {
                    noNumber = true;
                    readToken();
                }
                print("<CHAPTER");
                if (noNumber) {
                    print(" noNumber=\"true\"");
                }
                println(">");
                println("  <TITLE>");
                println("    <LATEX language=\"de\">");
                println("       " + readCurlyBraceContents());
                println("    </LATEX>");
                println("  </TITLE>");
                println("  <INTRODUCTION>");
                println("    <LATEX language=\"de\">");
                println("      <![CDATA[");
                chapter++;
                section = 0;
            } else if ("\\section".equals(token)) {
                println("      ]]>");
                println("    </LATEX>");
                println("  </INTRODUCTION>");
                if (section > 0) {
                    println("</SECTION>");
                }
                boolean noNumber = false;
                if ("*".equals(getToken())) {
                    noNumber = true;
                    readToken();
                }
                print("<SECTION");
                if (noNumber) {
                    print(" noNumber=\"true\"");
                }
                println(">");
                println("  <TITLE>");
                println("    <LATEX language=\"de\">");
                println("       " + readCurlyBraceContents());
                println("    </LATEX>");
                println("  </TITLE>");
                println("  <INTRODUCTION>");
                println("    <LATEX language=\"de\">");
                println("      <![CDATA[");
                section++;
            } else {
                print(token);
            }
        }
        if (chapter > 0) {
            println("      ]]>");
            println("    </LATEX>");
            if (introduction) {
                println("  </INTRODUCTION>");
            }
            if (section > 0) {
                println("    </SECTION>");
            }
            println("</CHAPTER>");
        }
        println("</QEDEQ>");
    }

    private void printMathTillEnd(final String curly) throws SourceFileException {
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
        output.println("\\begin{" + curly + "}");
        output.println(buffer);
        output.println("\\end{" + curly + "}");
        output.println();
        printMath(buffer);
    }

    private void printMathTillToken(final String token) throws SourceFileException {
        final StringBuffer buffer = new StringBuffer();
        do {
            final String item = readToken();
            if (token.equals(item)) {
                break;
            } else {
                buffer.append(item);
            }
        } while (true);
        output.print(token);
        output.print(buffer);
        output.println(token);
        printMath(buffer);
    }

    private void printMath(final StringBuffer buffer) throws SourceFileException {
        output.println("%%QEDEQ BEGIN");
        final LatexMathParser parser = new LatexMathParser(buffer, operators);
        try {
            boolean notFirst = false;
            while (true) {
                final Term term = parser.readTerm();
                if (term == null) {
                    break;
                }
                if (notFirst) {
                    output.println();
                    notFirst = true;
                }
                output.print(term.getQedeqXml());
            }
        } catch (ParserException e) {
            System.out.println(input.getRow() + ":" + input.getColumn() + ":");
            System.out.println(e.getMessage());
            System.out.println(input.getLine().replace('\t', ' ').replace('\015', ' '));
            final StringBuffer pointer = StringUtility.getSpaces(input.getColumn());
            pointer.append('^');
            System.out.println(pointer);
        } finally {
            output.println("%%QEDEQ END");
            output.println();
        }
    }

    private void printQedeqHeader() {
        println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        println("<QEDEQ");
        println("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        println("    xsi:noNamespaceSchemaLocation=\"http://www.qedeq.org/0_01_10/xml/qedeq.xsd\">");
        println("  <HEADER email=\"mime@qedeq.org\">");
        println("    <SPECIFICATION name=\"qedeq_sample1\" ruleVersion=\"1.00.00\">");
        println("      <LOCATIONS>");
        println("        <LOCATION value=\"http://qedeq.org/0.01.10/sample\"/>");
        println("      </LOCATIONS>");
        println("    </SPECIFICATION>");
        println("    <TITLE>");
        println("      <LATEX language=\"en\">");
        println("         Mathematical Example Module");
        println("      </LATEX>");
        println("    </TITLE>");
        println("    <ABSTRACT>");
        println("      <LATEX language=\"en\">");
        println("         In this very first qedeq module the XML specification is demonstrated.");
        println("      </LATEX>");
        println("    </ABSTRACT>");
        println("    <AUTHORS>");
        println("      <AUTHOR email=\"michael@meyling.com\">");
        println("        <NAME>");
        println("          <LATEX language=\"de\">");
        println("            Michael Meyling");
        println("          </LATEX>");
        println("        </NAME>");
        println("      </AUTHOR>");
        println("    </AUTHORS>");
        println("  </HEADER>");
    }

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


    private final void println(final String line) {
        output.println(line);
    }

    private final void print(final String line) {
        output.print(line);
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
