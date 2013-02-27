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

package org.qedeq.kernel.bo.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.LoadingListener;
import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.SourceArea;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.io.UrlUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.KernelProperties;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.ServiceModule;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.logic.FormalProofCheckerPlugin;
import org.qedeq.kernel.bo.service.logic.SimpleProofFinderPlugin;
import org.qedeq.kernel.bo.service.logic.WellFormedCheckerPlugin;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.base.module.Specification;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.config.QedeqConfig;
import org.qedeq.kernel.se.dto.module.QedeqVo;
import org.qedeq.kernel.se.state.LoadingState;


/**
 * This class provides a default implementation for the QEDEQ module services.
 *
 * @author  Michael Meyling
 */
public class DefaultInternalKernelServices implements ServiceModule, InternalKernelServices,
        Plugin {

    /** This class. */
    private static final Class CLASS = DefaultInternalKernelServices.class;

    /** For synchronized waiting. */
    private static final Object MONITOR = new Object();

    /** Number of method calls. */
    private volatile int processCounter = 0;

    /** Collection of already known QEDEQ modules. */
    private KernelQedeqBoStorage modules;

    /** Config access. */
    private final QedeqConfig config;

    /** Basic kernel properties. */
    private final KernelProperties kernel;

    /** This instance nows how to load a module from the file system. */
    private final QedeqFileDao qedeqFileDao;

    /** This instance manages plugins. */
    private final PluginManager pluginManager;

    /** This instance manages service processes. */
    private final ServiceProcessManager processManager;

    /** Validate module dependencies and status. */
    private boolean validate = true;

    /**
     * Constructor.
     *
     * @param   config  For config access.
     * @param   kernel  For kernel properties.
     * @param   loader  For loading QEDEQ modules.
     */
    public DefaultInternalKernelServices(final QedeqConfig config, final KernelProperties kernel,
            final QedeqFileDao loader) {
        this.config = config;
        this.kernel = kernel;
        this.qedeqFileDao = loader;
        processManager = new ServiceProcessManager();
        pluginManager = new PluginManager(this, processManager);
        loader.setServices(this);

////      pluginManager.addPlugin(MultiProofFinderPlugin.class.getName());
        pluginManager.addPlugin("org.qedeq.kernel.bo.service.unicode.Qedeq2UnicodeTextPlugin");
        pluginManager.addPlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin");
        pluginManager.addPlugin("org.qedeq.kernel.bo.service.unicode.Qedeq2Utf8Plugin");
////        pluginManager.addPlugin("org.qedeq.kernel.bo.service.heuristic.HeuristicCheckerPlugin");
        pluginManager.addPlugin("org.qedeq.kernel.bo.service.heuristic.DynamicHeuristicCheckerPlugin");
        pluginManager.addPlugin(SimpleProofFinderPlugin.class.getName());

        // add internal plugins
        pluginManager.addPlugin(WellFormedCheckerPlugin.class.getName());
        pluginManager.addPlugin(FormalProofCheckerPlugin.class.getName());
    }

    public void startupServices() {
        modules = new KernelQedeqBoStorage();
        if (config.isAutoReloadLastSessionChecked()) {
            autoReloadLastSessionChecked();
        }
    }

    public void shutdownServices() {
        processManager.terminateAndRemoveAllServiceProcesses();
        modules.removeAllModules();
        modules = null;
        // clear thread interrupt flag because we might have interrupted ourself
        Thread.interrupted();
    }

    /**
     * If configured load all QEDEQ modules that where successfully loaded the last time.
     */
    public void autoReloadLastSessionChecked() {
        if (config.isAutoReloadLastSessionChecked()) {
            final Thread thread = new Thread() {
                public void run() {
                    final String method = "autoReloadLastSessionChecked.thread.run()";
                    try {
                        Trace.begin(CLASS, this, method);
                        QedeqLog.getInstance().logMessage(
                            "Trying to load previously successfully loaded modules.");
                        final int number = config.getPreviouslyLoadedModules().length;
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
            QedeqLog.getInstance().logRequest("Removing module", address.getUrl());
            try {
                removeModule(getModules().getKernelQedeqBo(this, address));
            } catch (final RuntimeException e) {
                QedeqLog.getInstance().logFailureReply(
                    "Remove failed", address.getUrl(), e.getMessage());
            }

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
     * @return  Successful?
     */
    public boolean clearLocalBuffer() {
        final String method = "clearLocalBuffer";
        try {
            QedeqLog.getInstance().logMessage(
                "Clear local buffer from all QEDEQ files.");
            removeAllModules();
            final File bufferDir = getBufferDirectory().getCanonicalFile();
            if (bufferDir.exists() && !IoUtility.deleteDir(bufferDir, new FileFilter() {
                        public boolean accept(final File pathname) {
                            return pathname.getName().endsWith(".xml");
                        }
                    })) {
                throw new IOException("buffer could not be deleted: " + bufferDir);
            }
            QedeqLog.getInstance().logMessage("Local buffer was cleared.");
            return true;
        } catch (IOException e) {
            Trace.fatal(CLASS, this, method, "IO access problem", e);
            QedeqLog.getInstance().logMessage(
                "Local buffer not cleared. IO access problem. " + e.getMessage());
            return false;
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logMessage(
                "Local buffer not cleared. " + e.getMessage());
            return false;
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
                QedeqLog.getInstance().logRequest("Load module", address.getUrl());
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
                    "Successfully loaded", address.getUrl());
            }
        } catch (SourceFileExceptionList e) {
            Trace.trace(CLASS, this, method, e);
            QedeqLog.getInstance().logFailureState("Loading of module failed!", address.getUrl(),
                e.toString());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply("Loading failed", address.getUrl(), e.getMessage());
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
        prop.setLoadingProgressState(this, LoadingState.STATE_LOADING_FROM_BUFFER);
        final File localFile;
        try {
            localFile = getCanonicalReadableFile(prop);
        } catch (ModuleFileNotFoundException e) {
            final SourceFileExceptionList sfl = createSourceFileExceptionList(
                ServiceErrors.LOADING_FROM_FILE_BUFFER_FAILED_CODE,
                ServiceErrors.LOADING_FROM_FILE_BUFFER_FAILED_TEXT,
                prop.getUrl(), e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, sfl);
            throw sfl;
        }

        prop.setQedeqFileDao(getQedeqFileDao()); // remember loader for this module
        final Qedeq qedeq;
        try {
            qedeq = getQedeqFileDao().loadQedeq(prop, localFile);
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
        prop.setLoadingProgressState(this, LoadingState.STATE_LOADING_FROM_LOCAL_FILE);
        final File localFile;
        try {
            localFile = getCanonicalReadableFile(prop);
        } catch (ModuleFileNotFoundException e) {
            final SourceFileExceptionList sfl = createSourceFileExceptionList(
                ServiceErrors.LOADING_FROM_LOCAL_FILE_FAILED_CODE,
                ServiceErrors.LOADING_FROM_LOCAL_FILE_FAILED_TEXT,
                prop.getUrl(), e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED, sfl);
            throw sfl;
        }
        prop.setQedeqFileDao(getQedeqFileDao()); // remember loader for this module

        final Qedeq qedeq;
        try {
            qedeq = getQedeqFileDao().loadQedeq(prop, localFile);
        } catch (SourceFileExceptionList sfl) {
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED, sfl);
            throw sfl;
        }
        setCopiedQedeq(prop, qedeq);
    }

    private void setCopiedQedeq(final DefaultKernelQedeqBo prop, final Qedeq qedeq)
            throws SourceFileExceptionList {
        final String method = "setCopiedQedeq(DefaultKernelQedeqBo, Qedeq)";
        prop.setLoadingProgressState(this, LoadingState.STATE_LOADING_INTO_MEMORY);
        QedeqVo vo = null;
        try {
            vo = QedeqVoBuilder.createQedeq(prop.getModuleAddress(), qedeq);
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, method, "looks like a programming error", e);
            final SourceFileExceptionList xl = createSourceFileExceptionList(
                ServiceErrors.RUNTIME_ERROR_CODE,
                ServiceErrors.RUNTIME_ERROR_TEXT,
                prop.getModuleAddress().getUrl(), e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, xl);
            throw xl;
        } catch (ModuleDataException e) {
            if (e.getCause() != null) {
                Trace.fatal(CLASS, this, method, "looks like a programming error", e.getCause());
            } else {
                Trace.fatal(CLASS, this, method, "looks like a programming error", e);
            }
            final SourceFileExceptionList xl = prop.createSourceFileExceptionList(this, e, qedeq);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, xl);
            throw xl;
        }
        prop.setQedeqVo(vo);
        // TODO 20110213 m31: perhaps we need a new state, pre loaded? So when we put more
        // label testing into the moduleLabelCreator, we still can launch some plugins
        // On the other side: Label checking is only possible, if all referenced modules can
        // be loaded.
        //
        // Correct labels are necessary for many plugins (e.g. LaTeX and UTF-8 generation).
        // So a label checker must be run before that.
        // It might be a good idea to put it into the formal logic checker.
        // We could make a FormalChecker plugin. This starts loading required modules, checks
        // the labels and checks if the formulas are correctly written.
        // So we get some sub status (for every check) and an overall status (all checks
        // green). Later on the formal proof checker can be integrated too.
        // This should be the extended load status.
        final ModuleLabelsCreator moduleNodesCreator = new ModuleLabelsCreator(this, prop);
        try {
            moduleNodesCreator.createLabels();
            prop.setLoaded(vo, moduleNodesCreator.getLabels(), moduleNodesCreator.getConverter(),
                moduleNodesCreator.getTextConverter());
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
        final String method = "getCanonicalReadableFile(File)";
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
                Trace.fatal(CLASS, this, method, "getting module path failed", e);  // TODO 20110308 m31: make constant
                throw createSourceFileExceptionList(
                    ServiceErrors.LOADING_FROM_FILE_BUFFER_FAILED_CODE,
                    ServiceErrors.LOADING_FROM_FILE_BUFFER_FAILED_TEXT,
                    parent.getUrl(), e);
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
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply("Loading failed", prop.getUrl(), e.getMessage());
            throw e;
        } finally {
            processDec();
            Trace.end(CLASS, this, method);
        }
    }

    public ModuleAddress[] getAllLoadedModules() {
        return getModules().getAllLoadedModules();
    }

    public boolean loadRequiredModules(final ModuleAddress address) {
        final DefaultKernelQedeqBo prop = (DefaultKernelQedeqBo) loadModule(address);
        if (prop.hasBasicFailures()) {
            return false;
        }
        return LoadRequiredModules.loadRequired(this, prop);
    }

    /**
     * Load all previously checked QEDEQ modules.
     *
     * @return Successfully reloaded all modules.
     */
    public boolean loadPreviouslySuccessfullyLoadedModules() {
        processInc();
        try {
            final String[] list = config.getPreviouslyLoadedModules();
            boolean errors = false;
            for (int i = 0; i < list.length; i++) {
                try {
                    final ModuleAddress address = getModuleAddress(list[i]);
                    final QedeqBo prop = loadModule(address);
                    if (prop.hasErrors()) {
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
            final String prefix = "http://www.qedeq.org/" + kernel.getKernelVersionDirectory() + "/";
            final String[] list = new String[] {
                prefix + "doc/math/qedeq_logic_v1.xml",
                prefix + "doc/math/qedeq_formal_logic_v1.xml",
                prefix + "doc/math/qedeq_set_theory_v1.xml",
                prefix + "doc/project/qedeq_basic_concept.xml",
                prefix + "doc/project/qedeq_logic_language.xml",
                prefix + "sample/qedeq_sample1.xml",
                prefix + "sample/qedeq_sample2.xml",
                prefix + "sample/qedeq_sample3.xml",
                prefix + "sample/qedeq_sample4.xml",
                prefix + "sample/qedeq_error_sample_00.xml",
                prefix + "sample/qedeq_error_sample_01.xml",
                prefix + "sample/qedeq_error_sample_02.xml",
                prefix + "sample/qedeq_error_sample_03.xml",
                prefix + "sample/qedeq_error_sample_04.xml",
                prefix + "sample/qedeq_error_sample_05.xml",
                prefix + "sample/qedeq_error_sample_12.xml",
                prefix + "sample/qedeq_error_sample_13.xml",
                prefix + "sample/qedeq_error_sample_14.xml",
                prefix + "sample/qedeq_error_sample_15.xml",
                prefix + "sample/qedeq_error_sample_16.xml",
                prefix + "sample/qedeq_error_sample_17.xml",
                prefix + "sample/qedeq_error_sample_18.xml"};
            boolean errors = false;
            for (int i = 0; i < list.length; i++) {
                try {
                    final ModuleAddress address = getModuleAddress(list[i]);
                    final QedeqBo prop = loadModule(address);
                    if (prop.hasErrors()) {
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
     * @param   prop    Module properties.
     * @throws  SourceFileExceptionList Address was malformed or the file can not be found.
     */
    private void saveQedeqFromWebToBuffer(final DefaultKernelQedeqBo prop)
            throws SourceFileExceptionList {
        final String method = "saveQedeqFromWebToBuffer(DefaultKernelQedeqBo)";
        Trace.begin(CLASS, this, method);

        if (prop.getModuleAddress().isFileAddress()) { // this is already a local file
            Trace.fatal(CLASS, this, method, "tried to make a local copy for a local module", null);
            Trace.end(CLASS, this, method);
            return;
        }
        prop.setLoadingProgressState(this, LoadingState.STATE_LOADING_FROM_WEB);

        final File f = getLocalFilePath(prop.getModuleAddress());
        try {
            UrlUtility.saveUrlToFile(prop.getUrl(), f,
            config.getHttpProxyHost(), config.getHttpProxyPort(), config.getHttpNonProxyHosts(),
            config.getConnectionTimeout(), config.getReadTimeout(), new LoadingListener() {
                public void loadingCompletenessChanged(final double completeness) {
                    prop.setLoadingCompleteness((int) completeness * 100);
                }
            });
        } catch (IOException e) {
            Trace.trace(CLASS, this, method, e);
            try {
                f.delete();
            } catch (Exception ex) {
                Trace.trace(CLASS, this, method, ex);
            }
            final SourceFileExceptionList sfl = createSourceFileExceptionList(
                ServiceErrors.LOADING_FROM_WEB_FAILED_CODE,
                ServiceErrors.LOADING_FROM_WEB_FAILED_TEXT,
                prop.getUrl(), e);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_WEB_FAILED, sfl);
            Trace.trace(CLASS, this, method, "Couldn't access " + prop.getUrl());
            throw sfl;
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    public final File getLocalFilePath(final ModuleAddress address) {
        final String method = "getLocalFilePath(ModuleAddress)";
        URL url;
        try {
            url = new URL(address.getUrl());
        } catch (MalformedURLException e) {
            Trace.fatal(CLASS, this, method, "Could not get local file path.", e);
            return null;
        }
        Trace.param(CLASS, this, method, "protocol", url.getProtocol());
        Trace.param(CLASS, this, method, "host", url.getHost());
        Trace.param(CLASS, this, method, "port", url.getPort());
        Trace.param(CLASS, this, method, "path", url.getPath());
        Trace.param(CLASS, this, method, "file", url.getFile());
        if (address.isFileAddress()) {
            try {
                return UrlUtility.transformURLPathToFilePath(url);
            } catch (IllegalArgumentException e) {
                // should not occur because check for validy must be done in constructor of address
                Trace.fatal(CLASS, this, method, "Loading failed of local file with URL=" + url, e);
                throw new RuntimeException(e);
            }
        }
        StringBuffer file = new StringBuffer(url.getFile());
        StringUtility.replace(file, "_", "_1"); // remember all '_'
        StringUtility.replace(file, "/", "_2"); // preserve all '/'
        String encoded = file.toString(); // fallback file name
        try {
            encoded = URLEncoder.encode(file.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not occur
            Trace.trace(CLASS, method, e);
        }
        file.setLength(0);
        file.append(encoded);
        StringUtility.replace(file, "#", "##"); // escape all '#'
        StringUtility.replace(file, "_2", "#"); // from '/' into '#'
        StringUtility.replace(file, "_1", "_"); // from '_' into '_'
        // mime 2010-06-25: use if we throw no RuntimException
        // StringBuffer adr = new StringBuffer(url.toExternalForm());
        final StringBuffer adr;
        try {
            adr = new StringBuffer(new URL(url.getProtocol(), url.getHost(), url.getPort(), file
                .toString()).toExternalForm());
        } catch (MalformedURLException e) {
            Trace.fatal(CLASS, this, method, "unexpected", e);
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
        return config.getBufferDirectory();
    }

    public File getGenerationDirectory() {
        return config.getGenerationDirectory();
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
        }
        final StringBuffer buffer = new StringBuffer();
        final Reader reader = getQedeqFileDao().getModuleReader(bo);
        try {
            IoUtility.loadReader(reader, buffer);
        } finally {
            IoUtility.close(reader);
        }
        return buffer.toString();
    }

    public boolean checkWellFormedness(final ModuleAddress address) {
        final DefaultKernelQedeqBo prop = modules.getKernelQedeqBo(this, address);
        // did we check this already?
        if (prop.wasCheckedForBeingWellFormed()) {
            return true; // everything is OK
        }
        pluginManager.executePlugin(WellFormedCheckerPlugin.class.getName(), prop);
        return prop.wasCheckedForBeingWellFormed();
    }

    public boolean checkWellFormednessOld(final ModuleAddress address) {
        final String method = "checkWellFormedness(ModuleAddress)";
        final DefaultKernelQedeqBo prop = modules.getKernelQedeqBo(this, address);
        // did we check this already?
        if (prop.wasCheckedForBeingWellFormed()) {
            return true; // everything is OK
        }
        try {
            // TODO 20110606 m31: perhaps this should be a real plugin and is managed by the PluginManager?
            // perhaps we have to make a difference between normal and hidden internal plugins?
            final WellFormedCheckerPlugin checker = new WellFormedCheckerPlugin();
            // set default plugin values for not yet set parameters
            final Parameters parameters = KernelContext.getInstance().getConfig().getPluginEntries(checker);
            checker.setDefaultValuesForEmptyPluginParameters(parameters);
            KernelContext.getInstance().getConfig().setPluginKeyValues(checker, parameters);
            checker.createExecutor(prop, parameters).executePlugin();
        } catch (final RuntimeException e) {
            final String msg = "Check of being logical well formed failed";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, address.getUrl(), e.getMessage());
            throw e;
        } finally {
            if (validate) {
                modules.validateDependencies();
            }
        }
        return prop.wasCheckedForBeingWellFormed();
    }

    public boolean checkFormallyProved(final ModuleAddress address) {
        final DefaultKernelQedeqBo prop = modules.getKernelQedeqBo(this, address);
        // did we check this already?
        if (prop.wasCheckedForBeingFormallyProved()) {
            return true; // everything is OK
        }
        pluginManager.executePlugin(FormalProofCheckerPlugin.class.getName(), prop);
        return prop.wasCheckedForBeingFormallyProved();
    }

    public boolean checkFormallyProvedOld(final ModuleAddress address) {
        final String method = "checkFormallyProved(ModuleAddress)";
        final DefaultKernelQedeqBo prop = modules.getKernelQedeqBo(this, address);
        // did we check this already?
        if (prop.wasCheckedForBeingFormallyProved()) {
            return true; // everything is OK
        }
        try {
            // FIXME 20130215 m31: perhaps this should be a real plugin and is managed by the PluginManager?
            // perhaps we have to make a difference between normal and hidden internal plugins?
            final FormalProofCheckerPlugin checker = new FormalProofCheckerPlugin();
            // set default plugin values for not yet set parameters
            final Parameters parameters = KernelContext.getInstance().getConfig().getPluginEntries(checker);
            checker.setDefaultValuesForEmptyPluginParameters(parameters);
            KernelContext.getInstance().getConfig().setPluginKeyValues(checker, parameters);
            checker.createExecutor(prop, parameters).executePlugin();
        } catch (final RuntimeException e) {
            final String msg = "Check of fully formal correct proved failed";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, address.getUrl(), e.getMessage());
            throw e;
        } finally {
            if (validate) {
                modules.validateDependencies();
            }
        }
        return prop.wasCheckedForBeingWellFormed();
    }

    /**
     * Add plugin to services.
     *
     * @param   pluginClass Plugin class to instantiate.
     * @throws  RuntimeException    Addition failed.
     */
    public void addPlugin(final String pluginClass) {
        pluginManager.addPlugin(pluginClass);
    }

    public Plugin[] getPlugins() {
        return pluginManager.getPlugins();
    }

    public Object executePlugin(final String id, final ModuleAddress address) {
        return pluginManager.executePlugin(id, getKernelQedeqBo(address));
    }

    public void clearAllPluginResults(final ModuleAddress address) {
        pluginManager.clearAllPluginResults(getKernelQedeqBo(address));
    }

    public ServiceProcess[] getServiceProcesses() {
        return processManager.getServiceProcesses();
    }

    public ServiceProcess[] getRunningServiceProcesses() {
        return processManager.getRunningServiceProcesses();
    }

    public void stopAllPluginExecutions() {
        processManager.terminateAllServiceProcesses();
    }


    /**
     * Get all loaded QEDEQ modules.
     *
     * @return All QEDEQ modules.
     */
    private KernelQedeqBoStorage getModules() {
        return modules;
    }

    public SourceFileExceptionList createSourceFileExceptionList(final int code,
            final String message, final String address, final IOException e) {
        return new SourceFileExceptionList(new SourceFileException(this,
            code, message, e, new SourceArea(address), null));
    }

    public SourceFileExceptionList createSourceFileExceptionList(final int code,
            final String message, final String address, final RuntimeException e) {
        return new SourceFileExceptionList(new SourceFileException(this,
            code, message, e, new SourceArea(address), null));
    }

    public SourceFileExceptionList createSourceFileExceptionList(final int code,
            final String message, final String address, final Exception e) {
        return new SourceFileExceptionList(new SourceFileException(this,
            code, message, e, new SourceArea(address), null));
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
        final SourceFileExceptionList sfl = bo.getErrors();
        if (sfl.size() > 0) {
            final StringBuffer buffer = new StringBuffer();
            do {
                Reader reader = null;
                try {
                    reader = getQedeqFileDao().getModuleReader(bo);
                    IoUtility.loadReader(reader, buffer);
                } catch (IOException e) {
                    IoUtility.close(reader);
                    for (int i = 0; i < sfl.size(); i++) {
                        list.add(sfl.get(i).getDescription());
                    }
                    break; // out of do while
                }
                final TextInput input = new TextInput(buffer);
                try {
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
                                input.setRow(sf.getSourceArea().getStartPosition().getRow());
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
                } finally {
                    IoUtility.close(input);
                }
                break; // out of do while
            } while (true);
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginActionName() {
        return "Basis";
    }
    public QedeqFileDao getQedeqFileDao() {
        return qedeqFileDao;
    }

    public String getPluginDescription() {
        return "provides basic services for loading QEDEQ modules";
    }

    public QedeqConfig getConfig() {
        return config;
    }

    public String getKernelVersionDirectory() {
        return kernel.getKernelVersionDirectory();
    }

    public String getBuildId() {
        return kernel.getBuildId();
    }

    public String getDedication() {
        return kernel.getDedication();
    }

    public String getDescriptiveKernelVersion() {
        return kernel.getDescriptiveKernelVersion();
    }

    public String getKernelCodeName() {
        return kernel.getKernelCodeName();
    }

    public String getKernelVersion() {
        return kernel.getKernelVersion();
    }

    public String getMaximalRuleVersion() {
        return kernel.getMaximalRuleVersion();
    }

    public boolean isRuleVersionSupported(final String ruleVersion) {
        return kernel.isRuleVersionSupported(ruleVersion);
    }

    public boolean isSetConnectionTimeOutSupported() {
        return kernel.isSetConnectionTimeOutSupported();
    }

    public boolean isSetReadTimeoutSupported() {
        return kernel.isSetReadTimeoutSupported();
    }

}
