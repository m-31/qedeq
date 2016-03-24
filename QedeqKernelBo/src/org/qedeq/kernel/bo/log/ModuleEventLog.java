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
import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.common.QedeqBo;


/**
 * This class organizes the logging of module events.
 *
 * TODO mime 20080317: must this be a singleton?
 *
 * @author  Michael Meyling
 */
public final class ModuleEventLog implements ModuleEventListener {

    /** This class. */
    private static final Class CLASS = ModuleEventLog.class;

    /** The one and only instance. */
    private static ModuleEventLog instance = new ModuleEventLog();

    /** The loggers. */
    private List loggers = new ArrayList();


    /**
     * Get instance of Logger.
     *
     * @return  singleton
     */
    public static final ModuleEventLog getInstance() {
        return instance;
    }

    /**
     * Don't use me outside of this class.
     */
    private ModuleEventLog() {
        // nothing to do
    }

    /**
     * Add listener.
     *
     * @param   log Add this listener.
     */
    public final void addLog(final ModuleEventListener log) {
        loggers.add(log);
    }

    /**
     * Remove listener.
     *
     * @param   log Remove this listener.
     */
    public final void removeLog(final ModuleEventListener log) {
        loggers.remove(log);
    }

    /**
     * Add stream listener.
     *
     * @param   out Put messages into this stream.
     */
    public final void addLog(final PrintStream out) {
        final ModuleEventListener log = new DefaultModuleEventListener(out);
        loggers.add(log);
    }

    public void addModule(final QedeqBo prop) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the ModuleEventListener is free of programming errors...
                ((ModuleEventListener) loggers.get(i)).addModule(prop);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "addModule",
                    "ModuleEventListener throwed RuntimeException", e);
            }
        }
    }

    public void stateChanged(final QedeqBo prop) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the ModuleEventListener is free of programming errors...
                ((ModuleEventListener) loggers.get(i)).stateChanged(prop);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "stateChanged",
                    "ModuleEventListener throwed RuntimeException", e);
            }
        }
    }

    public void removeModule(final QedeqBo prop) {
        for (int i = 0; i < loggers.size(); i++) {
            try {   // we don't know if the ModuleEventListener is free of programming errors...
                ((ModuleEventListener) loggers.get(i)).removeModule(prop);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "removeModule",
                    "ModuleEventListener throwed RuntimeException", e);
            }
        }
    }


}
