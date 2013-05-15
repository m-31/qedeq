/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.handler.common.SaxDefaultHandler;
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
            Trace.fatal(CLASS, "Programming error.", method, e);
            throw services.createSourceFileExceptionList(
                ParserErrors.PARSER_PROGRAMMING_ERROR_CODE,
                ParserErrors.PARSER_PROGRAMMING_ERROR_TEXT,
                "" + from, e);
        } catch (ParserConfigurationException e) {
            Trace.fatal(CLASS, "Parser configuration error.", method, e);
            throw services.createSourceFileExceptionList(
                ParserErrors.PARSER_CONFIGURATION_ERROR_CODE,
                ParserErrors.PARSER_CONFIGURATION_ERROR_TEXT,
                "" + from, e);
        } catch (final SAXParseException e) {
            Trace.fatal(CLASS, "Configuration error, file corrupt: " + from, method, e);
            throw services.createSourceFileExceptionList(
                ParserErrors.XML_FILE_PARSING_FAILED_CODE,
                ParserErrors.XML_FILE_PARSING_FAILED_TEXT,
                "" + from, e);
        } catch (SAXException e) {
            Trace.fatal(CLASS, "Configuration error, file corrupt: " + from, method, e);
            throw services.createSourceFileExceptionList(
                ParserErrors.XML_FILE_PARSING_FAILED_CODE,
                ParserErrors.XML_FILE_PARSING_FAILED_TEXT,
                "" + from, e);
        } catch (javax.xml.parsers.FactoryConfigurationError e) {
            Trace.trace(CLASS, method, e);
            throw services.createSourceFileExceptionList(
                ParserErrors.PARSER_FACTORY_CONFIGURATION_CODE,
                ParserErrors.PARSER_FACTORY_CONFIGURATION_TEXT,
                "" + from, new RuntimeException(
                ParserErrors.PARSER_FACTORY_CONFIGURATION_TEXT, e));
        } finally {
            Trace.end(CLASS, method);
        }
    }

    public String getServiceId() {
        return CLASS.getName();
    }

    public String getServiceAction() {
        return "Operator Loader";
    }

    public String getServiceDescription() {
        return "loads XML descriptoin of mathematical operators";
    }

}
