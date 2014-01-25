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

package org.qedeq.kernel.bo.service.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * Holds all known QEDEQ modules.
 */
public class PluginResultManager {

    /** Maps plugins to results. */
    private final Map plugins = new HashMap();


    /**
     * Get all used plugins.
     *
     * @return  Used plugins.
     */
    synchronized ModuleServicePlugin[] getPlugins() {
        return (ModuleServicePlugin[]) plugins.keySet().toArray(new ModuleServicePlugin[] {});
    }

    /**
     * Clear all plugin results.
     */
    public void removeAllResults() {
        plugins.clear();
    }

    /**
     * Set a plugin execution results.
     *
     * @param   plugin      Set results for this plugin.
     * @param   errors      Plugin errors.
     * @param   warnings    Plugin warnings.
     */
    public synchronized void setResult(final ModuleServicePlugin plugin, final SourceFileExceptionList errors,
            final SourceFileExceptionList warnings) {
        ModuleServicePluginResults results = (ModuleServicePluginResults) plugins.get(plugin);
        if (results == null) {
            results = new ModuleServicePluginResults();
            plugins.put(plugin, results);
        }
        results.clear();
        results.addErrors(errors);
        results.addWarnings(warnings);
    }

    /**
     * Add results of a plugin.
     *
     * @param   plugin      Add results for this plugin.
     * @param   errors      Plugin errors.
     * @param   warnings    Plugin warnings.
     */
    public synchronized void addResult(final ModuleService plugin, final SourceFileExceptionList errors,
            final SourceFileExceptionList warnings) {
        ModuleServicePluginResults results = (ModuleServicePluginResults) plugins.get(plugin);
        if (results == null) {
            results = new ModuleServicePluginResults();
            plugins.put(plugin, results);
        }
        results.addErrors(errors);
        results.addWarnings(warnings);
    }

    /**
     * Get all errors that occurred. The resulting object is never <code>null</code>.
     *
     * @return  Error list.
     */
    public SourceFileExceptionList getAllErrors() {
        final SourceFileExceptionList errors = new SourceFileExceptionList();
        Iterator iterator = plugins.keySet().iterator();
        while (iterator.hasNext()) {
            errors.add(((ModuleServicePluginResults) plugins.get(iterator.next())).getErrors());
        }
        return errors;
    }

    /**
     * Get all warnings that occurred. The resulting object is never <code>null</code>.
     *
     * @return  Warnings list.
     */
    public SourceFileExceptionList getAllWarnings() {
        final SourceFileExceptionList warnings = new SourceFileExceptionList();
        Iterator iterator = plugins.keySet().iterator();
        while (iterator.hasNext()) {
            warnings.add(((ModuleServicePluginResults) plugins.get(iterator.next())).getWarnings());
        }
        return warnings;
    }

    /**
     * Get plugin states description.
     *
     * @return  Textual description of plugin states.
     */
    public synchronized String getPluginStateDescription() {
        final StringBuffer text = new StringBuffer();
        Iterator iterator = plugins.keySet().iterator();
        while (iterator.hasNext()) {
            if (text.length() > 0) {
                text.append(", ");
            }
            final ModuleServicePlugin key = (ModuleServicePlugin) iterator.next();
            ModuleServicePluginResults result = (ModuleServicePluginResults) plugins.get(key);
            text.append(key.getServiceAction());
            text.append(" ");
            if (result.hasErrors() && result.hasWarnings()) {
                text.append("has errors and warnings");
            } else if (result.hasErrors()) {
                text.append("has errors");
            } else if (result.hasWarnings()) {
                text.append("has warnings");
            } else {
                text.append("successful");
            }
        }
        return text.toString();
    }

}

