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

package org.qedeq.kernel.se.config;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.Parameters;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.common.Service;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class QedeqConfigTest extends QedeqTestCase {

    private QedeqConfig con1;
    private QedeqConfig con2;
    private File file1;
    private File file2;
    private File basis1;
    private File basis2;
    private Service service1;
    private Service service2;

    public QedeqConfigTest(){
        super();
    }

    public QedeqConfigTest(final String name){
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        this.service1 = new Service() {
            public String getServiceId() {
                return "org.qedeq.kernel.bo.service.logic.SimpleProofFinderService";
            }

            public String getServiceAction() {
                return "action";
            }

            public String getServiceDescription() {
                return "description";
            }};
                
        this.service2 = new Service() {
            public String getServiceId() {
                return Service.class.toString();
            }

            public String getServiceAction() {
                return "action";
            }

            public String getServiceDescription() {
                return "description";
            }};
                
        file1 = getFile("QedeqConfig/qedeq1.properties");
        basis1 = file1.getParentFile();
        this.con1 = new QedeqConfig(file1, "QedeqConfig test 1", basis1); 
        file2 = getFile("QedeqConfig/qedeq2.properties");
        basis2 = file2.getParentFile();
        this.con2 = new QedeqConfig(file2, "QedeqConfig test 2", basis2); 
    }
 
    public void testGetBasisDirectory() throws Exception {
        assertEquals(basis1.getCanonicalFile(), con1.getBasisDirectory());
        assertEquals(basis2.getCanonicalFile(), con2.getBasisDirectory());
    }

    public void testGetSetBufferDirectory() throws Exception {
        assertEquals(new File(basis1, "buffer").getCanonicalFile(),
            con1.getBufferDirectory().getCanonicalFile());
        con1.setBufferDirectory(new File(con1.getBasisDirectory(), "newLocation"));
        assertEquals(new File(basis1, "newLocation").getCanonicalFile(),
            con1.getBufferDirectory().getCanonicalFile());
        assertEquals(new File(basis2, "buffer").getCanonicalFile(),
            con2.getBufferDirectory().getCanonicalFile());
        con2.setBufferDirectory(new File(con2.getBasisDirectory(), "newLocation"));
        assertEquals(new File(basis2, "newLocation").getCanonicalFile(),
            con2.getBufferDirectory().getCanonicalFile());
    }

    public void testGetSetGenerationDirectory() throws Exception {
        assertEquals(new File(basis1, "generated").getCanonicalFile(),
            con1.getGenerationDirectory().getCanonicalFile());
        con1.setGenerationDirectory(new File(con1.getBasisDirectory(), "newGeneration"));
        assertEquals(new File(basis1, "newGeneration").getCanonicalFile(),
            con1.getGenerationDirectory().getCanonicalFile());
        assertEquals(new File(basis2, "generated").getCanonicalFile(),
            con2.getGenerationDirectory().getCanonicalFile());
        con2.setGenerationDirectory(new File(con2.getBasisDirectory(), "newGeneration"));
        assertEquals(new File(basis2, "newGeneration").getCanonicalFile(),
            con2.getGenerationDirectory().getCanonicalFile());
    }

    public void testGetLocalModulesDirectory() throws Exception {
        assertEquals(new File(basis1, "local").getCanonicalFile(),
            con1.getLocalModulesDirectory().getCanonicalFile());
        con1.setLocalModulesDirectory(new File(con1.getBasisDirectory(), "newLocal"));
        assertEquals(new File(basis1, "newLocal").getCanonicalFile(),
            con1.getLocalModulesDirectory().getCanonicalFile());
        assertEquals(new File(basis2, "local").getCanonicalFile(),
            con2.getLocalModulesDirectory().getCanonicalFile());
        con2.setLocalModulesDirectory(new File(con2.getBasisDirectory(), "newLocal"));
        assertEquals(new File(basis2, "newLocal").getCanonicalFile(),
            con2.getLocalModulesDirectory().getCanonicalFile());
    }

    public void testGetSetConnectTimeout() throws Exception {
        assertEquals(2002, con1.getConnectionTimeout());
        con1.setConnectionTimeout(1007);
        assertEquals(1007, con1.getConnectionTimeout());
        assertEquals(2000, con2.getConnectionTimeout());
        con2.setConnectionTimeout(1007);
        assertEquals(1007, con2.getConnectionTimeout());
    }

    public void testGetSetReadConnectTimeout() throws Exception {
        assertEquals(1001, con1.getReadTimeout());
        con1.setReadTimeout(2801);
        assertEquals(2801, con1.getReadTimeout());
        assertEquals(1000, con2.getReadTimeout());
        con2.setReadTimeout(1009);
        assertEquals(1009, con2.getReadTimeout());
    }

    public void testGetSetHttpProxyHost() throws Exception {
        final String key = "http.proxyHost";
        final String originalValue = System.getProperty(key);
        if (originalValue != null) {
            System.getProperties().remove(key);
        }

        assertEquals("proxy", con1.getHttpProxyHost());
        con1.setHttpProxyHost("newProxy");
        assertEquals("newProxy", con1.getHttpProxyHost());
        assertNull(con2.getHttpProxyHost());

        System.setProperty(key, "systemProxy");
        assertEquals("systemProxy", con2.getHttpProxyHost());
        con2.setHttpProxyHost("newProxys");
        assertEquals("newProxys", con2.getHttpProxyHost());

        assertEquals("newProxys", con2.getHttpProxyHost());
        con2.setHttpProxyHost("newProxys2");
        assertEquals("newProxys2", con2.getHttpProxyHost());

        if (originalValue != null) {
            System.setProperty(key, originalValue);
        } else {
            System.getProperties().remove(key);
        }
    }

    public void testGetSetHttpProxyPort() throws Exception {
        final String key = "http.proxyPort";
        final String originalValue = System.getProperty(key);
        if (originalValue != null) {
            System.getProperties().remove(key);
        }

        assertEquals("", con1.getHttpProxyPort());
        con1.setHttpProxyPort("888");
        assertEquals("888", con1.getHttpProxyPort());

        assertNull(con2.getHttpProxyPort());
        System.setProperty(key, "systemPort");
        assertEquals("systemPort", con2.getHttpProxyPort());

        con2.setHttpProxyPort("889");
        assertEquals("889", con2.getHttpProxyPort());

        if (originalValue != null) {
            System.setProperty(key, originalValue);
        } else {
            System.getProperties().remove(key);
        }

    }

    public void testGetSetHttpNonProxyHosts() throws Exception {
        final String key = "http.nonProxyHosts";
        final String originalValue = System.getProperty(key);
        if (originalValue != null) {
            System.getProperties().remove(key);
        }

        assertEquals("none", con1.getHttpNonProxyHosts());
        con1.setHttpNonProxyHosts("all");
        assertEquals("all", con1.getHttpNonProxyHosts());

        assertNull(con2.getHttpNonProxyHosts());
        System.setProperty(key, "systemNon");
        assertEquals("systemNon", con2.getHttpNonProxyHosts());

        con2.setHttpNonProxyHosts("neverland");
        assertEquals("neverland", con2.getHttpNonProxyHosts());

        if (originalValue != null) {
            System.setProperty(key, originalValue);
        } else {
            System.getProperties().remove(key);
        }

    }

    public void testGetLogFile() throws Exception {
        assertEquals(new File(basis1.getCanonicalFile(), "search/for/me/log.txt"),
            con1.getLogFile());
        assertEquals(new File(basis2.getCanonicalFile(), "log/log.txt"),
            con2.getLogFile());
    }

    public void testGetSaveModuleHistory() throws Exception {
        final String[] history = con1.getModuleHistory();
        assertEquals(12, history.length);
        assertEquals("http://wwww.qedeq.org/0_04_05/doc/sample/qedeq_sample3.xml", history[10]);
        List history0 = new Vector();
        history0.add("We dont need no thought control");
        con1.saveModuleHistory(history0);
        final String[] history02 = con1.getModuleHistory();
        assertEquals(1, history02.length);
        assertEquals("We dont need no thought control", history02[0]);
        final String[] history2 = con2.getModuleHistory();
        assertEquals(0, history2.length);
    }

    public void testGetSetPreviouslyLoadedModules() throws Exception {
        final String[] loaded = con1.getPreviouslyLoadedModules();
        assertEquals(21, loaded.length);
        assertEquals("http://www.qedeq.org/0_04_05/sample/qedeq_error_sample_15.xml", loaded[9]);
        final String[] newLoaded = new String[] {"one", "two", "three"};
        con1.setPreviouslyLoadedModules(newLoaded);
        assertTrue(EqualsUtility.equals(newLoaded, con1.getPreviouslyLoadedModules()));
        final String[] loaded2 = con2.getPreviouslyLoadedModules();
        assertEquals(0, loaded2.length);
    }

    public void testGetSetKeyValueString() throws Exception {
        assertEquals("true", con1.getKeyValue("automaticLogScroll"));
        assertEquals("true", con1.getKeyValue("automaticLogScroll", "false"));
        con1.setKeyValue("automaticLogScroll", "false");
        assertEquals("false", con1.getKeyValue("automaticLogScroll"));
    }

    public void testGetKeyValueStringBoolean() throws Exception {
        assertEquals(true, con1.getKeyValue("automaticLogScroll", false));
        con1.setKeyValue("automaticLogScroll", false);
        assertEquals(false, con1.getKeyValue("automaticLogScroll", true));
        assertEquals("false", con1.getKeyValue("automaticLogScroll"));
        con1.setKeyValue("automaticLogScroll", true);
        assertEquals(true, con1.getKeyValue("automaticLogScroll", false));
        assertEquals(false, con2.getKeyValue("automaticLogScroll2nowhere", false));
        assertEquals(true, con2.getKeyValue("automaticLogScroll3nowhere", true));
    }

    public void testGetKeyValueStringString() throws Exception {
        assertEquals("true", con1.getKeyValue("automaticLogScroll", "false"));
    }

    public void testGetKeyValueStringInt() throws Exception {
        try {
            assertEquals(15, con1.getKeyValue("automaticLogScroll", 15));
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals("2002", con1.getKeyValue("connectionTimeout"));
        assertEquals(2002, con1.getKeyValue("connectionTimeout", 1003));
        assertEquals(1003, con1.getKeyValue("connectionTimeoutFee", 1003));
    }

    public void testGetSetServiceValues() {
        Parameters paras = con1.getServiceEntries(service1);
        assertEquals(20, paras.keySet().size());
        assertEquals(6, paras.getInt("conjunctionOrder"));
        assertTrue(paras.getBoolean("boolean"));
        paras = con1.getServiceEntries(service2);
        assertEquals(0, paras.keySet().size());
        paras = con2.getServiceEntries(service2);
        assertEquals(0, paras.keySet().size());
        paras = new Parameters();
        paras.setDefault("mine", "value");
        paras.setDefault("apollo", "11");
        paras.setDefault("cheese", "true");
        con2.setServiceKeyValues(service1, paras);
        assertEquals("value", con2.getServiceKeyValue(service1, "mine", ""));
        assertEquals(11, con2.getServiceKeyValue(service1, "apollo", 13));
        assertEquals(true, con2.getServiceKeyValue(service1, "cheese", false));
        Parameters paras2 = con2.getServiceEntries(service1);
        assertEquals(3, paras2.keySet().size());
        assertEquals("value", paras2.getString("mine"));
        assertEquals("11", paras2.getString("apollo"));
        assertEquals("true", paras2.getString("cheese"));
        con1.setServiceKeyValue(service2, "mine", "value");
        con1.setServiceKeyValue(service2, "apollo", 11);
        con1.setServiceKeyValue(service2, "cheese", true);
        Parameters paras3 = con1.getServiceEntries(service2);
        assertEquals(3, paras3.keySet().size());
        assertEquals("value", paras3.getString("mine"));
        assertEquals("11", paras3.getString("apollo"));
        assertEquals("true", paras3.getString("cheese"));
    }

    public void testCreateAbsolutePath() throws Exception {
        new File(System.getProperty("file.separator"), "top").getCanonicalFile().equals(
            con1.createAbsolutePath(System.getProperty("file.separator") + "top"));
    }

    public void testIsSetIsTraceOn() {
        assertTrue(con1.isTraceOn());
        con1.setTraceOn(false);
        assertFalse(con1.isTraceOn());
        assertFalse(con2.isTraceOn());
        con2.setTraceOn(true);
        assertTrue(con2.isTraceOn());
    }

    public void testIsSetAutoReloadLastSessionChecked() {
        assertTrue(con1.isAutoReloadLastSessionChecked());
        con1.setAutoReloadLastSessionChecked(false);
        assertFalse(con1.isAutoReloadLastSessionChecked());
        assertTrue(con2.isAutoReloadLastSessionChecked());
        con2.setAutoReloadLastSessionChecked(true);
        assertTrue(con2.isAutoReloadLastSessionChecked());
    }

    public void testStore() throws Exception {
        final File file3 = new File(getOutdir(), "testQedeqConfig/qedeq3.properties");
        final File basis3 = file3.getParentFile();
        IoUtility.deleteDir(basis3, true);
        QedeqConfig con3 = new QedeqConfig(file3, "QedeqConfig test 3", basis3);
        assertFalse(12345 == con3.getConnectionTimeout());
        con3.setConnectionTimeout(12345);
        con3.store();
        QedeqConfig con4 = new QedeqConfig(file3, "ho", basis3);
        assertEquals(12345, con4.getConnectionTimeout());
        final File file5 = new File(getOutdir(), "testQedeqConfig/qedeq5.properties/dir");
        IoUtility.createNecessaryDirectories(file5);
        final File file6 = new File(getOutdir(), "testQedeqConfig/qedeq5.properties");
        QedeqConfig con5 = new QedeqConfig(file6, "ho", file6.getParentFile());
        try {
            con5.store();
            fail("Exception expected, file name is already a directory!");
        } catch (Exception e) {
            // ok
        }
    }
    
}
