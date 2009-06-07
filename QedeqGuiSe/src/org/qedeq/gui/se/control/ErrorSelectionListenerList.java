/* $Id: ErrorSelectionListenerList.java,v 1.2 2008/07/26 07:57:44 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.gui.se.control;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.common.SourceFileException;


/**
 * This class organizes the listening to error selection events.
 *
 * LATER mime 20080415: must this be a singleton?
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class ErrorSelectionListenerList implements ErrorSelectionListener {

    /** This class. */
    private static final Class CLASS = ErrorSelectionListenerList.class;

    /** The one and only instance. */
    private static ErrorSelectionListenerList instance = new ErrorSelectionListenerList();

    /** The loggers. */
    private List listeners = new ArrayList();

    /**
     * Get instance of master listener.
     *
     * @return  singleton
     */
    public static final ErrorSelectionListenerList getInstance() {
        return instance;
    }

    /**
     * Don't use me outside of this class.
     */
    private ErrorSelectionListenerList() {
    }

    /**
     * Add listener.
     *
     * @param   log Add this listener.
     */
    public final void addListener(final ErrorSelectionListener list) {
        listeners.add(list);
    }

    /**
     * Remove listener.
     *
     * @param   log Remove this listener.
     */
    public final void removeListener(final ErrorSelectionListener list) {
        listeners.remove(list);
    }

    public void selectError(final int errorNumber, final SourceFileException sf) {
        for (int i = 0; i < listeners.size(); i++) {
            try {   // we don't know if the ModuleEventListener is free of programming errors...
                ((ErrorSelectionListener) listeners.get(i)).selectError(errorNumber, sf);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "selectError",
                    "ErrorSelectionListener throwed RuntimeException", e);
            }
        }
    }

}
