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

package org.qedeq.kernel.bo.test;

import junit.framework.Test;

import org.qedeq.base.test.QedeqTestSuite;
import org.qedeq.kernel.bo.latex.ExtendedGenerateLatexTest;
import org.qedeq.kernel.bo.latex.GenerateLatexTest;
import org.qedeq.kernel.bo.logic.model.CalculateTruthThreeModelTest;
import org.qedeq.kernel.bo.logic.model.CalculateTruthZeroModelTest;
import org.qedeq.kernel.bo.logic.model.DynamicInterpreterTest;
import org.qedeq.kernel.bo.logic.wf.KernelBoLogicTestSuite;
import org.qedeq.kernel.bo.module.KernelBoModuleTestSuite;
import org.qedeq.kernel.bo.parser.KernelBoParserTestSuite;
import org.qedeq.kernel.bo.service.KernelBoServiceTestSuite;
import org.qedeq.kernel.bo.service.heuristic.DynamicHeuristicCheckerPluginTest;
import org.qedeq.kernel.bo.service.heuristic.HeuristicCheckerPluginTest;
import org.qedeq.kernel.bo.service.latex.Qedeq2LatexTest;
import org.qedeq.kernel.bo.service.utf8.Latex2Utf8Test;

/**
 * Run all tests for the project.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class KernelBoTestSuite extends QedeqTestSuite {


    /**
     * Get a new <code>KernelTestSuite</code>.
     *
     * @return Test.
     */
    public static Test suite() {
        return new KernelBoTestSuite();
    }

    /**
     * Constructor.
     */
    protected KernelBoTestSuite() {
        this(true, false);
    }

    /**
     * Constructor.
     *
     * @param   withTest    Execute test methods.
     * @param   withPest    Execute pest methods.
     */
    public KernelBoTestSuite(final boolean withTest, final boolean withPest) {
        super(withTest, withPest);
        addTest(KernelBoLogicTestSuite.suite());
        addTest(KernelBoModuleTestSuite.suite());
        addTest(KernelBoParserTestSuite.suite());
        addTest(KernelBoServiceTestSuite.suite());
        addTestSuite(Qedeq2LatexTest.class);
        addTestSuite(GenerateLatexTest.class);
        addTestSuite(CalculateTruthThreeModelTest.class);
        addTestSuite(CalculateTruthZeroModelTest.class);
        addTestSuite(HeuristicCheckerPluginTest.class);
        addTestSuite(DynamicHeuristicCheckerPluginTest.class);
        addTestSuite(DynamicInterpreterTest.class);
        addTestSuite(Latex2Utf8Test.class);

        // very slow:
        addTestSuite(ExtendedGenerateLatexTest.class);

        // test if all classes have tests
        addTestSuite(KernelBoEachClassHasATest.class);

        // test that we don't missed adding a test to this suite
        addTestIfEveryExistingTestIsCalled("org.qedeq.kernel.bo");
    }

}
