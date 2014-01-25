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

package org.qedeq.kernel.bo.service.common;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.common.ModuleService;


/**
 * Represents a plugin and its services.
 *
 * @author  Michael Meyling
 */
public interface ModuleServicePlugin extends ModuleService {

    /**
     * Create execution instance for this plugin.
     *
     * @param   qedeq       QEDEQ module to work on.
     * @param   parameters  Plugin specific parameters. Might not be <code>null</code>.
     * @return  Instance to execute the plugin.
     */
    public ModuleServicePluginExecutor createExecutor(KernelQedeqBo qedeq, Parameters parameters);

    /**
     * Set default configuration parameters.
     *
     * @param   parameters  Plugin specific parameters. Non existing key value pairs will
     *          be replaced by default values.
     */
    public void setDefaultValuesForEmptyPluginParameters(Parameters parameters);


}
