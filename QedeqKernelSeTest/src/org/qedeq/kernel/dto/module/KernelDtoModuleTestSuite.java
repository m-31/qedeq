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

package org.qedeq.kernel.dto.module;

import junit.framework.Test;

import org.qedeq.base.test.QedeqTestSuite;

/**
 * Run all junit tests for package org.qedeq.kernel.vo.module.
 *
 * @version $Revision: 1.3 $
 * @author    Michael Meyling
 */
public class KernelDtoModuleTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelVoModuleTestSuite</code>.
     *
     * @return  Test.
     */
    public static Test suite() {
        return new KernelDtoModuleTestSuite();
    }

    /**
     * Constructor.
     */
    public KernelDtoModuleTestSuite() {
        super();
        addTestSuite(AuthorListVoTest.class);
        addTestSuite(AuthorVoTest.class);
        addTestSuite(AxiomVoTest.class);
        addTestSuite(ChapterVoTest.class);
        addTestSuite(ChapterListVoTest.class);
        addTestSuite(PredicateDefinitionVoTest.class);
        addTestSuite(FunctionDefinitionVoTest.class);
        addTestSuite(FormulaVoTest.class);
        addTestSuite(HeaderVoTest.class);
        addTestSuite(ImportListVoTest.class);
        addTestSuite(ImportVoTest.class);
        addTestSuite(LatexListVoTest.class);
        addTestSuite(LatexVoTest.class);
        addTestSuite(LinkListVoTest.class);
        addTestSuite(LiteratureItemListVoTest.class);
        addTestSuite(LiteratureItemVoTest.class);
        addTestSuite(LocationListVoTest.class);
        addTestSuite(LocationVoTest.class);
        addTestSuite(NodeVoTest.class);
        addTestSuite(ProofListVoTest.class);
        addTestSuite(ProofVoTest.class);
        addTestSuite(PropositionVoTest.class);
        addTestSuite(QedeqVoTest.class);
        addTestSuite(RuleVoTest.class);
        addTestSuite(SectionListVoTest.class);
        addTestSuite(SectionVoTest.class);
        addTestSuite(SubsectionListVoTest.class);
        addTestSuite(SubsectionVoTest.class);
        addTestSuite(SpecificationVoTest.class);
        addTestSuite(TermVoTest.class);
        addTestSuite(UsedByListVoTest.class);
        addTestSuite(VariableListVoTest.class);
    }
}
