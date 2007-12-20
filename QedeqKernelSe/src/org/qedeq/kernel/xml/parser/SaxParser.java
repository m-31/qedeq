/* $Id: SaxParser.java,v 1.21 2007/08/28 21:10:15 m31 Exp $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Parser for XML files. This class uses features specific for Xerces.
 *
 * @version $Revision: 1.21 $
 * @author Michael Meyling
 */
public final class SaxParser {

    /** Namespaces feature id (http://xml.org/sax/features/namespaces). */
    private static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

    /** Validation feature id (http://xml.org/sax/features/validation). */
    private static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

    /** Schema validation feature id (http://apache.org/xml/features/validation/schema). */
    private static final String SCHEMA_VALIDATION_FEATURE_ID
        = "http://apache.org/xml/features/validation/schema";

    /** Schema full checking feature id
     * (http://apache.org/xml/features/validation/schema-full-checking). */
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID
        = "http://apache.org/xml/features/validation/schema-full-checking";

    /** Handler which deals with the XML contents. */
    private SaxDefaultHandler handler;

    /** SAX parser. */
    private XMLReader reader;

    /** Default handler for validation purpose only. */
    private final DefaultHandler deflt;

    /** Saved errors of parsing. */
    private DefaultSourceFileExceptionList exceptionList;

    /**
     * Constructor.
     *
     * @param   handler   Default handler for this application.
     * @throws  ParserConfigurationException    Severe parser configuration problem.
     * @throws  SAXException
     */
    public SaxParser(final SaxDefaultHandler handler) throws ParserConfigurationException,
            SAXException {
        super();

        this.handler = handler;
        this.deflt = new DefaultHandler();

        final String factoryImpl = System.getProperty("javax.xml.parsers.SAXParserFactory");
        if (factoryImpl == null) {
            System.setProperty("javax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);

        factory.setFeature(NAMESPACES_FEATURE_ID, true);
        factory.setFeature(VALIDATION_FEATURE_ID, true);
        try {
            factory.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
        } catch (SAXNotRecognizedException e) {
            Trace.trace(this, "constructor", e);
            // ignore
        }
        try {
            factory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
        } catch (SAXNotRecognizedException e) {
            Trace.trace(this, "constructor", e);
            // ignore
        }

        final SAXParser parser = factory.newSAXParser();
        if (!parser.isNamespaceAware()) {
            throw new ParserConfigurationException(
                "Current XML parser doesn't support namespaces.");
        }
        if (!parser.isValidating()) {
            throw new ParserConfigurationException(
                "Current XML parser doesn't support schema validation.");
        }

        reader = parser.getXMLReader();
        reader.setEntityResolver(new SaxEntityResolver(handler));

        // set parser features
        reader.setFeature(NAMESPACES_FEATURE_ID, true);
        reader.setFeature(VALIDATION_FEATURE_ID, true);
        try {
            reader.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
        } catch (SAXNotRecognizedException e) {
            Trace.trace(this, "constructor", e);
            // ignore
        }
        try {
            reader.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
        } catch (SAXNotRecognizedException e) {
            Trace.trace(this, "constructor", e);
            // ignore
        }
    }

    /**
     * Parse input source.
     *
     * @param   url             Parse data from this source.
     * @param   original        Original URL for the file. If this is <code>null</code> same as
     *                          file name.
     * @param   validateOnly    validate with {@link #deflt} or parse with {@link #handler}.
     * @throws  SourceFileExceptionList    Loading failed.
     */
    private void parse(final URL url, final URL original, final boolean validateOnly)
            throws SourceFileExceptionList {
        final String method = "parse(URL, boolean)";
        Trace.param(this, method, "url", url);

        InputStream in = null;
        try {
            in = url.openStream();
        } catch (IOException e) {
            throw new DefaultSourceFileExceptionList(e);
        }
        try {
            parse(original, validateOnly, in);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    Trace.trace(this, method, e);
                }
            }
        }
    }

    /**
     * Parse input source.
     *
     * @param   original        Original URL for the file. If this is <code>null</code> same as
     *                          file name.
     * @param   validateOnly    validate with {@link #deflt} or parse with {@link #handler}.
     * @param   in              Parse data from this source.
     * @throws  SourceFileExceptionList    Loading failed.
     */
    private void parse(final URL original, final boolean validateOnly, final InputStream in)
            throws SourceFileExceptionList {
        final String method = "parse(URL, boolean, InputStream)";
        BufferedReader dis = null;
        try {
            dis = new BufferedReader(new InputStreamReader(in));
            final InputSource input = new InputSource(dis);
            exceptionList = new DefaultSourceFileExceptionList();
            reader.setErrorHandler(new SaxErrorHandler(original, exceptionList));
            if (validateOnly) {
                reader.setContentHandler(deflt);
                reader.parse(input);
            } else {
                handler.setExceptionList(exceptionList);
                reader.setContentHandler(handler);
                handler.setUrl(original);
                reader.parse(input);
            }
        } catch (SAXException e) {
            final SourceFileException ex = new SourceFileException(e);
            if (exceptionList.size() <= 0) {
                exceptionList.add(ex);
            }
            throw exceptionList;
        } catch (IOException e) {
            exceptionList.add(e);
            throw exceptionList;
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (Exception e) {
                    Trace.trace(this, method, e);
                }
            }
        }
        if (exceptionList.size() > 0) {
            throw exceptionList;
        }
    }

    /**
     * Parses XML file.
     *
     * @param   fileName        File name.
     * @param   original        Original URL for the file. If this is <code>null</code> same as
     *                          file name.
     * @throws  SourceFileExceptionList    Loading failed.
     */
    public final void parse(final String fileName, final URL original)
            throws SourceFileExceptionList {
        final File file = new File(fileName);
        parse(file.getAbsoluteFile(), original);
    }

    /**
     * Parses the XML file.
     *
     * @param   file            File to parse.
     * @param   original        Original URL for the file. If this is <code>null</code> same as
     *                          file.
     * @throws  SourceFileExceptionList    Loading failed.
     */
    public final void parse(final File file, final URL original) throws SourceFileExceptionList {
        final URL url = IoUtility.toUrl(file.getAbsoluteFile());
        parse(url, original);
    }

    /**
     * Parses the XML file.
     *
     * @param   url             URL with File to parse.
     * @param   original        Original URL for the file. If this is <code>null</code> same as
     *                          file.
     * @throws  SourceFileExceptionList    Loading failed.
     */
    public final void parse(final URL url, final URL original) throws SourceFileExceptionList {
        final URL org = (original != null ? original : url);
        parse(url, org, true);
        parse(url, org, false);
    }

    /**
     * Parse input source.
     *
     * @param   in              Parse data from this source.
     * @param   validateOnly    Validate or parse with handler.
     * @throws  SourceFileExceptionList    Loading failed.
     */
    public void parse(final InputStream in, final boolean validateOnly)
            throws SourceFileExceptionList {

        // validateOnly    validate with {@link #deflt} or parse with {@link #handler}.
        parse(null, validateOnly, in);
    }

    /**
     * Get errors that occurred during last parsing.
     *
     * @return  List with collected Exceptions.
     */
    public DefaultSourceFileExceptionList getExceptionList() {
        return exceptionList;
    }

}
