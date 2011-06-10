/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;


/**
 * This class organizes the logging.
 *
 * TODO 20110606 m31: this class is a singleton but it would be better if it is not
 *                    to accomplish this we must put a getLogInstance method in all
 *                    important BO classes.
 *
 * @author  Michael Meyling
 */
public final class QedeqLog implements LogListener {

    /** This class. */
    private static final Class CLASS = QedeqLog.class;

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
    public synchronized void addLog(final LogListener log) {
        loggers.add(log);
        Trace.paramInfo(CLASS, this, "addLog(LogListener)", "log", log.getClass());
    }

    /**
     * Remove listener.
     *
     * @param   log Remove this listener.
     */
    public synchronized void removeLog(final LogListener log) {
        loggers.remove(log);
        Trace.paramInfo(CLASS, this, "removeLog(LogListener)", "log", log.getClass());
    }

    public synchronized void logMessageState(final String text, final String url) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logMessageState(text, url);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "logMessageState", "LogListener throwed RuntimeException",
                    e);
            }
        }
    }

    public synchronized void logFailureState(final String text, final String url, final String description) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logFailureState(text, url, description);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "logFailureState", "LogListener throwed RuntimeException",
                    e);
            }
        }
    }

    public synchronized void logSuccessfulState(final String text, final String url) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logSuccessfulState(text, url);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "logSuccessfulState",
                    "LogListener throwed RuntimeException", e);
            }
        }
    }

    public synchronized void logRequest(final String text, final String url) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logRequest(text, url);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "logRequest", "LogListener throwed RuntimeException", e);
            }
        }
    }

    public synchronized void logSuccessfulReply(final String text, final String url) {
        try {   // we don't know if the LogListener is free of programming errors...
            for (int i = 0; i < loggers.size(); i++) {
                ((LogListener) loggers.get(i)).logSuccessfulReply(text, url);
            }
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, "logSuccessfulReply", "LogListener throwed RuntimeException",
                e);
        }
    }

    public synchronized void logFailureReply(final String text, final String url, final String description) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logFailureReply(text, url, description);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "logFailureReply", "LogListener throwed RuntimeException",
                    e);
            }
        }
    }

    public synchronized void logMessage(final String text) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the LogListener is free of programming errors...
                ((LogListener) loggers.get(i)).logMessage(text);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "logMessage", "LogListener throwed RuntimeException", e);
            }
        }
    }


}
