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

import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class ModuleContextTest extends QedeqTestCase {

    private ModuleContext con1;
    private ModuleContext con2;
    private ModuleContext con3;
    private ModuleContext con4;
    private ModuleContext con5;
    private ModuleContext con6;
    private ModuleContext con10;

    public ModuleContextTest(){
        super();
    }

    public ModuleContextTest(final String name){
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        this.con1 = new ModuleContext(DefaultModuleAddress.MEMORY); 
        this.con2 = new ModuleContext(new DefaultModuleAddress(true, "bee"));
        this.con3 = new ModuleContext(DefaultModuleAddress.MEMORY, "location!location!"); 
        this.con4 = new ModuleContext(new DefaultModuleAddress(true, "bee"), "location!location!");
        this.con5 = new ModuleContext(DefaultModuleAddress.MEMORY, ""); 
        this.con6 = new ModuleContext(new DefaultModuleAddress(true, "bee"), "location!location");
        this.con10 = new ModuleContext(DefaultModuleAddress.MEMORY, "",
            new SourcePosition(1, 1),
            new SourcePosition(1, 2));
    }
 
    /**
     * Test constructor.
     */
    public void testConstructor() throws Exception {
        assertEquals(DefaultModuleAddress.MEMORY, con1.getModuleLocation());
        assertEquals("", con1.getLocationWithinModule());
        assertEquals(new DefaultModuleAddress(true, "bee"), con2.getModuleLocation());
        assertEquals("", con2.getLocationWithinModule());
        assertEquals(DefaultModuleAddress.MEMORY, con3.getModuleLocation());
        assertEquals("location!location!", con3.getLocationWithinModule());
        assertEquals(new DefaultModuleAddress(true, "bee"), con4.getModuleLocation());
        assertEquals("location!location!", con4.getLocationWithinModule());
        assertEquals(con1, new ModuleContext(con1));
        assertEquals(con6, new ModuleContext(con6));
        ModuleContext con51 = new ModuleContext(con5);
        assertEquals(con51, con5);
        con5.setLocationWithinModule("another location");
        assertFalse(con51.equals(con5));
        try {
            new ModuleContext((ModuleAddress) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new ModuleContext((ModuleContext) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new ModuleContext((ModuleContext) null, null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new ModuleContext((ModuleAddress) null, null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new ModuleContext(this.con1, null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new ModuleContext(this.con1.getModuleLocation(), null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }

    /**
     * Test constructor.
     */
    public void testGetDelta() throws Exception {
        assertNull(con1.getStartDelta());
        assertNull(con6.getStartDelta());
        assertNull(con1.getEndDelta());
        assertNull(con5.getEndDelta());
        assertEquals(new SourcePosition(1, 1), con10.getStartDelta());
        assertEquals(new SourcePosition(1, 2), con10.getEndDelta());
    }
    
    /**
     * Test hash code generation.
     */
    public void testHashCode() throws Exception {
        assertFalse(con1.hashCode() == con2.hashCode());
        assertFalse(con1.hashCode() == con3.hashCode());
        assertFalse(con1.hashCode() == con4.hashCode());
        assertEquals(con1.hashCode(), con5.hashCode());
        assertFalse(con1.hashCode() == con6.hashCode());
        assertFalse(con1.hashCode() == con10.hashCode());
        assertFalse(con2.hashCode() == con3.hashCode());
        assertFalse(con2.hashCode() == con4.hashCode());
        assertFalse(con2.hashCode() == con5.hashCode());
        assertFalse(con2.hashCode() == con6.hashCode());
        assertFalse(con2.hashCode() == con10.hashCode());
        assertFalse(con3.hashCode() == con4.hashCode());
        assertFalse(con3.hashCode() == con5.hashCode());
        assertFalse(con3.hashCode() == con6.hashCode());
        assertFalse(con3.hashCode() == con10.hashCode());
        assertFalse(con4.hashCode() == con5.hashCode());
        assertFalse(con4.hashCode() == con6.hashCode());
        assertFalse(con4.hashCode() == con10.hashCode());
        assertFalse(con5.hashCode() == con6.hashCode());
        assertFalse(con5.hashCode() == con10.hashCode());
        assertFalse(con6.hashCode() == con10.hashCode());
    }

    public void testEqualsObject() throws Exception {
        assertEquals(con1, con1);
        assertEquals(con2, con2);
        assertEquals(con3, con3);
        assertEquals(con4, con4);
        assertEquals(con5, con5);
        assertEquals(con6, con6);
        assertEquals(con10, con10);
        assertFalse(con1.equals(con2));
        assertFalse(con1.equals(con3));
        assertFalse(con1.equals(con4));
        assertEquals(con1, con5);
        assertFalse(con1.equals(con6));
        assertFalse(con2.equals(con3));
        assertFalse(con2.equals(con4));
        assertFalse(con2.equals(con5));
        assertFalse(con2.equals(con6));
        assertFalse(con3.equals(con4));
        assertFalse(con3.equals(con5));
        assertFalse(con3.equals(con6));
        assertFalse(con4.equals(con5));
        assertFalse(con4.equals(con6));
        assertFalse(con5.equals(con6));
    }


}
