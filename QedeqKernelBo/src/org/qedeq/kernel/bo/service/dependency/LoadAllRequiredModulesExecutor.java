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

package org.qedeq.kernel.bo.service.dependency;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.common.ServiceProcess;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.common.Plugin;


/**
 * Load all required QEDEQ modules.
 *
 * @author  Michael Meyling
 */
public final class LoadAllRequiredModulesExecutor extends ControlVisitor
        implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = LoadAllRequiredModulesExecutor.class;

    /**
     * Constructor.
     *
     * @param   plugin      Plugin we work for.
     * @param   prop        Internal QedeqBo.
     * @param   parameter   Currently ignored.
     */
    public LoadAllRequiredModulesExecutor(final Plugin plugin, final KernelQedeqBo prop,
            final Parameters parameter) {
        super(plugin, prop);
    }

    public Object executePlugin(final ServiceProcess process, final Object data) {
        if (getQedeqBo().hasLoadedRequiredModules()) {
            return Boolean.TRUE;
        }
        getServices().executePlugin(LoadDirectlyRequiredModulesPlugin.class.getName(),
            getQedeqBo(), data, process);
        if (!getQedeqBo().hasLoadedImports()) {
            return Boolean.FALSE;
        }
        final ModuleReferenceList imports = getQedeqBo().getRequiredModules();
        boolean result = true;
        for (int i = 0; i < imports.size(); i++) {
            if (!imports.getQedeqBo(i).hasLoadedImports()) {
                getServices().executePlugin(LoadAllRequiredModulesPlugin.class.getName(),
                    (KernelQedeqBo) imports.getQedeqBo(i), data, process);
            }
            if (!imports.getQedeqBo(i).hasLoadedImports()) {
                result = false;
            }
        }
        return Boolean.valueOf(result);
    }

}
