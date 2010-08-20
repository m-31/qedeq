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
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.logic.wf.Function;
import org.qedeq.kernel.bo.logic.wf.Predicate;
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
     * 011 -> 012(a)
     * 011 -> 012(b)
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
        final DefaultKernelQedeqBo qedeq = (DefaultKernelQedeqBo) KernelContext.getInstance().getQedeqBo(address);
        SourceFileExceptionList errors = qedeq.getErrors();
        SourceFileExceptionList warnings = qedeq.getWarnings();
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
        SourceFileExceptionList errors;
        if (!KernelContext.getInstance().checkModule(address)) {
            errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
            throw errors;
        }
        errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
        SourceFileExceptionList warnings = KernelContext.getInstance().getQedeqBo(address).getWarnings();
        assertNull(warnings);
        assertNull(errors);
    }

    /**
     * Load following dependencies:
     * <pre>
     * 051 -> 052
     * </pre>
     * In <code>052</code> the identity operator is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_05() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC051.xml"));
        if (!KernelContext.getInstance().checkModule(address)) {
            KernelContext.getInstance().getQedeqBo(address).getErrors().printStackTrace(System.out);
            throw KernelContext.getInstance().getQedeqBo(address).getErrors();
        }
        final DefaultKernelQedeqBo qedeq = (DefaultKernelQedeqBo) KernelContext.getInstance().getQedeqBo(address);
        SourceFileExceptionList errors = qedeq.getErrors();
        SourceFileExceptionList warnings = qedeq.getWarnings();
        assertNull(warnings);
        assertNull(errors);

        ModuleConstantsExistenceChecker checker = qedeq.getExistenceChecker();

        assertEquals("MCEC052.equal", checker.getIdentityOperator());
        assertFalse(qedeq.equals(checker.getQedeq(new Predicate(ExistenceChecker.NAME_EQUAL, "" + 2))));
        assertNull(checker.getQedeq(new Predicate(ExistenceChecker.NAME_EQUAL, "" + 2)));
        assertNull(checker.getQedeq(new Predicate("unknown." + ExistenceChecker.NAME_EQUAL, "" + 2)));
        assertEquals(KernelContext.getInstance().getQedeqBo(new DefaultModuleAddress(getFile("existence/MCEC052.xml"))),
                checker.getQedeq(new Predicate("MCEC052." + ExistenceChecker.NAME_EQUAL, "" + 2)));
        {
            final PredicateDefinition def = checker.get(new Predicate("MCEC052." + ExistenceChecker.NAME_EQUAL, "" + 2));
            assertEquals("#1 \\ =  \\ #2", def.getLatexPattern());
            assertEquals("" + 2, def.getArgumentNumber());
            assertNull(def.getFormula());
            assertEquals("equal", def.getName());
            assertEquals(2, def.getVariableList().size());
        }

        {
            final PredicateDefinition def = checker.getPredicate("MCEC052." + ExistenceChecker.NAME_EQUAL, 2);
            assertEquals("#1 \\ =  \\ #2", def.getLatexPattern());
            assertEquals("" + 2, def.getArgumentNumber());
            assertNull(def.getFormula());
            assertEquals("equal", def.getName());
            assertEquals(2, def.getVariableList().size());
        }

        {
            final PredicateDefinition def = checker.get(new Predicate("MCEC052x." + ExistenceChecker.NAME_EQUAL, "" + 2));
            assertNull(def);
        }

        {
            final PredicateDefinition def = checker.getPredicate("MCEC052x." + ExistenceChecker.NAME_EQUAL, 2);
            assertNull(def);
        }

        assertEquals("MCEC052.equal", checker.getIdentityOperator());
        assertFalse(qedeq.equals(checker.getQedeq(new Function("union", "" + 2))));
        assertNull(checker.getQedeq(new Function("union", "" + 2)));
        assertNull(checker.getQedeq(new Function("unknown." + "union", "" + 2)));
        assertEquals(KernelContext.getInstance().getQedeqBo(new DefaultModuleAddress(getFile("existence/MCEC052.xml"))),
                checker.getQedeq(new Function("MCEC052." + "union", "" + 2)));
        {
            final Function function = new Function("MCEC052x." + "union", "" + 2);
            assertFalse(checker.functionExists(function));
            assertNull(checker.get(function));
        }
        {
            final Function function = new Function("MCEC05." + "union", "" + 2);
            assertFalse(checker.functionExists(function));
            assertNull(checker.get(function));
        }
        {
            final Function function = new Function("MCEC052." + "uniont", "" + 2);
            assertFalse(checker.functionExists(function));
            assertNull(checker.get(function));
        }
        {
            final Function function = new Function("MCEC052." + "union", "" + 2);
            assertTrue(checker.functionExists(function));
            final FunctionDefinition def2 = checker.get(function);
            assertEquals("(#1 \\cup #2)", def2.getLatexPattern());
            assertEquals("" + 2, def2.getArgumentNumber());
            assertEquals("CLASS ( VAR ( \"z\"), OR ( PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"x\")),"
                + " PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"y\"))))", def2.getTerm().toString());
            assertEquals("union", def2.getName());
        }
        {
            assertTrue(checker.functionExists("MCEC052." + "union", 2));
            final FunctionDefinition def3 = checker.getFunction("MCEC052." + "union", 2);
            assertEquals("(#1 \\cup #2)", def3.getLatexPattern());
            assertEquals("" + 2, def3.getArgumentNumber());
            assertEquals("CLASS ( VAR ( \"z\"), OR ( PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"x\")),"
                + " PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"y\"))))", def3.getTerm().toString());
            assertEquals("union", def3.getName());
        }
        {
            assertFalse(checker.functionExists("MCEC052x." + "union", 2));
        }

    }

    /**
     * Load following dependencies:
     * <pre>
     * 061 -> 062(a)
     * 061 -> 062(b)
     * </pre>
     * In <code>062</code> the identity operator and the class operator is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_06() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC061.xml"));
        if (!KernelContext.getInstance().checkModule(address)) {
            KernelContext.getInstance().getQedeqBo(address).getErrors().printStackTrace(System.out);
            throw KernelContext.getInstance().getQedeqBo(address).getErrors();
        }
        final DefaultKernelQedeqBo qedeq = (DefaultKernelQedeqBo) KernelContext.getInstance().getQedeqBo(address);
        SourceFileExceptionList errors = qedeq.getErrors();
        SourceFileExceptionList warnings = qedeq.getWarnings();
        assertNull(warnings);
        assertNull(errors);

        ModuleConstantsExistenceChecker checker = qedeq.getExistenceChecker();

        assertEquals("MCEC062a.equal", checker.getIdentityOperator());
        assertFalse(qedeq.equals(checker.getQedeq(new Predicate(ExistenceChecker.NAME_EQUAL, "" + 2))));
        assertNull(checker.getQedeq(new Predicate(ExistenceChecker.NAME_EQUAL, "" + 2)));
        assertNull(checker.getQedeq(new Predicate("unknown." + ExistenceChecker.NAME_EQUAL, "" + 2)));
        assertEquals(KernelContext.getInstance().getQedeqBo(new DefaultModuleAddress(getFile("existence/MCEC062.xml"))),
                checker.getQedeq(new Predicate("MCEC062a." + ExistenceChecker.NAME_EQUAL, "" + 2)));
        final PredicateDefinition def = checker.get(new Predicate("MCEC062a." + ExistenceChecker.NAME_EQUAL, "" + 2));
        assertEquals("#1 \\ =  \\ #2", def.getLatexPattern());
        assertEquals("" + 2, def.getArgumentNumber());
        assertNull(def.getFormula());
        assertEquals("equal", def.getName());
        assertEquals(2, def.getVariableList().size());

        assertEquals("MCEC062a.equal", checker.getIdentityOperator());
        assertFalse(qedeq.equals(checker.getQedeq(new Function("union", "" + 2))));
        assertNull(checker.getQedeq(new Function("union", "" + 2)));
        assertNull(checker.getQedeq(new Function("unknown." + "union", "" + 2)));
        assertEquals(KernelContext.getInstance().getQedeqBo(new DefaultModuleAddress(getFile("existence/MCEC062.xml"))),
                checker.getQedeq(new Function("MCEC062a." + "union", "" + 2)));
        final FunctionDefinition def2 = checker.get(new Function("MCEC062a." + "union", "" + 2));
        assertEquals("(#1 \\cup #2)", def2.getLatexPattern());
        assertEquals("" + 2, def2.getArgumentNumber());
        assertEquals("CLASS ( VAR ( \"z\"), OR ( PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"x\")),"
            + " PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"y\"))))", def2.getTerm().toString());
        assertEquals("union", def2.getName());

    }

    /**
     * Load following dependencies:
     * <pre>
     * 071 -> 072
     * 071 -> 073
     * </pre>
     * In <code>072</code> and <code>074</code> the identity operator and the class operator is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_07() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC071.xml"));
        if (!KernelContext.getInstance().checkModule(address)) {
            SourceFileExceptionList errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = KernelContext.getInstance().getQedeqBo(address).getWarnings();
            assertNull(warnings);
            assertEquals(1, errors.size());
            assertEquals(123478, errors.get(0).getErrorCode());
            assertEquals(38, errors.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(15, errors.get(0).getSourceArea().getStartPosition().getColumn());
        } else {
            fail("failure for double definition of class operator expected");
        }
    }

    /**
     * Load following dependencies:
     * <pre>
     * 081 -> 082 -> 083
     * </pre>
     * In <code>081</code> and <code>083</code> the the class operator is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_08() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC081.xml"));
        if (!KernelContext.getInstance().checkModule(address)) {
            SourceFileExceptionList errors = KernelContext.getInstance().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = KernelContext.getInstance().getQedeqBo(address).getWarnings();
            assertNull(warnings);
            assertEquals(1, errors.size());
            assertEquals(123478, errors.get(0).getErrorCode());
            assertEquals(118, errors.get(0).getSourceArea().getStartPosition().getLine());
            assertEquals(11, errors.get(0).getSourceArea().getStartPosition().getColumn());
        } else {
            fail("failure for double definition of class operator expected");
        }
    }

}
