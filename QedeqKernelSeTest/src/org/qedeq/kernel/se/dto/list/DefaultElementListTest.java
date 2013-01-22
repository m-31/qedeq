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

package org.qedeq.kernel.se.dto.list;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class DefaultElementListTest extends QedeqTestCase {

    private DefaultElementList empty;

    private DefaultElementList one;

    private DefaultElementList two;

    private DefaultElementList three;

    private DefaultElementList withNone;

    private DefaultElementList withOne;

    private DefaultElementList withTwo;

    private DefaultElementList withTwo2;

    public DefaultElementListTest(){
        super();
    }

    public DefaultElementListTest(final String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        empty = new DefaultElementList("");
        one = new DefaultElementList("one");
        two = new DefaultElementList("two");
        three = new DefaultElementList("two");
        withNone = new DefaultElementList("");
        withOne = new DefaultElementList("one");
        withTwo = new DefaultElementList("two");
        withTwo2 = new DefaultElementList("two");
    }

    protected void tearDown() throws Exception {
        empty = null;
        one = null;
        two = null;
        three = null;
        super.tearDown();
    }


    /**
     * Test constructor.
     */
    public void testConstructor() throws Exception {
        try {
            new DefaultElementList(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }

    /**
     * Test getter.
     */
    public void testGet() throws Exception {
        assertEquals("", empty.getOperator());
        assertEquals("one", one.getOperator());
        assertEquals("two", two.getOperator());
        assertEquals("two", three.getOperator());
    }

    /**
     * Test toString.
     */
    public void testToString() throws Exception {
        assertEquals("", empty.toString());
        assertEquals("one", one.toString());
        assertEquals("two", two.toString());
        assertEquals("two", three.toString());
    }

    /**
     * Test hashCode.
     */
    public void testHashCode() throws Exception {
        assertTrue(0 == empty.hashCode());
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
