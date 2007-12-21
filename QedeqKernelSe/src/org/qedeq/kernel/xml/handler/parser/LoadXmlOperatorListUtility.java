/* $Id: LoadXmlOperatorListUtility.java,v 1.7 2007/12/21 23:33:47 m31 Exp $
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

package org.qedeq.kernel.xml.handler.parser;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Load operator list from an XML file.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public final class LoadXmlOperatorListUtility  {

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
    public static List getOperatorList(final URL from) throws SourceFileExceptionList {
        final String method = "List getOperatorList(String)";
        try {
            Trace.begin(LoadXmlOperatorListUtility.class, method);
            Trace.param(LoadXmlOperatorListUtility.class, method, "from", from);
            SaxDefaultHandler handler = new SaxDefaultHandler();
            ParserHandler simple = new ParserHandler(handler);
            handler.setBasisDocumentHandler(simple);
            SaxParser parser = new SaxParser(handler);
            parser.parse(from, null);
            return simple.getOperators();
        } catch (RuntimeException e) {
            Trace.trace(LoadXmlOperatorListUtility.class, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (ParserConfigurationException e) {
            Trace.trace(LoadXmlOperatorListUtility.class, method, e);
            final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList();
            list.add(e);
            throw list;
        } catch (final SAXParseException e) {
            Trace.trace(LoadXmlOperatorListUtility.class, method, e);
            final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList();
            list.add(e);
            throw list;
        } catch (SAXException e) {
            Trace.trace(LoadXmlOperatorListUtility.class, method, e);
            final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList();
            list.add(e);
            throw list;
        } catch (javax.xml.parsers.FactoryConfigurationError e) {
            Trace.trace(LoadXmlOperatorListUtility.class, method, e);
            final String msg = "SAX Parser not in classpath, "
                + "add for example \"xercesImpl.jar\" and \"xml-apis.jar\".";
            final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList();
            list.add(new IOException(msg));
            throw list;
        } finally {
            Trace.end(LoadXmlOperatorListUtility.class, method);
        }
    }

}
