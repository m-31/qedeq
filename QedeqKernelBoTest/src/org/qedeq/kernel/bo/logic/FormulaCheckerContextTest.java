/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.logic.wf.LogicalCheckException;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.service.ModuleLabelsCreator;
import org.qedeq.kernel.bo.service.QedeqVoBuilder;
import org.qedeq.kernel.bo.service.logic.QedeqBoFormalLogicCheckerExecutor;
import org.qedeq.kernel.bo.service.logic.QedeqBoFormalLogicCheckerPlugin;
import org.qedeq.kernel.bo.test.DummyPlugin;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.xml.dao.XmlQedeqFileDao;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;

/**
 * Test logic tests for QEDEQ modules.
 *
 * @author Michael Meyling
 */
public final class FormulaCheckerContextTest extends QedeqBoTestCase {

    /** This class. */
    private static final Class CLASS = FormulaChecker.class;


    public FormulaCheckerContextTest() {
        super();
    }

    public FormulaCheckerContextTest(final String name) {
        super(name);
    }

    public void testNegative00() throws Exception {
        try {
            check(getFile("qedeq_error_sample_00.xml"));
            fail("DefaultSourceFileExceptionList expected");
        } catch (DefaultSourceFileExceptionList ex) {
            Trace.trace(CLASS, this, "testNegative00", ex);
            assertEquals(1, ex.size());
            final SourceFileException check = ex.get(0);
            // check.printStackTrace();
            assertEquals(9001, check.getErrorCode());
            assertEquals(36, check.getSourceArea().getStartPosition().getRow());
            assertEquals(1, check.getSourceArea().getStartPosition().getColumn());
            assertEquals(36, check.getSourceArea().getEndPosition().getRow());
            assertEquals(20, check.getSourceArea().getEndPosition().getColumn());
        }
    }

    public void testNegative01() throws Exception {
        try {
            check(getFile("qedeq_error_sample_01.xml"));
            fail("DefaultSourceFileExceptionList expected");
        } catch (DefaultSourceFileExceptionList ex) {
            Trace.trace(CLASS, this, "testNegative01", ex);
            assertEquals(1, ex.size());
            final SourceFileException check = ex.get(0);
            assertEquals(9001, check.getErrorCode());
            assertEquals(39, check.getSourceArea().getStartPosition().getRow());
            assertEquals(1, check.getSourceArea().getStartPosition().getColumn());
            assertEquals(39, check.getSourceArea().getEndPosition().getRow());
            assertEquals(35, check.getSourceArea().getEndPosition().getColumn());
        }
    }

    public void testNegative02() throws Exception {
        try {
            check(getFile("qedeq_error_sample_02.xml"));
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
            check(getFile("qedeq_error_sample_03.xml"));
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
            checkViaKernel(getFile("qedeq_error_sample_04.xml"));
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

    public void testPositive01() throws Exception {
        checkViaKernel(getDocFile("math/qedeq_logic_v1.xml"));
    }

    public void testPositive02() throws Exception {
        checkViaKernel(getDocFile("math/qedeq_sample1.xml"));
    }

    public void testPositive03() throws Exception {
        checkViaKernel(getDocFile("math/qedeq_sample2.xml"));
    }

    public void testPositive04() throws Exception {
        checkViaKernel(getDocFile("math/qedeq_set_theory_v1.xml"));
    }

    public void testPositive05() throws Exception {
        checkViaKernel(getDocFile("project/qedeq_basic_concept.xml"));
    }

    public void testPositive06() throws Exception {
        checkViaKernel(getDocFile("project/qedeq_logic_language.xml"));
    }

    /**
     * Check logic of QEDEQ module given as XML file.
     *
     * @param   xmlFile Module file to check.
     */
    public static void check(final File xmlFile) throws IOException,
            ParserConfigurationException, SAXException, ModuleDataException,
            SourceFileExceptionList, NoSuchFieldException {
        final ModuleAddress context = KernelFacade.getKernelContext().getModuleAddress(
            IoUtility.toUrl(xmlFile.getAbsoluteFile()));
        SaxDefaultHandler handler = new SaxDefaultHandler(new DummyPlugin());
        QedeqHandler simple = new QedeqHandler(handler);
        handler.setBasisDocumentHandler(simple);
        SaxParser parser = new SaxParser(DummyPlugin.getInstance(), handler);
        parser.parse(xmlFile, xmlFile.getPath());
        final QedeqVo qedeq = (QedeqVo) simple.getQedeq();
        final InternalKernelServices services = (InternalKernelServices) IoUtility
            .getFieldContent(KernelFacade.getKernelContext(), "services");
        final QedeqFileDao loader = new XmlQedeqFileDao();
        loader.setServices(services);
        final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) KernelFacade
            .getKernelContext().getQedeqBo(context);
        prop.setQedeqFileDao(loader);
        IoUtility.setFieldContent(prop, "qedeq", qedeq);
        final ModuleLabelsCreator creator = new ModuleLabelsCreator(DummyPlugin.getInstance(),
            prop);
        prop.setLoaded(QedeqVoBuilder.createQedeq(prop.getModuleAddress(), qedeq),
            creator.getLabels(), creator.getConverter());
        prop.setLoadedRequiredModules(new KernelModuleReferenceList());
        final QedeqBoFormalLogicCheckerPlugin plugin = new QedeqBoFormalLogicCheckerPlugin();
        final Map parameters = new HashMap();
        parameters.put("checkerFactory", TestFormulaCheckerFactoryImpl.class.getName());
        final QedeqBoFormalLogicCheckerExecutor checker = (QedeqBoFormalLogicCheckerExecutor) plugin.createExecutor(prop, parameters);
        checker.executePlugin();
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
    }

    /**
     * Check logic of QEDEQ module given as XML file.
     *
     * @param   xmlFile Module file to check.
     */
    public static void checkViaKernel(final File xmlFile) throws NoSuchFieldException, SourceFileExceptionList, IOException {
        final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
            IoUtility.toUrl(xmlFile.getAbsoluteFile()));
        KernelQedeqBo qedeqBo= (KernelQedeqBo) KernelFacade.getKernelContext().loadModule(address);
        final QedeqBoFormalLogicCheckerPlugin plugin = new QedeqBoFormalLogicCheckerPlugin();
        final Map parameters = new HashMap();
        parameters.put("checkerFactory", TestFormulaCheckerFactoryImpl.class.getName());
        final QedeqBoFormalLogicCheckerExecutor checker = (QedeqBoFormalLogicCheckerExecutor) plugin.createExecutor(qedeqBo, parameters);
        checker.executePlugin();
        if (qedeqBo.hasErrors()) {
            throw qedeqBo.getErrors();
        }
    }

}
