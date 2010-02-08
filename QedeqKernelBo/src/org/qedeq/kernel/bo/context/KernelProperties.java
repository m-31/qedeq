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

import org.qedeq.kernel.config.QedeqConfig;


/**
 * QEDEQ kernel.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public interface KernelProperties {

    /**
     * Get relative version directory of this kernel.
     *
     * @return  Version sub directory.
     */
    public String getKernelVersionDirectory();

    /**
     * Get access to configuration parameters.
     *
     * @return  Configuration access.
     */
    public QedeqConfig getConfig();

}
