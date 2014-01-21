/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.logic.FormulaCheckerFactoryImpl;
import org.qedeq.kernel.bo.module.InternalPluginBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginExecutor;


/**
 * Checks if all formulas of a QEDEQ module are well formed. Also dependency checks are included.
 *
 * @author  Michael Meyling
 */
public final class WellFormedCheckerPlugin implements InternalPluginBo {

    /** This class. */
    private static final Class CLASS = WellFormedCheckerPlugin.class;

    public String getServiceId() {
        return CLASS.getName();
    }

    public String getServiceAction() {
        return "check well-formedness";
    }

    public String getServiceDescription() {
        return "checks well-formedness of formulas and correct dependency of declarations";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Parameters parameters) {
        return new WellFormedCheckerExecutor(this, qedeq, parameters);
    }

    public void setDefaultValuesForEmptyPluginParameters(final Parameters parameters) {
        parameters.setDefault("checkerFactory", FormulaCheckerFactoryImpl.class.getName());
    }

}
