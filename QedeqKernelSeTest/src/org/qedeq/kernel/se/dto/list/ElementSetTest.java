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

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.base.list.Element;

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
