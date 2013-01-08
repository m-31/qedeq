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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;


/**
 * Wraps a text output stream.
 *
 * @author  Michael Meyling
 */
public class TextOutput extends AbstractOutput {

    /** Wrapped stream. */
    private final PrintStream output;

    /** File name. */
    private final String name;

    /** Number of characters written. */
    private long position;

    /**
     * Constructor.
     *
     * @param   name        File name.
     * @param   output      Write to this output. Must have the correct encoding.
     */
    public TextOutput(final String name, final PrintStream output) {
        super();
        this.name = name;
        this.output = output;
    }

    /**
     * Constructor.
     *
     * @param   name        File name.
     * @param   output      Write to this output.
     * @param   encoding    Use this encoding.
     */
    public TextOutput(final String name, final OutputStream output, final String encoding) {
        super();
        this.name = name;
        try {
            this.output = new PrintStream(output, false, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Flush output.
     */
    public final void flush() {
        super.flush();
        output.flush();
    }

    /**
     * Close output.
     */
    public final void close() {
        output.close();
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
     * LATER mime 20070131: use something else than PrintStream to get better error support?
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

    public void append(final String text) {
        position += text.length();
        output.print(text);
    }

    public long getPosition() {
        return position;
    }

}
