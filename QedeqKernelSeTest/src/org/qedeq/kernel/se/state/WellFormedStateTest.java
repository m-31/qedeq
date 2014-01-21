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
import org.qedeq.kernel.se.state.WellFormedState;


/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class WellFormedStateTest extends QedeqTestCase {

    public WellFormedStateTest(){
        super();
    }

    public void testGetCode() {
        assertEquals(0, WellFormedState.STATE_UNCHECKED.getCode());
        assertEquals(16, WellFormedState.STATE_EXTERNAL_CHECKING.getCode());
        assertEquals(17, WellFormedState.STATE_EXTERNAL_CHECKING_FAILED.getCode());
        assertEquals(18, WellFormedState.STATE_INTERNAL_CHECKING.getCode());
        assertEquals(19, WellFormedState.STATE_INTERNAL_CHECKING_FAILED.getCode());
        assertEquals(20, WellFormedState.STATE_CHECKED.getCode());
    }

    public void testGetText() {
        assertEquals("unchecked", WellFormedState.STATE_UNCHECKED.getText());
        assertEquals("checking imports", WellFormedState.STATE_EXTERNAL_CHECKING.getText());
        assertEquals("checking imports failed", WellFormedState.STATE_EXTERNAL_CHECKING_FAILED.getText());
        assertEquals("wf checking", WellFormedState.STATE_INTERNAL_CHECKING.getText());
        assertEquals("wf checking failed", WellFormedState.STATE_INTERNAL_CHECKING_FAILED.getText());
        assertEquals("well formed", WellFormedState.STATE_CHECKED.getText());
    }

    public void testToString() {
        assertEquals("unchecked", WellFormedState.STATE_UNCHECKED.toString());
        assertEquals("checking imports", WellFormedState.STATE_EXTERNAL_CHECKING.toString());
        assertEquals("checking imports failed", WellFormedState.STATE_EXTERNAL_CHECKING_FAILED.toString());
        assertEquals("wf checking", WellFormedState.STATE_INTERNAL_CHECKING.toString());
        assertEquals("wf checking failed", WellFormedState.STATE_INTERNAL_CHECKING_FAILED.toString());
        assertEquals("well formed", WellFormedState.STATE_CHECKED.toString());
    }

    public void testIsFailure() {
        assertFalse(WellFormedState.STATE_UNCHECKED.isFailure());
        assertFalse(WellFormedState.STATE_EXTERNAL_CHECKING.isFailure());
        assertTrue(WellFormedState.STATE_EXTERNAL_CHECKING_FAILED.isFailure());
        assertFalse(WellFormedState.STATE_INTERNAL_CHECKING.isFailure());
        assertTrue(WellFormedState.STATE_INTERNAL_CHECKING_FAILED.isFailure());
        assertFalse(WellFormedState.STATE_CHECKED.isFailure());
    }

    public void testEquals() {
        assertEquals(WellFormedState.STATE_CHECKED, WellFormedState.STATE_CHECKED);
        assertFalse(WellFormedState.STATE_CHECKED.equals(
            WellFormedState.STATE_INTERNAL_CHECKING_FAILED));
        assertFalse(WellFormedState.STATE_CHECKED.equals(
            WellFormedState.STATE_CHECKED.toString()));
    }

    public void testHashCode() {
        final Set codes = new HashSet();
        codes.add(new Integer(WellFormedState.STATE_UNCHECKED.hashCode()));
        codes.add(new Integer(WellFormedState.STATE_EXTERNAL_CHECKING.hashCode()));
        codes.add(new Integer(WellFormedState.STATE_EXTERNAL_CHECKING_FAILED.hashCode()));
        codes.add(new Integer(WellFormedState.STATE_INTERNAL_CHECKING.hashCode()));
        codes.add(new Integer(WellFormedState.STATE_INTERNAL_CHECKING_FAILED.hashCode()));
        codes.add(new Integer(WellFormedState.STATE_CHECKED.hashCode()));
        assertEquals(6, codes.size());
    }

}
