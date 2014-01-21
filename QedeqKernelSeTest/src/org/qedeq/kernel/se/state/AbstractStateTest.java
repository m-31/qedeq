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

package org.qedeq.kernel.se.state;

import java.util.HashSet;
import java.util.Set;

import org.qedeq.base.test.QedeqTestCase;


/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class AbstractStateTest extends QedeqTestCase {

    private AbstractState instance1;

    private AbstractState instance2;

    private AbstractState instance3;

    public void setUp() throws Exception  {
        super.setUp();
        instance1 = new AbstractState("A Text", true, 13) {};
        instance2 = new AbstractState("A Text", true, 13) {};
        instance3 = new AbstractState("Another Text", false, 7) {};
    }
    
    public void testConstructor() {
        try {
            new AbstractState(null, true, 0) {};
            fail("Exception expected");
        } catch (RuntimeException e) {
            // ok
        }
    }

    public void testGetCode() {
        assertEquals(13, instance1.getCode());
        assertEquals(13, instance2.getCode());
        assertEquals(7, instance3.getCode());
    }

    public void testGetText() {
        assertEquals("A Text", instance1.getText());
        assertEquals("A Text", instance2.getText());
        assertEquals("Another Text", instance3.getText());
    }

    public void testToString() {
        assertEquals("A Text", instance1.toString());
        assertEquals("A Text", instance2.toString());
        assertEquals("Another Text", instance3.toString());
    }

    public void testIsFailure() {
        assertTrue(instance1.isFailure());
        assertTrue(instance2.isFailure());
        assertFalse(instance3.isFailure());
    }

    public void testEquals() {
        assertFalse(instance1.equals(instance2));
        assertFalse(instance1.equals(instance3));
        assertTrue(instance1.equals(instance1));
        assertTrue(instance3.equals(instance3));
    }

    public void testHashCode() {
        final Set codes = new HashSet();
        codes.add(new Integer(instance1.hashCode()));
        codes.add(new Integer(instance2.hashCode()));
        codes.add(new Integer(instance3.hashCode()));
        assertEquals(2, codes.size());
    }

}
