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

package org.qedeq.kernel.xml.test;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


/**
 * Error handler for XML parsing.
 *
 * @author  Michael Meyling
 */
public class SaxErrorHandler implements ErrorHandler {

    /** Collects errors. */
    private ExceptionList list;

    /**
     * Constructor.
     *
     * @param   list    Collector for the SAX exceptions.
     */
    public SaxErrorHandler(final ExceptionList list) {
        super();
        this.list = list;
    }


    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) {
        list.add(e);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e) {
        list.add(e);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) {
        list.add(e);
    }
}
