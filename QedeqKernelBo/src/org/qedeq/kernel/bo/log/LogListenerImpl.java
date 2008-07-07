/* $Id: LogListenerImpl.java,v 1.2 2008/03/27 05:16:26 m31 Exp $
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

package org.qedeq.kernel.bo.log;

import java.io.PrintStream;
import java.net.URL;

import org.qedeq.base.utility.DateUtility;

/**
 * Listener that writes events to a stream.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class LogListenerImpl implements LogListener {

    /** Stream for output. */
    private PrintStream out = System.out;

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
        out.println(DateUtility.getTimestamp() + " state:   " + text + "\n\t" + url);
    }

    public final void logFailureState(final String text, final URL url,
            final String description) {
        out.println(DateUtility.getTimestamp() + " failure: " + text + "\n\t" + url + "\n\t"
            + description);
    }

    public final void logSuccessfulState(final String text, final URL url) {
        out.println(DateUtility.getTimestamp() + " success: " + text + "\n\t" + url);
    }

    public void logRequest(final String text) {
        out.println(DateUtility.getTimestamp() + " request: " + text);
    }

    public final void logMessage(final String text) {
        out.println(DateUtility.getTimestamp() + " " + text);
    }


    public void logSuccessfulReply(final String text) {
        out.println(DateUtility.getTimestamp() + " reply:   " + text);
    }

    public void logFailureReply(final String text, final String description) {
        out.println(DateUtility.getTimestamp() + " reply:   " + text + "\n\t" + description);
    }

}
