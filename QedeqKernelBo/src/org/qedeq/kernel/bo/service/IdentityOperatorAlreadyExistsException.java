/* $Id: IdentityOperatorAlreadyExistsException.java,v 1.1 2008/07/26 07:58:28 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;


/**
 * The identity operator was defined twice.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class IdentityOperatorAlreadyExistsException extends ModuleDataException {

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     */
    public IdentityOperatorAlreadyExistsException(final int errorCode, final String message,
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
    public IdentityOperatorAlreadyExistsException(final int errorCode, final String message,
            final ModuleContext context, final ModuleContext reference) {
        super(errorCode, message, context, reference);
    }

}
