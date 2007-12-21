/* $Id: QedeqException.java,v 1.2 2007/12/21 23:33:47 m31 Exp $
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

package org.qedeq.kernel.common;


/**
 * Base class for all exceptions of this application.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public abstract class QedeqException extends Exception {

    /** Error code of this Exception. */
    private final int errorCode;

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   cause       Detailed exception information.
     */
    public QedeqException(final int errorCode, final String message,
            final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     */
    public QedeqException(final int errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Get error code.
     *
     * @return  Error code.
     */
    public final int getErrorCode() {
        return errorCode;
    }

}
