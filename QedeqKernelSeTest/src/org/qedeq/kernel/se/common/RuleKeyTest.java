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

package org.qedeq.kernel.se.common;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class RuleKeyTest extends QedeqTestCase {

    private RuleKey empty;

    private RuleKey one;

    private RuleKey two;

    private RuleKey three;

    public RuleKeyTest(){
        super();
    }

    public RuleKeyTest(final String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        empty = new RuleKey(null, null);
        one = new RuleKey("one", "1.00.00");
        two = new RuleKey("two", "0.00.99");
        three = new RuleKey("two", "0.00.99");
    }

    protected void tearDown() throws Exception {
        empty = null;
        one = null;
        two = null;
        three = null;
        super.tearDown();
    }

    /**
     * Test getter.
     */
    public void testGet() throws Exception {
        assertEquals(null, empty.getName());
        assertEquals(null, empty.getVersion());
        assertEquals("one", one.getName());
        assertEquals("1.00.00", one.getVersion());
        assertEquals("two", two.getName());
        assertEquals("0.00.99", two.getVersion());
        assertEquals("two", three.getName());
        assertEquals("0.00.99", three.getVersion());
    }

    /**
     * Test toString.
     */
    public void testToString() throws Exception {
        assertEquals("null [null]", empty.toString());
        assertEquals("one [1.00.00]", one.toString());
        assertEquals("two [0.00.99]", two.toString());
        assertEquals("two [0.00.99]", three.toString());
    }

    /**
     * Test hashCode.
     */
    public void testHashCode() throws Exception {
        assertEquals(0 , empty.hashCode());
        assertFalse(empty.hashCode() == one.hashCode());
        assertFalse(two.hashCode() == one.hashCode());
        assertTrue(two.hashCode() == three.hashCode());
    }

    /**
     * Test equals.
     */
    public void testEquals() throws Exception {
        assertFalse(empty.equals(null));
        assertFalse(empty.equals(one));
        assertFalse(empty.equals(two));
        assertFalse(empty.equals(three));
        assertFalse(one.equals(null));
        assertTrue(one.equals(one));
        assertFalse(one.equals(two));
        assertFalse(one.equals(three));
        assertFalse(two.equals(null));
        assertFalse(two.equals(one));
        assertTrue(two.equals(two));
        assertTrue(two.equals(three));
        assertFalse(three.equals(null));
        assertFalse(three.equals(one));
        assertTrue(three.equals(two));
        assertTrue(three.equals(three));
    }

}
