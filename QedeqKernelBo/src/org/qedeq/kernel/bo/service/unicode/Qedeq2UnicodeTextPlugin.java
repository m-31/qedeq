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
public final class Qedeq2UnicodeTextPlugin implements PluginBo {

    /** This class. */
    public static final Class CLASS = Qedeq2UnicodeTextPlugin.class;

    /**
     * Constructor.
     */
    public Qedeq2UnicodeTextPlugin() {
    }

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginName() {
        return "Show module as UTF-8 text";
    }

    public String getPluginDescription() {
        return "shows QEDEQ module within window as UTF-8 text";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Map parameters) {
        return new Qedeq2UnicodeTextExecutor(this, qedeq, parameters);
    }

}
