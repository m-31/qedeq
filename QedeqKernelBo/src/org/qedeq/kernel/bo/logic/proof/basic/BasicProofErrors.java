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


}
