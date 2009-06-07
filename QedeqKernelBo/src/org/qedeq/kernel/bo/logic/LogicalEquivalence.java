/* $Id: LogicalEquivalence.java,v 1.1 2008/07/26 07:58:30 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.bo.logic.wf.FormulaCheckException;
import org.qedeq.kernel.bo.logic.wf.LogicalCheckException;
import org.qedeq.kernel.bo.logic.wf.Operators;


/**
 * This class deals with {@link org.qedeq.kernel.base.list.Element}s and could check
 * if two formulas are logically equivalent.
 *
 * LATER mime 20050205: work in progress
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class LogicalEquivalence {

    /**
     * Constructor.
     */
    private LogicalEquivalence() {
        // nothing to do
    }

    /**
     * Assures that two words are logically equivalent.
     *
     * @param   formula1    First formula.
     * @param   formula2    Second formula.
     * @throws  LogicalCheckException  Check failed.
     */
    public static final void checkEquivalence(final Element formula1,
            final Element formula2)
            throws LogicalCheckException {
        if (formula1.equals(formula2)) {
            return;
        }
        if (formula1.isAtom() || formula2.isAtom()) {
            return; // LATER mime 20050330: where is the check???
        }
        final String op1 = formula1.getList().getOperator();
        final String op2 = formula2.getList().getOperator();
        if (op1.equals(op2) && (op1.equals(Operators.CONJUNCTION_OPERATOR)
                || op1.equals(Operators.DISJUNCTION_OPERATOR))) {
            final EqualFormulaSet sub1 = new EqualFormulaSet(formula1.getList());
            final EqualFormulaSet sub2 = new EqualFormulaSet(formula2.getList());
            if (sub1.equals(sub2)) {
                return;
            }

        }
        throw new FormulaCheckException(61, "no logical equivalence found", formula2, null);
    }

}
