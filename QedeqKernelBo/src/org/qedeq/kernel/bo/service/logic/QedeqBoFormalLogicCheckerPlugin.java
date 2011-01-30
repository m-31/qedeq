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

package org.qedeq.kernel.bo.service.logic;

import java.util.Map;

import org.qedeq.kernel.bo.PluginExecutor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @author  Michael Meyling
 */
public final class QedeqBoFormalLogicCheckerPlugin implements PluginBo {

    /** This class. */
    private static final Class CLASS = QedeqBoFormalLogicCheckerPlugin.class;

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginName() {
        return "Verifier";
    }

    public String getPluginDescription() {
        return "checks mathematical correctness";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Map parameters) {
        return new QedeqBoFormalLogicCheckerExecutor(this, qedeq, parameters);
    }

}
