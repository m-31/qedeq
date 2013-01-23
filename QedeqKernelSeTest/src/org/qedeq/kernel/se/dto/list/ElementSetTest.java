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

    private ElementSet empty;

    private ElementSet one;

    private ElementSet two;

    private ElementSet three;

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
        three = new ElementSet();
        three.add(new DefaultAtom("two"));
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
        assertEquals("{\"two\"}", three.toString());
        assertEquals("{\"ho\"\"hi\"}", new ElementSet(new Element[] {new DefaultAtom("ho\"hi")})
            .toString());
    }

    /**
     * Test hashCode.
     */
    public void testHashCode() {
        assertFalse(empty.hashCode() == three.hashCode());
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
