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

import java.util.Map;

import org.qedeq.kernel.common.Plugin;


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
     * @param   parameters  Plugin specific parameters. Might be <code>null</code>.
     * @return  Plugin specific resulting object. Might be <code>null</code>.
     */
    public Object executePlugin(KernelQedeqBo qedeq, Map parameters);

}
