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

package org.qedeq.base.utility;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link DateUtility}.
 *
 * @author  Michael Meyling
 */
public class DateUtilityTest extends QedeqTestCase {

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
     * Test getDuration(final int duration).
     *
     * @throws Exception
     */
    public void testGetDuration() throws Exception {
        assertEquals("00:00:00.000", DateUtility.getDuration(0));
        assertEquals("00:00:00.001", DateUtility.getDuration(1));
        assertEquals("23:59:59.999", DateUtility.getDuration(1000 * 60 * 60 * 24 - 1));
        assertEquals("1 day 00:00:00.000", DateUtility.getDuration(1000 * 60 * 60 * 24));
        assertEquals("1 day 23:59:59.999", DateUtility.getDuration(1000 * 60 * 60 * 24 * 2 - 1));
        assertEquals("2 days 00:00:00.000", DateUtility.getDuration(1000 * 60 * 60 * 24 * 2));
        assertEquals("365 days 00:00:00.000", DateUtility.getDuration(1000L * 60 * 60 * 24 * 365));
        assertEquals("22:59:59.999", DateUtility.getDuration(1000 * 60 * 60 * 24 - 1 - 60 * 60
            * 1000));
        assertEquals("23:00:59.999", DateUtility.getDuration(1000 * 60 * 60 * 24 - 1 - 60 * 60
            * 1000 + 60 * 1000));
    }

    public void testGetIsoTimestamp() throws Exception {
        final String ts = DateUtility.getIsoTimestamp();
        assertEquals("2009-06-07T02:10:12.000".length(), ts.length());
        assertEquals("-", ts.substring(4, 5));
        assertEquals("-", ts.substring(7, 8));
        assertEquals("T", ts.substring(10, 11));
        assertEquals(":", ts.substring(13, 14));
        assertEquals(":", ts.substring(16, 17));
        assertEquals(".", ts.substring(19, 20));
    }

    public void testGmtTimestamp() throws Exception {
        final String ts = DateUtility.getGmtTimestamp();
        assertEquals("2009-06-07 02:10:12.000".length(), ts.length());
        assertEquals("-", ts.substring(4, 5));
        assertEquals("-", ts.substring(7, 8));
        assertEquals(" ", ts.substring(10, 11));
        assertEquals(":", ts.substring(13, 14));
        assertEquals(":", ts.substring(16, 17));
        assertEquals(".", ts.substring(19, 20));
    }

    public void testTimestamp() throws Exception {
        final String ts = DateUtility.getTimestamp();
        assertEquals("2009-06-07 02:10:12.000".length(), ts.length());
        assertEquals("-", ts.substring(4, 5));
        assertEquals("-", ts.substring(7, 8));
        assertEquals(" ", ts.substring(10, 11));
        assertEquals(":", ts.substring(13, 14));
        assertEquals(":", ts.substring(16, 17));
        assertEquals(".", ts.substring(19, 20));
    }

}
