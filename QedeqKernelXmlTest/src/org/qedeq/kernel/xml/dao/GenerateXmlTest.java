/* $Id: GenerateXmlTest.java,v 1.1 2008/07/26 08:01:09 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.dao;

import java.io.File;
import java.io.IOException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * Test generating LaTeX files for all known samples.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public final class GenerateXmlTest extends QedeqTestCase {

    public void setUp() throws Exception {
        super.setUp();
        KernelFacade.startup();
    }

    public void tearDown() throws Exception {
        KernelFacade.shutdown();
        super.tearDown();
    }

    /**
     * Start main process.
     *
     * @throws Exception
     */
    public void testGeneration() throws Exception {
        File docDir = new File("../QedeqDoc");
        File genDir = new File("../../../qedeq_gen");
        // test if we are in the normal development environment, where a project with name
        //  "../QedeqDoc" exists, otherwise we assume to run within the build process
        if (!docDir.exists()) {
            docDir = new File(getIndir(), "doc");
            genDir = new File(getOutdir(), "doc");
            if (!docDir.exists()) {
                throw new IOException("unknown source directory for QEDEQ modules");
            }
        }
        generate(docDir, "math/qedeq_sample1.xml", genDir);

        generate(getIndir(), "qedeq_set_theory_compare.xml", genDir);
        generate(getIndir(), "qedeq_basic_concept_compare.xml", genDir);
    }

    /**
     * Call the generation of one LaTeX file and copy XML source to same destination directory.
     *
     * @param   dir         Start directory.
     * @param   xml         Relative path to XML file. Must not be <code>null</code>.
     * @param   destinationDirectory Directory path for LaTeX file. Must not be <code>null</code>.
     * @throws  IOException File IO failed.
     * @throws  XmlFilePositionExceptionList File data is invalid.
     */
    private static void generate(final File dir, final String xml,
            final File destinationDirectory) throws IOException, SourceFileExceptionList {
        final File xmlFile = new File(dir, xml);
        final File destination = new File(destinationDirectory, xml + "_").getAbsoluteFile();
        Xml2Xml.generate(xmlFile, destination);
        assertEquals(true, IoUtility.compareTextFiles(xmlFile, destination));
    }

}
