/* $Id: CheckLogicTest.java,v 1.5 2007/12/21 23:35:17 m31 Exp $
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

package org.qedeq.kernel.bo.logic;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.control.QedeqBoFormalLogicChecker;
import org.qedeq.kernel.bo.load.QedeqBoFactory;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.rel.test.text.KernelFacade;
import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;

/**
 * Test generating LaTeX files for all known samples.
 * 
 * @version $Revision: 1.5 $
 * @author Michael Meyling
 */
public final class CheckLogicTest extends QedeqTestCase {

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
            System.out.println(ex);
            assertEquals(1, ex.size());
            final SourceFileException check = ex.get(0);
            check.printStackTrace();
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
            System.out.println(ex);
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
        } catch (ModuleDataException e) {
            System.out.println(e.getClass().getName());
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
        } catch (ModuleDataException e) {
            System.out.println(e.getClass().getName());
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
        } catch (ModuleDataException e) {
            System.out.println(e.getClass().getName());
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
        Qedeq qedeq = simple.getQedeq();
        final QedeqBo qedeqBo = QedeqBoFactory.createQedeq(context, qedeq);
        QedeqBoFormalLogicChecker.check(context, qedeqBo);
    }

}
