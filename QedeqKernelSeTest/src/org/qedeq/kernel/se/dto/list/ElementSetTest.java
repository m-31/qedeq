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

import java.util.Iterator;
import java.util.List;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class ElementSetTest extends QedeqTestCase {

    /** {} */
    private ElementSet empty;

    /** {"one"} */
    private ElementSet one;

    /** {"two"} */
    private ElementSet two;

    /** {"two"} */
    private ElementSet two2;

    /** {"one", "two"} */
    private ElementSet oneTwo;

    public ElementSetTest(){
        super();
    }

    public ElementSetTest(final String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        empty = new ElementSet();
        one = new ElementSet(new Element[] {new DefaultAtom("one")});
        two = new ElementSet(new Element[] {new DefaultAtom("two")});
        two2 = new ElementSet();
        two2.add(new DefaultAtom("two"));
        oneTwo = new ElementSet();
        oneTwo.add(new DefaultAtom("one"));
        oneTwo.add(new DefaultAtom("two"));
    }

    protected void tearDown() throws Exception {
        empty = null;
        one = null;
        two = null;
        two2 = null;
        oneTwo = null;
        super.tearDown();
    }


    /**
     * Test constructor.
     */
    public void testConstructor() {
        try {
            new ElementSet((Element []) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new ElementSet((ElementSet) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new ElementSet((ElementList) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new ElementSet(new ElementList() {
                public void add(Element element) {
                }

                public Element getElement(int i) {
                    return null;
                }

                public List getElements() {
                    return null;
                }

                public String getOperator() {
                    return null;
                }

                public void insert(int position, Element element) {
                }

                public void remove(int i) {
                }

                public void replace(int position, Element element) {
                }

                public int size() {
                    return 0;
                }

                public Element copy() {
                    return null;
                }

                public Atom getAtom() {
                    return null;
                }

                public ElementList getList() {
                    return null;
                }

                public boolean isAtom() {
                    return true;
                }

                public boolean isList() {
                    return false;
                }

                public Element replace(Element search, Element replacement) {
                    return null;
                }});
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(one, new ElementSet(new ElementSet(new Element[] {new DefaultAtom("one")})));
        assertEquals(one, new ElementSet(
            new DefaultElementList("list", new Element[] {new DefaultAtom("one")})));
    }

    public void testSetOperations() {
        DefaultAtom o = new DefaultAtom("one");
        DefaultAtom t = new DefaultAtom("two");
        assertTrue(one.contains(o));
        DefaultAtom otto = new DefaultAtom("otto");
        assertFalse(one.contains(otto));
        one.remove(o);
        assertEquals(empty, one);
        one.remove(otto);
        assertEquals(empty, one);
        assertTrue(one.isEmpty());
        assertFalse(two.isEmpty());
        two.add(o);
        assertEquals(two, oneTwo);
        one.add(o);
        assertTrue(one.contains(o));
        assertFalse(one.contains(t));
        one.intersection(oneTwo);
        assertTrue(one.contains(o));
        assertFalse(one.contains(t));
        one.union(oneTwo);
        assertTrue(one.contains(o));
        assertTrue(one.contains(t));
        assertEquals(2, one.size());
    }

    public void testSetOperations2() {
        try {
            one.isSubset(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertTrue(one.isSubset(oneTwo));
        assertFalse(oneTwo.isSubset(one));
        assertTrue(oneTwo.isSubset(oneTwo));
        assertTrue(empty.isSubset(oneTwo));
        assertTrue(empty.isSubset(empty));
        try {
            one.union(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            one.intersection(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            one.minus(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            one.newDelta(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }

    public void testIsEmpty() {
        assertTrue(empty.isEmpty());
        assertFalse(oneTwo.isEmpty());
    }

    public void testIterator() {
        Iterator iterator = empty.iterator();
        assertFalse(iterator.hasNext());
        Iterator iterator2 = one.iterator();
        assertTrue(iterator2.hasNext());
        assertEquals(new DefaultAtom("one"), iterator2.next());
        assertFalse(iterator2.hasNext());
    }

    /**
     * Test toString.
     */
    public void testToString() {
        assertEquals("{}", empty.toString());
        assertEquals("{\"one\"}", one.toString());
        assertEquals("{\"two\"}", two.toString());
        assertEquals("{\"two\"}", two2.toString());
        assertEquals("{\"ho\"\"hi\"}", new ElementSet(new Element[] {new DefaultAtom("ho\"hi")})
            .toString());
        assertTrue("{\"one\", \"two\"}".equals(oneTwo.toString())
            || "{\"two\", \"one\"}".equals(oneTwo.toString()));
    }

    /**
     * Test hashCode.
     */
    public void testHashCode() {
        assertFalse(empty.hashCode() == two2.hashCode());
        assertFalse(empty.hashCode() == one.hashCode());
        assertFalse(two.hashCode() == one.hashCode());
        assertTrue(two.hashCode() == two2.hashCode());
    }

    /**
     * Test equals.
     */
    public void testEquals() {
        assertFalse(empty.equals(null));
        assertFalse(empty.equals(one));
        assertFalse(empty.equals(two));
        assertFalse(empty.equals(two2));
        assertFalse(one.equals(null));
        assertTrue(one.equals(one));
        assertFalse(one.equals(two));
        assertFalse(one.equals(two2));
        assertFalse(two.equals(null));
        assertFalse(two.equals(one));
        assertTrue(two.equals(two));
        assertTrue(two.equals(two2));
        assertFalse(two2.equals(null));
        assertFalse(two2.equals(one));
        assertTrue(two2.equals(two));
        assertTrue(two2.equals(two2));
    }

}
