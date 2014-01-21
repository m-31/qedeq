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

package org.qedeq.kernel.bo.logic.model;

import org.qedeq.kernel.bo.test.QedeqBoTestCase;

/**
 * Test class.
 *
 * @author  Michael Meyling
 */
public class OperatorTest extends QedeqBoTestCase {

    private Operator operator;

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
        operator = getOperator("op", 2);
    }

    public void testGetArgumentNumber() throws Exception {
        assertEquals(2, operator.getArgumentNumber());
    }

    public void testGetName() throws Exception {
        assertEquals("op", operator.getName());
    }

    public void testEquals() throws Exception {
        Operator op0 = getOperator("op", 2);
        assertEquals(op0, operator);
        assertEquals(operator, op0);
        Operator op1 = getOperator("op1", 2);
        assertFalse(operator.equals("op"));
        assertFalse(op1.equals(operator));
        assertFalse(operator.equals(op1));
        Operator op2 = getOperator("op", 3);
        assertFalse(op2.equals(operator));
        assertFalse(operator.equals(op2));
        Operator op3 = new Operator("op", 2) {};
        assertFalse(op3.equals(operator));
        assertFalse(operator.equals(op3));
    }

    public void testToString() throws Exception {
        assertEquals("op(*, *)", operator.toString());
        Operator op0 = getOperator("oper", 0);
        assertEquals("oper", op0.toString());
    }

    public void testHashCode() throws Exception {
        Operator op0 = getOperator("op", 2);
        assertEquals(operator.hashCode(), op0.hashCode());
        Operator op1 = new Operator("op", 2) {};
        assertFalse(operator.hashCode() == op1.hashCode());
        Operator op2 = getOperator("op2", 2);
        assertFalse(operator.hashCode() == op2.hashCode());
        Operator op3 = getOperator("op", 3);
        assertFalse(operator.hashCode() == op3.hashCode());
    }

    protected Operator getOperator(final String name, final int number) {
        return new MyOperator(name, number);
    }

    /**
     * Testing an operator class.
     * @author  m31
     */
    class MyOperator extends Operator {

        public MyOperator(final String name, final int number) {
            super(name, number);
        }
    }

}