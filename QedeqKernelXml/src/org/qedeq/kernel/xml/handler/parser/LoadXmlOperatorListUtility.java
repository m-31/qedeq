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

package org.qedeq.kernel.xml.handler.parser;

import java.io.File;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Load operator list from an XML file.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class LoadXmlOperatorListUtility  {

    /** This class. */
    private static final Class CLASS = LoadXmlOperatorListUtility.class;

    /**
     * Constructor.
     */
    private LoadXmlOperatorListUtility() {
        // nothing to do
    }

    /**
     * Get operator list out of XML file.
     *
     * @param   from            Read this XML file.
     * @return  Operator list.
     * @throws  SourceFileExceptionList    Loading failed.
     */
    public static List getOperatorList(final File from) throws SourceFileExceptionList {
        final String method = "List getOperatorList(String)";
        try {
            Trace.begin(CLASS, method);
            Trace.param(CLASS, method, "from", from);
            SaxDefaultHandler handler = new SaxDefaultHandler();
            ParserHandler simple = new ParserHandler(handler);
            handler.setBasisDocumentHandler(simple);
            SaxParser parser = new SaxParser(handler);
            parser.parse(from, null);
            return simple.getOperators();
        } catch (RuntimeException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (ParserConfigurationException e) {
            Trace.trace(CLASS, method, e);
            final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(
                new RuntimeException(e));   // TODO mime 20080404: search for better solution
            throw list;
        } catch (final SAXParseException e) {
            Trace.trace(CLASS, method, e);
            final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(
                new RuntimeException(e));   // TODO mime 20080404: search for better solution
            throw list;
        } catch (SAXException e) {
            Trace.trace(CLASS, method, e);
            final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(
                new RuntimeException(e));   // TODO mime 20080404: search for better solution
            throw list;
        } catch (javax.xml.parsers.FactoryConfigurationError e) {
            Trace.trace(CLASS, method, e);
            final String msg = "SAX Parser not in classpath, "
                + "add for example \"xercesImpl.jar\" and \"xml-apis.jar\".";
            final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(
                new RuntimeException(msg));   // TODO mime 20080404: search for better solution
            throw list;
        } finally {
            Trace.end(CLASS, method);
        }
    }

}
