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
import org.qedeq.kernel.bo.module.PluginBoImpl;


/**
 * Finds primitive formal proofs.
 *
 * @author  Michael Meyling
 */
public final class SimpleProofFinderPlugin extends PluginBoImpl {

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

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Map parameters) {
        return new SimpleProofFinderExecutor(this, qedeq, parameters);
    }

    public void setDefaultValuesForEmptyPluginParameters(final Map parameters) {
        setDefault(parameters, "extraVars", 1);
        setDefault(parameters, "maximumProofLines", Integer.MAX_VALUE - 2);
        setDefault(parameters, "skipFormulas", "");
        setDefault(parameters, "propositionVariableWeight", 3);
        setDefault(parameters, "propositionVariableOrder", 1);
        setDefault(parameters, "partFormulaOrder", 2);
        setDefault(parameters, "partFormulaWeight", 1);
        setDefault(parameters, "disjunctionOrder", 3);
        setDefault(parameters, "disjunctionWeight", 3);
        setDefault(parameters, "implicationOrder", 4);
        setDefault(parameters, "implicationWeight", 1);
        setDefault(parameters, "negationOrder", 5);
        setDefault(parameters, "negationWeight", 1);
        setDefault(parameters, "conjunctionOrder", 6);
        setDefault(parameters, "conjunctionWeight", 1);
        setDefault(parameters, "equivalenceOrder", 7);
        setDefault(parameters, "equivalenceWeight", 1);
        setDefault(parameters, "logFrequence", 1000);
        setDefault(parameters, "noSave", false);
    }

}
