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

import java.util.Map;

import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.se.common.Plugin;


/**
 * Represents a plugin and its services.
 *
 * @author  Michael Meyling
 */
public interface PluginBo extends Plugin {

    /**
     * Create execution instance for this plugin.
     *
     * @param   qedeq       QEDEQ module to work on.
     * @param   parameters  Plugin specific parameters. Might not be <code>null</code>.
     * @return  Instance to execute the plugin.
     */
    public PluginExecutor createExecutor(KernelQedeqBo qedeq, Map parameters);

    // FIXME 20110605 m31: set default plugin values for not existing values
    // this method should be called from PluginPreferences or PluginManager during
    // plugin registration otherwise we have the default definition in GUI and PluginExecutor
    // that is not very wise
    /**
     * Set default configuration parameters.
     *
     * @param   parameters  Plugin specific parameters. Might be <code>null</code>.
     */
    public void setDefaultValuesForEmptyPluginParameters(Map parameters);


}
