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

package org.qedeq.kernel.bo.logic.wf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.UrlUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.logic.common.FormulaChecker;
import org.qedeq.kernel.bo.logic.common.LogicalCheckException;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.basis.ModuleLabelsCreator;
import org.qedeq.kernel.bo.service.basis.QedeqVoBuilder;
import org.qedeq.kernel.bo.service.internal.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.service.internal.Element2LatexImpl;
import org.qedeq.kernel.bo.service.internal.Element2Utf8Impl;
import org.qedeq.kernel.bo.service.logic.WellFormedCheckerExecutor;
import org.qedeq.kernel.bo.service.logic.WellFormedCheckerPlugin;
import org.qedeq.kernel.bo.test.DummyPlugin;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.module.QedeqVo;
import org.qedeq.kernel.se.visitor.InterruptException;
import org.qedeq.kernel.xml.dao.XmlQedeqFileDao;
import org.qedeq.kernel.xml.handler.common.SaxDefaultHandler;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
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
    private InternalModuleServiceCall call;


    protected void tearDown() throws Exception {
        endServiceCall(call);
        super.tearDown();
    }

    public void testNegative00() throws Exception {
        try {
            check(getFile("qedeq_error_sample_00.xml"));
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList ex) {
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
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList ex) {
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
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList sfl) {
            Trace.trace(CLASS, this, "testNegative02", sfl);
            final Exception e = (Exception) sfl.get(0).getCause();
            Trace.param(CLASS, this, "testNegative02", "name", e.getClass().getName());
            assertTrue(e instanceof LogicalCheckException);
            final LogicalCheckException check = (LogicalCheckException) e;
            assertEquals(30550, check.getErrorCode());
            assertEquals("getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(0).getNode().getNodeType().getAxiom().getFormula().getElement().getList().getElement(1).getList()", check.getContext().getLocationWithinModule());
            assertNull(check.getReferenceContext());
        }
    }

    public void testNegative03() throws Exception {
        try {
            check(getFile("qedeq_error_sample_03.xml"));
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList sfl) {
            Trace.trace(CLASS, this, "testNegative03", sfl);
            final Exception e = (Exception) sfl.get(0).getCause();
            assertTrue(e instanceof LogicalCheckException);
            final LogicalCheckException check = (LogicalCheckException) e;
            assertEquals(30770, check.getErrorCode());
            assertEquals("getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(0).getNode().getNodeType().getAxiom().getFormula().getElement().getList().getElement(1)", check.getContext().getLocationWithinModule());
            assertNull(check.getReferenceContext());
        }
    }

    public void testNegative04() throws Exception {
        try {
            checkViaKernel(getFile("qedeq_error_sample_04.xml"));
            fail("SourceFileExceptionList expected");
        } catch (SourceFileExceptionList sfl) {
            Trace.trace(CLASS, this, "testNegative04", sfl);
            final Exception e = (Exception) sfl.get(0).getCause();
            assertTrue(e instanceof LogicalCheckException);
            final LogicalCheckException check = (LogicalCheckException) e;
            assertEquals(30780, check.getErrorCode());
            assertEquals("getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(0).getNode().getNodeType().getAxiom().getFormula().getElement().getList().getElement(1)", check.getContext().getLocationWithinModule());
            assertNull(check.getReferenceContext());
        }
    }

    public void testPositive01() throws Exception {
        checkViaKernel(getDocFile("math/qedeq_logic_v1.xml"));
    }

    public void testPositive02() throws Exception {
        checkViaKernel(getDocFile("sample/qedeq_sample1.xml"));
    }

    public void testPositive03() throws Exception {
        checkViaKernel(getDocFile("sample/qedeq_sample2.xml"));
    }

    public void testPositive03b() throws Exception {
        checkViaKernel(getDocFile("sample/qedeq_sample3.xml"));
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
    public void check(final File xmlFile) throws IOException,
            ParserConfigurationException, SAXException, ModuleDataException,
            SourceFileExceptionList, NoSuchFieldException {
        final ModuleAddress context = getServices().getModuleAddress(
            UrlUtility.toUrl(xmlFile.getAbsoluteFile()));
        SaxDefaultHandler handler = new SaxDefaultHandler(new DummyPlugin());
        QedeqHandler simple = new QedeqHandler(handler);
        handler.setBasisDocumentHandler(simple);
        SaxParser parser = new SaxParser(DummyPlugin.getInstance(), handler);
        parser.parse(xmlFile, xmlFile.getPath());
        final QedeqVo qedeq = (QedeqVo) simple.getQedeq();
        final QedeqFileDao loader = new XmlQedeqFileDao();
        loader.setServices(getInternalServices());
        final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) KernelFacade
            .getKernelContext().getQedeqBo(context);
        prop.setQedeqFileDao(loader);
        YodaUtility.setFieldContent(prop, "qedeq", qedeq);
        final ModuleLabelsCreator creator = new ModuleLabelsCreator(DummyPlugin.getInstance(),
            prop);
        call = createServiceCall("check", prop);
        final ModuleLabels labels = new ModuleLabels();
        final Element2LatexImpl converter = new Element2LatexImpl(labels);
        final Element2Utf8 textConverter = new Element2Utf8Impl(converter);
        creator.createLabels(call.getInternalServiceProcess(), labels);
        prop.setLoaded(QedeqVoBuilder.createQedeq(prop.getModuleAddress(), qedeq),
            labels, converter, textConverter);
        prop.setLoadedImports(new KernelModuleReferenceList());
        prop.setLoadedRequiredModules();
        final WellFormedCheckerPlugin plugin = new WellFormedCheckerPlugin();
        final Map parameters = new HashMap();
        parameters.put("checkerFactory", TestFormulaCheckerFactoryImpl.class.getName());
        final WellFormedCheckerExecutor checker = (WellFormedCheckerExecutor) plugin.createExecutor(
            prop, new Parameters(parameters));
        checker.executePlugin(call, null);
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
    }

    /**
     * Check logic of QEDEQ module given as XML file.
     *
     * @param   xmlFile Module file to check.
     */
    public void checkViaKernel(final File xmlFile) throws SourceFileExceptionList, IOException, InterruptException {
        final ModuleAddress address = getServices().getModuleAddress(
            UrlUtility.toUrl(xmlFile.getAbsoluteFile()));
        KernelQedeqBo qedeqBo= (KernelQedeqBo) getServices().loadModule(address);
        final WellFormedCheckerPlugin plugin = new WellFormedCheckerPlugin();
        final Map parameters = new HashMap();
        parameters.put("checkerFactory", TestFormulaCheckerFactoryImpl.class.getName());
        final WellFormedCheckerExecutor checker = (WellFormedCheckerExecutor) plugin.createExecutor(
            qedeqBo, new Parameters(parameters));
        call = createServiceCall("check", qedeqBo);
        checker.executePlugin(call, null);
        if (qedeqBo.hasErrors()) {
//            qedeqBo.getErrors().get(0).printStackTrace(System.out);
            throw qedeqBo.getErrors();
        }
    }

}
