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

package org.qedeq.base.io;

import java.util.Iterator;

import org.qedeq.base.io.Version;
import org.qedeq.base.io.VersionSet;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link VersionSet}.
 *
 * @author Michael Meyling
 */
public class VersionSetTest extends QedeqTestCase {

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
        final VersionSet v1 = new VersionSet(version1.toString());
        assertTrue(v1.contains(version1));
        final VersionSet v2 = new VersionSet();
        assertTrue(v2.isEmpty());
        try {
            new VersionSet("");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet(null);
            fail("exception expected");
        } catch (NullPointerException e) {
            // ok
        }
        try {
            new VersionSet("1");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet("1.");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet("1.2");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet("1.2.");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet("1.2.3.");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet("1.2.3.4");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet("+1.2.3");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet("-1.2.3");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new VersionSet("null");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
        new VersionSet("1234567890.1234567890.1234567890");    // ok
        try {
            new VersionSet("12345678901.12345678901.12345678901");
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testAddAndContainsClearIsEmpty() throws Exception {
        final VersionSet v1 = new VersionSet();
        assertFalse(v1.contains(version1));
        assertFalse(v1.contains(version1.toString()));
        assertTrue(v1.isEmpty());
        assertFalse(v1.contains(version2));
        assertFalse(v1.contains(version3));
        assertFalse(v1.contains(version4));
        assertFalse(v1.contains(version2.toString()));
        assertFalse(v1.contains(version3.toString()));
        assertFalse(v1.contains(version4.toString()));
        v1.add(version1);
        assertFalse(v1.isEmpty());
        v1.add(version2.toString());
        v1.add(version3);
        v1.add(version4.toString());
        assertTrue(v1.contains(version1));
        assertTrue(v1.contains(version2));
        assertTrue(v1.contains(version3));
        assertTrue(v1.contains(version4));
        assertTrue(v1.contains(version1.toString()));
        assertTrue(v1.contains(version2.toString()));
        assertTrue(v1.contains(version3.toString()));
        assertTrue(v1.contains(version4.toString()));
        v1.clear();
        assertFalse(v1.contains(version1));
        assertFalse(v1.contains(version1.toString()));
        assertTrue(v1.isEmpty());
        assertFalse(v1.contains(version2));
        assertFalse(v1.contains(version3));
        assertFalse(v1.contains(version4));
        assertFalse(v1.contains(version2.toString()));
        assertFalse(v1.contains(version3.toString()));
        assertFalse(v1.contains(version4.toString()));
    }

    public void testAddAndEquals() throws Exception {
        final VersionSet v1 = new VersionSet();
        v1.add(version1);
        v1.add(version2.toString());
        final VersionSet v2 = new VersionSet();
        v2.add(version3);
        v2.add(version4.toString());
        final VersionSet v3 = new VersionSet();
        v3.add(version2.toString());
        v3.add(version4.toString());
        v3.add(version1);
        v3.add(version3);
        final VersionSet v4 = new VersionSet();
        v4.add(version1);
        v4.add(version2);
        assertTrue(v1.equals(v1));
        assertTrue(v2.equals(v2));
        assertTrue(v3.equals(v3));
        assertTrue(v4.equals(v4));
        assertFalse(v1.equals(v2));
        assertFalse(v1.equals(v3));
        assertTrue(v1.equals(v4));
        assertFalse(v2.equals(v1));
        assertFalse(v3.equals(v1));
        assertTrue(v4.equals(v1));
        assertFalse(v2.equals(v3));
        assertFalse(v3.equals(v2));
        assertFalse(v2.equals(v4));
        assertFalse(v4.equals(v2));
        v1.addAll(v2);
        assertEquals(v1, v3);
    }

    public void testIterator() throws Exception {
        final VersionSet v1 = new VersionSet();
        v1.add("0.02.00");
        v1.add("0.01.00");
        v1.add("0.00.30");
        Iterator i = v1.iterator();
        assertTrue(i.hasNext());
        assertEquals(new Version("0.00.30"), i.next());
        assertTrue(i.hasNext());
        assertEquals(new Version("0.01.00"), i.next());
        assertTrue(i.hasNext());
        assertEquals(new Version("0.02.00"), i.next());
        assertFalse(i.hasNext());
    }
}