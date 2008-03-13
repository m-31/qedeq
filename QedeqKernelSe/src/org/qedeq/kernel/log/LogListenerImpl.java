/* $Id: LogListenerImpl.java,v 1.1 2007/04/01 07:59:49 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.log;

import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Listener that writes events to a stream.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class LogListenerImpl implements LogListener {

    /** Stream for output. */
    private PrintStream out = System.out;

    /** Timestamp format. */
    private static final SimpleDateFormat FORMATTER
      = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss,SSS");

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

    public final void logMessageState(final String text, final URL url) {
        out.println(getTimestamp() + " state:   " + text + "\n\t" + url);
    }

    public final void logFailureState(final String text, final URL url,
            final String description) {
        out.println(getTimestamp() + " failure: " + text + "\n\t" + url + "\n\t" + description);
    }

    public final void logSuccessfulState(final String text, final URL url) {
        out.println(getTimestamp() + " success: " + text + "\n\t" + url);
    }

    private static final String getTimestamp() {
        return FORMATTER.format(new Date()).toString();
    }

    public void logRequest(final String text) {
        out.println(getTimestamp() + " request: " + text);
    }

    public final void logMessage(final String text) {
        out.println(getTimestamp() + " " + text);
    }


    public void logSuccessfulReply(final String text) {
        out.println(getTimestamp() + " reply:   " + text);
    }

    public void logFailureReply(final String text, final String description) {
        out.println(getTimestamp() + " reply:   " + text + "\n\t" + description);
    }

}
