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

package org.qedeq.kernel.se.dto.list;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.base.list.Element;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class DefaultAtomTest extends QedeqTestCase {

    private DefaultAtom empty;

    private DefaultAtom one;

    private DefaultAtom two;

    private DefaultAtom three;

    public DefaultAtomTest(){
        super();
    }

    public DefaultAtomTest(final String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        empty = new DefaultAtom("");
        one = new DefaultAtom("one");
        two = new DefaultAtom("two");
        three = new DefaultAtom("two");
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
    public void testConstructor() {
        try {
            new DefaultAtom(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }

    /**
     * Test getter.
     */
    public void testGet()  {
        assertEquals("", empty.getString());
        assertEquals("one", one.getString());
        assertEquals("two", two.getString());
        assertEquals("two", three.getString());
    }

    /**
     * Test toString.
     */
    public void testToString() {
        assertEquals("\"\"", empty.toString());
        assertEquals("\"one\"", one.toString());
        assertEquals("\"two\"", two.toString());
        assertEquals("\"two\"", three.toString());
        assertEquals("\"ho\"\"hi\"", new DefaultAtom("ho\"hi").toString());
    }

    public void testGetList() {
        try {
            empty.getList();
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            two.getList();
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }

    public void testIsAtom() {
        assertTrue(empty.isAtom());
        assertTrue(one.isAtom());
        assertTrue(two.isAtom());
    }

    public void testIsList() {
        assertFalse(empty.isList());
        assertFalse(one.isList());
        assertFalse(two.isList());
    }

    public void testGetAtom() {
        assertEquals(empty, empty.getAtom());
        assertEquals(one, one.getAtom());
        assertEquals(two, two.getAtom());
        assertEquals(two, three.getAtom());
    }

    public void testReplace() {
        assertEquals(empty, empty.replace(null, null));
        assertEquals(empty, empty.replace(one, null));
        assertEquals(empty, empty.replace(one, two));
        assertEquals(two, one.replace(one, two));
        assertEquals(one, one.replace(two, one));
        assertEquals(one, one.replace(new DefaultElementList("neu", new Element[]{ one}), two));
        Element list = new DefaultElementList("two", new Element[] { new DefaultAtom("atom"),
            new DefaultElementList("deep")});
        assertEquals(list, one.replace(one, list));
    }

    /**
     * Test hashCode.
     */
    public void testHashCode()  {
        assertFalse(0 == empty.hashCode());
        assertFalse(empty.hashCode() == one.hashCode());
        assertFalse(two.hashCode() == one.hashCode());
        assertTrue(two.hashCode() == three.hashCode());
    }

    /**
     * Test equals.
     */
    public void testEquals() {
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
