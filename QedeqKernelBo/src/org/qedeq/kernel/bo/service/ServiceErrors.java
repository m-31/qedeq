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

package org.qedeq.kernel.bo.service;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for service package.
 *
 * @author  Michael Meyling
 */
public interface ServiceErrors extends ErrorCodes {

    /** Error code. */
    public static final int IO_ERROR_CODE = 9090;

    /** Error message. */
    public static final String IO_ERROR_TEXT
        = "Reading or writing failed.";


    /** Error code. */
    public static final int RUNTIME_ERROR_CODE = 90100;

    /** Error message. */
    public static final String RUNTIME_ERROR_TEXT
        = "Programming error occured.";


    /** Error code. */
    public static final int LOADING_FROM_FILE_BUFFER_FAILED_CODE = 90700;

    /** Error message. */
    public static final String LOADING_FROM_FILE_BUFFER_FAILED_TEXT
        = "Loading module from file buffer failed.";


    /** Error code. */
    public static final int LOADING_FROM_LOCAL_FILE_FAILED_CODE = 90710;

    /** Error message. */
    public static final String LOADING_FROM_LOCAL_FILE_FAILED_TEXT
        = "Loading module from local file failed.";


    /** Error code. */
    public static final int LOADING_FROM_WEB_FAILED_CODE = 90720;

    /** Error message. */
    public static final String LOADING_FROM_WEB_FAILED_TEXT
        = "Loading module from web failed.";


    /** Error code. */
    public static final int RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_CODE = 90722;

    /** Error message. */
    public static final String RECURSIVE_IMPORT_OF_MODULES_IS_FORBIDDEN_TEXT
        = "Recursive import of modules is forbidden, label: ";


    /** Error code. */
    public static final int IMPORT_OF_MODULE_FAILED_CODE = 90723;

    /** Error message. */
    public static final String IMPORT_OF_MODULE_FAILED_TEXT
        = "Import of module failed, label: ";


}
