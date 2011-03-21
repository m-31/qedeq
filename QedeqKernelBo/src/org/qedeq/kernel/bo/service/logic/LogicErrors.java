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

package org.qedeq.kernel.bo.service.logic;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for service package.
 *
 * @author  Michael Meyling
 */
public interface LogicErrors extends ErrorCodes {

    /** Error code. */
    public static final int IDENTITY_OPERATOR_ALREADY_EXISTS_CODE = 123476;

    /** Error message. */
    public static final String IDENTITY_OPERATOR_ALREADY_EXISTS_TEXT
        = "identity operator already defined with";


    /** Error code. */
    public static final int PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_CODE = 30810;

    /** Error message. */
    public static final String PREDICATE_DEFINITION_NEEDS_EQUIVALENCE_OPERATOR_TEXT
        = "a predicate definition needs an equivalence relation (with two parameters)";


    /** Error code. */
    public static final int PREDICATE_DEFINITION_NEEDS_PREDICATE_CONSTANT_CODE = 30810;

    /** Error message. */
    public static final String PREDICATE_DEFINITION_NEEDS_PREDICATE_CONSTANT_TEXT
        = "a predicate definition needs an predicate constant as the first parameter";


    /** Error code. */
    public static final int MUST_HAVE_NAME_OF_PREDICATE_CODE = 40720;

    /** Error message. */
    public static final String MUST_HAVE_NAME_OF_PREDICATE_TEXT
        = "predicate name found is not as expected: ";


    /** Error code. */
    public static final int FUNCTION_ALREADY_DEFINED_CODE = 40400;

    /** Error message. */
    public static final String FUNCTION_ALREADY_DEFINED_TEXT
        = "function was already defined for this argument number: ";


    /** Error code. */
    public static final int NO_DEFINITION_FORMULA_FOR_FUNCTION_CODE = 40730;

    /** Error message. */
    public static final String NO_DEFINITION_FORMULA_FOR_FUNCTION_TEXT
        = "no definition formula for new function found";


    /** Error code. */
    public static final int DEFINITION_FORMULA_FOR_FUNCTION_MUST_START_WITH_EQUAL_RELATION_CODE = 40740;

    /** Error message. */
    public static final String DEFINITION_FORMULA_FOR_FUNCTION_MUST_START_WITH_EQUAL_RELATION_TEXT
        = "definition formula for new function must start with an equal relation";


    /** Error code. */
    public static final int PREDICATE_ALREADY_DEFINED_CODE = 40400;

    /** Error message. */
    public static final String PREDICATE_ALREADY_DEFINED_TEXT
        = "predicate was already defined for this argument number: ";


    /** Error code. */
    public static final int MUST_BE_A_SUBJECT_VARIABLE_CODE = 40500;

    /** Error message. */
    public static final String MUST_BE_A_SUBJECT_VARIABLE_TEXT
        = "a subject variable was expected here, but we found: ";


    /** Error code. */
    public static final int SUBJECT_VARIABLE_OCCURS_NOT_FREE_CODE = 40510;

    /** Error message. */
    public static final String SUBJECT_VARIABLE_OCCURS_NOT_FREE_TEXT
        = "subject variable doesn't occur free in formula or term: ";


    /** Error code. */
    public static final int NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_CODE = 40520;

    /** Error message. */
    public static final String NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_TEXT
        = "number of subject variables in definition not equal to number of free subject variables of formula or term";


    /** Error code. */
    public static final int IDENTITY_OPERATOR_MUST_BE_DEFINED_FIRST_CODE = 40530;

    /** Error message. */
    public static final String IDENTITY_OPERATOR_MUST_BE_DEFINED_FIRST_TEXT
        = "the identity operator must be defined firstly before you can define a function constant";


    /** Error code. */
    public static final int DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_CODE = 40540;

    /** Error message. */
    public static final String DEFINITION_FORMULA_FOR_FUNCTION_MUST_BE_AN_EQUAL_RELATION_TEXT
        = "a function definition must be an equal relation";


    /** Error code. */
    public static final int FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_CODE = 40550;

    /** Error message. */
    public static final String FIRST_OPERAND_MUST_BE_A_NEW_FUNCTION_CONSTANT_TEXT
        = "first operand of equal relation must be the new function constant";


    /** Error code. */
    public static final int SECOND_OPERAND_MUST_BE_A_TERM_CODE = 40560;

    /** Error message. */
    public static final String SECOND_OPERAND_MUST_BE_A_TERM_TEXT
        = "first operand of equal relation must be the new function constant";


    /** Error code. */
    public static final int MODULE_IMPORT_CHECK_FAILED_CODE = 11231;

    /** Error message. */
    public static final String MODULE_IMPORT_CHECK_FAILED_TEXT
        = "import check failed: ";


    /** Error code. */
    public static final int PROPOSITION_FORMULA_MUST_NOT_BE_NULL_CODE = 37230;

    /** Error message. */
    public static final String PROPOSITION_FORMULA_MUST_NOT_BE_NULL_TEXT
        = "proposition formula must not be null";


    /** Error code. */
    public static final int NODE_FORMULAS_MUST_BE_WELL_FORMED_CODE = 37250;

    /** Error message. */
    public static final String NODE_FORMULAS_MUST_BE_WELL_FORMED_TEXT
        = "only nodes with well formed formulas can be checked";


    /** Error code. */
    public static final int NO_FORMAL_PROOF_FOUND_CODE = 37240;

    /** Error message. */
    public static final String NO_FORMAL_PROOF_FOUND_TEXT
        = "no formal proof found";


}
