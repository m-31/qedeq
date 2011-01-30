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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for module package.
 *
 * @author  Michael Meyling
 */
public interface ModuleErrors extends ErrorCodes {

    /** Error code. */
    public static final int QEDEQ_MODULE_NOT_LOADED_CODE = 90500;

    /** Error message. */
    public static final String QEDEQ_MODULE_NOT_LOADED_TEXT
        = "QEDEQ module couldn't be loaded.";


}
