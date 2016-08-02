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
package org.qedeq.kernel.bo.service.logic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.log.LogListenerImpl;
import org.qedeq.kernel.bo.log.ModuleEventListenerLog;
import org.qedeq.kernel.bo.log.ModuleEventLog;
import org.qedeq.kernel.bo.log.QedeqLog;
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

    private LogListenerImpl listener;
    private ModuleEventListenerLog moduleListener;

    public SimpleProofFinderPluginTest() {
        super();
    }

    public SimpleProofFinderPluginTest(final String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        listener = new LogListenerImpl();
        QedeqLog.getInstance().addLog(listener);
        moduleListener = new ModuleEventListenerLog();
        ModuleEventLog.getInstance().addLog(moduleListener);
    }

    public void tearDown() throws Exception {
        ModuleEventLog.getInstance().removeLog(moduleListener);
        QedeqLog.getInstance().removeLog(listener);
        super.tearDown();
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
        assertTrue(bo.isWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        KernelQedeqBo qedeq = (KernelQedeqBo) bo;
        removeFormalProof(qedeq, "proposition:one");
        removeFormalProof(qedeq, "proposition:two");
        // addDummyFormalProof(qedeq, "proposition:three"); TODO 20130325 m31: if proof extension
                                                                   // works, this might be useful
        getServices().checkFormallyProved(address);
        assertFalse(getServices().getQedeqBo(address).isFullyFormallyProved());
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
        getInternalServices().getConfig().setServiceKeyValues(new SimpleProofFinderPlugin(),
            new Parameters(parameters));
        getServices().executePlugin(SimpleProofFinderPlugin.class.getName(), address, null);
        getServices().checkFormallyProved(address);
        assertTrue(getServices().getQedeqBo(address).isFullyFormallyProved());
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
        assertTrue(bo.isWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        KernelQedeqBo qedeq = (KernelQedeqBo) bo;
        removeNodeType(qedeq, "axiom:universalInstantiation");
        removeNodeType(qedeq, "axiom:existencialGeneralization");
        removeFormalProof(qedeq, "proposition:one");
        removeFormalProof(qedeq, "proposition:two");
        // addDummyFormalProof(qedeq, "proposition:three"); TODO 20130325 m31: if proof extension
                                                                   // works, this might be useful
        removeNodeType(qedeq, "proposition:four");
        removeNodeType(qedeq, "proposition:five");
        removeNodeType(qedeq, "proposition:six");
        removeNodeType(qedeq, "proposition:seven");
        getServices().checkFormallyProved(address);
        assertFalse(getServices().getQedeqBo(address).isFullyFormallyProved());
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
        getInternalServices().getConfig().setServiceKeyValues(new SimpleProofFinderPlugin(), new Parameters(parameters));
        getServices().executePlugin(SimpleProofFinderPlugin.class.getName(), address, null);
        getServices().checkFormallyProved(address);
        assertTrue(getServices().getQedeqBo(address).isFullyFormallyProved());
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
        assertTrue(bo.isWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        KernelQedeqBo qedeq = (KernelQedeqBo) bo;
        removeNodeType(qedeq, "axiom:universalInstantiation");
        removeNodeType(qedeq, "axiom:existencialGeneralization");
        removeFormalProof(qedeq, "proposition:one");
        removeFormalProof(qedeq, "proposition:two");
        // addDummyFormalProof(qedeq, "proposition:three"); TODO 20130325 m31: if proof extension
                                                                   // works, this might be useful
        removeNodeType(qedeq, "proposition:four");
        removeNodeType(qedeq, "proposition:five");
        removeNodeType(qedeq, "proposition:six");
        removeNodeType(qedeq, "proposition:seven");
        assertFalse(getServices().getQedeqBo(address).isFullyFormallyProved());
        getServices().checkFormallyProved(address);
        assertFalse(getServices().getQedeqBo(address).isFullyFormallyProved());
        final Map parameters = new HashMap();
        parameters.put("noSave", "true");
        parameters.put("extraVars", "0");
        parameters.put("propositionVariableOrder", "2");
        parameters.put("propositionVariableWeight", "3");
        parameters.put("partFormulaOrder", "3");
        parameters.put("partFormulaWeight", "1");
        parameters.put("disjunctionOrder", "1");
        parameters.put("disjunctionWeight", "3");
        parameters.put("implicationWeight", "0");
        parameters.put("negationWeight", "0");
        parameters.put("conjunctionWeight", "0");
        parameters.put("equivalenceWeight", "0");
        getInternalServices().getConfig().setServiceKeyValues(new SimpleProofFinderPlugin(),
            new Parameters(parameters));
        getServices().executePlugin(SimpleProofFinderPlugin.class.getName(), address, null);
        getServices().checkFormallyProved(address);
        assertTrue(getServices().getQedeqBo(address).isFullyFormallyProved());
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
     * Remove node.
     *
     * @param   qedeq   Module.
     * @param   id      Id of node we want to "delete".
     */
    private void removeNodeType(KernelQedeqBo qedeq, final String id) {
        KernelNodeBo node = qedeq.getLabels().getNode(id);
        NodeVo node2 = node.getNodeVo();
        node2.setNodeType(null);
    }

    /**
     * Add empty formal proof list to proposition.
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
