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


}
