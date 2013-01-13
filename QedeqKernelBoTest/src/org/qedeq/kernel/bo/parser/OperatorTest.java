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

package org.qedeq.kernel.bo.parser;

import org.qedeq.kernel.bo.test.QedeqBoTestCase;

/**
 * Test class.
 *
 * @author  Michael Meyling
 */
public class OperatorTest extends QedeqBoTestCase {

    private Operator not;
    private Operator and;
    private Operator or;
    private Operator impl;
    private Operator equi;
    private Operator forall;
    private Operator exists;
    private Operator equal;
    private Operator in;
    private Operator isSet;
    private Operator classList;
    private Operator clasDef;
    private Operator union;
    private Operator intersection;
    private Operator subset;
    private Operator superset;
    private Operator phi;
    private Operator psi;
    private Operator alpha;
    private Operator beta;
    private Operator gamma;
    private Operator post1;
    private Operator post2;

    
    /**
     * Constructor.
     *
     */
    public OperatorTest() {
        super();
    }

    /**
     * Constructor.
     *
     * @param   name    Test case name.
     *
     */
    public OperatorTest(final String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        not          = new Operator("neg",     "NOT",      null,   110, 1, 1, 1);
        and          = new Operator("land",    "AND",      null,   100, 0, 2);
        or           = new Operator("lor",     "OR",       null,    90, 0, 2);
        impl         = new Operator("impl",    "IMPL",     null,    80, 0, 2, 2);
        equi         = new Operator("equi",    "EQUI",     null,    80, 0, 2);
        forall       = new Operator("forall",  "ALL",      null,   140, 1, 2, 2);
        exists       = new Operator("exists",   "EXISTS",   null,   140, 1, 2, 2);
        in           = new Operator("in",       "PREDCON", "IN",    200, 0, 2, 2);
        equal        = new Operator("=",        "PREDCON", "EQUAL", 200, 0, 2);
        isSet        = new Operator("isSet",    "PREDCON", "isSet", 200, 1, 1, 1);
        classList    = new Operator("{", ",", "}", "CLASSLIST",  null,    10, 0);
        clasDef      = new Operator("{", "|", "}", "CLASS",  null,    10, 2, 2);
        union        = new Operator("cup",      "UNION",    null,    30, 0, 2);
        intersection = new Operator("cap",      "INTER",   null,  40, 0, 2);
        subset       = new Operator("subset",   "PREDCON", "subset",   10, 2, 2, 2);
        superset     = new Operator("superset", "PREDCON", "superset", 10, 2, 2, 2);
        phi          = new Operator("phi",      "PREDVAR", "1",  200, 1, 1, 2);
        psi          = new Operator("psi",      "PREDVAR", "2",  200, 1, 1, 2);
        alpha        = new Operator("alpha",    "PREDVAR", "3",  200, 4, 0, -1);
        beta         = new Operator("beta",     "PREDVAR", "4",  200, 4, 0, -1);
        gamma        = new Operator("gamma",    "PREDVAR", "5",  200, 4, 0, -1);
        post1        = new Operator("post",     "POST",    "5",  1003, 2, 0, -1);
        post2        = new Operator("post",     "POST",    "5",  1005, 2, 0, -1);
    }

    public void testGetMin() throws Exception {
        assertEquals(1, not.getMin());
        assertEquals(2, and.getMin());
        assertEquals(2, or.getMin());
        assertEquals(2, impl.getMin());
        assertEquals(2, equi.getMin());
        assertEquals(2, forall.getMin());
        assertEquals(2, exists.getMin());
        assertEquals(2, equal.getMin());
        assertEquals(2, in.getMin());
        assertEquals(1, isSet.getMin());
        assertEquals(2, subset.getMin());
        assertEquals(2, superset.getMin());
        assertEquals(0, classList.getMin());
        assertEquals(2, clasDef.getMin());
        assertEquals(2, union.getMin());
        assertEquals(2, intersection.getMin());
        assertEquals(1, phi.getMin());
        assertEquals(1, psi.getMin());
        assertEquals(0, alpha.getMin());
        assertEquals(0, beta.getMin());
        assertEquals(0, gamma.getMin());
        assertEquals(0, post1.getMin());
        assertEquals(0, post2.getMin());
    }

    public void testGetMax() throws Exception {
        assertEquals(1, not.getMax());
        assertEquals(-1, and.getMax());
        assertEquals(-1, or.getMax());
        assertEquals(2, impl.getMax());
        assertEquals(-1, equi.getMax());
        assertEquals(2, forall.getMax());
        assertEquals(2, exists.getMax());
        assertEquals(-1, equal.getMax());
        assertEquals(2, in.getMax());
        assertEquals(1, isSet.getMax());
        assertEquals(-1, classList.getMax());
        assertEquals(2, clasDef.getMax());
        assertEquals(-1, union.getMax());
        assertEquals(-1, intersection.getMax());
        assertEquals(2, subset.getMax());
        assertEquals(2, superset.getMax());
        assertEquals(2, phi.getMax());
        assertEquals(2, psi.getMax());
        assertEquals(-1, alpha.getMax());
        assertEquals(-1, beta.getMax());
        assertEquals(-1, gamma.getMax());
    }

    public void testEquals() throws Exception {
        assertEquals(and, and);
        assertEquals(not, not);
        assertEquals(post1, post1);
        assertFalse(post1.equals(post2));
        assertFalse(not.equals(and));
        assertFalse(not.equals(null));
        assertFalse(or.equals(and));
    }

    public void testToString() throws Exception {
        assertEquals("land[2, ..], is infix", and.toString());
        assertEquals("impl[2, 2], is infix", impl.toString());
        assertEquals("forall[2, 2], is prefix", forall.toString());
        assertEquals("{[2, 2] | }, is prefix", clasDef.toString());
        assertEquals("{[0, ..] , }, is prefix", classList.toString());
        assertEquals("post[0, ..], is postfix", post1.toString());
    }

    public void testIsFix() {
        assertTrue(and.isInfix());
        assertFalse(and.isPrefix());
        assertFalse(and.isPostfix());
        assertFalse(and.isFunction());
        assertTrue(impl.isInfix());
        assertFalse(impl.isPrefix());
        assertFalse(impl.isPostfix());
        assertFalse(impl.isFunction());
        assertFalse(alpha.isInfix());
        assertTrue(alpha.isPrefix());
        assertFalse(alpha.isPostfix());
        assertTrue(alpha.isFunction());
        assertFalse(post1.isPrefix());
        assertTrue(post1.isPostfix());
        assertFalse(post1.isFunction());
        assertFalse(post1.isInfix());
    }

}