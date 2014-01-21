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

import org.qedeq.kernel.se.common.ErrorCodes;


/**
 * Contains the error codes for heuristic model checks.
 *
 * @author  Michael Meyling
 */
public interface HeuristicErrorCodes extends ErrorCodes {

    /** Error (or warning) number for: no tautology in our model. */
    public static final int EVALUATED_NOT_TRUE_CODE = 77007;

    /** Error (or warning) text for: no tautology in our model. */
    public static final String EVALUATED_NOT_TRUE_TEXT = "no tautology in our model";


    /** Error (or warning) number for: wrong calling convention, list expected. */
    public static final int WRONG_CALLING_CONVENTION_CODE = 77091;

    /** Error (or warning) text for: wrong calling convention, list expected. */
    public static final String WRONG_CALLING_CONVENTION_TEXT
        = "wrong calling convention, list expected";


    /** Error (or warning) number for: unknown operator. */
    public static final int UNKNOWN_OPERATOR_CODE = 77080;

    /** Error (or warning) text for: unknown operator. */
    public static final String UNKNOWN_OPERATOR_TEXT = "unknown operator: ";


    /** Error (or warning) number for: unknown predicate constant. */
    public static final int UNKNOWN_PREDICATE_CONSTANT_CODE = 77082;

    /** Error (or warning) text for: unknown predicate constant. */
    public static final String UNKNOWN_PREDICATE_CONSTANT_TEXT = "unknown predicate constant: ";


    /** Error (or warning) number for: unknown term operator. */
    public static final int UNKNOWN_TERM_OPERATOR_CODE = 77084;

    /** Error (or warning) text for: unknown term operator. */
    public static final String UNKNOWN_TERM_OPERATOR_TEXT = "unknown term operator: ";


    /** Error (or warning) number for: unknown function constant. */
    public static final int UNKNOWN_FUNCTION_CONSTANT_CODE = 77092;

    /** Error (or warning) text for: unknown function constant. */
    public static final String UNKNOWN_FUNCTION_CONSTANT_TEXT = "unknown function constant: ";


    /** Error (or warning) number for: unknown format for argument size. */
    public static final int UNKNOWN_ARGUMENT_FORMAT_CODE = 77100;

    /** Error (or warning) text for: unknown format for argument size. */
    public static final String UNKNOWN_ARGUMENT_FORMAT_TEXT = "unknown format for argument size: ";


    /** Error (or warning) number for: unknown format for argument size. */
    public static final int RUNTIME_EXCEPTION_CODE = 77777;

    /** Error (or warning) text for: unknown format for argument size. */
    public static final String RUNTIME_EXCEPTION_TEXT = "runtime problem: ";


    /** Error (or warning) number for: unknown import module. */
    public static final int UNKNOWN_IMPORT_MODULE_CODE = 78082;

    /** Error (or warning) text for: unknown import module. */
    public static final String UNKNOWN_IMPORT_MODULE_TEXT = "unknown (or not loaded) import module ";

    /** Error (or warning) text for: unknown import module. */
    public static final String UNKNOWN_IMPORT_MODULE_TEXT_2 = " for ";


    /** Error code. */
    public static final int PREDICATE_CALCULATION_FAILED_CODE = 40710;

    /** Error message. */
    public static final String PREDICATE_CALCULATION_FAILED_TEXT
        = "calculation for predicate failed: ";


}
