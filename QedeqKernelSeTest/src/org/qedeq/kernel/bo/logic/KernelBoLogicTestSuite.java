/* $Id: KernelBoLogicTestSuite.java,v 1.3 2007/12/21 23:35:17 m31 Exp $
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

package org.qedeq.kernel.bo.logic;

import junit.framework.Test;

import org.qedeq.kernel.test.QedeqTestSuite;

/**
 * Run all junit tests for package org.qedeq.kernel.bo.module.
 *
 * @version $Revision: 1.3 $
 * @author    Michael Meyling
 */
public class KernelBoLogicTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelVoModuleTestSuite</code>.
     *
     * @return  Test.
     */
    public static Test suite() {
        return new KernelBoLogicTestSuite();
    }

    /**
     * Constructor.
     */
    public KernelBoLogicTestSuite() {
        super();
        addTestSuite(FormulaCheckerTest.class);
        addTestSuite(FormulaCheckerGlobalTest.class);
        addTestSuite(FormulaCheckerSubjectVariableTest.class);
        addTestSuite(FormulaCheckerFunctionTermTest.class);
        addTestSuite(FormulaCheckerPredicateFormulaTest.class);
        addTestSuite(FormulaCheckerLogicalConnectivesTest.class);
        addTestSuite(FormulaCheckerNegationTest.class);
        addTestSuite(FormulaCheckerQuantifiersTest.class);
        addTestSuite(FormulaCheckerClassTermTest.class);
        addTestSuite(FormulaCheckerTermTest.class);
        addTestSuite(FormulaCheckerFormulaTest.class);
        addTestSuite(CheckLogicTest.class);
    }
}
