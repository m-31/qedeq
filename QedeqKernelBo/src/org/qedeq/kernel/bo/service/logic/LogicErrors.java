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
        = "no correct formal proof found";


    /** Error code. */
    public static final int RULE_VERSION_HAS_STILL_NO_PROOF_CHECKER_CODE = 37250;

    /** Error message. */
    public static final String RULE_VERSION_HAS_STILL_NO_PROOF_CHECKER_TEXT
        = "this rule version has still no proof checker implementation: ";


    /** Error code. */
    public static final int RULE_ALREADY_DEFINED_CODE = 37260;

    /** Error message. */
    public static final String RULE_ALREADY_DEFINED_TEXT
        = "rule was already defined for this version: ";


    /** Error code. */
    public static final int RULE_DEFINITIONS_DONT_MIX_CODE = 37270;

    /** Error message. */
    public static final String RULE_DEFINITIONS_DONT_MIX_TEXT
        = "rule version was defined in different modules: ";


    /** Error code. */
    public static final int CLASS_OPERATOR_ALREADY_DEFINED_CODE = 123478;

    /** Error message. */
    public static final String CLASS_OPERATOR_ALREADY_DEFINED_TEXT
        = "class operator already defined within this module: ";


    /** Error code. */
    public static final int THIS_IS_NOT_VALID_VERSION_FORMAT_CODE = 37300;

    /** Error message. */
    public static final String THIS_IS_NOT_VALID_VERSION_FORMAT_TEXT
        = "a version must be formed like a.b.c with nonegative integers a, b, c; out problem is: ";


    /** Error code. */
    public static final int MODULE_HAS_NO_HEADER_CODE = 37310;

    /** Error message. */
    public static final String MODULE_HAS_NO_HEADER_TEXT
        = "module has no header";


    /** Error code. */
    public static final int MODULE_HAS_NO_HEADER_SPECIFICATION_CODE = 37320;

    /** Error message. */
    public static final String MODULE_HAS_NO_HEADER_SPECIFICATION_TEXT
        = "module has no header specification";


    /** Error code. */
    public static final int RULE_WAS_NOT_DECLARED_BEFORE_CODE = 37330;

    /** Error message. */
    public static final String RULE_WAS_NOT_DECLARED_BEFORE_TEXT
        = "a rule with this name was not declared yet: ";


    /** Error code. */
    public static final int RULE_HAS_BEEN_DECLARED_BEFORE_CODE = 37340;

    /** Error message. */
    public static final String RULE_HAS_BEEN_DECLARED_BEFORE_TEXT
        = "a rule with this version was already declared: ";


    /** Error code. */
    public static final int OTHER_RULE_VERSION_EXPECTED_CODE = 37350;

    /** Error message. */
    public static final String OTHER_RULE_VERSION_EXPECTED_TEXT1
        = "we expected this rule verson: ";

    /** Error message. */
    public static final String OTHER_RULE_VERSION_EXPECTED_TEXT2
        = " but we got: ";


    /** Error code. */
    public static final int NEW_RULE_HAS_LOWER_VERSION_NUMBER_CODE = 37360;

    /** Error message. */
    public static final String NEW_RULE_HAS_LOWER_VERSION_NUMBER_TEXT
        = "the new declared rule version must be higher than the old one: ";


    /** Error code. */
    public static final int OLD_OR_NEW_RULE_HAS_INVALID_VERSION_NUMBER_PATTERN_CODE = 37370;

    /** Error message. */
    public static final String OLD_OR_NEW_RULE_HAS_INVALID_VERSION_NUMBER_PATTERN_TEXT
        = "the version numbers have not the allowed pattern (old or new rule version): ";


    /** Error code. */
    public static final int RULE_HAS_NO_NAME_OR_VERSION_CODE = 37380;

    /** Error message. */
    public static final String RULE_HAS_NO_NAME_OR_VERSION_TEXT
        = "this rule has no name or version: ";


    /** Error code. */
    public static final int RULE_DECLARED_IN_DIFFERENT_IMPORT_MODULES_CODE = 37390;

    /** Error message. */
    public static final String RULE_DECLARED_IN_DIFFERENT_IMPORT_MODULES_TEXT
        = "this rule is defined in two different modules: ";


}
