/* $Id: QedeqBoFactoryTest.java,v 1.20 2007/12/21 23:35:17 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Author;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.module.IllegalModuleDataException;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.rel.test.text.KernelFacade;
import org.qedeq.kernel.test.DynamicGetter;
import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationFinder;
import org.xml.sax.SAXException;

/**
 * For testing QEDEQ generation.
 *
 * @version $Revision: 1.20 $
 * @author Michael Meyling
 */
public class QedeqBoFactoryTest extends QedeqTestCase {

    private Qedeq ok;

    private Qedeq error;

    private static File okFile = new File("data/qedeq_sample1.xml");

    private static File errorFile = new File("data/qedeq_sample2_error.xml");

    protected void setUp() throws Exception {
        super.setUp();
        KernelFacade.startup();
        ok = (Qedeq) QedeqProxy.createProxy(createQedeqFromFile(okFile));
        ok.getHeader().getAuthorList().get(0);
        assertTrue(DynamicGetter.get(ok, "getHeader().getAuthorList().get(0)") instanceof Author);
        ok = (Qedeq) QedeqProxy.createProxy(ok);
        error = (Qedeq) QedeqProxy.createProxy(createQedeqFromFile(errorFile));
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
    public final void testCreateStringQedeq1() throws IOException, ParserConfigurationException,
            SAXException, ModuleDataException {
        final String method = "testCreateStringQedeq()";
        try {
            QedeqBoFactoryAssert.createQedeq(IoUtility.toUrl(errorFile.getCanonicalFile()), error);
            fail("IllegalModuleDataException expected");
        } catch (IllegalModuleDataException e) {
            System.err.println(e);
            System.err.println(e.getContext());
            final SimpleXPath xpath = Context2SimpleXPath.getXPath(e.getContext(), error);
            final SimpleXPath find = XPathLocationFinder.getXPathLocation(
                errorFile.getCanonicalFile(), xpath, IoUtility.toUrl(errorFile.getCanonicalFile()));
            System.out.println("found: " + find.getStartLocation());
            System.out.println("found: " + find.getEndLocation());
            assertEquals(221, find.getStartLocation().getLine());
            assertEquals(9, find.getStartLocation().getColumn());
            assertEquals(265, find.getEndLocation().getLine());
            assertEquals(16, find.getEndLocation().getColumn());
            Trace.trace(this, method, e);
        }
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     * 
     * @throws Exception    Unexpected failure of module creation. 
     */
    public final void testCreateStringQedeq2() throws Exception {
        loadQedeqAndAssertContext("project/qedeq_basic_concept.xml");
    }

    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     * 
     * @throws Exception    Unexpected failure of module creation. 
     */
    public final void testCreateStringQedeq3() throws Exception {
        loadQedeqAndAssertContext("project/qedeq_logic_language.xml");
    }
    
    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     * 
     * @throws Exception    Unexpected failure of module creation. 
     */
    public final void testCreateStringQedeq4() throws Exception {
        loadQedeqAndAssertContext("math/qedeq_sample1.xml");
    }
    
    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     * 
     * @throws Exception    Unexpected failure of module creation. 
     */
    public final void testCreateStringQedeq5() throws Exception {
        loadQedeqAndAssertContext("math/qedeq_set_theory_v1.xml");
    }
    
    /**
     * Class under test for QedeqBo createQedeq(String, Qedeq).
     * 
     * @throws Exception    Unexpected failure of module creation. 
     */
    public final void testCreateStringQedeq6() throws Exception {
        loadQedeqAndAssertContext("math/qedeq_logic_v1.xml");
    }

    public static final void loadQedeqAndAssertContext(final String name) throws IOException,
            ModuleDataException, ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        loadQedeqAndAssertContext(getQedeqFile(name));
    }
        
    public static final void loadQedeqAndAssertContext(final File file) throws IOException,
            ModuleDataException, ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        QedeqBoFactoryAssert.createQedeq(IoUtility.toUrl(file.getAbsoluteFile()),
            createQedeqFromFile(file));
    }
    
    public static final Qedeq loadQedeq(final String name) throws IOException,
            IllegalModuleDataException, ParserConfigurationException, SAXException,
            SourceFileExceptionList {
        return loadQedeq(getQedeqFile(name));
    }
    
    public static final Qedeq loadQedeq(final File file) throws IOException,
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
    public static final File getQedeqFile(final String relativePath) throws IOException {
        File docDir = new File("../QedeqDoc");
        // test if we are in the normal development environment, where a project with name
        //  "../QedeqDoc" exists, otherwise we assume to run within the release directory
        //  structure where the docs are in the directory ../doc
        if (!docDir.exists()) {
            docDir = new File("../doc");
            if (!docDir.exists()) {
                throw new IOException("unknown source directory for qedeq modules");
            }
        }
        final File qedeqFile = new File(docDir, relativePath);
        return qedeqFile;
    }

}
