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

package org.qedeq.base.utility;

import java.io.File;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link StringUtility}.
 * 
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class StringUtilityTest extends QedeqTestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test replace(String, String, String).
     * 
     * @throws Exception
     */
    public void testReplace() throws Exception {
        assertEquals("", StringUtility.replace("", "12345", "89"));
        assertEquals("", StringUtility.replace((String) null, "12345", "89"));
        assertEquals("", StringUtility.replace((String) null, null, null));
        assertEquals("", StringUtility.replace("", null, "89"));
        assertEquals("", StringUtility.replace("", "2", null));
        assertEquals("", StringUtility.replace("", "", null));
        assertEquals("", StringUtility.replace("", "", ""));
        assertEquals("", StringUtility.replace("", null, ""));
        assertEquals("", StringUtility.replace((String) null, "", ""));
        assertEquals("13", StringUtility.replace("12", "2", "3"));
        assertEquals("1", StringUtility.replace("12", "2", null));
        assertEquals("12", StringUtility.replace("12", "", "7"));
        assertEquals("12", StringUtility.replace("12", null, "7"));
        assertEquals("145", StringUtility.replace("12345", "23", null));
        assertEquals("12345", StringUtility.replace("12345", "23", "23"));
        assertEquals("12AA12AA12", StringUtility.replace("12012012", "0", "AA"));
        assertEquals("AA12AA12AA12AA", StringUtility.replace("0120120120", "0", "AA"));
        assertEquals("AA12ABA12AA12AA", StringUtility.replace("012ABA120120", "0", "AA"));
    }
    
    /**
     * Test replace(String, String, String).
     * 
     * @throws Exception
     */
    public void testReplaceStringBuffer() throws Exception {
        StringBuffer buffer = new StringBuffer();
        doReplace(buffer, "", "", "12345", "89");
        doReplace(buffer, "", "", null, null);
        doReplace(buffer, "", "", null, "89");
        doReplace(buffer, "", "", "2", null);
        doReplace(buffer, "", "", "", null);
        doReplace(buffer, "", "", "", "");
        doReplace(buffer, "13", "12", "2", "3");
        doReplace(buffer, "1", "12", "2", null);
        doReplace(buffer, "12", "12", "", "7");
        doReplace(buffer, "12", "12", null, "7");
        doReplace(buffer, "145", "12345", "23", null);
        doReplace(buffer, "12345", "12345", "23", "23");
        doReplace(buffer, "12AA12AA12", "12012012", "0", "AA");
        doReplace(buffer, "AA12AA12AA12AA", "0120120120", "0", "AA");
        doReplace(buffer, "AA12ABA12AA12AA", "012ABA120120", "0", "AA");
        try {
            doReplace(null, "AA12ABA12AA12AA", "012ABA120120", "0", "AA");
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected;
        }
    }

    private void doReplace(StringBuffer buffer, String expected, String text, String search, String replacement) {
        buffer.setLength(0);
        buffer.append(expected);
        StringUtility.replace(buffer, search, replacement);
        assertEquals(expected, buffer.toString());
    }
    
}
