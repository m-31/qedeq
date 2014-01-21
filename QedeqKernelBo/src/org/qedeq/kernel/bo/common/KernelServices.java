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

package org.qedeq.kernel.bo.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * The main QEDEQ kernel methods are assembled here.
 *
 * @author  Michael Meyling
 */
public interface KernelServices {

    /**
     * Remove all modules from memory.
     */
    public void removeAllModules();

    /**
     * Clear local buffer and all loaded QEDEQ modules.
     *
     * @return  Was deletion successful?
     */
    public boolean clearLocalBuffer();

    /**
     * Get a certain module. You can check the status to know if the loading was successful.
     *
     * @param   address     Address of module.
     * @return  Wanted module.
     */
    public QedeqBo loadModule(ModuleAddress address);

    /**
     * Get required modules of given module. You can check the status to know if the loading was
     * successful.
     *
     * @param   address  Address of module.
     * @return  Successful loading.
     */
    public boolean loadRequiredModules(ModuleAddress address);

    /**
     * Load all QEDEQ modules from project web directory for current kernel.
     *
     * @return  Successful loading.
     */
    public boolean loadAllModulesFromQedeq();

    /**
     * Remove a certain module.
     *
     * @param   address     Address of module.
     */
    public void removeModule(ModuleAddress address);

    /**
     * Get list of all currently loaded QEDEQ modules.
     *
     * @return  All currently loaded QEDEQ modules.
     */
    public ModuleAddress[] getAllLoadedModules();

    /**
     * Get {@link QedeqBo} for an address.
     *
     * @param   address Look for this address.
     * @return  Existing or new {@link QedeqBo}.
     */
    public QedeqBo getQedeqBo(ModuleAddress address);

    /**
     * Get source of an QEDEQ module.
     * If the module was not yet not buffered <code>null</code> is returned.
     *
     * @param   address     Address for QEDEQ module address.
     * @return  Contents of locally buffered QEDEQ module.
     * @throws  IOException Loading failed.
     */
    public String getSource(ModuleAddress address) throws  IOException;

    /**
     * Get module address from URL.
     *
     * @param   url     URL for QEDEQ module.
     * @return  Module address.
     * @throws  IOException     URL has not the correct format for referencing a QEDEQ module.
     */
    public ModuleAddress getModuleAddress(URL url) throws  IOException;

    /**
     * Get module address from URL.
     *
     * @param   url     URL for QEDEQ module.
     * @return  Module address.
     * @throws  IOException     URL has not the correct format for referencing a QEDEQ module.
     */
    public ModuleAddress getModuleAddress(String url) throws  IOException;

    /**
     * Get module address from URL.
     *
     * @param   file    Local QEDEQ module.
     * @return  Module address.
     * @throws  IOException     URL has not the correct format for referencing a QEDEQ module.
     */
    public ModuleAddress getModuleAddress(File file) throws  IOException;

    /**
     * Check if all formulas of a QEDEQ module and its required modules are well formed.
     *
     * @param   address Module to check.
     * @return  Was check successful?
     */
    public boolean checkWellFormedness(ModuleAddress address);

    /**
     * Check if all propositions of this and all required modules have correct formal proofs.
     *
     * @param   address Module to check.
     * @return  Was check successful?
     */
    public boolean checkFormallyProved(ModuleAddress address);

    /**
     * Get all installed plugins.
     *
     * @return  Installed plugins.
     */
    public Plugin[] getPlugins();

    /**
     * Execute plugin on given QEDEQ module.
     *
     * @param   id          Plugin id.
     * @param   address     QEDEQ module address.
     * @param   data        Process data. Additional data beside module.
     * @throws  InterruptException    User canceled further processing.
     * @return  Plugin specific resulting object. Might be <code>null</code>.
     */
    public Object executePlugin(final String id, final ModuleAddress address, final Object data)
        throws InterruptException;

    /**
     * Clear all plugin warnings and errors for given module.
     *
     * @param   address     QEDEQ module address.
     */
    public void clearAllPluginResults(final ModuleAddress address);

    /**
     * Get information about all service processes.
     *
     * @return  Result.
     */
    public ServiceProcess[] getServiceProcesses();

    /**
     * Get all running service processes. But remember a running process might currently
     * be blocked.
     *
     * @return  All service running processes.
     */
    public ServiceProcess[] getRunningServiceProcesses();

    /**
     * Stop all currently running plugin executions.
     */
    public void stopAllPluginExecutions();

}
