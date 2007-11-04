/* $Id: XmlFileException.java,v 1.1 2007/04/12 23:50:10 m31 Exp $
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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.ReplaceUtility;
import org.qedeq.kernel.utility.TextInput;
import org.xml.sax.SAXException;


/**
 * Data validation error. Occurs if a set or add method leads to wrong or inconsistent data.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class XmlFileException extends Exception {

    /** Serialization information. */
    private static final long serialVersionUID = -4109767904038020052L;

    /** Error code of this Exception. */
    private final int errorCode;

    /** Start of error location. */
    private final SourceArea errorArea;

    /** End of error location. */
    private final SourceArea referenceArea;

    /** Referenced line with marker. */
    private String line;

    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     * @param   errorArea   Error location.
     * @param   referenceArea   Error reference location.
     */
    public XmlFileException(final QedeqException exception, final SourceArea errorArea,
            final SourceArea referenceArea) {
        super(exception);
        this.errorCode = exception.getErrorCode();
        this.errorArea = errorArea;
        this.referenceArea = referenceArea;
    }


    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     */
    public XmlFileException(final SyntaxException exception) {
        super(exception);
        errorCode = exception.getErrorCode();
        errorArea = new SourceArea(exception.getErrorPosition().getAddress(),
            exception.getErrorPosition().getLocalAddress(), exception.getErrorPosition(), null);
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   url         Parsed file.
     * @param   exception   Exception to wrap.
     */
    public XmlFileException(final URL url, final Exception exception) {
        super(exception);
        this.errorCode = 9997;
        errorArea = new SourceArea(url, new SourcePosition(url, 1, 1), null);
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   file        Parsed file.
     * @param   exception   Exception to wrap.
     */
    public XmlFileException(final File file, final Exception exception) {
        super(exception);
        this.errorCode = 9998;
        final URL url = IoUtility.toUrl(file);
        errorArea = new SourceArea(url, new SourcePosition(url, 1, 1), null);
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     */
    public XmlFileException(final Exception exception) {
        super(exception);
        this.errorCode = 9999;
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     */
    public XmlFileException(final Throwable exception) {
        super(exception);
        this.errorCode = 10000;
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     */
    public XmlFileException(final IOException exception) {
        super(exception);
        this.errorCode = 9997;
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   exception   Exception to wrap.
     */
    public XmlFileException(final SAXException exception) {
        super(exception);
        this.errorCode = SyntaxException.SAX_PARSER_EXCEPTION;
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Get position information about error location.
     *
     * @return  Error location position.
     */
    public final SourceArea getSourceArea() {
        return errorArea;
    }

    /**
     * Get additional position information about another associated location.
     *
     * @return  Additional error location position.
     */
    public final SourceArea getReferenceArea() {
        return referenceArea;
    }

    /**
     * Get error code.
     *
     * @return  Error code.
     */
    public final int getErrorCode() {
        return errorCode;
    }

    /**
     * Get line that is referenced by {@link #getSourceArea()}.
     *
     * @return  Referenced line.
     */
    public final String getLine() {
        if (line == null) {
            line = "";
            try {
                final TextInput input = new TextInput(errorArea.getLocalAddress());
                input.setRow(errorArea.getStartPosition().getLine());
                input.setColumn(errorArea.getStartPosition().getColumn());
                line = input.getLine();
            } catch (Exception e) {
                // ignore
            }
        }
        return line;
    }

    public final String getMessage() {
        if (getCause() != null) {
            if (getCause().getCause() != null) {
                return getCause().getCause().getMessage();
            }
            return getCause().getMessage();
        }
        return "";
    }

    /**
     * Get detailed error description.
     * The first line contains {@link #getErrorCode()} and {@link #getMessage()}.
     * The second line contains the local address, the line and column.
     * Third line is the result or {@link #getLine()}.
     * In the fourth line the row position for the third line is marked.
     *
     * <p>TODO mime 20070219: rework description: add end (and perhaps reference) information
     *
     * @return  Error description.
     */
    public final String getDescription() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getErrorCode() + ": " + getMessage());
        if (errorArea != null && errorArea.getStartPosition() != null) {
            final SourcePosition start = errorArea.getStartPosition();
            buffer.append("\n");
            buffer.append(start.getLocalAddress() + ":" + start.getLine() + ":"
                + start.getColumn());
            buffer.append("\n");
            buffer.append(ReplaceUtility.replace(getLine(), "\t", " "));
            buffer.append("\n");
            final StringBuffer whitespace = IoUtility.getSpaces(start.getColumn() - 1);
            buffer.append(whitespace);
            buffer.append("^");
        }
        return buffer.toString();
    }

    public final String toString() {
        return getDescription();
    }
}
