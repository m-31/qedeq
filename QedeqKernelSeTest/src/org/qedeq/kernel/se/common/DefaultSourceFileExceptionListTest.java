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

package org.qedeq.kernel.se.common;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class DefaultSourceFileExceptionListTest extends QedeqTestCase {

    private DefaultSourceFileExceptionList empty;

    private DefaultSourceFileExceptionList one;

    private DefaultSourceFileExceptionList two;

    private DefaultSourceFileExceptionList three;

    private final Plugin plugin = new Plugin(){
        public String getPluginDescription() {
            return "i am doing nothing";
        }

        public String getPluginId() {
            return this.getClass().toString();
        }

        public String getPluginActionName() {
            return "dummy";
        }
    };

    public DefaultSourceFileExceptionListTest(){
        super();
    }

    public DefaultSourceFileExceptionListTest(final String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        empty = new DefaultSourceFileExceptionList();
        one = new DefaultSourceFileExceptionList(new SourceFileException(plugin, 4711,
            "no big problem", new RuntimeException("something bad"), (SourceArea) null, (SourceArea) null));
        two = new DefaultSourceFileExceptionList(one);
        two.add(new SourceFileException(plugin, 815,
                "no big problem", new RuntimeException("something else"), (SourceArea) null, (SourceArea) null));
        three = new DefaultSourceFileExceptionList();
        three.add(two);
        three.add(new SourceFileException(plugin, 17234,
                "no big problem", new RuntimeException("something other"), (SourceArea) null, (SourceArea) null));
    }

    protected void tearDown() throws Exception {
        empty = null;
        one = null;
        two = null;
        three = null;
        super.tearDown();
    }

    /**
     * Test size.
     */
    public void testSize() throws Exception {
        assertEquals(0, empty.size());
        assertEquals(1, one.size());
        assertEquals(2, two.size());
        assertEquals(3, three.size());
    }

    /**
     * Test get.
     */
    public void testGet() throws Exception {
        try {
            empty.get(0);
            fail("wrong index exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            three.get(3);
            fail("wrong index exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            two.get(-1);
            fail("wrong index exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(4711, one.get(0).getErrorCode());
        assertEquals(4711, two.get(0).getErrorCode());
        assertEquals(815, two.get(1).getErrorCode());
        assertEquals(4711, three.get(0).getErrorCode());
        assertEquals(815, three.get(1).getErrorCode());
        assertEquals(17234, three.get(2).getErrorCode());
    }

    /**
     * Test getCause.
     */
    public void testGetCause() throws Exception {
        assertTrue(one.getCause() instanceof SourceFileException);
        SourceFileException e = (SourceFileException) one.getCause();
        assertEquals(4711, e.getErrorCode());
        e = (SourceFileException) two.getCause();
        assertEquals(4711, e.getErrorCode());
        e = (SourceFileException) three.getCause();
        assertEquals(4711, e.getErrorCode());
    }

    /**
     * Test getCause.
     */
    public void testToString() throws Exception {
        assertEquals(three.toString(), "org.qedeq.kernel.se.common.DefaultSourceFileExceptionList\n"
            + "0: \t4711: no big problem; something bad\n"
            + "1: \t815: no big problem; something else\n"
            + "2: \t17234: no big problem; something other");
    }

}
