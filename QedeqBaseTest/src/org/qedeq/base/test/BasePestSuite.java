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

package org.qedeq.base.test;

import junit.framework.Test;


/**
 * Run all "pest" test methods for the project.
 *
 * @author Michael Meyling
 */
public class BasePestSuite extends BaseTestSuite {

    /**
     * Get a new <code>KernelTestSuiteWithPest</code>.
     *
     * @return Test.
     */
    public static Test suite() {
        return new BasePestSuite();
    }

    /**
     * Constructor.
     */
    public BasePestSuite() {
        super(false, true);
    }

}
