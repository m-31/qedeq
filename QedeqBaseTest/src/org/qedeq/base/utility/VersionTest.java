/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
 * Test {@link Enumerator}.
 *
 * @author Michael Meyling
 */
public class VersionTest extends QedeqTestCase {

    private Version version1;

    private Version version2;

    private Version version3;

    private Version version4;

    
    protected void setUp() throws Exception {
        super.setUp();
        version1 = new Version("0.00.00");
        version2 = new Version("0.01.00");
        version3 = new Version("0.02.01");
        version4 = new Version("1.00.00");
    }

    /**
     * Test constructors.
     *
     * @throws  Exception   Test failure.
     */
    public void testConstructors() throws Exception {
        final Version v1 = new Version("1.2.3");
        assertEquals(1, v1.getMajor());
        assertEquals(2, v1.getMinor());
        assertEquals(3, v1.getPatch());
        try {
            new Version("");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version(null);
            fail("exception expected");
        } catch (NullPointerException e) {
            // ok
        }
        try {
            new Version("1");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version("1.");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version("1.2");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version("1.2.");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version("1.2.3.");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version("1.2.3.4");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version("+1.2.3");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version("-1.2.3");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new Version("null");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        new Version("1234567890.1234567890.1234567890");    // ok
        try {
            new Version("12345678901.12345678901.12345678901");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        assertEquals(version1, new Version(" 0.00.00 "));
        assertEquals(version2, new Version(" 0.01.00 "));
        assertEquals(version3, new Version(" \t0.02. 01 "));
        assertEquals(version4, new Version("  1.00.00  "));
    }

    /**
     * Test getters.
     *
     * @throws  Exception   Test failure.
     */
    public void testGetters() throws Exception {
        assertEquals(0, version1.getMajor());
        assertEquals(0, version1.getMinor());
        assertEquals(0, version1.getPatch());
        assertEquals(0, version2.getMajor());
        assertEquals(1, version2.getMinor());
        assertEquals(0, version2.getPatch());
        assertEquals(0, version3.getMajor());
        assertEquals(2, version3.getMinor());
        assertEquals(1, version3.getPatch());
        assertEquals(1, version4.getMajor());
        assertEquals(0, version4.getMinor());
        assertEquals(0, version4.getPatch());
        assertEquals(123456, new Version("123456.0.0").getMajor());
        assertEquals(62743, new Version("123456.62743.0").getMinor());
        assertEquals(732864, new Version("123456.62743.732864").getPatch());
    }

    /**
     * Test compareTo.
     *
     * @throws  Exception   Test failure.
     */
    public void testCompareTo() throws Exception {
        assertEquals(0, version1.compareTo(version1));
        assertEquals(0, version2.compareTo(version2));
        assertEquals(0, version3.compareTo(version3));
        assertEquals(0, version4.compareTo(version4));
        assertEquals(-1, version1.compareTo(version2));
        assertEquals(-1, version2.compareTo(version3));
        assertEquals(-1, version3.compareTo(version4));
        assertEquals(-1, version2.compareTo(version4));
        assertEquals(-1, version1.compareTo(version3));
        assertEquals(-1, version1.compareTo(version4));
        assertEquals(1, version2.compareTo(version1));
        assertEquals(1, version3.compareTo(version2));
        assertEquals(1, version4.compareTo(version3));
        assertEquals(1, version4.compareTo(version2));
        assertEquals(1, version3.compareTo(version1));
        assertEquals(1, version4.compareTo(version1));
    }

    /**
     * Test isLess.
     *
     * @throws  Exception   Test failure.
     */
    public void testIsLess() throws Exception {
        assertTrue(version1.isLess(version2));
        assertTrue(version2.isLess(version3));
        assertTrue(version3.isLess(version4));
        assertTrue(version1.isLess(version3));
        assertTrue(version1.isLess(version4));
        assertTrue(version2.isLess(version3));
        assertFalse(version2.isLess(version1));
        assertFalse(version3.isLess(version2));
        assertFalse(version4.isLess(version3));
        assertFalse(version3.isLess(version1));
        assertFalse(version4.isLess(version1));
        assertFalse(version3.isLess(version2));
        assertFalse(version1.isLess(version1));
        assertFalse(version2.isLess(version2));
        assertFalse(version3.isLess(version3));
        assertFalse(version4.isLess(version4));
    }

    /**
     * Test isLess.
     *
     * @throws  Exception   Test failure.
     */
    public void testIsBigger() throws Exception {
        assertFalse(version1.isBigger(version2));
        assertFalse(version2.isBigger(version3));
        assertFalse(version3.isBigger(version4));
        assertFalse(version1.isBigger(version3));
        assertFalse(version1.isBigger(version4));
        assertFalse(version2.isBigger(version3));
        assertTrue(version2.isBigger(version1));
        assertTrue(version3.isBigger(version2));
        assertTrue(version4.isBigger(version3));
        assertTrue(version3.isBigger(version1));
        assertTrue(version4.isBigger(version1));
        assertTrue(version3.isBigger(version2));
        assertFalse(version1.isBigger(version1));
        assertFalse(version2.isBigger(version2));
        assertFalse(version3.isBigger(version3));
        assertFalse(version4.isBigger(version4));
    }

    /**
     * Test toString.
     *
     * @throws  Exception   Test failure.
     */
    public void testToString() throws Exception {
        assertEquals("0.00.00", version1.toString());
        assertEquals("0.01.00", version2.toString());
        assertEquals("0.02.01", version3.toString());
        assertEquals("1.00.00", version4.toString());
        assertEquals("625434.99.71", new Version("625434.99.71").toString());
        assertEquals("0.00.00", new Version("0.0.0").toString());
        assertEquals("0.09.09", new Version("0.9.9").toString());
        assertEquals("0.900.900", new Version("0.900.900").toString());
    }

    /**
     * Test equals.
     *
     * @throws  Exception   Test failure.
     */
    public void testEquals() throws Exception {
        assertEquals(version1, version1);
        assertEquals(version1, new Version("0.00.00"));
        assertEquals(version2, version2);
        assertEquals(version2, new Version("0.01.00"));
        assertEquals(version3, version3);
        assertEquals(version3, new Version("0.02.01"));
        assertEquals(version4, version4);
        assertEquals(version4, new Version("1.00.00"));
        assertFalse(version1.equals(null));
        assertFalse(version1.equals(version2));
        assertFalse(version2.equals(version3));
        assertFalse(version3.equals(version4));
        assertFalse(version4.equals(version1));
        assertFalse(version3.equals(version2));
        assertFalse(version1.equals(version3));
        assertFalse(version3.equals(version1));
    }

    /**
     * Test special equals method.
     *
     * @throws  Exception   Test failure.
     */
    public void testEquals2() throws Exception {
        assertTrue(version1.equals("0.00.00"));
        assertTrue(version1.equals("00000.00000.0"));
        assertTrue(version1.equals("0.0.0"));
        assertTrue(version2.equals("0.01.00"));
        assertFalse(version2.equals("0.00.00"));
        assertTrue(version3.equals("0.02.01"));
        assertFalse(version3.equals("0.02.00"));
        assertFalse(version3.equals("0.02.0"));
        assertTrue(version4.equals("1.00.00"));
    }

    /**
     * Test hashCode.
     *
     * @throws  Exception   Test failure.
     */
    public void testHashCode() throws Exception {
        assertFalse(version1.hashCode() == version2.hashCode());
        assertFalse(version2.hashCode() == version3.hashCode());
        assertFalse(version3.hashCode() == version4.hashCode());
        assertTrue((new Version("0.0.0")).hashCode() == version1.hashCode());
    }
    
    /**
     * Test less.
     *
     * @throws  Exception   Test failure.
     */
    public void testLess() throws Exception {
        assertTrue(Version.less("0.00.00", "0.00.01"));
        assertTrue(Version.less("1.00.00", "1.00.01"));
        assertTrue(Version.less("2.01.00", "2.01.01"));
        assertFalse(Version.less("2.01.00", "2.01.00"));
        assertFalse(Version.less("3.01.00", "2.01.00"));
        assertFalse(Version.less("0.00.00", "0.00.00"));
    }

    /**
     * Test bigger.
     *
     * @throws  Exception   Test failure.
     */
    public void testBigger() throws Exception {
        assertTrue(Version.bigger("0.00.10", "0.00.01"));
        assertTrue(Version.bigger("1.09.10", "0.00.01"));
        assertTrue(Version.bigger("0.01.10", "0.01.1"));
        assertFalse(Version.bigger("0.01.10", "0.01.11"));
        assertFalse(Version.bigger("0.01.10", "0.01.10"));
    }

}