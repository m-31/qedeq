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

package org.qedeq.kernel.se.dto.module;

import junit.framework.Test;

import org.qedeq.base.test.QedeqTestSuite;

/**
 * Run all JUnit tests for package org.qedeq.kernel.se.dto.module.
 *
 * @author    Michael Meyling
 */
public class KernelSeDtoModuleTestSuite extends QedeqTestSuite {

    /**
     * Get a new <code>KernelVoModuleTestSuite</code>.
     *
     * @return  Test.
     */
    public static Test suite() {
        return new KernelSeDtoModuleTestSuite();
    }

    /**
     * Constructor.
     */
    public KernelSeDtoModuleTestSuite() {
        super();
        addTestSuite(AddVoTest.class);
        addTestSuite(AuthorListVoTest.class);
        addTestSuite(AuthorVoTest.class);
        addTestSuite(AxiomVoTest.class);
        addTestSuite(ChangedRuleVoTest.class);
        addTestSuite(ChangedRuleListVoTest.class);
        addTestSuite(ChapterVoTest.class);
        addTestSuite(ChapterListVoTest.class);
        addTestSuite(ConditionalProofVoTest.class);
        addTestSuite(ConclusionVoTest.class);
        addTestSuite(HypothesisVoTest.class);
        addTestSuite(InitialPredicateDefinitionVoTest.class);
        addTestSuite(PredicateDefinitionVoTest.class);
        addTestSuite(ExistentialVoTest.class);
        addTestSuite(InitialFunctionDefinitionVoTest.class);
        addTestSuite(FunctionDefinitionVoTest.class);
        addTestSuite(FormulaVoTest.class);
        addTestSuite(FormalProofLineListVoTest.class);
        addTestSuite(FormalProofLineVoTest.class);
        addTestSuite(FormalProofListVoTest.class);
        addTestSuite(FormalProofVoTest.class);
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
        addTestSuite(ModusPonensVoTest.class);
        addTestSuite(NodeVoTest.class);
        addTestSuite(ProofListVoTest.class);
        addTestSuite(ProofVoTest.class);
        addTestSuite(PropositionVoTest.class);
        addTestSuite(QedeqVoTest.class);
        addTestSuite(RenameVoTest.class);
        addTestSuite(RuleVoTest.class);
        addTestSuite(SectionListVoTest.class);
        addTestSuite(SectionVoTest.class);
        addTestSuite(SubstFuncVoTest.class);
        addTestSuite(SubstFreeVoTest.class);
        addTestSuite(SubstPredVoTest.class);
        addTestSuite(SubsectionListVoTest.class);
        addTestSuite(SubsectionVoTest.class);
        addTestSuite(SpecificationVoTest.class);
        addTestSuite(TermVoTest.class);
        addTestSuite(UniversalVoTest.class);
        addTestSuite(UsedByListVoTest.class);
    }
}
