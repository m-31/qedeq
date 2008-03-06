/* $Id: QedeqBoTest.java,v 1.9 2007/12/21 23:35:16 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.control;

import org.qedeq.kernel.bo.logic.DefaultExistenceChecker;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.ModuleNodes;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.test.QedeqTestCase;

/**
 * Test class {@link org.qedeq.kernel.bo.module.QedeqBo}.
 *
 * @version $Revision: 1.9 $
 * @author    Michael Meyling
 */
public class DefaultQedeqBo2Test extends QedeqTestCase {
    
    
    public void testConstructor() throws Exception {
        try {
            new DefaultQedeqBo(null);
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            // expected
        }
        new DefaultQedeqBo(new DefaultModuleAddress("qedeq.org/text.xml"));
    }

    public void testHasFailures() throws Exception {
        DefaultQedeqBo bo;
        bo = new DefaultQedeqBo(new DefaultModuleAddress("qedeq.org/test.xml"));
        assertFalse(bo.hasFailures());
        bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, 
            new DefaultSourceFileExceptionList(new NullPointerException()));
        assertTrue(bo.hasFailures());
        bo.setLoaded(new QedeqVo());
        bo.setLabels(new ModuleNodes());
        assertFalse(bo.hasFailures());
        bo = new DefaultQedeqBo(new DefaultModuleAddress("qedeq.org/test.xml"));
        bo.setLoaded(new QedeqVo());
        bo.setLabels(new ModuleNodes());
        bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            new DefaultSourceFileExceptionList(new NullPointerException()));
        assertTrue(bo.hasFailures());
        bo.setLoadedRequiredModules(new DefaultModuleReferenceList());
        assertFalse(bo.hasFailures());
        bo.setChecked(new DefaultExistenceChecker());
        assertFalse(bo.hasFailures());
    }
    
    public void testSetLoadingFailureState() throws Exception {
        DefaultQedeqBo bo;
        bo = new DefaultQedeqBo(new DefaultModuleAddress("qedeq.org/test.xml"));
        assertFalse(bo.hasFailures());
        assertNull(bo.getException());
        try {
            bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, 
                null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        final DefaultSourceFileExceptionList defaultSourceFileExceptionList
            = new DefaultSourceFileExceptionList(new NullPointerException());
        try {
            bo.setLoadingFailureState(null, 
                defaultSourceFileExceptionList);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED,
            defaultSourceFileExceptionList);
        assertEquals(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED,
            bo.getLoadingState());
        assertEquals(defaultSourceFileExceptionList, bo.getException());
        bo.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED,
            defaultSourceFileExceptionList);
        assertEquals(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED,
            bo.getLoadingState());
        bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_WEB_FAILED,
            defaultSourceFileExceptionList);
        assertEquals(LoadingState.STATE_LOADING_FROM_WEB_FAILED,
            bo.getLoadingState());
        try {
            bo.setLoadingFailureState(LoadingState.STATE_LOADED, 
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER, 
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_WEB, 
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            bo.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY, 
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            bo.setLoadingFailureState(LoadingState.STATE_LOCATING_WITHIN_WEB, 
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            bo.setLoadingFailureState(LoadingState.STATE_UNDEFINED, 
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    public void testSetLoadingProgressState() throws Exception {
        DefaultQedeqBo bo;
        bo = new DefaultQedeqBo(new DefaultModuleAddress("qedeq.org/test.xml"));
        assertFalse(bo.hasFailures());
        assertFalse(bo.isLoaded());
        assertEquals(LoadingState.STATE_UNDEFINED, bo.getLoadingState());
        try {
            bo.setLoadingProgressState(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            bo.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED); 
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            bo.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB_FAILED); 
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            bo.setLoadingProgressState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED); 
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        bo.setLoadingProgressState(LoadingState.STATE_UNDEFINED);
        assertEquals(LoadingState.STATE_UNDEFINED, bo.getLoadingState());
        bo.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_BUFFER);
        assertEquals(LoadingState.STATE_LOADING_FROM_BUFFER, bo.getLoadingState());
        bo.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);
        assertEquals(LoadingState.STATE_LOADING_FROM_WEB, bo.getLoadingState());
        bo.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);
        assertEquals(LoadingState.STATE_LOADING_FROM_WEB, bo.getLoadingState());
        bo.setLoadingProgressState(LoadingState.STATE_LOADING_INTO_MEMORY);
        assertEquals(LoadingState.STATE_LOADING_INTO_MEMORY, bo.getLoadingState());
        bo.setLoadingProgressState(LoadingState.STATE_LOCATING_WITHIN_WEB);
        assertEquals(LoadingState.STATE_LOCATING_WITHIN_WEB, bo.getLoadingState());
        bo.setLoadingProgressState(LoadingState.STATE_UNDEFINED);
        assertEquals(LoadingState.STATE_UNDEFINED, bo.getLoadingState());
        try {
            bo.setLoadingProgressState(LoadingState.STATE_LOADED); 
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    public void testSetDependencyFailureState() throws Exception {
        DefaultQedeqBo bo;
        bo = new DefaultQedeqBo(new DefaultModuleAddress("qedeq.org/test.xml"));
        assertFalse(bo.hasFailures());
        assertNull(bo.getException());
        bo.setLoaded(new QedeqVo());
        bo.setLabels(new ModuleNodes());
        assertTrue(bo.isLoaded());
        assertNull(bo.getException());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
                null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        final DefaultSourceFileExceptionList defaultSourceFileExceptionList
            = new DefaultSourceFileExceptionList(new NullPointerException());
        try {
            bo.setLoadingFailureState(null, 
                defaultSourceFileExceptionList);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        bo.setLoadingProgressState(LoadingState.STATE_UNDEFINED);
        assertNull(bo.getException());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
                defaultSourceFileExceptionList);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, 
            defaultSourceFileExceptionList);
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
                defaultSourceFileExceptionList);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        bo.setLoaded(new QedeqVo());
        bo.setLabels(new ModuleNodes());
        assertTrue(bo.isLoaded());
        assertNull(bo.getException());
        bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            defaultSourceFileExceptionList);
        assertEquals(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            bo.getDependencyState());
        assertEquals(defaultSourceFileExceptionList, bo.getException());
        bo.setLoaded(new QedeqVo());
        bo.setLabels(new ModuleNodes());
        assertTrue(bo.isLoaded());
        assertNull(bo.getException());
        bo.setDependencyProgressState(DependencyState.STATE_UNDEFINED);
        try {
            bo.setDependencyFailureState(DependencyState.STATE_UNDEFINED,
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(DependencyState.STATE_UNDEFINED, bo.getDependencyState());
        assertNull(bo.getException());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES,
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(DependencyState.STATE_UNDEFINED, bo.getDependencyState());
        assertNull(bo.getException());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADED_REQUIRED_MODULES,
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(DependencyState.STATE_UNDEFINED, bo.getDependencyState());
        assertNull(bo.getException());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADED_REQUIRED_MODULES,
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(DependencyState.STATE_UNDEFINED, bo.getDependencyState());
        assertNull(bo.getException());
    }

}
