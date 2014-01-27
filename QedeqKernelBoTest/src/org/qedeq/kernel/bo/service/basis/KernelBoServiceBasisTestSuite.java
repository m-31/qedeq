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

package org.qedeq.kernel.bo.service.basis;

import junit.framework.Test;

import org.qedeq.base.test.QedeqTestSuite;
import org.qedeq.kernel.bo.service.internal.Element2LatexImplTest;
import org.qedeq.kernel.bo.service.logic.QedeqBoFormalLogicCheckerDirectTest;
import org.qedeq.kernel.bo.service.logic.QedeqBoFormalLogicCheckerTest;

/**
 * Run all JUnit tests for package org.qedeq.kernel.bo.module.
 *
 * @author  Michael Meyling
 */
public class KernelBoServiceBasisTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelVoModuleTestSuite</code>.
     *
     * @return  Test.
     */
    public static Test suite() {
        return new KernelBoServiceBasisTestSuite();
    }

    /**
     * Constructor.
     */
    public KernelBoServiceBasisTestSuite() {
        super();
        addTestSuite(CheckRequiredModuleExceptionTest.class);
        addTestSuite(DefaultKernelQedeqBoTest.class);
        addTestSuite(Element2LatexImplTest.class);
        addTestSuite(LoadRequiredModulesTest.class);
        addTestSuite(ModuleConstantsExistenceCheckerTest.class);
        addTestSuite(QedeqBoFormalLogicCheckerTest.class);
        addTestSuite(QedeqBoFormalLogicCheckerDirectTest.class);
        // very slow:
        addTestSuite(QedeqBoFactoryTest.class);
    }
}
