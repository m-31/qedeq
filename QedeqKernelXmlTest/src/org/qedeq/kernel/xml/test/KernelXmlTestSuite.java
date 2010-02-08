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

package org.qedeq.kernel.xml.test;

import junit.framework.Test;

import org.qedeq.base.test.QedeqTestSuite;
import org.qedeq.kernel.xml.common.XmlSyntaxExceptionTest;
import org.qedeq.kernel.xml.dao.GenerateXmlTest;
import org.qedeq.kernel.xml.parser.QedeqParserTest;
import org.qedeq.kernel.xml.schema.SchemaTest;
import org.qedeq.kernel.xml.tracker.KernelXmlTrackerTestSuite;

/**
 * Run all tests for the project.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class KernelXmlTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelTestSuite</code>.
     *
     * @return Test.
     */
    public static Test suite() {
        return new KernelXmlTestSuite();
    }

    /**
     * Constructor.
     */
    protected KernelXmlTestSuite() {
        this(true, false);
    }

    /**
     * Constructor.
     *
     * @param   withTest    Execute test methods.
     * @param   withPest    Execute pest methods.
     */
    public KernelXmlTestSuite(final boolean withTest, final boolean withPest) {
        super(withTest, withPest);
        addTestSuite(GenerateXmlTest.class);
        addTestSuite(QedeqParserTest.class);
        addTestSuite(SchemaTest.class);
        addTestSuite(XmlSyntaxExceptionTest.class);
        addTest(KernelXmlTrackerTestSuite.suite());
    }

}
