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
package org.qedeq.kernel.xml.tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.SourceArea;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.Enumerator;
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
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public final class XPathLocationParser extends SimpleHandler {

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

    /** Add this to found position. */
    private SourcePosition startDelta;

    /** Add this to found position. */
    private SourcePosition endDelta;

    /** Here the found element starts. */
    private SourcePosition start;

    /** Here the found element ends. */
    private SourcePosition end;

    /**
     * Search simple XPath within an XML file.
     *
     * @param   xmlFile Search this file.
     * @param   xpath   Search for this simple XPath.
     * @return  Source position information.
     * @throws  ParserConfigurationException    Parser configuration problem.
     * @throws  SAXException                    XML problem.
     * @throws  IOException                     IO problem.
     */
//    public static final SimpleXPath getXPathLocation(final File xmlFile, final String xpath)
//            throws ParserConfigurationException, SAXException, IOException {
//        return getXPathLocation(xmlFile, new SimpleXPath(xpath));
//    }

    /**
     * Search simple XPath within an XML file.
     *
     * @param   address     Name description (for example URL) for this XML file.
     * @param   xpath       Search for this simple XPath.
     * @param   startDelta  Skip position (relative to location start). Could be
     *                      <code>null</code>.
     * @param   endDelta    Mark until this column (relative to location start). Could
     *                      be <code>null</code>.
     * @param   file        Search this file.
     * @return  Source position information.
     */
    public static SourceArea findSourceArea(final String address, final SimpleXPath xpath,
            final SourcePosition startDelta, final SourcePosition endDelta,  final File file) {
        final String method = "findSourceArea(String, SimpleXPath, File)";
        final String message = "Could not find \"" + xpath + "\" within \"" + file + "\"";
        try {
            XPathLocationParser parser = new XPathLocationParser(xpath, startDelta, endDelta);
            parser.parse(file);
            if (parser.getStart() == null || parser.getEnd() == null) {
                Trace.fatal(CLASS, method, message, null);
                return new SourceArea(address);
            }
            return new SourceArea(address, parser.getStart(), parser.getEnd());
        } catch (ParserConfigurationException e) {
            Trace.fatal(CLASS, method, message, e);
        } catch (SAXException e) {
            Trace.fatal(CLASS, method, message, e);
        } catch (IOException e) {
            Trace.fatal(CLASS, method, message, e);
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, method, message, e);
        }
        return null;
    }

    /**
     * Search simple XPath within an XML file.
     *
     * @param   file        Search this file.
     * @param   xpath       Search for this simple XPath.
     * @return  Source position information.
     */
    public static SourceArea findSourceArea(final File file, final SimpleXPath xpath) {
        return findSourceArea(file.toString(), xpath, null, null, file);
    }

    /**
     * Constructor.
     *
     * @param   xpath                   XML file path.
     * @param   startDelta              Skip position (relative to location start). Could be
     *                                  <code>null</code>.
     * @param   endDelta                Mark until this column (relative to location start). Could
     *                                  be <code>null</code>.
     * @throws  ParserConfigurationException    Severe parser configuration problem.
     * @throws  SAXException                    XML problem.
     */
    public XPathLocationParser(final SimpleXPath xpath, final SourcePosition startDelta,
        final SourcePosition endDelta) throws ParserConfigurationException,
            SAXException {
        super();

        this.find = xpath;
        this.startDelta = startDelta;
        this.endDelta = endDelta;
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
     * @param   file        Parse this input.
     * @throws SAXException Syntactical or semantical problem occurred.
     * @throws IOException Technical problem occurred.
     */
    public final void parse(final File file) throws SAXException,
            IOException {
        this.xmlFile = file;
        elements.clear();
        level = 0;
        InputStream stream = null;
        try {
            current = new SimpleXPath();
            summary = new SimpleXPath();
            reader.setContentHandler(this);
            stream = new FileInputStream(file);
            reader.parse(new InputSource(stream));
        } catch (SAXException e) {
            Trace.trace(CLASS, this, "parse", e);
            throw e;
        } finally {
            IoUtility.close(stream);
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
            throw new SAXException("Locator unexpectedly null");
        }
        if (find.matchesElements(current, summary)) {
            Trace.trace(CLASS, this, method, "matching elements");
            Trace.param(CLASS, this, method, qName, current);
            TextInput xml = null;
            Reader xmlReader = null;
            try {
                xmlReader = new XmlReader(xmlFile);
                xml = new TextInput(xmlReader);
// LATER mime 20080608: old code
//                xml = new TextInput(xmlFile, IoUtility.getWorkingEncoding(getEncoding()));
            } catch (IOException io) {
                Trace.fatal(CLASS, this, method, "File \"" + xmlFile + "\" should be readable", io);
                if (getLocator() == null) {
                    throw new SAXException("Locator unexpectedly null");
                }
                // at least we can set the current location as find location
                start = new SourcePosition(
                    getLocator().getLineNumber(), getLocator().getColumnNumber());
                return;
            }
            try {
                xml.setRow(getLocator().getLineNumber());
                xml.setColumn(getLocator().getColumnNumber());
                if (startDelta != null) {
                    xml.skipWhiteSpace();
                    final String cdata = "<![CDATA[";
                    final String read = xml.readString(cdata.length());
                    final int cdataLength = (cdata.equals(read) ? cdata.length() : 0);
                    start = addDelta(xml, cdataLength, startDelta);
                    end = addDelta(xml, cdataLength, endDelta);
                    return;
                }
                try {
                    xml.skipBackToBeginOfXmlTag();
                } catch (RuntimeException e) {
                    Trace.trace(CLASS, this, method, e);
                }
                start = new SourcePosition(xml.getRow(), xml.getColumn());
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
                            start = new SourcePosition(row, col);
                            xml.readNextAttributeValue();
                            this.end = new SourcePosition(xml.getRow(),
                            xml.getColumn());
                            break;
                        }
                        xml.readNextAttributeValue();
                    } while (true);
                }
            } finally {
                IoUtility.close(xml);   // findbugs
            }
        }
    }

    /**
     * Set text input position according to locator and add delta plus tag length.
     *
     * @param   xml         This is the stream we work on.
     * @param   cdataLength Length of extra skip data.
     * @param   delta       Add this delta
     * @return  Resulting source position.
     */
    private SourcePosition addDelta(final TextInput xml, final int cdataLength,
            final SourcePosition delta) {
        xml.setRow(getLocator().getLineNumber());
        xml.setColumn(getLocator().getColumnNumber());
        if (delta.getRow() == 1 && cdataLength > 0) {
            xml.addColumn(cdataLength + delta.getColumn() - 1);
        } else {
            xml.addPosition(delta);
        }
        return new SourcePosition(xml.getRow(), xml.getColumn());
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
        if (find.matchesElements(current, summary) && find.getAttribute() == null
                && startDelta == null) {
            TextInput xml = null;
            Reader xmlReader = null;
            try {
                xmlReader = new XmlReader(xmlFile);
                xml = new TextInput(xmlReader);
// LATER mime 20080608: old code
//                xml = new TextInput(xmlFile, IoUtility.getWorkingEncoding(getEncoding()));
            } catch (IOException io) {
                Trace.fatal(CLASS, this, method, "File \"" + xmlFile + "\" should be readable", io);
                if (getLocator() == null) {
                    throw new SAXException("Locator unexpectedly null");
                }
                // at least we can set the current location as find location
                start = new SourcePosition(getLocator().getLineNumber(),
                    getLocator().getColumnNumber());
                return;
            } finally {
                IoUtility.close(xmlReader);
            }
            try {
                xml.setRow(getLocator().getLineNumber());
                xml.setColumn(getLocator().getColumnNumber());
                // xml.skipForwardToEndOfXmlTag(); // LATER mime 20050810: remove? comment in?
                this.end = new SourcePosition(xml.getRow(), xml.getColumn());
            } finally {
                IoUtility.close(xml);   // findbugs
            }
        }
        current.deleteLastElement();
        summary.deleteLastElement();
    }

    /**
     * Get starting source position of found element. Could be <code>null</code>.
     *
     * @return  Start position.
     */
    private SourcePosition getStart() {
        return start;
    }

    /**
     * Get ending source position of found element. Could be <code>null</code>.
     *
     * @return  End position.
     */
    private SourcePosition getEnd() {
        return end;
    }

}
