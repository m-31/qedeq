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

package org.qedeq.kernel.bo.latex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.common.KernelProperties;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.logic.common.LogicalCheckException;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.latex.Qedeq2LatexExecutor;
import org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin;
import org.qedeq.kernel.bo.service.latex.QedeqBoDuplicateLanguageChecker;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.xml.sax.SAXParseException;

/**
 * Test generating LaTeX files for all known samples.
 *
 * @author Michael Meyling
 */
public class GenerateLatexTest extends QedeqBoTestCase {

    /** This class. */
    private static final Class CLASS = GenerateLatexTest.class;

    /**
     * Generate main documents.
     *
     * @throws Exception
     */
    public void testGeneration() throws Exception {
        generate(getDocDir(), "math/qedeq_logic_v1.xml", getGenDir(), false, false);
        generate(getDocDir(), "math/qedeq_set_theory_v1.xml", getGenDir(), false, false);
        generate(getDocDir(), "math/qedeq_propositional_v1.xml", getGenDir(), false, false);
        generate(getDocDir(), "sample/qedeq_sample1.xml", getGenDir(), false, false);
        generate(getDocDir(), "sample/qedeq_sample2.xml", getGenDir(), false, false);
        generate(getDocDir(), "sample/qedeq_sample3.xml", getGenDir(), false, false);
        generate(getDocDir(), "project/qedeq_basic_concept.xml", getGenDir(), false, false);
        generate(getDocDir(), "project/qedeq_logic_language.xml", getGenDir(), true, false);
    }

    /**
     * Generate some example documents.
     *
     * @throws Exception
     */
    public void testGeneration2() throws Exception {
        System.setProperty("qedeq.test.xmlLocationFailures", Boolean.TRUE.toString());
        generate(getIndir(), "proof/proof_001.xml", getGenDir(), true, false);
        generate(getIndir(), "proof/proof_002.xml", getGenDir(), true, true);
        System.setProperty("qedeq.test.xmlLocationFailures", Boolean.FALSE.toString());
    }

    /**
     * Generate some example documents.
     *
     * @throws Exception
     */
    public void testNegativeGeneration2() throws Exception {
        System.setProperty("qedeq.test.xmlLocationFailures", Boolean.TRUE.toString());
        try {
            generate(getIndir(), "proof/proof_002.xml", getGenDir(), true, false);
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(8, ex.size());
            SourceFileException e0 = ex.get(0);
            assertTrue(e0.getCause() instanceof ModuleDataException);
            assertEquals(6100017, e0.getErrorCode());
            assertEquals(317, e0.getSourceArea().getStartPosition().getRow());
            assertEquals(24, e0.getSourceArea().getStartPosition().getColumn());
            SourceFileException e1 = ex.get(1);
            assertTrue(e1.getCause() instanceof ModuleDataException);
            assertEquals(610007, e1.getErrorCode());
            assertEquals(339, e1.getSourceArea().getStartPosition().getRow());
            assertEquals(24, e1.getSourceArea().getStartPosition().getColumn());
            SourceFileException e2 = ex.get(2);
            assertTrue(e2.getCause() instanceof ModuleDataException);
            assertEquals(610007, e2.getErrorCode());
            assertEquals(481, e2.getSourceArea().getStartPosition().getRow());
            assertEquals(23, e2.getSourceArea().getStartPosition().getColumn());
            SourceFileException e3 = ex.get(3);
            assertTrue(e3.getCause() instanceof ModuleDataException);
            assertEquals(610007, e3.getErrorCode());
            assertEquals(787, e3.getSourceArea().getStartPosition().getRow());
            assertEquals(24, e3.getSourceArea().getStartPosition().getColumn());
            SourceFileException e4 = ex.get(4);
            assertTrue(e4.getCause() instanceof ModuleDataException);
            assertEquals(610007, e4.getErrorCode());
            assertEquals(847, e4.getSourceArea().getStartPosition().getRow());
            assertEquals(34, e4.getSourceArea().getStartPosition().getColumn());
            SourceFileException e5 = ex.get(5);
            assertTrue(e5.getCause() instanceof ModuleDataException);
            assertEquals(610011, e5.getErrorCode());
            assertEquals(1501, e5.getSourceArea().getStartPosition().getRow());
            assertEquals(34, e5.getSourceArea().getStartPosition().getColumn());
            SourceFileException e6 = ex.get(6);
            assertTrue(e5.getCause() instanceof ModuleDataException);
            assertEquals(610011, e6.getErrorCode());
            assertEquals(1610, e6.getSourceArea().getStartPosition().getRow());
            assertEquals(23, e6.getSourceArea().getStartPosition().getColumn());
            SourceFileException e7 = ex.get(7);
            assertTrue(e7.getCause() instanceof ModuleDataException);
            assertEquals(610011, e7.getErrorCode());
            assertEquals(1610, e7.getSourceArea().getStartPosition().getRow());
            assertEquals(37, e7.getSourceArea().getStartPosition().getColumn());
        } finally {
            System.setProperty("qedeq.test.xmlLocationFailures", Boolean.FALSE.toString());
        }
    }

    public void testNegative02() throws IOException {
        try {
            generate(getIndir(), "qedeq_error_sample_12.xml", "de", new File(getGenDir(), "null"), false);
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof ModuleDataException);
            assertEquals(10002, e.getErrorCode());
            assertEquals(221, e.getSourceArea().getStartPosition().getRow());
            assertEquals(9, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    public void testNegative03() throws IOException {
        try {
            generate(getIndir(), "qedeq_error_sample_13.xml", "en", new File(getGenDir(), "null"), false);
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(315, e.getSourceArea().getStartPosition().getRow());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(315, e.getSourceArea().getEndPosition().getRow());
            assertEquals(30, e.getSourceArea().getEndPosition().getColumn());
        }
    }

    public void testNegative04() throws IOException {
        try {
            generate(getIndir(), "qedeq_error_sample_14.xml", "en", new File(getGenDir(), "null"), false);
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(7, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(13, e.getSourceArea().getStartPosition().getRow());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(13, e.getSourceArea().getEndPosition().getRow());
            assertEquals(13, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(1);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(16, e.getSourceArea().getStartPosition().getRow());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(16, e.getSourceArea().getEndPosition().getRow());
            assertEquals(16, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(2);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(19, e.getSourceArea().getStartPosition().getRow());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(19, e.getSourceArea().getEndPosition().getRow());
            assertEquals(15, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(3);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(22, e.getSourceArea().getStartPosition().getRow());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(22, e.getSourceArea().getEndPosition().getRow());
            assertEquals(15, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(4);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(26, e.getSourceArea().getStartPosition().getRow());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(26, e.getSourceArea().getEndPosition().getRow());
            assertEquals(23, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(5);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(69, e.getSourceArea().getStartPosition().getRow());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(69, e.getSourceArea().getEndPosition().getRow());
            assertEquals(55, e.getSourceArea().getEndPosition().getColumn());
            e = ex.get(6);
            assertTrue(e.getCause() instanceof SAXParseException);
            assertEquals(9001, e.getErrorCode());
            assertEquals(98, e.getSourceArea().getStartPosition().getRow());
            assertEquals(1, e.getSourceArea().getStartPosition().getColumn());
            assertEquals(98, e.getSourceArea().getEndPosition().getRow());
            assertEquals(47, e.getSourceArea().getEndPosition().getColumn());
        }
    }

    public void testNegative05() throws IOException {
        try {
            generate(getIndir(), "qedeq_error_sample_15.xml", "en", new File(getGenDir(), "null"), false);
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30550, e.getErrorCode());
            assertEquals(168, e.getSourceArea().getStartPosition().getRow());
            assertEquals(15, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    public void testNegative06() throws IOException {
        try {
            generate(getIndir(), "qedeq_error_sample_16.xml", "en", new File(getGenDir(), "null"), false);
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30590, e.getErrorCode());
            assertEquals(288, e.getSourceArea().getStartPosition().getRow());
            assertEquals(30, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    public void testNegative07() throws IOException {
        try {
            generate(getIndir(), "qedeq_error_sample_17.xml", "en", new File(getGenDir(), "null"), false);
            fail("IllegalModuleDataException expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof LogicalCheckException);
            assertEquals(30780, e.getErrorCode());
            assertEquals(298, e.getSourceArea().getStartPosition().getRow());
            assertEquals(17, e.getSourceArea().getStartPosition().getColumn());
        }
    }

    public void testNegative08() throws IOException {
        try {
            generate(getIndir(), "qedeq_error_sample_18.xml", "en", new File(getGenDir(), "null"), false);
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList list) {
            DefaultSourceFileExceptionList ex = (DefaultSourceFileExceptionList) list;
            assertEquals(1, ex.size());
            SourceFileException e = ex.get(0);
            assertTrue(e.getCause() instanceof ModuleDataException);
            assertEquals(1001, e.getErrorCode());
            assertEquals(308, e.getSourceArea().getStartPosition().getRow());
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
     * @param   ignoreWarnings          Don't bother about warnings?
     * @throws Exception Failure.
     */
    public void generate(final File dir, final String xml, final File destinationDirectory,
            final boolean onlyEn, final boolean ignoreWarnings) throws Exception {
        generate(dir, xml, "en", destinationDirectory, ignoreWarnings);
        if (!onlyEn) {
            generate(dir, xml, "de", destinationDirectory, ignoreWarnings);
        }
    }

    /**
     * Call the generation of one LaTeX file and copy XML source to same destination directory.
     *
     * @param   dir                     Start directory.
     * @param   xml                     Relative path to XML file. Must not be <code>null</code>.
     * @param   language                Generate text in this language. Can be <code>null</code>.
     * @param   destinationDirectory    Directory path for LaTeX file. Must not be <code>null</code>.
     * @param   ignoreWarnings          Don't bother about warnings?
     * @throws  IOException             File IO failed.
     * @throws  XmlFilePositionException File data is invalid.
     */
    public void generate(final File dir, final String xml, final String language,
            final File destinationDirectory, final boolean ignoreWarnings) throws IOException,
            SourceFileExceptionList {
        final File xmlFile = new File(dir, xml);
        final ModuleAddress address = getServices().getModuleAddress(
            IoUtility.toUrl(xmlFile));
        final KernelQedeqBo prop = (KernelQedeqBo) getServices().loadModule(
            address);
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        getServices().loadRequiredModules(prop.getModuleAddress());
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        getServices().checkModule(prop.getModuleAddress());
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        QedeqBoDuplicateLanguageChecker.check(new Plugin() {
                public String getPluginId() {
                    return QedeqBoDuplicateLanguageChecker.class.getName();
                }
    
                public String getPluginName() {
                    return "duplicate language checker";
                }
    
                public String getPluginDescription() {
                    return "Test for duplicate language entries within LaTeX sections";
                }

            }, prop);
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }

        final String web = "http://www.qedeq.org/"
            + ((KernelProperties) getServices()).getKernelVersionDirectory() + "/doc/" + xml;
        final InternalKernelServices services = (InternalKernelServices) IoUtility.getFieldContent(
            getServices(), "services");
        final ModuleAddress webAddress = new DefaultModuleAddress(web);
        services.getLocalFilePath(webAddress);
        IoUtility.copyFile(xmlFile, services.getLocalFilePath(webAddress));

        getServices().checkModule(webAddress);
        final QedeqBo webBo = getServices().getQedeqBo(webAddress);
        final File texFile = new File(destinationDirectory, xml.substring(0, xml.lastIndexOf('.'))
            + "_" + language + ".tex");
        generate((KernelQedeqBo) webBo, texFile, language, "1");
        final File texCopy = new File(dir, new File(new File(xml).getParent(), texFile.getName())
            .getPath());
        final File xmlCopy = new File(destinationDirectory, xml);
        IoUtility.createNecessaryDirectories(xmlCopy);
        IoUtility.copyFile(xmlFile, xmlCopy);
        IoUtility.copyFile(texFile, texCopy);
        if (webBo.hasErrors()) {
            throw webBo.getErrors();
        }
        if (!ignoreWarnings && webBo.hasWarnings()) {
            throw webBo.getWarnings();
        }
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
            final Map parameters = new HashMap();
            parameters.put("info", "true");
            final InputStream latex =(new Qedeq2LatexExecutor(new Qedeq2LatexPlugin(), prop, parameters)).createLatex(language, "1");
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
            throw new RuntimeException(e);
        } finally {
            Trace.end(CLASS, method);
        }
    }

}
