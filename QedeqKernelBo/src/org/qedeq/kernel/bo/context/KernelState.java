/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import java.io.IOException;

import org.qedeq.kernel.config.QedeqConfig;

/**
 * State changing methods for the kernel.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public interface KernelState extends KernelServices {

    /**
     * Kernel initialisation.
     *
     * @param   moduleServices   Factory for loading QEDEQ modules.
     * @param   qedeqConfig     Configuration to work with.
     * @throws  IOException     Initialisation failed.
     */
    public void init(KernelServices moduleServices, QedeqConfig qedeqConfig)
        throws IOException;

    /**
     * Start all kernel activity now.
     */
    public void startup();

    /**
     * Is the kernel ready to perform its main tasks.
     *
     * @return  Is the kernel ready?
     */
    public boolean isReady();

    /**
     * Closes the kernel.
     */
    public void shutdown();

}
