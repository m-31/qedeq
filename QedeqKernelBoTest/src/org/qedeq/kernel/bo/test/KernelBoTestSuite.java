/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.bo.KernelBoPackageTestSuite;
import org.qedeq.kernel.bo.common.KernelBoCommonTestSuite;
import org.qedeq.kernel.bo.log.KernelBoLogTestSuite;
import org.qedeq.kernel.bo.logic.KernelBoLogicTestSuite;
import org.qedeq.kernel.bo.logic.common.FormulaUtilityReplaceOperatorVariableTest;
import org.qedeq.kernel.bo.logic.common.FormulaUtilityReplaceSubjectVariableQuantifierTest;
import org.qedeq.kernel.bo.logic.model.KernelBoLogicModelTestSuite;
import org.qedeq.kernel.bo.logic.proof.checker.KernelBoLogicProofCheckerTestSuite;
import org.qedeq.kernel.bo.logic.proof.finder.KernelBoLogicProofFinderTestSuite;
import org.qedeq.kernel.bo.logic.wf.KernelBoLogicWfTestSuite;
import org.qedeq.kernel.bo.module.KernelBoModuleTestSuite;
import org.qedeq.kernel.bo.parser.KernelBoParserTestSuite;
import org.qedeq.kernel.bo.service.basis.KernelBoServiceTestSuite;
import org.qedeq.kernel.bo.service.heuristic.KernelBoServiceHeuristicTestSuite;
import org.qedeq.kernel.bo.service.internal.KernelBoServiceInternalTestSuite;
import org.qedeq.kernel.bo.service.latex.KernelBoServiceLatexTestSuite;
import org.qedeq.kernel.bo.service.logic.KernelBoServiceLogicTestSuite;
import org.qedeq.kernel.bo.service.unicode.GenerateUtf8Test;
import org.qedeq.kernel.bo.service.unicode.Latex2UnicodeParserTest;
import org.qedeq.kernel.bo.service.unicode.Qedeq2UnicodeTextExecutorTest;

/**
 * Run all tests for the project.
 *
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
        addTest(KernelBoPackageTestSuite.suite());
        addTest(KernelBoCommonTestSuite.suite());
        addTest(KernelBoLogTestSuite.suite());
        addTest(KernelBoLogicProofCheckerTestSuite.suite());
        addTest(KernelBoLogicProofFinderTestSuite.suite());
        addTest(KernelBoLogicTestSuite.suite());
        addTest(KernelBoLogicWfTestSuite.suite());
        addTest(KernelBoModuleTestSuite.suite());
        addTest(KernelBoParserTestSuite.suite());
        addTest(KernelBoServiceInternalTestSuite.suite());
        addTest(KernelBoServiceTestSuite.suite());
        addTest(KernelBoServiceLatexTestSuite.suite());
        addTest(KernelBoLogicModelTestSuite.suite());
        addTest(KernelBoServiceHeuristicTestSuite.suite());
        addTest(KernelBoServiceLogicTestSuite.suite());

        addTestSuite(Latex2UnicodeParserTest.class);
        addTestSuite(GenerateUtf8Test.class);
        addTestSuite(Qedeq2UnicodeTextExecutorTest.class);
        addTestSuite(FormulaUtilityReplaceSubjectVariableQuantifierTest.class);
        addTestSuite(FormulaUtilityReplaceOperatorVariableTest.class);

        // test if all classes have tests
        addTestSuite(KernelBoEachClassHasATest.class);

        // test that we don't missed adding a test to this suite
        addTestIfEveryExistingTestIsCalled("org.qedeq.kernel.bo");
    }

}
