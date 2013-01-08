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

    /** Tab level of current line. This is equal to spaces before any character is
     * written. After writing to the current line this value is fixed and doesn't change even
     * if the tab level is changed.
     */
    private String spacesForCurrentLine = "";

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
        final String[] lines = StringUtility.split(ws, "\n");
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                println();
            }
            addWsWithoutCR(lines[i]);
        }
    }

    /**
     * Add whitespace to output. Must not contain CRs.
     *
     * @param   ws  Add this whitespace.
     */
    private void addWsWithoutCR(final String ws) {
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
                // forget non fitting part of white space
                if (col != 0) {
                    appendFittingPart(wsBuffer.toString());
                    append("\n");
                }
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
        // remember tabular spaces when we start writing
        if (col == 0 && part.length() > 0) {
            setTabLevel();
        }
        tokenBuffer.append(part);
    }

    /**
     * Flush output.
     */
    public void flush() {
        addWsWithoutCR("");
        appendFittingPart(wsBuffer.toString());
        wsBuffer.setLength(0);
    }

    /**
     * Append a part of given text so that the current maximum column is not exceeded.
     *
     * @param   txt     Write this text.
     * @return  Length of written characters.
     */
    private int appendFittingPart(final String txt) {
        final int columnsLeft = columnsLeft();
        if (columnsLeft > 0) {
            final String part = StringUtility.substring(txt, 0, columnsLeft);
            append(part);
            col += part.length();
            return part.length();
        } else if (columnsLeft < 0) {
            append(txt);
            col += txt.length();
            return txt.length();
        }
        return 0;
    }

    /**
     * Print character to output.
     *
     * @param   c   Append this.
     */
    public void print(final char c) {
        print("" + c);
    }

    /**
     * Print text and split at white space if text doesn't fit within maximum column size.
     * Also flushes output.
     *
     * @param   text    Append this.
     */
    public void print(final String text) {
        flush();
        if (text == null) {
            addToken("null");
        } else {
            final String[] lines = StringUtility.split(text, "\n");
            for (int i = 0; i < lines.length; i++) {
                final Splitter split = new Splitter(lines[i]);
                while (split.hasNext()) {
                    final String token = split.nextToken();
                    final boolean isWhitespace = token.trim().length() == 0;
                    if (isWhitespace) {
                        addWsWithoutCR(token);
                    } else {
                        addToken(token);
                    }
                }
                if (i + 1 < lines.length) {
                    println();
                }
            }
        }
        flush();
    }

    /**
     * Append text directly to output device.
     *
     * @param   text    Append this text.
     */
    public abstract void append(final String text);

    /**
     * Get writing position.
     *
     * @return  Writing position.
     */
    public abstract long getPosition();

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
                // remember tabular spaces when we start writing
                setTabLevel();
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
     * TODO 20110104 m31: should't we use spacesForCurrentLine also?
     *
     * @param   length    Check if a text of this length could be appended without line break.
     * @return  Does it fit?
     */
    private boolean fits(final int length) {
        return breakAt <= 0 || col + length <= breakAt;
    }

    /**
     * How many columns have we left?
     *
     * @return  Columns left. Returns -1 if there is no limit.
     */
    public int columnsLeft() {
        if (breakAt <= 0) {
            return -1;
        } else {
            return Math.max(0, breakAt - col);
        }
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
        if (col == 0 && spaces.toString().trim().length() > 0) {
            setTabLevel();
            appendSpaces();
        }
        append("\n");
        col = 0;
    }

    /**
     * Skip until given column. To do this we append spaces.
     *
     * @param   column  Skip to this column.
     */
    public void skipToColumn(final int column) {
        for (int i = col; i < column; i++) {
            printWithoutSplit(" ");
        }
    }

    /**
     * Reset tab level to zero.
     */
    public final void clearLevel() {
        // flush();
        spaces.setLength(0);
    }

    /**
     * Decrement tab level.
     */
    public final void popLevel() {
        if (spaces.length() > 0) {
            spaces.setLength(spaces.length() - 2);
        }
    }

    /**
     * Decrement tab level.
     *
     * @param   characters  Number of characters to reduce from tab level.
     */
    public final void popLevel(final int characters) {
        if (spaces.length() > 0) {
            spaces.setLength(Math.max(spaces.length() - characters, 0));
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
     * Set current tab string.
     *
     * @param   level   Tab string.
     */
    public final void setLevel(final String level) {
        spaces.setLength(0);
        spaces.append(level);
    }

    /**
     * Increment tab level (this are two spaces).
     */
    public final void pushLevel() {
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
     * Set current tab level to current level. Might change unwritten lines.
     */
    public final void setTabLevel() {
        spacesForCurrentLine = spaces.toString();
    }

    /**
     * Set number of maximum columns. If possible we break before we reach this column number.
     * If less or equal to zero no line breaking is done automatically.
     *
     * @param   columns Maximum column size.
     */
    public void setColumns(final int columns) {
        if (columns < 0) {
            breakAt = 0;
        } else {
            breakAt = columns;
        }
    }

    /**
     * Return number of maximum columns. If equal to zero no line breaking is done automatically.
     *
     * @return  Maximum column size.
     */
    public final int getColumns() {
        return breakAt;
    }

    /**
     * Append tabulation and increase current column.
     */
    private void appendSpaces() {
        append(spacesForCurrentLine.toString());
        col += spacesForCurrentLine.length();
    }

}
