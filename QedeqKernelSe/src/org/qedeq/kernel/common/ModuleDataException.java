/* $Id: ModuleDataException.java,v 1.3 2007/04/12 23:50:04 m31 Exp $
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
 * Data validation error for a QEDEQ module. An error has always a reference to its
 * location. Maybe an additional reference for another location is provided.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public abstract class ModuleDataException extends QedeqException {

    /** Error location. */
    private ModuleContext context;

    /** Reference location to explain the error. */
    private ModuleContext referenceContext;

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     * @param   referenceContext  Reference location.
     * @param   cause       Detailed exception information.
     */
    public ModuleDataException(final int errorCode, final String message,
            final ModuleContext context, final ModuleContext referenceContext,
            final Exception cause) {
        super(errorCode, message, cause);
        // use copy constructor
        this.context = (context == null ? null : new ModuleContext(context));
        this.referenceContext = (referenceContext == null ? null
                : new ModuleContext(referenceContext));
    }

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     * @param   referenceContext  Reference location.
     */
    public ModuleDataException(final int errorCode, final String message,
            final ModuleContext context, final ModuleContext referenceContext) {
        super(errorCode, message);
        // use copy constructor
        this.context = (context == null ? null : new ModuleContext(context));
        this.referenceContext = (referenceContext == null ? null
                : new ModuleContext(referenceContext));
    }

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     * @param   cause       Detailed exception information.
     */
    public ModuleDataException(final int errorCode, final String message,
            final ModuleContext context, final Exception cause) {
        super(errorCode, message, cause);
        // use copy constructor
        this.context = (context == null ? null : new ModuleContext(context));
        this.referenceContext = null;
    }

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     */
    public ModuleDataException(final int errorCode, final String message,
            final ModuleContext context) {
        super(errorCode, message);
        // use copy constructor
        this.context = (context == null ? null : new ModuleContext(context));
        this.referenceContext = null;
    }

    /**
     * Get context information about error location.
     *
     * @return  Error location context.
     */
    public final ModuleContext getContext() {
        return context;
    }

    /**
     * Get additional context information about another associated location.
     *
     * @return  Additional error location context.
     */
    public final ModuleContext getReferenceContext() {
        return referenceContext;
    }

}
