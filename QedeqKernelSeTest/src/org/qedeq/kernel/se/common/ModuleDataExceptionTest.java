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

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class ModuleDataExceptionTest extends QedeqTestCase {

    private ModuleDataException ex1;
    private ModuleDataException ex2;
    private ModuleDataException ex3;
    private ModuleDataException ex4;
    private ModuleDataException ex5;
    private ModuleDataException ex6;
    private ModuleDataException ex7;
    private ModuleDataException ex8;

    public ModuleDataExceptionTest(){
        super();
    }

    public ModuleDataExceptionTest(final String name){
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        this.ex1 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress()), new ModuleContext(new DefaultModuleAddress(true, "bee")),
            new RuntimeException("I am the reason.")) {};
        this.ex2 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress()),
            new RuntimeException("I am the reason.")) {};
        this.ex3 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress())) {};
        this.ex4 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress()), new ModuleContext(new DefaultModuleAddress(true, "bee")),
            new RuntimeException("I am the next reason.")) {};
        this.ex5 = new ModuleDataException(104, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress())) {};
        this.ex6 = new ModuleDataException(107, "I am another bug!", new ModuleContext(
            new DefaultModuleAddress())) {};
        this.ex7 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress(true, "bee"))) {};
        this.ex8 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress(true, "bee")), new ModuleContext(
                new DefaultModuleAddress(true, "fly"))) {};
    }
 
    /**
     * Test constructor.
     */
    public void testConstructor() throws Exception {
        assertEquals(107, ex1.getErrorCode());
        assertEquals("I am a bug!", ex1.getMessage());
        assertEquals(new ModuleContext(new DefaultModuleAddress()), ex1.getContext());
        assertEquals("I am the reason.", ex1.getCause().getMessage());
        assertEquals(new ModuleContext(new DefaultModuleAddress(true, "bee")),
            ex1.getReferenceContext());
        assertEquals(107, ex2.getErrorCode());
        assertEquals("I am a bug!", ex2.getMessage());
        assertEquals(new ModuleContext(new DefaultModuleAddress()), ex2.getContext());
        assertEquals("I am the reason.", ex2.getCause().getMessage());
        assertEquals(null, ex2.getReferenceContext());
        assertEquals(107, ex3.getErrorCode());
        assertEquals("I am a bug!", ex3.getMessage());
        assertEquals(new ModuleContext(new DefaultModuleAddress()), ex3.getContext());
        assertEquals(null, ex3.getCause());
        assertEquals(null, ex3.getReferenceContext());
        assertEquals(107, ex4.getErrorCode());
        assertEquals("I am a bug!", ex4.getMessage());
        assertEquals(new ModuleContext(new DefaultModuleAddress()), ex4.getContext());
        assertEquals("I am the next reason.", ex4.getCause().getMessage());
        assertEquals(new ModuleContext(new DefaultModuleAddress(true, "bee")),
            ex4.getReferenceContext());
        assertNull(ex8.getCause());
        assertEquals(new ModuleContext(new DefaultModuleAddress(true, "fly")),
            ex8.getReferenceContext());
        assertEquals(new ModuleContext(new DefaultModuleAddress(true, "bee")),
            ex8.getContext());
        ModuleDataException e1 = new ModuleDataException(107, "I am a bug!",null, new ModuleContext(new DefaultModuleAddress(true, "bee")),
            new RuntimeException("I am the reason.")) {};
        assertNull(e1.getContext());
        ModuleDataException e2 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress()), null,
            new RuntimeException("I am the reason.")) {};
        assertNull(e2.getReferenceContext());
        ModuleDataException e3 = new ModuleDataException(107, "I am a bug!", null, 
            new ModuleContext(new DefaultModuleAddress(true, "bee"))) {};
        assertNull(e3.getContext());
        ModuleDataException e4 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress()), (ModuleContext) null) {};
        assertNull(e4.getReferenceContext());
        ModuleDataException e5 = new ModuleDataException(107, "I am a bug!", null) {};
        assertNull(e5.getContext());
        assertNull(e5.getReferenceContext());
        ModuleDataException e9 = new ModuleDataException(107, "I am a bug!", new ModuleContext(
            new DefaultModuleAddress(true, "bee")), null,
            new RuntimeException("I am the reason.")) {};
            assertNull(e9.getReferenceContext());
    }

    /**
     * Test hash code generation.
     */
    public void testHashCode() throws Exception {
        assertTrue(ex1.hashCode() == ex2.hashCode());
        assertTrue(ex1.hashCode() == ex3.hashCode());
        assertTrue(ex1.hashCode() == ex4.hashCode());
        assertTrue(ex2.hashCode() == ex3.hashCode());
        assertTrue(ex2.hashCode() == ex4.hashCode());
        assertTrue(ex3.hashCode() == ex4.hashCode());
        assertFalse(ex1.hashCode() == ex5.hashCode());
        assertFalse(ex1.hashCode() == ex6.hashCode());
        assertFalse(ex1.hashCode() == ex7.hashCode());
        assertFalse(ex5.hashCode() == ex6.hashCode());
        assertFalse(ex5.hashCode() == ex7.hashCode());
        assertFalse(ex6.hashCode() == ex7.hashCode());
    }

    public void testEqualsObject() throws Exception {
        assertEquals(ex1, ex2);
        assertEquals(ex1, ex3);
        assertEquals(ex1, ex4);
        assertEquals(ex2, ex3);
        assertEquals(ex2, ex4);
        assertEquals(ex3, ex4);
        assertFalse(ex1.equals(ex5));
        assertFalse(ex1.equals(ex6));
        assertFalse(ex1.equals(ex7));
        assertFalse(ex5.equals(ex6));
        assertFalse(ex5.equals(ex7));
        assertFalse(ex6.equals(ex7));
        assertFalse(ex1.equals(null));
        assertFalse(ex2.equals(null));
        assertFalse(ex3.equals(null));
        assertFalse(ex4.equals(null));
        assertFalse(ex5.equals(null));
        assertFalse(ex6.equals(null));
        assertFalse(ex7.equals(null));
    }


}
