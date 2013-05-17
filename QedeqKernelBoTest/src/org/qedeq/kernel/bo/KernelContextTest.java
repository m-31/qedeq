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

package org.qedeq.kernel.bo;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.qedeq.kernel.bo.test.DummyServiceModule;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.config.QedeqConfig;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * Test class.
 *
 * @author  Michael Meyling
 */
public class KernelContextTest extends QedeqBoTestCase {

    private KernelContext kernel;

    protected void setUp() {
        // we don't use super here!
        kernel = KernelContext.getInstance();
        kernel.shutdown();
    }

    protected void tearDown() {
        // we don't use super here!
        kernel.shutdown();
    }

    public void testGetBuildId() {
        assertNotNull(kernel.getBuildId());
    }

    public void testGetKernelVersion() {
        assertNotNull(kernel.getKernelVersion());
    }

    public void testGetDedicationVersion() {
        assertNotNull(kernel.getDedication());
    }

    public void testGetDescriptiveKernelVersion() {
        assertNotNull(kernel.getDescriptiveKernelVersion());
    }

    public void testGetKernelCodeName() {
        assertNotNull(kernel.getKernelCodeName());
    }

    public void testGetKernelVersionDirectory() {
        assertNotNull(kernel.getKernelVersionDirectory());
    }

    public void testIsRuleVersionSupported() {
        assertTrue(kernel.isRuleVersionSupported("1.01.00"));
        assertTrue(kernel.isRuleVersionSupported(kernel.getMaximalRuleVersion()));
        assertFalse(kernel.isRuleVersionSupported("1.00.00"));
    }

    public void testGetMaximalRuleVersion() {
        assertEquals("1.01.00", kernel.getMaximalRuleVersion());
    }

    public void testIsSetConnectionTimeOutSupported() {
        if (System.getProperty("java.runtime.version").compareTo("1.5") <= 0) {
            assertFalse(kernel.isSetConnectionTimeOutSupported());
        } else {
            assertTrue(kernel.isSetConnectionTimeOutSupported());
        }
    }

    public void testIsSetReadTimeoutSupported() {
        if (System.getProperty("java.runtime.version").compareTo("1.5") <= 0) {
            assertFalse(kernel.isSetReadTimeoutSupported());
        } else {
            assertTrue(kernel.isSetReadTimeoutSupported());
        }
    }

    public void testGetConfig() throws IOException {
        assertNull(kernel.getConfig());
        final QedeqConfig config = getConfig();
        kernel.init(config, new DummyServiceModule());
        assertEquals(config, kernel.getConfig());
    }

    public void testInit() throws IOException {
        try {
            kernel.init(null, new DummyServiceModule());
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            kernel.init(getConfig(), null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.init(getConfig(), new DummyServiceModule());
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.shutdown();
        kernel.init(getConfig(), new DummyServiceModule());
    }

    public void testStartup() throws IOException {
        try {
            kernel.startup();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        kernel.startup();
        kernel.shutdown();
        try {
            kernel.startup();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testShutdown() throws IOException {
        kernel.shutdown();
        kernel.shutdown();
        kernel.init(getConfig(), new DummyServiceModule());
        kernel.shutdown();
        kernel.init(getConfig(), new DummyServiceModule());
        kernel.startup();
        kernel.shutdown();
        kernel.shutdown();
    }

    public void testRemoveAllModules() throws IOException {
        try {
            kernel.removeAllModules();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.removeAllModules();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.removeAllModules();
    }
 
    public void testRemoveModule() throws IOException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.removeModule(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.removeModule(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.removeModule(address);
    }
 
    public void testClearLocalBuffer() throws IOException {
        try {
            kernel.clearLocalBuffer();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.clearLocalBuffer();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.clearLocalBuffer();
    }
 
    public void testLoadModule() throws IOException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.loadModule(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.loadModule(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.loadModule(address);
    }
 
    public void testLoadAllModulesFromQedeq() throws IOException {
        try {
            kernel.loadAllModulesFromQedeq();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.loadAllModulesFromQedeq();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.loadAllModulesFromQedeq();
    }
 
    public void testLoadRequiredModules() throws IOException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.loadRequiredModules(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.loadRequiredModules(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.loadRequiredModules(address);
    }
 
    public void testGetAllLoadedModules() throws IOException {
        try {
            kernel.getAllLoadedModules();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.getAllLoadedModules();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.getAllLoadedModules();
    }
 
    public void testGetQedeqBo() throws IOException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.getQedeqBo(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.getQedeqBo(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.getQedeqBo(address);
    }

    public void testGetModuleAddress() throws IOException {
        final URL address = new URL("http://qedeq.org/");
        try {
            kernel.getModuleAddress(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.getModuleAddress(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.getModuleAddress(address);
    }

    public void testGetModuleAddress2() throws IOException {
        final String address = "http://qedeq.org/";
        try {
            kernel.getModuleAddress(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.getModuleAddress(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.getModuleAddress(address);
    }

    public void testGetModuleAddress3() throws IOException {
        final File address = new File("qedeq.org");
        try {
            kernel.getModuleAddress(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.getModuleAddress(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.getModuleAddress(address);
    }

    public void testGetSource() throws IOException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.getSource(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.getSource(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.getSource(address);
    }

    public void testCheckWellFormedness() throws IOException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.checkWellFormedness(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.checkWellFormedness(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.checkWellFormedness(address);
    }

    public void testCheckFormallyProved() throws IOException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.checkFormallyProved(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.checkFormallyProved(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.checkFormallyProved(address);
    }

    public void testGetPlugins() throws IOException {
        try {
            kernel.getPlugins();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        kernel.getPlugins();
        kernel.startup();
        kernel.getPlugins();
    }

    public void testExecutePlugin() throws IOException, InterruptException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.executePlugin("noname", address, null);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.executePlugin("noname", address, null);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.executePlugin("noname", address, null);
    }

    public void testClearAllPluginResults() throws IOException {
        final ModuleAddress address = DefaultModuleAddress.MEMORY;
        try {
            kernel.clearAllPluginResults(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.clearAllPluginResults(address);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.clearAllPluginResults(address);
    }

    public void testGetServiceProcesses() throws IOException {
        try {
            kernel.getServiceProcesses();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.getServiceProcesses();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.getServiceProcesses();
    }

    public void testGetRunningServiceProcesses() throws IOException {
        try {
            kernel.getRunningServiceProcesses();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.getRunningServiceProcesses();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.getRunningServiceProcesses();
    }

    public void testStopAllPluginExecutions() throws IOException {
        try {
            kernel.stopAllPluginExecutions();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.init(getConfig(), new DummyServiceModule());
        try {
            kernel.stopAllPluginExecutions();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        kernel.startup();
        kernel.stopAllPluginExecutions();
    }

    private QedeqConfig getConfig() throws IOException {
        final File cf = new File(getOutdir(), "config/org.qedeq.properties");
        cf.getParentFile().mkdirs();
        cf.createNewFile();
        final QedeqConfig config = new QedeqConfig(
            cf,
            "This file is part of the project *Hilbert II* - http://www.qedeq.org",
            getOutdir());
        config.setAutoReloadLastSessionChecked(false);
        return config;
    }

}