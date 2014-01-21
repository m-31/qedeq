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
import org.qedeq.kernel.se.state.LoadingImportsState;


/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class LoadingImportsStateTest extends QedeqTestCase {

    public LoadingImportsStateTest(){
        super();
    }

    public void testGetCode() {
        assertEquals(0, LoadingImportsState.STATE_UNDEFINED.getCode());
        assertEquals(12, LoadingImportsState.STATE_LOADING_IMPORTS.getCode());
        assertEquals(13, LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.getCode());
        assertEquals(14, LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.getCode());
    }

    public void testGetText() {
        assertEquals("undefined", LoadingImportsState.STATE_UNDEFINED.getText());
        assertEquals("loading imported modules", LoadingImportsState.STATE_LOADING_IMPORTS.getText());
        assertEquals("loading imported modules failed", LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.getText());
        assertEquals("loaded imported modules", LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.getText());
    }

    public void testToString() {
        assertEquals("undefined", LoadingImportsState.STATE_UNDEFINED.toString());
        assertEquals("loading imported modules", LoadingImportsState.STATE_LOADING_IMPORTS.toString());
        assertEquals("loading imported modules failed", LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.toString());
        assertEquals("loaded imported modules", LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.toString());
    }

    public void testIsFailure() {
        assertFalse(LoadingImportsState.STATE_UNDEFINED.isFailure());
        assertFalse(LoadingImportsState.STATE_LOADING_IMPORTS.isFailure());
        assertTrue(LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.isFailure());
        assertFalse(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.isFailure());
    }

    public void testAreAllDirectlyRequiredLoaded() {
        assertFalse(LoadingImportsState.STATE_UNDEFINED.areAllDirectlyRequiredLoaded());
        assertFalse(LoadingImportsState.STATE_LOADING_IMPORTS.areAllDirectlyRequiredLoaded());
        assertFalse(LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.areAllDirectlyRequiredLoaded());
        assertTrue(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.areAllDirectlyRequiredLoaded());
    }

    public void testEquals() {
        assertEquals(LoadingImportsState.STATE_UNDEFINED, LoadingImportsState.STATE_UNDEFINED);
        assertEquals(LoadingImportsState.STATE_LOADING_IMPORTS,
            LoadingImportsState.STATE_LOADING_IMPORTS);
        assertEquals(LoadingImportsState.STATE_LOADING_IMPORTS_FAILED,
            LoadingImportsState.STATE_LOADING_IMPORTS_FAILED);
        assertEquals(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES,
            LoadingImportsState.STATE_LOADED_IMPORTED_MODULES);
        assertFalse(LoadingImportsState.STATE_UNDEFINED.equals(null));
        assertFalse(LoadingImportsState.STATE_LOADING_IMPORTS.equals(null));
        assertFalse(LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.equals(null));
        assertFalse(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.equals(null));
        assertFalse(LoadingImportsState.STATE_UNDEFINED.equals(
            LoadingImportsState.STATE_LOADED_IMPORTED_MODULES));
        assertFalse(LoadingImportsState.STATE_LOADING_IMPORTS.equals(
            LoadingImportsState.STATE_LOADED_IMPORTED_MODULES));
        assertFalse(LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.equals(
            LoadingImportsState.STATE_LOADED_IMPORTED_MODULES));
        assertFalse(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.equals(
            LoadingImportsState.STATE_LOADING_IMPORTS));
        assertFalse(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.equals(
            LoadingImportsState.STATE_LOADING_IMPORTS_FAILED));
        assertFalse(LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.equals(
            LoadingImportsState.STATE_LOADING_IMPORTS));
    }

    public void testHashCode() {
        final Set codes = new HashSet();
        codes.add(new Integer(LoadingImportsState.STATE_UNDEFINED.hashCode()));
        codes.add(new Integer(LoadingImportsState.STATE_LOADING_IMPORTS.hashCode()));
        codes.add(new Integer(LoadingImportsState.STATE_LOADING_IMPORTS_FAILED.hashCode()));
        codes.add(new Integer(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES.hashCode()));
        assertEquals(4, codes.size());
    }

}
