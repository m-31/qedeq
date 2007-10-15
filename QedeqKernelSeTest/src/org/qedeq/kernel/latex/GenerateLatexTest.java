/* $Id: GenerateLatexTest.java,v 1.21 2007/10/07 16:43:10 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.bo.control.QedeqBoFactoryTest;
import org.qedeq.kernel.bo.control.QedeqBoFormalLogicChecker;
import org.qedeq.kernel.bo.logic.LogicalCheckException;
import org.qedeq.kernel.bo.module.LogicalState;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.SyntaxException;
import org.qedeq.kernel.common.XmlFileException;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.rel.test.text.Xml2Latex;
import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;
import org.qedeq.kernel.xml.parser.DefaultXmlFileExceptionList;

/**
 * Test generating LaTeX files for all known samples.
 * 
 * @version $Revision: 1.21 $
 * @author Michael Meyling
 */
public final class GenerateLatexTest extends QedeqTestCase {

    /**
     * Start main process.
     * 
     * @throws Exception
     */
    public void testGeneration() throws Exception {
        File docDir = new File("../QedeqDoc");
        File genDir = new File("../../../qedeq_gen");
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
        generate(docDir, "math/qedeq_sample1.xml", genDir);
        generate(docDir, "math/qedeq_set_theory_v1.xml", genDir);
        generate(docDir, "math/qedeq_logic_v1.xml", genDir);
        generate(docDir, "project/qedeq_basic_concept.xml", genDir);
        generate(docDir, "project/qedeq_logic_language.xml", genDir);
    }

    public void testNegative02() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample2_error.xml", "de",
                new File("."));
            fail("IllegalModuleDataException expected");
        } catch (XmlFileExceptionList list) {
            DefaultXmlFileExceptionList ex = (DefaultXmlFileExceptionList) list;
            System.out.println(ex);
            assertEquals(1, ex.size());
            XmlFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof ModuleDataException);
            assertEquals(10001, e.getErrorCode());
            assertEquals(221, e.getSourceArea().getStartPosition().getLine());
            assertEquals(9, e.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative03() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample3_error.xml", "en",
                new File("."));
            fail("IllegalModuleDataException expected");
        } catch (XmlFileExceptionList list) {
            DefaultXmlFileExceptionList ex = (DefaultXmlFileExceptionList) list;
            System.out.println(ex);
            assertEquals(1, ex.size());
            XmlFileException e = ex.get(0);
            System.out.println(e.getCause().getClass().getName());
            assertTrue(e.getCause() instanceof SyntaxException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(311, e.getSourceArea().getStartPosition().getLine());
            assertEquals(30, e.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative04() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample4_error.xml", "en",
                new File("."));
            fail("IllegalModuleDataException expected");
        } catch (XmlFileExceptionList list) {
            DefaultXmlFileExceptionList ex = (DefaultXmlFileExceptionList) list;
            assertEquals(7, ex.size());
            XmlFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof SyntaxException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(13, e.getSourceArea().getStartPosition().getLine());
            assertEquals(13, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(1);
            assertTrue(e.getCause() instanceof SyntaxException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(16, e.getSourceArea().getStartPosition().getLine());
            assertEquals(16, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(2);
            assertTrue(e.getCause() instanceof SyntaxException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(19, e.getSourceArea().getStartPosition().getLine());
            assertEquals(15, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(3);
            assertTrue(e.getCause() instanceof SyntaxException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(22, e.getSourceArea().getStartPosition().getLine());
            assertEquals(15, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(4);
            assertTrue(e.getCause() instanceof SyntaxException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(26, e.getSourceArea().getStartPosition().getLine());
            assertEquals(23, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(5);
            assertTrue(e.getCause() instanceof SyntaxException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(69, e.getSourceArea().getStartPosition().getLine());
            assertEquals(47, e.getSourceArea().getStartPosition().getColumn());
            e = ex.get(6);
            assertTrue(e.getCause() instanceof SyntaxException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(98, e.getSourceArea().getStartPosition().getLine());
            assertEquals(47, e.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative05() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample5_error.xml", "en",
                new File("."));
            fail("IllegalModuleDataException expected");
        } catch (XmlFileExceptionList list) {
            DefaultXmlFileExceptionList ex = (DefaultXmlFileExceptionList) list;
            assertEquals(1, ex.size());
            XmlFileException e = ex.get(0);
            System.out.println(e.getCause().getClass().getName());
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30550, e.getErrorCode());
            assertEquals(166, e.getSourceArea().getStartPosition().getLine());
            assertEquals(15, e.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative06() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample6_error.xml", "en",
                new File("."));
            fail("IllegalModuleDataException expected");
        } catch (XmlFileExceptionList list) {
            DefaultXmlFileExceptionList ex = (DefaultXmlFileExceptionList) list;
            assertEquals(1, ex.size());
            XmlFileException e = ex.get(0);
            System.out.println(e.getCause().getClass().getName());
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30590, e.getErrorCode());
            assertEquals(284, e.getSourceArea().getStartPosition().getLine());
            assertEquals(21, e.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative07() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample7_error.xml", "en",
                new File("."));
            fail("IllegalModuleDataException expected");
        } catch (XmlFileExceptionList list) {
            DefaultXmlFileExceptionList ex = (DefaultXmlFileExceptionList) list;
            assertEquals(1, ex.size());
            XmlFileException e = ex.get(0);
            System.out.println(e.getCause().getClass().getName());
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30780, e.getErrorCode());
            assertEquals(294, e.getSourceArea().getStartPosition().getLine());
            assertEquals(17, e.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative08() throws IOException {
        try {
            generate(new File("."), "data/qedeq_sample8_error.xml", "en",
                new File("."));
            fail("IllegalModuleDataException expected");
        } catch (XmlFileExceptionList list) {
            DefaultXmlFileExceptionList ex = (DefaultXmlFileExceptionList) list;
            assertEquals(1, ex.size());
            XmlFileException e = ex.get(0);
            System.out.println(e.getCause().getClass().getName());
            assertTrue(e.getCause() instanceof ModuleDataException);
            assertEquals(1001, e.getErrorCode());
            assertEquals(304, e.getSourceArea().getStartPosition().getLine());
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
     * @throws  Exception   Failure.
     */
    private final static void generate(final File dir, final String xml, 
            final File destinationDirectory) throws Exception {
        generate(dir, xml, "en", destinationDirectory);
        generate(dir, xml, "de", destinationDirectory);
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
            final File destinationDirectory) throws IOException, XmlFileExceptionList {
        final File xmlFile = new File(dir, xml);
        final File texFile = new File(Xml2Latex.generate(xmlFile.getAbsolutePath(), null, language, 
            "1"));
        final File texCopy = new File(destinationDirectory, new File(new File(xml).getParent(), 
            texFile.getName()).getPath());
        final File xmlCopy = new File(destinationDirectory, xml);
        IoUtility.copyFile(xmlFile, xmlCopy);
        IoUtility.copyFile(texFile, texCopy);
        ModuleProperties prop = KernelContext.getInstance().getModuleProperties(IoUtility.toUrl(
            xmlFile.getAbsoluteFile().getCanonicalFile()).toExternalForm());
        QedeqBo qedeq = prop.getModule();
        try {
            QedeqLog.getInstance().logRequest("Check logical correctness of \""
                + prop.getAddress() + "\"");
            prop.setLogicalProgressState(LogicalState.STATE_INTERNAL_CHECKING);
            ModuleEventLog.getInstance().stateChanged(prop);
            QedeqBoFormalLogicChecker.check(KernelContext.getInstance().getLocalName(
                prop.getModuleAddress()), prop.getModule());
            QedeqLog.getInstance().logSuccessfulReply(
                "Check of logical correctness successful for \""
                + prop.getAddress() + "\"");
            prop.setLogicalProgressState(LogicalState.STATE_CHECKED);
            ModuleEventLog.getInstance().stateChanged(prop);
        } catch (ModuleDataException e) {
            final String msg = "Check of logical correctness failed for \""
                + prop.getAddress() + "\"";
            prop.setLogicalFailureState(
                LogicalState.STATE_INTERNAL_CHECK_FAILED, 
                    ModuleDataException2XmlFileException.createXmlFileExceptionList(e,
                    qedeq));
            ModuleEventLog.getInstance().stateChanged(prop);
            QedeqLog.getInstance().logFailureReply(e.getMessage(), msg);
            throw ModuleDataException2XmlFileException.createXmlFileExceptionList(e, qedeq);
        }
    }

}
