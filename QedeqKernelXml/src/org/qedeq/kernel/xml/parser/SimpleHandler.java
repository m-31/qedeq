/* $Id: SimpleHandler.java,v 1.11 2008/03/27 05:16:29 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

import java.lang.reflect.Method;
import java.net.URL;

import org.qedeq.base.trace.Trace;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;


/**
 * SAX handler that remembers {@link org.xml.sax.Locator} and possibly
 * encoding of XML document.
 *
 * @version $Revision: 1.11 $
 * @author  Michael Meyling
 */
public class SimpleHandler extends DefaultHandler {

    /** This class. */
    private static final Class CLASS = SimpleHandler.class;

    /** Locator for current row and column information. */
    private Locator locator;

    /** Which encoding was used to parse the document? */
    private String encoding;

    /** File that is parsed. */
    private URL url;

    /**
     * Constructor.
     */
    public SimpleHandler() {
        super();
    }

    /**
     * Receive a Locator object for document events.
     * Store the locator for use with other document events.
     *
     * @param   locator A locator for all SAX document events.
     * @see     org.xml.sax.ContentHandler#setDocumentLocator
     * @see     org.xml.sax.Locator
     */
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
        this.encoding = getEncoding(locator);
        Trace.paramInfo(CLASS, this, "setDocumentLocator(locator)", "encoding", encoding);
    }

    /**
     * Which encoding was used to parse the document?
     *
     * @return  Encoding for parsed document. Maybe <code>null</code>.
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * There is no way in SAX 2.0.0 or 2.0.1 to get information about the encoding; the SAX
     * Locator2 interface from the 1.1 extensions does provide methods to accomplish this,
     * Assuming <code>Locator</code> is an instance of the SAX Locator interface that supports
     * <code>Locator2</code> call we can get the information.
     *
     * @param   locator Locator.
     * @return  Encoding. Maybe <code>null</code>.
     */
    private static String getEncoding(final Locator locator) {
        String encoding = null;
        Method getEncoding = null;
        try {
            getEncoding = locator.getClass().getMethod("getEncoding", new Class[]{});
            if (getEncoding != null) {
                encoding = (String) getEncoding.invoke(locator, null);
            }
        } catch (Exception e) {
            // either this locator object doesn't have this
            // method, or we're on an old JDK
        }
        return encoding;
    }

    /**
     * Get document locator. This value set is set during parsing.
     *
     * @return  Locator. Maybe <code>null</code>.
     */
    protected Locator getLocator() {
        return locator;
    }

    /**
     * Set original file URL.
     *
     * @param   url     Data from this source is parsed. This URL is only for information. The
     *                  actual parsed data might be a local copy.
     */
    public final void setUrl(final URL url) {
        this.url = url;
    }

    /**
     * Get original file URL.
     *
     * @return  Data from this source is parsed. This URL is only for information. The
     *          actual parsed data might be a local copy.
     */
    public final URL getUrl() {
        return url;
    }

}
