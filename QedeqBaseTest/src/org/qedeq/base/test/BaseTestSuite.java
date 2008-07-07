/* $Id: KernelTestSuite.java,v 1.21 2008/03/27 05:12:46 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.io.KernelUtilityTestSuite;

/**
 * Run all tests for the project.
 * 
 * @version $Revision: 1.21 $
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
        addTest(KernelUtilityTestSuite.suite());
    }

}
