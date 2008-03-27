/* $Id: Xml2OtherGui.java,v 1.3 2008/03/27 05:12:45 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.rel.test.gui;


import org.qedeq.kernel.test.KernelFacade;
import org.qedeq.kernel.trace.Trace;


/**
 * Test application.
 *
 * @version $Revision: 1.3 $
 * @author    Michael Meyling
 */
public final class Xml2OtherGui  {

    /** This class. */
    private static final Class CLASS = Xml2OtherGui.class;

    /** Configuration file location. */
    private static final String CONFIG_XML2OTHER_PROPERTIES = "config/xml2other.properties";


    /**
     * Constructor.
     */
    private Xml2OtherGui() {
        // nothing to do
    }

    /**
     * Main method.
     *
     * @param   args        Not used.
     */
    public static final void main(final String[] args) {
        final String method = "main(String[])";
        try {
            Trace.begin(CLASS, method);
            Trace.trace(CLASS, method, "after initialization of trace");
            final StarterDialog starterDialog = new StarterDialog(
                KernelFacade.getKernelContext().getDescriptiveKernelVersion(), 
                CONFIG_XML2OTHER_PROPERTIES);
            Trace.trace(CLASS, method, "before show of starter dialog");
            starterDialog.show();
            Trace.trace(CLASS, method, "after show of starter dialog");
        } catch (final Exception e) {
            e.printStackTrace();
            Trace.trace(CLASS, method, e);
        } catch (final Error e) {
            e.printStackTrace();
            Trace.trace(CLASS, method, e);
        } finally {
            Trace.end(CLASS, method);
        }
    }

}
