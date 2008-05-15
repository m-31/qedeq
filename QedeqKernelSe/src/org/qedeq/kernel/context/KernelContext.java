/* $Id: KernelContext.java,v 1.18 2008/03/27 05:16:29 m31 Exp $
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

package org.qedeq.kernel.context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.qedeq.kernel.bo.control.InternalKernelServices;
import org.qedeq.kernel.bo.control.KernelState;
import org.qedeq.kernel.bo.module.KernelProperties;
import org.qedeq.kernel.bo.module.KernelServices;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.config.QedeqConfig;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;


/**
 * This class provides static access methods for basic informations.
 *
 * @version $Revision: 1.18 $
 * @author  Michael Meyling
 */
public final class KernelContext implements KernelProperties, KernelState, KernelServices {

    /** This class. */
    private static final Class CLASS = KernelContext.class;

    /** Version of this kernel. */
    private static final String KERNEL_VERSION = "0.03.10";

    /** Version dependent directory of this kernel. */
    private static final String KERNEL_VERSION_DIRECTORY = KERNEL_VERSION.replace('.', '_');

    /** Descriptive version information of this kernel. */
    private static final String DESCRIPTIVE_KERNEL_VERSION
        = "Hilbert II - Version " + KERNEL_VERSION + " (mongaga)";

    /** Maximal supported rule version of this kernel. */
    private static final String MAXIMAL_RULE_VERSION = "1.00.00";

    /** One and only instance of this class. */
    private static final KernelContext INSTANCE = new KernelContext();

    /** Lock file. */
    private static File lockFile;

    /** Lock file stream. */
    private static FileOutputStream lockStream;

    /** Initial kernel state. */
    private final KernelState initialState = new KernelState() {

        public void init(final InternalKernelServices moduleFactory, final QedeqConfig qedeqConfig)
                throws IOException {
            checkJavaVersion();
            checkIfApplicationIsAlreadyRunning(qedeqConfig);
            config = qedeqConfig;
            KernelContext.this.services = moduleFactory;
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
            IoUtility.close(lockStream);
            if (lockFile != null) {
                lockFile.delete();
            }
        }

        public void startup() {
            throw new IllegalStateException("Kernel not initialized");
        }

        public void removeAllModules() {
            throw new IllegalStateException("Kernel not initialized");
        }

        public void removeModule(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public void clearLocalBuffer() throws IOException {
            throw new IllegalStateException("Kernel not initialized");
        }

        public QedeqBo loadModule(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public boolean loadAllModulesFromQedeq() {
            throw new IllegalStateException("Kernel not initialized");
        }

        public void loadRequiredModules(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public ModuleAddress[] getAllLoadedModules() {
            throw new IllegalStateException("Kernel not initialized");
        }

        public QedeqBo getQedeqBo(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public ModuleAddress getModuleAddress(final URL url) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public ModuleAddress getModuleAddress(final String url) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public ModuleAddress getModuleAddress(final File file) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public String getSource(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public boolean checkModule(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not initialized");
        }

        public String[] getSourceFileExceptionList(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not initialized");
        }
    };

    /** Initial kernel state. */
    private final KernelState initializedState = new KernelState() {

        public void init(final InternalKernelServices moduleFactory, final QedeqConfig qedeqConfig)
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
            throw new IllegalStateException("Kernel not started");
        }

        public void removeModule(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not started");
        }

        public void clearLocalBuffer() throws IOException {
            throw new IllegalStateException("Kernel not started");
        }

        public QedeqBo loadModule(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not started");
        }

        public boolean loadAllModulesFromQedeq() {
            throw new IllegalStateException("Kernel not started");
        }

        public void loadRequiredModules(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not started");
        }

        public ModuleAddress[] getAllLoadedModules() {
            throw new IllegalStateException("Kernel not started");
        }

        public QedeqBo getQedeqBo(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not started");
        }

        public ModuleAddress getModuleAddress(final URL url) {
            throw new IllegalStateException("Kernel not started");
        }

        public ModuleAddress getModuleAddress(final String url) {
            throw new IllegalStateException("Kernel not started");
        }

        public ModuleAddress getModuleAddress(final File file) {
            throw new IllegalStateException("Kernel not started");
        }

        public String getSource(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not started");
        }

        public boolean checkModule(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not started");
        }

        public String[] getSourceFileExceptionList(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not started");
        }

    };

    /** State for ready kernel. */
    private final KernelState readyState = new KernelState() {

        public void init(final InternalKernelServices moduleFactory, final QedeqConfig qedeqConfig)
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

        public String[] getSourceFileExceptionList(final ModuleAddress address) {
            return services.getSourceFileExceptionList(address);
        }
    };

    /** Initial kernel state. */
    private KernelState currentState = initialState;

    /** For config access. */
    private QedeqConfig config;

    /** This object can service QEDEQ modules. */
    private InternalKernelServices services;

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

    public void init(final InternalKernelServices moduleFactory, final QedeqConfig qedeqConfig)
            throws IOException {
        currentState.init(moduleFactory, qedeqConfig);
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

    public String[] getSourceFileExceptionList(final ModuleAddress address) {
        return currentState.getSourceFileExceptionList(address);
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
     * Checks if the application is already running. To check that we create a file in the
     * buffer directory, open a stream and write something into it. The stream is not closed
     * until kernel shutdown.
     *
     * @param   qedeqConfig     Configuration.
     * @throws  IOException     Application is already running.
     */
    private void checkIfApplicationIsAlreadyRunning(final QedeqConfig qedeqConfig)
            throws IOException {
        lockFile = new File(qedeqConfig.getBufferDirectory(), "qedeq_lock.lck");
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
