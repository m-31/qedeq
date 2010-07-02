/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.test;

import java.io.PrintStream;

import org.qedeq.base.utility.DateUtility;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.log.ModuleEventListener;

/**
 * Listener that writes events to a stream.
 *
 * @author  Michael Meyling
 */
public class DefaultModuleEventListener implements ModuleEventListener {

    /** Stream for output. */
    private PrintStream out = System.out;

    /**
     * Constructor.
     */
    public DefaultModuleEventListener() {
        this(System.out);
    }

    /**
     * Constructor.
     *
     * @param   stream  Print to this stream.
     */
    public DefaultModuleEventListener(final PrintStream stream) {
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

    public void addModule(final QedeqBo prop) {
        out.println(DateUtility.getTimestamp() + " Module added. "
            + prop.getStateDescription() + "\n\t" + prop.getUrl());
    }

    public void stateChanged(final QedeqBo prop) {
        out.println(DateUtility.getTimestamp() + " Module state changed. "
            + prop.getStateDescription() + "\n\t" + prop.getUrl());
    }

    public void removeModule(final QedeqBo prop) {
        out.println(DateUtility.getTimestamp() + " Module removed. "
            + prop.getStateDescription() + "\n\t" + prop.getUrl());
    }

}
