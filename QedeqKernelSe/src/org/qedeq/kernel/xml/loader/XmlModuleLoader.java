/* $Id: DefaultModuleFactory.java,v 1.7 2008/01/26 12:39:08 m31 Exp $
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

package org.qedeq.kernel.xml.loader;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.control.DefaultQedeqBo;
import org.qedeq.kernel.bo.control.ModuleFileNotFoundException;
import org.qedeq.kernel.bo.control.ModuleLoader;
import org.qedeq.kernel.bo.load.QedeqVoBuilder;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.mapper.ModuleDataException2SourceFileException;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;


/**
 * This class provides access methods for loading QEDEQ modules.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public class XmlModuleLoader implements ModuleLoader {

    /** This class. */
    private static final Class CLASS = XmlModuleLoader.class;

    /**
     * Constructor.
     */
    public XmlModuleLoader() {
    }

    /**
     * Load a local QEDEQ module.
     *
     * @param   prop        Module properties.
     * @param   localFile   Load XML file from tbis location.
     * @throws  ModuleFileNotFoundException    Local file was not found.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     */
    public void loadLocalModule(final DefaultQedeqBo prop, final File localFile)
            throws ModuleFileNotFoundException, SourceFileExceptionList {
        final String method = "loadLocalModule";
        final File file;
        try {
            file = localFile.getCanonicalFile();
        } catch (IOException e) {
            Trace.trace(CLASS, this, method, e);
            throw new ModuleFileNotFoundException("file path not correct: " + localFile);
        }
        if (!file.canRead()) {
            Trace.trace(CLASS, this, method, "file not readable=" + file);
            throw new ModuleFileNotFoundException("file not found: " + file);
        }
        if (prop.getLoadingState() == LoadingState.STATE_UNDEFINED) {
            prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_BUFFER);
            ModuleEventLog.getInstance().addModule(prop);
        } else {
            prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_BUFFER);
            ModuleEventLog.getInstance().stateChanged(prop);
        }
        SaxDefaultHandler handler = new SaxDefaultHandler();
        QedeqHandler simple = new QedeqHandler(handler);
        handler.setBasisDocumentHandler(simple);
        Qedeq qedeq = null;
        SaxParser parser = null;
        try {
            parser = new SaxParser(handler);
        } catch (SAXException e) {
            Trace.trace(CLASS, this, method, e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED,
                new DefaultSourceFileExceptionList(e));
            ModuleEventLog.getInstance().stateChanged(prop);
            throw createXmlFileExceptionList(e);
        } catch (ParserConfigurationException e) {
            Trace.trace(CLASS, this, method, e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED,
                new DefaultSourceFileExceptionList(new RuntimeException(
                    "XML parser configuration error", e)));
            ModuleEventLog.getInstance().stateChanged(prop);
            throw createXmlFileExceptionList(e);
        }
        try {
            parser.parse(file, prop.getUrl());
        } catch (SourceFileExceptionList e) {
            Trace.trace(CLASS, this, method, e);
            prop.setEncoding(parser.getEncoding());
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, e);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw e;
        }
        prop.setEncoding(parser.getEncoding());
        qedeq = simple.getQedeq();
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_INTO_MEMORY);
        ModuleEventLog.getInstance().stateChanged(prop);
        try {
            QedeqVoBuilder.createQedeq(prop, qedeq);
        } catch (ModuleDataException e) {
            Trace.trace(CLASS, this, method, e);
            final SourceFileExceptionList xl
                = ModuleDataException2SourceFileException.createSourceFileExceptionList(e, qedeq);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, xl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw xl;
        }
        ModuleEventLog.getInstance().stateChanged(prop);
    }

    private SourceFileExceptionList createXmlFileExceptionList(
            final ParserConfigurationException e) {
        return new DefaultSourceFileExceptionList(e);
    }

    private DefaultSourceFileExceptionList createXmlFileExceptionList(final SAXException e) {
        return new DefaultSourceFileExceptionList(e);
    }
}
