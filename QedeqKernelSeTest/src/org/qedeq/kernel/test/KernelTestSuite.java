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

package org.qedeq.kernel.test;

import java.net.URL;

import junit.framework.Test;

import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import org.qedeq.kernel.bo.control.KernelBoControlTestSuite;
import org.qedeq.kernel.bo.logic.KernelBoLogicTestSuite;
import org.qedeq.kernel.bo.module.KernelBoModuleTestSuite;
import org.qedeq.kernel.dao.GenerateXmlTest;
import org.qedeq.kernel.dto.module.KernelDtoModuleTestSuite;
import org.qedeq.kernel.latex.GenerateLatexTest;
import org.qedeq.kernel.parser.KernelParserTestSuite;
import org.qedeq.kernel.utility.KernelUtilityTestSuite;
import org.qedeq.kernel.xml.parser.QedeqParserTest;
import org.qedeq.kernel.xml.schema.SchemaTest;
import org.qedeq.kernel.xml.tracker.KernelXmlTrackerTestSuite;

/**
 * Run all tests for the project.
 * 
 * @version $Revision: 1.21 $
 * @author Michael Meyling
 */
public class KernelTestSuite extends QedeqTestSuite {

    static {
        // init Log4J watchdog
        try {
            URL url = Loader.getResource("log4j.xml");
            // set properties and watch file every 5 seconds
            DOMConfigurator.configureAndWatch(url.getPath(), 5000);
        } catch (Exception e) {
            // we ignore this
        }
        
    }
    
    /**
     * Get a new <code>KernelTestSuite</code>.
     * 
     * @return Test.
     */
    public static Test suite() {
        return new KernelTestSuite();
    }

    /**
     * Constructor.
     */
    protected KernelTestSuite() {
        this(true, false);
    }

    /**
     * Constructor.
     *
     * @param   withTest    Execute test methods.      
     * @param   withPest    Execute pest methods.
     */
    public KernelTestSuite(final boolean withTest, final boolean withPest) {
        super(withTest, withPest);
        addTest(KernelBoControlTestSuite.suite());
        addTest(KernelBoLogicTestSuite.suite());
        addTest(KernelBoModuleTestSuite.suite());
        addTest(KernelDtoModuleTestSuite.suite());

        addTestSuite(GenerateLatexTest.class);
        addTestSuite(GenerateXmlTest.class);

        addTest(KernelParserTestSuite.suite());
        addTest(KernelUtilityTestSuite.suite());
        addTestSuite(QedeqParserTest.class);
        addTestSuite(SchemaTest.class);
        addTest(KernelXmlTrackerTestSuite.suite());
    }

}
