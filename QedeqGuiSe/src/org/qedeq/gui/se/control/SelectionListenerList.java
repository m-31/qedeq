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

package org.qedeq.gui.se.control;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.se.common.SourceFileException;


/**
 * This class organizes the listening to error and warning selection events.
 *
 * @author  Michael Meyling
 */
public final class SelectionListenerList implements SelectionListener {

    /** This class. */
    private static final Class CLASS = SelectionListenerList.class;

    /** The loggers. */
    private List listeners = new ArrayList();

    /**
     * Constructor.
     */
    public SelectionListenerList() {
    }

    /**
     * Add listener.
     *
     * @param   list    Add this listener.
     */
    public final void addListener(final SelectionListener list) {
        listeners.add(list);
    }

    /**
     * Remove listener.
     *
     * @param   list    Remove this listener.
     */
    public final void removeListener(final SelectionListener list) {
        listeners.remove(list);
    }

    public void selectError(final int number, final SourceFileException sf) {
        for (int i = 0; i < listeners.size(); i++) {
            try {   // we don't know if the ModuleEventListener is free of programming errors...
                ((SelectionListener) listeners.get(i)).selectError(number, sf);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "selectError",
                    "SelectionListener throwed RuntimeException", e);
            }
        }
    }

    public void selectWarning(final int number, final SourceFileException sf) {
        for (int i = 0; i < listeners.size(); i++) {
            try {   // we don't know if the ModuleEventListener is free of programming errors...
                ((SelectionListener) listeners.get(i)).selectWarning(number, sf);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, "selectWarning",
                    "SelectionListener throwed RuntimeException", e);
            }
        }
    }
}
