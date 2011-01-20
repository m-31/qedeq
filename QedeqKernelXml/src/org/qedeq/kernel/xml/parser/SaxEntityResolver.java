/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.context.KernelContext;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Resolve QEDEQ module XML schema.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class SaxEntityResolver implements EntityResolver {

    /** This class. */
    private static final Class CLASS = SaxEntityResolver.class;

    /** Handler that parses the document. */
    private final SaxDefaultHandler handler;

    /**
     * Constructor.
     *
     * @param   handler     This one does the parsing.
     */
    public SaxEntityResolver(final SaxDefaultHandler handler) {
        this.handler = handler;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity(final String publicId, final String systemId)
            throws FileNotFoundException, SAXException {
        final String method = "resolveEntity";
        Trace.param(CLASS, this, method, "systemId", systemId);
        Trace.param(CLASS, this, method, "publicId", publicId);
        if ((systemId == null)) {
            return null;
        }
        if (systemId.equals("http://www.qedeq.org/" + KernelContext.getInstance()
                .getKernelVersionDirectory() + "/xml/qedeq.xsd")) {
            InputStream in = SaxEntityResolver.class.getResourceAsStream(
                "/org/qedeq/kernel/xml/schema/qedeq.xsd");
            if (in == null) {
                throw new FileNotFoundException("/org/qedeq/kernel/xml/schema/qedeq.xsd");
            }
            InputSource inputSource = new InputSource(in);
            inputSource.setPublicId(publicId);
            inputSource.setSystemId(systemId);
            return inputSource;
        } else if (systemId.equals("http://www.qedeq.org/"
            + KernelContext.getInstance().getKernelVersionDirectory()
                + "/xml/parser.xsd")) {
            InputStream in = SaxEntityResolver.class.getResourceAsStream(
                "/org/qedeq/kernel/xml/schema/parser.xsd");
            if (in == null) {
                throw new FileNotFoundException("/org/qedeq/kernel/xml/schema/parser.xsd");
            }
            InputSource inputSource = new InputSource(in);
            inputSource.setPublicId(publicId);
            inputSource.setSystemId(systemId);
            return inputSource;
        }
        Trace.trace(CLASS, this, method, "unknown entity");
        SAXParseException sax = handler.createSAXParseException(
            "this kernel supports only the following XSDs: "
            + "\"http://www.qedeq.org/" + KernelContext.getInstance()
            .getKernelVersionDirectory() + "/xml/qedeq.xsd\"" + " and \n"
            + "\"http://www.qedeq.org/"
            + KernelContext.getInstance().getKernelVersionDirectory()
            + "/xml/parser.xsd\" "
            + "\nbut the document \"" + handler.getUrl() + "\" referenced \n\"" + systemId + "\"");
        throw sax;
        // LATER mime 20070604: this error should have correct location information,
        // but is hasn't! this problem should be solved later...
    }
}
