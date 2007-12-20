/* $Id: TestParser.java,v 1.7 2007/10/07 16:43:11 m31 Exp $
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

package org.qedeq.kernel.bo.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.handler.list.ElementHandler;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class TestParser {
    
    private SaxDefaultHandler handler;
    private XMLReader reader;

    /**
     * Constructor.
     *
     * @param   handler   Default handler for this application.
     * @throws  ParserConfigurationException    Severe parser configuration problem.
     * @throws  SAXException
     */
    public TestParser(final SaxDefaultHandler handler) throws ParserConfigurationException,
            SAXException {
        super();

        this.handler = handler;

        final String factoryImpl = System.getProperty("javax.xml.parsers.SAXParserFactory");
        if (factoryImpl == null) {
            System.setProperty("javax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);

        final SAXParser parser = factory.newSAXParser();
        reader = parser.getXMLReader();
    }

    /**
     * Parse input source.
     *
     * @param   url             Source URL. Only for information.
     * @param   validateOnly    validate with {@link #deflt} or parse with {@link #handler}.
     * @param   in              Parse data from this source.
     * @throws  SAXException    Syntactical or semantical problem occurred.
     * @throws  IOException     Technical problem occurred.
     */
    private void parse(final URL url, final InputStream in)
            throws IOException, SAXException {
        final String method = "parse(URL, boolean, InputStream)";
        BufferedReader dis = null;
        DefaultSourceFileExceptionList exceptionList = new DefaultSourceFileExceptionList();;
        try {
            dis = new BufferedReader(new InputStreamReader(in));
            final InputSource input = new InputSource(dis);
            
            reader.setErrorHandler(new SaxErrorHandler(url, exceptionList));
            handler.setExceptionList(exceptionList);
            reader.setContentHandler(handler);
            handler.setUrl(url);
            reader.parse(input);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (Exception e) {
                    Trace.trace(this, method, e);
                }
            }
        }
    }

    static protected final Element createElement(final String xml) throws ParserConfigurationException,
            SAXException, IOException {
        try {
            String data = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + xml;
            SaxDefaultHandler handler = new SaxDefaultHandler();
            ElementHandler simple = new ElementHandler(handler);
            handler.setBasisDocumentHandler(simple);
            TestParser parser = new TestParser(handler);
            parser.parse(null, new StringBufferInputStream(data));
            return simple.getElement();
        } catch (SAXException e) {
            Trace.trace(TestParser.class, "createElement", e);
            Trace.trace(TestParser.class, "createElement", e.getCause());
            throw e;
        } catch (IOException e) {
            Trace.trace(TestParser.class, "createElement", e);
            throw e;
        }
    }
    
}