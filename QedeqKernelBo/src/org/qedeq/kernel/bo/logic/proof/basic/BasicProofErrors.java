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

package org.qedeq.kernel.bo.logic.proof.basic;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for proof checker.
 *
 * @author  Michael Meyling
 */
public interface BasicProofErrors extends ErrorCodes {

    /** Error code. */
    public static final int PROOF_LINE_MUST_NOT_BE_NULL_CODE = 37100;

    /** Error message. */
    public static final String ELEMENT_MUST_NOT_BE_NULL_TEXT
        = "proof line must not be null";


    /** Error code. */
    public static final int REASON_MUST_NOT_BE_NULL_CODE = 37110;

    /** Error message. */
    public static final String REASON_MUST_NOT_BE_NULL_TEXT
        = "reason must not be null";


    /** Error code. */
    public static final int THIS_IS_NO_ALLOWED_BASIC_REASON_CODE = 37120;

    /** Error message. */
    public static final String THIS_IS_NO_ALLOWED_BASIC_REASON_TEXT
        = "this is no allowed basic rule: ";


    /** Error code. */
    public static final int THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE = 37130;

    /** Error message. */
    public static final String THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
        = "this is not a reference to a proved formula: ";


    /** Error code. */
    public static final int EXPECTED_FORMULA_DIFFERS_CODE = 37140;

    /** Error message. */
    public static final String EXPECTED_FORMULA_DIFFERS_TEXT
        = "this is not the expected part formula, please check with label: ";


    /** Error code. */
    public static final int LOCAL_LABEL_ALREADY_EXISTS_CODE = 37150;

    /** Error message. */
    public static final String LOCAL_LABEL_ALREADY_EXISTS_TEXT
        = "this proof line label exists already in this proof: ";


    /** Error code. */
    public static final int SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE = 37160;

    /** Error message. */
    public static final String SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
        = "this proof line label dosn't (yet) exist in this proof: ";


    /** Error code. */
    public static final int IMPLICATION_EXPECTED_CODE = 37170;

    /** Error message. */
    public static final String IMPLICATION_EXPECTED_TEXT
        = "this proof line label must point to an implication (with two arguments): ";


    /** Error code. */
    public static final int MUST_BE_HYPOTHESIS_OF_FIRST_REFERENCE_CODE = 37180;

    /** Error message. */
    public static final String MUST_BE_HYPOTHESIS_OF_FIRST_REFERENCE_TEXT
        = "this proof line label must be the hypothesis for the first reference: ";


    /** Error code. */
    public static final int CURRENT_MUST_BE_CONCLUSION_CODE = 37190;

    /** Error message. */
    public static final String CURRENT_MUST_BE_CONCLUSION_TEXT
        = "this proof line must be the conclusion of the first reference: ";


    /** Error code. */
    public static final int LAST_PROOF_LINE_MUST_BE_IDENTICAL_TO_PROPOSITION_CODE = 37200;

    /** Error message. */
    public static final String LAST_PROOF_LINE_MUST_BE_IDENTICAL_TO_PROPOSITION_TEXT
        = "the last proof line must be identical to the proposition formula";


    /** Error code. */
    public static final int SUBSTITUTION_FORMULA_IS_MISSING_CODE = 37210;

    /** Error message. */
    public static final String SUBSTITUTION_FORMULA_IS_MISSING_TEXT
        = "the substitution formula is missing";


    /** Error code. */
    public static final int REFERENCE_TO_PROVED_FORMULA_IS_MISSING_CODE = 37220;

    /** Error message. */
    public static final String REFERENCE_TO_PROVED_FORMULA_IS_MISSING_TEXT
        = "this is not a reference to a proved formula: ";


    /** Error code. */
    public static final int SUBSTITUTION_TERM_IS_MISSING_CODE = 37230;

    /** Error message. */
    public static final String SUBSTITUTION_TERM_IS_MISSING_TEXT
        = "the substitution term is missing";


    /** Error code. */
    public static final int ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_CODE = 37240;

    /** Error message. */
    public static final String ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_TEXT
        = "only free subject variables as parameters allowed";


    /** Error code. */
    public static final int FREE_SUBJECT_VARIABLES_SHOULD_NOT_GET_BOUND_CODE = 37250;

    /** Error message. */
    public static final String FREE_SUBJECT_VARIABLES_SHOULD_NOT_GET_BOUND_TEXT
        = "free subject variables should not get bound during substitution";


    /** Error code. */
    public static final int SUBSTITUTION_LOCATION_CONTAINS_BOUND_SUBJECT_VARIABLE_CODE = 37260;

    /** Error message. */
    public static final String SUBSTITUTION_LOCATION_CONTAINS_BOUND_SUBJECT_VARIABLE_TEXT
        = "the substitution location contains subject variables that are bound within the "
        + "replacement formula";


    /** Error code. */
    public static final int SUBJECT_VARIABLE_IS_MISSING_CODE = 37270;

    /** Error message. */
    public static final String SUBJECT_VARIABLE_IS_MISSING_TEXT
        = "subject variable is missing";


}
