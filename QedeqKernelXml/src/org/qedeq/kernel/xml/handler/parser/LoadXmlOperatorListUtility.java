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
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Load operator list from an XML file.
 *
 * @author  Michael Meyling
 */
public final class LoadXmlOperatorListUtility implements Plugin {

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
     * @param   services        Kernel services.    TODO m31 20100830: is this really necessary?
     * @param   from            Read this XML file.
     * @return  Operator list.
     * @throws  SourceFileExceptionList    Loading failed.
     */
    public static List getOperatorList(final InternalKernelServices services, final File from)
            throws SourceFileExceptionList {
        final String method = "List getOperatorList(String)";
        final LoadXmlOperatorListUtility util = new LoadXmlOperatorListUtility();
        try {
            Trace.begin(CLASS, method);
            Trace.param(CLASS, method, "from", from);
            SaxDefaultHandler handler = new SaxDefaultHandler(util);
            ParserHandler simple = new ParserHandler(handler);
            handler.setBasisDocumentHandler(simple);
            SaxParser parser = new SaxParser(util, handler);
            parser.parse(from, null);
            return simple.getOperators();
        } catch (RuntimeException e) {
            Trace.trace(CLASS, method, e);
            throw services.createSourceFileExceptionList(e);
        } catch (ParserConfigurationException e) {
            Trace.trace(CLASS, method, e);
            throw services.createSourceFileExceptionList(e);
        } catch (final SAXParseException e) {
            Trace.trace(CLASS, method, e);
            throw services.createSourceFileExceptionList(e);
        } catch (SAXException e) {
            throw services.createSourceFileExceptionList(e);
        } catch (javax.xml.parsers.FactoryConfigurationError e) {
            Trace.trace(CLASS, method, e);
            final String msg = "SAX Parser not in classpath, "
                + "add for example \"xercesImpl.jar\" and \"xml-apis.jar\".";
            throw services.createSourceFileExceptionList(new RuntimeException(msg, e));
        } finally {
            Trace.end(CLASS, method);
        }
    }

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginName() {
        return "Operator Loader";
    }

    public String getPluginDescription() {
        return "loads XML descriptoin of mathematical operators";
    }

}
