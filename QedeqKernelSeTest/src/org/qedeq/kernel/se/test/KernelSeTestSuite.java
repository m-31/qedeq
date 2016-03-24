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

package org.qedeq.kernel.se.test;

import junit.framework.Test;

import org.qedeq.base.test.QedeqTestSuite;
import org.qedeq.kernel.se.common.KernelSeCommonTestSuite;
import org.qedeq.kernel.se.config.KernelSeConfigTestSuite;
import org.qedeq.kernel.se.dto.list.KernelSeDtoListTestSuite;
import org.qedeq.kernel.se.dto.module.KernelSeDtoModuleTestSuite;
import org.qedeq.kernel.se.state.KernelSeStateTestSuite;
import org.qedeq.kernel.se.visitor.KernelSeVisitorTestSuite;

/**
 * Run all tests for the project.
 * 
 * @author Michael Meyling
 */
public class KernelSeTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelTestSuite</code>.
     * 
     * @return Test.
     */
    public static Test suite() {
        return new KernelSeTestSuite();
    }

    /**
     * Constructor.
     */
    protected KernelSeTestSuite() {
        this(true, false);
    }

    /**
     * Constructor.
     *
     * @param   withTest    Execute test methods.      
     * @param   withPest    Execute pest methods.
     */
    public KernelSeTestSuite(final boolean withTest, final boolean withPest) {
        super(withTest, withPest);
        addTest(KernelSeCommonTestSuite.suite());
        addTest(KernelSeConfigTestSuite.suite());
        addTest(KernelSeDtoListTestSuite.suite());
        addTest(KernelSeDtoModuleTestSuite.suite());
        addTest(KernelSeStateTestSuite.suite());
        addTest(KernelSeVisitorTestSuite.suite());
        // test if all classes have tests
        addTestSuite(KernelSeEachClassHasATest.class);

        // test that we don't missed adding a test to this suite
        addTestIfEveryExistingTestIsCalled("org.qedeq.kernel.se");
    }

}
