/* $Id: KernelContext.java,v 1.16 2007/12/21 23:33:48 m31 Exp $
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

package org.qedeq.kernel.context;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.bo.module.Kernel;
import org.qedeq.kernel.bo.module.KernelState;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleFactory;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.config.QedeqConfig;
import org.qedeq.kernel.log.QedeqLog;
import org.qedeq.kernel.trace.Trace;


/**
 * This class provides static access methods for basic informations.
 *
 * @version $Revision: 1.16 $
 * @author  Michael Meyling
 */
public final class KernelContext implements Kernel {

    /** This class. */
    private static final Class CLASS = KernelContext.class;

    /** Version of this kernel. */
    private static final String KERNEL_VERSION = "0.03.08";

    /** Version dependent directory of this kernel. */
    private static final String KERNEL_VERSION_DIRECTORY = KERNEL_VERSION.replace('.', '_');

    /** Descriptive version information of this kernel. */
    private static final String DESCRIPTIVE_KERNEL_VERSION
        = "Hilbert II - Version " + KERNEL_VERSION + " (mongaga)";

    /** Maximal supported rule version of this kernel. */
    private static final String MAXIMAL_RULE_VERSION = "1.00.00";

    /** One and only instance of this class. */
    private static final KernelContext INSTANCE = new KernelContext();

    /** Initial kernel state. */
    private final KernelState initialState = new KernelState() {

        public void init(final ModuleFactory moduleFactory, final QedeqConfig qedeqConfig)
                throws IOException {
            QedeqLog.getInstance().logMessage("This is "
                + KernelContext.getInstance().getDescriptiveKernelVersion());
            QedeqLog.getInstance().logMessage("  see \"http://www.qedeq.org\" for more "
                + "information");
            QedeqLog.getInstance().logMessage("  supports rules till version "
                + KernelContext.getInstance().getMaximalRuleVersion());
            config = qedeqConfig;
            KernelContext.this.moduleFactory = moduleFactory;
            currentState = initializedState;
        }

        public boolean isReady() {
            return false;
        }

        public QedeqConfig getConfig() {
            throw new IllegalStateException("Kernel not initialized");
        }

        public void shutdown() {
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

        public ModuleProperties loadModule(final ModuleAddress address)
                throws SourceFileExceptionList {
            throw new IllegalStateException("Kernel not initialized");
        }

        public ModuleProperties loadModule(final QedeqBo module, final Specification spec)
                throws SourceFileExceptionList {
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

        public File getBufferDirectory() {
            throw new IllegalStateException("Kernel not initialized");
        }

        public File getGenerationDirectory() {
            throw new IllegalStateException("Kernel not initialized");
        }

        public ModuleProperties getModuleProperties(final ModuleAddress address) {
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

        public File getLocalFilePath(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not initialized");
        }
    };

    /** Initial kernel state. */
    private final KernelState initializedState = new KernelState() {

        public void init(final ModuleFactory moduleFactory, final QedeqConfig qedeqConfig)
                throws IOException {
            throw new IllegalStateException("Kernel is already initialized");
        }

        public boolean isReady() {
            return false;
        }

        public QedeqConfig getConfig() {
            return config;
        }

        public void shutdown() {
            currentState = initialState;
            KernelContext.this.moduleFactory = null;
            QedeqLog.getInstance().logMessage("QEDEQ Kernel closed.");
        }

        public void startup() {
            moduleFactory.startup();
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

        public ModuleProperties loadModule(final ModuleAddress address)
                throws SourceFileExceptionList {
            throw new IllegalStateException("Kernel not started");
        }

        public ModuleProperties loadModule(final QedeqBo module, final Specification spec)
                throws SourceFileExceptionList {
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

        public File getBufferDirectory() {
            throw new IllegalStateException("Kernel not started");
        }

        public File getGenerationDirectory() {
            throw new IllegalStateException("Kernel not started");
        }

        public ModuleProperties getModuleProperties(final ModuleAddress address) {
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

        public File getLocalFilePath(final ModuleAddress address) {
            throw new IllegalStateException("Kernel not started");
        }
    };

    /** State for ready kernel. */
    private final KernelState readyState = new KernelState() {

        public void init(final ModuleFactory moduleFactory, final QedeqConfig qedeqConfig)
                throws IOException {
            // we are already ready
        }

        public boolean isReady() {
            return false;
        }

        public QedeqConfig getConfig() {
            return config;
        }

        public void shutdown() {
            try {
                config.setLoadedModules(moduleFactory.getAllLoadedModules());
                config.store();
                QedeqLog.getInstance().logMessage("Current config file successfully saved.");
            } catch (IOException e) {
                Trace.trace(CLASS, this, "shutdown()", e);
                QedeqLog.getInstance().logMessage("Saving current config file failed.");
            }
            currentState = initialState;
            KernelContext.this.moduleFactory = null;
            QedeqLog.getInstance().logMessage("QEDEQ Kernel closed.");
        }

        public void startup() {
            throw new IllegalStateException("Kernel is already initialized");
        }

        public void removeAllModules() {
            moduleFactory.removeAllModules();
        }

        public void removeModule(final ModuleAddress address) throws IOException {
            moduleFactory.removeModule(address);
        }

        public void clearLocalBuffer() throws IOException {
            moduleFactory.clearLocalBuffer();
        }

        public ModuleProperties loadModule(final ModuleAddress address)
                throws SourceFileExceptionList {
            return moduleFactory.loadModule(address);
        }

        public ModuleProperties loadModule(final QedeqBo module, final Specification spec)
                throws SourceFileExceptionList {
            return moduleFactory.loadModule(module, spec);
        }

        public boolean loadAllModulesFromQedeq() {
            return moduleFactory.loadAllModulesFromQedeq();
        }

        public void loadRequiredModules(final ModuleAddress address)
                throws SourceFileExceptionList {
            moduleFactory.loadRequiredModules(address);
        }

        public ModuleAddress[] getAllLoadedModules() {
            return moduleFactory.getAllLoadedModules();
        }

        public File getBufferDirectory() {
            return moduleFactory.getBufferDirectory();
        }

        public File getGenerationDirectory() {
            return moduleFactory.getGenerationDirectory();
        }

        public ModuleProperties getModuleProperties(final ModuleAddress address) {
            return moduleFactory.getModuleProperties(address);
        }

        public ModuleAddress getModuleAddress(final URL url) throws IOException {
            return moduleFactory.getModuleAddress(url);
        }

        public ModuleAddress getModuleAddress(final String url) throws IOException {
            return moduleFactory.getModuleAddress(url);
        }

        public ModuleAddress getModuleAddress(final File file) throws IOException {
            return moduleFactory.getModuleAddress(file);
        }

        public File getLocalFilePath(final ModuleAddress address) {
            return moduleFactory.getLocalFilePath(address);
        }

    };

    /** Initial kernel state. */
    private KernelState currentState = initialState;

    /** For config access. */
    private QedeqConfig config;

    /** This factory can produce QEDEQ modules. */
    private ModuleFactory moduleFactory;

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

    public void init(final ModuleFactory moduleFactory, final QedeqConfig qedeqConfig)
            throws IOException {
        currentState.init(moduleFactory, qedeqConfig);
    }

    public boolean isReady() {
        return currentState.isReady();
    }

    public QedeqConfig getConfig() {
        return currentState.getConfig();
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

    public void removeModule(final ModuleAddress address) throws IOException {
        currentState.removeModule(address);
    }

    public void clearLocalBuffer() throws IOException {
        currentState.clearLocalBuffer();
    }

    public ModuleProperties loadModule(final ModuleAddress address) throws SourceFileExceptionList {
        return currentState.loadModule(address);
    }

    public ModuleProperties loadModule(final QedeqBo module, final Specification spec)
            throws SourceFileExceptionList {
        return currentState.loadModule(module, spec);
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

    public File getBufferDirectory() {
        return currentState.getBufferDirectory();
    }

    public File getGenerationDirectory() {
        return currentState.getGenerationDirectory();
    }

    public ModuleProperties getModuleProperties(final ModuleAddress address) {
        return currentState.getModuleProperties(address);
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

    public File getLocalFilePath(final ModuleAddress address) {
        return currentState.getLocalFilePath(address);
    }

}
