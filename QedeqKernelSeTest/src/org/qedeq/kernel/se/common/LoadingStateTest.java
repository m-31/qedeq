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
public class LoadingStateTest extends QedeqTestCase {

    public LoadingStateTest(){
        super();
    }

    public void testGetCode() {
        assertEquals(0, LoadingState.STATE_UNDEFINED.getCode());
        assertEquals(1, LoadingState.STATE_LOCATING_WITHIN_WEB.getCode());
        assertEquals(2, LoadingState.STATE_LOCATING_WITHIN_WEB_FAILED.getCode());
        assertEquals(3, LoadingState.STATE_LOADING_FROM_WEB.getCode());
        assertEquals(4, LoadingState.STATE_LOADING_FROM_WEB_FAILED.getCode());
        assertEquals(7, LoadingState.STATE_LOADING_FROM_BUFFER.getCode());
        assertEquals(8, LoadingState.STATE_LOADING_FROM_BUFFER_FAILED.getCode());
        assertEquals(5, LoadingState.STATE_LOADING_FROM_LOCAL_FILE.getCode());
        assertEquals(6, LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED.getCode());
        assertEquals(9, LoadingState.STATE_LOADING_INTO_MEMORY.getCode());
        assertEquals(10, LoadingState.STATE_LOADING_INTO_MEMORY_FAILED.getCode());
        assertEquals(11, LoadingState.STATE_LOADED.getCode());
    }

    public void testGetText() {
        assertEquals("undefined", LoadingState.STATE_UNDEFINED.getText());
        assertEquals("locating within web", LoadingState.STATE_LOCATING_WITHIN_WEB.getText());
        assertEquals("locating within web failed", LoadingState.STATE_LOCATING_WITHIN_WEB_FAILED.getText());
        assertEquals("loading from web", LoadingState.STATE_LOADING_FROM_WEB.getText());
        assertEquals("loading from web failed", LoadingState.STATE_LOADING_FROM_WEB_FAILED.getText());
        assertEquals("loading from local buffer", LoadingState.STATE_LOADING_FROM_BUFFER.getText());
        assertEquals("loading from local buffer failed", LoadingState.STATE_LOADING_FROM_BUFFER_FAILED.getText());
        assertEquals("loading from local file", LoadingState.STATE_LOADING_FROM_LOCAL_FILE.getText());
        assertEquals("loading from local file failed", LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED.getText());
        assertEquals("loading into memory", LoadingState.STATE_LOADING_INTO_MEMORY.getText());
        assertEquals("loading into memory failed", LoadingState.STATE_LOADING_INTO_MEMORY_FAILED.getText());
        assertEquals("loaded", LoadingState.STATE_LOADED.getText());
    }

    public void testIsFailure() {
        assertFalse(LoadingState.STATE_UNDEFINED.isFailure());
        assertFalse(LoadingState.STATE_LOCATING_WITHIN_WEB.isFailure());
        assertTrue(LoadingState.STATE_LOCATING_WITHIN_WEB_FAILED.isFailure());
        assertFalse(LoadingState.STATE_LOADING_FROM_WEB.isFailure());
        assertTrue(LoadingState.STATE_LOADING_FROM_WEB_FAILED.isFailure());
        assertFalse(LoadingState.STATE_LOADING_FROM_BUFFER.isFailure());
        assertTrue(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED.isFailure());
        assertFalse(LoadingState.STATE_LOADING_FROM_LOCAL_FILE.isFailure());
        assertTrue(LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED.isFailure());
        assertFalse(LoadingState.STATE_LOADING_INTO_MEMORY.isFailure());
        assertTrue(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED.isFailure());
        assertFalse(LoadingState.STATE_LOADED.isFailure());
    }

    public void testEquals() {
        assertEquals(LoadingState.STATE_UNDEFINED, LoadingState.STATE_UNDEFINED);
    }

    public void testHashCode() {
        final Set codes = new HashSet();
        codes.add(new Integer(LoadingState.STATE_UNDEFINED.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOCATING_WITHIN_WEB.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOCATING_WITHIN_WEB_FAILED.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADING_FROM_WEB.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADING_FROM_WEB_FAILED.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADING_FROM_BUFFER.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADING_FROM_LOCAL_FILE.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADING_INTO_MEMORY.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED.hashCode()));
        codes.add(new Integer(LoadingState.STATE_LOADED.hashCode()));
        assertEquals(12, codes.size());
    }

}
