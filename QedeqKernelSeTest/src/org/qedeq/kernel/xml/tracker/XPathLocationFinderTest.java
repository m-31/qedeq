/* $Id: XPathLocationFinderTest.java,v 1.5 2007/02/25 20:04:33 m31 Exp $
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

package org.qedeq.kernel.xml.tracker;

import org.qedeq.kernel.test.QedeqTestCase;

/**
 * Test {@link org.qedeq.kernel.xml.tracker.XPathLocationFinder}.
 * 
 * @version $Revision: 1.5 $
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
        checkPosition("./data/xpathLocationFinder.xml", 
                "/QEDEQ", 
                2, 1, 304, 9);
        checkPosition("./data/xpathLocationFinder.xml", 
                "/QEDEQ[1]", 
                2, 1, 304, 9);
        checkPosition("./data/xpathLocationFinder.xml", 
                "/QEDEQ/CHAPTER/SECTION/NODE", 
                100, 7, 139, 14);
        checkPosition("./data/xpathLocationFinder.xml", 
                "/QEDEQ/CHAPTER[1]/SECTION/NODE[1]", 
                100, 7, 139, 14);
        checkPosition("./data/xpathLocationFinder.xml", 
                "/QEDEQ/CHAPTER/SECTION/NODE@label", 
                100, 13, 100, 23);
        checkPosition("./data/xpathLocationFinder.xml", 
                "/QEDEQ/CHAPTER/SECTION/NODE[2]", 
                140, 7, 212, 14);
        checkPosition("./data/xpathLocationFinder.xml",
                "/QEDEQ/CHAPTER/SECTION/NODE[2]/AXIOM/FORMULA/FORALL/FORALL/IMPL/FORALL/VAR@id",
                165, 26, 165, 32);
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
        final SimpleXPath result = XPathLocationFinder.getXPathLocation(fileName, xpath);
        System.out.println("Start position: " + result.getStartLocation());
        assertEquals(startRow, result.getStartLocation().getLine());
        assertEquals(startCol, result.getStartLocation().getColumn());
        System.out.println("End   position: " + result.getEndLocation());
        assertEquals(endRow, result.getEndLocation().getLine());
        assertEquals(endCol, result.getEndLocation().getColumn());
    }

}
