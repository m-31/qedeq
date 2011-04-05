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

package org.qedeq.kernel.bo.service.unicode;

import java.util.Map;

import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;


/**
 * Plugin to transfer a QEDEQ module into a UTF-8 text file.
 *
 * @author  Michael Meyling
 */
public final class Qedeq2Utf8Plugin implements PluginBo {

    /** This class. */
    public static final Class CLASS = Qedeq2Utf8Plugin.class;

    /**
     * Constructor.
     */
    public Qedeq2Utf8Plugin() {
    }

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginActionName() {
        return "Create UTF-8";
    }

    public String getPluginDescription() {
        return "transforms QEDEQ module into UTF-8 file";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Map parameters) {
        return new Qedeq2Utf8Executor(this, qedeq, parameters);
    }

}
