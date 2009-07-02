/* $Id: XmlQedeqFileDao.java,v 1.1 2008/07/26 08:00:51 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;
import org.xml.sax.SAXException;

import com.sun.syndication.io.XmlReader;


/**
 * This class provides access methods for loading QEDEQ modules from XML files.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class XmlQedeqFileDao implements QedeqFileDao {

    /** This class. */
    private static final Class CLASS = XmlQedeqFileDao.class;

    /** Internal kernel services. */
    private InternalKernelServices services;

    /**
     * Constructor.
     */
    public XmlQedeqFileDao() {
    }

    public void setServices(final InternalKernelServices services) {
        this.services = services;
    }

    public InternalKernelServices getServices() {
        return this.services;
    }

    public Qedeq loadQedeq(final QedeqBo prop, final File file)
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

    public void saveQedeq(final KernelQedeqBo prop, final File localFile)
            throws SourceFileExceptionList, IOException {
        final OutputStream outputStream = new FileOutputStream(localFile);
        final TextOutput printer = new TextOutput(localFile.getName(), outputStream);
        Qedeq2Xml.print(prop, printer);
    }

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
        }

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

    public Reader getModuleReader(final KernelQedeqBo bo)
            throws IOException {
        return new XmlReader(services.getLocalFilePath(bo.getModuleAddress()));
    }

}
