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

package org.qedeq.base.trace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Developer trace.
 *
 * @author  Michael Meyling
 */
public final class Trace {

    /** Logger for business messages. */
    private static final Log BUSINESS = LogFactory.getFactory().getInstance("log ");

    /** Is tracing on? If not only fatal errors and business messages are logged. */
    private static boolean traceOn = false;

    /**
     * Constructor.
     */
    private Trace() {
        // don't call me
    }

    /**
     * Set tracing on. If not set only fatal errors and business messages are logged.
     *
     * @param   on  Set tracing on?
     */
    public static void setTraceOn(final boolean on) {
        traceOn = on;
    }

    /**
     * Trace business log message. The message is logged on "error" level.
     *
     * @param   message         Business log message.
     */
    public static void log(final String message) {
        BUSINESS.error(message);
    }

    /**
     * Trace business log message. The message is logged on "error" level.
     *
     * @param   message         Business log message.
     * @param   additional      Extra info for the next line.
     */
    public static void log(final String message, final String additional) {
        if (BUSINESS.isErrorEnabled()) {
            BUSINESS.error(message);
            BUSINESS.error("    " + additional);
        }
    }

    /**
     * Trace business log message. The message is logged on "error" level.
     *
     * @param   message         Business log message.
     * @param   additional      Extra info for the next line.
     * @param   description     Further description.
     */
    public static void log(final String message, final String additional, final String description) {
        if (BUSINESS.isErrorEnabled()) {
            BUSINESS.error(message);
            BUSINESS.error("    " + additional);
            BUSINESS.error("    " + description);
        }
    }

    /**
     * Is debug log currently enabled?
     *
     * @param   tracingClass    Class we want to know the debug logging status for.
     * @return  Debug log enabled.
     */
    public static boolean isDebugEnabled(final Class tracingClass) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            return log.isDebugEnabled();
        } else {
            return false;
        }
    }

    /**
     * Trace object.
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   object          Object to trace.
     */
    public static void trace(final Class tracingClass, final Object tracingObject,
            final String method, final Object object) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + object);
            }
        }
    }

    /**
     * Trace object.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     * @param   object          Object to trace.
     */
    public static void trace(final Class tracingClass, final String method,
            final Object object) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + object);
            }
        }
    }

    /**
     * Trace throwable.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   throwable       Throwable to trace.
     */
    public static void trace(final Class tracingClass, final Object tracingObject,
            final String method, final Throwable throwable) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + throwable, throwable);
            }
        }
    }

    /**
     * Trace throwable.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     * @param   throwable       Throwable to trace.
     */
    public static void trace(final Class tracingClass, final String method,
            final Throwable throwable) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + throwable, throwable);
            }
        }
    }

    /**
     * Trace fatal throwable and extra description.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   description     Further information.
     * @param   throwable       Throwable to trace.
     */
    public static void fatal(final Class tracingClass, final Object tracingObject,
            final String method, final String description, final Throwable throwable) {
        final Log log = LogFactory.getFactory().getInstance(tracingClass);
        log.fatal("." + method + " " + description, throwable);
    }

    /**
     * Trace fatal throwable and extra description.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     * @param   description     Further information.
     * @param   throwable       Throwable to trace.
     */
    public static void fatal(final Class tracingClass, final String method,
            final String description, final Throwable throwable) {
        final Log log = LogFactory.getFactory().getInstance(tracingClass);
        log.fatal("." + method + " " + description, throwable);
    }

    /**
     * Trace throwable and extra description.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   description     Further information.
     * @param   throwable       Throwable to trace.
     */
    public static void trace(final Class tracingClass, final Object tracingObject,
            final String method, final String description, final Throwable throwable) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + description, throwable);
            }
        }
    }

    /**
     * Trace throwable and extra description.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     * @param   description     Further information.
     * @param   throwable       Throwable to trace.
     */
    public static void trace(final Class tracingClass, final String method,
            final String description, final Throwable throwable) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + description, throwable);
            }
        }
    }

    /**
     * Trace method begin. Should be followed by an analogous
     * {@link #end(Class, Object, String)} call.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     */
    public static void begin(final Class tracingClass, final Object tracingObject,
            final String method) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + "begin");
            }
        }
    }

    /**
     * Trace method begin. Should be followed by an analogous {@link #end(Class, String)} call.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     */
    public static void begin(final Class tracingClass, final String method) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + "begin");
            }
        }
    }

    /**
     * Trace method end.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     */
    public static void end(final Class tracingClass, final Object tracingObject,
            final String method) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + "end");
            }
        }
    }

    /**
     * Trace method end.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     */
    public static void end(final Class tracingClass, final String method) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + "end");
            }
        }
    }

    /**
     * Trace message.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   message         Message.
     */
    public static void info(final Class tracingClass, final Object tracingObject,
            final String method, final String message) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isInfoEnabled()) {
                log.info("." + method + " " + message);
            }
        }
    }

    /**
     * Trace method message.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     * @param   message         Message.
     */
    public static void info(final Class tracingClass, final String method,
            final String message) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isInfoEnabled()) {
                log.info("." + method + " " + message);
            }
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Class tracingClass, final Object tracingObject,
            final String method, final String param, final Object value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Class tracingClass, final String method,
            final String param, final Object value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Class tracingClass, final Object tracingObject,
            final String method, final String param, final int value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Class tracingClass, final String method,
            final String param, final int value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Class tracingClass, final Object tracingObject,
            final String method, final String param, final boolean value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Class tracingClass, final String method,
            final String param, final boolean value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isDebugEnabled()) {
                log.debug("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Write stacktrace into trace if debug level is on.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     */
    public static void traceStack(final Class tracingClass, final Object tracingObject,
            final String method) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (!log.isDebugEnabled()) {
                return;
            }
            try {
                throw new Exception("Stacktrace");
            } catch (Exception e) {
                log.debug("." + method + " " + e, e);
            }
        }
    }

    /**
     * Write stacktrace into trace if debug level is on.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     */
    public static final void traceStack(final Class tracingClass, final String method) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (!log.isDebugEnabled()) {
                return;
            }
            try {
                throw new Exception("Stacktrace");
            } catch (Exception e) {
                log.debug("." + method + " " + e, e);
            }
        }
    }

    /**
     * Parameter information.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   tracingObject   Instance that wants to make an info entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void paramInfo(final Class tracingClass, final Object tracingObject,
            final String method, final String param, final Object value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isInfoEnabled()) {
                log.info("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Parameter information.
     *
     * @param   tracingClass    Class that wants to make an info entry.
     * @param   method          Method of that class.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void paramInfo(final Class tracingClass, final String method,
            final String param, final Object value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isInfoEnabled()) {
                log.info("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Parameter information.
     * @param   tracingClass    Class that wants to make an info entry.
     * @param   tracingObject   Instance that wants to make an info entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void paramInfo(final Class tracingClass, final Object tracingObject,
            final String method, final String param, final int value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isInfoEnabled()) {
                log.info("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Parameter information.
     *
     * @param   tracingClass    Class that wants to make an info entry.
     * @param   method          Method of that class.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void paramInfo(final Class tracingClass, final String method,
            final String param, final int value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isInfoEnabled()) {
                log.info("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Parameter information.
     * @param   tracingClass    Class that wants to make an info entry.
     * @param   tracingObject   Instance that wants to make an info entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void paramInfo(final Class tracingClass, final Object tracingObject,
            final String method, final String param, final boolean value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isInfoEnabled()) {
                log.info("." + method + " " + param + "=" + value);
            }
        }
    }

    /**
     * Parameter information.
     *
     * @param   tracingClass    Class that wants to make an info entry.
     * @param   method          Method of that class.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void paramInfo(final Class tracingClass, final String method,
            final String param, final boolean value) {
        if (traceOn) {
            final Log log = LogFactory.getFactory().getInstance(tracingClass);
            if (log.isInfoEnabled()) {
                log.info("." + method + " " + param + "=" + value);
            }
        }
    }

}
