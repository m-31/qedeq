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

package org.qedeq.kernel.bo.parser;

import junit.framework.Test;

import org.qedeq.base.test.QedeqTestSuite;

/**
 * Run all JUnit tests for package org.qedeq.kernel.parser.
 *
 * @version $Revision: 1.1 $
 * @author    Michael Meyling
 */
public class KernelBoParserTestSuite extends QedeqTestSuite {

    /**
     * Get a new test suite.
     *
     * @return  Test.
     */
    public static Test suite() {
        return new KernelBoParserTestSuite();
    }

    /**
     * Constructor.
     */
    public KernelBoParserTestSuite() {
        super();
        addTestSuite(AsciiMathParserTest.class);
        addTestSuite(LatexMathParserTest.class);
        addTestSuite(OperatorTest.class);
        addTestSuite(SimpleMathParserTest.class);
    }
}
