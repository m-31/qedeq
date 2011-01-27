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

package org.qedeq.kernel.xml.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.qedeq.base.io.IoUtility;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class XmlNormalizer {

    private XmlNormalizerHandler normalize;
    
    public static void main (String args[]) throws Exception {
        normalize(new File(args[0]), new File(args[1]));
    }

    public XmlNormalizer () throws IOException, SAXException {
        ExceptionList parseErrorCollector = new ExceptionList();
        ErrorHandler errorHandler = new SaxErrorHandler(parseErrorCollector);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser;
        try {
            factory.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature("http://apache.org/xml/features/validation/schema", false);
            factory.setValidating(false);
            parser = factory.newSAXParser();
        } catch (SAXNotRecognizedException e) {
            throw new RuntimeException(e);
        } catch (SAXNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(errorHandler);

        normalize = new XmlNormalizerHandler();
        reader.setContentHandler(normalize);

    }


    public final static void normalize(final File from, final File to) throws IOException, SAXException {
        XmlNormalizer g = new XmlNormalizer();
        try {
            InputStream is = null;
            OutputStream os = null;
            try {
                System.out.println("\tnormalizing: " + from.getName());
                System.out.println("\tinto:        " + to.getName());
                is = new FileInputStream(from);
                os = new FileOutputStream(to);
                g.normalize.parse(is, os);
            } finally {
                IoUtility.close(is);
                IoUtility.close(os);
            }
        } catch (SAXParseException e) {
            System.out.println(from.getName() + " Zeile: " + e.getLineNumber() + " Spalte: " + e.getColumnNumber()
                + "  " + e.getMessage());
            throw e;
        }
    }
    

}
