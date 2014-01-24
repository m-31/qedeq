/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class SourceFileExceptionTest extends QedeqTestCase {

    private SourceFileException one;
    private SourceFileException two;
    private SourceFileException three;
    private SourceFileException four;
    private SourceFileException five;
    private SourceFileException six;
    private SourceFileException seven;

    private final Service plugin = new Service(){
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
    
    public SourceFileExceptionTest(){
        super();
    }

    public SourceFileExceptionTest(final String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        one = new SourceFileException(plugin, 4711,
            "no big problem", new RuntimeException("something bad"), (SourceArea) null, 
            (SourceArea) null);
        two = new SourceFileException(plugin, 815,
            "no big problem", new RuntimeException("something else"), new SourceArea("test"),
            new SourceArea("toast"));
        three = new SourceFileException(plugin, 17234,
            "no big problem", new RuntimeException("something other"),
            new SourceArea("test", new SourcePosition(1, 2), new SourcePosition(3, 17)), 
            new SourceArea("toast", new SourcePosition(13, 7), new SourcePosition(14, 19)));
        four = new SourceFileException(plugin, 17234,
            "no big problem", null,
            new SourceArea("test", new SourcePosition(1, 2), new SourcePosition(3, 17)), 
            new SourceArea("toast", new SourcePosition(13, 7), new SourcePosition(14, 19)));
        five = new SourceFileException(plugin, 17234,
            "one big problem", null,
            new SourceArea("test", SourcePosition.BEGIN, new SourcePosition(3, 17)),
            new SourceArea("toast", new SourcePosition(13, 7), SourcePosition.BEGIN));
        six = new SourceFileException(plugin,
                new IllegalModuleDataException(107, "I am a bug!", new ModuleContext(
                new DefaultModuleAddress()), new ModuleContext(new DefaultModuleAddress(true, "bee")),
                new RuntimeException("I am the next reason.")), null, null);
        seven = new SourceFileException(null, 17234,
                "no big problem", new RuntimeException("no big problem"),
                new SourceArea("test", new SourcePosition(1, 2), new SourcePosition(3, 17)), 
                new SourceArea("toast", new SourcePosition(13, 7), new SourcePosition(14, 19)));
    }
 
    protected void tearDown() throws Exception {
        one = null;
        two = null;
        three = null;
        four = null;
        five = null;
        six = null;
        seven = null;
        super.tearDown();
    }

    /**
     * Test constructor.
     */
    public void testConstructor() {
        assertEquals("no big problem; something bad", one.getMessage());
        assertEquals(4711, one.getErrorCode());
        assertNull(four.getCause());
        assertEquals(1, three.getSourceArea().getStartPosition().getRow());
        assertEquals(19, three.getReferenceArea().getEndPosition().getColumn());
        try {
            new SourceFileException(null, null, null, null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(107, six.getErrorCode());
    }

    public void testGetMessage() {
        assertEquals("no big problem; something else", two.getMessage());
        assertEquals("no big problem", four.getMessage());
        assertEquals("no big problem", seven.getMessage());
    }

    public void testGetCause() {
        assertEquals("something bad", one.getCause().getMessage());
    }

    public void testToString() {
        assertEquals("\t4711: no big problem; something bad", one.toString());
        assertEquals("test:1:2:3:17\n\t17234: no big problem; something other", three.toString());
    }

    public void testGetDescription() {
        assertEquals("\t4711: no big problem; something bad", one.getDescription());
        assertEquals("test:1:2:3:17\n\t17234: no big problem; something other", three.getDescription());
        assertEquals("test:1:2:3:17\n\t17234: no big problem", four.getDescription());
    }

    public void testGetPlugin() {
        assertEquals(plugin, one.getService());
        assertNull(seven.getService());
    }

    public void testGetSourceArea() {
        assertEquals(null, one.getSourceArea());
        assertEquals(new SourceArea("test"), two.getSourceArea());
    }

    public void testGetRefrenceArea() {
        assertEquals(null, one.getReferenceArea());
        assertEquals(new SourceArea("toast"), two.getReferenceArea());
    }

    /**
     * Test hash code generation.
     */
    public void testHashCode() {
        assertFalse(one.hashCode() == two.hashCode());
        assertFalse(three.hashCode() == two.hashCode());
        assertFalse(three.hashCode() == four.hashCode());
        assertFalse(five.hashCode() == four.hashCode());
        assertFalse(six.hashCode() == five.hashCode());
        assertFalse(six.hashCode() == seven.hashCode());
        assertFalse(one.hashCode() == three.hashCode());
        assertFalse(one.hashCode() == four.hashCode());
        assertFalse(one.hashCode() == five.hashCode());
        assertFalse(one.hashCode() == six.hashCode());
        assertFalse(one.hashCode() == seven.hashCode());
        assertFalse(two.hashCode() == four.hashCode());
        assertFalse(two.hashCode() == five.hashCode());
        assertFalse(two.hashCode() == six.hashCode());
        assertFalse(two.hashCode() == seven.hashCode());
        assertFalse(three.hashCode() == five.hashCode());
        assertFalse(three.hashCode() == six.hashCode());
        assertFalse(three.hashCode() == seven.hashCode());
        assertFalse(four.hashCode() == five.hashCode());
        assertFalse(four.hashCode() == six.hashCode());
        assertFalse(four.hashCode() == seven.hashCode());
        assertFalse(five.hashCode() == six.hashCode());
        assertFalse(five.hashCode() == seven.hashCode());
    }

    /**
     * Test equals method.
     */
    public void testEqualsObject() {
        assertEquals(one, one);
        assertEquals(two, two);
        assertEquals(three, three);
        assertEquals(four, four);
        assertEquals(five, five);
        assertFalse(one.equals(null));
        assertFalse(one.equals(one.toString()));
        assertFalse(one.equals(two));
        assertFalse(one.equals(three));
        assertFalse(one.equals(four));
        assertFalse(one.equals(five));
        assertFalse(two.equals(null));
        assertFalse(two.equals(two.toString()));
        assertFalse(two.equals(one));
        assertFalse(two.equals(three));
        assertFalse(two.equals(four));
        assertFalse(two.equals(five));
        assertFalse(three.equals(null));
        assertFalse(three.equals(one));
        assertFalse(three.equals(two));
        assertFalse(three.equals(four));
        assertFalse(three.equals(five));
    }


}
