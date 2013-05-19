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

package org.qedeq.base.trace;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link Trace}.
 *
 * @author Michael Meyling
 */
public class TraceTest extends QedeqTestCase {

    /** Where we write our logging to. */
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    /** Log4J root logger. */
    private Logger rootLogger;

    private boolean oldTrace;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        oldTrace = Trace.isTraceOn();
        initLog4J();
        Trace.setTraceOn(true);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        Trace.setTraceOn(oldTrace);
        rootLogger = Logger.getRootLogger();
        rootLogger.removeAllAppenders();
        rootLogger.setLevel(Level.OFF);
    }

    private void initLog4J() {
        out.reset();
        rootLogger = Logger.getRootLogger();
        rootLogger.removeAllAppenders();
        rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(new WriterAppender(
            new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), out));
    }

    public void testFatal() throws Exception {
        Trace.fatal(this.getClass(), this, "methodToLog", "##My Description##",
            new NullPointerException());
        String result = out.toString();
        assertTrue(result.indexOf("methodToLog") >= 0);
        assertTrue(result.indexOf("testFatal") >= 0);
        assertTrue(result.indexOf("##My Description##") >= 0);
        assertTrue(result.indexOf("FATAL") >= 0);
        assertTrue(result.indexOf("NullPointerException") >= 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.fatal(this.getClass(), this, "methodToLog", "##My Description##",
            new NullPointerException());
        result = out.toString();
        assertTrue(result.indexOf("methodToLog") >= 0);
        assertTrue(result.indexOf("testFatal") >= 0);
        assertTrue(result.indexOf("##My Description##") >= 0);
        assertTrue(result.indexOf("FATAL") >= 0);
        assertTrue(result.indexOf("NullPointerException") >= 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
    }

    public void testFatal2() throws Exception {
        Trace.fatal(this.getClass(), "methodToLog", "##My Description##",
            new NullPointerException());
        String result = out.toString();
        assertTrue(result.indexOf("methodToLog") >= 0);
        assertTrue(result.indexOf("testFatal") >= 0);
        assertTrue(result.indexOf("##My Description##") >= 0);
        assertTrue(result.indexOf("FATAL") >= 0);
        assertTrue(result.indexOf("NullPointerException") >= 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.fatal(this.getClass(), "methodToLog", "##My Description##",
            new NullPointerException());
        result = out.toString();
        assertTrue(result.indexOf("methodToLog") >= 0);
        assertTrue(result.indexOf("testFatal") >= 0);
        assertTrue(result.indexOf("##My Description##") >= 0);
        assertTrue(result.indexOf("FATAL") >= 0);
        assertTrue(result.indexOf("NullPointerException") >= 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
    }

    public void testInfo() throws Exception {
        Trace.info(this.getClass(), this, "testInfo",
            "*Super Info*");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testInfo") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("*Super Info") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.info(this.getClass(), this, "testInfo",
            "*Super Info*");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.info(this.getClass(), this, "testInfo",
            "*Super Info*");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testInfo") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("*Super Info") >= 0);
    }

    public void testInfo2() throws Exception {
        Trace.info(this.getClass(), "testInfo",
            "*Super Info*");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testInfo") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("*Super Info*") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.info(this.getClass(), "testInfo",
            "*Super Info*");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.info(this.getClass(), "testInfo",
            "*Super Info*");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testInfo") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("*Super Info*") >= 0);
    }

    public void testBegin() throws Exception {
        Trace.begin(this.getClass(), this, "testBegin");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testBegin") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("begin") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.begin(this.getClass(), this, "testBegin");
        result = out.toString();
        rootLogger.setLevel(Level.ERROR);
        Trace.begin(this.getClass(), this, "testBegin");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.begin(this.getClass(), this, "testBegin");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.begin(this.getClass(), this, "testBegin");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.begin(this.getClass(), this, "testBegin");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testBegin") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("begin") >= 0);
    }

    public void testBegin2() throws Exception {
        Trace.begin(this.getClass(), "testBegin");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testBegin") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("begin") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.begin(this.getClass(), "testBegin");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.begin(this.getClass(), "testBegin");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.begin(this.getClass(), "testBegin");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.begin(this.getClass(), "testBegin");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testBegin") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("begin") >= 0);
    }

    public void testEnd() throws Exception {
        Trace.end(this.getClass(), this, "testEnd");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testEnd") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("end") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.end(this.getClass(), this, "testEnd");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.end(this.getClass(), this, "testEnd");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.end(this.getClass(), this, "testEnd");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.end(this.getClass(), this, "testEnd");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testEnd") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("end") >= 0);
    }

    public void testEnd2() throws Exception {
        Trace.end(this.getClass(), "testEnd");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testEnd") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("end") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.end(this.getClass(), "testEnd");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.end(this.getClass(), "testEnd");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.end(this.getClass(), "testEnd");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.end(this.getClass(), "testEnd");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testEnd") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("end") >= 0);
    }

    public void testParamString() throws Exception {
        Trace.param(this.getClass(), this, "testParamString", "param", "6868");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamString") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.param(this.getClass(), this, "testParamString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.param(this.getClass(), this, "testParamString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.param(this.getClass(), this, "testParamString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.param(this.getClass(), this, "testParamString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamString") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
    }

    public void testParamString2() throws Exception {
        Trace.param(this.getClass(), "testParamString", "param", "6868");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamString") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.param(this.getClass(), "testParamString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.param(this.getClass(), "testParamString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.param(this.getClass(), "testParamString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.param(this.getClass(), "testParamString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamString") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
    }

    public void testParamInt() throws Exception {
        Trace.param(this.getClass(), this, "testParamInt", "param", 6868);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInt") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.param(this.getClass(), this, "testParamInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.param(this.getClass(), this, "testParamInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.param(this.getClass(), this, "testParamInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.param(this.getClass(), this, "testParamInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInt") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
    }

    public void testParamInt2() throws Exception {
        Trace.param(this.getClass(), "testParamInt", "param", 6868);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInt") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.param(this.getClass(), "testParamInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.param(this.getClass(), "testParamInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.param(this.getClass(), "testParamInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.param(this.getClass(), "testParamInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInt") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
    }

    public void testParamBoolean() throws Exception {
        Trace.param(this.getClass(), this, "testParamBoolean", "param", true);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamBoolean") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("true") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.param(this.getClass(), this, "testParamBoolean", "param", true);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.param(this.getClass(), this, "testParamBoolean", "param", false);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.param(this.getClass(), this, "testParamBoolean", "param", true);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.param(this.getClass(), this, "testParamBoolean", "param", false);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamBoolean") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("false") >= 0);
    }

    public void testParamBoolean2() throws Exception {
        Trace.param(this.getClass(), "testParamBoolean", "param", true);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamBoolean") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("true") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.param(this.getClass(), "testParamBoolean", "param", true);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.param(this.getClass(), "testParamBoolean", "param", false);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.param(this.getClass(), "testParamBoolean", "param", true);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.param(this.getClass(), "testParamBoolean", "param", false);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamBoolean") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("false") >= 0);
    }

    public void testParamObject() throws Exception {
        Object object = new Long("123456789");
        Trace.param(this.getClass(), this, "testParamObject", "param", object);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamObject") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("123456789") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.param(this.getClass(), this, "testParamObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.param(this.getClass(), this, "testParamObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.param(this.getClass(), this, "testParamObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.param(this.getClass(), this, "testParamObject", "param", object);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamObject") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("123456789") >= 0);
    }

    public void testParamObject2() throws Exception {
        Object object = new Long("9876543210");
        Trace.param(this.getClass(), "testParamObject", "param", object);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamObject") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("9876543210") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.param(this.getClass(), "testParamObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.param(this.getClass(), "testParamObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.param(this.getClass(), "testParamObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.param(this.getClass(), "testParamObject", "param", object);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamObject") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("9876543210") >= 0);
    }

    public void testParamInfoString() throws Exception {
        Trace.paramInfo(this.getClass(), this, "testParamInfoString", "param", "6868");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoString") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.paramInfo(this.getClass(), this, "testParamInfoString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.paramInfo(this.getClass(), this, "testParamInfoString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.paramInfo(this.getClass(), this, "testParamInfoString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.paramInfo(this.getClass(), this, "testParamInfoString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoString") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
    }

    public void testParamInfoString2() throws Exception {
        Trace.paramInfo(this.getClass(), "testParamInfoString", "param", "6868");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoString") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.paramInfo(this.getClass(), "testParamInfoString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.paramInfo(this.getClass(), "testParamInfoString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.paramInfo(this.getClass(), "testParamInfoString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.paramInfo(this.getClass(), "testParamInfoString", "param", "6868");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoString") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
    }

    public void testParamInfoInt() throws Exception {
        Trace.paramInfo(this.getClass(), this, "testParamInfoInt", "param", 6868);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoInt") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.paramInfo(this.getClass(), this, "testParamInfoInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.paramInfo(this.getClass(), this, "testParamInfoInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.paramInfo(this.getClass(), this, "testParamInfoInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.paramInfo(this.getClass(), this, "testParamInfoInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoInt") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
    }

    public void testParamInfoInt2() throws Exception {
        Trace.paramInfo(this.getClass(), "testParamInfoInt", "param", 6868);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoInt") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.paramInfo(this.getClass(), "testParamInfoInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.paramInfo(this.getClass(), "testParamInfoInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.paramInfo(this.getClass(), "testParamInfoInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.paramInfo(this.getClass(), "testParamInfoInt", "param", 6868);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoInt") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("6868") >= 0);
    }

    public void testParamInfoBoolean() throws Exception {
        Trace.paramInfo(this.getClass(), this, "testParamInfoBoolean", "param", true);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoBoolean") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("true") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.paramInfo(this.getClass(), this, "testParamInfoBoolean", "param", true);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.paramInfo(this.getClass(), this, "testParamInfoBoolean", "param", false);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.paramInfo(this.getClass(), this, "testParamInfoBoolean", "param", true);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.paramInfo(this.getClass(), this, "testParamInfoBoolean", "param", false);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoBoolean") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("false") >= 0);
    }

    public void testParamInfoBoolean2() throws Exception {
        Trace.paramInfo(this.getClass(), "testParamInfoBoolean", "param", true);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoBoolean") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("true") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.paramInfo(this.getClass(), "testParamInfoBoolean", "param", true);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.paramInfo(this.getClass(), "testParamInfoBoolean", "param", false);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.paramInfo(this.getClass(), "testParamInfoBoolean", "param", true);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.INFO);
        Trace.paramInfo(this.getClass(), "testParamInfoBoolean", "param", false);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoBoolean") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("false") >= 0);
    }

    public void testParamInfoObject() throws Exception {
        Object object = new Long("123456789");
        Trace.paramInfo(this.getClass(), this, "testParamInfoObject", "param", object);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoObject") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("123456789") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.paramInfo(this.getClass(), this, "testParamInfoObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.paramInfo(this.getClass(), this, "testParamInfoObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.paramInfo(this.getClass(), this, "testParamInfoObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.paramInfo(this.getClass(), this, "testParamInfoObject", "param", object);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoObject") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("123456789") >= 0);
    }

    public void testParamInfoObject2() throws Exception {
        Object object = new Long("9876543210");
        Trace.paramInfo(this.getClass(), "testParamInfoObject", "param", object);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoObject") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("9876543210") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.paramInfo(this.getClass(), "testParamInfoObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.paramInfo(this.getClass(), "testParamInfoObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.paramInfo(this.getClass(), "testParamInfoObject", "param", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.paramInfo(this.getClass(), "testParamInfoObject", "param", object);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testParamInfoObject") >= 0);
        assertTrue(result.indexOf("INFO") >= 0);
        assertTrue(result.indexOf("param") >= 0);
        assertTrue(result.indexOf("9876543210") >= 0);
    }

    public void testTraceObject() throws Exception {
        Object object = new Long("123456789");
        Trace.trace(this.getClass(), this, "testTraceObject", object);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceObject") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("123456789") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.trace(this.getClass(), this, "testTraceObject", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.trace(this.getClass(), this, "testTraceObject", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.trace(this.getClass(), this, "testTraceObject", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.trace(this.getClass(), this, "testTraceObject", object);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceObject") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("123456789") >= 0);
    }

    public void testTraceObject2() throws Exception {
        Object object = new Long("123456789");
        Trace.trace(this.getClass(), "testTraceObject", object);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceObject") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("123456789") >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.trace(this.getClass(), "testTraceObject", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.trace(this.getClass(), "testTraceObject", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.trace(this.getClass(), "testTraceObject", object);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.trace(this.getClass(), "testTraceObject", object);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceObject") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("123456789") >= 0);
    }

    public void testTraceThrowable() throws Exception {
        final Throwable throwable = new IllegalArgumentException("i am important");
        Trace.trace(this.getClass(), this, "testTraceThrowable", "bad situation", throwable);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceThrowable") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("bad situation") >= 0);
        assertTrue(result.indexOf(throwable.toString()) >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.trace(this.getClass(), this, "testTraceThrowable", "bad situation", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.trace(this.getClass(), this, "testTraceThrowable", "bad situation", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.trace(this.getClass(), this, "testTraceThrowable", "bad situation", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.trace(this.getClass(), this, "testTraceThrowable", "bad situation", throwable);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceThrowable") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("bad situation") >= 0);
        assertTrue(result.indexOf(throwable.toString()) >= 0);
    }

    public void testTraceThrowable2() throws Exception {
        final Throwable throwable = new IllegalArgumentException("i am important");
        Trace.trace(this.getClass(), "testTraceThrowable", "bad situation", throwable);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceThrowable") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("bad situation") >= 0);
        assertTrue(result.indexOf(throwable.toString()) >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.trace(this.getClass(), "testTraceThrowable", "bad situation", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.trace(this.getClass(), "testTraceThrowable", "bad situation", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.trace(this.getClass(), "testTraceThrowable", "bad situation", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.trace(this.getClass(), "testTraceThrowable", "bad situation", throwable);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceThrowable") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf("bad situation") >= 0);
        assertTrue(result.indexOf(throwable.toString()) >= 0);
    }

    public void testTraceThrowable3() throws Exception {
        final Throwable throwable = new IllegalArgumentException("i am important");
        Trace.trace(this.getClass(), this, "testTraceThrowable", throwable);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceThrowable") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf(throwable.toString()) >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.trace(this.getClass(), this, "testTraceThrowable", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.trace(this.getClass(), this, "testTraceThrowable", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.trace(this.getClass(), this, "testTraceThrowable", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.trace(this.getClass(), this, "testTraceThrowable", throwable);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceThrowable") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf(throwable.toString()) >= 0);
    }

    public void testTraceThrowable4() throws Exception {
        final Throwable throwable = new IllegalArgumentException("i am important");
        Trace.trace(this.getClass(), "testTraceThrowable", throwable);
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceThrowable") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf(throwable.toString()) >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.trace(this.getClass(), "testTraceThrowable", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.trace(this.getClass(), "testTraceThrowable", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.trace(this.getClass(), "testTraceThrowable", throwable);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.trace(this.getClass(), "testTraceThrowable", throwable);
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceThrowable") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf(throwable.toString()) >= 0);
    }

    public void testTraceStack() throws Exception {
        final Throwable throwable = new IllegalArgumentException("i am important");
        String stack = "";
        for (int i = 1; i < throwable.getStackTrace().length; i++) {
            stack += System.getProperty("line.separator") + "\tat " + throwable.getStackTrace()[i];
        }
        Trace.traceStack(this.getClass(), this, "testTraceStack");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceStack") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
//        System.out.println("stack=\n" + stack);
//        System.out.println("\nresult=\n" + result);
        assertTrue(result.indexOf(stack) >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.traceStack(this.getClass(), this, "testTraceStack");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.traceStack(this.getClass(), this, "testTraceStack");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.traceStack(this.getClass(), this, "testTraceStack");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.traceStack(this.getClass(), this, "testTraceStack");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceStack") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf(stack) >= 0);
    }

    public void testTraceStack2() throws Exception {
        final Throwable throwable = new IllegalArgumentException("i am important");
        String stack = "";
        for (int i = 1; i < throwable.getStackTrace().length; i++) {
            stack += System.getProperty("line.separator") + "\tat " + throwable.getStackTrace()[i];
        }
        Trace.traceStack(this.getClass(), "testTraceStack");
        String result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceStack") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf(stack) >= 0);
        out.reset();
        result = out.toString();
        assertTrue(result.length() == 0);
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.FATAL);
        Trace.traceStack(this.getClass(), "testTraceStack");
        result = out.toString();
        assertTrue(result.length() == 0);
        Trace.traceStack(this.getClass(), "testTraceStack");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.ERROR);
        Trace.traceStack(this.getClass(), "testTraceStack");
        result = out.toString();
        assertTrue(result.length() == 0);
        rootLogger.setLevel(Level.DEBUG);
        Trace.traceStack(this.getClass(), "testTraceStack");
        result = out.toString();
        assertTrue(result.length() > 0);
        assertTrue(result.indexOf(this.getClass().getName()) >= 0);
        assertTrue(result.indexOf("testTraceStack") >= 0);
        assertTrue(result.indexOf("DEBUG") >= 0);
        assertTrue(result.indexOf(stack) >= 0);
    }

    public void testTraceOn() throws Exception {
        Trace.setTraceOn(false);
        final Throwable throwable = new IllegalArgumentException("i am important");
        Trace.trace(this.getClass(), this, "testTraceThrowable", "bad situation", throwable);
        String result = out.toString();
        assertEquals(0, result.length());
        Trace.traceStack(this.getClass(), "testTraceOn");
        result = out.toString();
        assertEquals(0, result.length());
        Trace.trace(this.getClass(), "testTraceOn", "bad situation", throwable);
        result = out.toString();
        assertEquals(0, result.length());
    }

}

