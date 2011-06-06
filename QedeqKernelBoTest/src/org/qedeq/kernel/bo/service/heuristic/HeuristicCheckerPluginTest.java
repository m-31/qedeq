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
package org.qedeq.kernel.bo.service.heuristic;

import java.io.File;
import java.io.IOException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.logic.model.Model;
import org.qedeq.kernel.bo.logic.model.ThreeModel;
import org.qedeq.kernel.bo.logic.model.UnaryModel;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * For testing model validity.
 *
 * @author Michael Meyling
 */
public class HeuristicCheckerPluginTest extends QedeqBoTestCase {

    public HeuristicCheckerPluginTest() {
        super();
    }

    public HeuristicCheckerPluginTest(final String name) {
        super(name);
    }

    /**
     * Test with model.
     */
    public QedeqBo check(final Model model, final File dir, final String xml) throws IOException,
            SourceFileExceptionList {
        final File xmlFile = new File(dir, xml);
        final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
            IoUtility.toUrl(xmlFile));
        final KernelQedeqBo prop = (KernelQedeqBo) KernelFacade.getKernelContext().loadModule(
            address);
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        KernelFacade.getKernelContext().loadRequiredModules(prop.getModuleAddress());
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        KernelFacade.getKernelContext().checkModule(prop.getModuleAddress());
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        getServices().getConfig().setPluginKeyValue(new HeuristicCheckerPlugin(), "model",
            model.getClass().getName());
        getServices().executePlugin(
            "org.qedeq.kernel.bo.service.heuristic.HeuristicCheckerPlugin", prop.getModuleAddress()); 
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        return prop;
    }

    /**
     * Test logic script for heuristic errors with default model.
     *
     * @throws Exception
     */
    public void testQedeqLogicScript1() throws Exception {
        final QedeqBo bo = check(new ThreeModel(), getDocDir(), "math/qedeq_logic_v1.xml");
        assertEquals(0, bo.getErrors().size());
        assertEquals(0, bo.getWarnings().size());
    }

    /**
     * Test logic script for heuristic errors with zero model.
     *
     * @throws Exception
     */
    public void testQedeqLogicScript2() throws Exception {
        final QedeqBo bo = check(new UnaryModel(), getDocDir(), "math/qedeq_logic_v1.xml");
        assertEquals(0, bo.getErrors().size());
        assertEquals(0, bo.getWarnings().size());
    }

    /**
     * Test logic script for heuristic errors with default model.
     *
     * @throws Exception
     */
    public void testQedeqSetTheoryScript1() throws Exception {
        final QedeqBo bo = check(new ThreeModel(), getDocDir(), "math/qedeq_set_theory_v1.xml");
        assertEquals(0, bo.getErrors().size());
        assertEquals(46, bo.getWarnings().size());
    }

    /**
     * Test logic script for heuristic errors with zero model.
     *
     * @throws Exception
     */
    public void testQedeqSetTheoryScript2() throws Exception {
        final QedeqBo bo = check(new UnaryModel(), getDocDir(), "math/qedeq_set_theory_v1.xml");
        assertEquals(0, bo.getErrors().size());
        assertEquals(2, bo.getWarnings().size());
    }

}
