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

package org.qedeq.kernel.se.visitor;

import junit.framework.Test;

import org.qedeq.base.test.QedeqTestSuite;

/**
 * Run all JUnit tests for package org.qedeq.kernel.utility.
 *
 * @author  Michael Meyling
 */
public class KernelSeVisitorTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelVoModuleTestSuite</code>.
     *
     * @return  Test.
     */
    public static Test suite() {
        return new KernelSeVisitorTestSuite();
    }

    /**
     * Constructor.
     */
    public KernelSeVisitorTestSuite() {
        super();
        addTestSuite(AbstractModuleVisitorTest.class);
        addTestSuite(DefaultContextCheckerTest.class);
        addTestSuite(InterruptExceptionTest.class);
        addTestSuite(LatexList2TextTest.class);
        addTestSuite(QedeqNotNullTraverserTest.class);
        addTestSuite(QedeqNumbersTest.class);
    }
}
