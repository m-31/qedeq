/* $Id: GenerateLatexTest.java,v 1.25 2008/05/15 21:27:30 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.latex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.logic.wf.LogicalCheckException;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.DefaultModuleAddress;
import org.qedeq.kernel.bo.service.latex.Qedeq2Latex;
import org.qedeq.kernel.bo.service.latex.QedeqBoDuplicateLanguageChecker;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.xml.sax.SAXParseException;

/**
 * Test generating LaTeX files for all known samples.
 *
 * @version $Revision: 1.25 $
 * @author Michael Meyling
 */
public class GenerateLatexTest extends QedeqTestCase {

    /** This class. */
    private static final Class CLASS = GenerateLatexTest.class;

    /** Here should the result get into. */
    private File genDir;

    /** Here are the documents within. */
    private File docDir;

    public void setUp() throws Exception {
        super.setUp();
        docDir = new File("../QedeqDoc");
        genDir = new File("../../../qedeq_gen");
        // test if we are in the normal development environment, where a project with name
        // "../QedeqDoc" exists, otherwise we assume to run within the release directory
        // structure where the docs are in the directory ../doc
        if (!docDir.exists()) {
            docDir = getFile("doc");
            // or are we testing automatically?
            if (!docDir.exists()) {
                throw new IOException("unknown source directory for QEDEQ modules");
            }
            genDir = new File(getOutdir(), "doc");
        }
        KernelFacade.startup();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        KernelFacade.shutdown();
    }

    /**
     * Start main process.
     *
     * @throws Exception
     */
    public void testGeneration() throws Exception {
        generate(docDir, "math/qedeq_logic_v1.xml", genDir, false);
        generate(docDir, "math/qedeq_sample1.xml", genDir, false);
        generate(docDir, "math/qedeq_set_theory_v1.xml", genDir, false);
        generate(docDir, "project/qedeq_basic_concept.xml", genDir, false);
        generate(docDir, "project/qedeq_logic_language.xml", genDir, true);
    }

    public void testNegative02() throws IOException {
        try {
            generate(getIndir(), "qedeq_sample2_error.xml", "de", new File(genDir, "null"));
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof ModuleDataException);
            assertEquals(10002, e.getErrorCode());
            assertEquals(221, e.getSourceArea().getStartPosition().getLine());
            assertEquals(9, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    public void testNegative03() throws IOException {
        try {
            generate(getIndir(), "qedeq_sample3_error.xml", "en", new File(genDir, "null"));
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(313, e.getSourceArea().getStartPosition().getLine());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(313, e.getSourceArea().getEndPosition().getLine());
            assertEquals(30, e.getSourceArea().getEndPosition().getColumn());
        }
    }

    public void testNegative04() throws IOException {
        try {
            generate(getIndir(), "qedeq_sample4_error.xml", "en", new File(genDir, "null"));
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(7, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(13, e.getSourceArea().getStartPosition().getLine());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(13, e.getSourceArea().getEndPosition().getLine());
            assertEquals(13, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(1);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(16, e.getSourceArea().getStartPosition().getLine());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(16, e.getSourceArea().getEndPosition().getLine());
            assertEquals(16, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(2);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(19, e.getSourceArea().getStartPosition().getLine());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(19, e.getSourceArea().getEndPosition().getLine());
            assertEquals(15, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(3);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(22, e.getSourceArea().getStartPosition().getLine());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(22, e.getSourceArea().getEndPosition().getLine());
            assertEquals(15, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(4);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(26, e.getSourceArea().getStartPosition().getLine());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(26, e.getSourceArea().getEndPosition().getLine());
            assertEquals(23, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(5);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(69, e.getSourceArea().getStartPosition().getLine());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(69, e.getSourceArea().getEndPosition().getLine());
            assertEquals(47, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(6);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(98, e.getSourceArea().getStartPosition().getLine());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(98, e.getSourceArea().getEndPosition().getLine());
            assertEquals(47, e.getSourceArea().getEndPosition().getColumn());
        }
    }

    public void testNegative05() throws IOException {
        try {
            generate(getIndir(), "qedeq_sample5_error.xml", "en", new File(genDir, "null"));
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30550, e.getErrorCode());
            assertEquals(168, e.getSourceArea().getStartPosition().getLine());
            assertEquals(15, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    public void testNegative06() throws IOException {
        try {
            generate(getIndir(), "qedeq_sample6_error.xml", "en", new File(genDir, "null"));
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30590, e.getErrorCode());
            assertEquals(286, e.getSourceArea().getStartPosition().getLine());
            assertEquals(21, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    public void testNegative07() throws IOException {
        try {
            generate(getIndir(), "qedeq_sample7_error.xml", "en", new File(genDir, "null"));
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30780, e.getErrorCode());
            assertEquals(296, e.getSourceArea().getStartPosition().getLine());
            assertEquals(17, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    public void testNegative08() throws IOException {
        try {
            generate(getIndir(), "qedeq_sample8_error.xml", "en", new File(genDir, "null"));
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof ModuleDataException);
            assertEquals(1001, e.getErrorCode());
            assertEquals(306, e.getSourceArea().getStartPosition().getLine());
            assertEquals(15, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    /**
     * Call the generation of one LaTeX file and copy XML source to same destination directory for
     * all supported languages.
     *
     * @param dir Start directory.
     * @param xml Relative path to XML file. Must not be <code>null</code>.
     * @param destinationDirectory Directory path for LaTeX file. Must not be <code>null</code>.
     * @param onlyEn Generate only for language "en".
     * @throws Exception Failure.
     */
    public void generate(final File dir, final String xml, final File destinationDirectory,
            final boolean onlyEn) throws Exception {
        generate(dir, xml, "en", destinationDirectory);
        if (!onlyEn) {
            generate(dir, xml, "de", destinationDirectory);
        }
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
    public void generate(final File dir, final String xml, final String language,
            final File destinationDirectory) throws IOException, SourceFileExceptionList {
        final File xmlFile = new File(dir, xml);
        final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
            IoUtility.toUrl(xmlFile));
        final KernelQedeqBo prop = (KernelQedeqBo) KernelFacade.getKernelContext().loadModule(
            address);
        if (prop.hasFailures()) {
            throw prop.getException();
        }
        KernelFacade.getKernelContext().loadRequiredModules(prop.getModuleAddress());
        if (prop.hasFailures()) {
            throw prop.getException();
        }
        KernelFacade.getKernelContext().checkModule(prop.getModuleAddress());
        if (prop.hasFailures()) {
            throw prop.getException();
        }
        QedeqBoDuplicateLanguageChecker.check(prop);
        if (prop.hasFailures()) {
            throw prop.getException();
        }

        final String web = "http://qedeq.org/"
            + KernelFacade.getKernelContext().getKernelVersionDirectory() + "/doc/" + xml;
        final InternalKernelServices services = (InternalKernelServices) IoUtility.getFieldContent(
            KernelFacade.getKernelContext(), "services");
        final ModuleAddress webAddress = new DefaultModuleAddress(web);
        services.getLocalFilePath(webAddress);
        IoUtility.copyFile(xmlFile, services.getLocalFilePath(webAddress));

        KernelFacade.getKernelContext().checkModule(webAddress);
        final QedeqBo webBo = KernelFacade.getKernelContext().getQedeqBo(webAddress);
        final File texFile = new File(destinationDirectory, xml.substring(0, xml.lastIndexOf('.'))
            + "_" + language + ".tex");
        generate((KernelQedeqBo) webBo, texFile, language, "1");
        final File texCopy = new File(dir, new File(new File(xml).getParent(), texFile.getName())
            .getPath());
        final File xmlCopy = new File(destinationDirectory, xml);
        IoUtility.createNecessaryDirectories(xmlCopy);
        IoUtility.copyFile(xmlFile, xmlCopy);
        IoUtility.copyFile(texFile, texCopy);
    }

    /**
     * Generate LaTeX file out of XML file.
     *
     * @param prop Take this QEDEQ module.
     * @param to Write to this file. Could be <code>null</code>.
     * @param language Resulting language. Could be <code>null</code>.
     * @param level Resulting detail level. Could be <code>null</code>.
     * @return File name of generated LaTeX file.
     * @throws SourceFileExceptionList Something went wrong.
     */
    public String generate(final KernelQedeqBo prop, final File to, final String language,
            final String level) throws SourceFileExceptionList {
        final String method = "generate(String, String, String, String)";
        try {
            Trace.begin(CLASS, method);
            Trace.param(CLASS, method, "prop", prop);
            Trace.param(CLASS, method, "to", to);
            Trace.param(CLASS, method, "language", language);
            Trace.param(CLASS, method, "level", level);
            final InputStream latex = Qedeq2Latex.createLatex(prop, language, level);
            if (to != null) {
                IoUtility.createNecessaryDirectories(to);
                IoUtility.saveFile(latex, to);
                return to.getCanonicalPath();
            } else {
                latex.close();
                return prop.getName();
            }
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (RuntimeException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } finally {
            Trace.end(CLASS, method);
        }
    }

    public File getGenDir() {
        return genDir;
    }

    public File getDocDir() {
        return docDir;
    }

}
