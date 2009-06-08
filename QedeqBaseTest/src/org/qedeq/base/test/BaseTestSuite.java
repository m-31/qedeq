/* $Id: BaseTestSuite.java,v 1.1 2008/07/26 07:56:13 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.base.test;

import junit.framework.Test;

import org.qedeq.base.io.KernelBaseIoTestSuite;
import org.qedeq.base.trace.TraceTest;

/**
 * Run all tests for the project.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class BaseTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelTestSuite</code>.
     *
     * @return Test.
     */
    public static Test suite() {
        return new BaseTestSuite();
    }

    /**
     * Constructor.
     */
    protected BaseTestSuite() {
        this(true, false);
    }

    /**
     * Constructor.
     *
     * @param   withTest    Execute test methods.
     * @param   withPest    Execute pest methods.
     */
    public BaseTestSuite(final boolean withTest, final boolean withPest) {
        super(withTest, withPest);
        addTest(KernelBaseIoTestSuite.suite());
        addTestSuite(TraceTest.class);
    }

}
