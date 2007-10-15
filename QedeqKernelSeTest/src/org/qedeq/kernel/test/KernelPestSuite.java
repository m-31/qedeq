/* $Id: KernelPestSuite.java,v 1.3 2007/02/25 20:04:31 m31 Exp $
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

package org.qedeq.kernel.test;

import junit.framework.Test;


/**
 * Run all tests inclusive "pest" methods for the project.
 * 
 * @version $Revision: 1.3 $
 * @author Michael Meyling
 */
public class KernelPestSuite extends KernelTestSuite {

    /**
     * Get a new <code>KernelTestSuiteWithPest</code>.
     * 
     * @return Test.
     */
    public static Test suite() {
        return new KernelPestSuite();
    }

    /**
     * Constructor.
     */
    public KernelPestSuite() {
        super(false, true);
    }

}
