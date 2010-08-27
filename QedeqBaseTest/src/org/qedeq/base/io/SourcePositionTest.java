/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link SourcePosition}.
 *
 * @author    Michael Meyling
 */
public class SourcePositionTest extends QedeqTestCase {

    /** Test object 1. */
    private SourcePosition object1;

    /** Test object 2. */
    private SourcePosition object2;

    /** Test object 3. */
    private SourcePosition object3;

    /** Test object 4. */
    private SourcePosition object4;

    /** Test object 5. */
    private SourcePosition object5;

    protected void setUp() throws Exception {
        super.setUp();
        object1 = new SourcePosition(12, 17);
        object2 = new SourcePosition(12, 17);
        object3 = new SourcePosition(13, 19);
        object4 = new SourcePosition(12, 19);
        object5 = new SourcePosition(13, 17);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSourcePosition() {
        new SourcePosition(-1, -1);
    }

    public void testGetLine() {
        assertEquals(12, object1.getRow());
        assertEquals(12, object2.getRow());
        assertEquals(13, object3.getRow());
        assertEquals(12, object4.getRow());
        assertEquals(13, object5.getRow());
    }

    public void testGetEndPosition() {
        assertEquals(17, object1.getColumn());
        assertEquals(17, object2.getColumn());
        assertEquals(19, object3.getColumn());
        assertEquals(19, object4.getColumn());
        assertEquals(17, object5.getColumn());
    }

    public void testHashCode() {
        assertEquals(object1.hashCode(), object2.hashCode());
        assertTrue(object1.hashCode() != object3.hashCode());
        assertTrue(object1.hashCode() != object4.hashCode());
        assertTrue(object1.hashCode() != object5.hashCode());
        assertTrue(object2.hashCode() != object3.hashCode());
        assertTrue(object2.hashCode() != object4.hashCode());
        assertTrue(object2.hashCode() != object5.hashCode());
    }

    public void testEqualsObject() {
        assertEquals(object1, object2);
        assertEquals(object2, object1);
        assertTrue(!object1.equals(object3));
        assertTrue(!object3.equals(object1));
        assertTrue(!object1.equals(object4));
        assertTrue(!object4.equals(object1));
        assertTrue(!object1.equals(object5));
        assertTrue(!object5.equals(object1));
        assertTrue(!object2.equals(object3));
        assertTrue(!object3.equals(object2));
        assertTrue(!object2.equals(object4));
        assertTrue(!object4.equals(object2));
        assertTrue(!object2.equals(object5));
        assertTrue(!object5.equals(object2));
    }

    public void testToString() {
        assertEquals(object1.toString(), object2.toString());
        assertEquals(object2.toString(), object1.toString());
        assertTrue(!object1.toString().equals(object3.toString()));
        assertTrue(!object3.toString().equals(object1.toString()));
        assertTrue(!object1.toString().equals(object4.toString()));
        assertTrue(!object4.toString().equals(object1.toString()));
        assertTrue(!object1.toString().equals(object5.toString()));
        assertTrue(!object5.toString().equals(object1.toString()));
        assertTrue(!object2.toString().equals(object3.toString()));
        assertTrue(!object3.toString().equals(object2.toString()));
        assertTrue(!object2.toString().equals(object4.toString()));
        assertTrue(!object4.toString().equals(object2.toString()));
        assertTrue(!object2.toString().equals(object5.toString()));
        assertTrue(!object5.toString().equals(object2.toString()));
    }

}
