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

import org.qedeq.base.utility.StringUtility;


/**
 * Wraps a text output stream.
 *
 * FIXME m31 20101022: combine with TextOutput
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

    /**
     * Constructor.
     */
    public AbstractOutput() {
    }

    /**
     * Print character to output.
     *
     * @param   c   Append this.
     */
    public void print(final char c) {
        if ('\n' == c) {
            println();
            return;
        }
        if (col == 0) {
            appendSpaces();
        } else if (breakAt > 0 && col + 1 > breakAt) {
            println();
            appendSpaces();
        }
        append("" + c);
        col++;
    }

    /**
     * Append tabulation and increase current column.
     */
    private void appendSpaces() {
        append(spaces.toString());
        col += spaces.length();
    }

    /**
     * Print spaces and text to output.
     *
     * @param   text    Append this.
     */
    public void print(final String text) {
        if (text == null) {
            internalPrint("null");
            return;
        }
        final String[] args = StringUtility.split(text, "\n");
        for (int i = 0; i < args.length; i++) {
            internalPrint(args[i]);
            if (i + 1 < args.length) {
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
    private void internalPrint(final String text) {
        if (col == 0) {
            if (text != null && text.length() > 0) {
                appendSpaces();
            }
        } else if (breakAt > 0 && col + (text != null ? text.length() : 0) > breakAt) {
            println();
            appendSpaces();
        }
        append(text);
        col += text.length();
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
        append("\n");
        col = 0;
    }

    /**
     * Reset tab level to zero.
     */
    public final void clearLevel() {
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
        if (columns < 0) {
            breakAt = 0;
        } else {
            breakAt = columns;
        }
    }

}
