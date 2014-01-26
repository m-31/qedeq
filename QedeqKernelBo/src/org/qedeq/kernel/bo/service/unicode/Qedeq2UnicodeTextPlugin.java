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

package org.qedeq.kernel.bo.service.unicode;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.basis.ModuleServicePlugin;
import org.qedeq.kernel.bo.service.basis.ModuleServicePluginExecutor;


/**
 * Plugin to transfer a QEDEQ module into a UTF-8 text file.
 *
 * @author  Michael Meyling
 */
public final class Qedeq2UnicodeTextPlugin implements ModuleServicePlugin {

    /** This class. */
    public static final Class CLASS = Qedeq2UnicodeTextPlugin.class;

    /**
     * Constructor.
     */
    public Qedeq2UnicodeTextPlugin() {
        // nothing to do
    }

    public String getServiceId() {
        return CLASS.getName();
    }

    public String getServiceAction() {
        return "Show Module as UTF-8 text";
    }

    public String getServiceDescription() {
        return "shows QEDEQ module within window as UTF-8 text";
    }

    public ModuleServicePluginExecutor createExecutor(final KernelQedeqBo qedeq, final Parameters parameters) {
        return new Qedeq2UnicodeTextExecutor(this, qedeq, parameters);
    }

    public void setDefaultValuesForEmptyPluginParameters(final Parameters parameters) {
        parameters.setDefault("language", "en");
        parameters.setDefault("info", true);
        parameters.setDefault("brief", false);
        // automatically line break after this column. 0 means no automatic line breaking
        parameters.setDefault("maximumColumn", 80);
    }

}
