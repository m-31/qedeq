/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.logic.common.FunctionConstant;
import org.qedeq.kernel.bo.logic.common.FunctionKey;
import org.qedeq.kernel.bo.logic.common.PredicateConstant;
import org.qedeq.kernel.bo.logic.common.PredicateKey;
import org.qedeq.kernel.bo.module.ModuleConstantsExistenceChecker;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * For testing of checking existence of module constants.
 *
 * @author  Michael Meyling
 */
public class ModuleConstantsExistenceCheckerTest extends QedeqBoTestCase {

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
        if (!getServices().checkModule(address)) {
            getServices().getQedeqBo(address).getErrors().printStackTrace(System.out);
            throw getServices().getQedeqBo(address).getErrors();
        }
        final DefaultKernelQedeqBo qedeq = (DefaultKernelQedeqBo) getServices().getQedeqBo(address);
        SourceFileExceptionList errors = qedeq.getErrors();
        SourceFileExceptionList warnings = qedeq.getWarnings();
        assertNotNull(warnings);
        assertEquals(0, warnings.size());
        assertNotNull(errors);
        assertEquals(0, errors.size());
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
        if (!getServices().checkModule(address)) {
            SourceFileExceptionList errors = getServices().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = getServices().getQedeqBo(address).getWarnings();
            assertNotNull(warnings);
            assertEquals(0, warnings.size());
            assertEquals(1, errors.size());
            assertEquals(123476, errors.get(0).getErrorCode());
            assertEquals(38, errors.get(0).getSourceArea().getStartPosition().getRow());
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
        if (!getServices().checkModule(address)) {
            SourceFileExceptionList errors = getServices().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = getServices().getQedeqBo(address).getWarnings();
            assertNotNull(warnings);
            assertEquals(0, warnings.size());
            assertEquals(1, errors.size());
            assertEquals(123476, errors.get(0).getErrorCode());
            assertEquals(38, errors.get(0).getSourceArea().getStartPosition().getRow());
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
        if (!getServices().checkModule(address)) {
            errors = getServices().getQedeqBo(address).getErrors();
            throw errors;
        }
        errors = getServices().getQedeqBo(address).getErrors();
        SourceFileExceptionList warnings = getServices().getQedeqBo(address).getWarnings();
        assertNotNull(warnings);
        assertEquals(0, warnings.size());
        assertNotNull(errors);
        assertEquals(0, errors.size());
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
        if (!getServices().checkModule(address)) {
            getServices().getQedeqBo(address).getErrors().printStackTrace(System.out);
            throw getServices().getQedeqBo(address).getErrors();
        }
        final DefaultKernelQedeqBo qedeq = (DefaultKernelQedeqBo) getServices().getQedeqBo(address);
        SourceFileExceptionList errors = qedeq.getErrors();
        SourceFileExceptionList warnings = qedeq.getWarnings();
        assertNotNull(warnings);
        assertEquals(0, warnings.size());
        assertNotNull(errors);
        assertEquals(0, errors.size());

        ModuleConstantsExistenceChecker checker = qedeq.getExistenceChecker();

        assertEquals("MCEC052.equal", checker.getIdentityOperator());
        assertFalse(qedeq.equals(checker.getQedeq(new PredicateKey(ExistenceChecker.NAME_EQUAL, "" + 2))));
        assertNull(checker.getQedeq(new PredicateKey(ExistenceChecker.NAME_EQUAL, "" + 2)));
        assertNull(checker.getQedeq(new PredicateKey("unknown." + ExistenceChecker.NAME_EQUAL, "" + 2)));
        assertEquals(getServices().getQedeqBo(new DefaultModuleAddress(getFile("existence/MCEC052.xml"))),
                checker.getQedeq(new PredicateKey("MCEC052." + ExistenceChecker.NAME_EQUAL, "" + 2)));
        {
            final PredicateKey predicate = new PredicateKey("MCEC052." + ExistenceChecker.NAME_EQUAL, "" + 2);
            assertTrue(checker.predicateExists(predicate));
            final PredicateConstant def = checker.get(predicate);
//            assertEquals("#1 \\ =  \\ #2", def.getLatexPattern());
//            assertEquals("" + 2, def.getArgumentNumber());
            assertNull(def);
            assertTrue(checker.isInitialPredicate(predicate));
        }

        {
            assertTrue(checker.predicateExists("MCEC052." + ExistenceChecker.NAME_EQUAL, 2));
            final PredicateConstant def = checker.getPredicate("MCEC052." + ExistenceChecker.NAME_EQUAL, 2);
//            assertEquals("#1 \\ =  \\ #2", def.getLatexPattern());
//            assertEquals("" + 2, def.getArgumentNumber());
            assertNull(def);
        }

        {
            final PredicateKey predicate = new PredicateKey("MCEC052x." + ExistenceChecker.NAME_EQUAL, "" + 2);
            assertFalse(checker.predicateExists(predicate));
            final PredicateConstant def = checker.get(predicate);
            assertNull(def);
            assertTrue(!checker.isInitialPredicate(predicate));
        }

        {
            assertFalse(checker.predicateExists("MCEC052x." + ExistenceChecker.NAME_EQUAL, 2));
            final PredicateConstant def = checker.getPredicate("MCEC052x." + ExistenceChecker.NAME_EQUAL, 2);
            assertNull(def);
        }

        assertEquals("MCEC052.equal", checker.getIdentityOperator());
        assertFalse(qedeq.equals(checker.getQedeq(new FunctionKey("union", "" + 2))));
        assertNull(checker.getQedeq(new FunctionKey("union", "" + 2)));
        assertNull(checker.getQedeq(new FunctionKey("unknown." + "union", "" + 2)));
        assertEquals(getServices().getQedeqBo(new DefaultModuleAddress(getFile("existence/MCEC052.xml"))),
                checker.getQedeq(new FunctionKey("MCEC052." + "union", "" + 2)));
        {
            final FunctionKey function = new FunctionKey("MCEC052x." + "union", "" + 2);
            assertFalse(checker.functionExists(function));
            assertNull(checker.get(function));
        }
        {
            final FunctionKey function = new FunctionKey("MCEC05." + "union", "" + 2);
            assertFalse(checker.functionExists(function));
            assertNull(checker.get(function));
        }
        {
            final FunctionKey function = new FunctionKey("MCEC052." + "uniont", "" + 2);
            assertFalse(checker.functionExists(function));
            assertNull(checker.get(function));
        }
        {
            final FunctionKey function = new FunctionKey("MCEC052." + "union", "" + 2);
            assertTrue(checker.functionExists(function));
            final FunctionConstant def2 = checker.get(function);
//            assertEquals("(#1 \\cup #2)", def2.getLatexPattern());
//            assertEquals("" + 2, def2.getArgumentNumber());
            assertEquals("PREDCON ( \"equal\", FUNCON ( \"union\", VAR ( \"x\"), VAR ( \"y\")), "
                + "CLASS ( VAR ( \"z\"), OR ( PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"x\")),"
                + " PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"y\")))))",
                def2.getCompleteFormula().toString());
            assertEquals("union", def2.getName());
            assertEquals("2", def2.getArguments());
            assertTrue(!checker.isInitialFunction(function));
        }
        {
            assertTrue(checker.functionExists("MCEC052." + "union", 2));
            final FunctionConstant def3 = checker.getFunction("MCEC052." + "union", 2);
//            assertEquals("(#1 \\cup #2)", def3.getLatexPattern());
//            assertEquals("" + 2, def3.getArgumentNumber());
            assertEquals("CLASS ( VAR ( \"z\"), OR ( PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"x\")),"
                + " PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"y\"))))", def3.getDefiningTerm().toString());
            assertEquals("union", def3.getName());
            assertEquals("2", def3.getArguments());
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
        if (!getServices().checkModule(address)) {
            getServices().getQedeqBo(address).getErrors().printStackTrace(System.out);
            throw getServices().getQedeqBo(address).getErrors();
        }
        final DefaultKernelQedeqBo qedeq = (DefaultKernelQedeqBo) getServices().getQedeqBo(address);
        SourceFileExceptionList errors = qedeq.getErrors();
        SourceFileExceptionList warnings = qedeq.getWarnings();
        assertNotNull(warnings);
        assertEquals(0, warnings.size());
        assertNotNull(errors);
        assertEquals(0, errors.size());

        ModuleConstantsExistenceChecker checker = qedeq.getExistenceChecker();

        assertEquals("MCEC062a.equal", checker.getIdentityOperator());
        assertFalse(qedeq.equals(checker.getQedeq(new PredicateKey(ExistenceChecker.NAME_EQUAL, "" + 2))));
        assertNull(checker.getQedeq(new PredicateKey(ExistenceChecker.NAME_EQUAL, "" + 2)));
        assertNull(checker.getQedeq(new PredicateKey("unknown." + ExistenceChecker.NAME_EQUAL, "" + 2)));
        final PredicateKey key = new PredicateKey("MCEC062a." + ExistenceChecker.NAME_EQUAL, "" + 2);
        assertEquals(getServices().getQedeqBo(new DefaultModuleAddress(getFile("existence/MCEC062.xml"))),
                checker.getQedeq(key));
        final PredicateConstant def = checker.get(key);
//        assertEquals("#1 \\ =  \\ #2", def.getLatexPattern());
//        assertEquals("" + 2, def.getArgumentNumber());
        assertNull(def);
        assertTrue(checker.isInitialPredicate(key));

        assertEquals("MCEC062a.equal", checker.getIdentityOperator());
        assertFalse(qedeq.equals(checker.getQedeq(new FunctionKey("union", "" + 2))));
        assertNull(checker.getQedeq(new FunctionKey("union", "" + 2)));
        assertNull(checker.getQedeq(new FunctionKey("unknown." + "union", "" + 2)));
        final FunctionKey key2 = new FunctionKey("MCEC062a." + "union", "" + 2);
        assertEquals(getServices().getQedeqBo(new DefaultModuleAddress(getFile("existence/MCEC062.xml"))),
                checker.getQedeq(key2));
        final FunctionConstant def2 = checker.get(key2);
//        assertEquals("(#1 \\cup #2)", def2.getLatexPattern());
//        assertEquals("" + 2, def2.getArgumentNumber());
        assertEquals("CLASS ( VAR ( \"z\"), OR ( PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"x\")),"
            + " PREDCON ( \"in\", VAR ( \"z\"), VAR ( \"y\"))))", def2.getDefiningTerm().toString());
        assertEquals("union", def2.getName());
        assertEquals("2", def2.getArguments());
        assertTrue(!checker.isInitialFunction(key2));

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
        if (!getServices().checkModule(address)) {
            SourceFileExceptionList errors = getServices().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = getServices().getQedeqBo(address).getWarnings();
            assertNotNull(warnings);
            assertEquals(0, warnings.size());
            assertEquals(1, errors.size());
            assertEquals(123478, errors.get(0).getErrorCode());
            assertEquals(38, errors.get(0).getSourceArea().getStartPosition().getRow());
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
        if (!getServices().checkModule(address)) {
            SourceFileExceptionList errors = getServices().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = getServices().getQedeqBo(address).getWarnings();
            assertNotNull(warnings);
            assertEquals(0, warnings.size());
            System.out.println(errors);
            assertEquals(1, errors.size());
            assertEquals(37260, errors.get(0).getErrorCode());
            assertEquals(118, errors.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(52, errors.get(0).getSourceArea().getStartPosition().getColumn());
        } else {
            fail("failure for double definition of class operator expected");
        }
    }

    /**
     * Load following dependencies:
     * <pre>
     * 091 -> 092 -> 093
     * </pre>
     * In <code>091</code> and <code>093</code> the the identity predicate is defined.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_09() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC091.xml"));
        if (!getServices().checkModule(address)) {
            SourceFileExceptionList errors = getServices().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = getServices().getQedeqBo(address).getWarnings();
            assertNotNull(warnings);
            assertEquals(0, warnings.size());
//            System.out.println(errors);
            assertEquals(1, errors.size());
            assertEquals(123476, errors.get(0).getErrorCode());
            assertEquals(125, errors.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(11, errors.get(0).getSourceArea().getStartPosition().getColumn());
        } else {
            fail("failure for double definition of class operator expected");
        }
    }

    /**
     * Load following dependencies:
     * <pre>
     * 101
     * </pre>
     * In <code>101</code> the identity operator is not defined before defining a function constant.
     *
     * @throws Exception
     */
    public void testModuleConstancsExistenceChecker_10() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(getFile("existence/MCEC101.xml"));
        if (!getServices().checkModule(address)) {
            SourceFileExceptionList errors = getServices().getQedeqBo(address).getErrors();
            SourceFileExceptionList warnings = getServices().getQedeqBo(address).getWarnings();
            assertNotNull(warnings);
            assertEquals(0, warnings.size());
//            System.out.println(errors);
            assertEquals(1, errors.size());
            assertEquals(40530, errors.get(0).getErrorCode());
            assertEquals(151, errors.get(0).getSourceArea().getStartPosition().getRow());
            assertEquals(15, errors.get(0).getSourceArea().getStartPosition().getColumn());
        } else {
            fail("failure for double definition of class operator expected");
        }
    }

}
