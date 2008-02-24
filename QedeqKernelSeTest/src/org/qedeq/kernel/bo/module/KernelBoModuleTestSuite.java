/* $Id: KernelBoModuleTestSuite.java,v 1.7 2007/12/21 23:35:16 m31 Exp $
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

package org.qedeq.kernel.bo.module;

import junit.framework.Test;

import org.qedeq.kernel.test.QedeqTestSuite;

/**
 * Run all JUnit tests for package org.qedeq.kernel.bo.module.
 *
 * @version $Revision: 1.7 $
 * @author    Michael Meyling
 */
public class KernelBoModuleTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelVoModuleTestSuite</code>.
     *
     * @return  Test.
     */
    public static Test suite() {
        return new KernelBoModuleTestSuite();
    }

    /**
     * Constructor.
     */
    public KernelBoModuleTestSuite() {
        super();
        addTestSuite(DefaultQedeqBo2Test.class);
        addTestSuite(VisitorContextTest.class);
    }
}
