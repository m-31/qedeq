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

package org.qedeq.kernel.bo.service.utf8;

import java.util.Map;

import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginExecutor;


/**
 * Plugin to transfer a QEDEQ module into a LaTeX file.
 *
 * @author  Michael Meyling
 */
public final class Qedeq2Utf8TextPlugin implements PluginBo {

    /** This class. */
    public static final Class CLASS = Qedeq2Utf8TextPlugin.class;

    /**
     * Constructor.
     */
    public Qedeq2Utf8TextPlugin() {
    }

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginName() {
        return "Create UTF-8";
    }

    public String getPluginDescription() {
        return "transforms QEDEQ module into UTF-8 file";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Map parameters) {
        return new Qedeq2Utf8Executor(this, qedeq, parameters);
    }

}
