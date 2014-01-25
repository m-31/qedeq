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

import java.io.IOException;

import org.qedeq.kernel.se.config.QedeqConfig;

/**
 * State changing methods for the kernel.
 *
 * @author  Michael Meyling
 */
public interface KernelState extends KernelServices {

    /**
     * Kernel initialisation.
     *
     * @param   config                  Configuration access.
     * @param   moduleServices          Various kernel services are supported here.
     * @param   basic                   Basic kernel properties.
     * @throws  IllegalStateException   Kernel is already initialized.
     * @throws  IOException             Initialization failed.
     */
    public void init(QedeqConfig config, Kernel moduleServices, KernelProperties basic)
        throws IOException;

    /**
     * Start the kernel.
     *
     */
    public void startup();

    /**
     * Closes the kernel.
     */
    public void shutdown();

}
