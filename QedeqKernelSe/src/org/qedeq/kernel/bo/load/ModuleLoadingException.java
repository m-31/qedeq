/* $Id: ModuleLoadingException.java,v 1.2 2007/04/12 23:50:09 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.load;

import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.bo.module.ModuleDataException;


/**
 * Error during module loading.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public class ModuleLoadingException extends ModuleDataException {

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     * @param   e           Error cause.
     */
    public ModuleLoadingException(final int errorCode, final String message,
            final ModuleContext context, final Exception e) {
        super(errorCode, message, context, e);
    }

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     * @param   reference   Reference location.
     */
    public ModuleLoadingException(final int errorCode, final String message,
            final ModuleContext context, final ModuleContext reference) {
        super(errorCode, message, context, reference);
    }

}
