/* $Id: XPathLocationParser.java,v 1.22 2008/01/26 12:39:11 m31 Exp $
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
package org.qedeq.kernel.xml.tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.qedeq.kernel.common.SourcePosition;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.Enumerator;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.TextInput;
import org.qedeq.kernel.xml.parser.SimpleHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.syndication.io.XmlReader;

/**
 * Parser for XML files. Search simple XPath within an XML file.
 * Usage:
 * <pre>
 *      final XPathLocationParser parser = new XPathLocationParser(xpath);
 *      parser.parse(xmlFile, original);
 *      return parser.getFind();
 *
 * </pre>
 *
 * @version $Revision: 1.22 $
 * @author Michael Meyling
 */
public class XPathLocationParser extends SimpleHandler {

    /** This class. */
    private static final Class CLASS = XPathLocationParser.class;

    /** Namespaces feature id (http://xml.org/sax/features/namespaces). */
    private static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

    /** Validation feature id (http://xml.org/sax/features/validation). */
    private static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

    /** SAX parser. */
    private XMLReader reader;

    /** Search for this simple XPath expression. */
    private final SimpleXPath find;

    /** We are currently at this position. */
    private SimpleXPath current;

    /** We are currently at this position if we count only occurrences and take every element. The
     * elements are all named "*". */
    private SimpleXPath summary;

    /** This object is parsed. */
    private File xmlFile;

    /** Element stack. */
    private final List elements;

    /** Current stack level. */
    private int level;

    /** Original file location. */
    private URL original;

    /**
     * Search simple XPath within an XML file.
     *
     * @param   xmlFile Search this file.
     * @param   xpath   Search for this simple XPath.
     * @param   original    Original file location.
     * @return  Source position information.
     * @throws  ParserConfigurationException
     * @throws  SAXException
     * @throws  IOException
     */
    public static final SimpleXPath getXPathLocation(final File xmlFile, final String xpath,
            final URL original)
            throws ParserConfigurationException, SAXException, IOException {
        final XPathLocationParser parser = new XPathLocationParser(xpath);
        parser.parse(xmlFile, original);
        return parser.getFind();
    }

    /**
     * Search simple XPath within an XML file.
     *
     * @param   xmlFile Search this file.
     * @param   xpath   Search for this simple XPath.
     * @param   original    Original file location.
     * @return  Source position information.
     * @throws  ParserConfigurationException
     * @throws  SAXException
     * @throws  IOException
     */
    public static final SimpleXPath getXPathLocation(final File xmlFile, final SimpleXPath xpath,
            final URL original)
            throws ParserConfigurationException, SAXException, IOException {
        return getXPathLocation(xmlFile, xpath.toString(), original);
    }

    /**
     * Constructor.
     *
     * @param   xpath XML file path.
     * @throws  ParserConfigurationException Severe parser configuration problem.
     * @throws  SAXException
     */
    public XPathLocationParser(final String xpath) throws ParserConfigurationException,
            SAXException {
        super();

        find = new SimpleXPath(xpath);
        elements = new ArrayList(20);
        level = 0;

        final String factoryImpl = System.getProperty("javax.xml.parsers.SAXParserFactory");
        if (factoryImpl == null) {
            System.setProperty("javax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);

        factory.setFeature(NAMESPACES_FEATURE_ID, false);
        factory.setFeature(VALIDATION_FEATURE_ID, false);

        final SAXParser parser = factory.newSAXParser();

        reader = parser.getXMLReader();

        // set parser features
        reader.setFeature(NAMESPACES_FEATURE_ID, false);
        reader.setFeature(VALIDATION_FEATURE_ID, false);
    }

    /**
     * Parses XML file.
     *
     * @param   fileName    Parse this input.
     * @param   original    Original file location.
     * @throws SAXException Syntactical or semantical problem occurred.
     * @throws IOException  Technical problem occurred.
     */
    public final void parse(final String fileName, final URL original) throws SAXException,
            IOException {
        final File file = new File(fileName);
        parse(file, original);
    }

    /**
     * Parses XML file.
     *
     * @param   file        Parse this input.
     * @param   original    Original file location.
     * @throws SAXException Syntactical or semantical problem occurred.
     * @throws IOException Technical problem occurred.
     */
    public final void parse(final File file, final URL original) throws SAXException, IOException {
        this.xmlFile = file;
        this.original = original;
        elements.clear();
        level = 0;
        try {
            current = new SimpleXPath();
            summary = new SimpleXPath();
            reader.setContentHandler(this);
            InputStream stream = new FileInputStream(file);
            reader.parse(new InputSource(stream));
            xmlFile = null;
        } catch (SAXException e) {
            Trace.trace(CLASS, this, "parse", e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        elements.clear();
        level = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        elements.clear();
        level = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(final char[] ch, final int start, final int length)
            throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(final String prefix) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(final String name) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(final String target, final String data) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String,
     *      java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(final String namespaceURI, final String localName, final String qName,
            final Attributes atts) throws SAXException {
        final String method = "startElement(String, String, Attributes)";
        level++;
        summary.addElement("*", addOccurence("*"));
        current.addElement(qName, addOccurence(qName));

        // LATER mime 20070109: just for testing is the next if
/*
        if (find.matchesElementsBegining(current, summary)) {
            System.out.println("part match " + qName);
            xml.setRow(locator.getLineNumber());
            xml.setColumn(locator.getColumnNumber());
            try {
                xml.skipBackToBeginOfXmlTag();
            } catch (RuntimeException e) {
                Trace.trace(this, method, e);
            }
            find.setStartLocation(new SourcePosition(xml.getLocalAddress(), xml.getRow(), xml
                .getColumn()));
        }
*/
        if (getLocator() == null) {
            throw new SAXException("Locator unexpectly null");
        }
        if (find.matchesElements(current, summary)) {
            Trace.trace(CLASS, this, method, "matching elements");
            Trace.param(CLASS, this, method, qName, current);
            TextInput xml = null;
            try {
// LATER mime 20080608: old code
//                xml = new TextInput(xmlFile, IoUtility.getWorkingEncoding(getEncoding()));
                xml = new TextInput(xmlFile, IoUtility.getWorkingEncoding(getEncoding()));
            } catch (IOException io) {
                Trace.fatal(CLASS, this, method, "File \"" + xmlFile + "\" should be readable", io);
                if (getLocator() == null) {
                    throw new SAXException("Locator unexpectedly null");
                }
                // at least we can set the current location as find location
                find.setStartLocation(new SourcePosition(xml.getAddress(),
                    getLocator().getLineNumber(), getLocator().getColumnNumber()));
                return;
            }
            xml.setRow(getLocator().getLineNumber());
            xml.setColumn(getLocator().getColumnNumber());

            try {
                xml.skipBackToBeginOfXmlTag();
            } catch (RuntimeException e) {
                Trace.trace(CLASS, this, method, e);
            }
            find.setStartLocation(new SourcePosition(xml.getAddress(), xml.getRow(), xml
                .getColumn()));
            if (find.getAttribute() != null) {
                xml.read(); // skip <
                xml.readNextXmlName(); // must be element name
                String tag;
                do {
                    xml.skipWhiteSpace();
                    int row = xml.getRow();
                    int col = xml.getColumn();
                    try {
                        tag = xml.readNextXmlName();
                    } catch (IllegalArgumentException e) {
                        break; // LATER mime 20050621: create named exception in readNextXmlName
                    }
                    if (tag.equals(find.getAttribute())) {
                        find.setStartLocation(new SourcePosition(xml.getAddress(), row, col));
                        xml.readNextAttributeValue();
                        find.setEndLocation(new SourcePosition(xml.getAddress(), xml.getRow(),
                            xml.getColumn()));
                        break;
                    }
                    xml.readNextAttributeValue();
                } while (true);
            }
        }
    }

    /**
     * Add element occurrence.
     *
     * @param name Element that occurred.
     * @return Number of occurrences including this one.
     */
    private int addOccurence(final String name) {
        while (level < elements.size()) {
            elements.remove(elements.size() - 1);
        }
        while (level > elements.size()) {
            elements.add(new HashMap());
        }
        final Map levelMap = (Map) elements.get(level - 1);
        final Enumerator counter;
        if (levelMap.containsKey(name)) {
            counter = (Enumerator) levelMap.get(name);
            counter.increaseNumber();
        } else {
            counter = new Enumerator(1);
            levelMap.put(name, counter);
        }
        return counter.getNumber();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void endElement(final String namespaceURI, final String localName, final String qName)
            throws SAXException {
        final String method = "endElement(String, String, Attributes)";
        level--;
        if (getLocator() == null) {
            current.deleteLastElement();
            summary.deleteLastElement();
            throw new SAXException("Locator unexpectly null");
        }
        if (find.matchesElements(current, summary) && find.getAttribute() == null) {
            TextInput xml = null;
            try {
                xml = new TextInput(new XmlReader(xmlFile));
// LATER mime 20080608: old code
//                xml = new TextInput(xmlFile, IoUtility.getWorkingEncoding(getEncoding()));
            } catch (IOException io) {
                Trace.fatal(CLASS, this, method, "File \"" + xmlFile + "\" should be readable", io);
                if (getLocator() == null) {
                    throw new SAXException("Locator unexpectedly null");
                }
                // at least we can set the current location as find location
                find.setStartLocation(new SourcePosition(xml.getAddress(),
                    getLocator().getLineNumber(), getLocator().getColumnNumber()));
                return;
            }
            xml.setRow(getLocator().getLineNumber());
            xml.setColumn(getLocator().getColumnNumber());
            // xml.skipForwardToEndOfXmlTag(); // LATER mime 20050810: remove? comment in?
            find.setEndLocation(new SourcePosition(original, xml.getRow(), xml
                .getColumn()));
        }
        current.deleteLastElement();
        summary.deleteLastElement();
    }

    /**
     * Get searched XPath. Hopefully the start and end location are set.
     *
     * @return Searched XPath.
     */
    public SimpleXPath getFind() {
        return find;
    }

}
