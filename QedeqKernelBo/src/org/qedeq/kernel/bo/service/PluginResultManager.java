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

package org.qedeq.kernel.bo.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginResults;
import org.qedeq.kernel.se.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.se.common.Plugin;
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
    synchronized PluginBo[] getPlugins() {
        return (PluginBo[]) plugins.keySet().toArray(new PluginBo[] {});
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
    public synchronized void setResult(final PluginBo plugin, final SourceFileExceptionList errors,
            final SourceFileExceptionList warnings) {
        PluginResults results = (PluginResults) plugins.get(plugin);
        if (results == null) {
            results = new PluginResults();
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
    public synchronized void addResult(final Plugin plugin, final SourceFileExceptionList errors,
            final SourceFileExceptionList warnings) {
        PluginResults results = (PluginResults) plugins.get(plugin);
        if (results == null) {
            results = new PluginResults();
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
        final DefaultSourceFileExceptionList errors = new DefaultSourceFileExceptionList();
        Iterator iterator = plugins.keySet().iterator();
        while (iterator.hasNext()) {
            errors.add(((PluginResults) plugins.get(iterator.next())).getErrors());
        }
        return errors;
    }

    /**
     * Get all warnings that occurred. The resulting object is never <code>null</code>.
     *
     * @return  Warnings list.
     */
    public SourceFileExceptionList getAllWarnings() {
        final DefaultSourceFileExceptionList warnings = new DefaultSourceFileExceptionList();
        Iterator iterator = plugins.keySet().iterator();
        while (iterator.hasNext()) {
            warnings.add(((PluginResults) plugins.get(iterator.next())).getWarnings());
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
            final PluginBo key = (PluginBo) iterator.next();
            PluginResults result = (PluginResults) plugins.get(key);
            text.append(key.getPluginActionName());
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

