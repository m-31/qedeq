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

import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;

/**
 * Exception for QEDEQ into unicode conversion problems.
 *
 * @author  Michael Meyling
 */
public class UnicodeException extends ModuleDataException {

    /**
     * Constructs an exception.
     *
     * @param   code    Error (or warning) code.
     * @param   message Error message.
     * @param   context Current context.
     */
    public UnicodeException(final int code, final String message, final ModuleContext context) {
        super(code, message, context);
    }

}
