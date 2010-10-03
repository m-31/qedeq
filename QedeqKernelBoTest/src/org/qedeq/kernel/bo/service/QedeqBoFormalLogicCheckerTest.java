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
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.common.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleAddress;

/**
 * For testing of loading required QEDEQ modules.
 *
 * FIXME m31 20100823: integrate some unit tests here!
 *
 * @author Michael Meyling
 */
public class QedeqBoFormalLogicCheckerTest extends QedeqTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        KernelFacade.startup();
    }

    protected void tearDown() throws Exception {
        KernelFacade.shutdown();
        super.tearDown();
    }

    public QedeqBoFormalLogicCheckerTest() {
        super();
    }

    public QedeqBoFormalLogicCheckerTest(final String name) {
        super(name);
    }

    /**
     * Check module that imports a module with logical errors.
     *
     * @throws Exception
     */
    public void testCheckModule() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("qedeq_error_sample_05.xml"));
        KernelContext.getInstance().checkModule(address);
        final QedeqBo bo = KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.getLogicalState().isFailure());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(1, bo.getErrors().size());
        assertEquals(11231, bo.getErrors().get(0).getErrorCode());
    }

}
