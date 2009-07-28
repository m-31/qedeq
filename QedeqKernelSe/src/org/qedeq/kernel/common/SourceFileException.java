/* $Id: SourceFileException.java,v 1.5 2008/07/26 07:59:40 m31 Exp $
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

package org.qedeq.kernel.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.qedeq.base.io.IoUtility;


/**
 * Data validation error. Shows an error within a source file.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class SourceFileException extends QedeqException {

    /** Serialization information. */
    private static final long serialVersionUID = -4109767904038020052L;

    /** Start of error location. */
    private final SourceArea errorArea;

    /** End of error location. */
    private final SourceArea referenceArea;

    /**
     * Constructor.
     *
     * @param   errorCode   Error code.
     * @param   errorText   Error text.
     * @param   exception   Exception to wrap.
     * @param   errorArea   Error location.
     * @param   referenceArea   Error reference location.
     */
    public SourceFileException(final int errorCode, final String errorText,
            final Throwable exception, final SourceArea errorArea, final SourceArea referenceArea) {
        super(errorCode, errorText, exception);
        this.errorArea = errorArea;
        this.referenceArea = referenceArea;
    }


    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     * @param   errorArea   Error location.
     * @param   referenceArea   Error reference location.
     */
    public SourceFileException(final QedeqException exception, final SourceArea errorArea,
            final SourceArea referenceArea) {
        this(exception.getErrorCode(), exception.getMessage(), exception, errorArea, referenceArea);
    }


    /**
     * Constructor.
     *
     * @param   url         Parsed file.
     * @param   exception   Exception to wrap.
     */
    public SourceFileException(final String url, final Exception exception) {
        super(9997, exception.toString(), exception);     // TODO mime 20071116: error code refac
        errorArea = new SourceArea(url, new SourcePosition(url, 1, 1),
            new SourcePosition(url, 1, 1));
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   file        Parsed file.
     * @param   exception   Exception to wrap.
     * @deprecated  use URL
     */
    public SourceFileException(final File file, final Exception exception) {
        super(9998, exception.getMessage(), exception);     // TODO mime 20071116: error code refac
        final String url = IoUtility.toUrlString(file);
        errorArea = new SourceArea(url, new SourcePosition(url, 1, 1),
            new SourcePosition(url, 1, 1));
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     */
    public SourceFileException(final Exception exception) {
        super(9999, exception.getMessage(), exception);     // TODO mime 20071116: error code refac
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     */
    public SourceFileException(final Throwable exception) {
        super(1000, exception.toString(), exception);     // TODO mime 20071116: error code refac
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     */
    public SourceFileException(final IOException exception) {
        super(9997, exception.toString(), exception);     // TODO mime 20071116: error code refac
        errorArea = null;
        referenceArea = null;
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
            if (getCause() instanceof IOException) {
                return getCause().toString();
            }
            if (getCause().getCause() != null) {
                return getCause().getCause().getMessage();
            }
            return getCause().getMessage();
        }
        return super.getMessage();
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
        buffer.append(getErrorCode() + ": " + getMessage());
        if (errorArea != null && errorArea.getStartPosition() != null) {
            final SourcePosition start = errorArea.getStartPosition();
            buffer.append("\n");
            buffer.append(errorArea.getAddress() + ":" + start.getLine() + ":"
                + start.getColumn());
        }
        return buffer.toString();
    }

    public String toString() {
        return getDescription();
    }
}
