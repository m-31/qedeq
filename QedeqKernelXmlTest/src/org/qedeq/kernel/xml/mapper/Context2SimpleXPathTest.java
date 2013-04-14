/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.mapper;

import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;

/**
 * Test {@link org.qedeq.kernel.xml.mapper.Context2XPath}.
 *
 * @author  Michael Meyling
 */
public class Context2SimpleXPathTest extends QedeqBoTestCase {

    private KernelQedeqBo qedeq;

    protected void setUp() throws Exception {
        super.setUp();
        final ModuleAddress address = new DefaultModuleAddress(getFile("mapper/Context2SimpleXPath001.xml"));
        getServices().loadModule(address);
        qedeq = getInternalServices().getKernelQedeqBo(address);
        assertEquals(0, qedeq.getErrors().size());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetXPath01() throws ModuleDataException {
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress());
        assertEquals("/QEDEQ", Context2SimpleXPath.getXPath(find, qedeq.getQedeq()).toString());
    }

    public void testGetXPath02() throws ModuleDataException {
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress(), "getChapterList().get(0)");
        assertEquals("/QEDEQ/CHAPTER", Context2SimpleXPath.getXPath(find, qedeq.getQedeq()).toString());
    }

    public void testGetXPath03() throws ModuleDataException {
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress(), "getHeader()");
        assertEquals("/QEDEQ/HEADER", Context2SimpleXPath.getXPath(find, qedeq.getQedeq()).toString());
    }

    public void testGetXPath04() throws ModuleDataException {
        assertNotNull(qedeq.getQedeq().getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(2).getNode());
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress(), "getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(2).getNode()");
        assertEquals("/QEDEQ/CHAPTER/SECTION/SUBSECTIONS/NODE[3]", Context2SimpleXPath.getXPath(find, qedeq.getQedeq()).toString());
    }

    public void testGetXPath05() throws ModuleDataException {
        assertNotNull(qedeq.getQedeq().getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(2));
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress(), "getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(2)");
        assertEquals("/QEDEQ/CHAPTER/SECTION/SUBSECTIONS/NODE[3]", Context2SimpleXPath.getXPath(find, qedeq.getQedeq()).toString());
    }

    public void testGetXPath06() throws ModuleDataException {
        assertNotNull(qedeq.getQedeq().getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(2).getNode().getNodeType().getPredicateDefinition().getFormula().getElement().getList());
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress(), "getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(2).getNode().getNodeType().getPredicateDefinition().getFormula().getElement().getList()");
        assertEquals("/QEDEQ/CHAPTER/SECTION/SUBSECTIONS/NODE[3]/DEFINITION_PREDICATE/FORMULA/EQUI", Context2SimpleXPath.getXPath(find, qedeq.getQedeq()).toString());
    }

    public void testGetXPath07() throws ModuleDataException {
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress(), "getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(2).getNode().getNodeType().getPredicateDefinition().getFormula().getElement().getAtom()");
        assertEquals("/QEDEQ/CHAPTER/SECTION/SUBSECTIONS/NODE[3]/DEFINITION_PREDICATE/FORMULA", Context2SimpleXPath.getXPath(find, qedeq.getQedeq()).toString());
    }

    public void testGetXPath08() {
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress(), "getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(99).getNode().getNodeType()");
        try {
            Context2SimpleXPath.getXPath(find, qedeq.getQedeq());
            fail("ModuleDataException expected");
        } catch (ModuleDataException e) {
            // expected
        }
    }

    public void testGetXPath09() throws ModuleDataException {
        final ModuleContext find = new ModuleContext(qedeq.getModuleAddress(), "getChapterList().get(0).getSectionList().get(0).getSubsectionList().get(2).getNode().getNodeType().getUnknown()");
        assertEquals("/QEDEQ/CHAPTER/SECTION/SUBSECTIONS/NODE[3]",
            Context2SimpleXPath.getXPath(find, qedeq.getQedeq()).toString());
    }


}
