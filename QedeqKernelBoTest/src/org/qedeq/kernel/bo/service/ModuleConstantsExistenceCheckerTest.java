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
package org.qedeq.kernel.bo.service;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.common.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * For testing of checking existence of module constants.
 *
 * @author  Michael Meyling
 */
public class ModuleConstantsExistenceCheckerTest extends QedeqTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        KernelFacade.startup();
    }

    protected void tearDown() throws Exception {
        KernelFacade.shutdown();
        super.tearDown();
    }

    public ModuleConstantsExistenceCheckerTest() {
        super();
    }

    public ModuleConstantsExistenceCheckerTest(final String name) {
        super(name);
    }

    /**
     * Load following dependencies:
     * <pre>
     * 011 -> 012
     * 011 -> 012
     * </pre>
     * In <code>012</code> the identity operator is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_01() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC011.xml"));
        if (!KernelContext.getInstance().checkModule(address)) {
            KernelContext.getInstance().getQedeqBo(address).getErrors().printStackTrace(System.out);
            throw KernelContext.getInstance().getQedeqBo(address).getErrors();
        }
        SourceFileExceptionList errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
        SourceFileExceptionList warnings = KernelContext.getInstance().getQedeqBo(address).getWarnings();
        assertNull(warnings);
        assertNull(errors);
    }

    /**
     * Load following dependencies:
     * <pre>
     * 021 -> 022
     * 021 -> 023
     * </pre>
     * In <code>022</code> and <code>023</code> the identity operator is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_02() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC021.xml"));
        if (!KernelContext.getInstance().checkModule(address)) {
            SourceFileExceptionList errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = KernelContext.getInstance().getQedeqBo(address).getWarnings();
            assertNull(warnings);
            assertEquals(1, errors.size());
            assertEquals(123476, errors.get(0).getErrorCode());
            assertEquals(38, errors.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(15, errors.get(0).getSourceArea().getStartPosition().getColumn());
        } else {
            fail("failure for double definition of identity operator expected");
        }
    }

    /**
     * Load following dependencies:
     * <pre>
     * 031 -> 032
     * 031 -> 033 -> 034
     * </pre>
     * In <code>032</code> and <code>034</code> the identity operator is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_03() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC031.xml"));
        if (!KernelContext.getInstance().checkModule(address)) {
            SourceFileExceptionList errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = KernelContext.getInstance().getQedeqBo(address).getWarnings();
            assertNull(warnings);
            assertEquals(1, errors.size());
            assertEquals(123476, errors.get(0).getErrorCode());
            assertEquals(38, errors.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(15, errors.get(0).getSourceArea().getStartPosition().getColumn());
        } else {
            fail("failure for double definition of identity operator expected");
        }
    }

    /**
     * Load following dependencies:
     * <pre>
     * 041 -> 043 -> 044 -> 042
     * 041 -> 042
     * </pre>
     * In <code>042</code> the identity operator is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_04() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC041.xml"));
        if (!KernelContext.getInstance().checkModule(address)) {
            SourceFileExceptionList errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = KernelContext.getInstance().getQedeqBo(address).getWarnings();
            errors.printStackTrace(System.out);
            throw errors;
        }
        SourceFileExceptionList errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
        SourceFileExceptionList warnings = KernelContext.getInstance().getQedeqBo(address).getWarnings();
        assertNull(warnings);
        assertNull(errors);
    }

}
