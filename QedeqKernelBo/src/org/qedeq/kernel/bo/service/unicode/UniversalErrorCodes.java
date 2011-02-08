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

package org.qedeq.kernel.bo.service.unicode;

import org.qedeq.kernel.se.common.ErrorCodes;


/**
 * Contains the error codes for unicode visiting.
 *
 * @author  Michael Meyling
 */
public interface UniversalErrorCodes extends ErrorCodes {

    /** Error (or warning) number for: Node reference not found for. */
    public static final int NODE_REFERENCE_NOT_FOUND_CODE = 610007;

    /** Error (or warning) text for: Node reference not found for. */
    public static final String NODE_REFERENCE_NOT_FOUND_TEXT
        = "node reference not found for: ";

}
