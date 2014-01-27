/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.basis;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.UrlUtility;
import org.qedeq.base.test.DynamicGetter;
import org.qedeq.base.test.ObjectProxy;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.service.internal.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.test.DummyPlugin;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.handler.common.SaxDefaultHandler;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;

/**
 * For testing QEDEQ generation.
 *
 * @author Michael Meyling
 */
public class QedeqBoFactoryTest extends QedeqBoTestCase {

    /** This class. */
    private static final Class CLASS = QedeqBoFactoryTest.class;

    private Qedeq ok;

    private Qedeq error;

    private File okFile;

    private File errorFile;

    private InternalModuleServiceCall call;

    protected void setUp() throws Exception {
        super.setUp();
        try {
            okFile = getFile("qedeq_sample1.xml");
            errorFile = getFile("qedeq_error_sample_12.xml");
            ok = (Qedeq) ObjectProxy.createProxy(createQedeqFromFile(okFile));
            ok.getHeader().getAuthorList().get(0);
            assertTrue(DynamicGetter.get(ok, "getHeader().getAuthorList().get(0)") instanceof Author);
            ok = (Qedeq) ObjectProxy.createProxy(ok);
            error = (Qedeq) ObjectProxy.createProxy(createQedeqFromFile(errorFile));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            super.tearDown();
            throw e;
        }
    }

    protected void tearDown() throws Exception {
        ok = null;
        error = null;
        endServiceCall(call);
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
     */
    public void testCreateStringQedeq1() throws Exception {
        final String method = "testCreateStringQedeq()";
        final ModuleAddress address = getServices().getModuleAddress(
            UrlUtility.toUrl(errorFile.getCanonicalFile()));
        final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) KernelFacade
            .getKernelContext().getQedeqBo(address);
        try {
            QedeqBoFactoryAssert.createQedeq(prop, error);
            // TODO mime 20080306: move this test to another location, building doesn't include
            // checking any longer
            final ModuleLabelsCreator creator = new ModuleLabelsCreator(DummyPlugin.getInstance(), prop);
            call = createServiceCall("testCreateStringQedeq1", prop);
            final ModuleLabels labels = new ModuleLabels();
            creator.createLabels(call.getInternalServiceProcess(), labels);
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList e) {
            SourceFileException sf = e.get(0);
            assertEquals(221, sf.getSourceArea().getStartPosition().getRow());
            assertEquals(9, sf.getSourceArea().getStartPosition().getColumn());
            assertEquals(267, sf.getSourceArea().getEndPosition().getRow());
            assertEquals(16, sf.getSourceArea().getEndPosition().getColumn());
            Trace.trace(CLASS, this, method, e);
        } finally {
            endServiceCall(call);
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
        loadQedeqAndAssertContext("sample/qedeq_sample1.xml");
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     *
     * @throws Exception    Unexpected failure of module creation.
     */
    public void testCreateStringQedeq5() throws Exception {
        if (slow()) {
            loadQedeqAndAssertContext("math/qedeq_set_theory_v1.xml");
        }
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     *
     * @throws Exception    Unexpected failure of module creation.
     */
    public void testCreateStringQedeq6() throws Exception {
        if (slow()) {
            loadQedeqAndAssertContext("math/qedeq_logic_v1.xml");
        }
    }

    public void loadQedeqAndAssertContext(final String name) throws Exception,
            ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        loadQedeqAndAssertContext(getQedeqFile(name));
    }

    public static final void loadQedeqAndAssertContext(final File file) throws Exception,
            ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
            UrlUtility.toUrl(file.getCanonicalFile()));
        final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) KernelFacade
            .getKernelContext().getQedeqBo(address);
        QedeqBoFactoryAssert.createQedeq(prop, createQedeqFromFile(file));
    }

    public static Qedeq loadQedeq(final String name) throws IOException,
            ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        return loadQedeq(getQedeqFile(name));
    }

    public static Qedeq loadQedeq(final File file) throws ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        return createQedeqFromFile(file);
    }

    public static final Qedeq createQedeqFromFile(final File file)
            throws ParserConfigurationException, SAXException, SourceFileExceptionList {
        SaxDefaultHandler handler = new SaxDefaultHandler(new DummyPlugin());
        QedeqHandler simple = new QedeqHandler(handler);
        handler.setBasisDocumentHandler(simple);
        SaxParser parser = new SaxParser(DummyPlugin.getInstance(), handler);
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
