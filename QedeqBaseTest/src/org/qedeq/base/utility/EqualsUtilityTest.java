/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
 * Test {@link EqualsUtility}.
 *
 * @author  Michael Meyling
 */
public class EqualsUtilityTest extends QedeqTestCase {

    /**
     * Test {@link EqualsUtility#equals(Object, Object)}.
     *
     * @throws Exception
     */
    public void testEqualsObject() throws Exception {
        assertTrue(EqualsUtility.equals("A", "A"));
        final Object obj1 = new Object();
        final Object obj2 = new Object();
        assertFalse(EqualsUtility.equals(obj1, obj2));
        assertFalse(EqualsUtility.equals(obj2, obj1));
        assertTrue(EqualsUtility.equals(obj1, obj1));
        assertTrue(EqualsUtility.equals(obj2, obj2));
        assertFalse(EqualsUtility.equals(obj1, null));
        assertTrue(EqualsUtility.equals((Object) null, null));
        assertFalse(EqualsUtility.equals(null, obj1));
        assertFalse(EqualsUtility.equals(obj2, null));
        assertFalse(EqualsUtility.equals(null, obj2));
    }

    /**
     * Test {@link EqualsUtility#equals(byte[], byte[])}.
     *
     * @throws Exception
     */
    public void testEqualsByteArray() throws Exception {
        final byte[] obj1 = new byte[10];
        final byte[] obj2 = new byte[10];
        final byte[] obj3 = new byte[9];
        assertTrue(EqualsUtility.equals(obj1, obj2));
        assertTrue(EqualsUtility.equals(obj2, obj1));
        assertTrue(EqualsUtility.equals(obj1, obj1));
        assertTrue(EqualsUtility.equals(obj2, obj2));
        assertFalse(EqualsUtility.equals(obj1, null));
        assertTrue(EqualsUtility.equals((byte[]) null, null));
        assertFalse(EqualsUtility.equals(null, obj1));
        assertFalse(EqualsUtility.equals(obj2, null));
        assertFalse(EqualsUtility.equals(null, obj2));
        assertFalse(EqualsUtility.equals(obj1, obj3));
        assertFalse(EqualsUtility.equals(obj3, obj2));
        obj1[5] = 7;
        assertFalse(EqualsUtility.equals(obj1, obj2));
        assertFalse(EqualsUtility.equals(obj2, obj1));
    }

}
