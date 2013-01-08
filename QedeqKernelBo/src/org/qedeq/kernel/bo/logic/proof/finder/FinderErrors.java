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

package org.qedeq.kernel.bo.logic.proof.finder;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for proof finder package.
 *
 * @author  Michael Meyling
 */
public interface FinderErrors extends ErrorCodes {

    /** Error code. */
    public static final int PROOF_NOT_FOUND_CODE = 23791200;

    /** Error message. */
    public static final String PROOF_NOT_FOUND_TEXT
        = "Proof not found with current restrictions. During search we generated "
            + "this number of proof lines: ";


    /** Error code. */
    public static final int PROOF_FOUND_CODE = 23791210;

    /** Error message. */
    public static final String PROOF_FOUND_TEXT
        = "Proof found! During search we generated "
            + "this number of proof lines: ";


}
