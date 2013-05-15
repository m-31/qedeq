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
public class SourceFileExceptionListTest extends QedeqTestCase {

    private SourceFileExceptionList empty;

    private SourceFileExceptionList one;

    private SourceFileExceptionList two;

    private SourceFileExceptionList three;

    private SourceFileExceptionList four;

    private final Plugin plugin = new Plugin(){
        public String getServiceDescription() {
            return "i am doing nothing";
        }

        public String getServiceId() {
            return this.getClass().toString();
        }

        public String getServiceAction() {
            return "dummy";
        }
    };

    public SourceFileExceptionListTest(){
        super();
    }

    public SourceFileExceptionListTest(final String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        empty = new SourceFileExceptionList();
        final SourceFileException oneEx = new SourceFileException(plugin, 4711,
                "no big problem", new RuntimeException("something bad"), (SourceArea) null, (SourceArea) null);
        one = new SourceFileExceptionList(oneEx);
        one.add(oneEx);
        two = new SourceFileExceptionList(one);
        two.add(new SourceFileException(plugin, 815,
                "no big problem", new RuntimeException("something else"), (SourceArea) null, (SourceArea) null));
        three = new SourceFileExceptionList();
        three.add(two);
        three.add(two);
        three.add(new SourceFileException(plugin, 17234,
                "no big problem", new RuntimeException("something other"), (SourceArea) null, (SourceArea) null));
        four = new SourceFileExceptionList(two);
        four.add(new SourceFileException(plugin, 815,
                "big problem", new RuntimeException("something else"), (SourceArea) null, (SourceArea) null));
    }

    protected void tearDown() throws Exception {
        empty = null;
        one = null;
        two = null;
        three = null;
        four = null;
        super.tearDown();
    }

    public void testContructor() {
        assertEquals(0, empty.size());
        SourceFileExceptionList sf1 = new SourceFileExceptionList((SourceFileException) null);
        assertEquals(0, sf1.size());
        sf1.clear();
        assertEquals(0, sf1.size());
        SourceFileExceptionList sf2 = new SourceFileExceptionList((SourceFileExceptionList) null);
        assertEquals(0, sf1.size());
        sf2.clear();
        assertEquals(0, sf2.size());
    }
    

    public void testClear() {
        assertEquals(1, one.size());
        one.clear();
        assertEquals(0, one.size());
        assertEquals(3, three.size());
        three.clear();
        assertEquals(0, three.size());
    }

    public void testAdd1() {
        assertEquals(1, one.size());
        one.add((SourceFileException) null);
        assertEquals(1, one.size());
        one.add(new SourceFileException(plugin, 4711,
                "no big problem", new RuntimeException("something bad"), (SourceArea) null, (SourceArea) null));
        assertEquals(1, one.size());
        one.add(new SourceFileException(plugin, 4711,
                "no big problem", new RuntimeException("something bad2"), (SourceArea) null, (SourceArea) null));
        assertEquals(2, one.size());
    }

    public void testAdd2() {
        assertEquals(1, one.size());
        one.add((SourceFileExceptionList) null);
        assertEquals(1, one.size());
        one.add(one);
        assertEquals(1, one.size());
        one.add(two);
        assertEquals(2, one.size());
        three.add(new SourceFileException(plugin, 4711,
                "no big problem", new RuntimeException("something bad2"), (SourceArea) null, (SourceArea) null));
        one.add(three);
        assertEquals(4, one.size());
    }

    /**
     * Test size.
     */
    public void testSize() {
        assertEquals(0, empty.size());
        assertEquals(1, one.size());
        assertEquals(2, two.size());
        assertEquals(3, three.size());
        assertEquals(3, four.size());
    }

    /**
     * Test equals.
     */
    public void testEquals() {
        assertEquals(empty, empty);
        assertEquals(one, one);
        assertEquals(two, two);
        assertEquals(three, three);
        assertFalse(empty.equals(null));
        assertFalse(empty.equals(empty.toString()));
        assertFalse(empty.equals(one));
        assertFalse(empty.equals(two));
        assertFalse(empty.equals(three));
        assertFalse(one.equals(null));
        assertFalse(one.equals(one.toString()));
        assertFalse(one.equals(empty));
        assertFalse(one.equals(two));
        assertFalse(one.equals(three));
        assertFalse(two.equals(null));
        assertFalse(two.equals(two.toString()));
        assertFalse(two.equals(one));
        assertFalse(two.equals(empty));
        assertFalse(two.equals(three));
        assertFalse(three.equals(null));
        assertFalse(three.equals(three.toString()));
        assertFalse(three.equals(one));
        assertFalse(three.equals(empty));
        assertFalse(three.equals(one));
        assertFalse(three.equals(four));
        assertFalse(four.equals(three));
    }

    public void testHashCode() {
        assertFalse(empty.hashCode() == one.hashCode());
        assertFalse(one.hashCode() == two.hashCode());
        assertFalse(two.hashCode() == three.hashCode());
    }

    /**
     * Test get.
     */
    public void testGet() {
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
        assertEquals(17234, three.toArray()[2].getErrorCode());
    }

    /**
     * Test getCause.
     */
    public void testGetCause() {
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
    public void testToString() {
        assertEquals(three.toString(), "org.qedeq.kernel.se.common.SourceFileExceptionList\n"
            + "0: \t4711: no big problem; something bad\n"
            + "1: \t815: no big problem; something else\n"
            + "2: \t17234: no big problem; something other");
    }

}
