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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.se.common.ErrorCodes;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;


/**
 * An unexpected runtime exception was thrown during a visit.
 *
 * @author  Michael Meyling
 */
public class RuntimeVisitorException extends ModuleDataException implements ErrorCodes {

    /** Error (or warning) number for: unexpected runtime exception occurred. */
    public static final int UNEXPECTED_RUNTIME_EXCEPTION_CODE = 92001;

    /** Error (or warning) text for: unexpected runtime exception occurred. */
    public static final String UNEXPECTED_RUNTIME_EXCEPTION_MSG
        = "unexpected runtime exception occurred";


    /**
     * Constructor.
     *
     * @param   context     Error location.
     * @param   exception   This runtime exception occurred.
     */
    public RuntimeVisitorException(final ModuleContext context, final RuntimeException exception) {
        super(UNEXPECTED_RUNTIME_EXCEPTION_CODE, UNEXPECTED_RUNTIME_EXCEPTION_MSG,
            context, exception);
    }

}
