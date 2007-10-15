/* $Id: DefaultModuleFactory.java,v 1.5 2007/10/07 16:40:12 m31 Exp $
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
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.bo.module.Kernel;
import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.ModuleFactory;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;
import org.qedeq.kernel.xml.parser.DefaultXmlFileExceptionList;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SaxParser;
import org.xml.sax.SAXException;


/**
 * This class provides access methods for loading QEDEQ modules.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class DefaultModuleFactory implements ModuleFactory {

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

    public final void startup() {
        if (kernel.getConfig().isAutoReloadLastSessionChecked()) {
            autoReloadLastSessionChecked();
        }
    }

    /**
     * If configured load all QEDEQ modules that where successfully loaded the last time.
     */
    public final void autoReloadLastSessionChecked() {
        if (kernel.getConfig().isAutoReloadLastSessionChecked()) {
            final Thread thread = new Thread() {
                public void run() {
                    final String method = "start()";
                    try {
                        Trace.begin(this, method);
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
                        Trace.trace(this, method, e);
                    } finally {
                        Trace.begin(this, method);
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    public final void removeAllModules() {
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
     * @param   prop    Remove module identified by this property.
     */
    public final void removeModuleAndDependents(final ModuleProperties prop) {
        do {
            synchronized (syncToken) {
                if (processCounter == 0) {
                    getModules().removeModuleAndDependents(prop);
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
    public final void clearLocalBuffer()
            throws IOException {
        removeAllModules();
        final File bufferDir = getBufferDirectory();
        if (bufferDir.exists() && !IoUtility.deleteDir(bufferDir)) {
            throw new IOException("buffer could not be deleted");
        }
    }

    /**
     * Get a certain module.
     *
     * @param   address     Address of module.
     * @return  Wanted module.
     * @throws  XmlFileExceptionList    Module could not be successfully loaded.
     */
    public final QedeqBo loadModule(final String address)
            throws XmlFileExceptionList {
        processInc();
        try {
            final ModuleAddress moduleAddress;
            try {
                moduleAddress = new DefaultModuleAddress(address);
            } catch (IOException e) {
                throw createXmlFileExceptionList(e);
            }
            return loadModule(moduleAddress);
        } finally {
            processDec();
        }
    }


    public final QedeqBo loadModule(
            final ModuleAddress moduleAddress)
            throws XmlFileExceptionList {
        final String method = "loadModule(ModuleAddress)";
        processInc();
        try {
            final ModuleProperties prop = getModules().getModuleProperties(moduleAddress);
            synchronized (prop) {
                if (prop.isLoaded()) {
                    return prop.getModule();
                }

                // search in local file buffer
                try {
                    final QedeqBo mod = loadLocalModule(moduleAddress);
                    return mod;
                } catch (ModuleFileNotFoundException e) {     // file not found
                    // nothing to do, we will continue by creating a local copy
                } catch (XmlFileExceptionList e) {
                    Trace.trace(this, method, e);
                    throw e;
                }

                // make local copy
                try {
                    makeLocalCopy(moduleAddress);
                } catch (IOException e) {
                    Trace.trace(this, method, e);
                    QedeqLog.getInstance().logFailureState("Loading of module failed!",
                        moduleAddress.getURL(), e.getMessage());
                    throw createXmlFileExceptionList(e);
                }
                final QedeqBo mod;
                try {
                    mod = loadLocalModule(moduleAddress);
                } catch (ModuleFileNotFoundException e) {
                    // TODO mime 20070415: This should not occur because a local copy was
                    // at least created a few lines above
                    Trace.trace(this, method, e);
                    QedeqLog.getInstance().logFailureState("Loading of module failed!",
                        moduleAddress.getURL(), e.getMessage());
                    throw e.createXmlFileExceptionList();
                } catch (XmlFileExceptionList e) {
                    Trace.trace(this, method, e);
                    QedeqLog.getInstance().logFailureState("Loading of module failed!",
                    moduleAddress.getURL(), e.getMessage());
                    throw e;
                }
                return mod;
             }
         } finally {
             processDec();
         }
    }

    public final QedeqBo loadModule(final QedeqBo module,
            final Specification spec) throws XmlFileExceptionList {

        final String method = "loadModule(Module, Specification)";
        Trace.begin(this, method);
        Trace.trace(this, method, spec);
        processInc();
        try {
            final ModuleAddress[] modulePaths;
            try {
                modulePaths = DefaultModuleAddress.getModulePaths(module, spec);
            } catch (IOException e) {
                Trace.trace(this, method, e);
                throw createXmlFileExceptionList(e);
            }
            // search in already loaded modules
            for (int i = 0; i < modulePaths.length; i++) {
                final ModuleProperties prop
                    = getModules().getModuleProperties(modulePaths[i]);
                synchronized (prop) {
                    if (prop.isLoaded()) {
                        return (prop.getModule());
                    }
                }
            }

            // search in local file buffer
            Trace.trace(this, method, "searching file buffer");
            for (int i = 0; i < modulePaths.length; i++) {
                try {
                    final ModuleProperties prop
                        = getModules().getModuleProperties(modulePaths[i]);
                    Trace.trace(this, method, "synchronizing at prop=" + prop);
                    synchronized (prop) {
                        final QedeqBo mod = loadLocalModule(modulePaths[i]);
                        return mod;
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
                        makeLocalCopy(modulePaths[i]);
                        final QedeqBo mod = loadLocalModule(modulePaths[i]);
                        return mod;
                    }
                 } catch (IOException e) {
                     QedeqLog.getInstance().logFailureState("Loading of module failed!",
                         modulePaths[i].getURL(), e.getMessage());
                     Trace.trace(this, method, e);
                     throw createXmlFileExceptionList(e);
                 } catch (ModuleFileNotFoundException e) {
                     Trace.trace(this, method, e);
                     QedeqLog.getInstance().logFailureState("Loading of module failed!",
                         modulePaths[i].getURL(), e.getMessage());
                     throw e.createXmlFileExceptionList();
                 }
            }
            throw (new ModuleFileNotFoundException("no QEDEQ module found"))
                .createXmlFileExceptionList();
        } finally {
            processDec();
            Trace.end(this, method);
        }
    }

    public final String[] getAllLoadedModules() {
        return getModules().getAllLoadedModules();
    }

    /**
     * Load all previously checked QEDEQ modules.
     *
     * @return  Successfully reloaded all modules.
     */
    public final boolean loadPreviouslySuccessfullyLoadedModules() {
        processInc();
        try {
            final String[] list = kernel.getConfig().getPreviouslyCheckedModules();
            boolean errors = false;
            for (int i = 0; i < list.length; i++) {
                try {
                    loadModule(list[i]);
                } catch (XmlFileExceptionList e) {
                    errors = true;
                }
            }
            return !errors;
        } finally {
            processDec();
        }
    }

    // LATER mime 20070326: dynamic loading from web page directory
    public final boolean loadAllModulesFromQedeq() {
        processInc();
        try {
            final String prefix = "http://www.qedeq.org/"
                + kernel.getKernelVersionDirectory() + "/";
            final String[] list = new String[] {
                prefix + "doc/math/qedeq_logic_v1.xml",
                prefix + "doc/math/qedeq_set_theory_v1.xml",
                prefix + "doc/math/qedeq_sample1.xml",
                prefix + "doc/project/qedeq_basic_concept.xml",
                prefix + "doc/project/qedeq_logic_language.xml",
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
                    loadModule((String) list[i]);
                } catch (XmlFileExceptionList e) {
                    errors = true;
                }
            }
            return !errors;
        } finally {
           processDec();
        }
    }

    public final String getLocalName(final ModuleAddress moduleAddress) {
        if (moduleAddress.isFileAddress()) {
            return moduleAddress.getPath() + moduleAddress.getFileName();
        }
        return kernel.getBufferDirectory() + "/"
            + moduleAddress.localizeInFileSystem(moduleAddress.getURL());
    }


    /**
     * Load a local QEDEQ module.
     *
     * @param   moduleAddress   Module address.
     * @return  Loaded module.
     * @throws  ModuleFileNotFoundException    Local file was not found.
     * @throws  XmlFileExceptionList    Module could not be successfully loaded.
     */
    private final QedeqBo loadLocalModule(
            final ModuleAddress moduleAddress)
            throws ModuleFileNotFoundException, XmlFileExceptionList {
        final String method = "loadLocalModule";
        final String localName = getLocalName(moduleAddress);
        final File file;
        try {
            file = new File(localName).getCanonicalFile();
        } catch (IOException e) {
            Trace.trace(this, method, e);
            throw new ModuleFileNotFoundException("file path not correct: " + localName);
        }
        if (!file.canRead()) {
            Trace.trace(this, method, "file not readablee=" + file);
            throw new ModuleFileNotFoundException("file not found: " + file);
        }
        ModuleProperties prop = getModules().getModuleProperties(moduleAddress);
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
            Trace.trace(this, method, e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED,
                new DefaultXmlFileExceptionList(e));
            ModuleEventLog.getInstance().stateChanged(prop);
            throw createXmlFileExceptionList(e);
        } catch (ParserConfigurationException e) {
            Trace.trace(this, method, e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED,
                new DefaultXmlFileExceptionList(new RuntimeException(
                    "XML parser configuration error", e)));
            ModuleEventLog.getInstance().stateChanged(prop);
            throw createXmlFileExceptionList(e);
        }
        try {
            parser.parse(file);
        } catch (XmlFileExceptionList e) {
            Trace.trace(this, method, e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, e);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw e;
        }
        qedeq = simple.getQedeq();
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_INTO_MEMORY);
        ModuleEventLog.getInstance().stateChanged(prop);
        final QedeqBo qedeqBo;
        try {
            qedeqBo = QedeqBoFactory.createQedeq(file.getPath(), qedeq);
            qedeqBo.setModuleAddress(prop.getModuleAddress());
        } catch (ModuleDataException e) {
            Trace.trace(this, method, e);
            final XmlFileExceptionList xl
                = ModuleDataException2XmlFileException.createXmlFileExceptionList(e, qedeq);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, xl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw xl;
        }
        prop.setLoaded(qedeqBo);
        ModuleEventLog.getInstance().stateChanged(prop);
        return qedeqBo;
    }

    /**
     * Make local copy of a module if it is no file address.
     *
     * @param   moduleAddress   module address
     * @throws  IOException    if address was malformed or the file can not be found
     */
    private final synchronized void makeLocalCopy(
            final ModuleAddress moduleAddress)
            throws IOException {
        final String method = "makeLocalCopy";
        Trace.begin(this, method);
        ModuleProperties prop = getModules().getModuleProperties(moduleAddress);

        if (prop.getLoadingState() == LoadingState.STATE_UNDEFINED) {
            prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);
            ModuleEventLog.getInstance().addModule(prop);
        } else {
            prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);
            ModuleEventLog.getInstance().stateChanged(prop);
        }

        if (moduleAddress.isFileAddress()) {
            return;
        }
        FileOutputStream out = null;
        InputStream in = null;
        try {
            final URLConnection connection = moduleAddress.getURL().openConnection();
            in = connection.getInputStream();
            final int maximum = connection.getContentLength();
            if (!moduleAddress.getURL().equals(connection.getURL())) {
                throw new FileNotFoundException("\"" + moduleAddress.getURL()
                    + "\" was substituted by " + "\"" + connection.getURL()
                    + "\" from server");
            }
            {   // assure existence of directories
                final String localName = getLocalName(moduleAddress);
                System.out.println(localName);
                final File f = new File(localName).getParentFile();
                f.mkdirs();
            }
            out = new FileOutputStream(getLocalName(moduleAddress));
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
            Trace.trace(this, method, e);
            try {
                out.close();
            } catch (Exception ex) {
            }
            try {
                new File(getLocalName(moduleAddress)).delete();
            } catch (Exception ex) {
                Trace.trace(this, method, ex);
            }
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_WEB_FAILED,
                new DefaultXmlFileExceptionList(e));
            ModuleEventLog.getInstance().stateChanged(prop);
            Trace.trace(this, method, "Couldn't access " + moduleAddress.getURL());
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
            Trace.end(this, method);
        }
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

    public final File getBufferDirectory() {
        return kernel.getConfig().getRelativeToBasis(kernel.getConfig().getBufferDirectory());
    }

    public final File getGenerationDirectory() {
        return kernel.getConfig().getRelativeToBasis(kernel.getConfig().getGenerationDirectory());
    }

    public final ModuleProperties getModuleProperties(final String address) {
        try {
            final ModuleProperties prop = getModules().getModuleProperties(address);
            return prop;
        } catch (IOException e) {
            Trace.trace(this, "getModuleProperties", e);
            return null;
        }
    }

    /**
     * Get all loaded QEDEQ modules.
     *
     * @return  All QEDEQ modules.
     */
    private final Modules getModules() {
        return modules;
    }

    private XmlFileExceptionList createXmlFileExceptionList(final IOException e) {
        return new DefaultXmlFileExceptionList(e);
    }

    private XmlFileExceptionList createXmlFileExceptionList(final ParserConfigurationException e) {
        final DefaultXmlFileExceptionList list
            = new DefaultXmlFileExceptionList();
        list.add(e);
        return list;
    }

    private DefaultXmlFileExceptionList createXmlFileExceptionList(final SAXException e) {
        final DefaultXmlFileExceptionList list
            = new DefaultXmlFileExceptionList();
        list.add(e);
        return list;
    }

}
