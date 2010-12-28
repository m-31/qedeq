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

package org.qedeq.kernel.bo.service;

import org.qedeq.kernel.common.ErrorCodes;

/**
 * Error codes and messages for service package.
 *
 * @author  Michael Meyling
 */
public interface ServiceErrors extends ErrorCodes {

    /** Error code. */
    public static final int IDENTITY_OPERATOR_ALREADY_EXISTS = 123476;

    /** Error message. */
    public static final String IDENTITY_OPERATOR_ALREADY_EXISTS_TEXT
        = "identity operator already defined with";


    /** Error code. */
    public static final int IO_ERROR = 9090;

    /** Error message. */
    public static final String IO_ERROR_TEXT
        = "Reading or writing failed.";


    /** Error code. */
    public static final int RUNTIME_ERROR = 90100;

    /** Error message. */
    public static final String RUNTIME_ERROR_TEXT
        = "Programming error occured.";


    /** Error code. */
    public static final int QEDEQ_MODULE_NOT_LOADED_CODE = 90500;

    /** Error message. */
    public static final String QEDEQ_MODULE_NOT_LOADED_MSG
        = "QEDEQ module couldn't be loaded.";


    /** Error code. */
    public static final int EXCEPTION_ERROR = 90999;

    /** Error message. */ // FIXME 20101228 m31: what for is this?
    public static final String EXCEPTION_ERROR_TEXT
        = "Exception occured.";


}
