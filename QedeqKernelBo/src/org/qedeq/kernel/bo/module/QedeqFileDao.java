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

package org.qedeq.kernel.bo.module;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.qedeq.base.io.SourceArea;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * An instance of this interface can load and save QEDEQ module files.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public interface QedeqFileDao {

    /**
     * Set kernel services. Is called by the kernel to give this loader the opportunity to
     * use kernel services within its methods. This is the first method the kernel calls.
     *
     * @param   services    Internal kernel services.
     */
    public void setServices(InternalKernelServices services);

    /**
     * Get kernel services.
     *
     * @return  Internal kernel services.
     */
    public InternalKernelServices getServices();

    /**
     * Load a QEDEQ module from file.
     *
     * @param   prop        Module properties.
     * @param   localFile   Load XML file from tbis location.
     * @return  Loaded QEDEQ.
     * @throws  SourceFileExceptionList     Module could not be successfully loaded.
     */
    public Qedeq loadQedeq(QedeqBo prop, final File localFile)
            throws SourceFileExceptionList;

    /**
     * Save a QEDEQ module as file.
     *
     * @param   prop        Module properties.
     * @param   localFile   Save module in this file.
     * @throws  SourceFileExceptionList     Module could not be successfully saved.
     * @throws  IOException                 File saving failed.
     */
    public void saveQedeq(KernelQedeqBo prop, final File localFile)
            throws SourceFileExceptionList,  IOException;

    /**
     * Get area in source file for QEDEQ module context.
     *
     * @param   qedeq       Look at this QEDEQ module.
     * @param   context     Search for this context.
     * @return  Created file area. Maybe <code>null</code>.
     */
    public SourceArea createSourceArea(Qedeq qedeq, ModuleContext context);

    /**
     * Get reader for local buffered QEDEQ module.
     *
     * @param   prop    QEDEQ BO.
     * @return  Reader.
     * @throws  IOException     Reading failed.
     */
    public Reader getModuleReader(KernelQedeqBo prop) throws IOException;

}
