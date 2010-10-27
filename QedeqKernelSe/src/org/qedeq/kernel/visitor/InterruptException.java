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

package org.qedeq.kernel.visitor;

import org.qedeq.kernel.common.ErrorCodes;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;


/**
 * An interruption was set during a visit.
 *
 * @author  Michael Meyling
 */
public class InterruptException extends ModuleDataException implements ErrorCodes {

    /** Error (or warning) number for: unexpected runtime exception occurred. */
    public static final int THREAD_INTERUPTED_EXCEPTION_CODE = 87001;

    /** Error (or warning) text for: unexpected runtime exception occurred. */
    public static final String THREAD_INTERUPTED_EXCEPTION_MSG
        = "Process execution was canceled";


    /**
     * Constructor.
     *
     * @param   context     Error location.
     */
    public InterruptException(final ModuleContext context) {
        super(THREAD_INTERUPTED_EXCEPTION_CODE, THREAD_INTERUPTED_EXCEPTION_MSG,
            context, new RuntimeException(THREAD_INTERUPTED_EXCEPTION_MSG));
    }

}
