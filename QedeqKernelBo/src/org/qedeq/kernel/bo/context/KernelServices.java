/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * The main QEDEQ kernel methods are assembled here.
 *
 * @author  Michael Meyling
 */
public interface KernelServices {

    /**
     * Initialization of services. This method should be called from the kernel
     * directly after switching into ready state. Calling this method in ready state is not
     * supported.
     *
     * TODO m31 20070411: what about an appropriate closing method?
     * TODO m31 20080213: should not be here! Implementation detail!
     */
    public void startup();

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
     * FIXME m31 20100317: add SourceFileExceptionList to signature and implementation
     * @param   address     Address of module.
     * @return  Wanted module.
     */
    public QedeqBo loadModule(ModuleAddress address);

    /**
     * Get a certain module.
     *
     * @param   address  Address of module.
     * @throws  SourceFileExceptionList Required modules could not be successfully loaded.
     *                                  This can also happen if the required modules references
     *                                  form a circle.
     */
    public void loadRequiredModules(ModuleAddress address) throws SourceFileExceptionList;

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
     * FIXME m31 20100317: find a solution for server-client connection and plugin execution
     * Creates a LaTeX representation of given QEDEQ module.
     *
     * @param   address QEDEQ module address
     * @param   language Filter text to get and produce text in this language only.
     * @param   level Filter for this detail level. LATER mime 20050205: not supported yet.
     * @return  LaTeX data.
     * @throws  DefaultSourceFileExceptionList Major problem occurred.
     * @throws  IOException
     */
    public InputStream createLatex(final ModuleAddress address, String language, String level)
            throws SourceFileExceptionList, IOException;

    /**
     * Execute plugin on given QEDEQ module.
     *
     * @param   name    Plugin name
     * @param   address QEDEQ module address.
     * @throws  SourceFileExceptionList Major problem occurred.
     */
    public void executePlugin(final String name, final ModuleAddress address) throws SourceFileExceptionList;

}
