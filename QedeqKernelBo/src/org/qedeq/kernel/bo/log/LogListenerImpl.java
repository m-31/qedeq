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

package org.qedeq.kernel.bo.log;

import java.io.PrintStream;

import org.qedeq.base.utility.DateUtility;

/**
 * Listener that writes events to a stream.
 *
 * @author  Michael Meyling
 */
public final class LogListenerImpl implements LogListener {

    /** Stream for output. */
    private PrintStream out = System.out;

    /** For this module we logged the last event. */
    private String lastModuleUrl = "";

    /**
     * Constructor.
     */
    public LogListenerImpl() {
        this(System.out);
    }

    /**
     * Constructor.
     *
     * @param   stream  Print to this stream.
     */
    public LogListenerImpl(final PrintStream stream) {
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

    public final void logMessageState(final String text, final String url) {
        if (!lastModuleUrl.equals(url)) {
            out.println(url);
            lastModuleUrl = (url != null ? url : "");
        }
        out.println(DateUtility.getTimestamp() + " state:   " + text);
    }

    public final void logFailureState(final String text, final String url,
            final String description) {
        if (!lastModuleUrl.equals(url)) {
            out.println(url);
            lastModuleUrl = (url != null ? url : "");
        }
        out.println(DateUtility.getTimestamp() + " failure: " + text + "\n\t"
            + description);
    }

    public final void logSuccessfulState(final String text, final String url) {
        if (!lastModuleUrl.equals(url)) {
            out.println(url);
            lastModuleUrl = (url != null ? url : "");
        }
        out.println(DateUtility.getTimestamp() + " success: " + text);
    }

    public void logRequest(final String text, final String url) {
        if (!lastModuleUrl.equals(url)) {
            out.println(url);
            lastModuleUrl = (url != null ? url : "");
        }
        out.println(DateUtility.getTimestamp() + " request: " + text);
    }

    public final void logMessage(final String text) {
        lastModuleUrl = "";
        out.println(DateUtility.getTimestamp() + " " + text);
    }


    public void logSuccessfulReply(final String text, final String url) {
        if (!lastModuleUrl.equals(url)) {
            out.println(url);
            lastModuleUrl = (url != null ? url : "");
        }
        out.println(DateUtility.getTimestamp() + " reply:   " + text);
    }

    public void logFailureReply(final String text, final String url, final String description) {
        if (!lastModuleUrl.equals(url)) {
            out.println(url);
            lastModuleUrl = (url != null ? url : "");
        }
        out.println(DateUtility.getTimestamp() + " reply:   " + text + "\n\t" + description);
    }

}
