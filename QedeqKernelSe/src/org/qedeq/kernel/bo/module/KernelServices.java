/* $Id: ModuleFactory.java,v 1.7 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.bo.module;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * The main QEDEQ kernel methods are assembled here.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public interface KernelServices {

    /**
     * Initialisation of services. This method should be called from the {@link Kernel}
     * directly after switching into ready state. Calling this method in ready state is not
     * supported.
     *
     * TODO mime 20070411: what about an appropriate closing method?
     * FIXME mime 20080213: should not be here! Implemenatation detail!
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
     * Get a certain module.
     *
     * @param   address     Address of module.
     * @return  Wanted module.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     */
    public QedeqBo loadModule(ModuleAddress address) throws SourceFileExceptionList;

    /**
     * Get a certain module.
     *
     * @param   address  Address of module.
     * @throws  SourceFileExceptionList Required modules could not be successfully loaded.
     *                                  This can also happen if the required modules references
     *                                  form a circle.
     */
    public void loadRequiredModules(final ModuleAddress address) throws SourceFileExceptionList;

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
     * @throws  IOException    Module could not be successfully removed.
     */
    public void removeModule(final ModuleAddress address) throws IOException;

    /**
     * Get list of all currently loaded QEDEQ modules.
     *
     * @return  All currently loaded QEDEQ modules.
     */
    public ModuleAddress[] getAllLoadedModules();

     /**
     * Get buffer directory for QEDEQ module files.
     *
     * @return  buffer directory.
     */
    public File getBufferDirectory();

    /**
     * Get directory for generated files.
     *
     * @return  Generation directory.
     */
    public File getGenerationDirectory();

    /**
     * Get {@link QedeqBo} for an address.
     *
     * @param   address Look for this address.
     * @return  Existing or new {@link QedeqBo}, if address is maleformed
     *          <code>null</code> is returned.
     */
    public QedeqBo getQedeqBo(ModuleAddress address);

    /**
     * Transform an URL address into a local file path where the QEDEQ module is buffered.
     * If the module is not buffered <code>null</code> is returned.
     *
     * @param   address     Get local address for this QEDEQ module address.
     * @return  Local file path for that <code>address</code>.
     */
    public File getLocalFilePath(ModuleAddress address);

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

}
