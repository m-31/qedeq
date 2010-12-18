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

package org.qedeq.kernel.bo.logic.wf;

import org.qedeq.kernel.common.ErrorCodes;

/**
 * Error codes and messages for formula checker.
 *
 * @author  Michael Meyling
 */
public interface HigherLogicalErrors extends ErrorCodes {

    /** Error code. */
    public static final int PREDICATE_ALREADY_DEFINED = 40400;

    /** Error message. */
    public static final String PREDICATE_ALREADY_DEFINED_TEXT
        = "predicate was already defined for this argument number: ";


    /** Error code. */
    public static final int FUNCTION_ALREADY_DEFINED = 40400;

    /** Error message. */
    public static final String FUNCTION_ALREADY_DEFINED_TEXT
        = "function was already defined for this argument number: ";


    /** Error code. */
    public static final int MUST_BE_A_SUBJECT_VARIABLE_CODE = 40500;

    /** Error message. */
    public static final String MUST_BE_A_SUBJECT_VARIABLE_MSG
        = "a subject variable was expected here, but we found: ";


    /** Error code. */
    public static final int SUBJECT_VARIABLE_OCCURS_NOT_FREE_CODE = 40510;

    /** Error message. */
    public static final String SUBJECT_VARIABLE_OCCURS_NOT_FREE_MSG
        = "subject variable doesn't occur free in formula or term: ";


    /** Error code. */
    public static final int NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_CODE = 40520;

    /** Error message. */
    public static final String NUMBER_OF_FREE_SUBJECT_VARIABLES_NOT_EQUAL_MSG
        = "number of subject variables in definition not equal to number of free subject variables of formula or term";


    /** Error code. */
    public static final int MODULE_IMPORT_CHECK_FAILED_CODE = 11231;

    /** Error message. */
    public static final String MODULE_IMPORT_CHECK_FAILED_MSG
        = "import check failed: ";

    /** Error code. */
    public static final int PREDICATE_CALCULATION_FAILED_CODE = 40710;

    /** Error message. */
    public static final String PREDICATE_CALCULATION_FAILED_MSG
        = "calculation for predicate failed: ";

}
