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
package org.qedeq.kernel.bo.service.heuristic;

import java.io.File;
import java.io.IOException;

import org.qedeq.base.io.UrlUtility;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.logic.model.Model;
import org.qedeq.kernel.bo.logic.model.SixDynamicModel;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * For testing model validity.
 *
 * @author Michael Meyling
 */
public class DynamicHeuristicCheckerPluginTest extends QedeqBoTestCase {

    public DynamicHeuristicCheckerPluginTest() {
        super();
    }

    public DynamicHeuristicCheckerPluginTest(final String name) {
        super(name);
    }

    /**
     * Call the model check for a module.
     */
    public QedeqBo check(final Model model, final File dir, final String xml) throws IOException,
            SourceFileExceptionList {
        final File xmlFile = new File(dir, xml);
        final ModuleAddress address = getServices().getModuleAddress(
            UrlUtility.toUrl(xmlFile));
        final KernelQedeqBo prop = (KernelQedeqBo) getServices().loadModule(
            address);
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        getServices().loadRequiredModules(prop.getModuleAddress());
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        getServices().checkWellFormedness(prop.getModuleAddress());
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        getInternalServices().getConfig().setServiceKeyValue(new HeuristicCheckerPlugin(), "model",
                model.getClass().getName());
        getServices().executePlugin(
            "org.qedeq.kernel.bo.service.heuristic.DynamicHeuristicCheckerPlugin",
            prop.getModuleAddress(), null);
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        return prop;
    }

    /**
     * Test sample script for heuristic errors with default model.
     *
     * @throws Exception
     */
    public void testQedeqSampleScript1() throws Exception {
        final QedeqBo bo = check(new SixDynamicModel(), getDocDir(), "sample/qedeq_sample1.xml");
        assertEquals(0, bo.getErrors().size());
        assertEquals(0, bo.getWarnings().size());
    }

    /**
     * Test logic script for heuristic errors with default model.
     *
     * @throws Exception
     */
    public void kestQedeqLogicScript2() throws Exception {
        final QedeqBo bo = check(new SixDynamicModel(), getDocDir(), "math/qedeq_logic_v1.xml");
        assertEquals(0, bo.getErrors().size());
        assertEquals(0, bo.getWarnings().size());
    }

    /**
     * Test set theory script for heuristic errors with default model.
     *
     * @throws Exception
     */
    public void kestQedeqSetTheoryScript1() throws Exception {
        final QedeqBo bo = check(new SixDynamicModel(), getDocDir(), "math/qedeq_set_theory_v1.xml");
        assertEquals(0, bo.getErrors().size());
        assertEquals(52, bo.getWarnings().size());
    }

}
