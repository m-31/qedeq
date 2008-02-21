/* $Id: DefaultModuleEventListener.java,v 1.2 2008/01/26 12:39:10 m31 Exp $
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
import java.util.Date;
import java.text.SimpleDateFormat;

import org.qedeq.kernel.common.ModuleProperties;

/**
 * Listener that writes events to a stream.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public class DefaultModuleEventListener implements ModuleEventListener {

    /** Stream for output. */
    private PrintStream out = System.out;

    /** Timestamp format. */
    private static final SimpleDateFormat FORMATTER
      = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss,SSS");

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

    public void addModule(final ModuleProperties prop) {
        out.println(getTimestamp() + " Module added. "
            + prop.getStateDescription() + "\n\t" + prop.getUrl());
    }

    public void stateChanged(final ModuleProperties prop) {
        out.println(getTimestamp() + " Module state changed. "
            + prop.getStateDescription() + "\n\t" + prop.getUrl());
    }

    public void removeModule(final ModuleProperties prop) {
        out.println(getTimestamp() + " Module removed. "
            + prop.getStateDescription() + "\n\t" + prop.getUrl());
    }

    private static final String getTimestamp() {
        return FORMATTER.format(new Date()).toString();
    }

}
