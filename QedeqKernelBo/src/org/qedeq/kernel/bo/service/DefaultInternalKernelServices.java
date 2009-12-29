/* $Id: DefaultInternalKernelServices.java,v 1.2 2008/08/02 04:33:09 m31 Exp $
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

package org.qedeq.kernel.bo.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelProperties;
import org.qedeq.kernel.bo.context.KernelServices;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.latex.Qedeq2Latex;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;


/**
 * This class provides a default implementation for the QEDEQ module services.
 *
 * @version $Revision: 1.2 $
 * @author Michael Meyling
 */
public class DefaultInternalKernelServices implements KernelServices, InternalKernelServices {

    /** This class. */
    private static final Class CLASS = DefaultInternalKernelServices.class;

    /** For synchronized waiting. */
    private static final Object MONITOR = new Object();

    /** Number of method calls. */
    private volatile int processCounter = 0;

    /** Collection of already known QEDEQ modules. */
    private final KernelQedeqBoStorage modules;

    /** Kernel properties access. */
    private final KernelProperties kernel;

    /** This instance nows how to load a module from the file system. */
    private final QedeqFileDao qedeqFileDao;

    /** Validate module dependencies and status. */
    private boolean validate = true;

    /**
     * Constructor.
     *
     * @param kernel For kernel access.
     * @param loader For loading QEDEQ modules.
     */
    public DefaultInternalKernelServices(final KernelProperties kernel, final QedeqFileDao loader) {
        modules = new KernelQedeqBoStorage();
        this.kernel = kernel;
        this.qedeqFileDao = loader;
        loader.setServices(this);
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
                    final String method = "autoReloadLastSessionChecked.thread.run()";
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
                                    + number + " module" + (number != 1 ? "s" : "")
                                    + " were tried.");
                        }
                    } catch (Exception e) {
                        Trace.trace(CLASS, this, method, e);
                    } finally {
                        Trace.end(CLASS, this, method);
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void removeAllModules() {
        do {
            synchronized (this) {
                if (processCounter == 0) {
                    getModules().removeAllModules();
                    return;
                }
            }
            // we must wait for the other processes to stop (so that processCounter == 0)
            synchronized (MONITOR) {
                try {
                    MONITOR.wait(10000);
                } catch (InterruptedException e) {
                }
            }
        } while (true);

    }

    /**
     * Remove a QEDEQ module from memory.
     *
     * @param address Remove module identified by this address.
     */
    public void removeModule(final ModuleAddress address) {
        final QedeqBo prop = getQedeqBo(address);
        if (prop != null) {
            removeModule(getModules().getKernelQedeqBo(this, address));
            if (validate) {
                modules.validateDependencies();
            }
        }
    }

    /**
     * Remove a QEDEQ module from memory. This method must block all other methods and if this
     * method runs no other is allowed to run
     *
     * @param prop Remove module identified by this property.
     */
    private void removeModule(final DefaultKernelQedeqBo prop) {
        do {
            synchronized (this) {
                if (processCounter == 0) { // no other method is allowed to run
                    // TODO mime 20080319: one could call prop.setLoadingProgressState(
                    // LoadingState.STATE_DELETED) alone but that would
                    // miss to inform the KernelQedeqBoPool. How do we inform the pool?
                    // must the StateManager have a reference to it?
                    prop.delete();
                    getModules().removeModule(prop);
                    return;
                }
            }
            // we must wait for the other processes to stop (so that processCounter == 0)
            synchronized (MONITOR) {
                try {
                    MONITOR.wait(10000);
                } catch (InterruptedException e) {
                }
            }
        } while (true);

    }

    /**
     * Clear local file buffer and all loaded QEDEQ modules.
     *
     * @throws IOException Deletion of all buffered file was not successful.
     */
    public void clearLocalBuffer() throws IOException {
        removeAllModules();
        final File bufferDir = getBufferDirectory().getCanonicalFile();
        if (bufferDir.exists() && !IoUtility.deleteDir(bufferDir, new FileFilter() {
                    public boolean accept(final File pathname) {
                        return pathname.getName().endsWith(".xml");
                    }
                })) {
            throw new IOException("buffer could not be deleted: " + bufferDir);
        }
    }

    public QedeqBo loadModule(final ModuleAddress address) {
        final String method = "loadModule(ModuleAddress)";
        processInc();
        final DefaultKernelQedeqBo prop = getModules().getKernelQedeqBo(this, address);
        try {
            synchronized (prop) {
                if (prop.isLoaded()) {
                    return prop;
                }
                QedeqLog.getInstance().logRequest("Load module \"" + address + "\"");
                if (prop.getModuleAddress().isFileAddress()) {
                    loadLocalModule(prop);
                } else {
                    // search in local file buffer
                    try {
                        getCanonicalReadableFile(prop);
                    } catch (ModuleFileNotFoundException e) { // file not found
                        // we will continue by creating a local copy
                        saveQedeqFromWebToBuffer(prop);
                    }
                    loadBufferedModule(prop);
                }
                QedeqLog.getInstance().logSuccessfulReply(
                    "Module \"" + prop.getModuleAddress().getFileName()
                        + "\" was successfully loaded.");
            }
        } catch (SourceFileExceptionList e) {
            Trace.trace(CLASS, this, method, e);
            QedeqLog.getInstance().logFailureState("Loading of module failed!", address.getUrl(),
                e.toString());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply("Loading failed", e.getMessage());
        } finally {
            processDec();
        }
        return prop;
    }

    /**
     * Load buffered QEDEQ module file.
     *
     * @param prop Load this.
     * @throws SourceFileExceptionList Loading or QEDEQ module failed.
     */
    private void loadBufferedModule(final DefaultKernelQedeqBo prop)
            throws SourceFileExceptionList {
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_BUFFER);
        final File localFile;
        try {
            localFile = getCanonicalReadableFile(prop);
        } catch (ModuleFileNotFoundException e) {
            final SourceFileExceptionList sfl = createSourcelFileExceptionList(e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, sfl);
            throw sfl;
        }

        prop.setQedeqFileDao(getQedeqFileDao()); // remember loader for this module
        final Qedeq qedeq;
        try {
            qedeq = getQedeqFileDao().loadQedeq(prop, localFile);
        } catch (IOException e) {
            final SourceFileExceptionList sfl = new DefaultSourceFileExceptionList(e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, sfl);
            throw sfl;
        } catch (SourceFileExceptionList sfl) {
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, sfl);
            throw sfl;
        }
        setCopiedQedeq(prop, qedeq);
    }

    /**
     * Load QEDEQ module file with file loader.
     *
     * @param prop Load this.
     * @throws SourceFileExceptionList Loading or copying QEDEQ module failed.
     */
    private void loadLocalModule(final DefaultKernelQedeqBo prop) throws SourceFileExceptionList {
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_LOCAL_FILE);
        final File localFile;
        try {
            localFile = getCanonicalReadableFile(prop);
        } catch (ModuleFileNotFoundException e) {
            final SourceFileExceptionList sfl = createSourcelFileExceptionList(e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED, sfl);
            throw sfl;
        }
        prop.setQedeqFileDao(getQedeqFileDao()); // remember loader for this module

        final Qedeq qedeq;
        try {
            qedeq = getQedeqFileDao().loadQedeq(prop, localFile);
        } catch (IOException e) {
            final SourceFileExceptionList sfl = new DefaultSourceFileExceptionList(e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, sfl);
            throw sfl;
        } catch (SourceFileExceptionList sfl) {
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED, sfl);
            throw sfl;
        }
        setCopiedQedeq(prop, qedeq);
    }

    private void setCopiedQedeq(final DefaultKernelQedeqBo prop, final Qedeq qedeq)
            throws SourceFileExceptionList {
        final String method = "setCopiedQedeq(KernelQedeqBo, Qedeq)";
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_INTO_MEMORY);
        QedeqVo vo = null;
        try {
            vo = QedeqVoBuilder.createQedeq(prop.getModuleAddress(), qedeq);
        } catch (ModuleDataException e) {
            Trace.trace(CLASS, this, method, e);
            final SourceFileExceptionList xl = prop.createSourceFileExceptionList(e, qedeq);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, xl);
            throw xl;
        }
        prop.setQedeqVo(vo);
        ModuleLabelsCreator moduleNodesCreator = new ModuleLabelsCreator(prop);
        try {
            prop.setLoaded(vo, moduleNodesCreator.createLabels());
        } catch (SourceFileExceptionList sfl) {
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, sfl);
            throw sfl;
        }
    }

    /**
     * Check if file exists and is readable. Checks the local buffer file for a buffered module or
     * the module file address directly. Returns canonical file path.
     *
     * @param prop Check for this file.
     * @return Canonical file path.
     * @throws ModuleFileNotFoundException File doesn't exist or is not readable.
     */
    private File getCanonicalReadableFile(final QedeqBo prop) throws ModuleFileNotFoundException {
        final String method = "checkLocalBuffer(File)";
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
            throw new ModuleFileNotFoundException("file not readable: " + file);
        }
        return file;
    }

    /**
     * Load specified QEDEQ module from QEDEQ parent module.
     *
     * @param parent Parent module address.
     * @param spec Specification for another QEDEQ module.
     * @return Loaded module.
     * @throws SourceFileExceptionList Loading failed.
     */
    public KernelQedeqBo loadModule(final ModuleAddress parent, final Specification spec)
            throws SourceFileExceptionList {

        final String method = "loadModule(Module, Specification)";
        Trace.begin(CLASS, this, method);
        Trace.trace(CLASS, this, method, spec);
        processInc();
        DefaultKernelQedeqBo prop = null; // currently tried module
        try {
            final ModuleAddress[] modulePaths;
            try {
                modulePaths = parent.getModulePaths(spec);
            } catch (IOException e) {
                Trace.trace(CLASS, this, method, e);
                throw createSourceFileExceptionList(e);
            }

            // now we iterate over the possible module addresses
            for (int i = 0; i < modulePaths.length; i++) {
                prop = getModules().getKernelQedeqBo(this, modulePaths[i]);
                Trace.trace(CLASS, this, method, "synchronizing at prop=" + prop);
                synchronized (prop) {
                    if (prop.isLoaded()) {
                        return (prop);
                    }
                    try {
                        if (prop.getModuleAddress().isFileAddress()) {
                            loadLocalModule(prop);
                        } else {
                            // search in local file buffer
                            try {
                                getCanonicalReadableFile(prop);
                            } catch (ModuleFileNotFoundException e) { // file not found
                                // we will continue by creating a local copy
                                saveQedeqFromWebToBuffer(prop);
                            }
                            loadBufferedModule(prop);
                        }
                        // success!
                        return prop;
                    } catch (SourceFileExceptionList e) {
                        Trace.trace(CLASS, this, method, e);
                        if (i + 1 < modulePaths.length) {
                            QedeqLog.getInstance().logMessage("trying alternate path");
                            // we continue with the next path
                        } else {
                            // we surrender
                            throw e;
                        }
                    }
                }
            }
            return prop; // never called, only here to soothe the compiler

        } finally {
            processDec();
            Trace.end(CLASS, this, method);
        }
    }

    public ModuleAddress[] getAllLoadedModules() {
        return getModules().getAllLoadedModules();
    }

    public void loadRequiredModules(final ModuleAddress address) throws SourceFileExceptionList {
        final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) loadModule(address);
        if (prop.hasFailures()) {
            throw prop.getException(); // FIXME mime 20080603: remove exception from signature
            // of this function and also from loadRequired
        }
        LoadRequiredModules.loadRequired(prop);
    }

    /**
     * Load all previously checked QEDEQ modules.
     *
     * @return Successfully reloaded all modules.
     */
    public boolean loadPreviouslySuccessfullyLoadedModules() {
        processInc();
        try {
            final String[] list = kernel.getConfig().getPreviouslyCheckedModules();
            boolean errors = false;
            for (int i = 0; i < list.length; i++) {
                try {
                    final ModuleAddress address = getModuleAddress(list[i]);
                    final QedeqBo prop = loadModule(address);
                    if (prop.hasFailures()) {
                        errors = true;
                    }
                } catch (IOException e) {
                    Trace.fatal(CLASS, this, "loadPreviouslySuccessfullyLoadedModules",
                        "internal error: " + "saved URLs are malformed", e);
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
            final String prefix = "http://qedeq.org/" + kernel.getKernelVersionDirectory() + "/";
            final String[] list = new String[] {
                prefix + "doc/math/qedeq_logic_v1.xml",
                prefix + "doc/math/qedeq_set_theory_v1.xml",
                prefix + "doc/project/qedeq_basic_concept.xml",
                prefix + "doc/project/qedeq_logic_language.xml",
                prefix + "sample/qedeq_sample1.xml", prefix + "sample/qedeq_error_sample_00.xml",
                prefix + "sample/qedeq_error_sample_01.xml",
                prefix + "sample/qedeq_error_sample_02.xml",
                prefix + "sample/qedeq_error_sample_03.xml",
                prefix + "sample/qedeq_error_sample_04.xml",
                prefix + "sample/qedeq_sample2_error.xml",
                prefix + "sample/qedeq_sample3_error.xml",
                prefix + "sample/qedeq_sample4_error.xml",
                prefix + "sample/qedeq_sample5_error.xml",
                prefix + "sample/qedeq_sample6_error.xml",
                prefix + "sample/qedeq_sample7_error.xml", };
            boolean errors = false;
            for (int i = 0; i < list.length; i++) {
                try {
                    final ModuleAddress address = getModuleAddress(list[i]);
                    final QedeqBo prop = loadModule(address);
                    if (prop.hasFailures()) {
                        errors = true;
                    }
                } catch (IOException e) {
                    Trace.fatal(CLASS, this, "loadPreviouslySuccessfullyLoadedModules",
                        "internal error: " + "saved URLs are malformed", e);
                    errors = true;
                }
            }
            return !errors;
        } finally {
            processDec();
        }
    }

    /**
     * Make local copy of a module if it is no file address.
     *
     * @param prop Module properties.
     * @throws SourceFileExceptionList Address was malformed or the file can not be found.
     * @deprecated use {@link saveQedeqFromWebToBuffer}
     */
    public void saveQedeqFromWebToBufferOld(final DefaultKernelQedeqBo prop)
            throws SourceFileExceptionList {
        final String method = "makeLocalCopy";
        Trace.begin(CLASS, this, method);

        if (prop.getModuleAddress().isFileAddress()) { // this is already a local file
            Trace.fatal(CLASS, this, method, "tried to make a local copy for a local module", null);
            Trace.end(CLASS, this, method);
            return;
        }
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);

        FileOutputStream out = null;
        InputStream in = null;
        final File f = getLocalFilePath(prop.getModuleAddress());
        try {
            final URLConnection connection = new URL(prop.getUrl()).openConnection();

            if (connection instanceof HttpURLConnection) {
                final HttpURLConnection httpConnection = (HttpURLConnection) connection;
// FIXME mime 20090701: this is java 1.5 code, how do we do it in 1.4?
//                httpConnection.setConnectTimeout(kernel.getConfig().getConnectTimeout());
//                httpConnection.setReadTimeout(kernel.getConfig().getReadTimeout());
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == 200) {
                    in = httpConnection.getInputStream();
                } else {
                    in = httpConnection.getErrorStream();
                    final String errorText = IoUtility.loadStreamWithoutException(in, 1000);
                    throw new IOException("Response code from HTTP server was " + responseCode
                        + (errorText.length() > 0 ? "\nResponse  text from HTTP server was:\n"
                        + errorText : ""));
                }
            } else {
                Trace.paramInfo(CLASS, this, method, "connection.getClass", connection.getClass()
                    .toString());
                in = connection.getInputStream();
            }

            if (!prop.getUrl().equals(connection.getURL().toString())) {
                throw new FileNotFoundException("\"" + prop.getUrl() + "\" was substituted by "
                    + "\"" + connection.getURL() + "\" from server");
            }
            final int maximum = connection.getContentLength();
            IoUtility.createNecessaryDirectories(f);
            out = new FileOutputStream(f);
            final byte[] buffer = new byte[4096];
            int bytesRead; // bytes read during one buffer read
            int position = 0; // current reading position within the whole document
            // continue writing
            while ((bytesRead = in.read(buffer)) != -1) {
                position += bytesRead;
                out.write(buffer, 0, bytesRead);
                if (maximum > 0) {
                    long completeness = (long) (position * 100 / maximum);
                    if (completeness < 0) {
                        completeness = 0;
                    }
                    if (completeness > 100) {
                        completeness = 100;
                    }
                    prop.setLoadingCompleteness((int) completeness);
                }
            }
            prop.setLoadingCompleteness(100);
        } catch (IOException e) {
            Trace.trace(CLASS, this, method, e);
            IoUtility.close(out);
            out = null;
            try {
                f.delete(); // FIXME do we really want to delete it? Perhaps there are infos in it!!
            } catch (Exception ex) {
                Trace.trace(CLASS, this, method, ex);
            }
            final SourceFileExceptionList sfl = new DefaultSourceFileExceptionList(e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_WEB_FAILED, sfl);
            Trace.trace(CLASS, this, method, "Couldn't access " + prop.getUrl());
            throw sfl;
        } finally {
            IoUtility.close(out);
            IoUtility.close(in);
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Make local copy of a module if it is no file address.
     *
     * @param prop Module properties.
     * @throws SourceFileExceptionList Address was malformed or the file can not be found.
     */
    public void saveQedeqFromWebToBuffer(final DefaultKernelQedeqBo prop)
            throws SourceFileExceptionList {
        final String method = "makeLocalCopy";
        Trace.begin(CLASS, this, method);

        if (prop.getModuleAddress().isFileAddress()) { // this is already a local file
            Trace.fatal(CLASS, this, method, "tried to make a local copy for a local module", null);
            Trace.end(CLASS, this, method);
            return;
        }
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);

        final File f = getLocalFilePath(prop.getModuleAddress());
        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        final String pHost = System.getProperty("proxyHost", "");
        final int pPort = Integer.parseInt(System.getProperty("proxyPort", "80"));
//        System.out.println("proxyHost=" + pHost);
//        System.out.println("proxyPort=" + pPort);
//        System.out.println(0 / 0);
        if (pHost.length() > 0) {
            client.getHostConfiguration().setProxy(pHost, pPort);
        }

        // Create a method instance.
        GetMethod httpMethod = new GetMethod(prop.getUrl());

        try {
            // Provide custom retry handler is necessary
            httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));

            httpMethod.getParams().setSoTimeout(kernel.getConfig().getConnectTimeout());
            // Throws IOException on TimeOut.

            int statusCode = client.executeMethod(httpMethod);

            if (statusCode != HttpStatus.SC_OK) {
                throw new FileNotFoundException("Problems loading: " + prop.getUrl() + "\n"
                    + httpMethod.getStatusLine());
            }

            // Read the response body.
            byte[] responseBody = httpMethod.getResponseBody();
            IoUtility.createNecessaryDirectories(f);
            IoUtility.saveFileBinary(f, responseBody);
            prop.setLoadingCompleteness(100);
        } catch (IOException e) {
            Trace.trace(CLASS, this, method, e);
            try {
                f.delete(); // FIXME do we really want to delete it? Perhaps there are infos in it!!
            } catch (Exception ex) {
                Trace.trace(CLASS, this, method, ex);
            }
            final SourceFileExceptionList sfl = new DefaultSourceFileExceptionList(e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_WEB_FAILED, sfl);
            Trace.trace(CLASS, this, method, "Couldn't access " + prop.getUrl());
            throw sfl;
        } finally {
            // Release the connection.
            httpMethod.releaseConnection();
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Transform an URL address into a relative local file path. This might also change the file
     * name. If the URL address is already a file address, the original file path is returned.
     *
     * @param address Transform this URL.
     * @return Result of transformation.
     */
    public final File getLocalFilePath(final ModuleAddress address) {
        final String method = "localizeInFileSystem(URL)";
        URL url;
        try {
            url = new URL(address.getUrl());
        } catch (MalformedURLException e) {
            // should not happen    FIXME mime 20090728: check it out
            throw new RuntimeException(e);
        }
        if (address.isFileAddress()) {
            return new File(url.getFile());
        }
        Trace.param(CLASS, this, method, "protocol", url.getProtocol());
        Trace.param(CLASS, this, method, "host", url.getHost());
        Trace.param(CLASS, this, method, "port", url.getPort());
        Trace.param(CLASS, this, method, "path", url.getPath());
        Trace.param(CLASS, this, method, "file", url.getFile());
        StringBuffer file = new StringBuffer(url.getFile());
        StringUtility.replace(file, "_", "_1"); // remember all '_'
        StringUtility.replace(file, "/", "_2"); // preserve all '/'
        String encoded = file.toString(); // fallback file name
        try {
            encoded = URLEncoder.encode(file.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not occur
            Trace.trace(CLASS, "getLocalFilePath(ModuleAddress)", e);
        }
        file.setLength(0);
        file.append(encoded);
        StringUtility.replace(file, "#", "##"); // escape all '#'
        StringUtility.replace(file, "_2", "#"); // from '/' into '#'
        StringUtility.replace(file, "_1", "_"); // from '_' into '_'
        StringBuffer adr = new StringBuffer(url.toExternalForm());
        try {
            adr = new StringBuffer(new URL(url.getProtocol(), url.getHost(), url.getPort(), file
                .toString()).toExternalForm());
        } catch (MalformedURLException e) {
            Trace.fatal(CLASS, this, "getLocalFilePath(ModuleAddress)", "unexpected", e);
            throw new RuntimeException(e);
        }
        // escape characters:
        StringUtility.replace(adr, "://", "_"); // before host
        StringUtility.replace(adr, ":", "_"); // before protocol
        return new File(getBufferDirectory(), adr.toString());
    }

    /**
     * Increment intern process counter.
     */
    private synchronized void processInc() {
        this.processCounter++;
    }

    /**
     * Decrement intern process counter.
     */
    private synchronized void processDec() {
        this.processCounter--;
    }

    public File getBufferDirectory() {
        return kernel.getConfig().getBufferDirectory();
    }

    public File getGenerationDirectory() {
        return kernel.getConfig().getGenerationDirectory();
    }

    public KernelQedeqBo getKernelQedeqBo(final ModuleAddress address) {
        return getModules().getKernelQedeqBo(this, address);
    }

    public QedeqBo getQedeqBo(final ModuleAddress address) {
        return getModules().getKernelQedeqBo(this, address);
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

    public String getSource(final ModuleAddress address) throws IOException {
        final KernelQedeqBo bo = getKernelQedeqBo(address);
        if (bo.getLoadingState().equals(LoadingState.STATE_UNDEFINED)
            || bo.getLoadingState().equals(LoadingState.STATE_LOADING_FROM_WEB)
            || bo.getLoadingState().equals(LoadingState.STATE_LOADING_FROM_WEB_FAILED)) {
            return null;
            // TODO mime 20080313: remove me
            // throw new IllegalStateException("module is not yet buffered " + address);
        }
        final StringBuffer buffer = new StringBuffer();
        IoUtility.loadReader(getQedeqFileDao().getModuleReader(bo), buffer);
        return buffer.toString();
    }

    public boolean checkModule(final ModuleAddress address) {

        final String method = "checkModule(ModuleAddress)";
        final DefaultKernelQedeqBo prop = modules.getKernelQedeqBo(this, address);
        try {
            QedeqLog.getInstance().logRequest(
                "Check logical correctness for \"" + prop.getUrl() + "\"");

            loadModule(address);
            LoadRequiredModules.loadRequired(prop);

            QedeqBoFormalLogicChecker.check(prop);
            QedeqLog.getInstance().logSuccessfulReply(
                "Check of logical correctness successful for \"" + prop.getUrl() + "\"");
        } catch (final SourceFileExceptionList e) {
            final String msg = "Check of logical correctness failed for \"" + address.getUrl()
                + "\"";
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (final RuntimeException e) {
            final String msg = "Check of logical correctness failed for \"" + address.getUrl()
                + "\"";
            Trace.fatal(CLASS, this, method, msg, e);
            final SourceFileExceptionList xl = new DefaultSourceFileExceptionList(e);
            // TODO mime 20080124: every state must be able to change into
            // a failure state, here we only assume three cases
            if (!prop.isLoaded()) {
                if (!prop.getLoadingState().isFailure()) {
                    prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, xl);
                }
            } else if (!prop.hasLoadedRequiredModules()) {
                if (!prop.getDependencyState().isFailure()) {
                    prop.setDependencyFailureState(
                        DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, xl);
                }
            } else {
                if (!prop.getLogicalState().isFailure()) {
                    prop.setLogicalFailureState(LogicalState.STATE_EXTERNAL_CHECKING_FAILED, xl);
                }
            }
            QedeqLog.getInstance().logFailureReply(msg, e.toString());
        }
        if (validate) {
            modules.validateDependencies();
        }
        return prop.isChecked();
    }

    public InputStream createLatex(final ModuleAddress address, final String language,
            final String level) throws DefaultSourceFileExceptionList, IOException {
        return Qedeq2Latex.createLatex(getKernelQedeqBo(address), language, level);
    }

    public String generateLatex(final ModuleAddress address, final String language,
            final String level) throws DefaultSourceFileExceptionList, IOException {
        return Qedeq2Latex.generateLatex(getKernelQedeqBo(address), language, level).toString();
    }

    /**
     * Get all loaded QEDEQ modules.
     *
     * @return All QEDEQ modules.
     */
    private KernelQedeqBoStorage getModules() {
        return modules;
    }

    private SourceFileExceptionList createSourceFileExceptionList(final IOException e) {
        return new DefaultSourceFileExceptionList(e);
    }

    private SourceFileExceptionList createSourcelFileExceptionList(
            final ModuleFileNotFoundException e) {
        return new DefaultSourceFileExceptionList(new IOException(e.getMessage()));
    }

    /**
     * Get description of source file exception list.
     *
     * @param address Get description for this module exceptions.
     * @return Error description and location.
     */
    public String[] getSourceFileExceptionList(final ModuleAddress address) {
        final List list = new ArrayList();
        final KernelQedeqBo bo = getKernelQedeqBo(address);
        final SourceFileExceptionList sfl = bo.getException();
        if (sfl != null) {
            final StringBuffer buffer = new StringBuffer();
            do {
                try {
                    IoUtility.loadReader(getQedeqFileDao().getModuleReader(bo), buffer);
                } catch (IOException e) {
                    for (int i = 0; i < sfl.size(); i++) {
                        list.add(sfl.get(i).getDescription());
                    }
                    break; // out of do while
                }
                final TextInput input = new TextInput(buffer);
                input.setPosition(0);
                final StringBuffer buf = new StringBuffer();
                for (int i = 0; i < sfl.size(); i++) {
                    buf.setLength(0);
                    final SourceFileException sf = sfl.get(i);
                    buf.append(sf.getDescription());
                    try {
                        if (sf.getSourceArea() != null
                            && sf.getSourceArea().getStartPosition() != null) {
                            buf.append("\n");
                            input.setRow(sf.getSourceArea().getStartPosition().getLine());
                            buf.append(StringUtility.replace(input.getLine(), "\t", " "));
                            buf.append("\n");
                            final StringBuffer whitespace = StringUtility.getSpaces(sf
                                .getSourceArea().getStartPosition().getColumn() - 1);
                            buffer.append(whitespace);
                            buffer.append("^");
                        }
                    } catch (Exception e) {
                        Trace.trace(CLASS, this, "getSourceFileExceptionList(ModuleAddress)", e);
                    }
                    list.add(buf.toString());
                }
                break; // out of do while
            } while (true);
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public QedeqFileDao getQedeqFileDao() {
        return qedeqFileDao;
    }

}
