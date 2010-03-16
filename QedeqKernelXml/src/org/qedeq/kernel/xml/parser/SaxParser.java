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
package org.qedeq.kernel.xml.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;


/**
 * Parser for XML files. This class uses features specific for Xerces.
 *
 * @author Michael Meyling
 */
public final class SaxParser {

    /** This class. */
    private static final Class CLASS = SaxParser.class;

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

    /** Simple handler for validation purpose only. */
    private final SimpleHandler deflt;

    /** Saved errors of parsing. */
    private DefaultSourceFileExceptionList exceptionList;

    /** Plugin we work for. */
    private Plugin plugin;

    /**
     * Constructor.
     *
     * @param   plugin    We work for this plugin.
     * @param   handler   Default handler for this application.
     * @throws  ParserConfigurationException    Severe parser configuration problem.
     * @throws  SAXException
     */
    public SaxParser(final Plugin plugin, final SaxDefaultHandler handler)
            throws ParserConfigurationException, SAXException {
        super();

        this.handler = handler;
        this.deflt = new SimpleHandler();
        this.plugin = plugin;

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
            Trace.trace(CLASS, this, "constructor", e);
            // ignore
        }
        try {
            factory.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
        } catch (SAXNotRecognizedException e) {
            Trace.trace(CLASS, this, "constructor", e);
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
            Trace.trace(CLASS, this, "constructor", e);
            // ignore
        }
        try {
            reader.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
        } catch (SAXNotRecognizedException e) {
            Trace.trace(CLASS, this, "constructor", e);
            // ignore
        }

    }

    /**
     * Parse input source.
     * @param   in              Parse data from this file source.
     * @param   validateOnly    validate with {@link #deflt} or parse with {@link #handler}.
     * @param   original        Original URL for the file. If this is <code>null</code> same as
     *                          file name.
     *
     * @throws  SourceFileExceptionList    Loading failed.
     */
    private void parse(final File in, final boolean validateOnly, final String original)
            throws SourceFileExceptionList {
        final String method = "parse(URL, boolean, InputStream)";
        InputStream stream = null;
        exceptionList = new DefaultSourceFileExceptionList();
        try {
            stream = new FileInputStream(in);
            final InputSource input = new InputSource(stream);
            reader.setErrorHandler(new SaxErrorHandler(plugin, original, exceptionList));
            handler.setUrl(original);
            deflt.setUrl(original);
            if (validateOnly) {
                reader.setContentHandler(deflt);
                reader.parse(input);
            } else {
                handler.setExceptionList(exceptionList);
                reader.setContentHandler(handler);
                reader.parse(input);
            }
        } catch (SAXException e) {
            final SourceFileException ex = new SourceFileException(plugin, e);
            if (exceptionList.size() <= 0) {
                exceptionList.add(ex);
            }
            throw exceptionList;
        } catch (IOException e) {
            exceptionList.add(plugin, e);
            throw exceptionList;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    Trace.trace(CLASS, this, method, e);
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
    public final void parse(final String fileName, final String original)
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
    public final void parse(final File file, final String original) throws SourceFileExceptionList {
        parse(file, true, original);
        parse(file, false, original);
    }

    /**
     * Get errors that occurred during last parsing.
     *
     * @return  List with collected Exceptions.
     */
    public DefaultSourceFileExceptionList getExceptionList() {
        return exceptionList;
    }

    /**
     * Get encoding of XML document. This value is set during parsing the document.
     *
     * @return  Encoding. Maybe <code>null</code>.
     */
    public String getEncoding() {
        return deflt.getEncoding();
    }

}
