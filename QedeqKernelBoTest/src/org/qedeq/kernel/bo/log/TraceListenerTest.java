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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;

/**
 * Test class.
 *
 * @author  Michael Meyling
 */
public class TraceListenerTest extends QedeqBoTestCase {

    private TraceListener listener;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private boolean oldTrace;

    protected void setUp() throws Exception {
        super.setUp();
        out.reset();
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.removeAllAppenders();
        rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(new WriterAppender(
            new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), out));
        oldTrace = Trace.isTraceOn();
        Trace.setTraceOn(true);
        listener = new TraceListener();
        QedeqLog.getInstance().addLog(listener);
//        LogFactory.getFactory().getInstance("log");
    }

    protected void tearDown() throws Exception {
        QedeqLog.getInstance().removeLog(listener);
        Trace.setTraceOn(oldTrace);
        super.tearDown();
    }

    public void testLogFailureReply() throws Exception{
        listener.logFailureReply("failure and error", "http://mysite.org/mymodule.xml", "was this logged?");
        String out = getLogfileContents();
        assertTrue(out.trim().endsWith("was this logged?"));
        assertTrue(0 <= out.indexOf("reply"));
        assertTrue(0 <= out.indexOf("failure and error"));
        assertTrue(0 <= out.indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.length();
        listener.logFailureReply("failure and error", "http://mysite.org/mymodule.xml", "this is the end");
        out = getLogfileContents();
        assertTrue(out.trim().endsWith("this is the end"));
        assertTrue(pos <= out.indexOf("reply", pos));
        assertTrue(pos <= out.indexOf("failure and error", pos));
        assertTrue(-1 == out.indexOf("http://mysite.org/mymodule.xml", pos));
    }

    private String getLogfileContents() throws UnsupportedEncodingException {
        return out.toString("UTF-8");
    }

    public void testLogSuccessfulReply() throws Exception{
        listener.logSuccessfulReply("successfully loaded", "http://mysite.org/mymodule.xml");
        String out = getLogfileContents();
        assertTrue(0 <= out.indexOf("successfully loaded"));
        assertTrue(0 <= out.indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.length();
        listener.logSuccessfulReply("successfully loaded", "http://mysite.org/mymodule.xml");
        out = getLogfileContents();
        assertTrue(-1 == out.indexOf("http://mysite.org/mymodule.xml", pos));
        assertTrue(pos <= out.indexOf("successfully loaded", pos));
    }

    public void testLogMessageState() throws Exception{
        listener.logMessageState("loading 10 percent complete", "http://mysite.org/mymodule.xml");
        String out = getLogfileContents();
        assertTrue(0 <= out.indexOf("loading 10 percent complete"));
        assertTrue(0 <= out.indexOf("http://mysite.org/mymodule.xml"));
//        System.out.println(out);
        int pos = out.length();
        listener.logMessageState("loading 20 percent complete", "http://mysite.org/mymodule.xml");
        out = getLogfileContents();
        assertTrue(pos <= out.indexOf("loading 20 percent complete", pos));
        assertTrue(-1 == out.indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogFailureState() throws Exception{
        listener.logFailureState("error state occured", "http://mysite.org/mymodule.xml", "was this logged?");
        String out = getLogfileContents();
        assertTrue(out.trim().endsWith("was this logged?"));
        assertTrue(0 <= out.indexOf("failure"));
        assertTrue(0 <= out.indexOf("error state occured"));
        assertTrue(0 <= out.indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.length();
        listener.logFailureState("error state occured", "http://mysite.org/mymodule.xml", "was this logged!");
        out = getLogfileContents();
        assertTrue(out.trim().endsWith("was this logged!"));
        assertTrue(pos <= out.indexOf("failure", pos));
        assertTrue(pos <= out.indexOf("error state occured", pos));
        assertTrue(-1 == out.indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogSuccesfulState() throws Exception{
        listener.logSuccessfulState("we are the champions", "http://mysite.org/mymodule.xml");
        String out = getLogfileContents();
        assertTrue(0 <= out.indexOf("success"));
        assertTrue(0 <= out.indexOf("we are the champions"));
        assertTrue(0 <= out.indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.length();
        listener.logSuccessfulState("rain begins to fall", "http://mysite.org/mymodule.xml");
        out = getLogfileContents();
        assertTrue(pos <= out.indexOf("success", pos));
        assertTrue(pos <= out.indexOf("rain begins to fall", pos));
        assertTrue(-1 == out.indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogRequest() throws Exception{
        listener.logRequest("validating", "http://mysite.org/mymodule.xml");
        String out = getLogfileContents();
        assertTrue(0 <= out.indexOf("validating"));
        assertTrue(0 <= out.indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.length();
        listener.logRequest("validated", "http://mysite.org/mymodule.xml");
        out = getLogfileContents();
        assertTrue(pos <= out.indexOf("validated", pos));
        assertTrue(-1 == out.indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogMessage() throws Exception{
        listener.logMessage("we are on a yellow submarine");
        String out = getLogfileContents();
        assertTrue(0 <= out.indexOf("we are on a yellow submarine"));
        int pos = out.length();
        listener.logMessage("we are on a blue submarine");
        out = getLogfileContents();
        assertTrue(pos <= out.indexOf("we are on a blue submarine", pos));
    }

}