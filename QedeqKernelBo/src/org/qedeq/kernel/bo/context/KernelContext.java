/* $Id: KernelContext.java,v 1.1 2008/07/26 07:58:30 m31 Exp $
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

package org.qedeq.kernel.bo.context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.config.QedeqConfig;


/**
 * This class provides static access methods for basic informations.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class KernelContext implements KernelProperties, KernelState, KernelServices {

    /** Message for non started kernel. */
    private static final String KERNEL_NOT_STARTED = "Kernel not started";

    /** Message for non initialized kernel. */
    private static final String KERNEL_NOT_INITIALIZED = "Kernel not initialized";

    /** This class. */
    private static final Class CLASS = KernelContext.class;

    /** Version of this kernel. */
    private static final String KERNEL_VERSION = "0.03.11";

    /** Version dependent directory of this kernel. */
    private static final String KERNEL_VERSION_DIRECTORY = KERNEL_VERSION.replace('.', '_');

    /** Descriptive version information of this kernel. */
    private static final String DESCRIPTIVE_KERNEL_VERSION
        = "Hilbert II - Version " + KERNEL_VERSION + " (mongaga) ["
        + getBuildIdFromManifest() + "]";

    /** Maximal supported rule version of this kernel. */
    private static final String MAXIMAL_RULE_VERSION = "1.00.00";

    /** One and only instance of this class. */
    private static final KernelContext INSTANCE = new KernelContext();

    /** Lock file. */
    private File lockFile;

    /** Lock file stream. */
    private FileOutputStream lockStream;

    /** Initial kernel state. */
    private final KernelState initialState = new KernelState() {

        public void init(final KernelServices moduleServices, final QedeqConfig qedeqConfig)
                throws IOException {
            config = qedeqConfig;
            checkJavaVersion();
            createAllNecessaryDirectories();
            checkIfApplicationIsAlreadyRunning();
            KernelContext.this.services = moduleServices;
            QedeqLog.getInstance().logMessage("This is "
                + KernelContext.getInstance().getDescriptiveKernelVersion());
            QedeqLog.getInstance().logMessage("  see \"http://www.qedeq.org\" for more "
                + "information");
            QedeqLog.getInstance().logMessage("  supports rules till version "
                + KernelContext.getInstance().getMaximalRuleVersion());
            currentState = initializedState;
        }

        public boolean isReady() {
            return false;
        }

        public void shutdown() {
            currentState = initialState;
            if (lockStream == null) {
                return;
            }
            IoUtility.close(lockStream);
            if (lockFile != null) {
                lockFile.delete();
            }
        }

        public void startup() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public void removeAllModules() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public void removeModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public void clearLocalBuffer() throws IOException {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public QedeqBo loadModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public boolean loadAllModulesFromQedeq() {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public void loadRequiredModules(final ModuleAddress address) {
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

        public boolean checkModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public InputStream createLatex(final ModuleAddress prop, final String language,
                final String level)
                throws DefaultSourceFileExceptionList, IOException {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

        public String generateLatex(final ModuleAddress prop, final String language,
                final String level)
                throws DefaultSourceFileExceptionList, IOException {
            throw new IllegalStateException(KERNEL_NOT_INITIALIZED);
        }

    };

    /** Initial kernel state. */
    private final KernelState initializedState = new KernelState() {

        public void init(final KernelServices moduleServices, final QedeqConfig qedeqConfig)
                throws IOException {
            throw new IllegalStateException("Kernel is already initialized");
        }

        public boolean isReady() {
            return false;
        }

        public void shutdown() {
            QedeqLog.getInstance().logMessage("QEDEQ Kernel closed.");
            KernelContext.this.services = null;
            initialState.shutdown();
        }

        public void startup() {
            services.startup();
            currentState = readyState;
            QedeqLog.getInstance().logMessage("QEDEQ kernel opened.");
        }

        public void removeAllModules() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public void removeModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public void clearLocalBuffer() throws IOException {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public QedeqBo loadModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public boolean loadAllModulesFromQedeq() {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public void loadRequiredModules(final ModuleAddress address) {
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

        public boolean checkModule(final ModuleAddress address) {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public InputStream createLatex(final ModuleAddress prop, final String language,
                final String level)
                throws DefaultSourceFileExceptionList, IOException {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

        public String generateLatex(final ModuleAddress prop, final String language,
                final String level)
                throws DefaultSourceFileExceptionList, IOException {
            throw new IllegalStateException(KERNEL_NOT_STARTED);
        }

    };

    /** State for ready kernel. */
    private final KernelState readyState = new KernelState() {

        public void init(final KernelServices moduleServices, final QedeqConfig qedeqConfig)
                throws IOException {
            // we are already ready
        }

        public boolean isReady() {
            return false;
        }

        public void shutdown() {
            try {
                final ModuleAddress[] addresses = services.getAllLoadedModules();
                final String[] buffer = new String[addresses.length];
                for (int i = 0; i < addresses.length; i++) {
                    buffer[i] = addresses[i].toString();
                }
                config.setLoadedModules(buffer);
                config.store();
                QedeqLog.getInstance().logMessage("Current config file successfully saved.");
            } catch (IOException e) {
                Trace.trace(CLASS, this, "shutdown()", e);
                QedeqLog.getInstance().logMessage("Saving current config file failed.");
            }
            initializedState.shutdown();
        }

        public void startup() {
            throw new IllegalStateException("Kernel is already initialized");
        }

        public void removeAllModules() {
            services.removeAllModules();
        }

        public void removeModule(final ModuleAddress address) {
            services.removeModule(address);
        }

        public void clearLocalBuffer() throws IOException {
            services.clearLocalBuffer();
        }

        public QedeqBo loadModule(final ModuleAddress address) {
            return services.loadModule(address);
        }

        public boolean loadAllModulesFromQedeq() {
            return services.loadAllModulesFromQedeq();
        }

        public void loadRequiredModules(final ModuleAddress address)
                throws SourceFileExceptionList {
            services.loadRequiredModules(address);
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

        public boolean checkModule(final ModuleAddress address) {
            return services.checkModule(address);
        }

        public InputStream createLatex(final ModuleAddress address, final String language,
                final String level)
                throws DefaultSourceFileExceptionList, IOException {
            return services.createLatex(address, language, level);
        }

        public String generateLatex(final ModuleAddress address, final String language,
                final String level)
                throws DefaultSourceFileExceptionList, IOException {
            return services.generateLatex(address, language, level);
        }

    };

    /** Initial kernel state. */
    private KernelState currentState = initialState;

    /** For config access. */
    private QedeqConfig config;

    /** This object can service QEDEQ modules. */
    private KernelServices services;

    /**
     * Constructor.
     */
    private KernelContext() {
        // nothing to do
    }

    /**
     * Get instance of kernel context.
     *
     * @return  Singleton, which is responsible for the kernel access.
     */
    public static final KernelContext getInstance() {
        return INSTANCE;
    }

    /**
     * Get build information from JAR manifest file. Is also non empty string if no manifest
     * information is available.
     *
     * @return  Implementation-Version.
     */
    public static String getBuildIdFromManifest() {
        String build = KernelContext.class.getPackage().getImplementationVersion();
        if (build == null) {
            build = "no regular build";
        }
        return build;
    }

    /**
     * Get version of this kernel.
     *
     * @return  Kernel version.
     */
    public final String getKernelVersion() {
        return KERNEL_VERSION;
    }

    /**
     * Get relative version directory of this kernel.
     *
     * @return  Version sub directory.
     */
    public final String getKernelVersionDirectory() {
        return KERNEL_VERSION_DIRECTORY;
    }

    /**
     * Get descriptive version information of this kernel.
     *
     * @return  Version Information.
     */
    public final String getDescriptiveKernelVersion() {
        return DESCRIPTIVE_KERNEL_VERSION;
    }

    /**
     * Get maximal supported rule version of this kernel.
     *
     * @return  Maximal supported rule version.
     */
    public final String getMaximalRuleVersion() {
        return MAXIMAL_RULE_VERSION;
    }

    /**
     * Is a given rule version supported?
     *
     * @param   ruleVersion Check this one.
     * @return  Is the given rule version supported?
     */
    public final boolean isRuleVersionSupported(final String ruleVersion) {
        return MAXIMAL_RULE_VERSION.equals(ruleVersion);
    }

    public QedeqConfig getConfig() {
        return config;
    }

    public void init(final KernelServices moduleServices, final QedeqConfig qedeqConfig)
            throws IOException {
        currentState.init(moduleServices, qedeqConfig);
    }

    public boolean isReady() {
        return currentState.isReady();
    }

    public void shutdown() {
        currentState.shutdown();
    }

    public void startup() {
        currentState.startup();
    }

    public void removeAllModules() {
        currentState.removeAllModules();
    }

    public void removeModule(final ModuleAddress address) {
        currentState.removeModule(address);
    }

    public void clearLocalBuffer() throws IOException {
        currentState.clearLocalBuffer();
    }

    public QedeqBo loadModule(final ModuleAddress address) {
        return currentState.loadModule(address);
    }

    public boolean loadAllModulesFromQedeq() {
        return currentState.loadAllModulesFromQedeq();
    }

    public void loadRequiredModules(final ModuleAddress address) throws SourceFileExceptionList {
        currentState.loadRequiredModules(address);
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

    public boolean checkModule(final ModuleAddress address) {
        return currentState.checkModule(address);
    }

    public InputStream createLatex(final ModuleAddress address, final String language,
            final String level) throws DefaultSourceFileExceptionList,
            IOException {
        return currentState.createLatex(address, language, level);
    }

    public String generateLatex(final ModuleAddress address, final String language,
            final String level) throws DefaultSourceFileExceptionList,
            IOException {
        return currentState.generateLatex(address, language, level);
    }
    /**
     * Check java version. We want to be shure that the kernel is run at least with java 1.4.2
     *
     * @throws  IOException     Application is running below java 1.4.2.
     */
    private void checkJavaVersion() throws IOException {
        final int[] versions = IoUtility.getJavaVersion();
        if (versions == null) {
            Trace.fatal(CLASS, this, "checkJavaVersion", "running java version unknown", null);
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
        Trace.paramInfo(CLASS, this, "checkJavaVersion", "version", version);
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
        final File logFile = new File(config.getBasisDirectory(), config.getLogFile());
        final File logDir = logFile.getParentFile();
        if (!logDir.exists() &&  !logDir.mkdirs()) {
            throw new IOException("can't create directory: " + logDir.getAbsolutePath());
        }
        // buffer directory
        final File bufferDir = config.getBufferDirectory();
        if (!bufferDir.exists() &&  !bufferDir.mkdirs()) {
            throw new IOException("can't create directory: " + bufferDir.getAbsolutePath());
        }
        // generation directory
        final File generationDir = config.getGenerationDirectory();
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
    private void checkIfApplicationIsAlreadyRunning()
            throws IOException {
        lockFile = new File(config.getBufferDirectory(), "qedeq_lock.lck");
        final String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            if ((lockFile.exists() && !lockFile.delete())) {
                throw new IOException("It seems the application is already running.\n"
                    + "At least the file \"" + lockFile.getAbsolutePath()
                    + "\" couldn't be deleted.");
            }
        } else {
            if ((lockFile.exists())) {
                throw new IOException("It seems the application is already running or crashed."
                    + "\nAt least the file \"" + lockFile.getAbsolutePath()
                    + "\" must be manually deleted!");
            }
        }
        try {
            lockStream = new FileOutputStream(lockFile);
            lockStream.write("LOCKED".getBytes());
            lockStream.flush();
        } catch (IOException e) {
            throw new IOException("It seems the application is already running.\n"
                + "At least locking the file \"" + lockFile.getAbsolutePath() + "\" failed.");
        }
    }

}
