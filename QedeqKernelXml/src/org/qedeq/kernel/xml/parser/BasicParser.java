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

package org.qedeq.kernel.xml.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.handler.common.SaxDefaultHandler;
import org.qedeq.kernel.xml.handler.list.BasicHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Parse {@link Element}s.
 *
 * @author  Michael Meyling
 *
 */
public final class BasicParser {

    /** This class. */
    private static final Class CLASS = BasicParser.class;

    /** Here we handle the events. */
    private SaxDefaultHandler handler;

    /** XML reader. */
    private XMLReader reader;

    /** Here we describe this "plugin". */
    private static ModuleService plugin = new ModuleService() {
        public String getServiceDescription() {
            return "parses element lists and atoms";
        }
        public String getServiceId() {
            return BasicParser.class.getName();
        }
        public String getServiceAction() {
            return "element parser";
        }
    };


    /**
     * Constructor.
     *
     * @param   handler Default handler for this application.
     * @throws  ParserConfigurationException    Severe parser configuration problem.
     * @throws  SAXException    Parse problems.
     */
    private BasicParser(final SaxDefaultHandler handler)
            throws ParserConfigurationException, SAXException {
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
     * @param   in              Parse data from this source.
     * @throws  SAXException    Syntactical or semantical problem occurred.
     * @throws  IOException     Technical problem occurred.
     */
    private void parse(final String url, final Reader in)
            throws IOException, SAXException {
        final String method = "parse(URL, boolean, InputStream)";
        BufferedReader dis = null;
        SourceFileExceptionList exceptionList = new SourceFileExceptionList();;
        try {
            dis = new BufferedReader(in);
            final InputSource input = new InputSource(dis);
            reader.setErrorHandler(new SaxErrorHandler(plugin,
                url, exceptionList));
            handler.setExceptionList(exceptionList);
            reader.setContentHandler(handler);
            handler.setUrl(url);
            reader.parse(input);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (Exception e) {
                    Trace.trace(CLASS, this, method, e);
                }
            }
        }
    }

    /**
     * Create elements out of XML string.
     *
     * @param   xml     XML document part. XML header not necessary.
     * @return  Created elements.
     * @throws  ParserConfigurationException    Problem configuring parser.
     * @throws  SAXException                    Parsing problem.
     */
    public static final Element[] createElements(final String xml)
            throws ParserConfigurationException, SAXException {
        try {
            String data = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                + "<basic>\n"
                + xml + "\n"
                + "</basic>\n";
            SaxDefaultHandler handler = new SaxDefaultHandler(plugin);
            BasicHandler simple = new BasicHandler(handler);
            handler.setBasisDocumentHandler(simple);
            BasicParser parser = new BasicParser(handler);
            parser.parse("memory", IoUtility.stringToReader(data));
            return (Element[]) simple.getElements().toArray(new Element[]{});
        } catch (SAXException e) {
            Trace.trace(BasicParser.class, "createElement", e);
            Trace.trace(BasicParser.class, "createElement", e.getCause());
            throw e;
        } catch (IOException e) {
            Trace.trace(BasicParser.class, "createElement", e);
            // should not happen, hej we are parsing a String!
            throw new RuntimeException(e);
        }
    }

    /**
     * Create element out of XML string.
     *
     * @param   xml     XML document part. XML header not necessary.
     * @return  Created element.
     * @throws  ParserConfigurationException    Problem configuring parser.
     * @throws  SAXException                    Parsing problem.
     */
    public static final Element createElement(final String xml)
            throws ParserConfigurationException, SAXException {
        final Element[] elements = createElements(xml);
        if (elements == null || elements.length == 0) {
            return null;
        }
        return elements[0];
    }

}
