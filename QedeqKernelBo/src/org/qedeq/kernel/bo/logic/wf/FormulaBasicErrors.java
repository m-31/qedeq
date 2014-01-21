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

package org.qedeq.kernel.bo.logic.wf;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for formula checker.
 *
 * @author  Michael Meyling
 */
public interface FormulaBasicErrors extends ErrorCodes {

    /** Error code. */
    public static final int ELEMENT_MUST_NOT_BE_NULL = 30400;

    /** Error message. */
    public static final String ELEMENT_MUST_NOT_BE_NULL_TEXT
        = "element must not be null";


    /** Error code. */
    public static final int ATOM_MUST_NOT_BE_NULL = 30410;

    /** Error message. */
    public static final String ATOM_MUST_NOT_BE_NULL_TEXT
        = "atom must not be null";


    /** Error code. */
    public static final int LIST_MUST_NOT_BE_NULL = 30420;

    /** Error message. */
    public static final String LIST_MUST_NOT_BE_NULL_TEXT
        = "list must not be null";


    /** Error code. */
    public static final int ATOM_CONTENT_MUST_NOT_BE_NULL = 30430;

    /** Error message. */
    public static final String ATOM_CONTENT_MUST_NOT_BE_NULL_TEXT
        = "atom content must not be null";


    /** Error code. */
    public static final int ATOM_CONTENT_MUST_NOT_BE_EMPTY = 30440;

    /** Error message: first argument must be an atom. */
    public static final String ATOM_CONTENT_MUST_NOT_BE_EMPTY_TEXT
        = "atom content must not be empty";


    /** Error code. */
    public static final int OPERATOR_CONTENT_MUST_NOT_BE_NULL = 30450;

    /** Error message. */
    public static final String OPERATOR_CONTENT_MUST_NOT_BE_NULL_TEXT
        = "operator content must not be null";


    /** Error code. */
    public static final int OPERATOR_CONTENT_MUST_NOT_BE_EMPTY = 30460;

    /** Error message: first argument must be an atom. */
    public static final String OPERATOR_CONTENT_MUST_NOT_BE_EMPTY_TEXT
        = "operator content must not be empty";


    /** Error code. */
    public static final int LIST_EXPECTED = 30470;

    /** Error message. */
    public static final String LIST_EXPECTED_TEXT
        = "an atom is no formula";


    /** Error code. */
    public static final int UNKNOWN_LOGICAL_OPERATOR = 30530;

    /** Error message. */
    public static final String UNKNOWN_LOGICAL_OPERATOR_TEXT =
        "this logical operator is unknown: ";


    /** Error code. */
    public static final int SUBJECT_VARIABLE_EXPECTED = 30540;

    /** Error message. */
    public static final String SUBJECT_VARIABLE_EXPECTED_TEXT
        = "subject variable expected";


    /** Error code. */
    public static final int SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA = 30550;

    /** Error message. */
    public static final String SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA_TEXT
        = "subject variable is already bound in sub formula";


    /** Error code. */
    public static final int SUBJECT_VARIABLE_OCCURS_NOT_IN_RESTRICTION_FORMULA = 30560;

    /** Error message. */
    public static final String SUBJECT_VARIABLE_OCCURS_NOT_IN_RESTRICTION_FORMULA_TEXT
        = "subject variable occurs not in restriction formula";


    /** Error code. */
    public static final int EQUALITY_PREDICATE_NOT_YET_DEFINED = 30570;

    /** Error message. */
    public static final String EQUALITY_PREDICATE_NOT_YET_DEFINED_TEXT
        = "the equality predicate was not yet defined";


    /** Error code. */
    public static final int UNKNOWN_PREDICATE_CONSTANT = 30590;

    /** Error message. */
    public static final String UNKNOWN_PREDICATE_CONSTANT_TEXT
        = "this predicate constant is unknown (at least for this argument number): ";


    /** Error code. */
    public static final int UNKNOWN_TERM_OPERATOR = 30620;

    /** Error message. */
    public static final String UNKNOWN_TERM_OPERATOR_TEXT
        = "unknown term operator: ";


    /** Error code. */
    public static final int CLASS_OPERATOR_STILL_UNKNOWN = 30680;

    /** Error message. */
    public static final String CLASS_OPERATOR_STILL_UNKNOWN_TEXT
        = "the class operator is still undefined";


    /** Error code. */
    public static final int UNKNOWN_FUNCTION_CONSTANT = 30690;

    /** Error message. */
    public static final String UNKNOWN_FUNCTION_CONSTANT_TEXT
        = "this function constant is unknown (at least for this argument number): ";


    /** Error code. */
    public static final int EXACTLY_ONE_ARGUMENT_EXPECTED = 30710;

    /** Error message. */
    public static final String EXACTLY_ONE_ARGUMENT_EXPECTED_TEXT
        = "exactly one argument expected for the operator ";


    /** Error code. */
    public static final int AT_LEAST_ONE_ARGUMENT_EXPECTED = 30720;

    /** Error message. */
    public static final String AT_LEAST_ONE_ARGUMENT_EXPECTED_TEXT
        = "at least one argument expected for ";


    /** Error code. */
    public static final int FIRST_ARGUMENT_MUST_BE_AN_ATOM = 30730;

    /** Error message: first argument must be an atom. */
    public static final String FIRST_ARGUMENT_MUST_BE_AN_ATOM_TEXT
        = "first argument must be an atom";


    /** Error code. */
    public static final int MORE_THAN_ONE_ARGUMENT_EXPECTED = 30740;

    /** Error message. */
    public static final String MORE_THAN_ONE_ARGUMENT_EXPECTED_TEXT
        = "more than one argument expected for the operator ";


    /** Error code. */
    public static final int EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED = 30750;

    /** Error message. */
    public static final String EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED_TEXT
        = "exactly two or three arguments expected";


    /** Error code. */
    public static final int EXACTLY_TWO_ARGUMENTS_EXPECTED = 30760;

    /** Error message. */
    public static final String EXACTLY_TWO_ARGUMENTS_EXPECTED_TEXT
        = "exactly two or three arguments expected";


    /** Error code. */
    public static final int BOUND_VARIABLE_ALREADY_FREE = 30770;

    /** Error message. */
    public static final String BOUND_VARIABLE_ALREADY_FREE_TEXT
        = "these bound variables are already free in previous formulas: ";


    /** Error code. */
    public static final int FREE_VARIABLE_ALREADY_BOUND = 30780;

    /** Error message. */
    public static final String FREE_VARIABLE_ALREADY_BOUND_TEXT
        = "these free variables were already bound in previous formulas: ";


}
