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

package org.qedeq.kernel.bo.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.state.FormallyProvedState;
import org.qedeq.kernel.se.state.LoadingState;
import org.qedeq.kernel.se.state.WellFormedState;

/**
 * Test class.
 *
 * @author  Michael Meyling
 */
public class DefaultModuleEventListenerTest extends QedeqBoTestCase {

    private DefaultModuleEventListener listener;
    private ByteArrayOutputStream out;
    private QedeqBo qedeq;

    protected void setUp() throws Exception {
        super.setUp();
        out = new ByteArrayOutputStream();
        listener = new DefaultModuleEventListener(new PrintStream(out));
        qedeq = new QedeqBo() {
            
            public boolean isSupportedLanguage(String language) {
                return false;
            }
            
            public boolean isLoaded() {
                return false;
            }
            
            public boolean wasCheckedForBeingWellFormed() {
                return false;
            }
            
            public boolean wasCheckedForBeingFormallyProved() {
                return false;
            }
            
            public boolean hasWarnings() {
                return false;
            }
            
            public boolean hasLoadedRequiredModules() {
                return false;
            }
            
            public boolean hasErrors() {
                return false;
            }
            
            public boolean hasBasicFailures() {
                return false;
            }
            
            public SourceFileExceptionList getWarnings() {
                return new SourceFileExceptionList();
            }
            
            public String getUrl() {
                return "dummy";
            }
            
            public String[] getSupportedLanguages() {
                return ArrayUtils.EMPTY_STRING_ARRAY;
            }
            
            public String getStateDescription() {
                return LoadingState.STATE_LOADING_INTO_MEMORY.getText();
            }
            
            public String getRuleVersion() {
                return "1.00.00";
            }
            
            public ModuleReferenceList getRequiredModules() {
                return new KernelModuleReferenceList();
            }
            
            public Qedeq getQedeq() {
                return null;
            }
            
            public String getOriginalLanguage() {
                return "en";
            }
            
            public String getName() {
                return "dummy";
            }
            
            public ModuleAddress getModuleAddress() {
                return new DefaultModuleAddress();
            }
            
            public WellFormedState getWellFormedState() {
                return WellFormedState.STATE_UNCHECKED;
            }
            
            public LoadingState getLoadingState() {
                return LoadingState.STATE_LOADING_INTO_MEMORY;
            }
            
            public int getLoadingCompleteness() {
                return 0;
            }
            
            public SourceFileExceptionList getErrors() {
                return new SourceFileExceptionList();
            }
            
            public DependencyState getDependencyState() {
                return DependencyState.STATE_UNDEFINED;
            }

            public FormallyProvedState getFormallyProvedState() {
                return FormallyProvedState.STATE_UNCHECKED;
            }
        };
    }

    public void testAddModule() throws Exception {
        listener.addModule(qedeq);
//        System.out.println(out.toString("UTF-8"));
        assertTrue(out.toString("UTF-8").trim().endsWith("dummy"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("Module added"));
    }

    public void testRemoveModule() throws Exception {
        listener.removeModule(qedeq);
//        System.out.println(out.toString("UTF-8"));
        assertTrue(out.toString("UTF-8").trim().endsWith("dummy"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("Module removed"));
    }

    public void testStateChanged() throws Exception {
        listener.stateChanged(qedeq);
//        System.out.println(out.toString("UTF-8"));
        assertTrue(out.toString("UTF-8").trim().endsWith("dummy"));
        assertTrue(0 <= out.toString("UTF-8").indexOf("Module state changed"));
    }

    public void testSetPrintStream() throws Exception {
        listener = new DefaultModuleEventListener();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        listener.setPrintStream(new PrintStream(out2));
        listener.addModule(qedeq);
//        System.out.println(out2.toString("UTF-8"));
        assertTrue(out2.toString("UTF-8").trim().endsWith("dummy"));
        assertTrue(0 <= out2.toString("UTF-8").indexOf("add"));
    }

}