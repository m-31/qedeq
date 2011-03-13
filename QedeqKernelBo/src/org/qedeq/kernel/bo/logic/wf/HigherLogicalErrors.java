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

package org.qedeq.kernel.bo.logic.wf;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for formula checker.
 * FIXME 20110307 m31: check where this class is used and move it or the error messages
 *
 * @author  Michael Meyling
 */
public interface HigherLogicalErrors extends ErrorCodes {

    /** Error code. */
    public static final int MODULE_IMPORT_CHECK_FAILED_CODE = 11231;

    /** Error message. */
    public static final String MODULE_IMPORT_CHECK_FAILED_TEXT
        = "import check failed: ";

    /** Error code. */
    public static final int PREDICATE_CALCULATION_FAILED_CODE = 40710;

    /** Error message. */
    public static final String PREDICATE_CALCULATION_FAILED_TEXT
        = "calculation for predicate failed: ";


}
