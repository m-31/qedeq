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
import org.qedeq.kernel.bo.logic.ProofCheckerFactoryImpl;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.basis.InternalModuleServicePlugin;
import org.qedeq.kernel.bo.service.basis.ModuleServicePluginExecutor;


/**
 * Checks if all propositions have a correct formal proof.
 *
 * @author  Michael Meyling
 */
public final class FormalProofCheckerPlugin implements InternalModuleServicePlugin {

    /** This class. */
    private static final Class CLASS = FormalProofCheckerPlugin.class;

    public String getServiceId() {
        return CLASS.getName();
    }

    public String getServiceAction() {
        return "Check Proofs";
    }

    public String getServiceDescription() {
        return "checks formal proofs";
    }

    public ModuleServicePluginExecutor createExecutor(final KernelQedeqBo qedeq, final Parameters parameters) {
        return new FormalProofCheckerExecutor(this, qedeq, parameters);
    }

    public void setDefaultValuesForEmptyPluginParameters(final Parameters parameters) {
        parameters.setDefault("checkerFactory", ProofCheckerFactoryImpl.class.getName());
    }

}
