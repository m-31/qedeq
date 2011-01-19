/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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
package org.qedeq.kernel.bo.service;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * Testing {@link CheckRequiredModuleException}.
 *
 * @author  Michael Meyling
 */
public class CheckRequiredModuleExceptionTest extends QedeqTestCase {

    /** Test instance 1. */
    private CheckRequiredModuleException object1;

    /** Test instance 2. */
    private CheckRequiredModuleException object2;

    /** Data for test instance 2. */
    private ModuleContext context2;

    protected void setUp() throws Exception {
        super.setUp();
        object1 = new CheckRequiredModuleException(0, "my message", null);
        context2 = new ModuleContext(new DefaultModuleAddress("http://qedeq.org/0_03_12/doc/math/qedeq_logic_v1.xml"), "");
        object2 = new CheckRequiredModuleException(0, "my 2. message", context2);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test creator.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testCreator() throws Exception {
        new CheckRequiredModuleException(0, "fname", null);
        new CheckRequiredModuleException(0, "funame", null, null);
    }
    
    /**
     * Test getter.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testGetter() throws Exception {
        assertEquals(0, object1.getErrorCode());
        assertEquals("my message", object1.getMessage());
        assertEquals("my 2. message", object2.getMessage());
        assertNull(object1.getContext());
        assertEquals(context2, object2.getContext());
    }

}
