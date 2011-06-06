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


/**
 * Represents a plugin and its services.
 *
 * @author  Michael Meyling
 */
public abstract class PluginBoImpl implements PluginBo {

    /**
     * Set default configuration parameter if the key has still no value.
     *
     * @param   parameters  Plugin filtered parameter list.
     * @param   key         Key we want to check.
     * @param   value       Default value.
     */
    public void setDefault(final Map parameters, final String key, final int value) {
        if (!parameters.containsKey(key)) {
            parameters.put(key, "" + value);
        }
    }

    /**
     * Set default configuration parameter if the key has still no value.
     *
     * @param   parameters  Plugin filtered parameter list.
     * @param   key         Key we want to check.
     * @param   value       Default value.
     */
    public void setDefault(final Map parameters, final String key, final String value) {
        if (!parameters.containsKey(key)) {
            parameters.put(key, value);
        }
    }

    /**
     * Set default configuration parameter if the key has still no value.
     *
     * @param   parameters  Plugin filtered parameter list.
     * @param   key         Key we want to check.
     * @param   value       Default value.
     */
    public void setDefault(final Map parameters, final String key, final boolean value) {
        if (!parameters.containsKey(key)) {
            parameters.put(key, "" + value);
        }
    }

}
