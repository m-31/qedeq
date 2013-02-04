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
package org.qedeq.kernel.bo.logic.proof.checker;

import java.io.File;

import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.bo.logic.proof.common.ProofChecker;
import org.qedeq.kernel.bo.logic.proof.common.RuleChecker;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.RuleKey;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;

/**
 * For testing of checking formal proofs.
 *
 * @author Michael Meyling
 */
public class ProofCheckerTest extends QedeqBoTestCase {

    private ProofChecker checker0;

    private ProofChecker checker1;

    private ProofChecker checker2;

    private RuleChecker ruleCheckerAll;
    
    private ReferenceResolver resolverLocal;

    private Element disjunction_idempotence_axiom =
        new DefaultElementList("IMPL", new Element[] {
            new DefaultElementList("OR", new Element[] {
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("A")
                }),
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("A")
                })
            }),
            new DefaultElementList("PREDVAR", new Element[] {
                new DefaultAtom("A")
            })
        });

    public void setUp() throws Exception {
        super.setUp();
        checker0 = new ProofChecker0Impl();
        checker1 = new ProofChecker1Impl();
        checker2 = new ProofChecker2Impl();
        ruleCheckerAll = new RuleChecker() {
            public RuleKey getRule(String ruleName) {
                return new RuleKey(ruleName, "1.00.00");
            }
        };
        resolverLocal = new ReferenceResolver() {
            public Element getNormalizedFormula(Element element) {
                return element;
            }

            public Element getNormalizedLocalProofLineReference(
                    String reference) {
                return null;
            }

            public Element getNormalizedReferenceFormula(
                    String reference) {
                return null;
            }

            public ModuleContext getReferenceContext(String reference) {
                return null;
            }

            public boolean isLocalProofLineReference(String reference) {
                return false;
            }

            public boolean isProvedFormula(String reference) {
                return true;
            }
        };
    }


    /**
     * Find a proof.
     *
     * @throws Exception
     */
    public void testFind() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        KernelContext.getInstance().checkModule(address);
        final KernelQedeqBo bo = (KernelQedeqBo) KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.isChecked());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final KernelNodeBo node = bo.getLabels().getNode("proposition:one");
        final Proposition prop = node.getNodeVo().getNodeType().getProposition();
        final FormalProofLineList original = prop.getFormalProofList().get(0)
            .getFormalProofLineList();
        final FormalProofLineListVo list = new FormalProofLineListVo();
        for (int i = 0; i < 4; i++) {
            list.add(original.get(i));
        }
        LogicalCheckExceptionList e0 = 
            checker0.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e0);
        assertEquals(1, e0.size());
        assertEquals(37400, e0.get(0).getErrorCode());
        LogicalCheckExceptionList e2 = 
            checker2.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e2);
        System.out.println(e2);
    }

}
