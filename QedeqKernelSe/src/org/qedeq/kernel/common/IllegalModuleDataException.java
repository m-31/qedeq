/* $Id: IllegalModuleDataException.java,v 1.1 2008/03/27 05:16:25 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.common;


/**
 * Data validation error for a QEDEQ module. Occurs if a set or add method leads to wrong or
 * inconsistent data.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class IllegalModuleDataException extends ModuleDataException {

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     * @param   referenceContext  Reference location.
     * @param   cause       Detailed exception information.
     */
    public IllegalModuleDataException(final int errorCode, final String message,
            final ModuleContext context, final ModuleContext referenceContext,
            final Exception cause) {
        super(errorCode, message, context, referenceContext, cause);
    }

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     * @param   cause       Detailed exception information.
     */
    public IllegalModuleDataException(final int errorCode, final String message,
            final ModuleContext context, final Exception cause) {
        super(errorCode, message, context, cause);
    }

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     */
    public IllegalModuleDataException(final int errorCode, final String message,
            final ModuleContext context) {
        super(errorCode, message, context);
    }

}
