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

package org.qedeq.base.utility;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link Enumerator}.
 *
 * @author Michael Meyling
 */
public class EnumeratorTest extends QedeqTestCase {

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
     * Test constructors.
     */
    public void testConstructors() {
        Enumerator e1 = new Enumerator();
        assertEquals(0, e1.getNumber());
        Enumerator e2 = new Enumerator(0);
        assertEquals(0, e2.getNumber());
        Enumerator e3 = new Enumerator(-1);
        assertEquals(-1, e3.getNumber());
    }

    /**
     * Test {@link Enumerator#increaseNumber()}.
     */
    public void testIncrease() {
        Enumerator e1 = new Enumerator();
        e1.increaseNumber();
        assertEquals(1, e1.getNumber());
        Enumerator e2 = new Enumerator(0);
        e2.increaseNumber();
        assertEquals(1, e2.getNumber());
        assertEquals(e1, e2);
        assertNotSame(e1, e2);
        Enumerator e3 = new Enumerator(-1);
        e3.increaseNumber();
        e3.increaseNumber();
        assertEquals(e1, e3);
        assertEquals(e2, e3);
        assertEquals(1, e3.getNumber());
        Enumerator e4 = new Enumerator(100);
        for (int i = 0; i < 1000; i++) {
            e4.increaseNumber();
        }
        assertEquals(1100, e4.getNumber());
    }

    /**
     * Test {@link Enumerator#increaseNumber()}.
     */
    public void testToString() {
        Enumerator e1 = new Enumerator();
        assertEquals("0", e1.toString());
        Enumerator e2 = new Enumerator(1234);
        assertEquals("1234", e2.toString());
    }

    /**
     * Test {@link Enumerator#equals(Object)}.
     */
    public void testEquals() {
        Enumerator e1 = new Enumerator();
        assertEquals(0, e1.getNumber());
        Enumerator e2 = new Enumerator(0);
        assertEquals(0, e2.getNumber());
        assertEquals(e1, e2);
        assertNotSame(e1, e2);
        Enumerator e3 = new Enumerator(-1);
        assertEquals(-1, e3.getNumber());
        assertFalse(e1.equals(e3));
        assertFalse(e3.equals(e1));
        assertFalse(e2.equals(e3));
        assertFalse(e3.equals(e2));
        assertFalse(e3.equals(null));
        assertFalse(e1.equals(new Integer(0)));
    }

    /**
     * Test {@link Enumerator#hashCode()}.
     */
    public void testHashcode() {
        for (int i = -10; i < 100; i++) {
            // we assume a default implementation ...
            Enumerator e = new Enumerator(i);
            assertEquals(i, e.hashCode());
        }
    }

    public void testReset() {
        Enumerator e1 = new Enumerator();
        e1.increaseNumber();
        assertEquals(1, e1.getNumber());
        e1.reset();
        assertEquals(0, e1.getNumber());
    }
}
