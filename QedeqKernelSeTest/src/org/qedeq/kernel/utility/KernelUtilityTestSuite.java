/* $Id: KernelUtilityTestSuite.java,v 1.6 2008/01/26 12:39:51 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.utility;

import junit.framework.Test;

import org.qedeq.kernel.test.QedeqTestSuite;

/**
 * Run all JUnit tests for package org.qedeq.kernel.utility.
 *
 * @version $Revision: 1.6 $
 * @author    Michael Meyling
 */
public class KernelUtilityTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelVoModuleTestSuite</code>.
     *
     * @return  Test.
     */
    public static Test suite() {
        return new KernelUtilityTestSuite();
    }

    /**
     * Constructor.
     */
    public KernelUtilityTestSuite() {
        super();
        addTestSuite(TextInputTest.class);
        addTestSuite(IoUtilityTest.class);
    }
}
