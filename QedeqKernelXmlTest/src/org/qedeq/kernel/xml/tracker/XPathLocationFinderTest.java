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

package org.qedeq.kernel.xml.tracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link org.qedeq.kernel.xml.tracker.XPathLocationFinder}.
 *
 * @author Michael Meyling
 */
public class XPathLocationFinderTest extends QedeqTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test {@link XPathLocationFinder}.
     *
     * @throws Exception Test failed.
     */
    public final void testGetXPathLocation() throws Exception {
        checkPosition("xpathLocationFinder.xml",
                "/QEDEQ",
                2, 1, 304, 9);
        checkPosition("xpathLocationFinder.xml",
                "/QEDEQ[1]",
                2, 1, 304, 9);
        checkPosition("xpathLocationFinder.xml",
                "/QEDEQ/CHAPTER/SECTION/NODE",
                100, 7, 139, 14);
        checkPosition("xpathLocationFinder.xml",
                "/QEDEQ/CHAPTER[1]/SECTION/NODE[1]",
                100, 7, 139, 14);
        checkPosition("xpathLocationFinder.xml",
                "/QEDEQ/CHAPTER/SECTION/NODE@label",
                100, 13, 100, 23);
        checkPosition("xpathLocationFinder.xml",
                "/QEDEQ/CHAPTER/SECTION/NODE[2]",
                140, 7, 212, 14);
        checkPosition("xpathLocationFinder.xml",
                "/QEDEQ/CHAPTER/SECTION/NODE[2]/AXIOM/FORMULA/FORALL/FORALL/IMPL/FORALL/VAR@id",
                165, 26, 165, 32);
    }

    public void testHelp() throws Exception {
        String result = executeError(new String[] {"-h"});
        assertTrue(0 <= result.indexOf("Synopsis"));
        assertTrue(0 <= result.indexOf("Description"));
        assertTrue(0 <= result.indexOf("Options and Parameters"));
        assertTrue(0 <= result.indexOf("Example"));
        assertTrue(0 > result.indexOf("Unknown option"));
    }

    public void testUnknownOption() throws Exception {
        String result = executeError(new String[] {"-hs"});
        assertTrue(0 <= result.indexOf("Synopsis"));
        assertTrue(0 <= result.indexOf("Description"));
        assertTrue(0 <= result.indexOf("Options and Parameters"));
        assertTrue(0 <= result.indexOf("Example"));
        assertTrue(0 <= result.indexOf("Unknown option"));
    }

    public void testNoXPath() throws Exception {
        String result = executeError(new String[] {"test.xml"});
        assertTrue(0 <= result.indexOf("Synopsis"));
        assertTrue(0 <= result.indexOf("Description"));
        assertTrue(0 <= result.indexOf("Options and Parameters"));
        assertTrue(0 <= result.indexOf("Example"));
        assertTrue(0 > result.indexOf("Unknown option"));
        assertTrue(0 <= result.indexOf("XPath must be specified"));
    }

    public void testFileNotSpecified() throws Exception {
        String result = executeError(new String[] {"-xp", "none"});
        assertTrue(0 <= result.indexOf("Synopsis"));
        assertTrue(0 <= result.indexOf("Description"));
        assertTrue(0 <= result.indexOf("Options and Parameters"));
        assertTrue(0 <= result.indexOf("Example"));
        assertTrue(0 > result.indexOf("Unknown option"));
        assertTrue(0 <= result.indexOf("XML file must be specified"));
    }

    public void testXPathNotOk() throws Exception {
        String result = executeError(new String[] {"-xp", "none",
            getFile("xpathLocationFinder.xml").toString()});
        assertTrue(0 <= result.indexOf("XPath must start with '/'"));
    }

    public void testXPathNotOk2() throws Exception {
        String result = executeError(new String[] {"-xp", "",
            getFile("xpathLocationFinder.xml").toString()});
        assertTrue(0 <= result.indexOf("XPath must not be empty"));
    }

    public void testTagNotFound() throws Exception {
        String result = execute(new String[] {"-xp", "/none",
            getFile("xpathLocationFinder.xml").toString()});
        assertTrue(result.endsWith(":1:1:1:1"));
    }

    public void testFileNotOk() throws Exception {
        executeError(new String[] {"-xp", "/none", "none"});
        // LATER 20130413 m31: this should be a readable error!
    }

    /**
     * @param   fileName    Test this XML file.
     * @param   xpath       XPath to find.
     * @param   startRow    Expected resulting starting line.
     * @param   startCol    Expected resulting starting column.
     * @param   endRow      Expected resulting ending line.
     * @param   endCol      Expected resulting ending column.
     * @throws  Exception   Test failed.
     */
    private void checkPosition(String fileName, String xpath, int startRow, int startCol,
            int endRow, int endCol) throws Exception {
        final File file = getFile(fileName);
//        final SourceArea result = XPathLocationFinder.findSourceArea(file, new SimpleXPath(xpath));
        final String[] args = new String[3];
        args[0] = "-xp";
        args[1] = xpath;
        args[2] = getFile(fileName).toString();
        final String result = execute(args);
        assertTrue(result.endsWith(":" + startRow + ":" + startCol + ":" + endRow + ":" + endCol));
    }

    private String execute(final String[] args) throws Exception {
        final ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        final PrintStream p = new PrintStream(byteOutput);
        final PrintStream old = System.out;
        System.setOut(p);
        try {
            XPathLocationFinder.main(args);
            p.flush();
            final String result = byteOutput.toString();
            return result.substring(result.indexOf("\n")).trim();
        } finally {
            System.setOut(old);
        }
    }

    private String executeError(final String[] args) throws Exception {
        final ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        final PrintStream p = new PrintStream(byteOutput);
        final PrintStream old1 = System.out;
        final PrintStream old2 = System.err;
        System.setOut(p);
        System.setErr(p);
        try {
            XPathLocationFinder.main(args);
            p.flush();
            final String result = byteOutput.toString();
            return result;
        } finally {
            System.setOut(old1);
            System.setErr(old2);
//            System.out.println(byteOutput.toString());
        }
    }
    
}
