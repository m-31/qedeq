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

package org.qedeq.kernel.bo.service.heuristic;

import java.util.Map;

import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.logic.model.SixDynamicModel;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBoImpl;


/**
 * Test if QEDEQ module formulas are valid within a model.
 *
 * @author  Michael Meyling
 */
public class HeuristicCheckerPlugin extends PluginBoImpl {

    /** This class. */
    public static final Class CLASS = HeuristicCheckerPlugin.class;

    /**
     * Constructor.
     */
    public HeuristicCheckerPlugin() {
        // nothing to do
    }

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginActionName() {
        return "Heuristic tester";
    }

    public String getPluginDescription() {
        return "checks mathematical correctness by interpreting within a model";
    }

    public PluginExecutor createExecutor(final KernelQedeqBo qedeq, final Map parameters) {
        return new HeuristicCheckerExecutor(this, qedeq, parameters);
    }

    public void setDefaultValuesForEmptyPluginParameters(final Map parameters) {
        setDefault(parameters, "model", SixDynamicModel.class.getName());
    }

}
