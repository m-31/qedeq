/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.Plugin;

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
     * @throws  IOException Deletion of all buffered file was not successful.
     */
    public void clearLocalBuffer() throws IOException;

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
     * Checks if all formulas of a QEDEQ module and its required modules are well formed.
     *
     * @param   address Module to check.
     * @return  Was check successful?
     */
    public boolean checkModule(ModuleAddress address);

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
     * @param   parameters  Plugin specific parameters. Might be <code>null</code>.
     * @return  Plugin specific resulting object. Might be <code>null</code>.
     */
    public Object executePlugin(final String id, final ModuleAddress address, final Map parameters);

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

}
