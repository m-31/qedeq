/* $Id: QedeqTestCase.java,v 1.1 2008/07/26 07:56:13 m31 Exp $
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

package org.qedeq.base.test;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Basis class for all tests.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public abstract class QedeqTestCase extends TestCase {

    static {
        // init Log4J watchdog
        try {
            URL url = Loader.getResource("config/log4j.xml");
            if (url != null) {
                // set properties and watch file every 5 seconds
                DOMConfigurator.configureAndWatch(url.getPath(), 5000);
            } else {
                Logger.getRootLogger().setLevel(Level.ERROR);
            }
        } catch (Exception e) {
            // we ignore this
        }
    }

    /** Destination directory for generated output files. */
    private final File outdir;

    /** Source directory for input files. */
    private final File indir;

    /**
     * Constructor.
     *
     * @param   name    Test case name.
     */
    public QedeqTestCase(final String name) {
        super(name);
        outdir = new File(System.getProperty("qedeq.test.outdir", "../../../qedeq_gen"));
        indir = new File(System.getProperty("qedeq.test.indir", "data"));
    }

    /**
     * Constructor.
     */
    public QedeqTestCase() {
        super();
        outdir = new File(System.getProperty("qedeq.test.outdir", "../../../qedeq_gen"));
        indir = new File(System.getProperty("qedeq.test.indir", "data"));
    }

    /**
     * Get output directory for test output. Might be set initially by setting the system property
     * <code>qedeq.test.outdir</code>.
     *
     * @return  Directory for test output.
     */
    public File getOutdir() {
        return outdir;
    }

    /**
     * Get input directory for test input data. Might be set initially by setting the system
     * property <code>qedeq.test.indir</code>.
     *
     * @return  Directory for test input.
     */
    public File getIndir() {
        return indir;
    }

    /**
     * Get test input data file. Get file relative to {@link #getIndir()}.
     *
     * @param   fileName    Relative file path.
     * @return  Test data file.
     */
    public File getFile(final String fileName) {
        return new File(getIndir(), fileName);
    }

}
