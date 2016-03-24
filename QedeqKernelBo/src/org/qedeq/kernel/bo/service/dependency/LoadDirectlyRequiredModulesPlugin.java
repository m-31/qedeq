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

package org.qedeq.kernel.bo.service.dependency;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.basis.InternalModuleServicePlugin;
import org.qedeq.kernel.bo.service.basis.ModuleServicePluginExecutor;


/**
 * Load all imported modules.
 *
 * @author  Michael Meyling
 */
public final class LoadDirectlyRequiredModulesPlugin implements InternalModuleServicePlugin {

    /** This class. */
    private static final Class CLASS = LoadDirectlyRequiredModulesPlugin.class;

    public String getServiceId() {
        return CLASS.getName();
    }

    public String getServiceAction() {
        return "loading directly required modules";
    }

    public String getServiceDescription() {
        return "load all imported modules";
    }

    public ModuleServicePluginExecutor createExecutor(final KernelQedeqBo qedeq, final Parameters parameters) {
        return new LoadDirectlyRequiredModulesExecutor(this, qedeq, parameters);
    }

    public void setDefaultValuesForEmptyPluginParameters(final Parameters parameters) {
        // nothing to do
    }

}
