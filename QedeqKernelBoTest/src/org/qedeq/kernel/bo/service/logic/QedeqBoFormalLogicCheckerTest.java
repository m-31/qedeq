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
package org.qedeq.kernel.bo.service.logic;

import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;

/**
 * For testing of loading required QEDEQ modules.
 *
 * FIXME m31 20100823: integrate some more unit tests here!
 *
 * @author Michael Meyling
 */
public class QedeqBoFormalLogicCheckerTest extends QedeqBoTestCase {

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
        getServices().checkWellFormedness(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.getWellFormedState().isFailure());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(1, bo.getErrors().size());
        assertEquals(11231, bo.getErrors().get(0).getErrorCode());
    }

}
