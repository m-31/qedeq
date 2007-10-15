/* $Id: Trace.java,v 1.4 2007/09/02 00:13:41 m31 Exp $
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

package org.qedeq.kernel.trace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Developer trace.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class Trace {

    /**
     * Constructor.
     */
    private Trace() {
        // don't call me
    }

    /**
     * Trace object.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   object          Object to trace.
     */
    public static void trace(final Object tracingObject, final String method,
            final Object object) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(object);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(object);
        }
    }

    /**
     * Trace throwable.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   throwable       Throwable to trace.
     */
    public static void trace(final Object tracingObject, final String method,
            final Throwable throwable) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(throwable, throwable);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(throwable, throwable);
        }
    }

    /**
     * Trace fatal throwable and extra description.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   description     Further information.
     * @param   throwable       Throwable to trace.
     */
    public static void fatal(final Object tracingObject, final String method,
            final String description, final Throwable throwable) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        log.fatal(description, throwable);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        log.fatal(description, throwable);
    }

    /**
     * Trace throwable and extra description.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   description     Further information.
     * @param   throwable       Throwable to trace.
     */
    public static void trace(final Object tracingObject, final String method,
            final String description, final Throwable throwable) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(description, throwable);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(description, throwable);
        }
    }

    /**
     * Trace method begin. Should be followed by an analogous
     * {@link #end(Object, String)} call.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     */
    public static void begin(final Object tracingObject, final String method) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug("begin");
        }
    }

    /**
     * Trace method begin. Should be followed by an analogous {@link #end(Class, String)} call.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     */
    public static void begin(final Class tracingClass, final String method) {
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug("begin");
        }
    }

    /**
     * Trace method end.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     */
    public static void end(final Object tracingObject, final String method) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug("end");
        }
    }

    /**
     * Trace method end.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     */
    public static void end(final Class tracingClass, final String method) {
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug("end");
        }
    }

    /**
     * Trace message.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   message         Message.
     */
    public static void info(final Object tracingObject, final String method,
            final String message) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isInfoEnabled()) {
            log.info(message);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isInfoEnabled()) {
            log.info(message);
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Object tracingObject, final String method,
            final String param, final Object value) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(param + "=" + value);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(param + "=" + value);
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Object tracingObject, final String method,
            final String param, final int value) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(param + "=" + value);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(param + "=" + value);
        }
    }

    /**
     * Trace parameter.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void param(final Object tracingObject, final String method,
            final String param, final boolean value) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(param + "=" + value);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isDebugEnabled()) {
            log.debug(param + "=" + value);
        }
    }

    /**
     * Write stacktrace into trace.
     *
     * @param   tracingObject   Instance that wants to make a trace entry.
     * @param   method          Method of that object.
     */
    public static void traceStack(final Object tracingObject, final String method) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (!log.isDebugEnabled()) {
            return;
        }
        try {
            throw new Exception("Stacktrace");
        } catch (Exception e) {
            log.debug(e, e);
        }
    }

    /**
     * Write stacktrace into trace.
     *
     * @param   tracingClass    Class that wants to make a trace entry.
     * @param   method          Method of that class.
     */
    public static final void traceStack(final Class tracingClass, final String method) {
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (!log.isDebugEnabled()) {
            return;
        }
        try {
            throw new Exception("Stacktrace");
        } catch (Exception e) {
            log.debug(e, e);
        }
    }

    /**
     * Parameter information.
     *
     * @param   tracingObject   Instance that wants to make an info entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void paramInfo(final Object tracingObject, final String method,
            final String param, final Object value) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isInfoEnabled()) {
            log.info(param + "=" + value);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isInfoEnabled()) {
            log.info(param + "=" + value);
        }
    }

    /**
     * Parameter information.
     *
     * @param   tracingObject   Instance that wants to make an info entry.
     * @param   method          Method of that object.
     * @param   param           Parameter to trace.
     * @param   value           Value of parameter.
     */
    public static void paramInfo(final Object tracingObject, final String method,
            final String param, final int value) {
        final Log log = LogFactory.getFactory().getInstance(tracingObject.getClass()
            + "." + method);
        if (log.isInfoEnabled()) {
            log.info(param + "=" + value);
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
        final Log log = LogFactory.getFactory().getInstance(tracingClass
            + "." + method);
        if (log.isInfoEnabled()) {
            log.info(param + "=" + value);
        }
    }

}
