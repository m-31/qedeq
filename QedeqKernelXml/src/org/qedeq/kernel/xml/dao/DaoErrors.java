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

package org.qedeq.kernel.xml.dao;

import org.qedeq.kernel.common.ErrorCodes;

/**
 * Error codes and messages for DAO package.
 *
 * @author  Michael Meyling
 */
public interface DaoErrors extends ErrorCodes {

    /** Error code. */
    public static final int WRITING_MODULE_FILE_FAILED_CODE = 892771000;

    /** Error message. */
    public static final String WRITING_MODULE_FILE_FAILED_TEXT
        = "Writing of following QEDEQ module file failed: ";

    /** Error code. */
    public static final int PARSER_CONFIGURATION_ERROR_CODE = 892773010;

    /** Error message. */
    public static final String PARSER_CONFIGURATION_ERROR_TEXT
        = "Parser configuration error.";


    /** Error code. */
    public static final int PARSER_CONFIGURATION_OPTION_ERROR_CODE = 754123030;

    /** Error message. */
    public static final String PARSER_CONFIGURATION_OPTION_ERROR_TEXT
        = "Option not recognized or supported.";

}
