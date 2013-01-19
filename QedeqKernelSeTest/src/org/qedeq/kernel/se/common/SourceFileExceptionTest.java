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
        
    }
 
    protected void tearDown() throws Exception {
        one = null;
        two = null;
        three = null;
        super.tearDown();
    }

    /**
     * Test constructor.
     */
    public void testConstructor() throws Exception {
        assertEquals(1, three.getSourceArea().getStartPosition().getRow());
        assertEquals(19, three.getReferenceArea().getEndPosition().getColumn());
    }

    /**
     * Test hash code generation.
     */
    public void testHashCode() throws Exception {
        assertFalse(one.hashCode() == two.hashCode());
        assertFalse(three.hashCode() == two.hashCode());
    }

    public void testEqualsObject() throws Exception {
        assertEquals(one, one);
        assertEquals(two, two);
        assertEquals(three, three);
        assertFalse(one.equals(two));
        assertFalse(one.equals(three));
        assertFalse(two.equals(one));
        assertFalse(two.equals(three));
        assertFalse(three.equals(one));
        assertFalse(three.equals(two));
    }


}
