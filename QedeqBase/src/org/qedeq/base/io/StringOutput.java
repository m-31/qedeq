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
public class StringOutput extends AbstractOutput {

    /** Our buffer. */
    private final StringBuffer output;

    /**
     * Constructor.
     */
    public StringOutput() {
        super();
        output = new StringBuffer();
    }

    public void append(final String text) {
        output.append(text);
    }

    public String toString() {
        return output.toString();
    }

}
