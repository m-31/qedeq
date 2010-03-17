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

import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Represents a plugin and its services.
 *
 * @author  Michael Meyling
 */
public interface PluginBo extends Plugin {

    /**
     * Execute plugin for given QEDEQ module.
     *
     * @param   qedeq   QEDEQ module to work on.
     * @throws  SourceFileExceptionList Execution lead to errors.
     */
    public void executePlugin(KernelQedeqBo qedeq) throws SourceFileExceptionList;

    /**
     * Get plugin description.
     *
     * @return  Description of plugin.
     */
    public String getPluginDescription();

}
