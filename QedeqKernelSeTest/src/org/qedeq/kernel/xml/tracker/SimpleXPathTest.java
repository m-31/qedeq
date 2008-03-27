/* $Id: SimpleXPathTest.java,v 1.8 2008/03/27 05:12:46 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.tracker;

import org.qedeq.kernel.test.QedeqTestCase;

/**
 * Test {@link org.qedeq.kernel.xml.tracker.SimpleXPath}.
 *
 * @version $Revision: 1.8 $
 * @author  Michael Meyling
 */
public class SimpleXPathTest extends QedeqTestCase {

    /** Test object. */
    private SimpleXPath object1;

    /** Test object. */
    private SimpleXPath object2;

    /** Test object. */
    private SimpleXPath object3;

    /** Test object. */
    private SimpleXPath object4;

    /** Test object. */
    private SimpleXPath object5;

    /** Test object. */
    private SimpleXPath object6;

    /** Test object. */
    private SimpleXPath object7;

    protected void setUp() throws Exception {
        super.setUp();
        object1 = new SimpleXPath("/We/don't/need");
        object2 = new SimpleXPath("/We/dont/need");
        object3 = new SimpleXPath("/We/don't/need");
        object4 = new SimpleXPath("/We/don't/need/no");
        object5 = new SimpleXPath("/We/don't/need/no@education");
        object6 = new SimpleXPath("/All/we[2]/need/is");
        object7 = new SimpleXPath("/All/we/need/is[11]@love");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        object1 = null;
        object2 = null;
        object3 = null;
        object4 = null;
        object5 = null;
        object6 = null;
        object7 = null;
    }

    /**
     * Test constructor.
     */
    public void testSimpleXPath() {
        try {
            new SimpleXPath("");
            fail("empty XPath is forbidden");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            new SimpleXPath("no_absolute_path");
            fail("relative path is forbidden");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            new SimpleXPath("no_absolute_path");
            fail("relative path is forbidden");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            new SimpleXPath("/hello//again");
            fail("empty element is forbidden");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            new SimpleXPath("/hello/again/@");
            fail("empty attribute is forbidden");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            new SimpleXPath("/hello/again/");
            fail("XPath should not end with /");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            new SimpleXPath("/All/we[0]/have");
            fail("element number must be greater than 0");
        } catch (RuntimeException e) {
            // expected
        }
    }

    /**
     * Test {@link SimpleXPath#size()}.
     */
    public void testSize() {
        assertEquals(3, object1.size());
        assertEquals(3, object2.size());
        assertEquals(3, object3.size());
        assertEquals(4, object4.size());
        assertEquals(4, object5.size());
    }

    /**
     * Test {@link SimpleXPath#getElementName(int)}.
     */
    public void testGetElementName() {
        assertEquals("We", object1.getElementName(0));
        assertEquals("don't", object1.getElementName(1));
        assertEquals("need", object1.getElementName(2));
        try {
            object1.getElementName(3);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object1.getElementName(-1);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object1.getElementName(11);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        assertEquals("We", object4.getElementName(0));
        assertEquals("don't", object4.getElementName(1));
        assertEquals("need", object4.getElementName(2));
        assertEquals("no", object4.getElementName(3));
        try {
            object4.getElementName(4);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object4.getElementName(-1);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object4.getElementName(11);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        assertEquals("All", object7.getElementName(0));
        assertEquals("we", object7.getElementName(1));
        assertEquals("need", object7.getElementName(2));
        assertEquals("is", object7.getElementName(3));
        try {
            object7.getElementName(4);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object7.getElementName(-1);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object7.getElementName(11);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
    }

    /**
     * Test {@link SimpleXPath#getElementOccurrence(int)}.
     */
    public void testGetElementOccurrence() {
        assertEquals(1, object1.getElementOccurrence(0));
        assertEquals(1, object1.getElementOccurrence(1));
        assertEquals(1, object1.getElementOccurrence(2));
        try {
            object1.getElementOccurrence(3);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object1.getElementOccurrence(11);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object1.getElementOccurrence(-1);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        assertEquals(1, object6.getElementOccurrence(0));
        assertEquals(2, object6.getElementOccurrence(1));
        assertEquals(1, object6.getElementOccurrence(2));
        assertEquals(1, object6.getElementOccurrence(3));
        try {
            object6.getElementOccurrence(4);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object6.getElementOccurrence(11);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object6.getElementOccurrence(-1);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        assertEquals(1, object7.getElementOccurrence(0));
        assertEquals(1, object7.getElementOccurrence(1));
        assertEquals(1, object7.getElementOccurrence(2));
        assertEquals(11, object7.getElementOccurrence(3));
        try {
            object7.getElementOccurrence(4);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object7.getElementOccurrence(11);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            object7.getElementOccurrence(-1);
            fail("getter should throw exception for access to non existent element");
        } catch (RuntimeException e) {
            // expected
        }
    }

    /**
     * Test {@link SimpleXPath#getAttribute()}.
     */
    public void testGetAttribute() {
        assertNull(object1.getAttribute());
        assertNull(object2.getAttribute());
        assertNull(object3.getAttribute());
        assertNull(object4.getAttribute());
        assertEquals("education", object5.getAttribute());
        assertNull(object6.getAttribute());
        assertEquals("love", object7.getAttribute());
    }

    /**
     * Test {@link SimpleXPath#toString()}.
     */
    public void testToString() {
        assertNotNull(object1.toString());
        assertNotNull(object2.toString());
        assertNotNull(object3.toString());
        assertNotNull(object4.toString());
        assertNotNull(object5.toString());
        assertNotNull(object6.toString());
        assertNotNull(object7.toString());
    }

    /**
     * Test {@link SimpleXPath#hashCode()}.
     */
    public void testHashCode() {
        assertTrue(object1.hashCode() != object2.hashCode());
        assertTrue(object1.hashCode() == object3.hashCode());
        assertTrue(object1.hashCode() != object4.hashCode());
        assertTrue(object1.hashCode() != object5.hashCode());
        assertTrue(object1.hashCode() != object6.hashCode());
        assertTrue(object1.hashCode() != object7.hashCode());
        assertTrue(object2.hashCode() != object3.hashCode());
        assertTrue(object2.hashCode() != object4.hashCode());
        assertTrue(object2.hashCode() != object5.hashCode());
        assertTrue(object2.hashCode() != object6.hashCode());
        assertTrue(object2.hashCode() != object7.hashCode());
        assertTrue(object3.hashCode() != object4.hashCode());
        assertTrue(object3.hashCode() != object5.hashCode());
        assertTrue(object3.hashCode() != object6.hashCode());
        assertTrue(object3.hashCode() != object7.hashCode());
        assertTrue(object4.hashCode() != object5.hashCode());
        assertTrue(object4.hashCode() != object6.hashCode());
        assertTrue(object4.hashCode() != object7.hashCode());
        assertTrue(object5.hashCode() != object6.hashCode());
        assertTrue(object5.hashCode() != object7.hashCode());
        assertTrue(object6.hashCode() != object7.hashCode());
    }

    /**
     * Test {@link SimpleXPath#equals(Object)}.
     */
    public void testEquals() {
        assertTrue(object1.equals(object1));
        assertFalse(object1.equals(object2));
        assertTrue(object1.equals(object3));
        assertTrue(object1.hashCode() == object3.hashCode());
        assertFalse(object1.equals(object4));
        assertFalse(object1.equals(object5));
        assertFalse(object1.equals(object6));
        assertFalse(object1.equals(object7));
        assertFalse(object2.equals(object1));
        assertTrue(object2.equals(object2));
        assertFalse(object2.equals(object3));
        assertFalse(object2.equals(object4));
        assertFalse(object2.equals(object5));
        assertFalse(object2.equals(object6));
        assertFalse(object2.equals(object7));
        assertTrue(object3.equals(object1));
        assertFalse(object3.equals(object2));
        assertTrue(object3.equals(object3));
        assertFalse(object3.equals(object4));
        assertFalse(object3.equals(object5));
        assertFalse(object3.equals(object6));
        assertFalse(object3.equals(object7));
        assertFalse(object4.equals(object1));
        assertFalse(object4.equals(object2));
        assertFalse(object4.equals(object3));
        assertTrue(object4.equals(object4));
        assertFalse(object4.equals(object5));
        assertFalse(object4.equals(object6));
        assertFalse(object4.equals(object7));
        assertFalse(object5.equals(object1));
        assertFalse(object5.equals(object2));
        assertFalse(object5.equals(object3));
        assertFalse(object5.equals(object4));
        assertTrue(object5.equals(object5));
        assertFalse(object5.equals(object6));
        assertFalse(object5.equals(object7));
        assertFalse(object6.equals(object1));
        assertFalse(object6.equals(object2));
        assertFalse(object6.equals(object3));
        assertFalse(object6.equals(object4));
        assertFalse(object6.equals(object5));
        assertTrue(object6.equals(object6));
        assertFalse(object6.equals(object7));
        assertFalse(object6.equals(object1));
        assertFalse(object6.equals(object2));
        assertFalse(object6.equals(object3));
        assertFalse(object6.equals(object4));
        assertFalse(object6.equals(object5));
        assertTrue(object6.equals(object6));
        assertFalse(object6.equals(object7));
        assertTrue(object7.equals(object7));
        assertFalse((new SimpleXPath("/cat/on/the/hot@tin")).equals(new SimpleXPath("/cat/on/the/hot")));
        assertFalse((new SimpleXPath("/cat[2]/on/the/hot@tin")).equals(new SimpleXPath("/cat/on/the/hot@tin")));
        final SimpleXPath object10 = new SimpleXPath("/cat[1]/on/the/hot@tin");
        final SimpleXPath object11 = new SimpleXPath("/cat/on/the/hot@tin");
        assertTrue(object10.equals(object11));
        assertTrue(object10.hashCode() == object11.hashCode());
    }

}
