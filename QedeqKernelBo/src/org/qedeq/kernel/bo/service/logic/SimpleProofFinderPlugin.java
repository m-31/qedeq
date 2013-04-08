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

package org.qedeq.kernel.bo.service.logic;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginExecutor;


/**
 * Finds primitive formal proofs.
 *
 * @author  Michael Meyling
 */
public final class SimpleProofFinderPlugin implements PluginBo {

    /** This class. */
    private static final Class CLASS = SimpleProofFinderPlugin.class;

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginActionName() {
        return "Find Proofs";
    }

    public String getPluginDescription() {
        return "finds simple formal proofs and add them to module  [EXPERIMENTAL]";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Parameters parameters) {
        return new SimpleProofFinderExecutor(this, qedeq, parameters);
    }

    public void setDefaultValuesForEmptyPluginParameters(final Parameters parameters) {
        parameters.setDefault("extraVars", 1);
        parameters.setDefault("maximumProofLines", Integer.MAX_VALUE - 2);
        parameters.setDefault("skipFormulas", "");
        parameters.setDefault("propositionVariableWeight", 3);
        parameters.setDefault("propositionVariableOrder", 1);
        parameters.setDefault("partFormulaOrder", 2);
        parameters.setDefault("partFormulaWeight", 1);
        parameters.setDefault("disjunctionOrder", 3);
        parameters.setDefault("disjunctionWeight", 3);
        parameters.setDefault("implicationOrder", 4);
        parameters.setDefault("implicationWeight", 1);
        parameters.setDefault("negationOrder", 5);
        parameters.setDefault("negationWeight", 1);
        parameters.setDefault("conjunctionOrder", 6);
        parameters.setDefault("conjunctionWeight", 1);
        parameters.setDefault("equivalenceOrder", 7);
        parameters.setDefault("equivalenceWeight", 1);
        parameters.setDefault("logFrequence", 1000);
        parameters.setDefault("noSave", false);
    }

}
