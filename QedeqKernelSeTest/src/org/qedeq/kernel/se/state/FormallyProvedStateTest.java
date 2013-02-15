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

package org.qedeq.kernel.se.state;

import java.util.HashSet;
import java.util.Set;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.state.FormallyProvedState;


/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class FormallyProvedStateTest extends QedeqTestCase {

    public FormallyProvedStateTest(){
        super();
    }

    public void testGetCode() {
        assertEquals(0, FormallyProvedState.STATE_UNCHECKED.getCode());
        assertEquals(21, FormallyProvedState.STATE_EXTERNAL_CHECKING.getCode());
        assertEquals(22, FormallyProvedState.STATE_EXTERNAL_CHECKING_FAILED.getCode());
        assertEquals(23, FormallyProvedState.STATE_INTERNAL_CHECKING.getCode());
        assertEquals(24, FormallyProvedState.STATE_INTERNAL_CHECKING_FAILED.getCode());
        assertEquals(25, FormallyProvedState.STATE_CHECKED.getCode());
    }

    public void testGetText() {
        assertEquals("unchecked", FormallyProvedState.STATE_UNCHECKED.getText());
        assertEquals("checking imports", FormallyProvedState.STATE_EXTERNAL_CHECKING.getText());
        assertEquals("checking imports failed", FormallyProvedState.STATE_EXTERNAL_CHECKING_FAILED.getText());
        assertEquals("wf checking", FormallyProvedState.STATE_INTERNAL_CHECKING.getText());
        assertEquals("wf checking failed", FormallyProvedState.STATE_INTERNAL_CHECKING_FAILED.getText());
        assertEquals("well formed", FormallyProvedState.STATE_CHECKED.getText());
    }

    public void testToString() {
        assertEquals("unchecked", FormallyProvedState.STATE_UNCHECKED.toString());
        assertEquals("checking imports", FormallyProvedState.STATE_EXTERNAL_CHECKING.toString());
        assertEquals("checking imports failed", FormallyProvedState.STATE_EXTERNAL_CHECKING_FAILED.toString());
        assertEquals("wf checking", FormallyProvedState.STATE_INTERNAL_CHECKING.toString());
        assertEquals("wf checking failed", FormallyProvedState.STATE_INTERNAL_CHECKING_FAILED.toString());
        assertEquals("well formed", FormallyProvedState.STATE_CHECKED.toString());
    }

    public void testIsFailure() {
        assertFalse(FormallyProvedState.STATE_UNCHECKED.isFailure());
        assertFalse(FormallyProvedState.STATE_EXTERNAL_CHECKING.isFailure());
        assertTrue(FormallyProvedState.STATE_EXTERNAL_CHECKING_FAILED.isFailure());
        assertFalse(FormallyProvedState.STATE_INTERNAL_CHECKING.isFailure());
        assertTrue(FormallyProvedState.STATE_INTERNAL_CHECKING_FAILED.isFailure());
        assertFalse(FormallyProvedState.STATE_CHECKED.isFailure());
    }

    public void testEquals() {
        assertEquals(FormallyProvedState.STATE_CHECKED, FormallyProvedState.STATE_CHECKED);
        assertFalse(FormallyProvedState.STATE_CHECKED.equals(
            FormallyProvedState.STATE_INTERNAL_CHECKING_FAILED));
        assertFalse(FormallyProvedState.STATE_CHECKED.equals(
            FormallyProvedState.STATE_CHECKED.toString()));
    }

    public void testHashCode() {
        final Set codes = new HashSet();
        codes.add(new Integer(FormallyProvedState.STATE_UNCHECKED.hashCode()));
        codes.add(new Integer(FormallyProvedState.STATE_EXTERNAL_CHECKING.hashCode()));
        codes.add(new Integer(FormallyProvedState.STATE_EXTERNAL_CHECKING_FAILED.hashCode()));
        codes.add(new Integer(FormallyProvedState.STATE_INTERNAL_CHECKING.hashCode()));
        codes.add(new Integer(FormallyProvedState.STATE_INTERNAL_CHECKING_FAILED.hashCode()));
        codes.add(new Integer(FormallyProvedState.STATE_CHECKED.hashCode()));
        assertEquals(6, codes.size());
    }

}
