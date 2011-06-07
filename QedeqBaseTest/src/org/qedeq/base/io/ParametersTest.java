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
import java.util.TreeMap;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link Parameters}.
 * FIXME add more tests
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
        final TreeMap map2 = new TreeMap();
        object2 = new Parameters(map2);
        map2.put("hello", "7");
        object3 = new Parameters(new HashMap());
        object4 = new Parameters(map2);
        object5 = new Parameters(new HashMap());
        object5 = new Parameters(null);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetBoolean() {
        assertEquals(false, object1.getBoolean("hello"));
        assertEquals(false, object2.getBoolean("hello"));
    }

    public void testGetInt() {
        assertEquals(0, object1.getInt("hello"));
        assertEquals(7, object2.getInt("hello"));
    }

    public void testGetIntStandard() {
        assertEquals(9, object1.getInt("hello", 9));
        assertEquals(7, object2.getInt("hello"));
    }

    public void testGetBooleanStandard() {
        assertEquals(true, object1.getBoolean("hello", true));
    }

    public void testGetString() {
        assertEquals("", object1.getString("hello"));
    }

    public void testGetStringStandard() {
        assertEquals("hum", object1.getString("hello", "hum"));
    }


}
