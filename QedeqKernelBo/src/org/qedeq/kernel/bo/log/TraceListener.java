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

import org.qedeq.base.trace.Trace;

/**
 * Listener that writes events to the trace.
 *
 * @author  Michael Meyling
 */
public final class TraceListener implements LogListener {

    /** Last modules URL .*/
    private String lastModuleUrl = "";

    /**
     * Constructor.
     */
    public TraceListener() {
        // nothing to do
    }

    public final void logMessageState(final String text, final String url) {
        if (!lastModuleUrl.equals(url)) {
            Trace.log(url);
            lastModuleUrl = (url != null ? url : "");
        }
        Trace.log(" state:   " + text);
    }

    public final void logFailureState(final String text, final String url,
            final String description) {
        if (!lastModuleUrl.equals(url)) {
            Trace.log(url);
            lastModuleUrl = (url != null ? url : "");
        }
        Trace.log(" failure: " + text, description);
    }

    public final void logSuccessfulState(final String text, final String url) {
        if (!lastModuleUrl.equals(url)) {
            Trace.log(url);
            lastModuleUrl = (url != null ? url : "");
        }
        Trace.log(" success: " + text);
    }

    public void logRequest(final String text, final String url) {
        if (!lastModuleUrl.equals(url)) {
            Trace.log(url);
            lastModuleUrl = (url != null ? url : "");
        }
        Trace.log(" request: " + text);
    }

    public final void logMessage(final String text) {
        Trace.log(text);
    }

    public void logSuccessfulReply(final String text, final String url) {
        if (!lastModuleUrl.equals(url)) {
            Trace.log(url);
            lastModuleUrl = (url != null ? url : "");
        }
        Trace.log(" reply:   " + text);
    }

    public void logFailureReply(final String text, final String url, final String description) {
        if (!lastModuleUrl.equals(url)) {
            Trace.log(url);
            lastModuleUrl = (url != null ? url : "");
        }
        Trace.log(" reply:   " + text, description);
    }

}
