/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

import java.io.IOException;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.utility.EqualsUtility;


/**
 * Data validation error. Shows an error or warning within a source file.
 *
 * @author  Michael Meyling
 */
public class SourceFileException extends QedeqException {

    /** Serialization information. */
    private static final long serialVersionUID = -4109767904038020052L;

    /** Error code of this Exception. */
    private final Service service;

    /** Start of error location. */
    private final SourceArea errorArea;

    /** End of error location. */
    private final SourceArea referenceArea;


    /**
     * Constructor.
     *
     * @param   service     This service generated the error.
     * @param   errorCode   Error code.
     * @param   errorText   Error text.
     * @param   exception   Exception to wrap.
     * @param   errorArea   Error location.
     * @param   referenceArea   Error reference location.
     */
    public SourceFileException(final Service service, final int errorCode, final String errorText,
            final Throwable exception, final SourceArea errorArea, final SourceArea referenceArea) {
        super(errorCode, errorText, exception);
        this.service = service;
        this.errorArea = errorArea;
        this.referenceArea = referenceArea;
    }

    /**
     * Constructor.
     *
     * @param   service      This service generated the error.
     * @param   exception   Exception to wrap.
     * @param   errorArea   Error location.
     * @param   referenceArea   Error reference location.
     */
    public SourceFileException(final Service service, final QedeqException exception,
            final SourceArea errorArea, final SourceArea referenceArea) {
        this(service, exception.getErrorCode(), exception.getMessage(), exception,
            errorArea, referenceArea);
    }

    /**
     * Get service that found the error.
     *
     * @return  Service.
     */
    public Service getService() {
        return service;
    }

    /**
     * Get position information about error location.
     *
     * @return  Error location position.
     */
    public SourceArea getSourceArea() {
        return errorArea;
    }

    /**
     * Get additional position information about another associated location.
     *
     * @return  Additional error location position.
     */
    public SourceArea getReferenceArea() {
        return referenceArea;
    }

    public String getMessage() {
        if (getCause() != null) {
            if (getCause().getCause() != null) {
                return getText(super.getMessage(), getCause().getCause());
            }
            return getText(super.getMessage(), getCause());
        }
        return super.getMessage();
    }

    /**
     * Format error message.
     *
     * @param   message Normal error message.
     * @param   cause   This caused the exception. Must not be <code>null</code>.
     * @return  Error message.
     */
    private String getText(final String message, final Throwable cause) {
        if (message != null) {
            if (message.equals(cause.getMessage())) {
                return message;
            }
            if (cause instanceof IOException || cause instanceof NullPointerException) {
                return message + "; " + cause.toString();
            }
            return message + "; " + cause.getMessage();
        }
        return cause.toString();
    }

    /**
     * Get detailed error description.
     * The first line contains {@link #getErrorCode()} and {@link #getMessage()}.
     * The second line contains the local address, the line and column.
     *
     * @return  Error description.
     */
    public String getDescription() {
        final StringBuffer buffer = new StringBuffer();
        if (errorArea != null && errorArea.getStartPosition() != null) {
            final SourcePosition start = errorArea.getStartPosition();
            buffer.append(errorArea.getAddress() + ":" + start.getRow() + ":"
                    + start.getColumn());
            final SourcePosition end = errorArea.getEndPosition();
            if (end != null) {
                buffer.append(":" + end.getRow() + ":" + end.getColumn());
            }
            buffer.append("\n");
        }
        buffer.append("\t" + getErrorCode() + ": " + getMessage());
        return buffer.toString();
    }

    public final int hashCode() {
        return getErrorCode() ^ (errorArea != null ? errorArea.hashCode() : 13)
            ^ (getService() != null ? getService().hashCode() : 131)
            ^ (getMessage() != null ? getMessage().hashCode() : 499);
    }

    public final boolean equals(final Object obj) {
        if (!(obj instanceof SourceFileException)) {
            return false;
        }
        final SourceFileException other = (SourceFileException) obj;
        return  (getErrorCode() == other.getErrorCode())
            &&  EqualsUtility.equals(getService(), other.getService())
            &&  EqualsUtility.equals(getMessage(), other.getMessage())
            &&  EqualsUtility.equals(errorArea, other.errorArea);
    }

    public final String toString() {
        return getDescription();
    }

}
