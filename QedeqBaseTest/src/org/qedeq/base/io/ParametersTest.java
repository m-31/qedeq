/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.base.io;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link Parameters}.
 *
 * @author    Michael Meyling
 */
public class ParametersTest extends QedeqTestCase {

    /** Test object 1. */
    private Parameters object1;

    /** Test object 2. */
    private Parameters object2;

    /** Test object 3. */
    private Parameters object3;

    /** Test object 4. */
    private Parameters object4;

    /** Test object 5. */
    private Parameters object5;

    /** Test object 6. */
    private Parameters object6;

    protected void setUp() throws Exception {
        super.setUp();
        object1 = new Parameters(new HashMap());
        final Map map2 = new TreeMap();
        object2 = new Parameters(map2);
        map2.put("hello", "7");
        map2.put("holly", new RuntimeException("I am no string"));
        final Map map3 = new HashMap();
        map3.put("hint", "12345678901234567890");
        map3.put("hilt", "" + Integer.MAX_VALUE);
        object3 = new Parameters(map3);
        final Map map4 = new TreeMap();
        map4.put("hum", "true");
        map4.put("hom", "false");
        object4 = new Parameters(map4);
        final Map map5 = new HashMap();
        object5 = new Parameters(map5);
        map5.put("hum", new Integer(9));
        map5.put("hom", "  12  ");
        map5.put("him", "-13");
        object6 = new Parameters(new HashMap());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConstructor() {
        try {
            new Parameters(null);
            fail("Exception expected");
        } catch (RuntimeException e) {
            // OK
        }
        final Parameters p1 = new Parameters();
        assertTrue(p1.keySet().isEmpty());
        final Parameters p2 = new Parameters(new HashMap());
        assertTrue(p2.keySet().isEmpty());
        final Map m3 = new HashMap();
        final Parameters p3 = new Parameters(m3);
        assertTrue(p3.keySet().isEmpty());
        m3.put("xmass", "december");
        assertEquals("december", p3.getString("xmass"));
        m3.put("number", new Integer(7));
        assertEquals("", p3.getString("number"));
        assertEquals("", p3.getString("unknown"));
        assertEquals(0, p3.getInt("number"));
        assertEquals(0, p3.getInt("unknown"));
    }

    public void testGetBoolean() {
        assertEquals(false, object1.getBoolean("hello"));
        assertEquals(false, object2.getBoolean("hello"));
        assertEquals(false, object2.getBoolean("holly"));
        assertEquals(false, object3.getBoolean("hint"));
        assertEquals(true, object4.getBoolean("hum"));
        assertEquals(false, object4.getBoolean("hom"));
        assertEquals(false, object5.getBoolean("hom"));
        assertEquals(false, object6.getBoolean("hom"));
    }

    public void testGetBooleanStandard() {
        assertEquals(true, object1.getBoolean("hello", true));
        assertEquals(true, object4.getBoolean("hum", false));
        assertEquals(false, object4.getBoolean("hom", true));
        assertEquals(false, object3.getBoolean("hint", false));
        assertEquals(true, object5.getBoolean("hum", true));
        assertEquals(false, object6.getBoolean("hom", false));
    }

    public void testGetInt() {
        assertEquals(0, object1.getInt("hello"));
        assertEquals(7, object2.getInt("hello"));
        assertEquals(0, object5.getInt("hum"));
        assertEquals(12, object5.getInt("hom"));
        assertEquals(0, object3.getInt("hint"));
        assertEquals(2147483647, object3.getInt("hilt"));
        assertEquals(-13, object5.getInt("him"));
        assertEquals(12, object5.getInt("hom"));
        assertEquals(0, object6.getInt("hom"));
    }

    public void testGetIntStandard() {
        assertEquals(9, object1.getInt("hello", 9));
        assertEquals(7, object2.getInt("hello"));
        assertEquals(12, object5.getInt("hom", 0));
        assertEquals(11, object6.getInt("hom", 11));
    }

    public void testGetString() {
        assertEquals("", object1.getString("hello"));
        assertEquals("", object2.getString("holly"));
        assertEquals("", object5.getString("hum"));
    }

    public void testGetStringStandard() {
        assertEquals("hum", object1.getString("hello", "hum"));
        assertEquals("hom", object2.getString("holly", "hom"));
        assertEquals("hunter", object5.getString("hum", "hunter"));
    }

    public void testGetParameterString() {
        final Parameters p1 = new Parameters();
        assertEquals("", p1.getParameterString());
        p1.setDefault("one", 1);
        assertEquals("one=1", p1.getParameterString());
        p1.setDefault("one", 2);
        assertEquals("one=1", p1.getParameterString());
        p1.setDefault("b", false);
        assertTrue("one=1, b=false".equals(p1.getParameterString()) ||
            "b=false, one=1".equals(p1.getParameterString()));
        final Parameters p2 = new Parameters();
        assertEquals("", p2.getParameterString());
        p2.setDefault("one", "first");
        assertEquals("one=first", p2.getParameterString());
    }


}
