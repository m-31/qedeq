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

package org.qedeq.kernel.bo.module;

import java.io.File;
import java.io.IOException;

import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.bo.context.KernelServices;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * The kernel internal service methods are assembled here. Needed by the kernel and its helpers.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public interface InternalKernelServices extends KernelServices {

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
     * Load specified QEDEQ module from QEDEQ parent module.
     *
     * @param   parent  Parent module address.
     * @param   spec    Specification for another QEDEQ module.
     * @return  Loaded module.
     * @throws  SourceFileExceptionList     Loading failed.
     */
    public KernelQedeqBo loadModule(ModuleAddress parent, Specification spec)
            throws SourceFileExceptionList;

    /**
     * Get DAO for reading and writing QEDEQ modules from or to a file.
     *
     * @return  DAO.
     */
    public QedeqFileDao getQedeqFileDao();

    public SourceFileExceptionList createSourceFileExceptionList(final IOException e);

    public SourceFileExceptionList createSourceFileExceptionList(final RuntimeException e);

    public SourceFileExceptionList createSourceFileExceptionList(final Exception e);

}
