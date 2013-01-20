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
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class ConfigAccessTest extends QedeqTestCase {

    private ConfigAccess con1;
    private ConfigAccess con2;

    public ConfigAccessTest(){
        super();
    }

    public ConfigAccessTest(final String name){
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        this.con1 = new ConfigAccess(getFile("ConfigAccess/con1.properties"), "con1 test"); 
        this.con2 = new ConfigAccess(getFile("ConfigAccess/con2.properties"), "con2 test"); 
    }
 
    public void testStore() throws Exception {
        final File dir = new File(getOutdir(), "ConfigAccess");
        IoUtility.deleteDir(dir, true);
        final File file = new File(dir, "con3.properties");
        final ConfigAccess con3 = new ConfigAccess(file, "never");
        con3.setString("testValue", "we all live in a yellow submarine");
        con3.store();
        assertEquals("we all live in a yellow submarine", con3.getString("testValue"));
        final StringBuffer buffer = new StringBuffer();
        IoUtility.loadFile(file, buffer, "ISO-8859-1");
        assertTrue(buffer.indexOf("never") > 0);
        final ConfigAccess con4 = new ConfigAccess(file, "never");
        assertEquals("we all live in a yellow submarine", con4.getString("testValue"));
    }


    public void testGetter() throws Exception {
        assertEquals(4711, con1.getInteger("testInteger"));
        assertEquals(4713, con1.getInteger("testIntegerNonExisting", 4713));
        try {
            con1.getInteger("testIntegerNonExisting2");
            fail("Exceptin expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals("more light", con1.getString("testString"));
        assertEquals("", con1.getString("testEmpty"));
        try {
            con1.getInteger("testString");
            fail("Exceptin expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals("t1", con2.getString("test.t1"));
        assertEquals("t2", con2.getString("test.t2"));
        final Map test = con2.getProperties("test.");
        assertEquals(2, test.size());
        assertNull(test.get("test.t1"));
        assertNull(test.get("test.t2"));
        assertEquals("t1", test.get("t1"));
        assertEquals("t2", test.get("t2"));
    }

}
