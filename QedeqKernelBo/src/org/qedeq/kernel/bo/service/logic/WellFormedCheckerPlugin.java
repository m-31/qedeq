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

import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;


/**
 * Checks if all formulas of a QEDEQ module are well formed. Also dependency checks are included.
 *
 * @author  Michael Meyling
 */
public final class WellFormedCheckerPlugin implements PluginBo {

    /** This class. */
    private static final Class CLASS = WellFormedCheckerPlugin.class;

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginActionName() {
        return "check well-formedness";
    }

    public String getPluginDescription() {
        return "checks well-formedness of formulas and correct dependency of declarations";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Map parameters) {
        return new WellFormedCheckerExecutor(this, qedeq, parameters);
    }

}
