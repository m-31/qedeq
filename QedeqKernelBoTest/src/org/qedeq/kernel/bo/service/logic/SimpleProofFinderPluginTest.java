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
package org.qedeq.kernel.bo.service.logic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.se.dto.module.NodeVo;
import org.qedeq.kernel.se.dto.module.PropositionVo;

/**
 * For testing of loading required QEDEQ modules.
 *
 * FIXME m31 20100823: integrate some more unit tests here! Check proof result validity.
 *
 * @author Michael Meyling
 */
public class SimpleProofFinderPluginTest extends QedeqBoTestCase {

    public SimpleProofFinderPluginTest() {
        super();
    }

    public SimpleProofFinderPluginTest(final String name) {
        super(name);
    }

    /**
     * Try proof finder.
     *
     * @throws Exception
     */
    public void testPlugin() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        getServices().checkWellFormedness(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.wasCheckedForBeingWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        KernelQedeqBo qedeq = (KernelQedeqBo) bo;
        removeFormalProof(qedeq, "proposition:one");
        removeFormalProof(qedeq, "proposition:two");
        addDummyFormalProof(qedeq, "proposition:three");
        final Map parameters = new HashMap();
        parameters.put("noSave", "true");
        parameters.put("extraVars", "0");
        parameters.put("propositionVariableOrder", "2");
        parameters.put("propositionVariableWeight", "3");
        parameters.put("partFormulaWeight", "0");
        parameters.put("disjunctionOrder", "1");
        parameters.put("disjunctionWeight", "3");
        parameters.put("implicationWeight", "0");
        parameters.put("negationWeight", "0");
        parameters.put("conjunctionWeight", "0");
        parameters.put("equivalenceWeight", "0");
        getServices().getConfig().setPluginKeyValues(new SimpleProofFinderPlugin(), new Parameters(parameters));
        getServices().executePlugin(SimpleProofFinderPlugin.class.getName(), address);
    }

    /**
     * Try proof finder. Removes unused axioms (and is two formulas faster...).
     *
     * @throws Exception
     */
    public void testPlugin2() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        getServices().checkWellFormedness(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.wasCheckedForBeingWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        KernelQedeqBo qedeq = (KernelQedeqBo) bo;
        removeNodeType(qedeq, "axiom:universalInstantiation");
        removeNodeType(qedeq, "axiom:existencialGeneralization");
        removeFormalProof(qedeq, "proposition:one");
        removeFormalProof(qedeq, "proposition:two");
        addDummyFormalProof(qedeq, "proposition:three");
        final Map parameters = new HashMap();
        parameters.put("noSave", "true");
        parameters.put("extraVars", "0");
        parameters.put("propositionVariableOrder", "2");
        parameters.put("propositionVariableWeight", "3");
        parameters.put("partFormulaWeight", "0");
        parameters.put("disjunctionOrder", "1");
        parameters.put("disjunctionWeight", "3");
        parameters.put("implicationWeight", "0");
        parameters.put("negationWeight", "0");
        parameters.put("conjunctionWeight", "0");
        parameters.put("equivalenceWeight", "0");
        getServices().getConfig().setPluginKeyValues(new SimpleProofFinderPlugin(), new Parameters(parameters));
        getServices().executePlugin(SimpleProofFinderPlugin.class.getName(), address);
    }

    /**
     * Try proof finder. Removes unused axioms (and is two formulas faster...).
     *
     * @throws Exception
     */
    public void testPluginFast() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        getServices().checkWellFormedness(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.wasCheckedForBeingWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        KernelQedeqBo qedeq = (KernelQedeqBo) bo;
        removeNodeType(qedeq, "axiom:universalInstantiation");
        removeNodeType(qedeq, "axiom:existencialGeneralization");
        addDummyFormalProof(qedeq, "proposition:one");
        addDummyFormalProof(qedeq, "proposition:two");
        removeFormalProof(qedeq, "proposition:three");
        final Map parameters = new HashMap();
        parameters.put("noSave", "true");
        parameters.put("extraVars", "0");
        parameters.put("propositionVariableOrder", "2");
        parameters.put("propositionVariableWeight", "3");
        parameters.put("partFormulaOrder", "1");
        parameters.put("partFormulaWeight", "1");
        parameters.put("disjunctionOrder", "1");
        parameters.put("disjunctionWeight", "3");
        parameters.put("implicationWeight", "0");
        parameters.put("negationWeight", "0");
        parameters.put("conjunctionWeight", "0");
        parameters.put("equivalenceWeight", "0");
        getServices().getConfig().setPluginKeyValues(new SimpleProofFinderPlugin(), new Parameters(parameters));
        getServices().executePlugin(SimpleProofFinderPlugin.class.getName(), address);
    }

    /**
     * Remove formal proof from proposition.
     *
     * @param   qedeq   Module.
     * @param   id      Id of proposition where we remove all formal proofs.
     */
    private void removeFormalProof(KernelQedeqBo qedeq, final String id) {
        KernelNodeBo node = qedeq.getLabels().getNode(id);
        PropositionVo proposition = (PropositionVo) node.getNodeVo().getNodeType().getProposition();
        proposition.setFormalProofList(null);
    }

    /**
     * Remove axiom node.
     *
     * @param   qedeq   Module.
     * @param   id      Id of proposition where we remove all formal proofs.
     */
    private void removeNodeType(KernelQedeqBo qedeq, final String id) {
        KernelNodeBo node = qedeq.getLabels().getNode(id);
        NodeVo node2 = node.getNodeVo();
        node2.setNodeType(null);
    }

    /**
     * Remove formal proof from proposition.
     *
     * @param   qedeq   Module.
     * @param   id      Id of proposition where we remove all formal proofs.
     */
    private void addDummyFormalProof(KernelQedeqBo qedeq, final String id) {
        KernelNodeBo node = qedeq.getLabels().getNode(id);
        PropositionVo proposition = (PropositionVo) node.getNodeVo().getNodeType().getProposition();
        final FormalProofVo proof = new FormalProofVo();
        final FormalProofLineListVo lines = new FormalProofLineListVo();
        lines.add(new FormalProofLineVo());
        proof.setFormalProofLineList(lines);
        proposition.addFormalProof(proof);
    }

}
