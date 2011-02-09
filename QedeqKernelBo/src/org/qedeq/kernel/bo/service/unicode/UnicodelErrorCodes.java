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
 * Contains the error codes for conversion into unicode.
 *
 * @author  Michael Meyling
 */
public interface UnicodelErrorCodes extends ErrorCodes {

    /** Error (or warning) number for: Node reference not found for. */
    public static final int NODE_REFERENCE_NOT_FOUND_CODE = 610007;

    /** Error (or warning) text for: Node reference not found for. */
    public static final String NODE_REFERENCE_NOT_FOUND_TEXT
        = "node reference not found for: ";


    /** Error (or warning) number for: Node reference not found for. */
    public static final int NODE_REFERENCE_HAS_MORE_THAN_TWO_DOTS_CODE = 610011;

    /** Error (or warning) text for: Node reference not found for. */
    public static final String NODE_REFERENCE_HAS_MORE_THAN_TWO_DOTS_TEXT
        = "node reference has more than two dots: ";

    /** Error (or warning) number for: Module reference not found for. */
    public static final int MODULE_REFERENCE_NOT_FOUND_CODE = 6100017;

    /** Error (or warning) text for: Module reference not found for. */
    public static final String MODULE_REFERENCE_NOT_FOUND_TEXT
        = "module reference not found for: ";


}
