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


/**
 * Wraps a text output stream.
 *
 * @author  Michael Meyling
 */
public class StringOutput {

    /** Our buffer. */
    private final StringBuffer output;

    /** Tab level. */
    private StringBuffer spaces = new StringBuffer();

    /**
     * Constructor.
     */
    public StringOutput() {
        output = new StringBuffer();
    }

    /**
     * Print text to output.
     *
     * @param   text    Append this.
     */
    public void print(final String text) {
        output.append(text);
    }

    /**
     * Print character to output.
     *
     * @param   c   Append this.
     */
    public void print(final char c) {
        output.append(c);
    }

    /**
     * Print spaces and text to output.
     *
     * @param   text    Append this.
     */
    public void levelPrint(final String text) {
        output.append(spaces);
        output.append(text);
    }

    /**
     * Print object to output.
     *
     * @param   object  Append text representation of this.
     */
    public void print(final Object object) {
        output.append(object);
    }

    /**
     * Print spaces text and new line to output.
     *
     * @param   line    Append this.
     */
    public final void println(final String line) {
        output.append(line);
        println();
    }

    /**
     * Print spaces, text and new line to output.
     *
     * @param   line    Append this.
     */
    public final void levelPrintln(final String line) {
        output.append(spaces);
        println(line);
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
    public final void println() {
        output.append("\n");
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
     * Increment tab level.
     */
    public final void pushLevel() {
        spaces.append("  ");
    }

    public String toString() {
        return output.toString();
    }

}
