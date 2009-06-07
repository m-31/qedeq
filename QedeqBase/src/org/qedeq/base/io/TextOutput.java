/* $Id: TextOutput.java,v 1.1 2008/07/26 07:55:42 m31 Exp $
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

package org.qedeq.base.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Wraps a text output stream.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class TextOutput {

    /** Wrapped stream. */
    private final PrintStream output;

    /** File name. */
    private final String name;

    /** Tab level. */
    private StringBuffer spaces = new StringBuffer();

    /**
     * Constructor.
     *
     * @param   name    File name.
     * @param   output  Write to this output.
     */
    public TextOutput(final String name, final OutputStream output) {
        this.name = name;
        try {
            this.output = new PrintStream(output, false, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);  // should never occur
        }
    }

    /**
     * Print text to output.
     *
     * @param   text    Append this.
     */
    public void print(final String text) {
        output.print(text);
    }

    /**
     * Print spaces and text to output.
     *
     * @param   text    Append this.
     */
    public void levelPrint(final String text) {
        output.print(spaces);
        output.print(text);
    }

    /**
     * Print object to output.
     *
     * @param   object  Append text representation of this.
     */
    public void print(final Object object) {
        output.print(object);
    }

    /**
     * Print spaces text and new line to output.
     *
     * @param   line    Append this.
     */
    public final void println(final String line) {
        output.println(line);
    }

    /**
     * Print spaces, text and new line to output.
     *
     * @param   line    Append this.
     */
    public final void levelPrintln(final String line) {
        output.print(spaces);
        output.println(line);
    }

    /**
     * Print object and new line to output.
     *
     * @param   object  Append text representation of this.
     */
    public final void println(final Object object) {
        output.println(object);
    }

    /**
     * Print new line to output.
     */
    public final void println() {
        output.println();
    }

    /**
     * Flush output.
     */
    public final void flush() {
        output.flush();
    }

    /**
     * Close output.
     */
    public final void close() {
        output.close();
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

    /**
     * Did any error occur during output?
     *
     * @return  Did an error occur?
     */
    public final boolean checkError() {
        return output.checkError();
    }

    /**
     * Get name of output file.
     *
     * @return  File name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Get IO exception that occurred - if any.
     * <p>
     * TODO mime 20070131: use something else than PrintStream to get better error support?
     *
     * @return  Occurred IO exception. Could be <code>null</code>.
     */
    public final IOException getError() {
        if (checkError()) {
            return new IOException("Writing failed.");
        } else {
            return null;
        }
    }

}
