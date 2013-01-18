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

package org.qedeq.kernel.se.common;

import org.qedeq.base.utility.EqualsUtility;

/**
 * Data validation error for a QEDEQ module. An error has always a reference to its
 * location. Maybe an additional reference for another location is provided.
 * <br/>
 * An error code, message and location should fix a certain data validation error, so only these
 * informations are used for {@link #hashCode()} and {@link #equals(Object)}.
 *
 * @author  Michael Meyling
 */
public abstract class ModuleDataException extends QedeqException {

    /** Error location. */
    private final ModuleContext context;

    /** Reference location to explain the error. */
    private final ModuleContext referenceContext;

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
     * Constructor. Copies module context, so original can be changed.
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

    public final int hashCode() {
        return getErrorCode() ^ context.hashCode() ^ getMessage().hashCode();
    }

    public final boolean equals(final Object obj) {
        if (!(obj instanceof ModuleDataException)) {
            return false;
        }
        final ModuleDataException other = (ModuleDataException) obj;
        return  (getErrorCode() == other.getErrorCode())
            && EqualsUtility.equals(getMessage(), other.getMessage())
            && EqualsUtility.equals(context, other.context);
    }


}
