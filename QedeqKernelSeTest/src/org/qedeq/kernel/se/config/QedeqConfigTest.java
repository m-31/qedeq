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

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.Parameters;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.common.Plugin;

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
    private Plugin plugin1;
    private Plugin plugin2;

    public QedeqConfigTest(){
        super();
    }

    public QedeqConfigTest(final String name){
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        this.plugin1 = new Plugin() {
            public String getPluginId() {
                return "org.qedeq.kernel.bo.service.logic.SimpleProofFinderPlugin";
            }

            public String getPluginActionName() {
                return "action";
            }

            public String getPluginDescription() {
                return "description";
            }};
                
        this.plugin2 = new Plugin() {
            public String getPluginId() {
                return Plugin.class.toString();
            }

            public String getPluginActionName() {
                return "action";
            }

            public String getPluginDescription() {
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
    }

    public void testGetSetHttpProxyHost() throws Exception {
        assertEquals("proxy", con1.getHttpProxyHost());
        con1.setHttpProxyHost("newProxy");
        assertEquals("newProxy", con1.getHttpProxyHost());
    }

    public void testGetSetHttpProxyPort() throws Exception {
        assertEquals("", con1.getHttpProxyPort());
        con1.setHttpProxyPort("888");
        assertEquals("888", con1.getHttpProxyPort());
    }

    public void testGetSetHttpNonProxyHosts() throws Exception {
        assertEquals("none", con1.getHttpNonProxyHosts());
        con1.setHttpNonProxyHosts("all");
        assertEquals("all", con1.getHttpNonProxyHosts());
    }

    public void testGetLogFile() throws Exception {
        assertEquals(new File(basis1.getCanonicalFile(), "search/for/me/log.txt"),
            con1.getLogFile());
    }

    public void testGetModuleHistory() throws Exception {
        final String[] history = con1.getModuleHistory();
        assertEquals(12, history.length);
        assertEquals("http://wwww.qedeq.org/0_04_05/doc/sample/qedeq_sample3.xml", history[10]);
    }

    public void testGetSetPreviouslyLoadedModules() throws Exception {
        final String[] loaded = con1.getPreviouslyLoadedModules();
        assertEquals(21, loaded.length);
        assertEquals("http://www.qedeq.org/0_04_05/sample/qedeq_error_sample_15.xml", loaded[9]);
        final String[] newLoaded = new String[] {"one", "two", "three"};
        con1.setPreviouslyLoadedModules(newLoaded);
        assertTrue(EqualsUtility.equals(newLoaded, con1.getPreviouslyLoadedModules()));
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
        assertEquals(false, con1.getKeyValue("automaticLogScroll", false));
        assertEquals("false", con1.getKeyValue("automaticLogScroll"));
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

    public void testGetPluginValues() {
        Parameters paras = con1.getPluginEntries(plugin1);
        assertEquals(20, paras.keySet().size());
        assertEquals(6, paras.getInt("conjunctionOrder"));
        assertTrue(paras.getBoolean("boolean"));
        paras = con1.getPluginEntries(plugin2);
        assertEquals(0, paras.keySet().size());
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
    }
    
}
