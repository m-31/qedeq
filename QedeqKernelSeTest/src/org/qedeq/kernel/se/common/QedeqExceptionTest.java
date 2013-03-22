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

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.EqualsUtility;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class QedeqExceptionTest extends QedeqTestCase {

    private QedeqException ex1;
    private QedeqException ex2;
    private QedeqException ex4;

    public QedeqExceptionTest(){
        super();
    }

    public QedeqExceptionTest(final String name){
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        this.ex1 = new QedeqException(107, "I am a bug!",
            new RuntimeException("I am the reason.")) {};
        this.ex2 = new QedeqException(-107, "I am a bug!",
            new RuntimeException("I am the reason.")) {};
        this.ex4 = new QedeqException(107, "I am a bug!",
            new RuntimeException("I am the next reason.")) {};
    }
 
    /**
     * Test constructor.
     */
    public void testConstructor() {
        assertEquals(107, ex1.getErrorCode());
        assertEquals("I am a bug!", ex1.getMessage());
        assertEquals(-107, ex2.getErrorCode());
        assertEquals("I am a bug!", ex2.getMessage());
        assertEquals("I am the reason.", ex2.getCause().getMessage());
        assertEquals(107, ex4.getErrorCode());
        assertEquals("I am a bug!", ex4.getMessage());
    }

    /**
     * Test hash code generation.
     */
    public void testHashCode() {
        assertTrue(ex1.hashCode() != ex2.hashCode());
        assertTrue(ex1.hashCode() != ex4.hashCode());
        assertTrue(ex2.hashCode() != ex4.hashCode());
    }

    public void testEqualsObject() {
        assertFalse(EqualsUtility.equals(ex1, ex2));
        assertFalse(EqualsUtility.equals(ex2, ex4));
        assertFalse(EqualsUtility.equals(ex1, ex4));
        assertFalse(ex1.equals(null));
        assertFalse(ex2.equals(null));
        assertFalse(ex4.equals(null));
    }


    /*
     * Test toString.
     */
    public void testToString() {
        assertEquals(107, ex1.getErrorCode());
        assertEquals("I am a bug!", ex1.getMessage());
        assertEquals(-107, ex2.getErrorCode());
        assertEquals("I am a bug!", ex2.getMessage());
        assertEquals("I am the reason.", ex2.getCause().getMessage());
        assertEquals(107, ex4.getErrorCode());
        assertEquals("I am a bug!", ex4.getMessage());
    }

}
