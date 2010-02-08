/* This file is part of the project "Hilbert II" - http://www.qedeq.org
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

package org.qedeq.kernel.bo.logic.wf;

/**
 * Logical and term operators. These strings might occur as values for
 * {@link org.qedeq.kernel.base.list.ElementList#getOperator()}.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public interface Operators {

    /** Operator string for logical "and". */
    public static final String CONJUNCTION_OPERATOR = "AND";

    /** Operator string for logical "or". */
    public static final String DISJUNCTION_OPERATOR = "OR";

    /** Operator string for logical implication. */
    public static final String IMPLICATION_OPERATOR = "IMPL";

    /** Operator string for logical equivalence. */
    public static final String EQUIVALENCE_OPERATOR = "EQUI";

    /** Operator string for logical negation. */
    public static final String NEGATION_OPERATOR = "NOT";

    /** Operator string for logical "all" operator. */
    public static final String UNIVERSAL_QUANTIFIER_OPERATOR = "FORALL";

    /** Operator string for logical "exists" operator. */
    public static final String EXISTENTIAL_QUANTIFIER_OPERATOR = "EXISTS";

    /** Operator string for logical "exists unique" operator. */
    public static final String UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR = "EXISTSU";

    /** Operator string for predicate constants. */
    public static final String PREDICATE_CONSTANT = "PREDCON";

    /** Operator string for predicate variables. */
    public static final String PREDICATE_VARIABLE = "PREDVAR";

    /** Operator string for subject variables. */
    public static final String SUBJECT_VARIABLE = "VAR";

    /** Operator string for function constants. */
    public static final String FUNCTION_CONSTANT = "FUNCON";

    /** Operator string for function constants. */
    public static final String FUNCTION_VARIABLE = "FUNVAR";

    /** Operator string for class operator. */
    public static final String CLASS_OP = "CLASS";

}
