/* $Id: InternalKernelServices.java,v 1.1 2008/03/27 05:16:24 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import java.io.File;
import java.io.IOException;

import org.qedeq.kernel.bo.module.KernelServices;
import org.qedeq.kernel.common.ModuleAddress;

/**
 * The kernel internal service methods are assembled here.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public interface InternalKernelServices extends KernelServices {

    /**
     * Initialisation of services. This method should be called from the kernel
     * directly after switching into ready state. Calling this method in ready state is not
     * supported.
     *
     * TODO mime 20070411: what about an appropriate closing method?
     * TODO mime 20080213: should not be here! Implementation detail!
     */
    public void startup();

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
     * Get {@link KernelQedeqBo} for an address.
     *
     * @param   address Look for this address.
     * @return  Existing or new {@link KernelQedeqBo}, if address is malformed
     *          <code>null</code> is returned.
     */
    public KernelQedeqBo getKernelQedeqBo(ModuleAddress address);

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
     * @param   file    Local QEDEQ module.
     * @return  Module address.
     * @throws  IOException     URL has not the correct format for referencing a QEDEQ module.
     */
    public ModuleAddress getModuleAddress(File file) throws  IOException;


}
