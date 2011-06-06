/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
package org.qedeq.kernel.bo.logic.proof.finder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.log.LogListenerImpl;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.common.ProofFinder;
import org.qedeq.kernel.bo.logic.common.ProofFoundException;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;

/**
 * For testing of finding formal proofs.
 *
 * @author Michael Meyling
 */
public class ProofFinderTest extends QedeqBoTestCase {

    public ProofFinderTest() {
        super();
    }

    public ProofFinderTest(final String name) {
        super(name);
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
        final ProofFinder finder = new ProofFinderImpl();
        final FormalProofLineList original = prop.getFormalProofList().get(0)
            .getFormalProofLineList();
        final FormalProofLineListVo list = new FormalProofLineListVo();
        for (int i = 0; i < 4; i++) {
            list.add(original.get(i));
        }
        final Map parameters = new HashMap();
        parameters.put("extraVars", "0");
        parameters.put("maximumProofLines", "100000");
        parameters.put("propositionVariableOrder", "2");
        parameters.put("propositionVariableWeight", "3");
        parameters.put("partFormulaWeight", "0");
        parameters.put("disjunctionOrder", "1");
        parameters.put("disjunctionWeight", "3");
        parameters.put("implicationWeight", "0");
        parameters.put("negationWeight", "0");
        parameters.put("conjunctionWeight", "0");
        parameters.put("equivalenceWeight", "0");
        try {
            finder.findProof(prop.getFormula().getElement(), list, new ModuleContext(
            new DefaultModuleAddress()), parameters, new LogListenerImpl(), bo.getElement2Utf8());
            fail("no proof found");
        } catch (ProofFoundException e) {
            assertNotNull(e.getProofLines());
        }
    }

    /**
     * Find a proof.
     *
     * @throws Exception
     */
    public void testFind2() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        KernelContext.getInstance().checkModule(address);
        final KernelQedeqBo bo = (KernelQedeqBo) KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.isChecked());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final KernelNodeBo node = bo.getLabels().getNode("proposition:two");
        final Proposition prop = node.getNodeVo().getNodeType().getProposition();
        final ProofFinder finder = new ProofFinderImpl();
        final FormalProofLineList original = prop.getFormalProofList().get(0)
            .getFormalProofLineList();
        final FormalProofLineListVo list = new FormalProofLineListVo();
        for (int i = 0; i < 3; i++) {
            list.add(original.get(i));
        }
        final Map parameters = new HashMap();
        parameters.put("extraVars", "0");
        parameters.put("maximumProofLines", "100000");
        parameters.put("propositionVariableOrder", "2");
        parameters.put("propositionVariableWeight", "3");
        parameters.put("partFormulaWeight", "0");
        parameters.put("disjunctionOrder", "1");
        parameters.put("disjunctionWeight", "3");
        parameters.put("implicationWeight", "0");
        parameters.put("negationWeight", "0");
        parameters.put("conjunctionWeight", "0");
        parameters.put("equivalenceWeight", "0");
        try {
            finder.findProof(prop.getFormula().getElement(), list, new ModuleContext(
                new DefaultModuleAddress()), parameters, QedeqLog.getInstance(),
                bo.getElement2Utf8());
            fail("no proof found");
        } catch (ProofFoundException e) {
            assertNotNull(e.getProofLines());
        }
    }

}
