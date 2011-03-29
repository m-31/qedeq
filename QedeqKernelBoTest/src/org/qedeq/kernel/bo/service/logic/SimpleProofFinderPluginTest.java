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
package org.qedeq.kernel.bo.service.logic;

import java.io.File;

import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.se.dto.module.PropositionVo;

/**
 * For testing of loading required QEDEQ modules.
 *
 * FIXME m31 20100823: integrate some more unit tests here!
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
     * Check module that imports a module with logical errors.
     *
     * @throws Exception
     */
    public void testPlugin() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        KernelContext.getInstance().checkModule(address);
        final QedeqBo bo = KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.isChecked());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        KernelQedeqBo qedeq = (KernelQedeqBo) bo;
        removeFormalProof(qedeq, "proposition:one");
        removeFormalProof(qedeq, "proposition:two");
        addDummyFormalProof(qedeq, "proposition:three");
        KernelContext.getInstance().executePlugin(SimpleProofFinderPlugin.class.getName(),
            address, null);
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