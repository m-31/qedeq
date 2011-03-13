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
import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.logic.model.Model;
import org.qedeq.kernel.bo.logic.model.SixDynamicModel;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * For test of generating LaTeX output.
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
     * Call the generation of one LaTeX file and copy XML source to same destination directory.
     *
     * @param dir Start directory.
     * @param xml Relative path to XML file. Must not be <code>null</code>.
     * @param language Generate text in this language. Can be <code>null</code>.
     * @param destinationDirectory Directory path for LaTeX file. Must not be <code>null</code>.
     * @throws IOException File IO failed.
     * @throws XmlFilePositionException File data is invalid.
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

        final Map parameters = new HashMap();
        parameters.put(HeuristicCheckerPlugin.class.getName() + "$model",
            model.getClass().getName());
        KernelFacade.getKernelContext().executePlugin(
            "org.qedeq.kernel.bo.service.heuristic.DynamicHeuristicCheckerPlugin", prop.getModuleAddress(), 
            parameters);
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
