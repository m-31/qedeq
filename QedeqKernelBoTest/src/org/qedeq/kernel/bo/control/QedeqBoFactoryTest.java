/* $Id: QedeqBoFactoryTest.java,v 1.1 2008/07/26 07:59:15 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.test.DynamicGetter;
import org.qedeq.base.test.ObjectProxy;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.Author;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.service.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.service.ModuleLabelsCreator;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.common.IllegalModuleDataException;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;

/**
 * For testing QEDEQ generation.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class QedeqBoFactoryTest extends QedeqTestCase {

    /** This class. */
    private static final Class CLASS = QedeqBoFactoryTest.class;

    private Qedeq ok;

    private Qedeq error;

    private File okFile;

    private File errorFile;

    protected void setUp() throws Exception {
        super.setUp();
        KernelFacade.startup();
        try {
            okFile = getFile("qedeq_sample1.xml");
            errorFile = getFile("qedeq_sample2_error.xml");
            ok = (Qedeq) ObjectProxy.createProxy(createQedeqFromFile(okFile));
            ok.getHeader().getAuthorList().get(0);
            assertTrue(DynamicGetter.get(ok, "getHeader().getAuthorList().get(0)") instanceof Author);
            ok = (Qedeq) ObjectProxy.createProxy(ok);
            error = (Qedeq) ObjectProxy.createProxy(createQedeqFromFile(errorFile));
        } catch (Exception e) {
            KernelFacade.shutdown();
        }
    }

    protected void tearDown() throws Exception {
        KernelFacade.shutdown();
        ok = null;
        error = null;
        super.tearDown();
    }

    public QedeqBoFactoryTest() {
        super();
    }

    public QedeqBoFactoryTest(final String name) {
        super(name);
    }

    /**
     * Class under test for QedeqBo create(String, Qedeq).
     *
     * @throws IOException  Module creation failed due to IO error.
     * @throws SAXException Module parsing failed.
     * @throws ParserConfigurationException Parser configuration problem.
     */
    public void testCreateStringQedeq1() throws IOException, ParserConfigurationException,
            SAXException, ModuleDataException, SourceFileExceptionList {
        final String method = "testCreateStringQedeq()";
        final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
            IoUtility.toUrl(errorFile.getCanonicalFile()));
        final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) KernelFacade
            .getKernelContext().getQedeqBo(address);
        try {
            QedeqBoFactoryAssert.createQedeq(prop, error);
            // TODO mime 20080306: move this test to another location, building doesn't include
            // checking any longer
            final ModuleLabelsCreator creator = new ModuleLabelsCreator(prop);
            creator.createLabels();
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList e) {
            SourceFileException sf = e.get(0);
            assertEquals(221, sf.getSourceArea().getStartPosition().getLine());
            assertEquals(9, sf.getSourceArea().getStartPosition().getColumn());
            assertEquals(265, sf.getSourceArea().getEndPosition().getLine());
            assertEquals(16, sf.getSourceArea().getEndPosition().getColumn());
            Trace.trace(CLASS, this, method, e);
        }
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     *
     * @throws Exception    Unexpected failure of module creation.
     */
    public void testCreateStringQedeq2() throws Exception {
        loadQedeqAndAssertContext("project/qedeq_basic_concept.xml");
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     *
     * @throws Exception    Unexpected failure of module creation.
     */
    public void testCreateStringQedeq3() throws Exception {
        loadQedeqAndAssertContext("project/qedeq_logic_language.xml");
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     *
     * @throws Exception    Unexpected failure of module creation.
     */
    public void testCreateStringQedeq4() throws Exception {
        loadQedeqAndAssertContext("math/qedeq_sample1.xml");
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     *
     * @throws Exception    Unexpected failure of module creation.
     */
    public void testCreateStringQedeq5() throws Exception {
        loadQedeqAndAssertContext("math/qedeq_set_theory_v1.xml");
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     *
     * @throws Exception    Unexpected failure of module creation.
     */
    public void testCreateStringQedeq6() throws Exception {
        loadQedeqAndAssertContext("math/qedeq_logic_v1.xml");
    }

    public void loadQedeqAndAssertContext(final String name) throws IOException,
            ModuleDataException, ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        loadQedeqAndAssertContext(getQedeqFile(name));
    }

    public static final void loadQedeqAndAssertContext(final File file) throws IOException,
            ModuleDataException, ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
            IoUtility.toUrl(file.getCanonicalFile()));
        final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) KernelFacade
            .getKernelContext().getQedeqBo(address);
        QedeqBoFactoryAssert.createQedeq(prop, createQedeqFromFile(file));
    }

    public static Qedeq loadQedeq(final String name) throws IOException,
            IllegalModuleDataException, ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        return loadQedeq(getQedeqFile(name));
    }

    public static Qedeq loadQedeq(final File file) throws IOException,
            ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        return createQedeqFromFile(file);
    }

    public static final Qedeq createQedeqFromFile(final File file)
            throws ParserConfigurationException, SAXException, IOException,
            SourceFileExceptionList {
        SaxDefaultHandler handler = new SaxDefaultHandler();
        QedeqHandler simple = new QedeqHandler(handler);
        handler.setBasisDocumentHandler(simple);
        SaxParser parser = new SaxParser(handler);
        parser.parse(file, null);
        return simple.getQedeq();
    }

    /**
     * Get QEDEQ file.
     *
     * @param   relativePath    File path relative to documentation directory.
     *
     * @return  Path to file.
     * @throws  IOException IO-Failure.
     */
    public static File getQedeqFile(final String relativePath) throws IOException {
        File docDir = new File("../QedeqDoc");
        // test if we are in the normal development environment, where a project with name
        //  "../QedeqDoc" exists, otherwise we assume to run within the release directory
        //  structure where the docs are in the directory ../doc
        if (!docDir.exists()) {
            docDir = new File("../doc");
            if (!docDir.exists()) {
                docDir = (new QedeqTestCase(){ }).getFile("doc");
                if (!docDir.exists()) {
                    throw new IOException("unknown source directory for QEDEQ modules");
                }
            }
        }
        final File qedeqFile = new File(docDir, relativePath);
        return qedeqFile;
    }

}
