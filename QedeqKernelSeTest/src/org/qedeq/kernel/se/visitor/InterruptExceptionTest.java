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

package org.qedeq.kernel.se.visitor;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class InterruptExceptionTest extends QedeqTestCase {

    private InterruptException ex1;

    private InterruptException ex2;

    private InterruptException ex3;

    public void setUp() throws Exception {
        super.setUp();
        this.ex1 = new InterruptException(new ModuleContext(
            new DefaultModuleAddress()));
        this.ex2 = new InterruptException(new ModuleContext(
            new DefaultModuleAddress()));
        this.ex3 = new InterruptException(new ModuleContext(
            new DefaultModuleAddress(true, "another")));
    }
 
    /**
     * Test constructor.
     */
    public void testConstructor() throws Exception {
        assertEquals(87001, ex1.getErrorCode());
        assertEquals("Process execution was canceled", ex1.getMessage());
        assertEquals(new ModuleContext(new DefaultModuleAddress()), ex1.getContext());
    }

    /**
     * Test hash code generation.
     */
    public void testHashCode() throws Exception {
        assertTrue(ex1.hashCode() == ex2.hashCode());
        assertFalse(ex1.hashCode() == ex3.hashCode());
    }

    public void testEqualsObject() throws Exception {
        assertEquals(ex1, ex2);
        assertFalse(ex1.equals(ex3));
        assertFalse(ex3.equals(ex2));
    }

}
