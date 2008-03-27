/* $Id: GenerateLatexTest.java,v 1.24 2008/03/27 05:12:46 m31 Exp $
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

package org.qedeq.kernel.latex;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.qedeq.kernel.bo.control.DefaultModuleAddress;
import org.qedeq.kernel.bo.control.InternalKernelServices;
import org.qedeq.kernel.bo.control.KernelQedeqBo;
import org.qedeq.kernel.bo.control.KernelQedeqBoPool;
import org.qedeq.kernel.bo.control.QedeqBoDuplicateLanguageChecker;
import org.qedeq.kernel.bo.control.QedeqBoFactoryTest;
import org.qedeq.kernel.bo.logic.LogicalCheckException;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.test.KernelFacade;
import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.utility.IoUtility;
import org.xml.sax.SAXParseException;

/**
 * Test generating LaTeX files for all known samples.
 * 
 * @version $Revision: 1.24 $
 * @author Michael Meyling
 */
public final class GenerateLatexTest extends QedeqTestCase {

    private File genDir;
    private File docDir;

    public void setUp() throws Exception{
        super.setUp();
        docDir = new File("../QedeqDoc");
        genDir = new File("../../../qedeq_gen");
        // test if we are in the normal development environment, where a project with name
        //  "../QedeqDoc" exists, otherwise we assume to run within the release directory
        //  structure where the docs are in the directory ../doc
        if (!docDir.exists()) {
            docDir = new File("../doc");
            genDir = new File("../doc");
            if (!docDir.exists()) {
                throw new IOException("unknown source directory for qedeq modules");
            }
        }
        KernelFacade.startup();
    }
    
    public void tearDown() throws Exception{
        super.tearDown();
        KernelFacade.shutdown();
    }
    
    /**
     * Start main process.
     * 
     * @throws Exception
     */
    public void testGeneration() throws Exception {
        generate(docDir, "math/qedeq_sample1.xml", genDir, false);
        generate(docDir, "math/qedeq_set_theory_v1.xml", genDir, false);
        generate(docDir, "math/qedeq_logic_v1.xml", genDir, false);
        generate(docDir, "project/qedeq_basic_concept.xml", genDir, false);
        generate(docDir, "project/qedeq_logic_language.xml", genDir, true);
    }

    public void testNegative02() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample2_error.xml", "de",
                new File(genDir, "null"));
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
            generate(new File("."), "data/qedeq_sample3_error.xml", "en",
                new File(genDir, "null"));
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(313, e.getSourceArea().getStartPosition().getLine());
            assertEquals(30, e.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative04() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample4_error.xml", "en",
                new File(genDir, "null"));
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(7, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(13, e.getSourceArea().getStartPosition().getLine());
            assertEquals(13, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(1);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(16, e.getSourceArea().getStartPosition().getLine());
            assertEquals(16, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(2);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(19, e.getSourceArea().getStartPosition().getLine());
            assertEquals(15, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(3);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(22, e.getSourceArea().getStartPosition().getLine());
            assertEquals(15, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(4);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(26, e.getSourceArea().getStartPosition().getLine());
            assertEquals(23, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(5);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(69, e.getSourceArea().getStartPosition().getLine());
            assertEquals(47, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(6);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(98, e.getSourceArea().getStartPosition().getLine());
            assertEquals(47, e.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative05() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample5_error.xml", "en",
                new File(genDir, "null"));
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
            generate(new File("."), "data/qedeq_sample6_error.xml", "en",
                new File(genDir, "null"));
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
            generate(new File("."), "data/qedeq_sample7_error.xml", "en",
                new File(genDir, "null"));
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
            generate(new File("."), "data/qedeq_sample8_error.xml", "en",
                new File(genDir, "null"));
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
     * <p>
     * Tests also if the parsed context can be found by
     * {@link org.qedeq.kernel.xml.mapper.Context2XPath#getXPath(ModuleContext)}.
     *
     * @param   dir         Start directory.
     * @param   xml         Relative path to XML file. Must not be <code>null</code>.
     * @param   destinationDirectory Directory path for LaTeX file. Must not be <code>null</code>.
     * @param   onlyEn      Generate only for language "en".
     * @throws  Exception   Failure.
     */
    private final static void generate(final File dir, final String xml, 
            final File destinationDirectory, final boolean onlyEn) throws Exception {
        generate(dir, xml, "en", destinationDirectory);
        if (!onlyEn) {
            generate(dir, xml, "de", destinationDirectory);
        }
        QedeqBoFactoryTest.loadQedeqAndAssertContext(new File(dir, xml));
    }

    /**
     * Call the generation of one LaTeX file and copy XML source to same destination directory.
     *
     * @param   dir         Start directory.
     * @param   xml         Relative path to XML file. Must not be <code>null</code>.
     * @param   language    Generate text in this language. Can be <code>null</code>.
     * @param   destinationDirectory Directory path for LaTeX file. Must not be <code>null</code>.
     * @throws  IOException File IO failed.
     * @throws  XmlFilePositionException File data is invalid.
     */
    private static void generate(final File dir, final String xml, final String language,
            final File destinationDirectory) throws IOException, SourceFileExceptionList {
        final File xmlFile = new File(dir, xml);
        final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
            IoUtility.toUrl(xmlFile));
        final KernelQedeqBo prop = (KernelQedeqBo) KernelFacade.getKernelContext()
            .loadModule(address);
        KernelFacade.getKernelContext().loadRequiredModules(prop.getModuleAddress());
        KernelFacade.getKernelContext().checkModule(prop.getModuleAddress());
        QedeqBoDuplicateLanguageChecker.check(prop);
        if (!prop.isChecked()) {
            throw prop.getException();
        }
        
        // FIXME doesn't work for links to external QEDEQ modules because that are absolute links!
        final String web = "http://qedeq.org/" 
            + KernelFacade.getKernelContext().getKernelVersionDirectory() + "/doc/" + xml;
        final KernelQedeqBo fakeProp = (KernelQedeqBo) KernelFacade.getKernelContext()
            .getQedeqBo(new DefaultModuleAddress(web));
        fakeProp.setLoader(prop.getLoader());
        IoUtility.setFieldContent(fakeProp, "qedeq", prop.getQedeq());
        fakeProp.getKernelRequiredModules().set(prop.getKernelRequiredModules());
        fakeProp.getDependentModules().set(prop.getDependentModules());
        IoUtility.setFieldContent(fakeProp, "loadingState", LoadingState.STATE_LOADED);
        IoUtility.setFieldContent(fakeProp, "dependencyState",
            DependencyState.STATE_LOADED_REQUIRED_MODULES);
        IoUtility.setFieldContent(fakeProp, "logicalState", LogicalState.STATE_CHECKED);
        final InternalKernelServices services = (InternalKernelServices) IoUtility
            .getFieldContent(KernelFacade.getKernelContext(), "services");
        final KernelQedeqBoPool pool = (KernelQedeqBoPool) IoUtility
            .getFieldContent(services, "modules");
        final HashMap bos = (HashMap) IoUtility
            .getFieldContent(pool, "bos");
        bos.remove(address);
        bos.put(address, fakeProp);
        final File texFile = new File(destinationDirectory, 
            xml.substring(0, xml.lastIndexOf('.')) + "_" + language + ".tex");
        IoUtility.setFieldContent(services, "validate", Boolean.FALSE);
        Xml2Latex.generate(fakeProp, texFile, language, "1");
        IoUtility.setFieldContent(services, "validate", Boolean.TRUE);
        bos.remove(address);
        bos.clear();
        final File texCopy = new File(dir, new File(new File(xml).getParent(), 
            texFile.getName()).getPath());
        final File xmlCopy = new File(destinationDirectory, xml);
        IoUtility.createNecessaryDirectories(xmlCopy);
        IoUtility.copyFile(xmlFile, xmlCopy);
        IoUtility.copyFile(texFile, texCopy);
    }

}
