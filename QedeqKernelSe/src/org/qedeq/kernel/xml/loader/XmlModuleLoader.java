/* $Id: XmlModuleLoader.java,v 1.2 2008/05/15 21:27:48 m31 Exp $
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

package org.qedeq.kernel.xml.loader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.control.InternalKernelServices;
import org.qedeq.kernel.bo.control.KernelQedeqBo;
import org.qedeq.kernel.bo.control.ModuleLoader;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;
import org.xml.sax.SAXException;

import com.sun.syndication.io.XmlReader;


/**
 * This class provides access methods for loading QEDEQ modules.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public class XmlModuleLoader implements ModuleLoader {

    /** This class. */
    private static final Class CLASS = XmlModuleLoader.class;

    /** Internal kernel services. */
    private InternalKernelServices services;

    /**
     * Constructor.
     */
    public XmlModuleLoader() {
    }

    public void setServices(final InternalKernelServices services) {
        this.services = services;
    }

    public InternalKernelServices getServices() {
        return this.services;
    }

    /**
     * Load a local QEDEQ module.
     *
     * @param   prop        Module properties.
     * @param   file        Load XML file from this location.
     * @return  QEDEQ module.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     */
    public Qedeq loadLocalModule(final KernelQedeqBo prop, final File file)
            throws SourceFileExceptionList {
        final String method = "loadLocalModule";
        SaxDefaultHandler handler = new SaxDefaultHandler();
        QedeqHandler simple = new QedeqHandler(handler);
        handler.setBasisDocumentHandler(simple);
        SaxParser parser = null;
        try {
            parser = new SaxParser(handler);
        } catch (SAXException e) {
            Trace.trace(CLASS, this, method, e);
            final DefaultSourceFileExceptionList sfl = new DefaultSourceFileExceptionList(
                // TODO mime 20080404: search for better solution
                new RuntimeException(e));
            throw sfl;
        } catch (ParserConfigurationException e) {
            Trace.trace(CLASS, this, method, e);
            final DefaultSourceFileExceptionList sfl = new DefaultSourceFileExceptionList(
                // TODO mime 20080404: search for better solution
                new RuntimeException("XML parser configuration error", e));
            throw sfl;
        }
        try {
            parser.parse(file, prop.getUrl());
        } catch (SourceFileExceptionList e) {
            Trace.trace(CLASS, this, method, e);
            throw e;
        }
        return simple.getQedeq();
    }

    /**
     * Get area in XML source file for QEDEQ module context.
     *
     * @param   qedeq       Look at this QEDEQ module.
     * @param   context     Search for this context.
     * @return  Created file area. Maybe <code>null</code>.
     */
    public SourceArea createSourceArea(final Qedeq qedeq, final ModuleContext context) {
        // copy constructor
        final String method = "createSourceArea(Qedeq, ModuleContext)";
        if (qedeq == null || context == null) {
            return null;
        }
        ModuleContext ctext = new ModuleContext(context);
        final String xpath;
        try {
            xpath = Context2SimpleXPath.getXPath(ctext, qedeq).toString();
        } catch (ModuleDataException e) {
            Trace.trace(CLASS, method, e);
            return null;
        };

        SimpleXPath find = null;
        try {
            find = XPathLocationParser.getXPathLocation(
                services.getLocalFilePath(ctext.getModuleLocation()),
                xpath,
                ctext.getModuleLocation().getURL());
            if (find.getStartLocation() == null) {
                return null;
            }
            return new SourceArea(ctext.getModuleLocation().getURL(), find.getStartLocation(),
                find.getEndLocation());
        } catch (ParserConfigurationException e) {
            Trace.trace(CLASS, method, e);
        } catch (SAXException e) {
            Trace.trace(CLASS, method, e);
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
        }
        return null;
    }

    /**
     * Return reader for QEDEQ module source.
     *
     * @param   bo  Get source for this one.
     * @return  Source.
     * @throws  IOException     Reading failed.
     */
    public Reader getModuleReader(final KernelQedeqBo bo)
            throws IOException {
        return new XmlReader(services.getLocalFilePath(bo.getModuleAddress()));
    }

}
