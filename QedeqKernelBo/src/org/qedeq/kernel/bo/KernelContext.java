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

package org.qedeq.kernel.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileLock;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.BasicKernel;
import org.qedeq.kernel.bo.common.KernelProperties;
import org.qedeq.kernel.bo.common.KernelServices;
import org.qedeq.kernel.bo.common.KernelState;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.Kernel;
import org.qedeq.kernel.bo.common.ServiceJob;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.config.QedeqConfig;
import org.qedeq.kernel.se.visitor.InterruptException;


/**
 * This class provides static access methods for the kernel.
 *
 * @author  Michael Meyling
 */
public final class KernelContext implements KernelProperties, KernelServices {

    /** Message for non started kernel. */
    private static final String KERNEL_NOT_STARTED = "Kernel not started";

    /** Message for non initialized kernel. */
    private static final String KERNEL_NOT_INITIALIZED = "Kernel not initialized";

    /** This class. */
    private static final Class CLASS = KernelContext.class;

    /** One and only instance of this class. */
    private static final KernelContext INSTANCE = new KernelContext();

    /** Lock file. */
    private File lockFile;

    /** Lock file stream. */
    private FileOutputStream lockStream;

    /** Initial kernel state. */
    private final KernelState initialState = new KernelState() {

        public void init(final QedeqConfig config, final Kernel moduleServices, final KernelProperties basic)
                throws IOException {
            if (config == null) {
                throw new NullPointerException("QedeqConfig is null");
            }
            if (moduleServices == null) {
                throw new NullPointerException("ServiceModule is null");
            }
            if (basic == null) {
                throw new NullPointerException("KernelProperties is null");
            }
            KernelContext.this.config = config;
            KernelContext.this.basic = basic;
            Trace.setTraceOn(config.isTraceOn());
            checkJavaVersion();
            createAllNecessaryDirectories();
            checkIfApplicationIsAlreadyRunningAndLockFile();
            KernelContext.this.services = moduleServices;
            QedeqLog.getInstance().logMessage("--------------------------------------------------"
                 + "---------------------------------------");
            QedeqLog.getInstance().logMessage("This is "
                + KernelContext.getInstance().getDescriptiveKernelVersion());
            QedeqLog.getInstance().logMessage("  see \"http://www.qedeq.org\" for more "
                + "information");
            QedeqLog.getInstance().logMessage("  supports rules till version "
                + KernelContext.getInstance().getMaximalRuleVersion());
            QedeqLog.getInstance().logMessage("  Java version: "
                + StringUtility.alignRight(System.getProperty("java.version", "unknown"), 10));
            QedeqLog.getInstance().logMessage("  used memory:  "
                + StringUtility.alignRight(Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory(), 10));
            QedeqLog.getInstance().logMessage("  free memory:  "
                + StringUtility.alignRight(Runtime.getRuntime().freeMemory(), 10));
            QedeqLog.getInstance().logMessage("  total memory: "
                + StringUtility.alignRight(Runtime.getRuntime().totalMemory(), 10));
            QedeqLog.getInstance().logMessage("  max. memory:  "
                + StringUtility.alignRight(Runtime.getRuntime().maxMemory(), 10));
            QedeqLog.getInstance().logMessage("  processors/cores: "
                + StringUtility.alignRight(Runtime.getRuntime().availableProcessors(), 6));
            currentState = initializedState;
        }

        public void startup() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public void shutdown() {
            currentState = initialState;
            // close stream and associated channel
            IoUtility.close(lockStream);
            lockStream = null;
            config = null;
            services = null;
        }

        public boolean removeAllModules() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public void removeModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public boolean clearLocalBuffer() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public QedeqBo loadModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public boolean loadAllModulesFromQedeq() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public boolean loadRequiredModules(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public ModuleAddress[] getAllLoadedModules() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public QedeqBo getQedeqBo(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public ModuleAddress getModuleAddress(final URL url) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public ModuleAddress getModuleAddress(final String url) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public ModuleAddress getModuleAddress(final File file) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public String getSource(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public boolean checkWellFormedness(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public boolean checkFormallyProved(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public ModuleService[] getPlugins() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public Object executePlugin(final String pluginName, final ModuleAddress address,
                final Object data) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public void clearAllPluginResults(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public ServiceJob[] getServiceProcesses() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public ServiceJob[] getRunningServiceProcesses() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public void terminateAllServiceProcesses() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

    };

    /** Initial kernel state. */
    private final KernelState initializedState = new KernelState() {

        public void init(final QedeqConfig config, final Kernel moduleServices,
                final KernelProperties basic) throws IOException {
            throw new IllegalStateException("Kernel is already initialized");
        }

        public void startup() {
            services.startupServices();
            currentState = readyState;
            QedeqLog.getInstance().logMessage("QEDEQ kernel opened.");
        }

        public void shutdown() {
            services.shutdownServices();
            KernelContext.this.services = null;
            initialState.shutdown();
            QedeqLog.getInstance().logMessage("QEDEQ Kernel closed.");
        }

        public boolean removeAllModules() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public void removeModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public boolean clearLocalBuffer() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public QedeqBo loadModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public boolean loadAllModulesFromQedeq() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public boolean loadRequiredModules(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public ModuleAddress[] getAllLoadedModules() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public QedeqBo getQedeqBo(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public ModuleAddress getModuleAddress(final URL url) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public ModuleAddress getModuleAddress(final String url) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public ModuleAddress getModuleAddress(final File file) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public String getSource(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public boolean checkWellFormedness(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public boolean checkFormallyProved(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public ModuleService[] getPlugins() {
            return services.getPlugins();
        }

        public Object executePlugin(final String pluginName, final ModuleAddress address,
                final Object data) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public void clearAllPluginResults(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public ServiceJob[] getServiceProcesses() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public ServiceJob[] getRunningServiceProcesses() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public void terminateAllServiceProcesses() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

    };

    /** State for ready kernel. */
    private final KernelState readyState = new KernelState() {

        public void init(final QedeqConfig config, final Kernel moduleServices,
                final KernelProperties basic) throws IOException {
            // we are already ready
        }

        public void startup() {
            // we are already ready
        }

        public void shutdown() {
            try {
                final ModuleAddress[] addresses = services.getAllLoadedModules();
                if (addresses != null) {
                    final String[] buffer = new String[addresses.length];
                    for (int i = 0; i < addresses.length; i++) {
                        buffer[i] = addresses[i].toString();
                    }
                    getConfig().setPreviouslyLoadedModules(buffer);
                    getConfig().store();
                    QedeqLog.getInstance().logMessage("Current config file successfully saved.");
                }
            } catch (IOException e) {
                Trace.trace(CLASS, this, "shutdown()", e);
                QedeqLog.getInstance().logMessage("Saving current config file failed.");
            }
            initializedState.shutdown();
        }

        public boolean removeAllModules() {
            return services.removeAllModules();
        }

        public void removeModule(final ModuleAddress address) {
            services.removeModule(address);
        }

        public boolean clearLocalBuffer() {
            return services.clearLocalBuffer();
        }

        public QedeqBo loadModule(final ModuleAddress address) {
            return services.loadModule(address);
        }

        public boolean loadAllModulesFromQedeq() {
            return services.loadAllModulesFromQedeq();
        }

        public boolean loadRequiredModules(final ModuleAddress address) {
            return services.loadRequiredModules(address);
        }

        public ModuleAddress[] getAllLoadedModules() {
            return services.getAllLoadedModules();
        }

        public QedeqBo getQedeqBo(final ModuleAddress address) {
            return services.getQedeqBo(address);
        }

        public ModuleAddress getModuleAddress(final URL url) throws IOException {
            return services.getModuleAddress(url);
        }

        public ModuleAddress getModuleAddress(final String url) throws IOException {
            return services.getModuleAddress(url);
        }

        public ModuleAddress getModuleAddress(final File file) throws IOException {
            return services.getModuleAddress(file);
        }

        public String getSource(final ModuleAddress address) throws IOException {
            return services.getSource(address);
        }

        public boolean checkWellFormedness(final ModuleAddress address) {
            return services.checkWellFormedness(address);
        }

        public boolean checkFormallyProved(final ModuleAddress address) {
            return services.checkFormallyProved(address);
        }

        public ModuleService[] getPlugins() {
            return services.getPlugins();
        }

        public Object executePlugin(final String pluginName, final ModuleAddress address,
                final Object data) throws InterruptException {
            return services.executePlugin(pluginName, address, data);
        }

        public void clearAllPluginResults(final ModuleAddress address) {
            services.clearAllPluginResults(address);
        }

        public ServiceJob[] getServiceProcesses() {
            return services.getServiceProcesses();
        }

        public ServiceJob[] getRunningServiceProcesses() {
            return services.getRunningServiceProcesses();
        }

        public void terminateAllServiceProcesses() {
            services.terminateAllServiceProcesses();
        }

    };

    /** Kernel configuration. */
    private QedeqConfig config;

    /** Initial kernel state. */
    private KernelState currentState = initialState;

    /** For basic kernel informations. */
    private KernelProperties basic;

    /** This object can service QEDEQ modules. */
    private Kernel services;

    /**
     * Constructor.
     */
    private KernelContext() {
        basic = new BasicKernel();
    }

    /**
     * Get instance of kernel context.
     *
     * @return  Singleton, which is responsible for the kernel access.
     */
    public static final KernelContext getInstance() {
        return INSTANCE;
    }

    public String getBuildId() {
        return basic.getBuildId();
    }

    public final String getKernelVersion() {
        return basic.getKernelVersion();
    }

    public final String getKernelCodeName() {
        return basic.getKernelCodeName();
    }

    public final String getKernelVersionDirectory() {
        return basic.getKernelVersionDirectory();
    }

    public final String getDescriptiveKernelVersion() {
        return basic.getDescriptiveKernelVersion();
    }

    public final String getDedication() {
        return basic.getDedication();
    }

    public final String getMaximalRuleVersion() {
        return basic.getMaximalRuleVersion();
    }

    public final boolean isRuleVersionSupported(final String ruleVersion) {
        return basic.isRuleVersionSupported(ruleVersion);
    }

    public boolean isSetConnectionTimeOutSupported() {
        return basic.isSetConnectionTimeOutSupported();
    }

    public boolean isSetReadTimeoutSupported() {
        return basic.isSetReadTimeoutSupported();
    }

    public QedeqConfig getConfig() {
        return config;
    }

    /**
     * Init the kernel.
     *
     * @param   config                  Configuration access. Must not be <code>null</code>.
     * @param   moduleServices          Services for the kernel. Must not be <code>null</code>.
     * @throws  IllegalStateException   Kernel is already initialized.
     * @throws  IOException             Initialization failure.
     */
    public void init(final QedeqConfig config, final Kernel moduleServices) throws IOException {
        currentState.init(config, moduleServices, basic);
    }

    /**
     * Startup the kernel.
     */
    public void startup() {
        currentState.startup();
    }

    /**
     * Shutdown the kernel.
     */
    public void shutdown() {
        currentState.shutdown();
    }

    public boolean removeAllModules() {
        return currentState.removeAllModules();
    }

    public void removeModule(final ModuleAddress address) {
        currentState.removeModule(address);
    }

    public boolean clearLocalBuffer() {
        return currentState.clearLocalBuffer();
    }

    public QedeqBo loadModule(final ModuleAddress address) {
        return currentState.loadModule(address);
    }

    public boolean loadAllModulesFromQedeq() {
        return currentState.loadAllModulesFromQedeq();
    }

    public boolean loadRequiredModules(final ModuleAddress address) {
        return currentState.loadRequiredModules(address);
    }

    public ModuleAddress[] getAllLoadedModules() {
        return currentState.getAllLoadedModules();
    }

    public QedeqBo getQedeqBo(final ModuleAddress address) {
        return currentState.getQedeqBo(address);
    }

    public ModuleAddress getModuleAddress(final URL url) throws IOException {
        return currentState.getModuleAddress(url);
    }

    public ModuleAddress getModuleAddress(final String url) throws IOException {
        return currentState.getModuleAddress(url);
    }

    public ModuleAddress getModuleAddress(final File file) throws IOException {
        return currentState.getModuleAddress(file);
    }

    public String getSource(final ModuleAddress address) throws IOException {
        return currentState.getSource(address);
    }

    public boolean checkWellFormedness(final ModuleAddress address) {
        return currentState.checkWellFormedness(address);
    }

    public boolean checkFormallyProved(final ModuleAddress address) {
        return currentState.checkFormallyProved(address);
    }

    public ModuleService[] getPlugins() {
        return currentState.getPlugins();
    }

    public Object executePlugin(final String pluginName, final ModuleAddress address,
            final Object data) throws InterruptException {
        return currentState.executePlugin(pluginName, address, data);
    }

    public void clearAllPluginResults(final ModuleAddress address) {
        currentState.clearAllPluginResults(address);
    }

    public ServiceJob[] getServiceProcesses() {
        return currentState.getServiceProcesses();
    }

    public ServiceJob[] getRunningServiceProcesses() {
        return currentState.getRunningServiceProcesses();
    }

    public void terminateAllServiceProcesses() {
        currentState.terminateAllServiceProcesses();
    }

    /**
     * Check java version. We want to be sure that the kernel is run at least with java 1.4.2
     *
     * @throws  IOException     Application is running below java 1.4.2.
     */
    private void checkJavaVersion() throws IOException {
        final String method = "checkJavaVersion";
        Trace.info(CLASS, this, method, "running on java version "
            + System.getProperty("java.version"));
        final int[] versions = IoUtility.getJavaVersion();
        if (versions == null) {
            Trace.fatal(CLASS, this, method, "running java version unknown", null);
            // we try to continue
            return;
        }
        final StringBuffer version = new StringBuffer();
        for (int i = 0; i < versions.length; i++) {
            if (i > 0) {
                version.append(".");
            }
            version.append(versions[i]);
        }
        Trace.paramInfo(CLASS, this, method, "version", version);
        // >= 1
        if (versions.length < 1 || versions[0] < 1) {
            throw new IOException("This application requires at least Java 1.4.2 but we got "
                + version);
        }
        if (versions[0] == 1) {         // further checking
            // >= 1.4
            if (versions.length < 2 || versions[1] < 4) {
                throw new IOException("This application requires at least Java 1.4.2 but we got "
                    + version);
            }
            if (versions[1] == 4) {     // further checking
                // >=1.4.2
                if (versions.length < 3 || versions[2] < 2) {
                    throw new IOException(
                        "This application requires at least Java 1.4.2 but we got "
                        + version);
                }
            }
        }
    }

    /**
     * Create all necessary directories for the kernel.
     *
     * @throws  IOException     Creation was not possible.
     */
    void createAllNecessaryDirectories() throws IOException {
        // log directory
        final File logFile = getConfig().getLogFile();
        final File logDir = logFile.getParentFile();
        if (!logDir.exists() &&  !logDir.mkdirs()) {
            throw new IOException("can't create directory: " + logDir.getAbsolutePath());
        }
        // buffer directory
        final File bufferDir = getConfig().getBufferDirectory();
        if (!bufferDir.exists() &&  !bufferDir.mkdirs()) {
            throw new IOException("can't create directory: " + bufferDir.getAbsolutePath());
        }
        // generation directory
        final File generationDir = getConfig().getGenerationDirectory();
        if (!generationDir.exists() &&  !generationDir.mkdirs()) {
            throw new IOException("can't create directory: " + generationDir.getAbsolutePath());
        }
    }

    /**
     * Checks if the application is already running. To check that we create a file in the
     * buffer directory, open a stream and write something into it. The stream is not closed
     * until kernel shutdown.
     *
     * @throws  IOException     Application is already running.
     */
    private void checkIfApplicationIsAlreadyRunningAndLockFile()
            throws IOException {
        lockFile = new File(getConfig().getBufferDirectory(), "qedeq_lock.lck");
        FileLock fl = null;
        try {
            lockStream = new FileOutputStream(lockFile);
            lockStream.write("LOCKED".getBytes("UTF8"));
            lockStream.flush();
            fl = lockStream.getChannel().tryLock();
        } catch (IOException e) {
            throw new IOException("It seems the application is already running.\n"
                + "At least accessing the file \"" + lockFile.getAbsolutePath() + "\" failed.");
        }
        if (fl == null) {
            throw new IOException("It seems the application is already running.\n"
                + "At least locking the file \"" + lockFile.getAbsolutePath() + "\" failed.");
        }
    }

}
