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

package org.qedeq.kernel.xml.parser;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Error handler for XML parsing.
 *
 * @author  Michael Meyling
 */
public class SaxErrorHandler implements ErrorHandler {

    /** This class. */
    private static final Class CLASS = SaxErrorHandler.class;

    /** Error code for Exceptions thrown by the SAXParser. */
    public static final int SAX_PARSER_EXCEPTION = 9001;

    /** This plugin is currently working. */
    private final Plugin plugin;

    /** File that is parsed. */
    private final String url;

    /** Collects errors. */
    private final SourceFileExceptionList list;

    /**
     * Constructor.
     *
     * @param   plugin  This plugin generated the error.
     * @param   url     URL that is parsed.
     * @param   list    Collector for the SAX exceptions.
     */
    public SaxErrorHandler(final Plugin plugin, final String url,
            final SourceFileExceptionList list) {
        super();
        Trace.param(CLASS, this, "SaxErrorHandler", "url", url);
        this.plugin = plugin;
        this.url = url;
        this.list = list;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public final void warning(final SAXParseException e) throws SAXException {
        final SourceFileException sf = new SourceFileException(plugin, SAX_PARSER_EXCEPTION, e.getMessage(),
            e, new SourceArea(url, new SourcePosition(e.getLineNumber(), 1),
            new SourcePosition(e.getLineNumber(), e.getColumnNumber())), null);
        Trace.trace(CLASS, this, "warning", e);
        Trace.trace(CLASS, this, "warning", sf);
        list.add(sf);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public final void error(final SAXParseException e) throws SAXException {
        final SourceFileException sf = new SourceFileException(plugin, SAX_PARSER_EXCEPTION, e.getMessage(),
            e, new SourceArea(url, new SourcePosition(e.getLineNumber(), 1),
            new SourcePosition(e.getLineNumber(), e.getColumnNumber())), null);
        Trace.trace(CLASS, this, "error", e);
        Trace.trace(CLASS, this, "error", sf);
        list.add(sf);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public final void fatalError(final SAXParseException e) throws SAXException {
        final SourceFileException sf = new SourceFileException(plugin, SAX_PARSER_EXCEPTION, e.getMessage(),
            e, new SourceArea(url, new SourcePosition(e.getLineNumber(), 1),
            new SourcePosition(e.getLineNumber(), e.getColumnNumber())), null);
        Trace.trace(CLASS, this, "fatalError", e);
        Trace.trace(CLASS, this, "fatalError", sf);
        list.add(sf);
    }

}
