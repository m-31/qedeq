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

package org.qedeq.base.io;

import org.qedeq.base.utility.Splitter;
import org.qedeq.base.utility.StringUtility;


/**
 * Wraps a text output stream.
 *
 * @author  Michael Meyling
 */
public abstract class AbstractOutput {

    /** Tab level. */
    private StringBuffer spaces = new StringBuffer();

    /** Break at this column if greater zero. */
    private int breakAt;

    /** Current column. */
    private int col;

    /** Token buffer. */
    private StringBuffer tokenBuffer = new StringBuffer();

    /** Whitespace buffer. */
    private StringBuffer wsBuffer = new StringBuffer();

    /**
     * Constructor.
     */
    public AbstractOutput() {
    }

    /**
     * Add whitespace to output.
     *
     * @param   ws  Add this whitespace.
     */
    public void addWs(final String ws) {
        if (tokenBuffer.length() > 0) {
            if (fits(wsBuffer.length() + tokenBuffer.length())) {
                if (col == 0) {
                    appendSpaces();
                }
                append(wsBuffer.toString());
                col += wsBuffer.length();
                append(tokenBuffer.toString());
                col += tokenBuffer.length();
            } else {
                // forget old whitespace
                append("\n");
                col = 0;
                appendSpaces();
                append(tokenBuffer.toString());
                col += tokenBuffer.length();
            }
            wsBuffer.setLength(0);
            tokenBuffer.setLength(0);
        }
        wsBuffer.append(ws);
    }

    /**
     * Append token to output.
     *
     * @param   part    Add this part.
     */
    public void addToken(final String part) {
        tokenBuffer.append(part);
    }

    /**
     * Flush output.
     */
    public void flush() {
        addWs("");
        wsBuffer.setLength(0);
    }

    /**
     * Print character to output.
     *
     * @param   c   Append this.
     */
    public void print(final char c) {
        flush();
        print("" + c);
//        if ('\n' == c) {
//            println();
//            return;
//        }
//        if (col == 0) {
//            appendSpaces();
//        } else if (breakAt > 0 && col + 1 > breakAt) {
//            lastColBeforeBreak = col;
//            println();
//            appendSpaces();
//        }
//        append("" + c);
//        col++;
    }

    /**
     * Print spaces and text to output. Treads spaces and CR specially.
     *
     * @param   text    Append this.
     */
    public void print(final String text) {
        flush();
        if (text == null) {
            printWithoutSplit("null");
            return;
        }
        final String[] lines = StringUtility.split(text, "\n");
        for (int i = 0; i < lines.length; i++) {
            final Splitter split = new Splitter(lines[i]);
            while (split.hasNext()) {
                final String token = split.nextToken();
//                if (breakNecessary(token) && lastChar() == ' '
                if (!fits(token) && " ".equals(token)) {
                } else {
                    printWithoutSplit(token);
                }
            }
            if (i + 1 < lines.length) {
                println();
            }
        }
    }

    /**
     * Append text to output device.
     *
     * @param   text    Append this text.
     */
    protected abstract void append(final String text);

    /**
     * Print spaces and text to output.
     *
     * @param   text    Append this.
     */
    public void printWithoutSplit(final String text) {
        flush();
        if (text == null) {
            return;
        }
        if (col == 0) {
            if (text.length() > 0) {
                appendSpaces();
            }
        } else if (!fits(text)) {
            println();
            appendSpaces();
        }
        append(text);
        col += text.length();
    }

    /**
     * Does the text fit to current line?
     *
     * @param   text    Check if this text could be appended without line break.
     * @return  Does it fit?
     */
    private boolean fits(final String text) {
        if (text == null) {
            return true;
        }
        return fits(text.length());
    }

    /**
     * Does a text with given length fit to current line?
     *
     * @param   length    Check if a text of this length could be appended without line break.
     * @return  Does it fit?
     */
    private boolean fits(final int length) {
        return breakAt <= 0 || col + length <= breakAt;
    }

    /**
     * Print object to output.
     *
     * @param   object  Append text representation of this.
     */
    public void print(final Object object) {
        print(String.valueOf(object));
    }

    /**
     * Print spaces text and new line to output.
     *
     * @param   token   Append this.
     */
    public final void println(final String token) {
        print(token);
        println();
    }

    /**
     * Print object and new line to output.
     *
     * @param   object  Append text representation of this.
     */
    public final void println(final Object object) {
        println(String.valueOf(object));
    }

    /**
     * Print new line to output.
     */
    public void println() {
        flush();
        append("\n");
        col = 0;
    }

    /**
     * Reset tab level to zero.
     */
    public final void clearLevel() {
        flush();
        spaces.setLength(0);
    }

    /**
     * Decrement tab level.
     */
    public final void popLevel() {
        flush();
        if (spaces.length() > 0) {
            spaces.setLength(spaces.length() - 2);
        }
    }

    /**
     * Return current tab string.
     *
     * @return  Current tab string.
     */
    public final String getLevel() {
        return spaces.toString();
    }

    /**
     * Increment tab level.
     */
    public final void pushLevel() {
        // FIXME m31 20101024: make flush unnecessary! Perhaps remember old break value?
        // if not we can not pushLevel popLevel and print further on (the same line)!
        flush();
        spaces.append("  ");
    }

    /**
     * Increment tab level with following symbols.
     *
     * @param   symbols Symbols to tab width. Length should be exactly 2 characters!
     */
    public final void pushLevel(final String symbols) {
        spaces.append(symbols);
    }

    /**
     * Set number of maximum columns. If possible we break before we reach this column number.
     * If less or equal to zero no line breaking is done automatically.
     *
     * @param   columns Maximum column size.
     */
    public void setColumns(final int columns) {
        System.out.println("columns: " + columns);
        if (columns < 0) {
            breakAt = 0;
        } else {
            breakAt = columns;
        }
    }

    /**
     * Append tabulation and increase current column.
     */
    private void appendSpaces() {
        append(spaces.toString());
        col += spaces.length();
    }

}
