/* $Id: IoUtilityTest.java,v 1.1 2008/07/26 07:56:13 m31 Exp $
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

package org.qedeq.base.trace;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link org.qedeq.kernel.utility.TextInput}.
 * 
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class TraceTest extends QedeqTestCase {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Logger rootLogger;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        initLog4J();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void initLog4J() {
        out.reset();
        rootLogger = Logger.getRootLogger();
        rootLogger.removeAllAppenders();
        rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(new WriterAppender(
            new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), out));
    }

    public void testTrace() throws Exception {
        Trace.fatal(this.getClass(), "methodToLog", "##My Description##",
            new NullPointerException());
        final String result = out.toString();
        assertTrue(result.contains("methodToLog"));
        assertTrue(result.contains("testTrace"));
        assertTrue(result.contains("##My Description##"));
        assertTrue(result.contains("FATAL"));
        assertTrue(result.contains("NullPointerException"));
        assertTrue(result.contains(this.getClass().getName()));
    }
    

}
