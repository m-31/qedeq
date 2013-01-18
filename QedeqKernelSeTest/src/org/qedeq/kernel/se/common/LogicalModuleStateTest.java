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

package org.qedeq.kernel.se.common;

import java.util.HashSet;
import java.util.Set;

import org.qedeq.base.test.QedeqTestCase;


/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class LogicalModuleStateTest extends QedeqTestCase {

    public LogicalModuleStateTest(){
        super();
    }

    public void testGetCode() {
        assertEquals(0, LogicalModuleState.STATE_UNCHECKED.getCode());
        assertEquals(1, LogicalModuleState.STATE_EXTERNAL_CHECKING.getCode());
        assertEquals(2, LogicalModuleState.STATE_EXTERNAL_CHECKING_FAILED.getCode());
        assertEquals(3, LogicalModuleState.STATE_INTERNAL_CHECKING.getCode());
        assertEquals(4, LogicalModuleState.STATE_INTERNAL_CHECKING_FAILED.getCode());
        assertEquals(5, LogicalModuleState.STATE_CHECKED.getCode());
    }

    public void testGetText() {
        assertEquals("unchecked", LogicalModuleState.STATE_UNCHECKED.getText());
        assertEquals("checking imports", LogicalModuleState.STATE_EXTERNAL_CHECKING.getText());
        assertEquals("checking imports failed", LogicalModuleState.STATE_EXTERNAL_CHECKING_FAILED.getText());
        assertEquals("wf checking", LogicalModuleState.STATE_INTERNAL_CHECKING.getText());
        assertEquals("wf checking failed", LogicalModuleState.STATE_INTERNAL_CHECKING_FAILED.getText());
        assertEquals("well formed", LogicalModuleState.STATE_CHECKED.getText());
    }

    public void testIsFailure() {
        assertFalse(LogicalModuleState.STATE_UNCHECKED.isFailure());
        assertFalse(LogicalModuleState.STATE_EXTERNAL_CHECKING.isFailure());
        assertTrue(LogicalModuleState.STATE_EXTERNAL_CHECKING_FAILED.isFailure());
        assertFalse(LogicalModuleState.STATE_INTERNAL_CHECKING.isFailure());
        assertTrue(LogicalModuleState.STATE_INTERNAL_CHECKING_FAILED.isFailure());
        assertFalse(LogicalModuleState.STATE_CHECKED.isFailure());
    }

    public void testEquals() {
        assertEquals(LogicalModuleState.STATE_CHECKED, LogicalModuleState.STATE_CHECKED);
        assertFalse(LogicalModuleState.STATE_CHECKED.equals(
            LogicalModuleState.STATE_INTERNAL_CHECKING_FAILED));
    }

    public void testHashCode() {
        final Set codes = new HashSet();
        codes.add(new Integer(LogicalModuleState.STATE_UNCHECKED.hashCode()));
        codes.add(new Integer(LogicalModuleState.STATE_EXTERNAL_CHECKING.hashCode()));
        codes.add(new Integer(LogicalModuleState.STATE_EXTERNAL_CHECKING_FAILED.hashCode()));
        codes.add(new Integer(LogicalModuleState.STATE_INTERNAL_CHECKING.hashCode()));
        codes.add(new Integer(LogicalModuleState.STATE_INTERNAL_CHECKING_FAILED.hashCode()));
        codes.add(new Integer(LogicalModuleState.STATE_CHECKED.hashCode()));
        assertEquals(6, codes.size());
    }

}
