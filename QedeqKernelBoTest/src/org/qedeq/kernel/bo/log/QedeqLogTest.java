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

package org.qedeq.kernel.bo.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.qedeq.kernel.bo.test.QedeqBoTestCase;

/**
 * Test class.
 *
 * @author  Michael Meyling
 */
public class QedeqLogTest extends QedeqBoTestCase {

    private QedeqLog listener;
    private LogListenerImpl listenerBasis;
    private ByteArrayOutputStream out;
    private LogListener thrower;

    protected void setUp() throws Exception {
        super.setUp();
        out = new ByteArrayOutputStream();
        listenerBasis = new LogListenerImpl(new PrintStream(out));
        listener = QedeqLog.getInstance();
        listener.addLog(listenerBasis);
        thrower = new LogListener() {
            public void logMessage(String text) {
                throw new RuntimeException("Boo! This test exception is ok!");
            }

            public void logRequest(String text, String url) {
                throw new RuntimeException("Boo! This test exception is ok!");
            }

            public void logSuccessfulReply(String text, String url) {
                throw new RuntimeException("Boo! This test exception is ok!");
            }

            public void logFailureReply(String text, String url,
                    String description) {
                throw new RuntimeException("Boo! This test exception is ok!");
            }

            public void logMessageState(String text, String url) {
                throw new RuntimeException("Boo! This test exception is ok!");
            }

            public void logFailureState(String text, String url,
                    String description) {
                throw new RuntimeException("Boo! This test exception is ok!");
            }

            public void logSuccessfulState(String text, String url) {
                throw new RuntimeException("Boo! This test exception is ok!");
            }
        };
        listener.addLog(thrower);
    }

    public void tearDown() throws Exception {
        listener.removeLog(thrower);
        listener.removeLog(listenerBasis);
        super.tearDown();
    }

    public void testLogFailureReply() throws Exception{
        listener.logFailureReply("failure and error", "http://mysite.org/mymodule.xml", "was this logged?");
        System.out.println(out.toString("UTF-8"));
        assertTrue(out.toString("UTF-8").trim().endsWith("was this logged?"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("reply"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("failure and error"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
    }

    public void testLogSuccessfulReply() throws Exception{
        listener.logSuccessfulReply("successfully loaded", "http://mysite.org/mymodule.xml");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("successfully loaded"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
    }

    public void testLogMessageState() throws Exception{
        listener.logMessageState("loading 10 percent complete", "http://mysite.org/mymodule.xml");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("loading 10 percent complete"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
    }

    public void testLogFailureState() throws Exception{
        listener.logFailureState("error state occured", "http://mysite.org/mymodule.xml", "was this logged?");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(out.toString("UTF-8").trim().endsWith("was this logged?"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("failure"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("error state occured"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
    }

    public void testLogSuccesfulState() throws Exception{
        listener.logSuccessfulState("we are the champions", "http://mysite.org/mymodule.xml");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("success"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("we are the champions"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
    }

    public void testLogRequest() throws Exception{
        listener.logRequest("validate", "http://mysite.org/mymodule.xml");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("validate"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
    }

    public void testLogMessage() throws Exception{
        listener.logMessage("we are on a yellow submarine");
        assertTrue(0 <= out.toString("UTF-8").indexOf("we are on a yellow submarine"));
    }

    public void testRemoveLog() throws Exception{
        listener.removeLog(null);
        listener.logMessage("we are on a yellow submarine");
        assertTrue(0 <= out.toString("UTF-8").indexOf("we are on a yellow submarine"));
    }

    public void testRemoveLog2() throws Exception{
        listener.removeLog(listenerBasis);
        listener.logMessage("we are on a yellow submarine");
        assertEquals(0, out.size());
    }

    public void testAddLog() throws Exception {
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        listener.addLog(new LogListenerImpl(new PrintStream(out2)));
        listener.logFailureReply("failure", "http://mysite.org/mymodule.xml", "was this logged?");
        assertTrue(out.toString("UTF-8").trim().endsWith("was this logged?"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("reply"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("failure"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
        assertTrue(out2.toString("UTF-8").trim().endsWith("was this logged?"));
        assertTrue(0 <= out2.toString("UTF-8").indexOf("reply"));
        assertTrue(0 <= out2.toString("UTF-8").indexOf("failure"));
        assertTrue(0 <= out2.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
    }

    public void testConstructor() throws Exception {
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        LogListenerImpl listener2 = new LogListenerImpl(new PrintStream(out2));
        listener2.logFailureReply("failure", "http://mysite.org/mymodule.xml", "was this logged?");
//        System.out.println(out2.toString("UTF-8"));
        assertTrue(out2.toString("UTF-8").trim().endsWith("was this logged?"));
        assertTrue(0 <= out2.toString("UTF-8").indexOf("reply"));
        assertTrue(0 <= out2.toString("UTF-8").indexOf("failure"));
        assertTrue(0 <= out2.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
    }

}