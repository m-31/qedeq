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

package org.qedeq.base.test;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.UrlUtility;
import org.qedeq.base.utility.StringUtility;

/**
 * Basis class for all tests.
 *
 * @author Michael Meyling
 */
public abstract class QedeqTestCase extends TestCase {

    /** Destination directory for generated output files. */
    private static final File OUTDIR =
        new File(System.getProperty("qedeq.test.outdir", "../../qedeq_test"));

    /** Source directory for input files. */
    private static final File INDIR =
        new File(System.getProperty("qedeq.test.indir", "data"));


    static {
        // init Log4J watchdog
        try {
            final File logConfig = new File(System.getProperty("qedeq.test.log4j",
                "../../qedeq_test/config/log4j.xml"));
            URL url = null;
            if (logConfig.canRead()) {
                url = UrlUtility.toUrl(logConfig);
            }
            if (url == null) {
                // try development environment
                final StringBuffer buffer = new StringBuffer();
                IoUtility.loadFile(new File("../QedeqBuild/resources/config/log4j.xml"), buffer, "UTF8");
                StringUtility.replace(buffer, "\"./log/trace.log\"", "\"../../qedeq_test/log/trace.log\"");
                IoUtility.saveFile(logConfig, buffer, "UTF8");
                if (logConfig.canRead()) {
                    url = UrlUtility.toUrl(logConfig);
                }
            }
            if (url != null) {
                // set properties and watch file every 15 seconds
                DOMConfigurator.configureAndWatch(url.getPath(), 15000);
            } else {
                Logger.getRootLogger().setLevel(Level.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            // we ignore this
        }
    }

    /** Should the methods of this class execute fast? */
    private final boolean fast;

    /**
     * Constructor.
     *
     * @param   name    Test case name.
     */
    public QedeqTestCase(final String name) {
        super(name);
        fast = "true".equalsIgnoreCase(System.getProperty("qedeq.test.fast", "true"));
    }

    /**
     * Constructor.
     */
    public QedeqTestCase() {
        super();
        fast = "true".equalsIgnoreCase(System.getProperty("qedeq.test.fast", "true"));
    }

    /**
     * Get output directory for test output. Might be set initially by setting the system property
     * <code>qedeq.test.outdir</code>.
     *
     * @return  Directory for test output.
     */
    public File getOutdir() {
        return OUTDIR;
    }

    /**
     * Get input directory for test input data. Might be set initially by setting the system
     * property <code>qedeq.test.indir</code>.
     *
     * @return  Directory for test input.
     */
    public File getIndir() {
        return INDIR;
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

    /**
     * Should the test case be finished fast?
     *
     * @return  Should the test case be finished fast?
     */
    private boolean fast() {
        return fast;
    }

    /**
     * Should a slow test method be executed?. Also prints name of current method
     * to System.out.
     * The test case should ask this method at the begin of long running test methods.
     *
     * @return  Should even slow test methods be executed?
     */
    public boolean slow() {
       if (fast()) {
           final StackTraceElement[] st = new Exception().getStackTrace();
           final StackTraceElement e = st[1];
           // find test method
           String name = e.getMethodName();
           for (int i = 1; i < st.length; i++) {
               if (st[i].getMethodName().startsWith("test")) {
                   name = st[i].getMethodName();
                   break;
               }
           }
           System.out.println("skipping slow test "
                   + e.getClassName() + "." + name);
           return false;
       }
       return true;
    }

}
