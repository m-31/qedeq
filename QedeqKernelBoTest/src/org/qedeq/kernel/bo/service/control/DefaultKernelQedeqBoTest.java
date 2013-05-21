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

package org.qedeq.kernel.bo.service.control;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.service.control.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.service.control.Element2LatexImpl;
import org.qedeq.kernel.bo.service.control.Element2Utf8Impl;
import org.qedeq.kernel.bo.service.logic.ModuleConstantsExistenceCheckerImpl;
import org.qedeq.kernel.bo.test.DummyInternalKernelServices;
import org.qedeq.kernel.bo.test.DummyPlugin;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.QedeqException;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.module.QedeqVo;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.state.LoadingState;

/**
 * Test class {@link org.qedeq.kernel.bo.module.QedeqBo}.
 *
 * @author  Michael Meyling
 */
public class DefaultKernelQedeqBoTest extends QedeqTestCase {

    final InternalKernelServices services = new DummyInternalKernelServices();
    
    final Plugin plugin = new Plugin() {

        public String getServiceId() {
            return this.getClass().getName();
        }

        public String getServiceAction() {
            return "basic testing kernel";
        }

        public String getServiceDescription() {
            return "Testing Kernel";
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
        assertFalse(bo.hasErrors());
        bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED,
            new SourceFileExceptionList(new SourceFileException(DummyPlugin.getInstance(),
                new QedeqException(1, "myError") {}, null, null)));
        assertTrue(bo.hasErrors());
        ModuleLabels labels = new ModuleLabels();
        Element2LatexImpl converter = new Element2LatexImpl(labels);
        Element2Utf8 textConverter = new Element2Utf8Impl(converter);
        bo.setLoaded(new QedeqVo(), labels, converter, textConverter);
        assertFalse(bo.hasErrors());
        bo = new DefaultKernelQedeqBo(services, new DefaultModuleAddress("qedeq.org/test.xml"));
        bo.setLoaded(new QedeqVo(), labels, converter, textConverter);
        bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            new SourceFileExceptionList(new SourceFileException(DummyPlugin.getInstance(),
                    new QedeqException(1, "myError") {}, null, null)));
        assertTrue(bo.hasErrors());
        bo.setLoadedImports(new KernelModuleReferenceList());
        assertFalse(bo.hasErrors());
        bo.setLoadedRequiredModules();
        assertFalse(bo.hasErrors());
        bo.setWellFormed(new ModuleConstantsExistenceCheckerImpl(bo));
        assertFalse(bo.hasErrors());
    }

    public void testSetLoadingFailureState() throws Exception {
        DefaultKernelQedeqBo bo;
        bo = new DefaultKernelQedeqBo(services, new DefaultModuleAddress("qedeq.org/test.xml"));
        assertFalse(bo.hasErrors());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        try {
            bo.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED,
                null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        final SourceFileExceptionList defaultSourceFileExceptionList
            = new SourceFileExceptionList(new SourceFileException(DummyPlugin.getInstance(),
                new QedeqException(1, "myError") {}, null, null));
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
        assertEquals(defaultSourceFileExceptionList, bo.getErrors());
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
        assertFalse(bo.hasErrors());
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
        assertFalse(bo.hasErrors());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        ModuleLabels labels = new ModuleLabels();
        Element2LatexImpl converter = new Element2LatexImpl(labels);
        Element2Utf8 textConverter = new Element2Utf8Impl(converter);
        bo.setLoaded(new QedeqVo(), labels, converter, textConverter);
        assertTrue(bo.isLoaded());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
                null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        final SourceFileExceptionList defaultSourceFileExceptionList
            = new SourceFileExceptionList(new SourceFileException(DummyPlugin.getInstance(),
                new QedeqException(1, "myError") {}, null, null));
        try {
            bo.setLoadingFailureState(null,
                defaultSourceFileExceptionList);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        bo.setLoadingProgressState(LoadingState.STATE_UNDEFINED);
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
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
        labels = new ModuleLabels();
        converter = new Element2LatexImpl(labels);
        textConverter = new Element2Utf8Impl(converter);
        bo.setLoaded(new QedeqVo(), labels, converter, textConverter);
        assertTrue(bo.isLoaded());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            defaultSourceFileExceptionList);
        assertEquals(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
            bo.getDependencyState());
        assertEquals(defaultSourceFileExceptionList, bo.getErrors());
        bo.setLoaded(new QedeqVo(), labels, converter, textConverter);
        assertTrue(bo.isLoaded());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        bo.setDependencyProgressState(DependencyState.STATE_UNDEFINED);
        try {
            bo.setDependencyFailureState(DependencyState.STATE_UNDEFINED,
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(DependencyState.STATE_UNDEFINED, bo.getDependencyState());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES,
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(DependencyState.STATE_UNDEFINED, bo.getDependencyState());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADED_REQUIRED_MODULES,
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(DependencyState.STATE_UNDEFINED, bo.getDependencyState());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
        try {
            bo.setDependencyFailureState(DependencyState.STATE_LOADED_REQUIRED_MODULES,
                defaultSourceFileExceptionList);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(DependencyState.STATE_UNDEFINED, bo.getDependencyState());
        assertNotNull(bo.getErrors());
        assertEquals(0, bo.getErrors().size());
    }

}
