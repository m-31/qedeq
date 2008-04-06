/* $Id: CheckLogicTest.java,v 1.7 2008/03/27 05:12:43 m31 Exp $
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

package org.qedeq.kernel.bo.logic;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.bo.control.InternalKernelServices;
import org.qedeq.kernel.bo.control.KernelModuleReferenceList;
import org.qedeq.kernel.bo.control.KernelQedeqBo;
import org.qedeq.kernel.bo.control.ModuleLabelsCreator;
import org.qedeq.kernel.bo.control.ModuleLoader;
import org.qedeq.kernel.bo.control.QedeqBoFormalLogicChecker;
import org.qedeq.kernel.bo.load.QedeqVoBuilder;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.test.KernelFacade;
import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.loader.XmlModuleLoader;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;

/**
 * Test generating LaTeX files for all known samples.
 * 
 * @version $Revision: 1.7 $
 * @author Michael Meyling
 */
public final class CheckLogicTest extends QedeqTestCase {

    /** This class. */
    private static final Class CLASS = FormulaChecker.class;


    public CheckLogicTest() {
        super();
    }
    
    public CheckLogicTest(final String name) {
        super(name);
    }

    public void setUp() {
       KernelFacade.startup(); 
    }
    
    public void tearDown() {
        KernelFacade.shutdown();
    }
    
    public void testNegative00() throws Exception {
        try {
            generate(new File("."), "data/qedeq_error_sample_00.xml");
            fail("DefaultSourceFileExceptionList expected");
        } catch (DefaultSourceFileExceptionList ex) {
            Trace.trace(CLASS, this, "testNegative00", ex);
            assertEquals(1, ex.size());
            final SourceFileException check = ex.get(0);
            // check.printStackTrace();
            assertEquals(9001, check.getErrorCode());
            assertEquals(36, check.getSourceArea().getStartPosition().getLine());
            assertEquals(20, check.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative01() throws Exception {
        try {
            generate(new File("."), "data/qedeq_error_sample_01.xml");
            fail("DefaultSourceFileExceptionList expected");
        } catch (DefaultSourceFileExceptionList ex) {
            Trace.trace(CLASS, this, "testNegative01", ex);
            assertEquals(1, ex.size());
            final SourceFileException check = ex.get(0);
            assertEquals(9001, check.getErrorCode());
            assertEquals(39, check.getSourceArea().getStartPosition().getLine());
            assertEquals(35, check.getSourceArea().getStartPosition().getColumn());
        }
    }
    
    public void testNegative02() throws Exception {
        try {
            generate(new File("."), "data/qedeq_error_sample_02.xml");
            fail("ModuleDataException expected");
        } catch (SourceFileExceptionList sfl) {
            Trace.trace(CLASS, this, "testNegative02", sfl);
            final Exception e = (Exception) sfl.get(0).getCause();
            Trace.param(CLASS, this, "testNegative02", "name", e.getClass().getName());
            assertTrue(e instanceof LogicalCheckException);
            final LogicalCheckException check = (LogicalCheckException) e;
            assertEquals(30550, check.getErrorCode());
            assertEquals("getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(0).getNodeType().getAxiom().getFormula().getElement().getList().getElement(1).getList()", check.getContext().getLocationWithinModule());
            assertNull(check.getReferenceContext());
        }
    }
    
    public void testNegative03() throws Exception {
        try {
            generate(new File("."), "data/qedeq_error_sample_03.xml");
            fail("ModuleDataException expected");
        } catch (SourceFileExceptionList sfl) {
            Trace.trace(CLASS, this, "testNegative03", sfl);
            final Exception e = (Exception) sfl.get(0).getCause();
            assertTrue(e instanceof LogicalCheckException);
            final LogicalCheckException check = (LogicalCheckException) e;
            assertEquals(30770, check.getErrorCode());
            assertEquals("getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(0).getNodeType().getAxiom().getFormula().getElement().getList().getElement(1)", check.getContext().getLocationWithinModule());
            assertNull(check.getReferenceContext());
        }
    }
    
    public void testNegative04() throws Exception {
        try {
            generate(new File("."), "data/qedeq_error_sample_04.xml");
            fail("ModuleDataException expected");
        } catch (SourceFileExceptionList sfl) {
            Trace.trace(CLASS, this, "testNegative04", sfl);
            final Exception e = (Exception) sfl.get(0).getCause();
            assertTrue(e instanceof LogicalCheckException);
            final LogicalCheckException check = (LogicalCheckException) e;
            assertEquals(30780, check.getErrorCode());
            assertEquals("getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(0).getNodeType().getAxiom().getFormula().getElement().getList().getElement(1)", check.getContext().getLocationWithinModule());
            assertNull(check.getReferenceContext());
        }
    }

    /**
     * Call the generation of one LaTeX file and copy XML source to same destination directory.
     *
     * @param   dir         Start directory.
     * @param   xml         Relative path to XML file. Must not be <code>null</code>.
     */
    private static void generate(final File dir, final String xml) throws IOException, 
            ParserConfigurationException, SAXException, ModuleDataException,
            SourceFileExceptionList {
        final File xmlFile = new File(dir, xml).getAbsoluteFile();
        final ModuleAddress context = KernelFacade.getKernelContext().getModuleAddress(
            IoUtility.toUrl(xmlFile.getAbsoluteFile()));
        SaxDefaultHandler handler = new SaxDefaultHandler();
        QedeqHandler simple = new QedeqHandler(handler);
        handler.setBasisDocumentHandler(simple);
        SaxParser parser = new SaxParser(handler);
        parser.parse(xmlFile, null);
        final QedeqVo qedeq = (QedeqVo) simple.getQedeq();
        final InternalKernelServices services = (InternalKernelServices) IoUtility
            .getFieldContent(KernelFacade.getKernelContext(), "services");
        final ModuleLoader loader = new XmlModuleLoader();
        loader.setServices(services);
        final KernelQedeqBo prop = (KernelQedeqBo) KernelFacade
            .getKernelContext().getQedeqBo(context);
        prop.setLoader(loader);
        IoUtility.setFieldContent(prop, "qedeq", qedeq);
        prop.setLoaded(QedeqVoBuilder.createQedeq(prop.getModuleAddress(), qedeq),
            new ModuleLabelsCreator(prop).createLabels());
        prop.setLoadedRequiredModules(new KernelModuleReferenceList());
        QedeqBoFormalLogicChecker.check((KernelQedeqBo) prop);
    }

}
