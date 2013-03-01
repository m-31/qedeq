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

package org.qedeq.kernel.xml.dao;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.qedeq.base.io.IoUtility;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.test.XmlNormalizer;
import org.xml.sax.SAXException;

/**
 * Test generating LaTeX files for all known samples.
 *
 * @author  Michael Meyling
 */
public final class GenerateXmlTest extends QedeqBoTestCase {

    /**
     * Start main process.
     *
     * @throws Exception
     */
    public void testGeneration() throws Exception {
        File docDir = new File("../QedeqDoc");
        File inDir2 = new File("../QedeqKernelBoTest/data");
        File genDir = new File("../../../qedeq_gen");
        // test if we are in the normal development environment, where a project with name
        //  "../QedeqDoc" exists, otherwise we assume to run within the build process
        if (!docDir.exists()) {
            docDir = new File(getIndir(), "doc");
            genDir = new File(getOutdir(), "doc");
            inDir2 = getIndir();
            if (!docDir.exists()) {
                throw new IOException("unknown source directory for QEDEQ modules");
            }
        }

        // compare directly
        generate(getIndir(), "qedeq_set_theory_compare.xml", genDir, false);
        generate(getIndir(), "qedeq_basic_concept_compare.xml", genDir, false);

        final FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".xml");
            }
        };

        // compare all XML documents in doc directory
        final List docFiles = IoUtility.listFilesRecursively(docDir, filter);
        final Iterator i = docFiles.iterator();
        while (i.hasNext()) {
            generate((File) i.next(), genDir, true);
        }

        // compare all XML documents in data directory
        final List dataFiles = IoUtility.listFilesRecursively(inDir2, filter);
        final Iterator j = dataFiles.iterator();
        while (j.hasNext()) {
            try {
                generate((File) j.next(), genDir, true);
            } catch (SourceFileExceptionList e) {
                // ignore
            }
        }

//        generate(inDir2, "proof/proof_001.xml", genDir, true);
    }

    /**
     * Call the generation of one LaTeX file and copy XML source to same destination directory.
     *
     * @param   dir         Start directory.
     * @param   xml         Relative path to XML file. Must not be <code>null</code>.
     * @param   destinationDirectory Directory path for LaTeX file. Must not be <code>null</code>.
     * @param   normalize   Normalize before comparing?
     * @throws  IOException File IO failed.
     */
    private void generate(final File dir, final String xml,
            final File destinationDirectory, final boolean normalize)
            throws IOException, SourceFileExceptionList, SAXException {
        generate(new File(dir, xml), destinationDirectory, normalize);
    }

    /**
     * Call the generation of one LaTeX file and copy XML source to same destination directory.
     *
     * @param   xmlFile                 XML file. Must not be <code>null</code>.
     * @param   destinationDirectory    Directory path for LaTeX file. Must not be <code>null</code>.
     * @param   normalize               Normalize before comparing?
     * @throws  IOException             File IO failed.
     */
    private void generate(final File xmlFile, final File destinationDirectory, final boolean normalize)
            throws IOException, SourceFileExceptionList, SAXException {
        final File destination = new File(destinationDirectory, xmlFile.getName() + "_").getAbsoluteFile();
        System.out.println("generation of " + xmlFile + " to " + destination);
        Xml2Xml.generate(getServices(), xmlFile, destination);
        if (!normalize) {
            assertEquals(true, IoUtility.compareTextFiles(xmlFile, destination, "UTF-8"));
//            assertEquals(IoUtility.loadFile(xmlFile.getAbsolutePath(), "UTF-8"), 
//                IoUtility.loadFile(destination.getAbsolutePath(), "UTF-8"));
        } else {
            final File xmlFile2 = new File(destinationDirectory, xmlFile.getName() + "2_").getAbsoluteFile();
            XmlNormalizer.normalize(xmlFile, xmlFile2);
            final File destination2 = new File(destinationDirectory, xmlFile.getName() + "3_").getAbsoluteFile();
            XmlNormalizer.normalize(destination, destination2);
            System.out.println("comparing " + xmlFile2 + " with " + destination2);
            assertEquals(true, IoUtility.compareTextFiles(xmlFile2, destination2, "UTF-8"));
        }
    }

}
