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

import java.util.Vector;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.base.list.Element;

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
        withNone = new DefaultElementList("", new Element[] {});
        withOne = new DefaultElementList("one", new Element[] { new DefaultAtom("atom")});
        withTwo = new DefaultElementList("two", new Element[] { new DefaultAtom("atom"),
            new DefaultElementList("deep")});
        withTwo2 = new DefaultElementList("two");
        withTwo2.add(new DefaultAtom("atom"));
        withTwo2.add(new DefaultElementList("deep"));
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
        try {
            new DefaultElementList(null, (Element[]) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new DefaultElementList("operator", (Element[]) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertFalse(new DefaultElementList("one").equals(new DefaultAtom("on")));
    }

    public void testGetElement() {
        try {
            empty.getElement(0);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            one.getElement(0);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            one.getElement(-1);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(new DefaultAtom("atom"), withTwo.getElement(0));
        assertEquals(new DefaultElementList("deep"), withTwo.getElement(1));
        try {
            withTwo.getElement(2);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        withTwo.remove(0);
        assertEquals(new DefaultElementList("deep"), withTwo.getElement(0));
        try {
            withTwo.getElement(1);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }

    public void testSize() {
        assertEquals(0, empty.size());
        assertEquals(0, one.size());
        assertEquals(1, withOne.size());
        assertEquals(2, withTwo.size());
    }

    public void testGetElements() {
        assertEquals(new Vector() {}, empty.getElements());
        Vector result = new Vector();
        result.add(new DefaultAtom("atom"));
        result.add(new DefaultElementList("deep"));
        assertEquals(result, withTwo.getElements());
    }

    public void testCopy() {
        Element copy = withTwo.copy();
        assertEquals(withTwo, copy);
        assertFalse(withTwo == copy);
        copy = empty.copy();
        assertEquals(empty, copy);
        assertFalse(empty == copy);
    }

    public void testAddAndSizeAndInsertAndRemove() {
        try {
            empty.add(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            empty.remove(0);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            withTwo.remove(-1);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            withTwo.insert(0, null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(0, one.size());
        assertEquals(2, withTwo.size());
        withTwo.add(new DefaultAtom("atom"));
        assertEquals(3, withTwo.size());
        assertFalse(withTwo.equals(withTwo2));
        withTwo.remove(0);
        assertFalse(withTwo.equals(withTwo2));
        withTwo.remove(0);
        assertFalse(withTwo.equals(withOne));
        try {
            withTwo.remove(1);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertFalse(withTwo.equals(withOne));
        withTwo.remove(0);
        assertEquals(new DefaultElementList("two"), withTwo);
        withTwo.insert(0, new DefaultAtom("atom"));
        withTwo.insert(1, new DefaultElementList("deep"));
        assertEquals(withTwo2, withTwo);
        withTwo.insert(2, new DefaultAtom("multi"));
        withTwo.remove(2);
        assertEquals(withTwo2, withTwo);
    }

    public void testGetList() {
        assertEquals(empty, empty.getList());
        assertEquals(one, one.getList());
        assertEquals(two, two.getList());
        assertEquals(two, three.getList());
        assertEquals(withOne, withOne.getList());
    }

    public void testGetAtom() {
        try {
            empty.getAtom();
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            two.getAtom();
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }

    public void testIsList() {
        assertTrue(empty.isList());
        assertTrue(one.isList());
        assertTrue(two.isList());
    }

    public void testIsAtom() {
        assertFalse(empty.isAtom());
        assertFalse(one.isAtom());
        assertFalse(two.isAtom());
    }

    public void testReplace1() {
        assertEquals(empty, empty.replace(null, null));
        assertEquals(empty, empty.replace(one, null));
        assertEquals(empty, empty.replace(one, two));
        assertEquals(two, one.replace(one, two));
        assertEquals(one, one.replace(two, one));
        assertEquals(one, one.replace(new DefaultElementList("neu", new Element[]{ one}), two));
        Element list = new DefaultElementList("two", new Element[] { new DefaultAtom("atom"),
            new DefaultElementList("deep")});
        assertEquals(list, one.replace(one, list));
        assertEquals("one ( \"ATOM\")", withOne.replace(new DefaultAtom("atom"),
             new DefaultAtom("ATOM")).toString());
    }

    public void testReplace2() {
        try {
            empty.replace(0, null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            empty.replace(0, new DefaultAtom("one"));
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            withOne.replace(-1, new DefaultAtom("one"));
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            empty.replace(0, new DefaultAtom("one"));
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            empty.replace(1, new DefaultAtom("one"));
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            withOne.replace(1, new DefaultAtom("one"));
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        withOne.replace(0, new DefaultAtom("expected"));
        assertEquals(new DefaultAtom("expected"), withOne.getElement(0));
    }


    public void testInsert() {
        try {
            empty.insert(1, new DefaultAtom("one"));
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            empty.insert(-1, new DefaultAtom("one"));
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        empty.insert(0, new DefaultAtom("one"));
        assertEquals(new DefaultElementList("", new Element[] {new DefaultAtom("one") }), empty);
    }

    public void testGetOperator() throws Exception {
        assertEquals("", empty.getOperator());
        assertEquals("one", one.getOperator());
        assertEquals("two", two.getOperator());
        assertEquals("two", three.getOperator());
        assertEquals("", withNone.getOperator());
        assertEquals("one", withOne.getOperator());
        assertEquals("two", withTwo.getOperator());
        assertEquals("two", withTwo2.getOperator());
    }

    /**
     * Test toString.
     */
    public void testToString() throws Exception {
        assertEquals("", empty.toString());
        assertEquals("one", one.toString());
        assertEquals("two", two.toString());
        assertEquals("two", three.toString());
        assertEquals("", withNone.toString());
        assertEquals("one ( \"atom\")", withOne.toString());
        assertEquals("two ( \"atom\", deep)", withTwo.toString());
        assertEquals("two ( \"atom\", deep)", withTwo2.toString());
    }

    /**
     * Test hashCode.
     */
    public void testHashCode() throws Exception {
        assertTrue(0 == empty.hashCode());
        assertFalse(empty.hashCode() == one.hashCode());
        assertFalse(two.hashCode() == one.hashCode());
        assertTrue(two.hashCode() == three.hashCode());
        assertFalse(withNone.hashCode() == one.hashCode());
        assertTrue(withNone.hashCode() == empty.hashCode());
        assertFalse(withOne.hashCode() == withTwo.hashCode());
        assertFalse(withOne.hashCode() == withTwo2.hashCode());
        assertTrue(withTwo.hashCode() == withTwo2.hashCode());
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
        assertTrue(withTwo.equals(withTwo2));
        assertTrue(empty.equals(withNone));
        assertFalse(empty.equals(""));
        assertFalse(withTwo.equals(withOne));
    }

}
