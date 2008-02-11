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

package org.qedeq.kernel.bo.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.bo.control.LoadRequiredModules;
import org.qedeq.kernel.bo.module.Kernel;
import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.ModuleFactory;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.StringUtility;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;
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
public class DefaultModuleFactory implements ModuleFactory {

    /** This class. */
    private static final Class CLASS = DefaultModuleFactory.class;

    /** For synchronized waiting. */
    private final String monitor = new String();

    /** Token for synchronization. */
    private final String syncToken = new String();

    /** Number of method calls. */
    private int processCounter = 0;

    /** Collection of modules. */
    private final Modules modules;

    /** Kernel access. */
    private final Kernel kernel;

    /**
     * Constructor.
     *
     * @param   kernel  For kernel access.
     */
    public DefaultModuleFactory(final Kernel kernel) {
        modules = new Modules();
        this.kernel = kernel;
    }

    public void startup() {
        if (kernel.getConfig().isAutoReloadLastSessionChecked()) {
            autoReloadLastSessionChecked();
        }
    }

    /**
     * If configured load all QEDEQ modules that where successfully loaded the last time.
     */
    public void autoReloadLastSessionChecked() {
        if (kernel.getConfig().isAutoReloadLastSessionChecked()) {
            final Thread thread = new Thread() {
                public void run() {
                    final String method = "start()";
                    try {
                        Trace.begin(CLASS, this, method);
                        QedeqLog.getInstance().logMessage(
                            "Trying to load previously successfully loaded modules.");
                        final int number = kernel.getConfig().getPreviouslyCheckedModules().length;
                        if (loadPreviouslySuccessfullyLoadedModules()) {
                            QedeqLog.getInstance().logMessage(
                                "Loading of " + number + " previously successfully loaded module"
                                + (number != 1 ? "s" : "") + " successfully done.");
                        } else {
                            QedeqLog.getInstance().logMessage(
                                "Loading of all previously successfully checked modules failed. "
                                + number + " module" + (number != 1 ? "s" : "") + " were tried.");
                        }
                    } catch (Exception e) {
                        Trace.trace(CLASS, this, method, e);
                    } finally {
                        Trace.begin(CLASS, this, method);
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void removeAllModules() {
        do {
            synchronized (syncToken) {
                if (processCounter == 0) {
                    getModules().removeAllModules();
                    return;
                }
            }
            synchronized (monitor) {
                try {
                    monitor.wait(10000);
                } catch (InterruptedException e) {
                }
            }
        } while (true);

    }

    /**
     * Remove a QEDEQ module from memory.
     *
     * @param   address     Remove module identified by this address.
     * @throws  IOException Module is not known to the kernel.
     */
    public void removeModule(final ModuleAddress address) throws IOException {
        final ModuleProperties prop = getModuleProperties(address);
        if (prop != null) {
            removeModule(getModuleProperties(address));
        } else  {
            throw new IOException("Module not known: " + address);
        }
    }

    /**
     * Remove a QEDEQ module from memory.
     *
     * @param   prop    Remove module identified by this property.
     */
    public void removeModule(final ModuleProperties prop) {
        do {
            synchronized (syncToken) {
                if (processCounter == 0) {
                    getModules().removeModule(prop);
                    return;
                }
            }
            synchronized (monitor) {
                try {
                    this.monitor.wait(10000);
                } catch (InterruptedException e) {
                }
            }
        } while (true);

    }

    /**
     * Clear local file buffer and all loaded QEDEQ modules.
     *
     * @throws  IOException Deletion of all buffered file was not successful.
     */
    public void clearLocalBuffer()
            throws IOException {
        removeAllModules();
        final File bufferDir = getBufferDirectory().getCanonicalFile();
        if (bufferDir.exists() && !IoUtility.deleteDir(bufferDir, false)) {
            throw new IOException("buffer could not be deleted: " + bufferDir);
        }
    }

    /**
     * Get a certain module.
     *
     * @param   address     Address of module.
     * @return  Wanted module.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     */
    public ModuleProperties loadModule(final ModuleAddress address) throws SourceFileExceptionList {
        final String method = "loadModule(URL)";
        processInc();
        try {
            final ModuleProperties prop = getModules().getModuleProperties(address);
            synchronized (prop) {
                if (prop.isLoaded()) {
                    return prop;
                }

                // search in local file buffer
                try {
                    loadLocalModule(prop);
                    return prop;
                } catch (ModuleFileNotFoundException e) {     // file not found
                    // nothing to do, we will continue by creating a local copy
                } catch (SourceFileExceptionList e) {
                    Trace.trace(CLASS, this, method, e);
                    QedeqLog.getInstance().logFailureState("Loading of module failed!",
                        address.getURL(), e.toString());
                    throw e;
                }

                // make local copy
                try {
                    makeLocalCopy(prop);
                } catch (IOException e) {
                    Trace.trace(CLASS, this, method, e);
                    QedeqLog.getInstance().logFailureState("Loading of module failed!",
                        address.getURL(), e.toString());
                    throw createXmlFileExceptionList(e);
                }
                try {
                    loadLocalModule(prop);
                } catch (ModuleFileNotFoundException e) {
                    // TODO mime 20070415: This should not occur because a local copy was
                    // at least created a few lines above
                    Trace.trace(CLASS, this, method, e);
                    QedeqLog.getInstance().logFailureState("Loading of module failed!",
                        address.getURL(), e.getMessage());
                    // TODO mime 20071125: refac codes
                    final SourceFileException sf = new SourceFileException(1021,
                        "Loading of module \"" + address.getURL() +  "\"failed",
                        e, (SourceArea) null, (SourceArea) null);
                    final DefaultSourceFileExceptionList sfl = new DefaultSourceFileExceptionList(
                        sf);
                    throw sfl;
                } catch (SourceFileExceptionList e) {
                    Trace.trace(CLASS, this, method, e);
                    QedeqLog.getInstance().logFailureState("Loading of module failed!",
                    address.getURL(), e.getMessage());
                    throw e;
                }
                return prop;
             }
         } finally {
             processDec();
         }
    }

    public ModuleProperties loadModule(final QedeqBo module,
            final Specification spec) throws SourceFileExceptionList {

        final String method = "loadModule(Module, Specification)";
        Trace.begin(CLASS, this, method);
        Trace.trace(CLASS, this, method, spec);
        processInc();
        try {
            final ModuleAddress[] modulePaths;
            try {
                modulePaths = DefaultModuleAddress.getModulePaths(module.getModuleAddress(), spec);
            } catch (IOException e) {
                Trace.trace(CLASS, this, method, e);
                throw createXmlFileExceptionList(e);
            }
            // search in already loaded modules
            for (int i = 0; i < modulePaths.length; i++) {
                final ModuleProperties prop
                    = getModules().getModuleProperties(modulePaths[i]);
                synchronized (prop) {
                    if (prop.isLoaded()) {
                        return (prop);
                    }
                }
            }

            // search in local file buffer
            Trace.trace(CLASS, this, method, "searching file buffer");
            for (int i = 0; i < modulePaths.length; i++) {
                try {
                    final ModuleProperties prop
                        = getModules().getModuleProperties(modulePaths[i]);
                    Trace.trace(CLASS, this, method, "synchronizing at prop=" + prop);
                    synchronized (prop) {
                        loadLocalModule(prop);
                        return prop;
                    }
                } catch (ModuleFileNotFoundException e) {
                    // file not found try loading URL later on
                }
            }

            // try loading url directly
            for (int i = 0; i < modulePaths.length; i++) {
                try {
                    final ModuleProperties prop
                        = getModules().getModuleProperties(modulePaths[i]);
                    synchronized (prop) {
                        makeLocalCopy(prop);
                        loadLocalModule(prop);
                        return prop;
                    }
                 } catch (IOException e) {
                     QedeqLog.getInstance().logFailureState("Loading of module failed!",
                         modulePaths[i].getURL(), e.toString());
                     Trace.trace(CLASS, this, method, e);
                     throw createXmlFileExceptionList(e);
                 } catch (ModuleFileNotFoundException e) {
                     Trace.trace(CLASS, this, method, e);
                     QedeqLog.getInstance().logFailureState("Loading of module failed!",
                         modulePaths[i].getURL(), e.getMessage());
                     throw createXmlFileExceptionList(e);
                 }
            }
            throw createXmlFileExceptionList(new ModuleFileNotFoundException(
                "no QEDEQ module found"));
        } finally {
            processDec();
            Trace.end(CLASS, this, method);
        }
    }

    public ModuleAddress[] getAllLoadedModules() {
        return getModules().getAllLoadedModules();
    }


    public void loadRequiredModules(final ModuleAddress address) throws SourceFileExceptionList {
        final ModuleProperties prop = loadModule(address);
        LoadRequiredModules.loadRequired(prop);
    }

    /**
     * Load all previously checked QEDEQ modules.
     *
     * @return  Successfully reloaded all modules.
     */
    public boolean loadPreviouslySuccessfullyLoadedModules() {
        processInc();
        try {
            final String[] list = kernel.getConfig().getPreviouslyCheckedModules();
            boolean errors = false;
            for (int i = 0; i < list.length; i++) {
                try {
                    final ModuleAddress address = getModuleAddress(list[i]);
                    loadModule(address);
                } catch (SourceFileExceptionList e) {
                    errors = true;
                } catch (IOException e) {
                    Trace.fatal(CLASS, this, "loadPreviouslySuccessfullyLoadedModules",
                        "internal error: "
                            + "saved URLs are malformed", e);
                    errors = true;
                }
            }
            return !errors;
        } finally {
            processDec();
        }
    }

    // LATER mime 20070326: dynamic loading from web page directory
    public boolean loadAllModulesFromQedeq() {
        processInc();
        try {
            final String prefix = "http://qedeq.org/"
                + kernel.getKernelVersionDirectory() + "/";
            final String[] list = new String[] {
                prefix + "doc/math/qedeq_logic_v1.xml",
                prefix + "doc/math/qedeq_set_theory_v1.xml",
                prefix + "doc/project/qedeq_basic_concept.xml",
                prefix + "doc/project/qedeq_logic_language.xml",
                prefix + "sample/qedeq_sample1.xml",
                prefix + "sample/qedeq_error_sample_00.xml",
                prefix + "sample/qedeq_error_sample_01.xml",
                prefix + "sample/qedeq_error_sample_02.xml",
                prefix + "sample/qedeq_error_sample_03.xml",
                prefix + "sample/qedeq_error_sample_04.xml",
                prefix + "sample/qedeq_sample2_error.xml",
                prefix + "sample/qedeq_sample3_error.xml",
                prefix + "sample/qedeq_sample4_error.xml",
                prefix + "sample/qedeq_sample5_error.xml",
                prefix + "sample/qedeq_sample6_error.xml",
                prefix + "sample/qedeq_sample7_error.xml",
            };
            boolean errors = false;
            for (int i = 0; i < list.length; i++) {
                try {
                    final ModuleAddress address = getModuleAddress(list[i]);
                    loadModule(address);
                } catch (SourceFileExceptionList e) {
                    errors = true;
                } catch (IOException e) {
                    Trace.fatal(CLASS, this, "loadPreviouslySuccessfullyLoadedModules",
                        "internal error: "
                            + "saved URLs are malformed", e);
                    errors = true;
                }
            }
            return !errors;
        } finally {
           processDec();
        }
    }

    /**
     * Load a local QEDEQ module.
     *
     * @param   prop   Module properties.
     * @throws  ModuleFileNotFoundException    Local file was not found.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     */
    private final void loadLocalModule(final ModuleProperties prop)
            throws ModuleFileNotFoundException, SourceFileExceptionList {
        final String method = "loadLocalModule";
        final File localFile = getLocalFilePath(prop.getModuleAddress());
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
        final QedeqBo qedeqBo;
        try {
            qedeqBo = QedeqBoFactory.createQedeq(prop.getModuleAddress(), qedeq);
        } catch (ModuleDataException e) {
            Trace.trace(CLASS, this, method, e);
            final SourceFileExceptionList xl
                = ModuleDataException2XmlFileException.createXmlFileExceptionList(e, qedeq);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, xl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw xl;
        }
        prop.setLoaded(qedeqBo);
// FIXME        prop.setEncoding(handler.getEncoding());
        ModuleEventLog.getInstance().stateChanged(prop);
    }

    /**
     * Make local copy of a module if it is no file address.
     *
     * @param   prop         Module properties.
     * @throws  IOException    if address was malformed or the file can not be found
     */
    private final synchronized void makeLocalCopy(
            final ModuleProperties prop)
            throws IOException {
        final String method = "makeLocalCopy";
        Trace.begin(CLASS, this, method);

        if (prop.getLoadingState() == LoadingState.STATE_UNDEFINED) {
            prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);
            ModuleEventLog.getInstance().addModule(prop);
        } else {
            prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);
            ModuleEventLog.getInstance().stateChanged(prop);
        }

        if (prop.getModuleAddress().isFileAddress()) {
            return;
        }
        FileOutputStream out = null;
        InputStream in = null;
        final File f = getLocalFilePath(prop.getModuleAddress());
        try {
            final URLConnection connection = prop.getUrl().openConnection();
            in = connection.getInputStream();
            final int maximum = connection.getContentLength();
            if (!prop.getUrl().equals(connection.getURL())) {
                throw new FileNotFoundException("\"" + prop.getUrl()
                    + "\" was substituted by " + "\"" + connection.getURL()
                    + "\" from server");
            }
            IoUtility.createNecessaryDirectories(f);
            out = new FileOutputStream(f);
            final byte[] buffer = new byte[4096];
            int bytesRead;  // bytes read during one buffer read
            int position = 0;   // current reading position within the whole document
            // continue writing
            while ((bytesRead = in.read(buffer)) != -1) {
                position += bytesRead;
                out.write(buffer, 0, bytesRead);
                int completeness = (int) (position * 100 / maximum);
                if (completeness < 0) {
                    completeness = 0;
                }
                if (completeness > 100) {
                    completeness = 100;
                }
                prop.setLoadingCompleteness(completeness);
            }
            prop.setLoadingCompleteness(100);
        } catch (IOException e) {
            Trace.trace(CLASS, this, method, e);
            try {
                out.close();
            } catch (Exception ex) {
            }
            try {
                f.delete();
            } catch (Exception ex) {
                Trace.trace(CLASS, this, method, ex);
            }
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_WEB_FAILED,
                new DefaultSourceFileExceptionList(e));
            ModuleEventLog.getInstance().stateChanged(prop);
            Trace.trace(CLASS, this, method, "Couldn't access " + prop.getUrl());
            throw e;
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            };
            try {
                in.close();
            } catch (Exception e) {
            };
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Transform an URL address into a relative local file path. This can also be another file name.
     *
     * @param   address     Transform this URL.
     * @return  Result of transformation.
     */
    public final File getLocalFilePath(final ModuleAddress address) {
        final String method = "localizeInFileSystem(URL)";
        if (address.isFileAddress()) {
            return new File(address.getURL().getFile());
        }
        final URL url = address.getURL();
        Trace.param(CLASS, this, method, "protocol", url.getProtocol());
        Trace.param(CLASS, this, method, "host", url.getHost());
        Trace.param(CLASS, this, method, "port", url.getPort());
        Trace.param(CLASS, this, method, "path", url.getPath());
        Trace.param(CLASS, this, method, "file", url.getFile());
        StringBuffer file = new StringBuffer(url.getFile());
        StringUtility.replace(file, "_", "__");    // remember all '_'
        StringUtility.replace(file, "/", "_1");    // preserve all '/'
        String encoded = file.toString();           // fallback file name
        try {
            encoded = URLEncoder.encode(file.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not occur
            Trace.trace(DefaultModuleAddress.class, "localizeInFileSystem(String)", e);
        }
        file.setLength(0);
        file.append(encoded);
        StringUtility.replace(file, "#", "##");    // escape all '#'
        StringUtility.replace(file, "_1", "#");    // from '/' into '#'
        StringUtility.replace(file, "__", "_");    // from '_' into '_'
        StringBuffer adr = new StringBuffer(url.toExternalForm());
        try {
            adr = new  StringBuffer(new URL(url.getProtocol(), url.getHost(),
                url.getPort(), file.toString()).toExternalForm());
        } catch (MalformedURLException e) {
            Trace.fatal(CLASS, this, "localizeInFileSystem(URL)", "unexpected", e);
            e.printStackTrace();
        }
        // escape characters:
        StringUtility.replace(adr, "://", "_");    // before host
        StringUtility.replace(adr, ":", "_");      // before protocol
        return new File(kernel.getBufferDirectory(), adr.toString());
    }

    /**
     * Increment intern process counter.
     */
    private final void processInc() {
        synchronized (syncToken) {
            this.processCounter++;
        }
    }


    /**
     * Decrement intern process counter.
     */
    private final void processDec() {
        synchronized (syncToken) {
            this.processCounter--;
        }
    }

    public File getBufferDirectory() {
        return kernel.getConfig().getBufferDirectory();
    }

    public File getGenerationDirectory() {
        return kernel.getConfig().getGenerationDirectory();
    }

    public ModuleProperties getModuleProperties(final ModuleAddress address) {
        return getModules().getModuleProperties(address);
    }

    public ModuleAddress getModuleAddress(final URL url) throws IOException {
        return new DefaultModuleAddress(url);
    }

    public ModuleAddress getModuleAddress(final String url) throws IOException {
        return new DefaultModuleAddress(url);
    }

    public ModuleAddress getModuleAddress(final File file) throws IOException {
        return new DefaultModuleAddress(file);
    }

    /**
     * Get all loaded QEDEQ modules.
     *
     * @return  All QEDEQ modules.
     */
    private final Modules getModules() {
        return modules;
    }

    private SourceFileExceptionList createXmlFileExceptionList(final IOException e) {
        return new DefaultSourceFileExceptionList(e);
    }

    private SourceFileExceptionList createXmlFileExceptionList(
            final ModuleFileNotFoundException e) {
        return new DefaultSourceFileExceptionList(e);
    }

    private SourceFileExceptionList createXmlFileExceptionList(
            final ParserConfigurationException e) {
        return new DefaultSourceFileExceptionList(e);
    }

    private DefaultSourceFileExceptionList createXmlFileExceptionList(final SAXException e) {
        return new DefaultSourceFileExceptionList(e);
    }

}
