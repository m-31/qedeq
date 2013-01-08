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

package org.qedeq.kernel.bo.common;

import org.qedeq.kernel.bo.test.QedeqBoTestCase;

/**
 * Test class.
 *
 * @author  Michael Meyling
 */
public class BasicKernelTest extends QedeqBoTestCase {

    private BasicKernel kernel;

    /**
     * Constructor.
     *
     */
    public BasicKernelTest() {
        super();
    }

    /**
     * Constructor.
     *
     * @param   name    Test case name.
     *
     */
    public BasicKernelTest(final String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        kernel = new BasicKernel();
    }

    public void testGetBuildId() {
        assertNotNull(kernel.getBuildId());
    }

    public void testGetKernelVersion() {
        assertNotNull(kernel.getKernelVersion());
    }

    public void testGetDedicationVersion() {
        assertNotNull(kernel.getDedication());
    }

    public void testGetDescriptiveKernelVersion() {
        assertNotNull(kernel.getDescriptiveKernelVersion());
    }

    public void testGetKernelCodeName() {
        assertNotNull(kernel.getKernelCodeName());
    }

    public void testGetKernelVersionDirectory() {
        assertNotNull(kernel.getKernelVersionDirectory());
    }

    public void testIsRuleVersionSupported() {
        assertTrue(kernel.isRuleVersionSupported("1.01.00"));
        assertTrue(kernel.isRuleVersionSupported(kernel.getMaximalRuleVersion()));
        assertFalse(kernel.isRuleVersionSupported("1.00.00"));
    }

    public void testGetMaximalRuleVersion() {
        assertEquals("1.01.00", kernel.getMaximalRuleVersion());
    }

    // TODO 20130108 m31: do we really never want to test with java 1.4 any more?
    public void testIsSetConnectionTimeOutSupported() {
        assertTrue(kernel.isSetConnectionTimeOutSupported());
    }

    // TODO 20130108 m31: do we really never want to test with java 1.4 any more?
    public void testIsSetReadTimeoutSupported() {
        assertTrue(kernel.isSetReadTimeoutSupported());
    }


}