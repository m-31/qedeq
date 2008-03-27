/* $Id: CheckRequiredModuleException.java,v 1.2 2008/03/27 05:16:24 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;


/**
 * Check failed error for an required QEDEQ module.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public class CheckRequiredModuleException extends ModuleDataException {

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     */
    public CheckRequiredModuleException(final int errorCode, final String message,
            final ModuleContext context) {
        super(errorCode, message, context);
    }

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     * @param   reference   Reference location.
     */
    public CheckRequiredModuleException(final int errorCode, final String message,
            final ModuleContext context, final ModuleContext reference) {
        super(errorCode, message, context, reference);
    }

}
