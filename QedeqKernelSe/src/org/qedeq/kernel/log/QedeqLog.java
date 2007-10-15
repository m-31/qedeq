/* $Id: QedeqLog.java,v 1.3 2007/09/02 00:13:41 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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
import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.trace.Trace;


/**
 * This class organizes the logging.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public final class QedeqLog implements LogListener {

    /** The one and only instance. */
    private static QedeqLog instance = new QedeqLog();

    /** The loggers. */
    private List loggers = new ArrayList();


    /**
     * Get instance of Logger.
     *
     * @return  singleton
     */
    public static final QedeqLog getInstance() {
        return instance;
    }


    /**
     * Don't use me outside of this class.
     */
    private QedeqLog() {
    }

    /**
     * Add listener.
     *
     * @param   log Add this listener.
     */
    public final void addLog(final LogListener log) {
        loggers.add(log);
        Trace.paramInfo(this, "addLog(LogListener)", "log", log.getClass());
    }

    /**
     * Add stream listener.
     *
     * @param   out Put messages into this stream.
     */
    public final void addLog(final PrintStream out) {
        final LogListener log = new LogListenerImpl(out);
        loggers.add(log);
    }

    public void logMessageState(final String text, final URL url) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logMessageState(text, url);
            } catch (RuntimeException e) {
                Trace.fatal(this, "logMessageState", "LogListener throwed RuntimeException", e);
            }
        }
    }

    public void logFailureState(final String text, final URL url, final String description) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logFailureState(text, url, description);
            } catch (RuntimeException e) {
                Trace.fatal(this, "logFailureState", "LogListener throwed RuntimeException", e);
            }
        }
    }

    public void logSuccessfulState(final String text, final URL url) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logSuccessfulState(text, url);
            } catch (RuntimeException e) {
                Trace.fatal(this, "logSuccessfulState", "LogListener throwed RuntimeException", e);
            }
        }
    }

    public void logRequest(final String text) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logRequest(text);
            } catch (RuntimeException e) {
                Trace.fatal(this, "logRequest", "LogListener throwed RuntimeException", e);
            }
        }
    }

    public void logSuccessfulReply(final String text) {
        try {   // we don't know if the LogListener is free of programming errors...
            for (int i = 0; i < loggers.size(); i++) {
                ((LogListener) loggers.get(i)).logSuccessfulReply(text);
            }
        } catch (RuntimeException e) {
            Trace.fatal(this, "logSuccessfulReply", "LogListener throwed RuntimeException", e);
        }
    }

    public void logFailureReply(final String text, final String description) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logFailureReply(text, description);
            } catch (RuntimeException e) {
                Trace.fatal(this, "logFailureReply", "LogListener throwed RuntimeException", e);
            }
        }
    }

    public void logMessage(final String text) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logMessage(text);
            } catch (RuntimeException e) {
                Trace.fatal(this, "logMessage", "LogListener throwed RuntimeException", e);
            }
        }
    }


}
