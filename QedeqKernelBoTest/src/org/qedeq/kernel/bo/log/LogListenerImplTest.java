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
public class LogListenerImplTest extends QedeqBoTestCase {

    private LogListenerImpl listener;
    private ByteArrayOutputStream out;

    protected void setUp() throws Exception {
        super.setUp();
        out = new ByteArrayOutputStream();
        listener = new LogListenerImpl(new PrintStream(out));
    }

    public void testLogFailureReply() throws Exception{
        listener.logFailureReply("failure and error", "http://mysite.org/mymodule.xml", "was this logged?");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(out.toString("UTF-8").trim().endsWith("was this logged?"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("reply"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("failure and error"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.toString("UTF-8").length();
        listener.logFailureReply("failure and error", "http://mysite.org/mymodule.xml", "this is the end");
        assertTrue(out.toString("UTF-8").trim().endsWith("this is the end"));
        assertTrue(pos <= out.toString("UTF-8").indexOf("reply", pos));
        assertTrue(pos <= out.toString("UTF-8").indexOf("failure and error", pos));
        assertTrue(-1 == out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogSuccessfulReply() throws Exception{
        listener.logSuccessfulReply("successfully loaded", "http://mysite.org/mymodule.xml");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("successfully loaded"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.toString("UTF-8").length();
        listener.logSuccessfulReply("successfully loaded", "http://mysite.org/mymodule.xml");
        assertTrue(-1 == out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml", pos));
        assertTrue(pos <= out.toString("UTF-8").indexOf("successfully loaded", pos));
    }

    public void testLogMessageState() throws Exception{
        listener.logMessageState("loading 10 percent complete", "http://mysite.org/mymodule.xml");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("loading 10 percent complete"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
//        System.out.println(out.toString("UTF-8"));
        int pos = out.toString("UTF-8").length();
        listener.logMessageState("loading 20 percent complete", "http://mysite.org/mymodule.xml");
        assertTrue(pos <= out.toString("UTF-8").indexOf("loading 20 percent complete", pos));
        assertTrue(-1 == out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogFailureState() throws Exception{
        listener.logFailureState("error state occured", "http://mysite.org/mymodule.xml", "was this logged?");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(out.toString("UTF-8").trim().endsWith("was this logged?"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("failure"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("error state occured"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.toString("UTF-8").length();
        listener.logFailureState("error state occured", "http://mysite.org/mymodule.xml", "was this logged!");
        assertTrue(out.toString("UTF-8").trim().endsWith("was this logged!"));
        assertTrue(pos <= out.toString("UTF-8").indexOf("failure", pos));
        assertTrue(pos <= out.toString("UTF-8").indexOf("error state occured", pos));
        assertTrue(-1 == out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogSuccesfulState() throws Exception{
        listener.logSuccessfulState("we are the champions", "http://mysite.org/mymodule.xml");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("success"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("we are the champions"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.toString("UTF-8").length();
        listener.logSuccessfulState("rain begins to fall", "http://mysite.org/mymodule.xml");
        assertTrue(pos <= out.toString("UTF-8").indexOf("success", pos));
        assertTrue(pos <= out.toString("UTF-8").indexOf("rain begins to fall", pos));
        assertTrue(-1 == out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogRequest() throws Exception{
        listener.logRequest("validating", "http://mysite.org/mymodule.xml");
//        System.out.println(out.toString("UTF-8"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("validating"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml"));
        int pos = out.toString("UTF-8").length();
        listener.logRequest("validated", "http://mysite.org/mymodule.xml");
        assertTrue(pos <= out.toString("UTF-8").indexOf("validated", pos));
        assertTrue(-1 == out.toString("UTF-8").indexOf("http://mysite.org/mymodule.xml", pos));
    }

    public void testLogMessage() throws Exception{
        listener.logMessage("we are on a yellow submarine");
        assertTrue(0 <= out.toString("UTF-8").indexOf("we are on a yellow submarine"));
        int pos = out.toString("UTF-8").length();
        listener.logMessage("we are on a blue submarine");
        assertTrue(pos <= out.toString("UTF-8").indexOf("we are on a blue submarine", pos));
    }

    public void testSetPrintStream() throws Exception {
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        listener.setPrintStream(new PrintStream(out2));
        listener.logFailureReply("failure", "http://mysite.org/mymodule.xml", "was this logged?");
//        System.out.println(out2.toString("UTF-8"));
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