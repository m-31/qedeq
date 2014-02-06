/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.internal;

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
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.common.Kernel;
import org.qedeq.kernel.bo.common.KernelProperties;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.ServiceJob;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleArbiter;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.basis.ModuleFileNotFoundException;
import org.qedeq.kernel.bo.service.basis.ModuleLabelsCreator;
import org.qedeq.kernel.bo.service.basis.ModuleServiceExecutor;
import org.qedeq.kernel.bo.service.basis.QedeqVoBuilder;
import org.qedeq.kernel.bo.service.basis.ServiceErrors;
import org.qedeq.kernel.bo.service.dependency.LoadDirectlyRequiredModulesPlugin;
import org.qedeq.kernel.bo.service.dependency.LoadRequiredModulesPlugin;
import org.qedeq.kernel.bo.service.logic.FormalProofCheckerPlugin;
import org.qedeq.kernel.bo.service.logic.SimpleProofFinderPlugin;
import org.qedeq.kernel.bo.service.logic.WellFormedCheckerPlugin;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.base.module.Specification;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.config.QedeqConfig;
import org.qedeq.kernel.se.dto.module.QedeqVo;
import org.qedeq.kernel.se.state.LoadingState;
import org.qedeq.kernel.se.visitor.ContextChecker;
import org.qedeq.kernel.se.visitor.DefaultContextChecker;
import org.qedeq.kernel.se.visitor.InterruptException;


/**
 * This class provides a default implementation for the QEDEQ module services.
 *
 * @author  Michael Meyling
 */
public class DefaultInternalKernelServices implements Kernel, InternalKernelServices,
        Service {

    /** This class. */
    private static final Class CLASS = DefaultInternalKernelServices.class;

    /** Collection of already known QEDEQ modules. */
    private KernelQedeqBoStorage modules;

    /** Config access. */
    private final QedeqConfig config;

    /** Basic kernel properties. */
    private final KernelProperties kernel;

    /** This instance nows how to load a module from the file system. */
    private final QedeqFileDao qedeqFileDao;

    /** Synchronize module access. */
    private ModuleArbiter arbiter;

    /** This instance manages plugins. */
    private final PluginManager pluginManager;

    /** This instance manages service processes. */
    private ServiceProcessManager processManager;

    /** Validate module dependencies and status. */
    private boolean validate = true;

    /** We check the context with this checker. */
    private ContextChecker contextChecker;


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
        pluginManager = new PluginManager(this);
        loader.setServices(this);

////      pluginManager.addPlugin(MultiProofFinderPlugin.class.getName());
        pluginManager.addPlugin("org.qedeq.kernel.bo.service.unicode.Qedeq2UnicodeTextPlugin");
        pluginManager.addPlugin("org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin");
        pluginManager.addPlugin("org.qedeq.kernel.bo.service.unicode.Qedeq2Utf8Plugin");
////        pluginManager.addPlugin("org.qedeq.kernel.bo.service.heuristic.HeuristicCheckerPlugin");
        pluginManager.addPlugin("org.qedeq.kernel.bo.service.heuristic.DynamicHeuristicCheckerPlugin");
        pluginManager.addPlugin(SimpleProofFinderPlugin.class.getName());

        // add internal plugins
        pluginManager.addPlugin(LoadDirectlyRequiredModulesPlugin.class.getName());
        pluginManager.addPlugin(LoadRequiredModulesPlugin.class.getName());
        pluginManager.addPlugin(WellFormedCheckerPlugin.class.getName());
        pluginManager.addPlugin(FormalProofCheckerPlugin.class.getName());
    }

    public synchronized void startupServices() {
        modules = new KernelQedeqBoStorage();
        arbiter = new ModuleArbiterImpl();
        processManager = new ServiceProcessManager(pluginManager, arbiter);
        contextChecker = new DefaultContextChecker();
        if (config.isAutoReloadLastSessionChecked()) {
            autoReloadLastSessionChecked();
        }
    }

    public synchronized void shutdownServices() {
        processManager.terminateAndRemoveAllServiceProcesses();
        processManager = null;
        modules.removeAllModules();
        modules = null;
        arbiter = null;
        // clear thread interrupt flag because we might have interrupted ourself
        Thread.interrupted();
    }

    /**
     * If configured load all QEDEQ modules that where successfully loaded the last time.
     */
    private void autoReloadLastSessionChecked() {
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
//        getModules().removeAllModules();
        InternalServiceJob proc = null;
        InternalModuleServiceCall call = null;
        try {
            proc = processManager.createServiceProcess("remove all modules");
            getModules().lockAndRemoveAllModules(this, processManager, proc);
            proc.setSuccessState();
        } catch (final InterruptException e) {
            QedeqLog.getInstance().logMessage("Remove all modules failed: " + e.getMessage());
            if (proc != null) {
                proc.setInterruptedState();
            }
        } finally {
            processManager.endServiceCall(call);
        }
        if (validate) {
            modules.validateDependencies();
        }
        // FIXME implement as below for removeModule
    }

    public void removeModule(final ModuleAddress address) {
        final KernelQedeqBo prop = getKernelQedeqBo(address);
        if (prop != null) {
            QedeqLog.getInstance().logRequest("Removing module", address.getUrl());
            InternalServiceJob proc = null;
            InternalModuleServiceCall call = null;
            try {
                proc = processManager.createServiceProcess("remove module");
                call = processManager.createServiceCall(this, prop, Parameters.EMPTY,
                    Parameters.EMPTY, proc);
                removeModule((DefaultKernelQedeqBo) prop);
                call.finish();
                proc.setSuccessState();
            } catch (final InterruptException e) {
                QedeqLog.getInstance().logFailureReply(
                    "Remove failed", address.getUrl(), e.getMessage());
                if (proc != null) {
                    proc.setInterruptedState();
                }
            } finally {
                processManager.endServiceCall(call);
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
        synchronized (prop) {
            // FIXME mime 20080319: one could call prop.setLoadingProgressState(
            // LoadingState.STATE_DELETED) alone but that would
            // miss to inform the KernelQedeqBoPool. How do we inform the pool?
            // must the StateManager have a reference to it?
            prop.delete();
            getModules().removeModule(prop);
            return;
        }
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

    public KernelQedeqBo loadKernelModule(final InternalServiceJob process, final ModuleAddress address)
            throws InterruptException {
        final String method = "loadModule(InternalServiceProcess, ModuleAddress)";
        final DefaultKernelQedeqBo prop = getModules().getKernelQedeqBo(this, address);
        if (prop.isLoaded()) {
            return prop;
        }
        final ModuleServiceExecutor executor = new ModuleServiceExecutor() {
            public void executeService(final InternalModuleServiceCall call) throws InterruptException {
                try {
                    synchronized (prop) {
                        if (prop.isLoaded()) {
                            call.finish();
                            return;
                        }
                        QedeqLog.getInstance().logRequest("Load module", address.getUrl());
                        if (prop.getModuleAddress().isFileAddress()) {
                            call.setAction("file loading");
                            loadLocalModule(call.getInternalServiceProcess(), prop);
                        } else {
                            // search in local file buffer
                            try {
                                getCanonicalReadableFile(prop);
                            } catch (ModuleFileNotFoundException e) { // file not found
                                // we will continue by creating a local copy
                                call.setAction("web loading");
                                saveQedeqFromWebToBuffer(call, prop);
                                call.setExecutionPercentage(50);
                            }
                            call.setAction("buffer loading");
                            loadBufferedModule(call.getInternalServiceProcess(), prop);
                        }
                        QedeqLog.getInstance().logSuccessfulReply(
                            "Successfully loaded", address.getUrl());
                        call.finish();
                    }
                } catch (SourceFileExceptionList e) {
                    Trace.trace(CLASS, this, method, e);
                    QedeqLog.getInstance().logFailureState("Loading of module failed.", address.getUrl(),
                        e.toString());
                    call.finish("Loading of module failed.");
                } catch (final RuntimeException e) {
                    Trace.fatal(CLASS, this, method, "unexpected problem", e);
                    QedeqLog.getInstance().logFailureReply("Loading failed", address.getUrl(), e.getMessage());
                    call.finish("Loading of module failed: " + e.getMessage());
                }
            }

        };
        this.processManager.executeService(new ModuleService() {
            public String getServiceAction() {
                return "load QEDEQ module";
            }

            public String getServiceDescription() {
                return "take QEDEQ module address and try to load and parse the content";
            }

            public String getServiceId() {
                return "" + hashCode();
            }
        }, executor, prop, process);
        return prop;
//        try {
//            synchronized (prop) {
//                if (prop.isLoaded()) {
//                    return prop;
//                }
//                QedeqLog.getInstance().logRequest("Load module", address.getUrl());
//                if (prop.getModuleAddress().isFileAddress()) {
//                    loadLocalModule(proc, prop);
//                } else {
//                    // search in local file buffer
//                    try {
//                        getCanonicalReadableFile(prop);
//                    } catch (ModuleFileNotFoundException e) { // file not found
//                        // we will continue by creating a local copy
//                        saveQedeqFromWebToBuffer(prop);
//                    }
//                    loadBufferedModule(proc, prop);
//                }
//                QedeqLog.getInstance().logSuccessfulReply(
//                    "Successfully loaded", address.getUrl());
//            }
//        } catch (SourceFileExceptionList e) {
//            Trace.trace(CLASS, this, method, e);
//            QedeqLog.getInstance().logFailureState("Loading of module failed!", address.getUrl(),
//                e.toString());
//        } catch (final RuntimeException e) {
//            Trace.fatal(CLASS, this, method, "unexpected problem", e);
//            QedeqLog.getInstance().logFailureReply("Loading failed", address.getUrl(), e.getMessage());
//        } finally {
//            call.setSuccessState();
//        }
//        return prop;
    }


    public QedeqBo loadModule(final ModuleAddress address) {
        final InternalServiceJob proc = processManager.createServiceProcess("LoadModule");
        final QedeqBo result = getQedeqBo(address);
        try {
            loadKernelModule(proc, address);
            proc.setSuccessState();
        } catch (InterruptException e) {
            proc.setInterruptedState();
        }
        return result;
    }

    /**
     * Load buffered QEDEQ module file.
     *
     * @param   process Service process we run within.
     * @param   prop    Load this.
     * @throws  SourceFileExceptionList Loading or QEDEQ module failed.
     */
    private void loadBufferedModule(final InternalServiceJob process,
            final DefaultKernelQedeqBo prop) throws SourceFileExceptionList {
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_BUFFER);
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
            qedeq = getQedeqFileDao().loadQedeq(process, prop, localFile);
        } catch (SourceFileExceptionList sfl) {
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_BUFFER_FAILED, sfl);
            throw sfl;
        }
        setCopiedQedeq(process, prop, qedeq);
    }

    /**
     * Load QEDEQ module file with file loader.
     *
     * @param   process Service process we run within.
     * @param   prop    Load this.
     * @throws  SourceFileExceptionList Loading or copying QEDEQ module failed.
     */
    private void loadLocalModule(final InternalServiceJob process,
            final DefaultKernelQedeqBo prop) throws SourceFileExceptionList {
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_LOCAL_FILE);
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
            qedeq = getQedeqFileDao().loadQedeq(process, prop, localFile);
        } catch (SourceFileExceptionList sfl) {
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_FROM_LOCAL_FILE_FAILED, sfl);
            throw sfl;
        }
        setCopiedQedeq(process, prop, qedeq);
    }

    private void setCopiedQedeq(final InternalServiceJob process, final DefaultKernelQedeqBo prop,
            final Qedeq qedeq) throws SourceFileExceptionList {
        final String method = "setCopiedQedeq(DefaultKernelQedeqBo, Qedeq)";
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_INTO_MEMORY);
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
            final ModuleLabels labels = new ModuleLabels();
            final Element2LatexImpl converter = new Element2LatexImpl(labels);
            final Element2Utf8 textConverter = new Element2Utf8Impl(converter);
            moduleNodesCreator.createLabels(process, labels);
            prop.setLoaded(vo, labels, converter, textConverter);
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
     * @param   process    Our service process we run within.
     * @param   parent  Parent module address.
     * @param   spec    Specification for another QEDEQ module.
     * @return  Loaded module.
     * @throws  SourceFileExceptionList Loading failed.
     * @throws  InterruptException User canceled request.
     */
    public KernelQedeqBo loadModule(final InternalServiceJob process, final ModuleAddress parent,
            final Specification spec) throws SourceFileExceptionList, InterruptException {

        final String method = "loadModule(Module, Specification)";
        Trace.begin(CLASS, this, method);
        Trace.trace(CLASS, this, method, spec);
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
                if (prop.isLoaded()) {
                    return (prop);
                }
                synchronized (prop) {
                    if (prop.isLoaded()) {
                        return (prop);
                    }
                    try {
                        if (prop.getModuleAddress().isFileAddress()) {
                            loadLocalModule(process, prop);
                        } else {
                            // search in local file buffer
                            try {
                                getCanonicalReadableFile(prop);
                            } catch (ModuleFileNotFoundException e) { // file not found
                                // we will continue by creating a local copy
                                saveQedeqFromWebToBuffer((InternalModuleServiceCall) process.getModuleServiceCall(),
                                    prop);
                            }
                            loadBufferedModule(process, prop);
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
            QedeqLog.getInstance().logFailureReply("Loading failed",
                (prop != null ? prop.getUrl() : "unknownURL"), e.getMessage());
            throw e;
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    public ModuleAddress[] getAllLoadedModules() {
        return getModules().getAllLoadedModules();
    }

    /**
     * Load all previously checked QEDEQ modules.
     *
     * @return Successfully reloaded all modules.
     */
    public boolean loadPreviouslySuccessfullyLoadedModules() {
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
    }

    // LATER mime 20070326: dynamic loading from web page directory
    public boolean loadAllModulesFromQedeq() {
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
            } catch (final IOException e) {
                Trace.fatal(CLASS, this, "loadPreviouslySuccessfullyLoadedModules",
                    "internal error: " + "saved URLs are malformed", e);
                errors = true;
            }
        }
        return !errors;
    }

    /**
     * Make local copy of a module if it is no file address.
     *
     * @param   call    Service call we run within.
     * @param   prop    Module properties.
     * @throws  SourceFileExceptionList Address was malformed or the file can not be found.
     * @throws  InterruptException User canceled request.
     */
    private void saveQedeqFromWebToBuffer(final InternalModuleServiceCall call, final DefaultKernelQedeqBo prop)
            throws SourceFileExceptionList,  InterruptException {
        final String method = "saveQedeqFromWebToBuffer(DefaultKernelQedeqBo)";
        Trace.begin(CLASS, this, method);

        if (prop.getModuleAddress().isFileAddress()) { // this is already a local file
            Trace.fatal(CLASS, this, method, "tried to make a local copy for a local module", null);
            Trace.end(CLASS, this, method);
            return;
        }

        final ModuleServiceExecutor executor = new ModuleServiceExecutor() {

            public void executeService(final InternalModuleServiceCall call) {
                final File f = getLocalFilePath(prop.getModuleAddress());
                prop.setLoadingProgressState(LoadingState.STATE_LOADING_FROM_WEB);
                try {
                    UrlUtility.saveUrlToFile(prop.getUrl(), f,
                        config.getHttpProxyHost(), config.getHttpProxyPort(), config.getHttpNonProxyHosts(),
                        config.getConnectionTimeout(), config.getReadTimeout(), new LoadingListener() {
                        public void loadingCompletenessChanged(final double completeness) {
                            final double percentage = completeness * 100;
                            call.setExecutionPercentage(percentage);
                            prop.setLoadingCompleteness((int) percentage);
                        }
                    });
                    call.finish();
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
                    call.finish("Couldn't save URL " + prop.getUrl() + " to file: " + e.getMessage());
                }
            }
        };

        this.processManager.executeService(new ModuleService() {
            public String getServiceAction() {
                return "saving from web to file buffer";
            }

            public String getServiceDescription() {
                return "download QEDEQ module from web URL and save it to a local file";
            }

            public String getServiceId() {
                return "" + hashCode();
            }
        }, executor, prop, call.getInternalServiceProcess());
        Trace.end(CLASS, this, method);

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
        return getKernelQedeqBo(address);
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

    public boolean loadRequiredModules(final ModuleAddress address) {
        final KernelQedeqBo prop = getKernelQedeqBo(address);
        // did we check this already?
        if (prop.hasLoadedRequiredModules()) {
            return true; // everything is OK
        }
        try {
            loadModule(address);
            executePlugin(null, LoadRequiredModulesPlugin.class.getName(), prop, null);
        } catch (InterruptException e) {    // TODO 20130521 m31: ok?
            // ignore;
        }
        if (validate) {
            modules.validateDependencies();
        }
        return prop.hasLoadedRequiredModules();
    }

    public boolean loadRequiredModules(final InternalServiceJob process, final KernelQedeqBo qedeq)
            throws InterruptException {
        // did we check this already?
        if (qedeq.hasLoadedRequiredModules()) {
            return true; // everything is OK
        }
        executePlugin(process, LoadRequiredModulesPlugin.class.getName(), qedeq, null);
        return qedeq.hasLoadedRequiredModules();
    }

    public boolean checkWellFormedness(final ModuleAddress address) {
        final DefaultKernelQedeqBo prop = modules.getKernelQedeqBo(this, address);
        // did we check this already?
        if (prop.isWellFormed()) {
            return true; // everything is OK
        }
        try {
            loadModule(address);
            executePlugin(null, WellFormedCheckerPlugin.class.getName(), prop, null);
        } catch (InterruptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (validate) {
            modules.validateDependencies();
        }
        return prop.isWellFormed();
    }

    public boolean checkWellFormedness(final InternalServiceJob process, final KernelQedeqBo qedeq) {
        // did we check this already?
        if (qedeq.isWellFormed()) {
            return true; // everything is OK
        }
        try {
            executePlugin(process, WellFormedCheckerPlugin.class.getName(), qedeq,
                null);
        } catch (InterruptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return qedeq.isWellFormed();
    }

    public boolean checkFormallyProved(final ModuleAddress address) {
        final DefaultKernelQedeqBo prop = modules.getKernelQedeqBo(this, address);
        // did we check this already?
        if (prop.isFullyFormallyProved()) {
            return true; // everything is OK
        }
        try {
            loadModule(address);
            executePlugin(null, FormalProofCheckerPlugin.class.getName(), prop, null);
        } catch (InterruptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (validate) {
            modules.validateDependencies();
        }
        return prop.isFullyFormallyProved();
    }

    public boolean checkFormallyProved(final InternalServiceJob process, final KernelQedeqBo qedeq) {
        // did we check this already?
        if (qedeq.isFullyFormallyProved()) {
            return true; // everything is OK
        }
        try {
            executePlugin(process, FormalProofCheckerPlugin.class.getName(), qedeq, null);
        } catch (InterruptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return qedeq.isFullyFormallyProved();
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

    public ModuleService[] getPlugins() {
        return pluginManager.getNonInternalPlugins();
    }

    public Object executePlugin(final String id, final ModuleAddress address, final Object data) {
        try {
            loadModule(address);
            return processManager.executePlugin(id, getKernelQedeqBo(address), data, null);
        } catch (InterruptException e) {
            return null;
        }
    }

    public Object executePlugin(final InternalServiceJob process, final String id, final KernelQedeqBo qedeq,
            final Object data) throws InterruptException {
        loadModule(qedeq.getModuleAddress());
        return processManager.executePlugin(id, qedeq, data, process);
    }

    public void clearAllPluginResults(final ModuleAddress address) {
        pluginManager.clearAllPluginResults(getKernelQedeqBo(address));
    }

    public ServiceJob[] getServiceProcesses() {
        return processManager.getServiceProcesses();
    }

    public ServiceJob[] getRunningServiceProcesses() {
        return processManager.getRunningServiceProcesses();
    }

    public void terminateAllServiceProcesses() {
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

    public String getServiceId() {
        return CLASS.getName();
    }

    public String getServiceAction() {
        return "Basis";
    }
    public QedeqFileDao getQedeqFileDao() {
        return qedeqFileDao;
    }

    public String getServiceDescription() {
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

    public ContextChecker getContextChecker() {
        return contextChecker;
    }

    /**
     * Set the context checker. This is useful especially for test classes.
     *
     * @param   contextChecker  We check the context with this checker now.
     */
    public void setContextChecker(final ContextChecker contextChecker) {
        this.contextChecker = contextChecker;
    }

}
