/* $Id: SaxErrorHandler.java,v 1.14 2007/05/10 00:37:52 m31 Exp $
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

package org.qedeq.kernel.xml.parser;

import java.net.URL;

import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourcePosition;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Error handler for XML parsing.
 *
 * @version $Revision: 1.14 $
 * @author  Michael Meyling
 */
public class SaxErrorHandler implements ErrorHandler {

    /** Error code for Exceptions thrown by the SAXParser. */
    public static final int SAX_PARSER_EXCEPTION = 9001;

    /** File that is parsed. */
    private final URL url;

    /** Collects errors. */
    private final DefaultSourceFileExceptionList list;

    /**
     * Constructor.
     *
     * @param   url     URL that is parsed.
     * @param   list    Collector for the SAX exceptions.
     */
    public SaxErrorHandler(final URL url, final DefaultSourceFileExceptionList list) {
        super();
        this.url = url;
        this.list = list;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public final void warning(final SAXParseException e) throws SAXException {
        final SourceFileException sf = new SourceFileException(SAX_PARSER_EXCEPTION, e.getMessage(),
            e, new SourceArea(url, new SourcePosition(url, e.getLineNumber(),
                e.getColumnNumber()), null), null);
        list.add(sf);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public final void error(final SAXParseException e) throws SAXException {
        final SourceFileException sf = new SourceFileException(SAX_PARSER_EXCEPTION, e.getMessage(),
            e, new SourceArea(url, new SourcePosition(url, e.getLineNumber(),
                e.getColumnNumber()), null), null);
        list.add(sf);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public final void fatalError(final SAXParseException e) throws SAXException {
        final SourceFileException sf = new SourceFileException(SAX_PARSER_EXCEPTION, e.getMessage(),
            e, new SourceArea(url, new SourcePosition(url, e.getLineNumber(),
                e.getColumnNumber()), null), null);
        list.add(sf);
    }

}
