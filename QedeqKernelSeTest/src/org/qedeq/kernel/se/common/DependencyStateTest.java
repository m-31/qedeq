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
public class DependencyStateTest extends QedeqTestCase {

    public DependencyStateTest(){
        super();
    }

    public void testGetCode() {
        assertEquals(0, DependencyState.STATE_UNDEFINED.getCode());
        assertEquals(11, DependencyState.STATE_LOADING_REQUIRED_MODULES.getCode());
        assertEquals(12, DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED.getCode());
        assertEquals(15, DependencyState.STATE_LOADED_REQUIRED_MODULES.getCode());
    }

    public void testGetText() {
        assertEquals("undefined", DependencyState.STATE_UNDEFINED.getText());
        assertEquals("loading required modules", DependencyState.STATE_LOADING_REQUIRED_MODULES.getText());
        assertEquals("loading required modules failed", DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED.getText());
        assertEquals("loaded required modules", DependencyState.STATE_LOADED_REQUIRED_MODULES.getText());
    }

    public void testIsFailure() {
        assertFalse(DependencyState.STATE_UNDEFINED.isFailure());
        assertFalse(DependencyState.STATE_LOADING_REQUIRED_MODULES.isFailure());
        assertTrue(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED.isFailure());
        assertFalse(DependencyState.STATE_LOADED_REQUIRED_MODULES.isFailure());
    }

    public void testAreAllRequiredLoaded() {
        assertFalse(DependencyState.STATE_UNDEFINED.areAllRequiredLoaded());
        assertFalse(DependencyState.STATE_LOADING_REQUIRED_MODULES.areAllRequiredLoaded());
        assertFalse(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED.areAllRequiredLoaded());
        assertTrue(DependencyState.STATE_LOADED_REQUIRED_MODULES.areAllRequiredLoaded());
    }

    public void testEquals() {
        assertEquals(DependencyState.STATE_UNDEFINED, DependencyState.STATE_UNDEFINED);
        assertEquals(DependencyState.STATE_LOADING_REQUIRED_MODULES,
            DependencyState.STATE_LOADING_REQUIRED_MODULES);
        assertEquals(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED);
        assertEquals(DependencyState.STATE_LOADED_REQUIRED_MODULES,
            DependencyState.STATE_LOADED_REQUIRED_MODULES);
        assertFalse(DependencyState.STATE_UNDEFINED.equals(null));
        assertFalse(DependencyState.STATE_LOADING_REQUIRED_MODULES.equals(null));
        assertFalse(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED.equals(null));
        assertFalse(DependencyState.STATE_LOADED_REQUIRED_MODULES.equals(null));
        assertFalse(DependencyState.STATE_UNDEFINED.equals(
            DependencyState.STATE_LOADED_REQUIRED_MODULES));
        assertFalse(DependencyState.STATE_LOADING_REQUIRED_MODULES.equals(
            DependencyState.STATE_LOADED_REQUIRED_MODULES));
        assertFalse(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED.equals(
            DependencyState.STATE_LOADED_REQUIRED_MODULES));
        assertFalse(DependencyState.STATE_LOADED_REQUIRED_MODULES.equals(
            DependencyState.STATE_LOADING_REQUIRED_MODULES));
        assertFalse(DependencyState.STATE_LOADED_REQUIRED_MODULES.equals(
            DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED));
        assertFalse(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED.equals(
            DependencyState.STATE_LOADING_REQUIRED_MODULES));
    }

    public void testHashCode() {
        final Set codes = new HashSet();
        codes.add(new Integer(DependencyState.STATE_UNDEFINED.hashCode()));
        codes.add(new Integer(DependencyState.STATE_LOADING_REQUIRED_MODULES.hashCode()));
        codes.add(new Integer(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED.hashCode()));
        codes.add(new Integer(DependencyState.STATE_LOADED_REQUIRED_MODULES.hashCode()));
        assertEquals(4, codes.size());
    }

}