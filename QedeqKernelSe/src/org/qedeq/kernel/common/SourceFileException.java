/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.io.IoUtility;


/**
 * Data validation error. Shows an error within a source file.
 *
 * @author  Michael Meyling
 */
public class SourceFileException extends QedeqException {

    /** Serialization information. */
    private static final long serialVersionUID = -4109767904038020052L;

    /** Error code of this Exception. */
    private final Plugin plugin;

    /** Start of error location. */
    private final SourceArea errorArea;

    /** End of error location. */
    private final SourceArea referenceArea;


    /**
     * Constructor.
     *
     * @param   plugin      This plugin generated the error.
     * @param   errorCode   Error code.
     * @param   errorText   Error text.
     * @param   exception   Exception to wrap.
     * @param   errorArea   Error location.
     * @param   referenceArea   Error reference location.
     */
    public SourceFileException(final Plugin plugin, final int errorCode, final String errorText,
            final Throwable exception, final SourceArea errorArea, final SourceArea referenceArea) {
        super(errorCode, errorText, exception);
        this.plugin = plugin;
        this.errorArea = errorArea;
        this.referenceArea = referenceArea;
    }

    /**
     * Constructor.
     *
     * @param   plugin      This plugin generated the error.
     * @param   exception   Exception to wrap.
     * @param   errorArea   Error location.
     * @param   referenceArea   Error reference location.
     */
    public SourceFileException(final Plugin plugin, final QedeqException exception,
            final SourceArea errorArea, final SourceArea referenceArea) {
        this(plugin, exception.getErrorCode(), exception.getMessage(), exception,
            errorArea, referenceArea);
    }

    /**
     * Constructor.
     *
     * @param   plugin      This plugin generated the error.
     * @param   url         Parsed file.
     * @param   exception   Exception to wrap.
     */
    public SourceFileException(final Plugin plugin, final String url, final Exception exception) {
        super(9997, exception.toString(), exception);     // TODO mime 20071116: error code refactor
        this.plugin = plugin;
        errorArea = new SourceArea(url, new SourcePosition(url, 1, 1),
            new SourcePosition(url, 1, 1));
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   plugin      This plugin generated the error.
     * @param   file        Parsed file.
     * @param   exception   Exception to wrap.
     * @deprecated  use URL
     */
    public SourceFileException(final Plugin plugin, final File file, final Exception exception) {
        super(9998, exception.getMessage(), exception);     // TODO mime 20071116: error code refactor
        this.plugin = plugin;
        final String url = IoUtility.toUrlString(file);
        errorArea = new SourceArea(url, new SourcePosition(url, 1, 1),
            new SourcePosition(url, 1, 1));
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   plugin      This plugin generated the error.
     * @param   exception   Exception to wrap.
     */
    public SourceFileException(final Plugin plugin, final Exception exception) {
        super(9999, exception.getMessage(), exception);     // TODO mime 20071116: error code refactor
        this.plugin = plugin;
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   plugin      This plugin generated the error.
     * @param   exception   Exception to wrap.
     */
    public SourceFileException(final Plugin plugin, final Throwable exception) {
        super(1000, exception.toString(), exception);     // TODO mime 20071116: error code refac
        this.plugin = plugin;
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Constructor.
     *
     * @param   plugin      This plugin generated the error.
     * @param   exception   Exception to wrap.
     */
    public SourceFileException(final Plugin plugin, final IOException exception) {
        super(9997, exception.toString(), exception);     // TODO mime 20071116: error code refac
        this.plugin = plugin;
        errorArea = null;
        referenceArea = null;
    }

    /**
     * Get plugin that found the error.
     *
     * @return  Plugin.
     */
    public Plugin getPlugin() {
        return plugin;
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
