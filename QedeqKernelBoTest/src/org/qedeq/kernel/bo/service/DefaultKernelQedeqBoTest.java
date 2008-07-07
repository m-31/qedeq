/* $Id: DefaultQedeqBo2Test.java,v 1.1 2008/03/27 05:12:45 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.module.DefaultExistenceChecker;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleLabels;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;

/**
 * Test class {@link org.qedeq.kernel.bo.module.QedeqBo}.
 *
 * @version $Revision: 1.1 $
 * @author    Michael Meyling
 */
public class DefaultKernelQedeqBoTest extends QedeqTestCase {
    
    final InternalKernelServices services = new InternalKernelServices() {
            public File getBufferDirectory() {
                return null;
            }
            public File getGenerationDirectory() {
                return null;
            }
            public File getLocalFilePath(ModuleAddress address) {
                return null;
            }
            public void startup() {
            }
            public void removeAllModules() {
            }
            public void clearLocalBuffer() throws IOException {
            }
            public QedeqBo loadModule(ModuleAddress address) {
                return null;
            }
            public void loadRequiredModules(ModuleAddress address) throws SourceFileExceptionList {
            }
            public boolean loadAllModulesFromQedeq() {
                return false;
            }
            public void removeModule(ModuleAddress address) {
            }
            public ModuleAddress[] getAllLoadedModules() {
                return null;
            }
            public QedeqBo getQedeqBo(ModuleAddress address) {
                return null;
            }
            public String getSource(ModuleAddress address) throws IOException {
                return null;
            }
            public ModuleAddress getModuleAddress(URL url) throws IOException {
                return null;
            }
            public ModuleAddress getModuleAddress(String url) throws IOException {
                return null;
            }
            public ModuleAddress getModuleAddress(File file) throws IOException {
                return null;
            }
            public boolean checkModule(ModuleAddress address) {
                return false;
            }
            public InputStream createLatex(ModuleAddress address, String language, String level) throws DefaultSourceFileExceptionList, IOException {
                return null;
            }
            public String generateLatex(ModuleAddress address, String language, String level) throws DefaultSourceFileExceptionList, IOException {
                return null;
            }
            public QedeqFileDao getQedeqFileDao() {
                return null;
            }
            public KernelQedeqBo getKernelQedeqBo(ModuleAddress address) {
                return null;
            }
            public KernelQedeqBo loadModule(ModuleAddress parent, Specification spec) throws SourceFileExceptionList {
                return null;
            }};
    
    public void testConstructor() throws Exception {
        try {
            new DefaultKernelQedeqBo(null, null);
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            // expected
        }
        new DefaultKernelQedeqBo(services, new DefaultModuleAddress("qedeq.org/text.xml"));
    }

    public void testHasFailures() throws Exception {
        DefaultKernelQedeqBo bo;
        bo = new DefaultKernelQedeqBo(services, new DefaultModuleAddress("qedeq.org/test.xml"));
        assertFalse(bo.hasFailures());
        bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, 
            new DefaultSourceFileExceptionList(new NullPointerException()));
        assertTrue(bo.hasFailures());
        bo.setLoaded(new QedeqVo(), new ModuleLabels());
        assertFalse(bo.hasFailures());
        bo = new DefaultKernelQedeqBo(services, new DefaultModuleAddress("qedeq.org/test.xml"));
        bo.setLoaded(new QedeqVo(), new ModuleLabels());
        bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            new DefaultSourceFileExceptionList(new NullPointerException()));
        assertTrue(bo.hasFailures());
        bo.setLoadedRequiredModules(new KernelModuleReferenceList());
        assertFalse(bo.hasFailures());
        bo.setChecked(new DefaultExistenceChecker());
        assertFalse(bo.hasFailures());
    }
    
    public void testSetLoadingFailureState() throws Exception {
        DefaultKernelQedeqBo bo;
        bo = new DefaultKernelQedeqBo(services, new DefaultModuleAddress("qedeq.org/test.xml"));
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
        DefaultKernelQedeqBo bo;
        bo = new DefaultKernelQedeqBo(services, new DefaultModuleAddress("qedeq.org/test.xml"));
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
        DefaultKernelQedeqBo bo;
        bo = new DefaultKernelQedeqBo(services, new DefaultModuleAddress("qedeq.org/test.xml"));
        assertFalse(bo.hasFailures());
        assertNull(bo.getException());
        bo.setLoaded(new QedeqVo(), new ModuleLabels());
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
        bo.setLoaded(new QedeqVo(), new ModuleLabels());
        assertTrue(bo.isLoaded());
        assertNull(bo.getException());
        bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            defaultSourceFileExceptionList);
        assertEquals(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            bo.getDependencyState());
        assertEquals(defaultSourceFileExceptionList, bo.getException());
        bo.setLoaded(new QedeqVo(), new ModuleLabels());
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
