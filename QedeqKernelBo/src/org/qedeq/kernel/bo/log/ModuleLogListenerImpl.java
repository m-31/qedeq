/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.log;

import java.io.PrintStream;

import org.qedeq.base.utility.DateUtility;

/**
 * Listener that writes events to a stream.
 *
 * @author  Michael Meyling
 */
public final class ModuleLogListenerImpl implements ModuleLogListener {

    /** Stream for output. */
    private PrintStream out = System.out;

    /** For this module we are logging. */
    private final String moduleUrl;

    /**
     * Constructor.
     *
     * @param   moduleUrl   We log for this module.
     */
    public ModuleLogListenerImpl(final String moduleUrl) {
        this(moduleUrl, System.out);
    }

    /**
     * Constructor.
     *
     * @param   moduleUrl   We log for this module.
     * @param   stream      Print to this stream.
     */
    public ModuleLogListenerImpl(final String moduleUrl, final PrintStream stream) {
        this.moduleUrl = moduleUrl;
        this.out = stream;
    }

    /**
     * Set output stream.
     *
     * @param   stream  Output stream.
     */
    public final void setPrintStream(final PrintStream stream) {
        this.out = stream;
    }

    public final void logMessageState(final String text) {
        out.println(moduleUrl);
        out.println(DateUtility.getTimestamp() + " state:   " + text);
    }

}
