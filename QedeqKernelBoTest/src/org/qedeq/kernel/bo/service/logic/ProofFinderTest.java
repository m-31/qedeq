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
import org.qedeq.kernel.bo.logic.common.ProofFinder;
import org.qedeq.kernel.bo.logic.proof.finder.ProofFinderImpl;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;

/**
 * For testing of loading required QEDEQ modules.
 *
 * FIXME 20110322 m31: work in progress
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
     * Check module that imports a module with logical errors.
     *
     * @throws Exception
     */
    public void testCheckModule() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        KernelContext.getInstance().checkModule(address);
        final QedeqBo bo = KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.isChecked());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final KernelQedeqBo q = (KernelQedeqBo) bo;
        final KernelNodeBo node = q.getLabels().getNode("proposition:one");
        final Proposition prop = node.getNodeVo().getNodeType().getProposition();
        final ProofFinder finder = new ProofFinderImpl();
        final FormalProofLineList original = prop.getFormalProofList().get(0)
            .getFormalProofLineList();
        final FormalProofLineListVo list = new FormalProofLineListVo();
        for (int i = 0; i < 7; i++) {
            list.add(original.get(i));
        }
        finder.findProof(prop.getFormula().getElement(), list);
    }

    /**
     * Check module that imports a module with logical errors.
     *
     * @throws Exception
     */
    public void testCheckModule2() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        KernelContext.getInstance().checkModule(address);
        final QedeqBo bo = KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.isChecked());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final KernelQedeqBo q = (KernelQedeqBo) bo;
        final KernelNodeBo node = q.getLabels().getNode("proposition:one");
        final Proposition prop = node.getNodeVo().getNodeType().getProposition();
        final ProofFinder finder = new ProofFinderImpl();
        final FormalProofLineList original = prop.getFormalProofList().get(0)
            .getFormalProofLineList();
        final FormalProofLineListVo list = new FormalProofLineListVo();
        for (int i = 0; i < 4; i++) {
            list.add(original.get(i));
        }
        finder.findProof(prop.getFormula().getElement(), list);
    }

}
