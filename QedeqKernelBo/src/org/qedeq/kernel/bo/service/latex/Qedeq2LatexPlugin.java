/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.latex;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginExecutor;


/**
 * Plugin to transfer a QEDEQ module into a LaTeX file.
 *
 * @author  Michael Meyling
 */
public final class Qedeq2LatexPlugin implements PluginBo {

    /** This class. */
    public static final Class CLASS = Qedeq2LatexPlugin.class;

    /**
     * Constructor.
     */
    public Qedeq2LatexPlugin() {
        // nothing to do
    }

    public String getServiceId() {
        return CLASS.getName();
    }

    public String getServiceAction() {
        return "Create LaTeX";
    }

    public String getServiceDescription() {
        return "transforms QEDEQ module into LaTeX file";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Parameters parameters) {
        return new Qedeq2LatexExecutor(this, qedeq, parameters);
    }

    public void setDefaultValuesForEmptyPluginParameters(final Parameters parameters) {
        parameters.setDefault("info", true);
        parameters.setDefault("brief", false);
    }

}
